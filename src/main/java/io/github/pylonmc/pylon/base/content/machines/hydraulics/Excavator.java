package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public abstract class Excavator extends PylonBlock implements PylonMultiblock {

    public static final NamespacedKey WORKING_KEY = baseKey("working");
    public static final NamespacedKey INDEX_KEY = baseKey("index");

    public final int radius = getSettings().getOrThrow("radius", ConfigAdapter.INT);
    public final int depth = getSettings().getOrThrow("depth", ConfigAdapter.INT);

    protected final List<BlockPosition> blockPositions = new ArrayList<>();
    protected final Set<ChunkPosition> chunkPositions = new HashSet<>();
    protected boolean working;
    protected int index;

    @SuppressWarnings("unused")
    public Excavator(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        working = true;
        index = 0;
    }

    @SuppressWarnings({"DataFlowIssue", "unused"})
    public Excavator(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        working = pdc.get(WORKING_KEY, PylonSerializers.BOOLEAN);
        index = pdc.get(INDEX_KEY, PylonSerializers.INTEGER);
    }

    @Override
    public void postInitialise() {
        for (int y = 0; y >= -depth; y--) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPosition blockPosition = new BlockPosition(getBlock().getRelative(x, y, z));
                    blockPositions.add(blockPosition);
                    chunkPositions.add(blockPosition.getChunk());
                }
            }
        }
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(WORKING_KEY, PylonSerializers.BOOLEAN, working);
        pdc.set(INDEX_KEY, PylonSerializers.INTEGER, index);
    }

    protected boolean tickExcavator() {
        if (!working) {
            return false;
        }

        if (index >= blockPositions.size()) {
            working = false;
            index = 0;
            return false;
        }

        while (index < blockPositions.size() - 1) {
            index++;
            BlockPosition position = blockPositions.get(index);
            if (position.getChunk().isLoaded() && breakBlock(position.getBlock())) {
                return true;
            }
        }

        // Finished excavating; move back to first position and do one more pass in case
        // something has changed in the meantime
        working = false;
        index = 0;
        while (index < blockPositions.size() - 1) {
            index++;
            BlockPosition position = blockPositions.get(index);
            if (position.getChunk().isLoaded() && breakBlock(position.getBlock())) {
                working = true;
                break;
            }
        }

        return false;
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
                && relative.getBlockY() >= 0 && relative.getBlockY() <= depth;
    }

    @Override
    public void onMultiblockRefreshed() {
        working = true;
    }

    abstract protected boolean breakBlock(Block block);
}
