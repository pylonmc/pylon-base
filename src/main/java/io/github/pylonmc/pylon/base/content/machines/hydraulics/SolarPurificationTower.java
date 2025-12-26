package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SolarPurificationTower extends PylonBlock implements PylonSimpleMultiblock, PylonTickingBlock, PylonFluidBufferBlock {

    public final double purificationSpeed = getSettings().getOrThrow("purification-speed", ConfigAdapter.DOUBLE);
    public final double purificationEfficiency = getSettings().getOrThrow("purification-efficiency", ConfigAdapter.DOUBLE);
    public final double hydraulicFluidBuffer = getSettings().getOrThrow("hydraulic-fluid-buffer", ConfigAdapter.DOUBLE);
    public final double rainSpeedFraction = getSettings().getOrThrow("rain-speed-fraction", ConfigAdapter.DOUBLE);
    public final int lensLayers = getSettings().getOrThrow("lens-layers", ConfigAdapter.INT);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    public static class Item extends PylonItem {

        public final double purificationSpeed = getSettings().getOrThrow("purification-speed", ConfigAdapter.DOUBLE);
        public final double purificationEfficiency = getSettings().getOrThrow("purification-efficiency", ConfigAdapter.DOUBLE);
        public final double rainSpeedFraction = getSettings().getOrThrow("rain-speed-fraction", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("rain_speed_percentage", UnitFormat.PERCENT.format(rainSpeedFraction * 100)),
                    PylonArgument.of("purification_speed", UnitFormat.MILLIBUCKETS_PER_SECOND.format(purificationSpeed)),
                    PylonArgument.of("purification_efficiency", UnitFormat.PERCENT.format(purificationEfficiency * 100))
            );
        }
    }

    @SuppressWarnings("unused")
    public SolarPurificationTower(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(tickInterval);
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidBuffer, true, false);
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidBuffer, false, true);
    }

    @SuppressWarnings("unused")
    public SolarPurificationTower(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Map<@NotNull Vector3i, @NotNull MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();

        components.put(new Vector3i(0, 1, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 2, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 3, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 4, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_CAP));

        for (int j = 1; j < lensLayers + 1; j++) {
            for (int i = 0; i < 1 + 4*j; i++) {
                components.put(new Vector3i(2*j, 1, i - 2*j), new PylonMultiblockComponent(BaseKeys.SOLAR_LENS));
                components.put(new Vector3i(-2*j, 1, i - 2*j), new PylonMultiblockComponent(BaseKeys.SOLAR_LENS));
                components.put(new Vector3i(i - 2*j, 1, 2*j), new PylonMultiblockComponent(BaseKeys.SOLAR_LENS));
                components.put(new Vector3i(i - 2*j, 1, -2*j), new PylonMultiblockComponent(BaseKeys.SOLAR_LENS));
            }
        }

        return components;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded() || !getBlock().getWorld().isDayTime()) {
            return;
        }

        double multiplier = getBlock().getWorld().isClearWeather() ? 1.0 : rainSpeedFraction;
        double toPurify = Math.min(
                // maximum amount of dirty hydraulic fluid that can be purified this tick
                deltaSeconds * purificationSpeed * multiplier,
                Math.min(
                        // amount of dirty hydraulic fluid available
                        fluidAmount(BaseFluids.DIRTY_HYDRAULIC_FLUID),
                        // how much dirty hydraulic fluid can be converted without overflowing the hydraulic fluid buffer
                        fluidSpaceRemaining(BaseFluids.HYDRAULIC_FLUID) / purificationEfficiency
                )
        );

        removeFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, toPurify);
        addFluid(BaseFluids.HYDRAULIC_FLUID, toPurify * purificationEfficiency);
    }

    @Override
    public @Nullable BlockFace getFacing() {
        return PylonFluidBufferBlock.super.getFacing();
    }
}
