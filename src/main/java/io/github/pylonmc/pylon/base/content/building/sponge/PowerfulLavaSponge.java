package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author balugaq
 */
public class PowerfulLavaSponge extends PowerfulSponge {
    private static final Config settings = Settings.get(BaseKeys.POWERFUL_LAVA_SPONGE);
    public static final int CHECK_RANGE = settings.getOrThrow("check_range", Integer.class);

    public PowerfulLavaSponge(@NotNull Block block) {
        super(block);
    }

    /**
     * Checks if a block is absorbable by this lava sponge.
     * Lava sponges can absorb:
     * <ul>
     *   <li>Lava blocks</li>
     *   <li>Lava cauldrons</li>
     * </ul>
     *
     * @param block The block to check
     * @return true if the block can be absorbed, false otherwise
     */
    @Override
    public boolean isAbsorbable(@NotNull Block block) {
        return block.getType() == Material.LAVA || block.getType() == Material.LAVA_CAULDRON;
    }

    /**
     * Absorbs a block by removing the lava from it.
     * <ul>
     *   <li>Lava blocks become air</li>
     *   <li>Lava cauldrons become empty cauldrons</li>
     * </ul>
     *
     * @param block The block to absorb
     */
    @Override
    public void absorb(@NotNull Block block) {
        if (block.getType() == Material.LAVA) {
            block.setType(Material.AIR);
        } else if (block.getType() == Material.LAVA_CAULDRON) {
            block.setType(Material.CAULDRON);
        }
    }

    /**
     * Gets the range of this sponge's absorption ability.
     *
     * @return The Manhattan distance this sponge can absorb lava within
     */
    @Override
    public int getRange() {
        return CHECK_RANGE;
    }

    /**
     * Transforms this sponge into a hot lava sponge.
     * This method breaks the current block and places a HotLavaSponge in its place.
     *
     * @param sponge The sponge block to transform
     */
    @Override
    public void toDriedSponge(@NotNull Block sponge) {
        BlockStorage.breakBlock(sponge);
        BlockStorage.placeBlock(sponge, BaseKeys.HOT_LAVA_SPONGE);
    }

    /**
     * @author balugaq
     */
    public static class Item extends PylonItem {

        public final int CHECK_RANGE = getSettings().getOrThrow("check_range", Integer.class);

        /**
         * Constructs a new PowerfulLavaSponge item with the given item stack.
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
