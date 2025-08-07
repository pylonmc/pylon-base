package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.ItemUtils;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.ProgressItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class CoalFiredPurificationTower extends PylonBlock
        implements PylonFluidBufferBlock, PylonSimpleMultiblock, PylonTickingBlock, PylonGuiBlock {

    private static final Config settings = Settings.get(BaseKeys.COAL_FIRED_PURIFICATION_TOWER);
    public static final double FLUID_MB_PER_SECOND = settings.getOrThrow("fluid-mb-per-second", Integer.class);
    public static final double FLUID_BUFFER = settings.getOrThrow("fluid-buffer-mb", Integer.class);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", Integer.class);
    public static final Map<ItemStack, Integer> FUELS = new HashMap<>();

    static {
        ConfigSection config = settings.getSectionOrThrow("fuels");
        for (String key : config.getKeys()) {
            FUELS.put(PylonUtils.itemFromName(key), config.getOrThrow(key, Integer.class));
        }
    }

    private static final NamespacedKey FUEL_KEY = baseKey("fuel");
    private static final NamespacedKey FUEL_SECONDS_ELAPSED_KEY = baseKey("fuel_seconds_elapsed");

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
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, FLUID_BUFFER, true, false);
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, FLUID_BUFFER, false, true);
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
    public @NotNull Map<@NotNull Vector3i, @NotNull MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();

        components.put(new Vector3i(0, 1, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 2, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 3, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 4, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_CAP));

        return components;
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return TICK_INTERVAL;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
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
        }

        if (!isRunning()) {
            return;
        }

        double toPurify = Math.min(
                deltaSeconds * FLUID_MB_PER_SECOND,
                Math.min(fluidAmount(BaseFluids.DIRTY_HYDRAULIC_FLUID), fluidSpaceRemaining(BaseFluids.HYDRAULIC_FLUID))
        );
        removeFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, toPurify);
        addFluid(BaseFluids.HYDRAULIC_FLUID, toPurify);

        fuelSecondsElapsed += deltaSeconds;
        progressItem.setProgress(fuelSecondsElapsed / FUELS.get(fuel));
        if (fuelSecondsElapsed >= FUELS.get(fuel)) {
            fuel = null;
            progressItem.notifyWindows();
        }
    }

    public boolean isRunning() {
        return fuel != null
                && fluidAmount(BaseFluids.DIRTY_HYDRAULIC_FLUID) > 1.0e-3
                // enough space in output buffer for another tick worth of purification
                && fluidSpaceRemaining(BaseFluids.HYDRAULIC_FLUID) >= FLUID_MB_PER_SECOND * TICK_INTERVAL / 20.0;
    }
}
