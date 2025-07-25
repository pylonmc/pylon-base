package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public final class SmelteryHopper extends SmelteryComponent implements PylonTickingBlock {

    @SuppressWarnings("unused")
    public SmelteryHopper(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public SmelteryHopper(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return 2;
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
                if (meltingRecipe.input().isSimilar(item)) {
                    if (!new PrePylonCraftEvent<>(MeltingRecipe.RECIPE_TYPE, meltingRecipe, controller).callEvent()) {
                        continue;
                    }
                    recipe = meltingRecipe;
                    break;
                }
            }
            if (recipe == null) continue;
            if (controller.getTemperature() >= recipe.temperature()) {
                controller.addFluid(recipe.result(), CastingRecipe.CAST_AMOUNT);
                item.subtract();
                new PylonCraftEvent<>(MeltingRecipe.RECIPE_TYPE, recipe, controller).callEvent();
            }
        }
    }
}
