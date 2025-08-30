package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSponge;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * PowerfulSponge is an abstract base class for powerful sponge blocks that can absorb liquids in a large area.
 * <p>
 * This class provides the core functionality for different types of powerful sponges including:
 * <ul>
 *   <li>{@link PowerfulWaterSponge} - able to absorb water</li>
 *   <li>{@link PowerfulLavaSponge} - able to absorb lava</li>
 *   <li>{@link HotLavaSponge} - able to absorb lava, but with special behavior (90% chance turn into obsidian,
 *   10% chance turn back into {@link PowerfulLavaSponge})</li>
 * </ul>
 * </p>
 * <p>
 * Powerful Sponges evolutions:
 * <pre>
 *                          Fireproof rune powered
 *   [PowerfulWaterSponge] -----------------------→  [PowerfulLavaSponge]
 *     Dry out  ↑ |                            10% chance     ↑ |
 *     in Blast | | Inside water              Inside water    | | Inside lava
 *     Furnace  | ↓                                           | ↓
 *   [WetWaterSponge]                                [HotLavaSponge]
 *     Inside     |                            90% chance     |
 *     Nether     |                           Inside water    |
 *                ↓                                           ↓
 *   [PowerfulWaterSponge]                           [Obsidian]
 * </pre>
 * </p>
 *
 * @author balugaq
 * @see PylonSponge
 * @see PowerfulWaterSponge
 * @see PowerfulLavaSponge
 * @see HotLavaSponge
 */
public abstract class PowerfulSponge extends PylonBlock implements PylonSponge, PylonTickingBlock {
    public static final BlockFace[] NEARBY_FACES = {
            BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN
    };

    public PowerfulSponge(@NotNull Block block) {
        super(block);
    }

    public PowerfulSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public PowerfulSponge(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    /**
     * Handles the sponge absorption event.
     * This method is called when the sponge would normally absorb water.
     * It cancels the default event and instead handles absorption in a custom way.
     *
     * @param event The sponge absorption event
     */
    @Override
    public void onAbsorb(@NotNull SpongeAbsorbEvent event) {
        event.setCancelled(true);

        List<Block> blocks = getBlocksInManhattanDistance(event, getRange());
        Block sponge = event.getBlock();
        for (Block block : blocks) {
            absorb(block);
        }

        PylonBase.runSyncLater(() -> {
            toDriedSponge(sponge);
        }, 1);
    }

    /**
     * Gets all blocks within a Manhattan distance in a diamond-like pattern.
     *
     * @param event    The sponge absorption event
     * @param distance The maximum Manhattan distance to check
     * @return A list of blocks within the specified distance that are absorbable
     */
    public @NotNull List<Block> getBlocksInManhattanDistance(@NotNull SpongeAbsorbEvent event, int distance) {
        List<Block> result = new ArrayList<>();

        if (distance < 0) {
            return result;
        }

        Block center = event.getBlock(); // sponge block
        World world = center.getWorld();
        WorldBorder border = world.getWorldBorder();
        int centerX = center.getX();
        int centerY = center.getY();
        int centerZ = center.getZ();

        for (int x = centerX - distance; x <= centerX + distance; x++) {
            int remainingX = distance - Math.abs(x - centerX);
            if (remainingX < 0) continue;

            for (int y = centerY - remainingX; y <= centerY + remainingX; y++) {
                int remainingY = remainingX - Math.abs(y - centerY);
                if (remainingY < 0) continue;

                for (int z = centerZ - remainingY; z <= centerZ + remainingY; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!border.isInside(block.getLocation())) {
                        continue;
                    }

                    if (!isAbsorbable(block)) {
                        continue;
                    }

                    result.add(block);
                }
            }
        }

        return result;
    }

    /**
     * Checks if a block can be absorbed by this sponge.
     *
     * @param block The block to check
     * @return true if the block can be absorbed, false otherwise
     */
    public abstract boolean isAbsorbable(@NotNull Block block);

    /**
     * Absorbs a specific block (changes it to air or appropriate state).
     *
     * @param block The block to absorb
     */
    public abstract void absorb(@NotNull Block block);

    /**
     * Transforms the sponge block into its "dried" or used state.
     *
     * @param sponge The sponge block to transform
     */
    public abstract void toDriedSponge(@NotNull Block sponge);

    /**
     * Gets the range of this sponge's absorption ability.
     *
     * @return The Manhattan distance this sponge can absorb liquids within
     */
    public abstract int getRange();

    /**
     * Fix cannot absorb lava
     *
     * @see PowerfulLavaSponge
     * @see HotLavaSponge
     */
    public void tick(double deltaSeconds) {
    }

    @NotNull
    public SpongeAbsorbEvent makeUpEvent() {
        return new SpongeAbsorbEvent(getBlock(), new ArrayList<>());
    }

    public boolean canAbsorb() {
        Location location = getBlock().getLocation();
        for (BlockFace blockFace : NEARBY_FACES) {
            if (isAbsorbable(location.clone().add(blockFace.getDirection()).getBlock())) {
                return true;
            }
        }

        return false;
    }
}
