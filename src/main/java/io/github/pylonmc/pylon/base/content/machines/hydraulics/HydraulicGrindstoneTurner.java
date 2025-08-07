package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.machines.simple.Grindstone;
import io.github.pylonmc.pylon.base.recipes.GrindstoneRecipe;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;


public class HydraulicGrindstoneTurner extends PylonBlock
        implements PylonMultiblock, PylonTickingBlock, PylonEntityHolderBlock, PylonFluidBufferBlock {

    private static final Config settings = Settings.get(BaseKeys.HYDRAULIC_GRINDSTONE_TURNER);
    public static final int HYDRAULIC_FLUID_INPUT_MB_PER_SECOND = settings.getOrThrow("hydraulic-fluid-input-mb-per-second", Integer.class);
    public static final int DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND = settings.getOrThrow("dirty-hydraulic-fluid-output-mb-per-second", Integer.class);
    public static final double HYDRAULIC_FLUID_BUFFER = settings.getOrThrow("hydraulic-fluid-buffer", Integer.class);
    public static final double DIRTY_HYDRAULIC_FLUID_BUFFER = settings.getOrThrow("dirty-hydraulic-fluid-buffer", Integer.class);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "hydraulic_fluid_input", UnitFormat.MILLIBUCKETS_PER_SECOND.format(HYDRAULIC_FLUID_INPUT_MB_PER_SECOND),
                    "dirty_hydraulic_fluid_output", UnitFormat.MILLIBUCKETS_PER_SECOND.format(DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND)
            );
        }
    }

    @SuppressWarnings("unused")
    public HydraulicGrindstoneTurner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(Grindstone.TICK_RATE);
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, DIRTY_HYDRAULIC_FLUID_BUFFER, false, true);
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
        return otherBlock.getLocation().equals(getBlock().getLocation());
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
            return;
        }

        Grindstone grindstone = BlockStorage.getAs(Grindstone.class, getBlock().getRelative(BlockFace.UP));
        Preconditions.checkState(grindstone != null);

        if (grindstone.getRecipe() != null) {
            return;
        }

        GrindstoneRecipe nextRecipe = grindstone.getNextRecipe();
        if (nextRecipe == null) {
            return;
        }

        double inputFluidAmount = nextRecipe.timeTicks() * HYDRAULIC_FLUID_INPUT_MB_PER_SECOND / 20.0;
        double outputFluidAmount = nextRecipe.timeTicks() * DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND / 20.0;

        if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < inputFluidAmount
                || fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < outputFluidAmount
                || !grindstone.tryStartRecipe(nextRecipe, null)
        ) {
            return;
        }

        removeFluid(BaseFluids.HYDRAULIC_FLUID, inputFluidAmount);
        addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, outputFluidAmount);
    }
}
