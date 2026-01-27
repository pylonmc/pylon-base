package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.recipes.TableSawRecipe;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.rebar.block.PylonBlock;
import io.github.pylonmc.rebar.block.base.*;
import io.github.pylonmc.rebar.block.context.BlockBreakContext;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.rebar.entity.display.transform.TransformBuilder;
import io.github.pylonmc.rebar.fluid.FluidPointType;
import io.github.pylonmc.rebar.i18n.PylonArgument;
import io.github.pylonmc.rebar.item.PylonItem;
import io.github.pylonmc.rebar.logistics.LogisticGroupType;
import io.github.pylonmc.rebar.logistics.slot.ItemDisplayLogisticSlot;
import io.github.pylonmc.rebar.util.PylonUtils;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import io.github.pylonmc.rebar.waila.WailaDisplay;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class HydraulicTableSaw extends PylonBlock implements
        PylonFluidBufferBlock,
        PylonInteractBlock,
        PylonTickingBlock,
        PylonDirectionalBlock,
        PylonLogisticBlock,
        PylonRecipeProcessor<TableSawRecipe>{

    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final int hydraulicFluidUsage = getSettings().getOrThrow("hydraulic-fluid-usage", ConfigAdapter.INT);
    public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.INT);

    public final ItemStackBuilder sawItem = ItemStackBuilder.of(Material.IRON_BARS)
            .addCustomModelDataString(getKey() + ":saw");

    public static class Item extends PylonItem {

        public final int hydraulicFluidUsage = getSettings().getOrThrow("hydraulic-fluid-usage", ConfigAdapter.INT);
        public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("hydraulic-fluid-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(hydraulicFluidUsage)),
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(buffer))
            );
        }
    }

    @SuppressWarnings("unused")
    public HydraulicTableSaw(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(tickInterval);
        setFacing(context.getFacing());
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);
        addEntity("item", new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .scale(0.3))
                .build(block.getLocation().toCenterLocation().add(0, 0.65, 0))
        );
        addEntity("saw", new ItemDisplayBuilder()
                .itemStack(sawItem)
                .transformation(new TransformBuilder()
                        .scale(0.6, 0.4, 0.4))
                .build(block.getLocation().toCenterLocation().add(0, 0.7, 0))
        );
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, buffer, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, buffer, false, true);
        setRecipeType(TableSawRecipe.RECIPE_TYPE);
    }

    @SuppressWarnings("unused")
    public HydraulicTableSaw(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("input", LogisticGroupType.INPUT, new ItemDisplayLogisticSlot(getItemDisplay()));
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

        ItemDisplay itemDisplay = getItemDisplay();
        ItemStack oldStack = itemDisplay.getItemStack();
        ItemStack newStack = event.getItem();

        // drop old item
        if (!oldStack.getType().isAir()) {
            getBlock().getWorld().dropItem(
                    getBlock().getLocation().toCenterLocation().add(0, 0.75, 0),
                    oldStack
            );
            itemDisplay.setItemStack(null);
            stopRecipe();
            return;
        }

        // insert new item
        if (newStack != null) {
            ItemStack stackToInsert = newStack.clone();
            itemDisplay.setItemStack(stackToInsert);
            newStack.setAmount(0);
        }
    }

    @Override
    public void tick() {
        double hydraulicFluidToConsume = hydraulicFluidUsage * getTickInterval() / 20.0;
        if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidToConsume
                || fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < hydraulicFluidToConsume) {
            return;
        }

        if (isProcessingRecipe()) {
            new ParticleBuilder(Particle.BLOCK)
                    .count(5)
                    .location(getBlock().getLocation().toCenterLocation().add(0, 0.75, 0))
                    .data(getCurrentRecipe().particleData())
                    .spawn();
            removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidToConsume);
            addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidToConsume);
            progressRecipe(tickInterval);
            return;
        }

        ItemStack stack = getItemDisplay().getItemStack();
        for (TableSawRecipe recipe : TableSawRecipe.RECIPE_TYPE) {
            if (!stack.isSimilar(recipe.input()) || stack.getAmount() < recipe.input().getAmount()) {
                continue;
            }

            startRecipe(recipe, recipe.timeTicks());
            break;
        }
    }

    public ItemDisplay getItemDisplay() {
        return getHeldEntityOrThrow(ItemDisplay.class, "item");
    }

    @Override
    public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonFluidBufferBlock.super.onBreak(drops, context);
        drops.add(getItemDisplay().getItemStack());
    }

    @Override
    public void onRecipeFinished(@NotNull TableSawRecipe recipe) {
        getItemDisplay().setItemStack(getItemDisplay().getItemStack().subtract(recipe.input().getAmount()));
        getBlock().getWorld().dropItemNaturally(
                getBlock().getLocation().toCenterLocation().add(0, 0.75, 0),
                recipe.result()
        );
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
