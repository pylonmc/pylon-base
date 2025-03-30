package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.item.*;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

import java.util.function.Function;

public class HealthTalisman extends PylonItemSchema {
    private static boolean recipesRegistered = false;
    public int healthAmount;

    public HealthTalisman(NamespacedKey id, Class<? extends PylonItem<? extends HealthTalisman>> itemClass
            , ItemStack template, int healthAmount, Function<ItemStack, ShapedRecipe> recipeFunc) {
        super(id, itemClass, template);
        this.healthAmount = healthAmount;
        assert template.getMaxStackSize() == 1;
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipeFunc.apply(template));
    }

    public static class Item extends PylonItem<HealthTalisman> implements HealthTalismanItem {
        HealthTalisman schema;

        public Item(HealthTalisman schema, ItemStack itemStack) {
            super(schema, itemStack);
            this.schema = schema;
        }

        public int getHealthIncrease(){
            return schema.healthAmount;
        }
    }
}
