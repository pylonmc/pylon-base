package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public final class SmelteryHopper extends SmelteryComponent implements PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("smeltery_hopper");

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
            MeltRecipe recipe = null;
            for (MeltRecipe meltRecipe : MeltRecipe.RECIPE_TYPE) {
                if (meltRecipe.input().isSimilar(item)) {
                    if (!new PrePylonCraftEvent<>(MeltRecipe.RECIPE_TYPE, meltRecipe, controller).callEvent()) {
                        continue;
                    }
                    recipe = meltRecipe;
                    break;
                }
            }
            if (recipe == null) continue;
            if (controller.getTemperature() >= recipe.temperature()) {
                controller.addFluid(recipe.result(), CastRecipe.CAST_AMOUNT);
                item.subtract();
                new PylonCraftEvent<>(MeltRecipe.RECIPE_TYPE, recipe, controller).callEvent();
            }
        }
    }
}
