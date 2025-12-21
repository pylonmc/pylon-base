package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.recipes.intermediate.RecipeFormation;
import io.github.pylonmc.pylon.base.recipes.intermediate.Step;
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
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public record BlueprintWorkbenchRecipe(
    NamespacedKey key,
    RecipeFormation recipe,
    List<ItemStack> results,
    List<Step> steps
) implements PylonRecipe, Step.StepsHolder {

    public static final RecipeType<BlueprintWorkbenchRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("blueprint_workbench")) {
        @Override
        protected @NotNull BlueprintWorkbenchRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            List<ItemStack> results = section.getOrThrow("results", ConfigAdapter.LIST.from(ConfigAdapter.ITEM_STACK));

            // region Recipe
            var recipeSection = section.getSection("ingredients");
            Map<Character, RecipeInput.Item> ingredientKey = recipeSection.getOrThrow("key", ConfigAdapter.MAP.from(ConfigAdapter.CHAR, ConfigAdapter.RECIPE_INPUT_ITEM));

            List<String> pattern = recipeSection.getOrThrow("pattern", ConfigAdapter.LIST.from(ConfigAdapter.STRING));

            RecipeFormation recipe = new RecipeFormation(pattern.toArray(new String[0]));

            // Convert List<String> to String[]
            for (var entry : ingredientKey.entrySet()) {
                Character character = entry.getKey();
                RecipeInput.Item item = entry.getValue();
                recipe.setIngredient(character, new RecipeChoice.ExactChoice(
                    item.getItems().stream()
                        .map(it -> it.createItemStack().asQuantity(item.getAmount()))
                        .toList()
                    )
                );
            }

            //endregion

            List<Step> steps = section.getOrThrow("steps", ConfigAdapter.LIST.from(Step.ADAPTER));

            return new BlueprintWorkbenchRecipe(
                key,
                recipe,
                results,
                steps
            );
        }
    };

    public static List<BlueprintWorkbenchRecipe> findRecipes(ItemStack[] items) {
        List<BlueprintWorkbenchRecipe> recipes = new ArrayList<>();
        for (BlueprintWorkbenchRecipe recipe : RECIPE_TYPE.getRecipes()) {
            if (recipe.recipe.check(items)) {
                recipes.add(recipe);
            }
        }

        return recipes;
    }

    public static BlueprintWorkbenchRecipe findRecipe(ItemStack[] items, int offset) {
        List<BlueprintWorkbenchRecipe> recipes = findRecipes(items);
        if (recipes.isEmpty()) return null;

        return recipes.get(offset % recipes.size());
    }

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        var exactChoiceList = recipe.getIngredients().values().stream()
            .map(RecipeChoice.ExactChoice.class::cast)
            .toList();

        List<RecipeInput> result = new ArrayList<>(9);
        for (var exactChoice : exactChoiceList) {
            result.add(new RecipeInput.Item(1, exactChoice.getChoices().toArray(ItemStack[]::new)));
        }

        return result;
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
        return results.stream().map(FluidOrItem::of).toList();
    }

    @Override
    public @NotNull Gui display() {

        var gui = Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# 0 3 6 r a d g #",
                "# 1 4 7 $ b e h #",
                "# 2 5 8 # c f i #",
                "# # # # # # # # #"
            )
            .addIngredient('r', BaseItems.BLUEPRINT_WORKBENCH)
            .addIngredient('#', GuiItems.backgroundBlack())
            .addIngredient('$', this.getItemStep())
            .build();

        int height = recipe.getRows().length;
        int width = recipe.getRows()[0].length();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                gui.setItem(10 + x + 9 * y, recipe.getDisplaySlot(x, y));
            }
        }

        int amount = results.size();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (amount == 0) {
                    break;
                }

                gui.setItem(14 + x + 9 * y, ItemButton.from(
                    results.get(--amount)
                ));
            }

            if (amount == 0) {
                break;
            }
        }

        return gui;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

}