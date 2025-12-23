package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class HydraulicMiner extends PylonBlock implements
        PylonFluidBufferBlock,
        PylonDirectionalBlock,
        PylonTickingBlock,
        PylonMultiblock,
        PylonProcessor {

    public final double hydraulicFluidUsage = getSettings().getOrThrow("hydraulic-fluid-usage", ConfigAdapter.DOUBLE);
    public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final int ticksPerBlock = getSettings().getOrThrow("ticks-per-block", ConfigAdapter.INT);
    public final List<Material> blocks = getSettings().getOrThrow("blocks", ConfigAdapter.LIST.from(ConfigAdapter.MATERIAL));
    public final Material topMaterial = getSettings().getOrThrow("top-material", ConfigAdapter.MATERIAL);

    public static class Item extends PylonItem {

        public final double hydraulicFluidUsage = getSettings().getOrThrow("hydraulic-fluid-usage", ConfigAdapter.DOUBLE);
        public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);
        public final int ticksPerBlock = getSettings().getOrThrow("ticks-per-block", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("hydraulic-fluid-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(hydraulicFluidUsage)),
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(buffer)),
                    PylonArgument.of("time-per-block", UnitFormat.SECONDS.format(ticksPerBlock / 20.0))
            );
        }
    }

    public ItemStackBuilder topStack = ItemStackBuilder.of(topMaterial)
            .addCustomModelDataString(getKey() + ":top");
    public ItemStackBuilder drillStack = ItemStackBuilder.of(Material.GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":drill");

    @SuppressWarnings("unused")
    public HydraulicMiner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(tickInterval);
        createFluidPoint(FluidPointType.INPUT, BlockFace.EAST, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.WEST, context, false);
        setFacing(context.getFacing().getOppositeFace());
        addEntity("top", new ItemDisplayBuilder()
                .itemStack(topStack)
                .transformation(new TransformBuilder()
                        .scale(0.7, 0.2, 0.7))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("drill", new ItemDisplayBuilder()
                .itemStack(drillStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, -0.5, 0.5)
                        .scale(0.6, 0.6, 0.2)
                        .rotate(0, 0, Math.PI / 4))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, buffer, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, buffer, false, true);
    }

    @SuppressWarnings("unused")
    public HydraulicMiner(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void postInitialise() {
        tryStartDrilling();
    }

    @Override
    public void tick() {
        double hydraulicFluidUsed = hydraulicFluidUsage * tickInterval / 20;
        if (!isProcessing()
                || fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidUsed
                || fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < hydraulicFluidUsed
        ) {
            return;
        }

        Block drilling = getBlock().getRelative(getFacing());
        new ParticleBuilder(Particle.BLOCK)
                .count(5)
                .location(getBlock().getLocation().toCenterLocation().add(0, 0.6, 0))
                .data(drilling.getBlockData())
                .spawn();

        removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidUsed);
        addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidUsed);
        progressProcess(tickInterval);
    }

    public void tryStartDrilling() {
        if (isProcessing()) {
            return;
        }

        Block toDrill = getBlock().getRelative(getFacing());
        if (BlockStorage.isPylonBlock(toDrill) || !blocks.contains(toDrill.getType())) {
            return;
        }

        startProcess(ticksPerBlock);
    }

    @Override
    public void onProcessFinished() {
        Block toDrill = getBlock().getRelative(getFacing());
        if (BlockStorage.isPylonBlock(toDrill)
                || !blocks.contains(toDrill.getType())
                || !new BlockBreakBlockEvent(toDrill, getBlock(), new ArrayList<>()).callEvent()
        ) {
            return;
        }

        toDrill.breakNaturally();
    }

    @Override
    public @NotNull Set<ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock().getRelative(getFacing()).getChunk()));
    }

    @Override
    public boolean checkFormed() {
        return true;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return getBlock().getRelative(getFacing()).equals(otherBlock);
    }

    @Override
    public void onMultiblockRefreshed() {
        Block toDrill = getBlock().getRelative(getFacing());
        if (isProcessing() && (BlockStorage.isPylonBlock(toDrill) || !blocks.contains(toDrill.getType()))) {
            stopProcess();
            return;
        }
        tryStartDrilling();
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
