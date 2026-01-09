package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.event.PrePylonBlockPlaceEvent;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public abstract class Miner extends PylonBlock implements PylonMultiblock, PylonProcessor {

    public static final NamespacedKey INDEX_KEY = baseKey("index");
    public static final NamespacedKey BLOCK_POSITIONS_KEY = baseKey("block_positions");
    public static final NamespacedKey CHUNK_POSITIONS_KEY = baseKey("chunk_positions");

    public final int radius = getSettings().getOrThrow("radius", ConfigAdapter.INT);

    protected final List<BlockPosition> blockPositions;
    protected final Set<ChunkPosition> chunkPositions;
    protected int index;

    @SuppressWarnings("unused")
    public Miner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        index = 0;
        blockPositions = new ArrayList<>();
        chunkPositions = new HashSet<>();
        for (int y = radius; y >= -radius; y--) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPosition blockPosition = new BlockPosition(getBlock().getRelative(x, y, z));
                    blockPositions.add(blockPosition);
                    chunkPositions.add(blockPosition.getChunk());
                }
            }
        }
    }

    @SuppressWarnings({"DataFlowIssue", "unused"})
    public Miner(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        index = pdc.get(INDEX_KEY, PylonSerializers.INTEGER);
        blockPositions = pdc.get(BLOCK_POSITIONS_KEY, PylonSerializers.LIST.listTypeFrom(PylonSerializers.BLOCK_POSITION));
        chunkPositions = pdc.get(CHUNK_POSITIONS_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.CHUNK_POSITION));
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(INDEX_KEY, PylonSerializers.INTEGER, index);
        pdc.set(BLOCK_POSITIONS_KEY, PylonSerializers.LIST.listTypeFrom(PylonSerializers.BLOCK_POSITION), blockPositions);
        pdc.set(CHUNK_POSITIONS_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.CHUNK_POSITION), chunkPositions);
    }

    private boolean checkBlocks() {
        while (index < blockPositions.size() - 1) {
            BlockPosition position = blockPositions.get(index);
            if (!position.getChunk().isLoaded()) {
                index++;
                continue;
            }

            Integer breakTicks = getBreakTicks(position.getBlock());
            if (breakTicks == null) {
                index++;
                continue;
            }

            startProcess(breakTicks);
            return false;
        }
        index = 0;
        return true;
    }

    protected void updateMiner() {
        if (checkBlocks()) {
            // Finished mining; do one more pass in case something has changed in the meantime
            checkBlocks();
        }
    }

    @Override
    public @NotNull Set<ChunkPosition> getChunksOccupied() {
        return chunkPositions;
    }

    @Override
    public boolean checkFormed() {
        return true;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        Vector relative = getBlock().getLocation().subtract(otherBlock.getLocation()).toVector();
        return Math.abs(relative.getBlockX()) <= radius
                && Math.abs(relative.getBlockZ()) <= radius
                && Math.abs(relative.getBlockY()) <= radius;
    }

    @Override
    public void onMultiblockRefreshed() {
        updateMiner();
    }

    abstract protected Integer getBreakTicks(@NotNull Block block);
}
