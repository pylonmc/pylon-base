package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.function.Function;

public class HealthTalisman extends PylonItemSchema {
    public int healthAmount;

    public HealthTalisman(NamespacedKey id,
                          Class<? extends PylonItem<? extends HealthTalisman>> itemClass,
                          ItemStack template,
                          int healthAmount,
                          Function<ItemStack, ShapedRecipe> recipeFunc) {
        super(id, itemClass, template);
        if(template.getMaxStackSize() != 1){
            throw new IllegalArgumentException("Max stack size for health talisman must be equal to 1");
        }
        this.healthAmount = healthAmount;
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

    public interface HealthTalismanItem {
        public int getHealthIncrease();
    }
}
