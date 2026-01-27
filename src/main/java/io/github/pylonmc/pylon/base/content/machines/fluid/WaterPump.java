package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.rebar.block.PylonBlock;
import io.github.pylonmc.rebar.block.base.PylonFluidBlock;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.config.PylonConfig;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.fluid.FluidPointType;
import io.github.pylonmc.rebar.fluid.PylonFluid;
import io.github.pylonmc.rebar.i18n.PylonArgument;
import io.github.pylonmc.rebar.item.PylonItem;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;


public class WaterPump extends PylonBlock implements PylonFluidBlock {

    public final double waterPerSecond = getSettings().getOrThrow("water-per-second", ConfigAdapter.DOUBLE);

    public static class Item extends PylonItem {

        public final double waterPerSecond = getSettings().getOrThrow("water-per-second", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(PylonArgument.of(
                    "water_per_second", UnitFormat.MILLIBUCKETS_PER_SECOND.format(waterPerSecond)
            ));
        }
    }

    @SuppressWarnings("unused")
    public WaterPump(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.UP);
    }

    @SuppressWarnings("unused")
    public WaterPump(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids() {
        return getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER
                ? Map.of(BaseFluids.WATER, waterPerSecond * PylonConfig.FLUID_TICK_INTERVAL / 20.0)
                : Map.of();
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {}
}