package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.base.recipes.MoldingRecipe;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.Keyed;
import org.bukkit.inventory.ItemStack;


public interface Moldable extends Keyed {
    void doMoldingClick();
    boolean isMoldingFinished();

    default ItemStack moldingInputStack() {
        return PylonRegistry.ITEMS.getOrThrow(getKey()).getItemStack();
    }

    default ItemStack moldingResult() {
        for (MoldingRecipe recipe : MoldingRecipe.RECIPE_TYPE) {
            if (recipe.isInput(moldingInputStack())) {
                return recipe.result();
            }
        }
        throw new IllegalStateException("Moldable item " + getKey() + " does not have an associated molding recipe");
    }

    default int totalMoldingClicks() {
        for (MoldingRecipe recipe : MoldingRecipe.RECIPE_TYPE) {
            if (recipe.isInput(moldingInputStack())) {
                return recipe.moldingCycles();
            }
        }
        throw new IllegalStateException("Moldable item " + getKey() + " does not have an associated molding recipe");
    }
}
