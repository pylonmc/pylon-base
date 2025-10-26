package io.github.pylonmc.pylon.base.content.machines.fluid;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.content.machines.fluid.gui.FluidSelector;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import io.github.pylonmc.pylon.core.config.PylonConfig;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.VirtualFluidPoint;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class FluidFilter extends PylonBlock
        implements PylonFluidTank, PylonEntityHolderBlock, PylonGuiBlock {

    public static class Item extends PylonItem {

        public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(buffer))
            );
        }
    }

    public static final NamespacedKey FLUID_KEY = baseKey("fluid");

    public final ItemStack mainStack = ItemStackBuilder.of(Material.WHITE_CONCRETE)
            .addCustomModelDataString(getKey() + ":main")
            .build();
    public final ItemStack noFluidStack = ItemStackBuilder.of(Material.RED_CONCRETE)
            .addCustomModelDataString(getKey() + ":fluid:none")
            .build();

    protected @Nullable PylonFluid fluid;

    @SuppressWarnings("unused")
    public FluidFilter(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        fluid = null;

        // a bit of a hack - treat capacity as effectively infinite and override
        // fluidAmountRequested to control how much fluid comes in
        setCapacity(1.0e9);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid valve can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(PylonUtils.rotateToPlayerFacing(player, BlockFace.EAST, false).getDirection().toVector3d())
                        .scale(0.25, 0.25, 0.5)
                )
                .build(block.getLocation().toCenterLocation())
        );
        addEntity("fluid", new ItemDisplayBuilder()
                .itemStack(noFluidStack)
                .transformation(new TransformBuilder()
                        .lookAlong(PylonUtils.rotateToPlayerFacing(player, BlockFace.EAST, false).getDirection().toVector3d())
                        .scale(0.2, 0.3, 0.45)
                )
                .build(block.getLocation().toCenterLocation())
        );
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.EAST, 0.25F));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.WEST, 0.25F));
        setDisableBlockTextureEntity(true);
    }

    @SuppressWarnings("unused")
    public FluidFilter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        fluid = pdc.get(FLUID_KEY, PylonSerializers.PYLON_FLUID);
        setDisableBlockTextureEntity(true);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, FLUID_KEY, PylonSerializers.PYLON_FLUID, fluid);
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(PylonArgument.of(
                "fluid",
                fluid == null ? Component.translatable("pylon.pylonbase.fluid.none") : fluid.getName()
        )));
    }

    private @NotNull ItemDisplay getFluidDisplay() {
        return getHeldEntityOrThrow(ItemDisplay.class, "fluid");
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return true;
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid, double deltaSeconds) {
        if (fluid != this.fluid) {
            return 0.0;
        }

        // Make sure the filter always has enough fluid for one tick's worth of output
        // somewhat hacky
        VirtualFluidPoint output = getHeldPylonEntityOrThrow(FluidPointInteraction.class, "output").getPoint();
        VirtualFluidPoint input = getHeldPylonEntityOrThrow(FluidPointInteraction.class, "input").getPoint();
        double outputFluidPerSecond = FluidManager.getFluidPerSecond(output.getSegment());
        double inputFluidPerSecond = FluidManager.getFluidPerSecond(input.getSegment());
        return Math.max(0.0, Math.min(outputFluidPerSecond, inputFluidPerSecond)
                * PylonConfig.getFluidTickInterval()
                * deltaSeconds
                - getFluidAmount()
        );
    }

    public void setFluid(PylonFluid fluid) {
        this.fluid = fluid;
        getFluidDisplay().setItemStack(fluid == null ? noFluidStack : fluid.getItem());
    }

    @Override
    public @NotNull Gui createGui() {
        return (FluidSelector.make(() -> fluid, this::setFluid));
    }

    @Override
    public @NotNull Component getGuiTitle() {
        return Component.translatable("pylon.pylonbase.item.fluid_filter.gui");
    }
}
