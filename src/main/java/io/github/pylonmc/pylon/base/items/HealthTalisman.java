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

public class HealthTalisman extends PylonItemSchema {
    private static boolean recipesRegistered = false;
    public int healthAmount;

    public HealthTalisman(NamespacedKey id, Class<? extends PylonItem<? extends HealthTalisman>> itemClass
            , ItemStack template, int healthAmount) {
        super(id, itemClass, template);
        this.healthAmount = healthAmount;
        assert template.getMaxStackSize() == 1;
        if(!recipesRegistered){
            ShapedRecipe simpleRecipe = new ShapedRecipe(new NamespacedKey(PylonBase.getInstance(), "simple_healing_talisman"), PylonItems.SIMPLE_HEALTH_TALISMAN.getItemStack());
            simpleRecipe.shape(
                    "MMM",
                    "MRM",
                    "MMM"
            );
            simpleRecipe.setIngredient('M', Material.GLISTERING_MELON_SLICE);
            simpleRecipe.setIngredient('R', Material.REDSTONE);
            simpleRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
            RecipeTypes.VANILLA_CRAFTING.addRecipe(simpleRecipe);
            ShapedRecipe advancedRecipe = new ShapedRecipe(new NamespacedKey(PylonBase.getInstance(), "advanced_healing_talisman"), PylonItems.ADVANCED_HEALTH_TALISMAN.getItemStack());
            advancedRecipe.shape(
                    "SSS",
                    "SSS",
                    "SSS"
            );
            advancedRecipe.setIngredient('S', PylonItems.SIMPLE_HEALTH_TALISMAN.getItemStack());
            advancedRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
            RecipeTypes.VANILLA_CRAFTING.addRecipe(advancedRecipe);
            ShapedRecipe ultimateRecipe = new ShapedRecipe(new NamespacedKey(PylonBase.getInstance(), "ultimate_healing_talisman"), PylonItems.ULTIMATE_HEALTH_TALISMAN.getItemStack());
            ultimateRecipe.shape(
                    "AAA",
                    "AAA",
                    "AAA"
            );
            ultimateRecipe.setIngredient('A', PylonItems.ADVANCED_HEALTH_TALISMAN.getItemStack());
            ultimateRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
            RecipeTypes.VANILLA_CRAFTING.addRecipe(ultimateRecipe);
            recipesRegistered = true;
        }
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

    public static interface HealthTalismanItem {
        public int getHealthIncrease();
    }
}
