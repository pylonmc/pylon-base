package io.github.pylonmc.pylon.base.recipes.intermediate;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import xyz.xenondevs.invui.item.Item;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public class RecipeFormation {
    private final String[] rows;
    private final Map<Character, RecipeChoice> ingredients = new HashMap<>();

    public RecipeFormation(String... shape) {
        Preconditions.checkNotNull(shape);
        Preconditions.checkArgument(shape.length > 0 && shape.length < 4);

        int lastLen = -1;
        for (String row : shape) {
            Preconditions.checkArgument(row != null, "Shape cannot have null rows");
            Preconditions.checkArgument(!row.isEmpty() && row.length() < 4, "Crafting rows should be 1, 2, or 3 characters, not ", row.length());

            Preconditions.checkArgument(lastLen == -1 || lastLen == row.length(), "Crafting recipes must be rectangular");
            lastLen = row.length();
        }

        rows = Arrays.copyOf(shape, shape.length);
    }

    public void setIngredient(char c, RecipeChoice choice) {
        ingredients.put(c, choice);
    }

    public boolean check(ItemStack[] array) {
        RecipeChoice[] grid = new RecipeChoice[9];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                int position = x + y * 3;
                RecipeChoice choice = getChoice(x, y);
                grid[position] = choice;
            }
        }

        for (int i = 0; i < 9; i++) {
            RecipeChoice choice = grid[i];
            ItemStack stack = array[i];

            if (stack == null) stack = ItemStack.empty();
            if (choice == null && stack.isEmpty()) continue;

            if (choice == null || !choice.test(stack)) {
                return false;
            }
        }

        return true;
    }

    public RecipeChoice getChoice(int x, int y) {
        var character = this.rows[y].charAt(x);
        return this.ingredients.get(character);
    }

    public Item getDisplaySlot(int x, int y) {
        return ItemButton.from(getChoice(x, y));
    }
}
