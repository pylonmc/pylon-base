package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * HotPowerfulLavaSponge is a powerful sponge that can absorb lava but has special behavior.
 * <p>
 * When placed in water, it has a:
 * <ul>
 *   <li>90% chance of turning into obsidian by default</li>
 *   <li>10% chance of turning back into a {@link PowerfulLavaSponge} by default</li>
 * </ul>
 * </p>
 *
 * @author balugaq
 * @see PowerfulSponge
 * @see PowerfulLavaSponge
 */
public class HotPowerfulLavaSponge extends PowerfulSponge {
    private static final Config settings = Settings.get(BaseKeys.HOT_POWERFUL_LAVA_SPONGE);
    private static final int CHECK_RANGE = settings.getOrThrow("check-range", ConfigAdapter.INT);
    private static final double REUSE_RATE = settings.getOrThrow("reuse-rate", ConfigAdapter.DOUBLE);
    private final Location particleDisplayLoc = getBlock().getLocation().clone().add(0.5, 0.5, 0.5);

    public HotPowerfulLavaSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public HotPowerfulLavaSponge(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    /**
     * Checks if a block is absorbable by this hot lava sponge.
     * Hot lava sponges can absorb:
     * <ul>
     *   <li>Water blocks</li>
     *   <li>Waterlogged blocks</li>
     *   <li>Water cauldrons</li>
     * </ul>
     *
     * @param block The block to check
     * @return true if the block can be absorbed, false otherwise
     */
    @Override
    public boolean isAbsorbable(@NotNull Block block) {
        return block.getType() == Material.WATER
                || block.getBlockData() instanceof Waterlogged
                || block.getType() == Material.WATER_CAULDRON;
    }

    /**
     * Absorbs a block by removing the water from it.
     * <ul>
     *   <li>Water blocks become air</li>
     *   <li>Waterlogged blocks become un-waterlogged</li>
     *   <li>Water cauldrons become empty cauldrons</li>
     * </ul>
     *
     * @param block The block to absorb
     */
    @Override
    public void absorb(@NotNull Block block) {
        if (block.getType() == Material.WATER) {
            block.setType(Material.AIR);
        } else if (block.getBlockData() instanceof Waterlogged w) {
            w.setWaterlogged(false);
            block.setBlockData(w);
        } else if (block.getType() == Material.WATER_CAULDRON) {
            block.setType(Material.CAULDRON);
        }
    }

    public void tick(double deltaSeconds) {
        BaseUtils.spawnParticle(Particle.FLAME, particleDisplayLoc, 3);

        tryAbsorbNearbyBlocks();
    }

    /**
     * Gets the range of this sponge's absorption ability.
     *
     * @return The Manhattan distance this sponge can absorb water within
     */
    @Override
    public int getRange() {
        return CHECK_RANGE;
    }

    /**
     * Transforms this sponge based on chance by default:
     * <ul>
     *   <li>90% chance: turns into obsidian with particle effects</li>
     *   <li>10% chance: turns back into a {@link PowerfulLavaSponge}</li>
     * </ul>
     *
     * @param sponge The sponge block to transform
     */
    @Override
    public void toDriedSponge(@NotNull Block sponge) {
        BlockStorage.breakBlock(sponge, new BlockBreakContext.PluginBreak(false));
        if (ThreadLocalRandom.current().nextDouble() > REUSE_RATE) {
            // 90% chance of becoming unusable obsidian
            sponge.setType(Material.OBSIDIAN);
            BaseUtils.spawnParticle(Particle.FLAME, particleDisplayLoc, 20);
            BaseUtils.spawnParticle(Particle.SMOKE, particleDisplayLoc, 50);
        } else {
            // 10% chance of reusing the sponge
            BlockStorage.placeBlock(sponge, BaseKeys.POWERFUL_LAVA_SPONGE);
        }
    }

    /**
     * @author balugaq
     */
    public static class Item extends PylonItem {
        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        /**
         * Gets the placeholders for this item, including the check range.
         *
         * @return A list of placeholders for this item
         */
        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("check-range", UnitFormat.BLOCKS.format(CHECK_RANGE).decimalPlaces(1)),
                    PylonArgument.of("reuse-rate", UnitFormat.PERCENT.format(REUSE_RATE * 100).decimalPlaces(1))
            );
        }
    }
}