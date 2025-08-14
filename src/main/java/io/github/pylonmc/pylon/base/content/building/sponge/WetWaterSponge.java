package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * WetWaterSponge is the result of a {@link PowerfulWaterSponge} absorbing water.
 * <p>
 * This sponge is "used" and does not have any special abilities.
 * It represents the end state of a PowerfulWaterSponge after it has absorbed water.
 * </p>
 *
 * @author balugaq
 * @see PowerfulSponge
 * @see PowerfulWaterSponge
 */
public class WetWaterSponge extends PylonBlock {
    protected WetWaterSponge(@NotNull Block block) {
        super(block);
    }

    /**
     * Item representation of a WetWaterSponge.
     *
     * @author balugaq
     */
    public static class Item extends PylonItem {
        /**
         * Constructs a new WetWaterSponge item with the given item stack.
         *
         * @param stack The item stack
         */
        public Item(@NotNull ItemStack stack) {
            super(stack);
        }
    }
}