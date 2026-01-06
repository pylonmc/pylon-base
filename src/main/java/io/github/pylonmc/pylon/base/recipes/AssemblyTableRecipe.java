package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.recipes.intermediate.Step;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
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
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public record AssemblyTableRecipe(
    NamespacedKey key,
    List<ItemStack> ingredients,
    List<ItemStack> results,
    List<Step> steps
) implements PylonRecipe, Step.StepsHolder {

    public static final RecipeType<AssemblyTableRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("assembly_table")) {

        @Override
        protected @NotNull AssemblyTableRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            List<ItemStack> ingredients = section.getOrThrow("ingredients", ConfigAdapter.LIST.from(ConfigAdapter.ITEM_STACK));
            List<ItemStack> results = section.getOrThrow("results", ConfigAdapter.LIST.from(ConfigAdapter.ITEM_STACK));
            List<Step> steps = section.getOrThrow("steps", ConfigAdapter.LIST.from(Step.ADAPTER));

            return new AssemblyTableRecipe(
                key,
                ingredients,
                results,
                steps
            );
        }
    };

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        return ingredients.stream().map(RecipeInput::of).map(RecipeInput.class::cast).toList();
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
        return results.stream().map(FluidOrItem::of).toList();
    }

    @Override
    public @NotNull Gui display() {
        VirtualInventory input = new VirtualInventory(9);
        VirtualInventory output = new VirtualInventory(9);

        for (ItemStack ingredient : ingredients) {
            input.addItem(UpdateReason.SUPPRESSED, ingredient);
        }

        for (ItemStack result : results) {
            output.addItem(UpdateReason.SUPPRESSED, result);
        }

        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# x x x r y y y #",
                "# x x x $ y y y #",
                "# x x x # y y y #",
                "# # # # # # # # #"
            )
            .addIngredient('#', GuiItems.backgroundBlack())
            .addIngredient('x', input)
            .addIngredient('y', output)
            .addIngredient('r', BaseItems.ASSEMBLY_TABLE)
            .addIngredient('$', this.getItemStep())
            .build();
    }

    public static List<AssemblyTableRecipe> findRecipes(ItemStack[] items) {
        List<AssemblyTableRecipe> recipes = new ArrayList<>();

        for (AssemblyTableRecipe recipe : RECIPE_TYPE.getRecipes()) {
            List<ItemStack> requiredItems = new ArrayList<>(recipe.ingredients);
            for (ItemStack item : items) {
                if (item == null) continue;

                Iterator<ItemStack> iterator = requiredItems.iterator();
                while (iterator.hasNext()) {
                    ItemStack requirement = iterator.next();

                    if (requirement.getAmount() > item.getAmount()) continue;
                    if (!requirement.isSimilar(item)) continue;

                    iterator.remove();
                    break;
                }
            }

            if (requiredItems.isEmpty()) recipes.add(recipe);
        }

        return recipes;
    }

    public static AssemblyTableRecipe findRecipe(ItemStack[] items, int offset) {
        List<AssemblyTableRecipe> recipes = findRecipes(items);
        if (recipes.isEmpty()) return null;

        return recipes.get(offset % recipes.size());
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
