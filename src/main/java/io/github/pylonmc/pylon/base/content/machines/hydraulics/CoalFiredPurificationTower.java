package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.machines.diesel.production.Biorefinery;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.ProgressItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
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

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class CoalFiredPurificationTower extends PylonBlock implements
        PylonFluidBufferBlock,
        PylonSimpleMultiblock,
        PylonDirectionalBlock,
        PylonProcessor,
        PylonGuiBlock,
        PylonTickingBlock {

    public final double purificationSpeed = getSettings().getOrThrow("purification-speed", ConfigAdapter.INT);
    public final double purificationEfficiency = getSettings().getOrThrow("purification-efficiency", ConfigAdapter.DOUBLE);
    public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.INT);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    public static final NamespacedKey FUELS_KEY = baseKey("coal_fired_purification_tower_fuels");
    public static final PylonRegistry<Fuel> FUELS = new PylonRegistry<>(FUELS_KEY);

    // TODO display fuels
    public record Fuel(
            @NotNull NamespacedKey key,
            @NotNull ItemStack stack,
            int burnTimeSeconds
    ) implements Keyed {
        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }
    }

    static {
        PylonRegistry.addRegistry(FUELS);
        FUELS.register(new Fuel(
                baseKey("coal"),
                new ItemStack(Material.COAL),
                15
        ));
        FUELS.register(new Fuel(
                baseKey("coal_block"),
                new ItemStack(Material.COAL_BLOCK),
                135
        ));
        FUELS.register(new Fuel(
                baseKey("charcoal"),
                new ItemStack(Material.CHARCOAL),
                10
        ));
        FUELS.register(new Fuel(
                baseKey("charcoal_block"),
                BaseItems.CHARCOAL_BLOCK,
                90
        ));
    }

    private final ItemStackBuilder idleProgressItem = ItemStackBuilder.of(Material.BLAZE_POWDER)
            .name(Component.translatable("pylon.pylonbase.item.coal_fired_purification_tower.progress_item.idle"));
    private final ItemStackBuilder runningProgressItem = ItemStackBuilder.of(Material.BLAZE_POWDER)
            .name(Component.translatable("pylon.pylonbase.item.coal_fired_purification_tower.progress_item.running"));

    private final VirtualInventory inventory = new VirtualInventory(1);

    public static class Item extends PylonItem {

        public final double purificationSpeed = getSettings().getOrThrow("purification-speed", ConfigAdapter.INT);
        public final double purificationEfficiency = getSettings().getOrThrow("purification-efficiency", ConfigAdapter.DOUBLE);
        public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("purification_speed", UnitFormat.MILLIBUCKETS_PER_SECOND.format(purificationSpeed)),
                    PylonArgument.of("purification_efficiency", UnitFormat.PERCENT.format(purificationEfficiency * 100)),
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(buffer))
            );
        }
    }

    @SuppressWarnings("unused")
    public CoalFiredPurificationTower(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(tickInterval);
        setFacing(context.getFacing());
        setProcessProgressItem(new ProgressItem(GuiItems.background()));
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, buffer, true, false);
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, buffer, false, true);
    }

    @SuppressWarnings("unused")
    public CoalFiredPurificationTower(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
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
                .addIngredient('f', getProcessProgressItem())
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

        progressProcess(tickInterval);

        if (!isProcessing()) {
            ItemStack item = inventory.getUnsafeItem(0);
            for (Fuel fuel : FUELS) {
                if (item == null || !PylonUtils.isPylonSimilar(item, fuel.stack)) {
                    continue;
                }

                inventory.setItemAmount(null, 0, item.getAmount() - 1);
                getProcessProgressItem().setItemStackBuilder(runningProgressItem);
                startProcess(fuel.burnTimeSeconds * 20);
                break;
            }
        }

        if (!isRunning()) {
            return;
        }

        double toPurify = Math.min(
                // maximum amount of dirty hydraulic fluid that can be purified this tick
                purificationSpeed * getTickInterval() / 20.0,
                Math.min(
                        // amount of dirty hydraulic fluid available
                        fluidAmount(BaseFluids.DIRTY_HYDRAULIC_FLUID),
                        // how much dirty hydraulic fluid can be converted without overflowing the hydraulic fluid buffer
                        fluidSpaceRemaining(BaseFluids.HYDRAULIC_FLUID) / purificationEfficiency
                )
        );

        removeFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, toPurify);
        addFluid(BaseFluids.HYDRAULIC_FLUID, toPurify * purificationEfficiency);
    }

    @Override
    public void onProcessFinished() {
        getProcessProgressItem().setItemStackBuilder(idleProgressItem);
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("input-bar", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.DIRTY_HYDRAULIC_FLUID),
                        fluidCapacity(BaseFluids.DIRTY_HYDRAULIC_FLUID),
                        20,
                        TextColor.fromHexString("#48459b")
                )),
                PylonArgument.of("output-bar", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.HYDRAULIC_FLUID),
                        fluidCapacity(BaseFluids.HYDRAULIC_FLUID),
                        20,
                        TextColor.fromHexString("#212d99")
                ))
        ));
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
