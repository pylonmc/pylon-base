package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

/**
 * WetPowerfulWaterSponge is the result of a {@link PowerfulWaterSponge} absorbing water.
 * <p>
 * This sponge is "used" and does not have any special abilities.
 * It represents the end state of a PowerfulWaterSponge after it has absorbed water.
 * </p>
 *
 * @author balugaq
 * @see PowerfulSponge
 * @see PowerfulWaterSponge
 */
public class WetPowerfulWaterSponge extends PylonBlock {
    public WetPowerfulWaterSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        Location location = block.getLocation();
        if (location.getWorld().getEnvironment() == World.Environment.NETHER) {
            Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
                // Dry out the sponge in the nether
                BlockStorage.breakBlock(location, new BlockBreakContext.PluginBreak(false));
                BlockStorage.placeBlock(location, BaseKeys.POWERFUL_WATER_SPONGE);
            }, 1);
        }
    }

    public WetPowerfulWaterSponge(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }
}