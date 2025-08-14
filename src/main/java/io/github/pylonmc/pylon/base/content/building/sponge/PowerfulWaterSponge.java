package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * PowerfulWaterSponge is a powerful sponge that can absorb water in a large area.
 * <p>
 * When it absorbs water, it transforms into a {@link WetWaterSponge}.
 * </p>
 *
 * @author balugaq
 * @see PowerfulSponge
 * @see WetWaterSponge
 */
public class PowerfulWaterSponge extends PowerfulSponge {
    public final int CHECK_RANGE = getSettings().getOrThrow("check_range", Integer.class);

    public PowerfulWaterSponge(@NotNull Block block) {
        super(block);
    }

    /**
     * Checks if a block is absorbable by this water sponge.
     * Water sponges can absorb:
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
     *   <li>Waterlogged blocks become unw waterlogged</li>
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
     * Transforms this sponge into a wet sponge.
     * This method breaks the current block and places a WetWaterSponge in its place.
     *
     * @param sponge The sponge block to transform
     */
    public void toDriedSponge(@NotNull Block sponge) {
        BlockStorage.breakBlock(sponge);
        BlockStorage.placeBlock(sponge, BaseKeys.WET_WATER_SPONGE);
    }

    /**
     * @author balugaq
     */
    public static class Item extends PylonItem {

        public final int CHECK_RANGE = getSettings().getOrThrow("check_range", Integer.class);

        /**
         * Constructs a new PowerfulWaterSponge item with the given item stack.
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
