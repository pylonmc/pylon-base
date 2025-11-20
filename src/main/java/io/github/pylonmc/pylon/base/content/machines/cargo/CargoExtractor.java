package io.github.pylonmc.pylon.base.content.machines.cargo;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonLogisticBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.logistics.LogisticSlot;
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import kotlin.Pair;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;


public class CargoExtractor extends PylonBlock implements PylonMultiblock, PylonDirectionalBlock, PylonLogisticBlock {

    private BlockFace facing;

    public CargoExtractor(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        if (!(context instanceof BlockCreateContext.PlayerPlace playerPlaceContext)) {
            throw new IllegalArgumentException("Cargo interface can only be placed by player");
        }

        facing = playerPlaceContext.getPlayer().getFacing();
    }

    public CargoExtractor(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock()), new ChunkPosition(getTarget()));
    }

    @Override
    public boolean checkFormed() {
        return BlockStorage.get(getTarget()) instanceof PylonLogisticBlock;
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
    public void setupLogisticSlotGroups() {}

    @Override
    public @NotNull Map<String, Pair<LogisticSlotType, LogisticSlot[]>> getLogisticSlotGroups() {
        PylonLogisticBlock logisticBlock = getTargetLogisticBlock();
        return logisticBlock != null
                ? logisticBlock.getLogisticSlotGroups()
                : Collections.emptyMap();
    }

    public @NotNull Block getTarget() {
        return getBlock().getRelative(facing);
    }

    public @Nullable PylonLogisticBlock getTargetLogisticBlock() {
        return BlockStorage.getAs(PylonLogisticBlock.class, getTarget().getRelative(facing));
    }
}
