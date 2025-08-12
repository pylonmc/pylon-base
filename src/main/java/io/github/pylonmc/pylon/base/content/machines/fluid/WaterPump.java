package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;


public class WaterPump extends PylonBlock implements PylonFluidBlock, PylonEntityHolderBlock {

    public static class Item extends PylonItem {

        public final double waterPerSecond = getSettings().getOrThrow("water-per-second", Double.class);

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

    public final double waterPerSecond = getSettings().getOrThrow("water-per-second", Double.class);

    @SuppressWarnings("unused")
    public WaterPump(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.UP));
    }

    @SuppressWarnings("unused")
    public WaterPump(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(double deltaSeconds) {
        return getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER
                ? Map.of(BaseFluids.WATER, waterPerSecond * deltaSeconds)
                : Map.of() ;
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {}
}