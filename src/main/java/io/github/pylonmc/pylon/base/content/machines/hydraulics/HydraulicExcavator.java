package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class HydraulicExcavator extends PylonBlock implements
        PylonTickingBlock,
        PylonDirectionalBlock,
        PylonInteractBlock,
        PylonFluidBufferBlock {

    public static final NamespacedKey WORKING_KEY = baseKey("working");
    public static final NamespacedKey INDEX_KEY = baseKey("index");

    public final int radius = getSettings().getOrThrow("radius", ConfigAdapter.INT);
    public final int depth = getSettings().getOrThrow("depth", ConfigAdapter.INT);
    public final int blockBreakIntervalTicks = getSettings().getOrThrow("block-break-interval-ticks", ConfigAdapter.INT);
    public final int hydraulicFluidUsage = getSettings().getOrThrow("hydraulic-fluid-usage", ConfigAdapter.INT);
    public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);

    private final List<BlockPosition> blockPositions = new ArrayList<>();
    private boolean working;
    private int index;

    public static class Item extends PylonItem {

        public final int radius = getSettings().getOrThrow("radius", ConfigAdapter.INT);
        public final int depth = getSettings().getOrThrow("depth", ConfigAdapter.INT);
        public final int blockBreakIntervalTicks = getSettings().getOrThrow("block-break-interval-ticks", ConfigAdapter.INT);
        public final int hydraulicFluidUsage = getSettings().getOrThrow("hydraulic-fluid-usage", ConfigAdapter.INT);
        public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("radius", UnitFormat.BLOCKS.format(radius)),
                    PylonArgument.of("depth", UnitFormat.BLOCKS.format(depth)),
                    PylonArgument.of("time-per-block", UnitFormat.SECONDS.format(blockBreakIntervalTicks / 20.0)),
                    PylonArgument.of("hydraulic-fluid-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(hydraulicFluidUsage)),
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(buffer))
            );
        }
    }

    private void initBlockPositions() {
        for (int y = 0; y >= -depth; y--) {
            for (int x = -radius; x <= radius; x++) {
                for (int z = -radius; z <= radius; z++) {
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

        setTickInterval(blockBreakIntervalTicks);
        setFacing(context.getFacing());

        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);

        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, buffer, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, buffer, false, true);
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
    public void tick() {
        if (!working) {
            return;
        }

        if (index >= blockPositions.size()) {
            working = false;
            index = 0;
            return;
        }

        double hydraulicFluidUsed = hydraulicFluidUsage * getTickInterval() / 20.0;

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

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("input-bar", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.HYDRAULIC_FLUID),
                        fluidCapacity(BaseFluids.HYDRAULIC_FLUID),
                        20,
                        TextColor.fromHexString("#212d99")
                )),
                PylonArgument.of("output-bar", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.DIRTY_HYDRAULIC_FLUID),
                        fluidCapacity(BaseFluids.DIRTY_HYDRAULIC_FLUID),
                        20,
                        TextColor.fromHexString("#48459b")
                ))
        ));
    }
}
