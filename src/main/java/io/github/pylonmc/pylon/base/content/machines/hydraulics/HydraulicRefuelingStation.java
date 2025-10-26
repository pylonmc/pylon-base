package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;


public class HydraulicRefuelingStation extends PylonBlock
        implements PylonFluidBlock, PylonEntityHolderBlock, PylonInteractBlock, PylonBreakHandler {

    @SuppressWarnings("unused")
    public HydraulicRefuelingStation(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));
        addEntity("casing", new ItemDisplayBuilder()
                .itemStack(ItemStackBuilder.of(Material.ORANGE_STAINED_GLASS)
                        .addCustomModelDataString(getKey() + ":casing")
                )
                .transformation(new TransformBuilder()
                        .translate(0, 0.1, 0)
                        .scale(0.7)
                )
                .build(getBlock().getLocation().toCenterLocation())
        );
        addEntity("item", new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .translate(0, 0.25, 0)
                        .scale(0.4)
                )
                .build(getBlock().getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public HydraulicRefuelingStation(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || !event.getAction().isRightClick()) {
            return;
        }

        ItemDisplay itemDisplay = getHeldEntityOrThrow(ItemDisplay.class, "item");
        ItemStack toInsert = event.getPlayer().getInventory().getItem(EquipmentSlot.HAND);

        if (!itemDisplay.getItemStack().isEmpty()) {
            getBlock().getWorld().dropItemNaturally(
                    getBlock().getLocation().toCenterLocation().add(0, 0.25, 0),
                    itemDisplay.getItemStack()
            );
            itemDisplay.setItemStack(new ItemStack(Material.AIR));
        }

        if (PylonItem.fromStack(toInsert) instanceof HydraulicRefuelable) {
            itemDisplay.setItemStack(toInsert.asQuantity(1));
            toInsert.subtract();
        }
    }

    public @Nullable HydraulicRefuelable getHeldRefuelableItem() {
        ItemStack stack = getHeldEntityOrThrow(ItemDisplay.class, "item").getItemStack();
        if (PylonItem.fromStack(stack) instanceof HydraulicRefuelable refuelable) {
            return refuelable;
        }
        return null;
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        HydraulicRefuelable refuelable = getHeldRefuelableItem();
        if (refuelable == null) {
            return new WailaDisplay(
                    getDefaultWailaTranslationKey().arguments(PylonArgument.of("extra", "")
                    ));
        }
        Component hydraulicFluidBar = BaseUtils.createFluidAmountBar(
                refuelable.getHydraulicFluid(),
                refuelable.getHydraulicFluidCapacity(),
                20,
                TextColor.fromHexString("#212d99")
        );
        Component dirtyHydraulicFluidBar = BaseUtils.createFluidAmountBar(
                refuelable.getDirtyHydraulicFluid(),
                refuelable.getDirtyHydraulicFluidCapacity(),
                20,
                TextColor.fromHexString("#48459b")
        );
        return new WailaDisplay(
                getDefaultWailaTranslationKey().arguments(
                        PylonArgument.of(
                                "extra",
                                Component.translatable("pylon.pylonbase.message.hydraulic_refueling_station.extra").arguments(
                                        PylonArgument.of("hydraulic-fluid-bar", hydraulicFluidBar),
                                        PylonArgument.of("dirty-hydraulic-fluid-bar", dirtyHydraulicFluidBar)
                                )
                        )
                )
        );
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids(double deltaSeconds) {
        HydraulicRefuelable refuelable = getHeldRefuelableItem();
        if (refuelable == null) {
            return Map.of();
        }
        return Map.of(BaseFluids.DIRTY_HYDRAULIC_FLUID, refuelable.getDirtyHydraulicFluid());
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid, double deltaSeconds) {
        if (!fluid.equals(BaseFluids.HYDRAULIC_FLUID)) {
            return 0.0;
        }
        HydraulicRefuelable refuelable = getHeldRefuelableItem();
        if (refuelable == null) {
            return 0.0;
        }
        return refuelable.getHydraulicFluidSpace();
    }

    @Override
    public void onFluidAdded(@NotNull PylonFluid fluid, double amount) {
        HydraulicRefuelable refuelable = getHeldRefuelableItem();
        refuelable.setHydraulicFluid(refuelable.getHydraulicFluid() + amount);

        // Itemdisplay's item has to be set again after it's been edited for some unknown reason
        ItemStack stack = ((PylonItem) refuelable).getStack();
        getHeldEntityOrThrow(ItemDisplay.class, "item").setItemStack(stack);
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {
        HydraulicRefuelable refuelable = getHeldRefuelableItem();
        refuelable.setDirtyHydraulicFluid(refuelable.getDirtyHydraulicFluid() - amount);

        // Itemdisplay's item has to be set again after it's been edited for some unknown reason
        ItemStack stack = ((PylonItem) refuelable).getStack();
        getHeldEntityOrThrow(ItemDisplay.class, "item").setItemStack(stack);
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        ItemStack stack = getHeldEntityOrThrow(ItemDisplay.class, "item").getItemStack();
        if (!stack.isEmpty()) {
            drops.add(stack);
        }
    }
}
