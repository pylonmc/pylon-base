package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSponge;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SpongeAbsorbEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
 *                                             90% chance     |
 *                                            Inside water    |
 *                                                            ↓
 *                                                   [Obsidian]
 * </pre>
 * </p>
 *
 * @author balugaq
 * @see PylonSponge
 * @see PowerfulWaterSponge
 * @see PowerfulLavaSponge
 * @see HotLavaSponge
 */
public abstract class PowerfulSponge extends PylonBlock implements PylonSponge {
    public static final String KEY_PLAYER = "player";

    public PowerfulSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        if (context instanceof BlockCreateContext.PlayerPlace p) {
            // Will it work...?
            BlockStorage.get(block).getSettings().set(KEY_PLAYER, p.getPlayer().getUniqueId());
        }
    }

    /**
     * Gets the player who placed the sponge block.
     *
     * @param block The sponge block
     * @return The player who placed the block, or null if not found
     */
    public static @NotNull Player getPlacer(@NotNull Block block) {
        return Bukkit.getPlayer(UUID.fromString(BlockStorage.get(block).getSettings().get(KEY_PLAYER, String.class)));
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
        Player player = getPlacer(sponge);
        for (Block block : blocks) {
            // before absorbs, we need to check permission
            if (!BaseUtils.canBreakBlock(player, block)) {
                continue;
            }

            // now we ensured that the player can break the block
            absorb(block);
        }

        toDriedSponge(sponge);
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
                    if (border.isInside(block.getLocation())) {
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
}
