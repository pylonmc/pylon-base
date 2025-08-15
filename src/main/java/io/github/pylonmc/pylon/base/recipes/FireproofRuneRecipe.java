package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.magic.FireproofRune;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

/**
 * @author balugaq
 */
public record FireproofRuneRecipe(
        @NotNull NamespacedKey key,
        @NotNull ItemStack input,
        @NotNull ItemStack result
) implements PylonRecipe {
    public static final RecipeType<FireproofRuneRecipe> RECIPE_TYPE = new RecipeType<>(
            BaseKeys.FIREPROOF_RUNE
    );

    /**
     * Creates a new FireproofRuneRecipe with the given parameters.
     *
     * @param key    The namespaced key for this recipe
     * @param input  The input item stack
     * @param result The result item stack
     * @return A new FireproofRuneRecipe instance
     * @throws IllegalArgumentException if input or result is not an item
     */
    public static @NotNull FireproofRuneRecipe of(
            @NotNull NamespacedKey key,
            @NotNull ItemStack input,
            @NotNull ItemStack result
    ) {
        if (!input.getType().isItem()) {
            throw new IllegalArgumentException("Input must be an item");
        }
        if (!result.getType().isItem()) {
            throw new IllegalArgumentException("Result must be an item");
        }
        return new FireproofRuneRecipe(key, input, result);
    }


    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # b # # # #",
                        "# # # # # g # # #",
                        "# i g g g g g r #",
                        "# # # # # g # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('b', ItemButton.fromStack(BaseItems.FIREPROOF_RUNE))
                .addIngredient('g', ItemButton.fromStack(new ItemStack(Material.GREEN_STAINED_GLASS_PANE)))
                .addIngredient('i', ItemButton.fromStack(input))
                .addIngredient('r', ItemButton.fromStack(result))
                .build();
    }

    /**
     * Return the namespaced identifier for this object.
     *
     * @return this object's key
     */
    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<FluidOrItem> getInputs() {
        return List.of(FluidOrItem.of(input));
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(result));
    }
}
