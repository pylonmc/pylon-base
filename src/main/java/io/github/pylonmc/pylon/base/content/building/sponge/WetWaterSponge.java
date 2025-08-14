package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

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

        // Dry out the sponge in the nether
        Location location = block.getLocation();
        if (location.getWorld().getEnvironment() == World.Environment.NETHER) {
            BlockStorage.breakBlock(location);
            BlockStorage.placeBlock(location, BaseKeys.POWERFUL_WATER_SPONGE);
            BaseUtils.spawnParticle(Particle.ELECTRIC_SPARK, location.add(0.5, 1, 0.5), 40);
        }
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