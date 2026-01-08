package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.recipe.ConfigurableRecipeType;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * This recipe represents the recipe that used fireproof rune as a catalyst to create an upgraded item.
 * It happens when the player drops the fireproof rune on an item.
 *
 * @param input the item to be upgraded
 * @param result the upgraded item
 * @author balugaq
 */
public record FireproofRuneRecipe(
        @NotNull NamespacedKey key,
        @NotNull ItemStack input,
        @NotNull ItemStack result
) implements PylonRecipe {
    public static final RecipeType<FireproofRuneRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("fireproof_rune")) {
        @Override
        protected @NotNull FireproofRuneRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new FireproofRuneRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK)
            );
        }
    };

    public static @NotNull FireproofRuneRecipe of(
            @NotNull NamespacedKey key,
            @NotNull ItemStack input,
            @NotNull ItemStack result
    ) {
        Preconditions.checkArgument(input.getType().isItem(), "Input must be an item");
        Preconditions.checkArgument(result.getType().isItem(), "Result must be an item");

        return new FireproofRuneRecipe(key, input, result);
    }


    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# # # i f o # # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('f', ItemButton.from(BaseItems.FIREPROOF_RUNE))
                .addIngredient('i', ItemButton.from(input))
                .addIngredient('o', ItemButton.from(result))
                .build();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        return List.of(RecipeInput.of(input));
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(result));
    }
}
