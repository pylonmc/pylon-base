package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.SmokingRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class MonsterJerky extends PylonItemSchema {
    public static final int EXPERIENCE_GAIN = 1;
    public static final int COOKING_TIME = 3 * 20; // 3 secs
    public static final int COOKING_TIME_SMOKER = 2 * 20; // 2 secs
    public static final int DEFAULT_NUTRITION = 3;
    public static final float DEFAULT_SATURATION = 1.25f;
    public static final boolean DEFAULT_CAN_ALWAYS_EAT = false;

    public MonsterJerky(NamespacedKey id, Class<? extends PylonItem<? extends PylonItemSchema>> itemClass,
                        ItemStack template){
        super(id, itemClass, template);
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

    public static class Item extends PylonItem<MonsterJerky> {
        public Item(MonsterJerky schema, ItemStack itemStack) { super(schema, itemStack);}
    }

    public record Recipe(
            NamespacedKey key,
            List<ItemStack> ingredients,
            ItemStack result,
            MiningLevel level,
            float chance
    ) implements Keyed {
        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }

    public static final RecipeType<MonsterJerky.Recipe> RECIPE_TYPE = new RecipeType<>(
            new NamespacedKey(PylonBase.getInstance(), "monster_jerky")
    );

    static {
        PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
    }
}
