package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.util.FoodProperties;
import io.github.pylonmc.pylon.base.util.MiningLevel;
import io.github.pylonmc.pylon.core.item.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class MonsterJerky extends PylonItemSchema {
    private final FoodProperties foodProperties;
    public final static int experienceGain = 1;
    public final static int cookingTime = 3 * 20; // 3 secs
    public final static int defaultNutrition = 3;
    public final static float defaultSaturation = 1.25f;
    public final static boolean defaultCanAlwaysEat = false;

    public MonsterJerky(NamespacedKey id, Class<? extends PylonItem<? extends PylonItemSchema>> itemClass,
                        ItemStack template, FoodProperties foodProperties){
        super(id, itemClass, template);
        this.foodProperties = foodProperties;
        FurnaceRecipe recipe = new FurnaceRecipe(id, template, Material.ROTTEN_FLESH, experienceGain, cookingTime);
        recipe.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_FURNACE.addRecipe(recipe);
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

    public static class Item extends PylonItem<MonsterJerky> implements FoodComponent {
        public Item(MonsterJerky schema, ItemStack itemStack) { super(schema, itemStack);}

        @Override
        public int getNutrition() {
            return getSchema().foodProperties.nutrition;
        }

        @Override
        public void setNutrition(int newNutrition) {
            getSchema().foodProperties.nutrition = newNutrition;
        }

        @Override
        public float getSaturation(){
            return getSchema().foodProperties.saturation;
        }

        @Override
        public void setSaturation(float newSaturation){
            getSchema().foodProperties.saturation = newSaturation;
        }

        @Override
        public boolean canAlwaysEat(){
            return getSchema().foodProperties.canAlwaysEat;
        }

        @Override
        public void setCanAlwaysEat(boolean newCanAlwaysEat){
            getSchema().foodProperties.canAlwaysEat = newCanAlwaysEat;
        }

        @Override
        public @NotNull Map<String, Object> serialize() {
            throw new NotImplementedException();
        }
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
