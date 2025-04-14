package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.function.Function;


@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class MonsterJerky extends PylonItemSchema {

    public MonsterJerky(NamespacedKey key, Class<? extends SimplePylonItem> itemClass, Function<NamespacedKey, ItemStack> template) {
        super(key, itemClass, template);

        int nutrition = getSettings().getOrThrow("nutrition", Integer.class);
        float saturation = getSettings().getOrThrow("saturation", Double.class).floatValue();
        this.template.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .canAlwaysEat(false)
                .nutrition(nutrition)
                .saturation(saturation)
                .build());
    }

    @Override
    public void onRegister(@NotNull PylonRegistry<?> registry) {
        float xp = getSettings().getOrThrow("cooking.xp", Double.class).floatValue();

        int furnaceCookingTime = getSettings().getOrThrow("cooking.time.furnace", Integer.class);
        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                new NamespacedKey(PylonBase.getInstance(), "monster_jerky_furnace"),
                template,
                Material.ROTTEN_FLESH,
                xp,
                furnaceCookingTime
        );
        furnaceRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_FURNACE.addRecipe(furnaceRecipe);

        int smokerCookingTime = getSettings().getOrThrow("cooking.time.smoker", Integer.class);
        SmokingRecipe smokingRecipe = new SmokingRecipe(
                new NamespacedKey(PylonBase.getInstance(), "monster_jerky_smoker"),
                template,
                Material.ROTTEN_FLESH,
                xp,
                smokerCookingTime
        );
        smokingRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_SMOKING.addRecipe(smokingRecipe);

        ShapedRecipe leatherRecipe = new ShapedRecipe(
                new NamespacedKey(PylonBase.getInstance(), "leather"),
                new ItemStack(Material.LEATHER)
        );
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
