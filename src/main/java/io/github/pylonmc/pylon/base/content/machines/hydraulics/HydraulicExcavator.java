package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
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


public class HydraulicExcavator extends PylonBlock implements PylonTickingBlock, PylonInteractBlock, PylonFluidBufferBlock {

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
                    PylonArgument.of("hydraulic-fluid-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(HYDRAULIC_FLUID_USAGE))
            );
        }
    }

    public static final NamespacedKey WORKING_KEY = baseKey("working");
    public static final NamespacedKey INDEX_KEY = baseKey("index");

    public static final Config settings = Settings.get(BaseKeys.HYDRAULIC_EXCAVATOR);
    public static final int RADIUS = settings.getOrThrow("radius", ConfigAdapter.INT);
    public static final int DEPTH = settings.getOrThrow("depth", ConfigAdapter.INT);
    public static final int BLOCK_BREAK_INTERVAL_TICKS = settings.getOrThrow("block-break-interval-ticks", ConfigAdapter.INT);
    public static final int HYDRAULIC_FLUID_USAGE = settings.getOrThrow("hydraulic-fluid-usage", ConfigAdapter.INT);
    public static final double HYDRAULIC_FLUID_BUFFER = settings.getOrThrow("hydraulic-fluid-buffer", ConfigAdapter.DOUBLE);

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

        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);

        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, false, true);
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

        double hydraulicFluidUsed = HYDRAULIC_FLUID_USAGE * getTickInterval() / 20.0;

        if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidUsed
                || fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < hydraulicFluidUsed
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
                    removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidUsed);
                    addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidUsed);
                    break;
                }
            }
        }
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        working = true;
    }
}
