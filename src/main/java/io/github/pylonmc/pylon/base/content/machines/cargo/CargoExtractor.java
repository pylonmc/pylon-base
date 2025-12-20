package io.github.pylonmc.pylon.base.content.machines.cargo;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.cargo.CargoDuct;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.event.PylonCargoConnectEvent;
import io.github.pylonmc.pylon.core.event.PylonCargoDisconnectEvent;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticGroup;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class CargoExtractor extends PylonBlock
        implements PylonMultiblock, PylonDirectionalBlock, PylonCargoBlock, PylonEntityHolderBlock {

    public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder outputStack = ItemStackBuilder.of(Material.RED_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":output");
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
    public CargoExtractor(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());

        addCargoLogisticGroup(getFacing().getOppositeFace(), "output");
        for (BlockFace face : PylonUtils.perpendicularImmediateFaces(getFacing())) {
            addCargoLogisticGroup(face, "output");
        }
        setCargoTransferRate(transferRate);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, 0.4)
                        .scale(0.65, 0.65, 0.2)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("output", new ItemDisplayBuilder()
                .itemStack(outputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, 0.3)
                        .scale(0.4, 0.4, 0.05)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("duct", new ItemDisplayBuilder()
                .itemStack(ductStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, 0.0625)
                        .scale(0.35, 0.35, 0.475)
                )
                .build(block.getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public CargoExtractor(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock()), new ChunkPosition(getTarget()));
    }

    @Override
    public boolean checkFormed() {
        return getTargetLogisticBlock() != null;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return new BlockPosition(otherBlock)
                .equals(new BlockPosition(getTarget()));
    }

    @Override
    public @NotNull Map<String, LogisticGroup> getLogisticGroups() {
        PylonLogisticBlock logisticBlock = getTargetLogisticBlock();
        return logisticBlock != null
                ? logisticBlock.getLogisticGroups()
                : Collections.emptyMap();
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
            addCargoLogisticGroup(face, "output");
        }
        for (BlockFace face : faces) {
            if (BlockStorage.get(getBlock().getRelative(face)) instanceof CargoDuct duct) {
                duct.updateConnectedFaces();
            }
        }
    }

    public @NotNull Block getTarget() {
        return getBlock().getRelative(getFacing());
    }

    public @Nullable PylonLogisticBlock getTargetLogisticBlock() {
        PylonLogisticBlock block = BlockStorage.getAs(PylonLogisticBlock.class, getTarget());
        return block instanceof PylonCargoBlock ? null : block;
    }
}
