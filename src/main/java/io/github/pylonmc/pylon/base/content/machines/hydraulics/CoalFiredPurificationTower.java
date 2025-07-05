package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseBlocks;
import io.github.pylonmc.pylon.base.content.machines.hydraulics.base.SimplePurificationMachine;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.ItemUtils;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.ProgressItem;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.pylonKey;


public class CoalFiredPurificationTower extends SimplePurificationMachine
        implements PylonSimpleMultiblock, PylonTickingBlock, PylonGuiBlock {

    public static final NamespacedKey COAL_FIRED_PURIFICATION_TOWER_KEY = pylonKey("coal_fired_purification_tower");
    public static final ItemStack COAL_FIRED_PURIFICATION_TOWER_STACK = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, COAL_FIRED_PURIFICATION_TOWER_KEY)
            .build();

    public static final double FLUID_MB_PER_SECOND = Settings.get(COAL_FIRED_PURIFICATION_TOWER_KEY).getOrThrow("fluid-mb-per-second", Integer.class);
    public static final double FLUID_BUFFER = Settings.get(COAL_FIRED_PURIFICATION_TOWER_KEY).getOrThrow("fluid-buffer-mb", Integer.class);
    public static final int TICK_INTERVAL = Settings.get(COAL_FIRED_PURIFICATION_TOWER_KEY).getOrThrow("tick-interval", Integer.class);
    public static final Map<ItemStack, Integer> FUELS = new HashMap<>();

    static {
        ConfigSection config = Settings.get(COAL_FIRED_PURIFICATION_TOWER_KEY).getSectionOrThrow("fuels");
        for (String key : config.getKeys()) {
            FUELS.put(config.getItemOrThrow(key), config.getOrThrow(key, Integer.class));
        }
    }

    private static final NamespacedKey FUEL_KEY = pylonKey("fuel");
    private static final NamespacedKey FUEL_SECONDS_ELAPSED_KEY = pylonKey("fuel_seconds_elapsed");

    public static final Component NO_FUEL = Component.translatable("pylon.pylonbase.message.hydraulic_status.no_fuel");

    private @Nullable ItemStack fuel;
    private double fuelSecondsElapsed;

    private final VirtualInventory inventory = new VirtualInventory(1);
    private final FuelProgressItem progressItem = new FuelProgressItem();

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "fluid_mb_per_second", UnitFormat.MILLIBUCKETS_PER_SECOND.format(FLUID_MB_PER_SECOND)
            );
        }
    }

    @Getter private Component status = IDLE;

    private class FuelProgressItem extends ProgressItem {
        public FuelProgressItem() {
            super(Material.BLAZE_POWDER, true);
        }

        @Override
        protected void completeItem(@NotNull ItemStackBuilder builder) {
            builder.name(Component.translatable(
                    "pylon.pylonbase.item.coal_fired_purification_tower.progress_item." + (isRunning() ? "running" : "idle"))
            );
        }

        @Override
        public @Nullable Duration getTotalTime() {
            return fuel == null
                    ? null
                    : Duration.ofSeconds(FUELS.get(fuel));
        }
    }

    @SuppressWarnings("unused")
    public CoalFiredPurificationTower(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        fuel = null;
        fuelSecondsElapsed = 0.0;
    }

    @SuppressWarnings("unused")
    public CoalFiredPurificationTower(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        fuel = pdc.get(FUEL_KEY, PylonSerializers.ITEM_STACK);
        fuelSecondsElapsed = pdc.get(FUEL_SECONDS_ELAPSED_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        super.write(pdc);
        PdcUtils.setNullable(pdc, FUEL_KEY, PylonSerializers.ITEM_STACK, fuel);
        pdc.set(FUEL_SECONDS_ELAPSED_KEY, PylonSerializers.DOUBLE, fuelSecondsElapsed);
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # f # # # #",
                        "# # # # x # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('f', progressItem)
                .addIngredient('x', inventory)
                .addIngredient('#', GuiItems.background())
                .build();
    }

    @Override
    public double getDirtyHydraulicFluidBuffer() {
        return FLUID_BUFFER;
    }

    @Override
    public double getHydraulicFluidBuffer() {
        return FLUID_BUFFER;
    }

    @Override
    public @NotNull Map<@NotNull Vector3i, @NotNull MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();

        components.put(new Vector3i(0, 1, 0), new PylonMultiblockComponent(BaseBlocks.PURIFICATION_TOWER_GLASS_KEY));
        components.put(new Vector3i(0, 2, 0), new PylonMultiblockComponent(BaseBlocks.PURIFICATION_TOWER_GLASS_KEY));
        components.put(new Vector3i(0, 3, 0), new PylonMultiblockComponent(BaseBlocks.PURIFICATION_TOWER_GLASS_KEY));
        components.put(new Vector3i(0, 4, 0), new PylonMultiblockComponent(BaseBlocks.PURIFICATION_TOWER_CAP));

        return components;
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return TICK_INTERVAL;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
            status = INCOMPLETE;
            return;
        }

        if (fuel == null) {
            ItemStack item = inventory.getUnsafeItem(0);
            for (Map.Entry<ItemStack, Integer> fuel : FUELS.entrySet()) {
                if (item == null || !ItemUtils.isPylonSimilar(item, fuel.getKey())) {
                    continue;
                }

                inventory.setItemAmount(null, 0, item.getAmount() - 1);
                this.fuel = fuel.getKey();
                fuelSecondsElapsed = 0.0;
                break;
            }
            if (fuel == null) {
                status = NO_FUEL;
            }
        }

        if (!isRunning()) {
            status = IDLE;
            return;
        }

        purify(deltaSeconds * FLUID_MB_PER_SECOND);
        fuelSecondsElapsed += deltaSeconds;
        progressItem.setProgress(fuelSecondsElapsed / FUELS.get(fuel));
        if (fuelSecondsElapsed >= FUELS.get(fuel)) {
            fuel = null;
            progressItem.notifyWindows();
        }
        status = WORKING;
    }

    public boolean isRunning() {
        return fuel != null
                && dirtyHydraulicFluidAmount > 1.0e-3
                // enough space in output buffer for another tick worth of purification
                && FLUID_BUFFER - hydraulicFluidAmount >= FLUID_MB_PER_SECOND * TICK_INTERVAL / 20.0;
    }
}
