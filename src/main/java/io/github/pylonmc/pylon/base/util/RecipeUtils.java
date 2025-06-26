package io.github.pylonmc.pylon.base.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.inventory.Inventory;

import java.util.Arrays;
import java.util.List;
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

    public static boolean matchRecipeChoiceMap(Map<RecipeChoice, Integer> recipe, List<ItemStack> stacks){
        for(Map.Entry<RecipeChoice, Integer> component : recipe.entrySet()){
            int sumItems = 0;
            for(ItemStack stack : stacks){
                if(component.getKey().test(stack)){
                    sumItems += stack.getAmount();
                }
            }
            if(sumItems < component.getValue()){
                return false;
            }
        }
        return true;
    }

    public static void removeRecipeChoiceMapFromGui(Map<RecipeChoice, Integer> recipe, AbstractGui gui){
        for(Map.Entry<RecipeChoice, Integer> component : recipe.entrySet()){
            int required = component.getValue();
            rmitemloop:
            for(Inventory inventory : gui.getAllInventories()){
                for(int i = 0; i < inventory.getSize(); i++){
                    ItemStack curItem = inventory.getItem(i);
                    if(curItem != null && component.getKey().test(curItem)){
                        if(required <= curItem.getAmount()){
                            curItem.subtract(required);
                            inventory.setItemSilently(i, curItem);
                            break rmitemloop;
                        }
                        else {
                            required -= curItem.getAmount();
                            curItem.subtract(curItem.getAmount());
                            inventory.setItemSilently(i, curItem);
                        }
                    }
                }
            }
        }
    }
}
