package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

/**
 * HotLavaSponge is a powerful sponge that can absorb lava but has special behavior.
 * <p>
 * When placed in water, it has a:
 * <ul>
 *   <li>90% chance of turning into obsidian</li>
 *   <li>10% chance of turning back into a {@link PowerfulLavaSponge}</li>
 * </ul>
 * </p>
 *
 * @author balugaq
 * @see PowerfulSponge
 * @see PowerfulLavaSponge
 */
public class HotLavaSponge extends PowerfulSponge {
    public static final Random RANDOM = new Random();
    public final int CHECK_RANGE = getSettings().getOrThrow("check_range", Integer.class);

    public HotLavaSponge(@NotNull Block block) {
        super(block);
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
     * Transforms this sponge based on chance:
     * <ul>
     *   <li>90% chance: turns into obsidian with particle effects</li>
     *   <li>10% chance: turns back into a {@link PowerfulLavaSponge}</li>
     * </ul>
     *
     * @param sponge The sponge block to transform
     */
    @Override
    public void toDriedSponge(@NotNull Block sponge) {
        BlockStorage.breakBlock(sponge);
        if (RANDOM.nextDouble() > 0.1) {
            // 90% chance of becoming unusable obsidian
            sponge.setType(Material.OBSIDIAN);
            Location explodeLoc = sponge.getLocation().add(0.5, 0.5, 0.5);
            BaseUtils.spawnParticle(Particle.FLAME, explodeLoc, 20);
            BaseUtils.spawnParticle(Particle.SMOKE, explodeLoc, 50);
        } else {
            // 10% chance of reusing the sponge
            BlockStorage.placeBlock(sponge, BaseKeys.POWERFUL_LAVA_SPONGE);
        }
    }

    /**
     * @author balugaq
     */
    public static class Item extends PylonItem {

        public final int CHECK_RANGE = getSettings().getOrThrow("check_range", Integer.class);

        /**
         * Constructs a new HotLavaSponge item with the given item stack.
         *
         * @param stack The item stack
         */
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
                    PylonArgument.of("check_range", UnitFormat.BLOCKS.format(CHECK_RANGE).decimalPlaces(1))
            );
        }
    }
}