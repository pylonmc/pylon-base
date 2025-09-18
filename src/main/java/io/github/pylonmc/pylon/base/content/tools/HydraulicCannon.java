package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.util.DisplayProjectile;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HydraulicCannon extends PylonItem implements PylonInteractor {

    private static final Config settings = getSettings(BaseKeys.HYDRAULIC_CANNON);
    public static Material projectileMaterial = settings.getOrThrow("projectile.material", ConfigAdapter.MATERIAL);
    public static float projectileThickness = settings.getOrThrow("projectile.thickness", ConfigAdapter.FLOAT);
    public static float projectileLength = settings.getOrThrow("projectile.length", ConfigAdapter.FLOAT);
    public static float projectileSpeedBlocksPerSecond = settings.getOrThrow("projectile.speed-blocks-per-second", ConfigAdapter.FLOAT);
    public static double projectileDamage = settings.getOrThrow("projectile.damage", ConfigAdapter.DOUBLE);
    public static int projectileTickInterval = settings.getOrThrow("projectile.tick-interval", ConfigAdapter.INT);
    public static int projectileLifetimeTicks = settings.getOrThrow("projectile.lifetime-ticks", ConfigAdapter.INT);

    @SuppressWarnings("unused")
    public HydraulicCannon(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("range", UnitFormat.BLOCKS.format(Math.round(projectileSpeedBlocksPerSecond * projectileLifetimeTicks / 20.0))),
                PylonArgument.of("speed", UnitFormat.BLOCKS_PER_SECOND.format(projectileSpeedBlocksPerSecond))
        );
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location source = player.getEyeLocation().subtract(0, 0.5, 0);
        Vector direction = player.getEyeLocation().getDirection();
        DisplayProjectile.spawn(
                player,
                projectileMaterial,
                source,
                direction,
                projectileThickness,
                projectileLength,
                projectileSpeedBlocksPerSecond,
                projectileDamage,
                projectileTickInterval,
                projectileLifetimeTicks
        );
    }
}
