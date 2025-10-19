package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.recipes.MeltingRecipe;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.base.PylonVanillaContainerBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class SmelteryHopper extends SmelteryComponent implements PylonTickingBlock, PylonVanillaContainerBlock, PylonBreakHandler {

    @SuppressWarnings("unused")
    public SmelteryHopper(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(SmelteryController.TICK_INTERVAL);
    }

    @SuppressWarnings("unused")
    public SmelteryHopper(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onItemMoveFrom(InventoryMoveItemEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        Hopper hopper = (Hopper) getBlock().getState();

        for (ItemStack item : hopper.getInventory()) {
            drops.add(item);
        }
    }

    @Override
    public void tick(double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller == null) return;
        Hopper hopper = (Hopper) getBlock().getState(false);
        for (ItemStack item : hopper.getInventory().getContents()) {
            if (item == null) continue;
            MeltingRecipe recipe = null;
            for (MeltingRecipe meltingRecipe : MeltingRecipe.RECIPE_TYPE) {
                if (meltingRecipe.input().contains(item)) {
                    if (!new PrePylonCraftEvent<>(MeltingRecipe.RECIPE_TYPE, meltingRecipe, controller).callEvent()) {
                        continue;
                    }
                    recipe = meltingRecipe;
                    break;
                }
            }
            if (recipe == null) continue;
            double fluidAmountAfterAdding = controller.getTotalFluid() + recipe.resultAmount();
            if (controller.getTemperature() >= recipe.temperature() && fluidAmountAfterAdding <= controller.getCapacity()) {
                controller.addFluid(recipe.result(), recipe.resultAmount());
                item.subtract();
                new PylonCraftEvent<>(MeltingRecipe.RECIPE_TYPE, recipe, controller).callEvent();
            }
        }
    }
}
