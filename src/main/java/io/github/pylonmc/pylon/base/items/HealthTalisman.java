package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.*;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;

@SuppressWarnings("UnstableApiUsage")
public class HealthTalisman extends PylonItemSchema {
    public static final ItemStack SIMPLE_TALISMAN_STACK = new ItemStackBuilder(Material.AMETHYST_SHARD)
            .name("Simple Healing Talisman")
            .lore(new LoreBuilder()
                    .attributeLine("Max health increase", 6, Quantity.HEARTS)
                    .arrow().text("Passive effect while in inventory").newline()
                    .arrow().text("Does not stack"))
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    public static final ItemStack ADVANCED_TALISMAN_STACK = new ItemStackBuilder(Material.AMETHYST_CLUSTER)
            .name("Advanced Healing Talisman")
            .lore(new LoreBuilder()
                    .attributeLine("Max health increase", 6, Quantity.HEARTS)
                    .arrow().text("Passive effect while in inventory").newline()
                    .arrow().text("Does not stack"))
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    public static final ItemStack ULTIMATE_TALISMAN_STACK = new ItemStackBuilder(Material.BUDDING_AMETHYST)
            .name("Ultimate Healing Talisman")
            .lore(new LoreBuilder()
                    .attributeLine("Max health increase", 10, Quantity.HEARTS)
                    .arrow().text("Passive effect while in inventory").newline()
                    .arrow().text("Does not stack"))
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    private static boolean recipesRegistered = false;
    public int healthAmount;

    public HealthTalisman(NamespacedKey id, Class<? extends PylonItem<? extends HealthTalisman>> itemClass
            , ItemStack template, int healthAmount) {
        super(id, itemClass, template);
        this.healthAmount = healthAmount;
        assert template.getMaxStackSize() == 1;
        if(!recipesRegistered){
            ShapedRecipe simpleRecipe = new ShapedRecipe(new NamespacedKey(PylonBase.getInstance(), "simple_healing_talisman"), SIMPLE_TALISMAN_STACK);
            simpleRecipe.shape(
                    "MMM",
                    "MRM",
                    "MMM"
            );
            simpleRecipe.setIngredient('M', Material.GLISTERING_MELON_SLICE);
            simpleRecipe.setIngredient('R', Material.REDSTONE);
            simpleRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
            RecipeTypes.VANILLA_CRAFTING.addRecipe(simpleRecipe);
            ShapedRecipe advancedRecipe = new ShapedRecipe(new NamespacedKey(PylonBase.getInstance(), "advanced_healing_talisman"), ADVANCED_TALISMAN_STACK);
            advancedRecipe.shape(
                    "SSS",
                    "SSS",
                    "SSS"
            );
            advancedRecipe.setIngredient('S', SIMPLE_TALISMAN_STACK);
            advancedRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
            RecipeTypes.VANILLA_CRAFTING.addRecipe(advancedRecipe);
            ShapedRecipe ultimateRecipe = new ShapedRecipe(new NamespacedKey(PylonBase.getInstance(), "ultimate_healing_talisman"), ULTIMATE_TALISMAN_STACK);
            ultimateRecipe.shape(
                    "AAA",
                    "AAA",
                    "AAA"
            );
            ultimateRecipe.setIngredient('A', ADVANCED_TALISMAN_STACK);
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
