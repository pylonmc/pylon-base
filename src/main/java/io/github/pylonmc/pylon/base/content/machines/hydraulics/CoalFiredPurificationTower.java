package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonProcessor;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.ItemTypeWrapper;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.ProgressItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CoalFiredPurificationTower extends PylonBlock
        implements PylonFluidBufferBlock, PylonSimpleMultiblock, PylonProcessor, PylonGuiBlock, PylonTickingBlock {

    private static final Config settings = Settings.get(BaseKeys.COAL_FIRED_PURIFICATION_TOWER);
    public static final double PURIFICATION_SPEED = settings.getOrThrow("purification-speed", ConfigAdapter.INT);
    public static final double PURIFICATION_EFFICIENCY = settings.getOrThrow("purification-efficiency", ConfigAdapter.DOUBLE);
    public static final double HYDRAULIC_FLUID_BUFFER = settings.getOrThrow("hydraulic-fluid-buffer", ConfigAdapter.INT);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", ConfigAdapter.INT);
    public static final Map<ItemStack, Integer> FUELS = new HashMap<>();

    static {
        ConfigSection config = settings.getSectionOrThrow("fuels");
        for (String key : config.getKeys()) {
            FUELS.put(
                    ItemTypeWrapper.of(NamespacedKey.fromString(key)).createItemStack(),
                    config.getOrThrow(key, ConfigAdapter.INT)
            );
        }
    }

    private final ItemStackBuilder idleProgressItem = ItemStackBuilder.of(Material.BLAZE_POWDER)
            .name(Component.translatable("pylon.pylonbase.item.coal_fired_purification_tower.progress_item.idle"));
    private final ItemStackBuilder runningProgressItem = ItemStackBuilder.of(Material.BLAZE_POWDER)
            .name(Component.translatable("pylon.pylonbase.item.coal_fired_purification_tower.progress_item.running"));

    private final VirtualInventory inventory = new VirtualInventory(1);
    private final ProgressItem progressItem = new ProgressItem(idleProgressItem);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("purification_speed", UnitFormat.MILLIBUCKETS_PER_SECOND.format(PURIFICATION_SPEED)),
                    PylonArgument.of("purification_efficiency", UnitFormat.PERCENT.format(PURIFICATION_EFFICIENCY * 100))
            );
        }
    }

    @SuppressWarnings("unused")
    public CoalFiredPurificationTower(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(TICK_INTERVAL);
        createFluidPoint(FluidPointType.INPUT, BlockFace.EAST, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.WEST, context, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, true, false);
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, false, true);
    }

    @SuppressWarnings("unused")
    public CoalFiredPurificationTower(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void postInitialise() {
        setProcessProgressItem(progressItem);
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # f # # # #",
                        "# # # # x # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('f', progressItem)
                .addIngredient('x', inventory)
                .addIngredient('#', GuiItems.background())
                .build();
    }

    @Override
    public @NotNull Map<@NotNull Vector3i, @NotNull MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();

        components.put(new Vector3i(0, 1, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 2, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 3, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 4, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 5, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_GLASS));
        components.put(new Vector3i(0, 6, 0), new PylonMultiblockComponent(BaseKeys.PURIFICATION_TOWER_CAP));

        return components;
    }

    @Override
    public void tick() {
        if (!isFormedAndFullyLoaded()) {
            return;
        }

        progressProcess(TICK_INTERVAL);

        if (!isProcessing()) {
            ItemStack item = inventory.getUnsafeItem(0);
            for (Map.Entry<ItemStack, Integer> fuel : FUELS.entrySet()) {
                if (item == null || !PylonUtils.isPylonSimilar(item, fuel.getKey())) {
                    continue;
                }

                inventory.setItemAmount(null, 0, item.getAmount() - 1);
                progressItem.setItemStackBuilder(runningProgressItem);
                startProcess(fuel.getValue() * 20);
                break;
            }
        }

        if (!isRunning()) {
            return;
        }

        double toPurify = Math.min(
                // maximum amount of dirty hydraulic fluid that can be purified this tick
                PURIFICATION_SPEED * getTickInterval() / 20.0,
                Math.min(
                        // amount of dirty hydraulic fluid available
                        fluidAmount(BaseFluids.DIRTY_HYDRAULIC_FLUID),
                        // how much dirty hydraulic fluid can be converted without overflowing the hydraulic fluid buffer
                        fluidSpaceRemaining(BaseFluids.HYDRAULIC_FLUID) / PURIFICATION_EFFICIENCY
                )
        );

        removeFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, toPurify);
        addFluid(BaseFluids.HYDRAULIC_FLUID, toPurify * PURIFICATION_EFFICIENCY);
    }

    @Override
    public void onProcessFinished() {
        progressItem.setItemStackBuilder(idleProgressItem);
    }

    public boolean isRunning() {
        return isProcessing()
                // input buffer not empty
                && fluidAmount(BaseFluids.DIRTY_HYDRAULIC_FLUID) > 1.0e-3
                // output buffer not full
                && fluidSpaceRemaining(BaseFluids.HYDRAULIC_FLUID) > 1.0e-3;
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonFluidBufferBlock.super.onBreak(drops, context);
        PylonGuiBlock.super.onBreak(drops, context);
    }
}
