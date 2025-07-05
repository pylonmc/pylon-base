package io.github.pylonmc.pylon.base.items.hydraulic.purification;

import io.github.pylonmc.pylon.base.BaseBlocks;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class SolarPurificationTower extends SimplePurificationMachine implements PylonSimpleMultiblock, PylonTickingBlock {

    public static final NamespacedKey SOLAR_PURIFICATION_TOWER_1_KEY = pylonKey("solar_purification_tower_1");
    public static final NamespacedKey SOLAR_PURIFICATION_TOWER_2_KEY = pylonKey("solar_purification_tower_2");
    public static final NamespacedKey SOLAR_PURIFICATION_TOWER_3_KEY = pylonKey("solar_purification_tower_3");
    public static final NamespacedKey SOLAR_PURIFICATION_TOWER_4_KEY = pylonKey("solar_purification_tower_4");
    public static final NamespacedKey SOLAR_PURIFICATION_TOWER_5_KEY = pylonKey("solar_purification_tower_5");
    public static final ItemStack SOLAR_PURIFICATION_TOWER_1_STACK = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, SOLAR_PURIFICATION_TOWER_1_KEY)
            .build();
    public static final ItemStack SOLAR_PURIFICATION_TOWER_2_STACK = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, SOLAR_PURIFICATION_TOWER_2_KEY)
            .build();
    public static final ItemStack SOLAR_PURIFICATION_TOWER_3_STACK = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, SOLAR_PURIFICATION_TOWER_3_KEY)
            .build();
    public static final ItemStack SOLAR_PURIFICATION_TOWER_4_STACK = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, SOLAR_PURIFICATION_TOWER_4_KEY)
            .build();
    public static final ItemStack SOLAR_PURIFICATION_TOWER_5_STACK = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, SOLAR_PURIFICATION_TOWER_5_KEY)
            .build();

    public final double fluidMbPerSecond = getSettings().getOrThrow("fluid-mb-per-second", Integer.class);
    public final double fluidBuffer = getSettings().getOrThrow("fluid-buffer-mb", Integer.class);
    public final double rainSpeedFraction = getSettings().getOrThrow("rain-speed-fraction", Double.class);
    public final int lensLayers = getSettings().getOrThrow("lens-layers", Integer.class);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", Integer.class);

    public static final Component NO_SUNLIGHT = Component.translatable("pylon.pylonbase.message.hydraulic_status.no_fuel");
    public static final Component WORKING_WITH_REDUCED_EFFICIENCY = Component.translatable("pylon.pylonbase.message.hydraulic_status.working_with_reduced_efficiency");

    public static class Item extends PylonItem {

        public final double fluidMbPerSecond = getSettings().getOrThrow("fluid-mb-per-second", Integer.class);
        public final double rainSpeedFraction = getSettings().getOrThrow("rain-speed-fraction", Double.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "rain_speed_percentage", UnitFormat.PERCENT.format(rainSpeedFraction * 100),
                    "fluid_mb_per_second", UnitFormat.MILLIBUCKETS_PER_SECOND.format(fluidMbPerSecond)
            );
        }
    }

    @Getter private Component status = IDLE;

    @SuppressWarnings("unused")
    public SolarPurificationTower(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public SolarPurificationTower(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    double getDirtyHydraulicFluidBuffer() {
        return fluidBuffer;
    }

    @Override
    double getHydraulicFluidBuffer() {
        return fluidBuffer;
    }

    @Override
    public @NotNull Map<@NotNull Vector3i, @NotNull MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();

        components.put(new Vector3i(0, 1, 0), new PylonMultiblockComponent(BaseBlocks.PURIFICATION_TOWER_GLASS_KEY));
        components.put(new Vector3i(0, 2, 0), new PylonMultiblockComponent(BaseBlocks.PURIFICATION_TOWER_GLASS_KEY));
        components.put(new Vector3i(0, 3, 0), new PylonMultiblockComponent(BaseBlocks.PURIFICATION_TOWER_GLASS_KEY));
        components.put(new Vector3i(0, 4, 0), new PylonMultiblockComponent(BaseBlocks.PURIFICATION_TOWER_CAP));

        for (int j = 1; j < lensLayers + 1; j++) {
            for (int i = 0; i < 1 + 4*j; i++) {
                components.put(new Vector3i(2*j, 1, i - 2*j), new PylonMultiblockComponent(BaseBlocks.SOLAR_LENS_KEY));
                components.put(new Vector3i(-2*j, 1, i - 2*j), new PylonMultiblockComponent(BaseBlocks.SOLAR_LENS_KEY));
                components.put(new Vector3i(i - 2*j, 1, 2*j), new PylonMultiblockComponent(BaseBlocks.SOLAR_LENS_KEY));
                components.put(new Vector3i(i - 2*j, 1, -2*j), new PylonMultiblockComponent(BaseBlocks.SOLAR_LENS_KEY));
            }
        }

        return components;
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return tickInterval;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
            status = INCOMPLETE;
            return;
        }

        if (!getBlock().getWorld().isDayTime()) {
            status = NO_SUNLIGHT;
            return;
        }

        if (!getBlock().getWorld().isClearWeather()) {
            purify(deltaSeconds * fluidMbPerSecond * rainSpeedFraction);
            status = WORKING_WITH_REDUCED_EFFICIENCY;
            return;
        }

        purify(deltaSeconds * fluidMbPerSecond);
        status = WORKING;
    }
}
