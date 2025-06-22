package io.github.pylonmc.pylon.base.items.hydraulic;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.base.items.multiblocks.Grindstone;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import lombok.Getter;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class HydraulicGrindstoneTurner extends PylonBlock implements PylonFluidIoBlock, PylonMultiblock, PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("hydraulic_grindstone_turner");
    public static final NamespacedKey HYDRAULIC_FLUID_AMOUNT_KEY = pylonKey("hydraulic_fluid_amount");
    public static final NamespacedKey DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY = pylonKey("hydraulic_fluid_amount");

    public static final int HYDRAULIC_FLUID_INPUT_MB_PER_SECOND = Settings.get(KEY).getOrThrow("hydraulic-fluid-input-mb-per-second", Integer.class);
    public static final int DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND = Settings.get(KEY).getOrThrow("dirty-hydraulic-fluid-output-mb-per-second", Integer.class);

    public static final double HYDRAULIC_FLUID_BUFFER = Settings.get(KEY).getOrThrow("hydraulic-fluid-buffer", Integer.class);
    public static final double DIRTY_HYDRAULIC_FLUID_BUFFER = Settings.get(KEY).getOrThrow("dirty-hydraulic-fluid-buffer", Integer.class);

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

    @Getter private double hydraulicFluidAmount;
    @Getter private double dirtyHydraulicFluidAmount;

    @SuppressWarnings("unused")
    public HydraulicGrindstoneTurner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        hydraulicFluidAmount = 0.0;
    }

    @SuppressWarnings("unused")
    public HydraulicGrindstoneTurner(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        hydraulicFluidAmount = pdc.get(HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
        dirtyHydraulicFluidAmount = pdc.get(DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, hydraulicFluidAmount);
        pdc.set(DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, dirtyHydraulicFluidAmount);
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, BlockFace.NORTH),
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.OUTPUT, BlockFace.SOUTH)
        );
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(PylonFluids.DIRTY_HYDRAULIC_FLUID, dirtyHydraulicFluidAmount);
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        dirtyHydraulicFluidAmount -= amount;
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(PylonFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER - hydraulicFluidAmount);
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        hydraulicFluidAmount += amount;
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
            return;
        }

        Grindstone grindstone = BlockStorage.getAs(Grindstone.class, getBlock().getRelative(BlockFace.UP));
        Preconditions.checkState(grindstone != null);

        if (grindstone.getRecipe() != null) {
            return;
        }

        Grindstone.Recipe nextRecipe = grindstone.getNextRecipe();
        if (nextRecipe == null) {
            return;
        }

        // Check enough hydraulic fluid to finish the craft
        double hydraulicFluidRequired = nextRecipe.timeTicks() * HYDRAULIC_FLUID_INPUT_MB_PER_SECOND / 20.0;
        if (hydraulicFluidAmount < hydraulicFluidRequired) {
            return;
        }

        // Check enough space for dirty hydraulic fluid
        double dirtyHydraulicFluidOutput = nextRecipe.timeTicks() * DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND / 20.0;
        if (dirtyHydraulicFluidOutput > DIRTY_HYDRAULIC_FLUID_BUFFER - dirtyHydraulicFluidAmount) {
            return;
        }

        grindstone.tryStartRecipe(nextRecipe, null);

        hydraulicFluidAmount -= hydraulicFluidRequired;
        dirtyHydraulicFluidAmount += dirtyHydraulicFluidOutput;
    }
}
