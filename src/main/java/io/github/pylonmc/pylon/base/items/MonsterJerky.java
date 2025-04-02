package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNullByDefault;


@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class MonsterJerky extends PylonItemSchema {
    public static final int EXPERIENCE_GAIN = 1;
    public static final int COOKING_TIME = 3 * 20; // 3 secs
    public static final int COOKING_TIME_SMOKER = 2 * 20; // 2 secs

    public MonsterJerky(NamespacedKey key, Class<? extends SimplePylonItem> itemClass, ItemStack template) {
        super(key, itemClass, template);

        int nutrition = getSettings().getOrThrow("nutrition", Integer.class);
        float saturation = getSettings().getOrThrow("saturation", Float.class);
        template.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .canAlwaysEat(false)
                .nutrition(nutrition)
                .saturation(saturation)
                .build());

        FurnaceRecipe recipe = new FurnaceRecipe(new NamespacedKey(PylonBase.getInstance(), "monster_jerky_furnace"), template, Material.ROTTEN_FLESH, EXPERIENCE_GAIN, COOKING_TIME);
        recipe.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_FURNACE.addRecipe(recipe);
        SmokingRecipe recipeSmoking = new SmokingRecipe(new NamespacedKey(PylonBase.getInstance(), "monster_jerky_smoker"), template, Material.ROTTEN_FLESH, EXPERIENCE_GAIN, COOKING_TIME_SMOKER);
        recipeSmoking.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_SMOKING.addRecipe(recipeSmoking);
        ShapedRecipe leatherRecipe = new ShapedRecipe(new NamespacedKey(PylonBase.getInstance(), "leather"), new ItemStackBuilder(Material.LEATHER).build());
        leatherRecipe.shape(
                "RR ",
                "RR ",
                "   "
        );
        leatherRecipe.setIngredient('R', template);
        leatherRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(leatherRecipe);
    }
}
