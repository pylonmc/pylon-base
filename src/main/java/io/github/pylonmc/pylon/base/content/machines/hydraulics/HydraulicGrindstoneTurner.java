package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.machines.hydraulics.base.SimpleHydraulicMachine;
import io.github.pylonmc.pylon.base.content.machines.simple.Grindstone;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;


public class HydraulicGrindstoneTurner extends SimpleHydraulicMachine implements PylonMultiblock, PylonTickingBlock {
    public static final Component MISSING_GRINDSTONE = Component.translatable("pylon.pylonbase.message.hydraulic_status.missing_grindstone");

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
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("hydraulic_fluid_input", UnitFormat.MILLIBUCKETS_PER_SECOND.format(HYDRAULIC_FLUID_INPUT_MB_PER_SECOND)),
                    PylonArgument.of("dirty_hydraulic_fluid_output", UnitFormat.MILLIBUCKETS_PER_SECOND.format(DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND))
            );
        }
    }

    @Getter private Component status = IDLE;

    @SuppressWarnings("unused")
    public HydraulicGrindstoneTurner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public HydraulicGrindstoneTurner(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public double getHydraulicFluidBuffer() {
        return HYDRAULIC_FLUID_BUFFER;
    }

    @Override
    public double getDirtyHydraulicFluidBuffer() {
        return DIRTY_HYDRAULIC_FLUID_BUFFER;
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
    public int getCustomTickRate(int globalTickRate) {
        return Grindstone.TICK_RATE;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
            status = MISSING_GRINDSTONE;
            return;
        }

        Grindstone grindstone = BlockStorage.getAs(Grindstone.class, getBlock().getRelative(BlockFace.UP));
        Preconditions.checkState(grindstone != null);

        if (grindstone.getRecipe() != null) {
            status = WORKING;
            return;
        }

        Grindstone.Recipe nextRecipe = grindstone.getNextRecipe();
        if (nextRecipe == null) {
            status = IDLE;
            return;
        }

        double inputFluidAmount = nextRecipe.timeTicks() * HYDRAULIC_FLUID_INPUT_MB_PER_SECOND / 20.0;
        double outputFluidAmount = nextRecipe.timeTicks() * DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND / 20.0;

        if (hydraulicFluidAmount < inputFluidAmount) {
            status = NOT_ENOUGH_HYDRAULIC_FLUID;
            return;
        }

        if (getRemainingDirtyCapacity() < outputFluidAmount) {
            status = DIRTY_HYDRAULIC_FLUID_BUFFER_FULL;
            return;
        }

        if (!grindstone.tryStartRecipe(nextRecipe, null)) {
            status = IDLE;
            return;
        }

        startCraft(inputFluidAmount, outputFluidAmount);
        status = WORKING;
    }
}
