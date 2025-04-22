package io.github.pylonmc.pylon.base.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Arrays;
import java.util.Map;

public final class RecipeUtils {

    public static final int DEFAULT_FURNACE_TIME_TICKS = 20 * 10;
    public static final int DEFAULT_SMOKER_TIME_TICKS = 20 * 5;
    public static final int DEFAULT_BLAST_FURNACE_TIME_TICKS = 20 * 5;

    private RecipeUtils() {
        throw new AssertionError("Utility class");
    }

    public static ShapedRecipe reflect(ShapedRecipe recipe) {
        NamespacedKey key = recipe.getKey();
        key = new NamespacedKey(key.getNamespace(), key.getKey() + "_reflected");
        ShapedRecipe reflected = new ShapedRecipe(key, recipe.getResult());
        reflected.setGroup(recipe.getGroup());
        reflected.setCategory(recipe.getCategory());
        String[] shape = Arrays.stream(recipe.getShape()).map(
                line -> {
                    char[] newChars = new char[line.length()];
                    for (int i = line.length() - 1; i >= 0; i--) {
                        newChars[i] = line.charAt(line.length() - 1 - i);
                    }
                    return new String(newChars);
                }
        ).toArray(String[]::new);
        reflected.shape(shape);
        for (Map.Entry<Character, RecipeChoice> entry : recipe.getChoiceMap().entrySet()) {
            reflected.setIngredient(entry.getKey(), entry.getValue());
        }
        return reflected;
    }
}
