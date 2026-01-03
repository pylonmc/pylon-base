package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class HydraulicMiner extends Miner implements
        PylonTickingBlock,
        PylonDirectionalBlock,
        PylonInteractBlock,
        PylonFluidBufferBlock {

    public static NamespacedKey TOOL_KEY = baseKey("tool");

    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final double speed = getSettings().getOrThrow("speed", ConfigAdapter.DOUBLE);
    public final int hydraulicFluidPerBlock = getSettings().getOrThrow("hydraulic-fluid-per-block", ConfigAdapter.INT);
    public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);

    public static class Item extends PylonItem {

        public final int radius = getSettings().getOrThrow("radius", ConfigAdapter.INT);
        public final double speed = getSettings().getOrThrow("speed", ConfigAdapter.DOUBLE);
        public final int hydraulicFluidPerBlock = getSettings().getOrThrow("hydraulic-fluid-per-block", ConfigAdapter.INT);
        public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            int diameter = 2 * radius + 1;
            return List.of(
                    PylonArgument.of("speed", UnitFormat.PERCENT.format(speed * 100.0)),
                    PylonArgument.of("mining-area", diameter + "x" + diameter + "x" + diameter),
                    PylonArgument.of("hydraulic-fluid-per-block", UnitFormat.MILLIBUCKETS.format(hydraulicFluidPerBlock)),
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(buffer))
            );
        }
    }

    public ItemStackBuilder topStack = ItemStackBuilder.of(Material.YELLOW_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":top");

    public @Nullable ItemStack tool;

    @SuppressWarnings("unused")
    public HydraulicMiner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setTickInterval(tickInterval);
        setFacing(context.getFacing());

        addEntity("top1", new ItemDisplayBuilder()
                .itemStack(topStack)
                .transformation(new TransformBuilder()
                        .scale(0.6, 0.199, 0.6))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("top2", new ItemDisplayBuilder()
                .itemStack(topStack)
                .transformation(new TransformBuilder()
                        .scale(0.6, 0.2, 0.6)
                        .rotate(0, Math.PI / 4, 0))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );

        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);

        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, buffer, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, buffer, false, true);

        tool = null;
    }

    @SuppressWarnings("unused")
    public HydraulicMiner(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        tool = pdc.get(TOOL_KEY, PylonSerializers.ITEM_STACK);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(TOOL_KEY, PylonSerializers.ITEM_STACK, tool);
    }

    @Override
    public void postInitialise() {
        super.postInitialise();
        updateMiner();
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) {
            return;
        }

        event.setCancelled(true);

        // drop old item
        if (tool != null) {
            getBlock().getWorld().dropItem(
                    getBlock().getLocation().toCenterLocation().add(0, 0.25, 0),
                    tool
            );
            tool = null;
            stopProcess();
            return;
        }

        // insert new item
        ItemStack newStack = event.getItem();
        if (newStack != null) {
            tool = newStack.clone();
            newStack.setAmount(0);
            updateMiner();
        }
    }

    @Override
    public void tick() {
        if (!isProcessing()
                || fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidPerBlock
                || fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < hydraulicFluidPerBlock
        ) {
            return;
        }

        progressProcess(tickInterval);
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

    @Override
    public void onProcessFinished() {
        Block block = blockPositions.get(index).getBlock();
        List<ItemStack> drops = block.getDrops().stream().toList();
        if (tool == null
                || !BaseUtils.shouldBreakBlockUsingTool(block, tool)
                || PylonMultiblock.loadedMultiblocksWithComponent(block).size() > 1
                || !new BlockBreakBlockEvent(block, getBlock(), drops).callEvent()
        ) {
            return;
        }

        block.breakNaturally();
        tool.setData(DataComponentTypes.DAMAGE, tool.getData(DataComponentTypes.DAMAGE) + 1);
        if (Objects.equals(tool.getData(DataComponentTypes.DAMAGE), tool.getData(DataComponentTypes.MAX_DAMAGE))) {
            tool = null;
        }
        removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidPerBlock);
        addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidPerBlock);
        updateMiner();
    }

    @Override
    protected @Nullable Integer getBreakTicks(@NotNull Block block) {
        if (tool == null
                || !BaseUtils.shouldBreakBlockUsingTool(block, tool)
                || PylonMultiblock.loadedMultiblocksWithComponent(block).size() > 1
        ) {
            return null;
        }
        return (int) Math.round(PylonUtils.getBlockBreakTicks(tool, block) / speed);
    }
}
