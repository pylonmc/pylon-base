package io.github.pylonmc.pylon.base.content.machines.fluid;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.content.machines.fluid.gui.IntRangeInventory;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.window.Window;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class FluidLimiter extends PylonBlock implements PylonFluidTank, PylonInteractBlock {

    public final ItemStack mainStack = ItemStackBuilder.of(Material.WHITE_CONCRETE)
        .addCustomModelDataString(getKey() + ":main")
        .build();
    public final ItemStack noFluidStack = ItemStackBuilder.of(Material.RED_CONCRETE)
        .addCustomModelDataString(getKey() + ":fluid:none")
        .build();

    public final IntRangeInventory regulator;

    private static final NamespacedKey AMOUNT_KEY = baseKey("amount");
    public static final NamespacedKey REAL_MAX_CAPACITY = baseKey("real_max_capacity");

    @SuppressWarnings("unused")
    public FluidLimiter(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        this.regulator = new IntRangeInventory(BaseItems.FLUID_LIMITER, this::getPipeMax);

        // a bit of a hack - treat capacity as effectively infinite and override
        // fluidAmountRequested to control how much fluid comes in
        setCapacity(1.0e9);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid filter can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        // fatty filter for now todo: add a proper unique display
        addEntity("main", new ItemDisplayBuilder()
            .itemStack(mainStack)
            .transformation(new TransformBuilder()
                .lookAlong(PylonUtils.rotateToPlayerFacing(player, BlockFace.EAST, false).getDirection().toVector3d())
                .scale(0.4, 0.25, 0.5)
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
        createFluidPoint(FluidPointType.INPUT, BlockFace.EAST, context, false, 0.25F);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.WEST, context, false, 0.25F);
        setDisableBlockTextureEntity(true);
    }

    @SuppressWarnings("unused")
    public FluidLimiter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        this.regulator = new IntRangeInventory(
            BaseItems.FLUID_LIMITER,
            this::getPipeMax,
            pdc.get(AMOUNT_KEY, PylonSerializers.INTEGER)
        );
        setDisableBlockTextureEntity(true);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(AMOUNT_KEY, PylonSerializers.INTEGER, regulator.getAmount());
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return true;
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()
            || event.getPlayer().isSneaking()
            || event.getHand() != EquipmentSlot.HAND
            || event.useInteractedBlock() == Event.Result.DENY) {
            return;
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        Window.single()
            .setGui(regulator.makeGui())
            .setTitle(new AdventureComponentWrapper(getNameTranslationKey()))
            .setViewer(event.getPlayer())
            .addCloseHandler(this::updateFluidPerSecond)
            .build()
            .open();
    }

    private void updateFluidPerSecond() {
        var output = getFluidPointDisplayOrThrow(FluidPointType.OUTPUT);
        var segment = output.getPoint().getSegment();
        FluidManager.setFluidPerSecond(segment, regulator.getAmount());
    }

    private int getPipeMax() {
        return Double.valueOf(getPipeMaxDouble()).intValue();
    }

    private double getPipeMaxDouble() {
        var output = getFluidPointDisplayOrThrow(FluidPointType.OUTPUT);

        PersistentDataContainer pdc = output.getEntity().getPersistentDataContainer();
        Double realCapacity = pdc.get(REAL_MAX_CAPACITY, PylonSerializers.DOUBLE);
        if (realCapacity == null) {
            double original = FluidManager.getFluidPerSecond(output.getPoint().getSegment());
            pdc.set(REAL_MAX_CAPACITY, PersistentDataType.DOUBLE, original);
            return original;
        } else {
            return realCapacity;
        }
    }
}
