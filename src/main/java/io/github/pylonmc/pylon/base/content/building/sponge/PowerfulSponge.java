package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSponge;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.util.PylonUtils;
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
 *   <li>{@link LavaSponge} - able to absorb lava</li>
 *   <li>{@link HotLavaSponge} - able to absorb lava, but with special behavior (90% chance turn into obsidian,
 *   10% chance turn back into {@link LavaSponge})</li>
 * </ul>
 * </p>
 * <p>
 * Powerful Sponges evolutions:
 * <pre>
 *                          Fireproof rune powered
 *   [PowerfulWaterSponge] -----------------------→  [LavaSponge]
 *     Dry out  ↑ |                            90% chance   ↑ |
 *     in Blast | | Inside water              Inside water  | | Inside lava
 *     Furnace  | ↓                                         | ↓
 *   [WetPowerfulWaterSponge]                        [HotLavaSponge]
 *     Inside     |                            10% chance     |
 *     Nether     |                           Inside water    |
 *                ↓                                           ↓
 *   [PowerfulWaterSponge]                           [Obsidian]
 * </pre>
 * </p>
 *
 * @author balugaq
 * @see PylonSponge
 * @see PowerfulWaterSponge
 * @see LavaSponge
 * @see HotLavaSponge
 */
public abstract class PowerfulSponge extends PylonBlock implements PylonSponge, PylonTickingBlock {
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

        onAbsorb(event.getBlock());
    }

    private void onAbsorb(@NotNull Block sponge) {
        List<Block> blocks = getBlocksInManhattanDistance(sponge, getRange());
        for (Block block : blocks) {
            absorb(block);
        }

        if (!blocks.isEmpty()) {
            toDriedSponge(sponge);
        }
    }

    /**
     * Gets all blocks within a Manhattan distance in a diamond-like pattern.
     *
     * @param sponge   The sponge block
     * @param distance The maximum Manhattan distance to check
     * @return A list of blocks within the specified distance that are absorbable
     */
    private @NotNull List<Block> getBlocksInManhattanDistance(@NotNull Block sponge, int distance) {
        List<Block> result = new ArrayList<>();

        if (distance < 0) {
            return result;
        }

        World world = sponge.getWorld();
        WorldBorder border = world.getWorldBorder();
        int centerX = sponge.getX();
        int centerY = sponge.getY();
        int centerZ = sponge.getZ();

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
     * Used to absorb lava, waterlogged blocks
     *
     * @see PowerfulWaterSponge
     * @see LavaSponge
     * @see HotLavaSponge
     */
    public void tick(double deltaSeconds) {
    }

    /**
     * Try to absorb nearby blocks
     *
     * @see HotLavaSponge#tick(double)
     * @see LavaSponge#tick(double)
     */
    public void tryAbsorbNearbyBlocks() {
        Location location = getBlock().getLocation();
        for (BlockFace blockFace : PylonUtils.IMMEDIATE_FACES) {
            if (isAbsorbable(location.clone().add(blockFace.getDirection()).getBlock())) {
                // Find a nearby absorbable block
                onAbsorb(getBlock());
                return;
            }
        }
    }
}
