package io.github.pylonmc.pylon.base.items.fluid.items;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.fluid.gui.FluidSelector;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.config.PylonConfig;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.window.Window;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidFilter extends PylonBlock implements PylonEntityHolderBlock, PylonFluidBlock, PylonInteractableBlock {

    public static final NamespacedKey KEY = pylonKey("fluid_filter");

    public static final NamespacedKey FLUID_KEY = pylonKey("fluid");
    public static final NamespacedKey BUFFER_KEY = pylonKey("buffer");

    public static final Material MAIN_MATERIAL = Material.WHITE_TERRACOTTA;
    public static final Material NO_FLUID_MATERIAL = Material.RED_TERRACOTTA;

    protected final Map<String, UUID> entities;
    protected @Nullable PylonFluid fluid;
    protected double buffer;

    @SuppressWarnings("unused")
    public FluidFilter(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid valve can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        FluidConnectionPoint input = new FluidConnectionPoint(getBlock(), "input", FluidConnectionPoint.Type.INPUT);
        FluidConnectionPoint output = new FluidConnectionPoint(getBlock(), "output", FluidConnectionPoint.Type.OUTPUT);

        entities = new HashMap<>(Map.of(
                "main", MainDisplay.make(block, player).getUuid(),
                "fluid", FluidDisplay.make(block, player).getUuid(),
                "input", FluidConnectionInteraction.make(player, input, BlockFace.EAST, 0.25F).getUuid(),
                "output", FluidConnectionInteraction.make(player, output, BlockFace.WEST, 0.25F).getUuid()
        ));
        fluid = null;
        buffer = 0.0;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidFilter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        entities = loadHeldEntities(pdc);
        fluid = pdc.get(FLUID_KEY, PylonSerializers.PYLON_FLUID);
        buffer = pdc.get(BUFFER_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        saveHeldEntities(pdc);
        PdcUtils.setNullable(pdc, FLUID_KEY, PylonSerializers.PYLON_FLUID, fluid);
        pdc.set(BUFFER_KEY, PylonSerializers.DOUBLE, buffer);
    }

    @Override
    public @NotNull Map<String, UUID> getHeldEntities() {
        return entities;
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Window.single()
                .setGui(FluidSelector.make(() -> fluid, this::setFluid))
                .setViewer(event.getPlayer())
                .setTitle("Select fluid")
                .build()
                .open();
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(getName(), Map.of(
                "fluid", Component.translatable("pylon.pylonbase.fluid." + (fluid == null ? "none" : fluid.getKey().getKey()))
        ));
    }

    private @NotNull FluidDisplay getFluidDisplay() {
        return Objects.requireNonNull(getHeldEntity(FluidDisplay.class, "fluid"));
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        double outputFluidPerSecond = FluidManager.getFluidPerSecond(getOutputPoint().getSegment());
        double inputFluidPerSecond = FluidManager.getFluidPerSecond(getInputPoint().getSegment());
        return fluid == null
            ? Map.of()
            : Map.of(fluid, Math.max(0.0, Math.min(outputFluidPerSecond, inputFluidPerSecond) * PylonConfig.getFluidIntervalTicks() * deltaSeconds - buffer));
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        buffer += amount;
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return fluid == null
                ? Map.of()
                : Map.of(fluid, buffer);
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        buffer -= amount;
    }

    private @NotNull FluidConnectionPoint getOutputPoint() {
        //noinspection DataFlowIssue
        return getHeldEntity(FluidConnectionInteraction.class, "output").getPoint();
    }

    private @NotNull FluidConnectionPoint getInputPoint() {
        //noinspection DataFlowIssue
        return getHeldEntity(FluidConnectionInteraction.class, "input").getPoint();
    }

    public void setFluid(PylonFluid fluid) {
        this.fluid = fluid;
        getFluidDisplay().setFluid(fluid);
    }

    public static class MainDisplay extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("fluid_filter_main_display");

        @SuppressWarnings("unused")
        public MainDisplay(@NotNull ItemDisplay entity) {
            super(entity);
        }

        public MainDisplay(@NotNull Block block, @NotNull Player player) {
            super(KEY, new ItemDisplayBuilder()
                    .material(MAIN_MATERIAL)
                    .transformation(new TransformBuilder()
                            .lookAlong(PylonUtils.rotateToPlayerFacing(player, BlockFace.EAST).getDirection().toVector3d())
                            .scale(0.25, 0.25, 0.5)
                    )
                    .build(block.getLocation().toCenterLocation())
            );
        }

        public static @NotNull FluidFilter.MainDisplay make(@NotNull Block block, @NotNull Player player) {
            MainDisplay display = new MainDisplay(block, player);
            EntityStorage.add(display);
            return display;
        }
    }

    public static class FluidDisplay extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("fluid_filter_fluid_display");

        @SuppressWarnings("unused")
        public FluidDisplay(@NotNull ItemDisplay entity) {
            super(entity);
        }

        public FluidDisplay(@NotNull Block block, @NotNull Player player) {
            super(KEY, new ItemDisplayBuilder()
                    .material(NO_FLUID_MATERIAL)
                    .transformation(new TransformBuilder()
                            .lookAlong(PylonUtils.rotateToPlayerFacing(player, BlockFace.EAST).getDirection().toVector3d())
                            .scale(0.2, 0.3, 0.45)
                    )
                    .build(block.getLocation().toCenterLocation())
            );
        }

        public void setFluid(@Nullable PylonFluid fluid) {
            getEntity().setItemStack(new ItemStack(fluid == null ? NO_FLUID_MATERIAL : fluid.getMaterial()));
        }

        public static @NotNull FluidDisplay make(@NotNull Block block, @NotNull Player player) {
            FluidDisplay display = new FluidDisplay(block, player);
            EntityStorage.add(display);
            return display;
        }
    }
}
