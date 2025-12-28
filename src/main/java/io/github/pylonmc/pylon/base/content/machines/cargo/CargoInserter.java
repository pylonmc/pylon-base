package io.github.pylonmc.pylon.base.content.machines.cargo;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.cargo.CargoDuct;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.event.PylonCargoConnectEvent;
import io.github.pylonmc.pylon.core.event.PylonCargoDisconnectEvent;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticGroup;
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.*;


public class CargoInserter extends PylonBlock implements
        PylonDirectionalBlock,
        PylonCargoBlock,
        PylonEntityHolderBlock,
        PylonGuiBlock,
        PylonMultiblock {

    public static final NamespacedKey TARGET_LOGISTIC_GROUP_KEY = BaseUtils.baseKey("target_logistic_group");

    public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder inputStack = ItemStackBuilder.of(Material.LIME_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":input");
    public final ItemStackBuilder ductStack = ItemStackBuilder.of(Material.GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":duct");

    public @Nullable String targetLogisticGroup = null;


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

    public class InventoryCycleItem extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            PylonLogisticBlock logisticBlock = getTargetLogisticBlock();
            if (targetLogisticGroup == null || logisticBlock == null) {
                return ItemStackBuilder.of(Material.BARRIER)
                        .name("pylon.pylonbase.gui.no-target-logistic-group");
            }

            List<String> availableGroups = getAvailableLogisticGroups();
            availableGroups.sort(String::compareTo);

            int index = availableGroups.indexOf(targetLogisticGroup);
            Preconditions.checkState(index != -1);
            Material material = CargoExtractor.groupMaterials.get(index % CargoExtractor.groupMaterials.size());

            PylonBlock pylonBlock = BlockStorage.get(logisticBlock.getBlock());
            Preconditions.checkState(pylonBlock != null);

            ItemStackBuilder builder = ItemStackBuilder.gui(material, "logistic-group:" + index)
                    .name(Component.translatable("pylon.pylonbase.gui.logistic-group-cycle-item.name")
                            .arguments(PylonArgument.of(
                                    "inventory",
                                    Component.translatable("pylon." + pylonBlock.getKey().getNamespace() + ".inventory." + targetLogisticGroup)
                            ))
                    );
            if (availableGroups.size() > 1) {
                builder.lore(Component.translatable("pylon.pylonbase.gui.logistic-group-cycle-item.lore"));
            }
            return builder;
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            PylonLogisticBlock logisticBlock = getTargetLogisticBlock();
            if (targetLogisticGroup == null || logisticBlock == null) {
                return;
            }

            List<String> availableGroups = getAvailableLogisticGroups();
            availableGroups.sort(String::compareTo);

            int index = availableGroups.indexOf(targetLogisticGroup);
            targetLogisticGroup = availableGroups.get((index + 1) % availableGroups.size());

            for (BlockFace face : getCargoLogisticGroups().keySet()) {
                removeCargoLogisticGroup(face);
                if (targetLogisticGroup != null) {
                    addCargoLogisticGroup(face, targetLogisticGroup);
                }
            }

            notifyWindows();
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

        refreshTargetLogisticGroup();
    }

    @SuppressWarnings("unused")
    public CargoInserter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        targetLogisticGroup = pdc.get(TARGET_LOGISTIC_GROUP_KEY, PylonSerializers.STRING);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, TARGET_LOGISTIC_GROUP_KEY, PylonSerializers.STRING, targetLogisticGroup);
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
        faces.add(getFacing().getOppositeFace());
        for (BlockFace face : faces) {
            addCargoLogisticGroup(face, "input");
        }
        for (BlockFace face : faces) {
            if (BlockStorage.get(getBlock().getRelative(face)) instanceof CargoDuct duct) {
                duct.updateConnectedFaces();
            }
        }
    }

    @Override
    public @NotNull Map<String, LogisticGroup> getLogisticGroups() {
        PylonLogisticBlock logisticBlock = getTargetLogisticBlock();
        return logisticBlock != null
                ? logisticBlock.getLogisticGroups()
                : Collections.emptyMap();
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock().getRelative(getFacing()).getChunk()));
    }

    @Override
    public boolean checkFormed() {
        return true;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return getBlock().getRelative(getFacing()).equals(otherBlock);
    }

    @Override
    public void onMultiblockFormed() {
        refreshTargetLogisticGroup();
    }

    @Override
    public void onMultiblockUnformed(boolean partUnloaded) {
        targetLogisticGroup = null;
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure("# # # # i # # # #")
                .addIngredient('#', GuiItems.background())
                .addIngredient('i', new InventoryCycleItem())
                .build();
    }

    public void refreshTargetLogisticGroup() {
        PylonLogisticBlock target = getTargetLogisticBlock();
        if (target == null) {
            return;
        }

        if (targetLogisticGroup == null || target.getLogisticGroup(targetLogisticGroup) == null) {
            List<String> availableLogisticGroups = getAvailableLogisticGroups();
            if (availableLogisticGroups.isEmpty()) {
                return;
            }
            targetLogisticGroup = availableLogisticGroups.getFirst();
        }

        for (BlockFace face : getCargoLogisticGroups().keySet()) {
            removeCargoLogisticGroup(face);
            if (targetLogisticGroup != null) {
                addCargoLogisticGroup(face, targetLogisticGroup);
            }
        }
    }

    public @Nullable PylonLogisticBlock getTargetLogisticBlock() {
        Block block = getBlock().getRelative(getFacing().getOppositeFace());
        PylonLogisticBlock pylonBlock = BlockStorage.getAs(PylonLogisticBlock.class, block);
        return pylonBlock instanceof PylonCargoBlock ? null : pylonBlock;
    }

    private @NotNull List<String> getAvailableLogisticGroups() {
        PylonLogisticBlock target = getTargetLogisticBlock();
        if (target == null) {
            return List.of();
        }

        List<String> availableLogisticGroups = new ArrayList<>();
        for (Map.Entry<String, LogisticGroup> entry : target.getLogisticGroups().entrySet()) {
            if (entry.getValue().getSlotType() == LogisticSlotType.BOTH
                    || entry.getValue().getSlotType() == LogisticSlotType.INPUT
            ) {
                availableLogisticGroups.add(entry.getKey());
            }
        }
        return availableLogisticGroups;
    }
}
