package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.machines.simple.Grindstone;
import io.github.pylonmc.pylon.base.recipes.GrindstoneRecipe;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;


public class HydraulicGrindstoneTurner extends PylonBlock implements PylonMultiblock, PylonTickingBlock, PylonFluidBufferBlock {

    private static final Config settings = Settings.get(BaseKeys.HYDRAULIC_GRINDSTONE_TURNER);
    public static final int HYDRAULIC_FLUID_USAGE = settings.getOrThrow("hydraulic-fluid-usage", ConfigAdapter.INT);
    public static final double HYDRAULIC_FLUID_BUFFER = settings.getOrThrow("hydraulic-fluid-buffer", ConfigAdapter.INT);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("hydraulic-fluid-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(HYDRAULIC_FLUID_USAGE))
            );
        }
    }

    @SuppressWarnings("unused")
    public HydraulicGrindstoneTurner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(Grindstone.CYCLE_DURATION_TICKS + 1);
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, false, true);
    }

    @SuppressWarnings("unused")
    public HydraulicGrindstoneTurner(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock().getChunk()));
    }

    @Override
    public boolean checkFormed() {
        return BlockStorage.get(getBlock().getRelative(BlockFace.UP)) instanceof Grindstone;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return otherBlock == getBlock().getRelative(BlockFace.UP);
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
            return;
        }

        Grindstone grindstone = BlockStorage.getAs(Grindstone.class, getBlock().getRelative(BlockFace.UP));
        Preconditions.checkState(grindstone != null);

        if (grindstone.isRecipeInProgress()) {
            return;
        }

        GrindstoneRecipe nextRecipe = grindstone.getNextRecipe();
        if (nextRecipe == null) {
            return;
        }

        double hydraulicFluidUsed = HYDRAULIC_FLUID_USAGE * nextRecipe.timeTicks() / 20.0;

        if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidUsed
                || fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < hydraulicFluidUsed
                || !grindstone.tryStartRecipe(nextRecipe, null)
        ) {
            return;
        }

        removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidUsed);
        addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidUsed);
    }
}
