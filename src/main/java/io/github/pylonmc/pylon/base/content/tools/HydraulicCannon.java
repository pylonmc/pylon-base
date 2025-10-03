package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.machines.hydraulics.HydraulicRefuelable;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.base.util.DisplayProjectile;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.pylonmc.pylon.core.util.PylonUtils.isPylonSimilar;


public class HydraulicCannon extends PylonItem implements PylonInteractor, HydraulicRefuelable {

    private static final Config settings = getSettings(BaseKeys.HYDRAULIC_CANNON);
    public static final int COOLDOWN_TICKS = settings.getOrThrow("cooldown-ticks", ConfigAdapter.INT);
    public static final double HYDRAULIC_FLUID_CAPACITY = Settings.get(BaseKeys.PORTABLE_FLUID_TANK_COPPER).getOrThrow("capacity", ConfigAdapter.DOUBLE);
    public static final double DIRTY_HYDRAULIC_FLUID_CAPACITY = Settings.get(BaseKeys.PORTABLE_FLUID_TANK_COPPER).getOrThrow("capacity", ConfigAdapter.DOUBLE);
    public static final double HYDRAULIC_FLUID_PER_SHOT = settings.getOrThrow("hydraulic-fluid-per-shot", ConfigAdapter.DOUBLE);
    public static final double DIRTY_HYDRAULIC_FLUID_PER_SHOT = settings.getOrThrow("dirty-hydraulic-fluid-per-shot", ConfigAdapter.DOUBLE);
    public static final Material PROJECTILE_MATERIAL = settings.getOrThrow("projectile.material", ConfigAdapter.MATERIAL);
    public static final float PROJECTILE_THICKNESS = settings.getOrThrow("projectile.thickness", ConfigAdapter.FLOAT);
    public static final float PROJECTILE_LENGTH = settings.getOrThrow("projectile.length", ConfigAdapter.FLOAT);
    public static final float PROJECTILE_SPEED_BLOCKS_PER_SECOND = settings.getOrThrow("projectile.speed-blocks-per-second", ConfigAdapter.FLOAT);
    public static final double PROJECTILE_DAMAGE = settings.getOrThrow("projectile.damage", ConfigAdapter.DOUBLE);
    public static final int PROJECTILE_TICK_INTERVAL = settings.getOrThrow("projectile.tick-interval", ConfigAdapter.INT);
    public static final int PROJECTILE_LIFETIME_TICKS = settings.getOrThrow("projectile.lifetime-ticks", ConfigAdapter.INT);

    @SuppressWarnings("unused")
    public HydraulicCannon(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("cooldown", UnitFormat.SECONDS.format(COOLDOWN_TICKS / 20.0)),
                PylonArgument.of("range", UnitFormat.BLOCKS.format(Math.round(PROJECTILE_SPEED_BLOCKS_PER_SECOND * PROJECTILE_LIFETIME_TICKS / 20.0))),
                PylonArgument.of("speed", UnitFormat.BLOCKS_PER_SECOND.format(PROJECTILE_SPEED_BLOCKS_PER_SECOND)),
                PylonArgument.of("hydraulic-fluid-per-shot", UnitFormat.MILLIBUCKETS.format(HYDRAULIC_FLUID_PER_SHOT)),
                PylonArgument.of("dirty-hydraulic-fluid-per-shot", UnitFormat.MILLIBUCKETS.format(DIRTY_HYDRAULIC_FLUID_PER_SHOT)),
                PylonArgument.of("hydraulic-fluid", BaseUtils.createFluidAmountBar(
                        getHydraulicFluid(),
                        HYDRAULIC_FLUID_CAPACITY,
                        20,
                        TextColor.fromHexString("#212d99")
                )),
                PylonArgument.of("dirty-hydraulic-fluid", BaseUtils.createFluidAmountBar(
                        getDirtyHydraulicFluid(),
                        DIRTY_HYDRAULIC_FLUID_CAPACITY,
                        20,
                        TextColor.fromHexString("#48459b")
                ))
        );
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        if (getHydraulicFluid() < HYDRAULIC_FLUID_PER_SHOT || getDirtyHydraulicFluidSpace() < DIRTY_HYDRAULIC_FLUID_PER_SHOT) {
            return;
        }

        boolean projectileFound = false;
        for (ItemStack stack : event.getPlayer().getInventory()) {
            if (isPylonSimilar(stack, BaseItems.TIN_PROJECTILE)) {
                stack.subtract();
                projectileFound = true;
                break;
            }
        }
        if (!projectileFound) {
            return;
        }

        setHydraulicFluid(getHydraulicFluid() - HYDRAULIC_FLUID_PER_SHOT);
        setDirtyHydraulicFluid(getDirtyHydraulicFluid() + DIRTY_HYDRAULIC_FLUID_PER_SHOT);

        Player player = event.getPlayer();
        player.setCooldown(getStack(), COOLDOWN_TICKS);
        Vector direction = player.getEyeLocation().getDirection();
        Location source = player.getEyeLocation()
                .subtract(0, 0.5, 0)
                .add(direction.clone().multiply(1.5));
        new DisplayProjectile(
                player,
                PROJECTILE_MATERIAL,
                source,
                direction,
                PROJECTILE_THICKNESS,
                PROJECTILE_LENGTH,
                PROJECTILE_SPEED_BLOCKS_PER_SECOND,
                PROJECTILE_DAMAGE,
                PROJECTILE_TICK_INTERVAL,
                PROJECTILE_LIFETIME_TICKS
        );
    }

    @Override
    public double getHydraulicFluid() {
        return getStack().getPersistentDataContainer().get(BaseFluids.HYDRAULIC_FLUID.getKey(), PylonSerializers.DOUBLE);
    }

    @Override
    public double getDirtyHydraulicFluid() {
        return getStack().getPersistentDataContainer().get(BaseFluids.DIRTY_HYDRAULIC_FLUID.getKey(), PylonSerializers.DOUBLE);
    }

    @Override
    public void setHydraulicFluid(double amount) {
        getStack().editPersistentDataContainer(pdc -> {
            pdc.set(BaseFluids.HYDRAULIC_FLUID.getKey(), PylonSerializers.DOUBLE, amount);
        });
    }

    @Override
    public void setDirtyHydraulicFluid(double amount) {
        getStack().editPersistentDataContainer(pdc -> {
            pdc.set(BaseFluids.DIRTY_HYDRAULIC_FLUID.getKey(), PylonSerializers.DOUBLE, amount);
        });
    }

    @Override
    public double getHydraulicFluidCapacity() {
        return HYDRAULIC_FLUID_CAPACITY;
    }

    @Override
    public double getDirtyHydraulicFluidCapacity() {
        return DIRTY_HYDRAULIC_FLUID_CAPACITY;
    }
}
