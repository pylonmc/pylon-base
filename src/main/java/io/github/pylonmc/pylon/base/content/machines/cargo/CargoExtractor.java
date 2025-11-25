package io.github.pylonmc.pylon.base.content.machines.cargo;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.cargo.CargoDuct;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.event.PylonCargoDuctConnectEvent;
import io.github.pylonmc.pylon.core.event.PylonCargoDuctDisconnectEvent;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticGroup;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class CargoExtractor extends PylonBlock
        implements PylonMultiblock, PylonDirectionalBlock, PylonCargoBlock, PylonEntityHolderBlock {

    private static final NamespacedKey FACING_KEY = baseKey("facing");

    private final BlockFace facing;

    public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

    public final ItemStack mainStack = ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":main")
            .build();
    public final ItemStack outputStack = ItemStackBuilder.of(Material.RED_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":output")
            .build();
    public final ItemStack ductStack = ItemStackBuilder.of(Material.GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":duct")
            .build();

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

        if (!(context instanceof BlockCreateContext.PlayerPlace playerPlaceContext)) {
            throw new IllegalArgumentException("Cargo extractor can only be placed by player");
        }

        facing = playerPlaceContext.getPlayer().getFacing();

        addCargoLogisticGroup(facing.getOppositeFace(), "output");
        for (BlockFace face : PylonUtils.perpendicularImmediateFaces(facing)) {
            addCargoLogisticGroup(face, "output");
        }
        setCargoTransferRate(transferRate);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .translate(0, 0, 0.4)
                        .scale(0.65, 0.65, 0.2)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("input", new ItemDisplayBuilder()
                .itemStack(outputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .translate(0, 0, 0.3)
                        .scale(0.4, 0.4, 0.05)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("duct", new ItemDisplayBuilder()
                .itemStack(ductStack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .translate(0, 0, 0.0625)
                        .scale(0.35, 0.35, 0.475)
                )
                .build(block.getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public CargoExtractor(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        facing = pdc.get(FACING_KEY, PylonSerializers.BLOCK_FACE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(FACING_KEY, PylonSerializers.BLOCK_FACE, facing);
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        Set<ChunkPosition> chunks = new HashSet<>();
        chunks.add(new ChunkPosition(getBlock()));
        chunks.add(new ChunkPosition(getTarget()));
        return chunks;
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
    public @Nullable BlockFace getFacing() {
        return facing;
    }

    @Override
    public void setupLogisticGroups() {}

    @Override
    public @NotNull Map<String, LogisticGroup> getLogisticGroups() {
        PylonLogisticBlock logisticBlock = getTargetLogisticBlock();
        return logisticBlock != null
                ? logisticBlock.getLogisticGroups()
                : Collections.emptyMap();
    }

    @Override
    public void onDuctConnected(@NotNull PylonCargoDuctConnectEvent event) {
        // Remove all faces that aren't to the connected block - this will make sure only
        // one duct is connected at a time
        BlockPosition ductPosition = new BlockPosition(event.getDuct().getBlock());
        BlockPosition thisPosition = new BlockPosition(getBlock());
        BlockFace connectedFace = PylonUtils.vectorToBlockFace(ductPosition.minus(thisPosition).getVector3i());
        for (BlockFace face : getCargoLogisticGroups().keySet()) {
            if (face != connectedFace) {
                removeCargoLogisticGroup(face);
            }
        }
    }

    @Override
    public void onDuctDisconnected(@NotNull PylonCargoDuctDisconnectEvent event) {
        // Allow connecting to all faces now that there are zero connections
        List<BlockFace> faces = PylonUtils.perpendicularImmediateFaces(facing);
        faces.add(facing.getOppositeFace());
        for (BlockFace face : faces) {
            addCargoLogisticGroup(face, "output");
        }
        for (BlockFace face : faces) {
            if (BlockStorage.get(getBlock().getRelative(face)) instanceof CargoDuct duct ) {
                duct.updateConnectedFaces();
            }
        }
    }

    public @NotNull Block getTarget() {
        return getBlock().getRelative(facing);
    }

    public @Nullable PylonLogisticBlock getTargetLogisticBlock() {
        PylonLogisticBlock block = BlockStorage.getAs(PylonLogisticBlock.class, getTarget());
        return block instanceof PylonCargoBlock ? null : block;
    }
}
