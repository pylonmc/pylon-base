package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class WaterPump extends PylonBlock implements PylonFluidBlock, PylonEntityHolderBlock {

    public static class Item extends PylonItem {

        public final double waterPerSecond = getSettings().getOrThrow("water-per-second", Double.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "water_per_second", UnitFormat.MILLIBUCKETS_PER_SECOND.format(waterPerSecond)
            );
        }
    }

    public final double waterPerSecond = getSettings().getOrThrow("water-per-second", Double.class);

    @SuppressWarnings("unused")
    public WaterPump(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings("unused")
    public WaterPump(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        return Map.of(
                "output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.UP)
        );
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(double deltaSeconds) {
        if (getBlock().getRelative(BlockFace.DOWN).getType() != Material.WATER) {
            return Map.of();
        }
        return Map.of(BaseFluids.WATER, waterPerSecond * deltaSeconds);
    }

    @Override
    public void removeFluid(@NotNull PylonFluid fluid, double amount) {
        // nothing, water block is treated as infinite lol
    }
}