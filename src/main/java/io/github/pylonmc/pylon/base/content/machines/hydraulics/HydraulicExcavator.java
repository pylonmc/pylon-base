package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class HydraulicExcavator extends PylonBlock
        implements PylonTickingBlock, PylonInteractableBlock, PylonFluidBufferBlock, PylonEntityHolderBlock {

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("radius", UnitFormat.BLOCKS.format(RADIUS)),
                    PylonArgument.of("depth", UnitFormat.BLOCKS.format(DEPTH)),
                    PylonArgument.of("time-per-block", UnitFormat.SECONDS.format(BLOCK_BREAK_INTERVAL_TICKS / 20.0)),
                    PylonArgument.of("hydraulic-fluid-consumption", UnitFormat.MILLIBUCKETS_PER_SECOND.format(HYDRAULIC_FLUID_INPUT_MB_PER_SECOND)),
                    PylonArgument.of("dirty-hydraulic-fluid-output", UnitFormat.MILLIBUCKETS_PER_SECOND.format(DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND))
            );
        }
    }

    public static final NamespacedKey WORKING_KEY = baseKey("working");
    public static final NamespacedKey INDEX_KEY = baseKey("index");

    public static final Config settings = Settings.get(BaseKeys.HYDRAULIC_EXCAVATOR);
    public static final int RADIUS = settings.getOrThrow("radius", ConfigAdapter.INT);
    public static final int DEPTH = settings.getOrThrow("depth", ConfigAdapter.INT);
    public static final int BLOCK_BREAK_INTERVAL_TICKS = settings.getOrThrow("block-break-interval-ticks", ConfigAdapter.INT);
    public static final int HYDRAULIC_FLUID_INPUT_MB_PER_SECOND = settings.getOrThrow("hydraulic-fluid-input-mb-per-second", ConfigAdapter.INT);
    public static final int DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND = settings.getOrThrow("dirty-hydraulic-fluid-output-mb-per-second", ConfigAdapter.INT);
    public static final double HYDRAULIC_FLUID_BUFFER = settings.getOrThrow("hydraulic-fluid-buffer", ConfigAdapter.DOUBLE);
    public static final double DIRTY_HYDRAULIC_FLUID_BUFFER = settings.getOrThrow("dirty-hydraulic-fluid-buffer", ConfigAdapter.DOUBLE);

    private final List<BlockPosition> blockPositions = new ArrayList<>();
    private boolean working;
    private int index;

    private void initBlockPositions() {
        for (int y = 0; y >= -DEPTH; y--) {
            for (int x = -RADIUS; x <= RADIUS; x++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    blockPositions.add(new BlockPosition(getBlock().getRelative(x, y, z)));
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public HydraulicExcavator(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        initBlockPositions();
        working = false;
        index = 0;

        setTickInterval(BLOCK_BREAK_INTERVAL_TICKS);

        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));

        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, DIRTY_HYDRAULIC_FLUID_BUFFER, false, true);
    }

    @SuppressWarnings({"DataFlowIssue", "unused"})
    public HydraulicExcavator(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        initBlockPositions();
        working = pdc.get(WORKING_KEY, PylonSerializers.BOOLEAN);
        index = pdc.get(INDEX_KEY, PylonSerializers.INTEGER);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(WORKING_KEY, PylonSerializers.BOOLEAN, working);
        pdc.set(INDEX_KEY, PylonSerializers.INTEGER, index);
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!working) {
            return;
        }

        if (index >= blockPositions.size()) {
            working = false;
            index = 0;
            return;
        }

        double hydraulicFluidInput = HYDRAULIC_FLUID_INPUT_MB_PER_SECOND * deltaSeconds;
        double dirtyHydraulicFluidOutput = DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND * deltaSeconds;

        if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidInput
                || fluidCapacity(BaseFluids.DIRTY_HYDRAULIC_FLUID) < dirtyHydraulicFluidOutput
        ) {
            return;
        }

        while (index < blockPositions.size()) {
            BlockPosition position = blockPositions.get(index);
            index++;

            if (position.getChunk().isLoaded()) {
                Block block = position.getBlock();
                if (Tag.MINEABLE_SHOVEL.isTagged(block.getType())) {
                    block.breakNaturally();
                    break;
                }
            }
        }

        removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidInput);
        addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, dirtyHydraulicFluidOutput);
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        working = true;
    }
}
