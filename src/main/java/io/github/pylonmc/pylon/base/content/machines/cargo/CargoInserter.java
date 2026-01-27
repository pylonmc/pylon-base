package io.github.pylonmc.pylon.base.content.machines.cargo;

import io.github.pylonmc.rebar.block.BlockStorage;
import io.github.pylonmc.rebar.block.base.*;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.content.cargo.CargoDuct;
import io.github.pylonmc.rebar.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.rebar.entity.display.transform.TransformBuilder;
import io.github.pylonmc.rebar.event.PylonCargoConnectEvent;
import io.github.pylonmc.rebar.event.PylonCargoDisconnectEvent;
import io.github.pylonmc.rebar.i18n.PylonArgument;
import io.github.pylonmc.rebar.item.PylonItem;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import io.github.pylonmc.rebar.logistics.LogisticGroup;
import io.github.pylonmc.rebar.logistics.LogisticGroupType;
import io.github.pylonmc.rebar.util.PylonUtils;
import io.github.pylonmc.rebar.util.gui.GuiItems;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.*;


public class CargoInserter extends CargoInteractor implements
        PylonCargoBlock,
        PylonGuiBlock {

    public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder inputStack = ItemStackBuilder.of(Material.LIME_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":input");
    public final ItemStackBuilder ductStack = ItemStackBuilder.of(Material.GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":duct");

    public static class Item extends PylonItem {

        public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of(
                            "transfer-rate",
                            UnitFormat.ITEMS_PER_SECOND.format(PylonCargoBlock.cargoItemsTransferredPerSecond(transferRate))
                    )
            );
        }
    }

    @SuppressWarnings("unused")
    public CargoInserter(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());

        addCargoLogisticGroup(getFacing(), "input");
        for (BlockFace face : PylonUtils.perpendicularImmediateFaces(getFacing())) {
            addCargoLogisticGroup(face, "input");
        }
        setCargoTransferRate(transferRate);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, -0.4)
                        .scale(0.65, 0.65, 0.2)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("input", new ItemDisplayBuilder()
                .itemStack(inputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, -0.3)
                        .scale(0.4, 0.4, 0.05)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("duct", new ItemDisplayBuilder()
                .itemStack(ductStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, -0.0625)
                        .scale(0.35, 0.35, 0.475)
                )
                .build(block.getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public CargoInserter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onDuctConnected(@NotNull PylonCargoConnectEvent event) {
        // Remove all faces that aren't to the connected block - this will make sure only
        // one duct is connected at a time
        for (BlockFace face : getCargoLogisticGroups().keySet()) {
            if (!getBlock().getRelative(face).equals(event.getBlock1().getBlock()) && !getBlock().getRelative(face).equals(event.getBlock2().getBlock())) {
                removeCargoLogisticGroup(face);
            }
        }
    }

    @Override
    public void onDuctDisconnected(@NotNull PylonCargoDisconnectEvent event) {
        // Allow connecting to all faces now that there are zero connections
        List<BlockFace> faces = PylonUtils.perpendicularImmediateFaces(getFacing());
        faces.add(getFacing());
        for (BlockFace face : faces) {
            if (targetLogisticGroup != null) {
                addCargoLogisticGroup(face, targetLogisticGroup);
            } else {
                addCargoLogisticGroup(face, "placeholder_unused_kind_of_a_hack");
            }
        }
        for (BlockFace face : faces) {
            if (BlockStorage.get(getBlock().getRelative(face)) instanceof CargoDuct duct) {
                duct.updateConnectedFaces();
            }
        }
    }

    @Override
    public @NotNull Map<String, LogisticGroup> getLogisticGroups() {
        return targetGroups;
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure("# # # # i # # # #")
                .addIngredient('#', GuiItems.background())
                .addIngredient('i', new InventoryCycleItem())
                .build();
    }

    @Override
    public void setTargetLogisticGroup(String targetLogisticGroup) {
        this.targetLogisticGroup = targetLogisticGroup;
        for (BlockFace face : getCargoLogisticGroups().keySet()) {
            removeCargoLogisticGroup(face);
            // Slight hack: Set the logistic group to 'none' (which should not exist)
            addCargoLogisticGroup(face, targetLogisticGroup == null ? "none" : targetLogisticGroup);
        }
    }

    @Override
    public boolean isValidGroup(@NotNull LogisticGroup group) {
        return group.getSlotType() == LogisticGroupType.BOTH || group.getSlotType() == LogisticGroupType.INPUT;
    }
}
