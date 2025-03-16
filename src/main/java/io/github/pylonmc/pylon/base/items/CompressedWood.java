package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.List;

public class CompressedWood extends PylonItemSchema {
    private static final List<Material> WOODS = List.of(Material.OAK_WOOD, Material.BIRCH_WOOD, Material.SPRUCE_WOOD, Material.JUNGLE_WOOD, Material.ACACIA_WOOD, Material.DARK_OAK_WOOD, Material.MANGROVE_WOOD, Material.CRIMSON_STEM);
    public CompressedWood(NamespacedKey id, Class<? extends SimplePylonItem> itemClass, ItemStack template){
        super(id, itemClass, template);
        for(var i = 0; i < WOODS.size(); i++){
            var recipe = new ShapedRecipe(new NamespacedKey(PylonBase.getInstance(), "compressed_wood_" + WOODS.get(i).toString()), template);
            recipe.shape(
                    "WWW",
                    "WWW",
                    "WWW"
            );
            recipe.setIngredient('W', WOODS.get(i));
            recipe.setCategory(CraftingBookCategory.MISC);
            RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
        }
    }
}
