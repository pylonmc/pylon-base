package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.content.machines.simple.MagicAltar;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.*;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * @param inputs pedestal inputs (must be of size 8) (setting the itemstack to have an amount that's not 1 will
 *               have no effect)
 * @param catalyst the item which must be right-clicked on the magic altar to start the craft (setting the
 *                 itemstack to have an amount that's not 1 will have no effect)
 * @param result the output (respects amount)
 */
public record MagicAltarRecipe(
        @NotNull NamespacedKey key,
        @NotNull List<RecipeInput.@Nullable Item> inputs,
        @NotNull RecipeInput.Item catalyst,
        @NotNull ItemStack result,
        double timeSeconds
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<MagicAltarRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("magic_altar")) {
        @Override
        protected @NotNull MagicAltarRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            List<String> shape = section.getOrThrow("shape", ConfigAdapter.LIST.from(ConfigAdapter.STRING));
            if (shape.size() != 3) {
                throw new IllegalArgumentException("Invalid shape size, must be 3");
            }
            for (String row : shape) {
                if (row.length() != 3) {
                    throw new IllegalArgumentException("Invalid shape row length, must be 3");
                }
            }
            Map<Character, RecipeInput.Item> itemMap = section.getOrThrow("key", ConfigAdapter.MAP.from(
                    ConfigAdapter.CHAR,
                    ConfigAdapter.RECIPE_INPUT_ITEM
            ));

            StringBuilder ingredientChars = new StringBuilder();
            ingredientChars.append(shape.getFirst());
            ingredientChars.append(shape.get(1).charAt(2));
            ingredientChars.append(new StringBuilder(shape.get(2)).reverse());
            ingredientChars.append(shape.get(1).charAt(0));
            List<RecipeInput.Item> inputs = new ArrayList<>(8);
            for (int i = 0; i < ingredientChars.length(); i++) {
                char c = ingredientChars.charAt(i);
                if (c == ' ') {
                    inputs.add(null);
                } else if (itemMap.containsKey(c)) {
                    inputs.add(itemMap.get(c));
                } else {
                    throw new IllegalArgumentException("Unknown character in shape: " + c);
                }
            }

            RecipeInput.Item catalyst = itemMap.get(shape.get(1).charAt(1));
            if (catalyst == null) {
                throw new IllegalArgumentException("Catalyst (center item) cannot be empty");
            }

            return new MagicAltarRecipe(
                    key,
                    inputs,
                    catalyst,
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("time-seconds", ConfigAdapter.DOUBLE)
            );
        }
    };

    public MagicAltarRecipe {
        if (inputs.size() != MagicAltar.PEDESTAL_COUNT) {
            throw new IllegalArgumentException("Invalid number of inputs, must be " + MagicAltar.PEDESTAL_COUNT);
        }
    }

    public boolean ingredientsMatch(List<ItemStack> ingredients) {
        Preconditions.checkArgument(
                ingredients.size() == MagicAltar.PEDESTAL_COUNT,
                "Invalid number of ingredients, must be %d",
                MagicAltar.PEDESTAL_COUNT
        );

        for (int i = 0; i < MagicAltar.PEDESTAL_COUNT; i++) {
            boolean allIngredientsMatch = true;
            for (int j = 0; j < MagicAltar.PEDESTAL_COUNT; j++) {
                RecipeInput.Item input = this.inputs.get(j);
                if (input != null && !input.matches(ingredients.get(j))) {
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
        return ingredientsMatch(ingredients) && this.catalyst.matches(catalyst);
    }

    @Override
    public @NotNull List<RecipeInput> getInputs() {
        List<RecipeInput> inputResult = new ArrayList<>(inputs);
        inputResult.add(catalyst);
        return inputResult;
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(result));
    }

    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # 0 1 2 # # # #",
                        "m # 7 c 3 # t # r",
                        "# # 6 5 4 # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('m', ItemButton.from(BaseItems.MAGIC_ALTAR))
                .addIngredient('c', ItemButton.from(catalyst))
                .addIngredient('0', ItemButton.from(inputs.get(0)))
                .addIngredient('1', ItemButton.from(inputs.get(1)))
                .addIngredient('2', ItemButton.from(inputs.get(2)))
                .addIngredient('3', ItemButton.from(inputs.get(3)))
                .addIngredient('4', ItemButton.from(inputs.get(4)))
                .addIngredient('5', ItemButton.from(inputs.get(5)))
                .addIngredient('6', ItemButton.from(inputs.get(6)))
                .addIngredient('7', ItemButton.from(inputs.get(7)))
                .addIngredient('t', GuiItems.progressCyclingItem((int) (timeSeconds * 20),
                        ItemStackBuilder.of(Material.CLOCK)
                                .name(net.kyori.adventure.text.Component.translatable(
                                        "pylon.pylonbase.guide.recipe.magic-altar",
                                        PylonArgument.of("time", UnitFormat.SECONDS.format(timeSeconds))
                                ))
                ))
                .addIngredient('r', ItemButton.from(result))
                .build();
    }
}
