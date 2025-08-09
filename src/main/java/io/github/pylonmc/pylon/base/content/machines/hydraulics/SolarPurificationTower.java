package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.ComponentLike;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SolarPurificationTower extends PylonBlock
        implements PylonSimpleMultiblock, PylonTickingBlock, PylonFluidBufferBlock {

    public final double fluidMbPerSecond = getSettings().getOrThrow("fluid-mb-per-second", Integer.class);
    public final double fluidBuffer = getSettings().getOrThrow("fluid-buffer-mb", Integer.class);
    public final double rainSpeedFraction = getSettings().getOrThrow("rain-speed-fraction", Double.class);
    public final int lensLayers = getSettings().getOrThrow("lens-layers", Integer.class);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", Integer.class);

    public static class Item extends PylonItem {

        public final double fluidMbPerSecond = getSettings().getOrThrow("fluid-mb-per-second", Integer.class);
        public final double rainSpeedFraction = getSettings().getOrThrow("rain-speed-fraction", Double.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("rain_speed_percentage", UnitFormat.PERCENT.format(rainSpeedFraction * 100)),
                    PylonArgument.of("fluid_mb_per_second", UnitFormat.MILLIBUCKETS_PER_SECOND.format(fluidMbPerSecond))
            );
        }
    }

    @SuppressWarnings("unused")
    public SolarPurificationTower(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, fluidBuffer, true, false);
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, fluidBuffer, false, true);
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
    public int getCustomTickRate(int globalTickRate) {
        return tickInterval;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded() || !getBlock().getWorld().isDayTime()) {
            return;
        }

        double multiplier = getBlock().getWorld().isClearWeather() ? 1.0 : rainSpeedFraction;
        double toPurify = Math.min(
                deltaSeconds * fluidMbPerSecond * multiplier,
                Math.min(fluidAmount(BaseFluids.DIRTY_HYDRAULIC_FLUID), fluidSpaceRemaining(BaseFluids.HYDRAULIC_FLUID))
        );
        removeFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, toPurify);
        addFluid(BaseFluids.HYDRAULIC_FLUID, toPurify);
    }
}
