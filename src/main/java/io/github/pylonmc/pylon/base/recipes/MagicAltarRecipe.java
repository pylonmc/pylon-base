package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.machines.simple.MagicAltar;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Ingredients list must be of size 8. Set an ingredient to null to leave that pedestal empty.
 *
 * Ingredients and catalyst must have an amount of 1
 */
public record MagicAltarRecipe(
        NamespacedKey key,
        List<RecipeChoice> ingredients,
        RecipeChoice catalyst,
        ItemStack result,
        double timeSeconds
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<MagicAltarRecipe> RECIPE_TYPE = new RecipeType<>(
            new NamespacedKey(PylonBase.getInstance(), "magic_altar")
    );

    static {
        PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
    }

    public boolean ingredientsMatch(List<ItemStack> ingredients) {
        assert this.ingredients.size() == MagicAltar.PEDESTAL_COUNT;
        assert ingredients.size() == MagicAltar.PEDESTAL_COUNT;

        for (int i = 0; i < MagicAltar.PEDESTAL_COUNT; i++) {

            boolean allIngredientsMatch = true;
            for (int j = 0; j < MagicAltar.PEDESTAL_COUNT; j++) {
                RecipeChoice recipeChoice = this.ingredients.get(j);
                if (recipeChoice != null && !recipeChoice.test(ingredients.get(j))) {
                    allIngredientsMatch = false;
                    break;
                }
            }

            if (allIngredientsMatch) {
                return true;
            }

            ingredients.add(ingredients.removeFirst());
        }

        return false;
    }

    public boolean isValidRecipe(List<ItemStack> ingredients, ItemStack catalyst) {
        return ingredientsMatch(ingredients) && this.catalyst.test(catalyst);
    }

    @Override
    public @NotNull List<@NotNull RecipeChoice> getInputItems() {
        List<RecipeChoice> choices = new ArrayList<>(ingredients);
        choices.add(catalyst);
        choices.removeIf(Objects::isNull);
        return choices;
    }

    @Override
    public @NotNull List<@NotNull ItemStack> getOutputItems() {
        return List.of(result);
    }

    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # 7 0 1 # # # #",
                        "m # 6 c 2 # t # r",
                        "# # 5 4 3 # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('m', ItemButton.fromStack(BaseItems.MAGIC_ALTAR))
                .addIngredient('c', ItemButton.fromChoice(catalyst))
                .addIngredient('0', ItemButton.fromChoice(ingredients.get(0)))
                .addIngredient('1', ItemButton.fromChoice(ingredients.get(1)))
                .addIngredient('2', ItemButton.fromChoice(ingredients.get(2)))
                .addIngredient('3', ItemButton.fromChoice(ingredients.get(3)))
                .addIngredient('4', ItemButton.fromChoice(ingredients.get(4)))
                .addIngredient('5', ItemButton.fromChoice(ingredients.get(5)))
                .addIngredient('6', ItemButton.fromChoice(ingredients.get(6)))
                .addIngredient('7', ItemButton.fromChoice(ingredients.get(7)))
                .addIngredient('t', GuiItems.progressCyclingItem((int) (timeSeconds * 20),
                        ItemStackBuilder.of(Material.CLOCK)
                                .name(net.kyori.adventure.text.Component.translatable(
                                        "pylon.pylonbase.guide.recipe.magic-altar",
                                        PylonArgument.of("time", UnitFormat.SECONDS.format(timeSeconds))
                                ))
                ))
                .addIngredient('r', ItemButton.fromStack(result))
                .build();
    }
}
