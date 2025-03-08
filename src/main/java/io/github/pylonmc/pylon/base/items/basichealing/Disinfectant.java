package io.github.pylonmc.pylon.base.items.basichealing;

import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

public class Disinfectant extends PylonItemSchema {
    public Disinfectant(NamespacedKey id, Class<? extends SimplePylonItem> itemClass, ItemStack template){
        super(id, itemClass, template);
        ShapedRecipe craftingRecipe = new ShapedRecipe(id, template);
        craftingRecipe.shape(
                "DDD",
                "D D",
                "DDD"
        );
        craftingRecipe.setIngredient('D', Material.DRIPSTONE_BLOCK);
        craftingRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(craftingRecipe);
    }
}
