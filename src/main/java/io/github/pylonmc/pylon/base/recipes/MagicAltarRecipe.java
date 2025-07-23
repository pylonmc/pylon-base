package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.machines.simple.MagicAltar;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.github.pylonmc.pylon.core.util.ItemUtils.isPylonSimilar;

/**
 * @param inputs pedestal inputs (must be of size 8) (setting the itemstack to have an amount that's not 1 will
 *               have no effect)
 * @param catalyst the item which must be right-clicked on the magic altar to start the craft (setting the
 *                 itemstack to have an amount that's not 1 will have no effect)
 * @param result the output (respects amount)
 */
public record MagicAltarRecipe(
        NamespacedKey key,
        List<ItemStack> inputs,
        ItemStack catalyst,
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
        assert this.inputs.size() == MagicAltar.PEDESTAL_COUNT;
        assert ingredients.size() == MagicAltar.PEDESTAL_COUNT;

        for (int i = 0; i < MagicAltar.PEDESTAL_COUNT; i++) {

            boolean allIngredientsMatch = true;
            for (int j = 0; j < MagicAltar.PEDESTAL_COUNT; j++) {
                ItemStack input = this.inputs.get(j);
                if (input != null && !isPylonSimilar(input, ingredients.get(j))) {
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
        return ingredientsMatch(ingredients) && isPylonSimilar(this.catalyst, catalyst);
    }

    @Override
    public @NotNull List<FluidOrItem> getInputs() {
        List<FluidOrItem> inputs = new ArrayList<>(this.inputs.stream()
                .map(input -> (FluidOrItem) FluidOrItem.of(input))
                .toList()
        );
        inputs.add(FluidOrItem.of(catalyst));
        inputs.removeIf(Objects::isNull);
        return inputs;
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
                        "# # 7 0 1 # # # #",
                        "m # 6 c 2 # t # r",
                        "# # 5 4 3 # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('m', ItemButton.fromStack(BaseItems.MAGIC_ALTAR))
                .addIngredient('c', ItemButton.fromStack(catalyst))
                .addIngredient('0', ItemButton.fromStack(inputs.get(0)))
                .addIngredient('1', ItemButton.fromStack(inputs.get(1)))
                .addIngredient('2', ItemButton.fromStack(inputs.get(2)))
                .addIngredient('3', ItemButton.fromStack(inputs.get(3)))
                .addIngredient('4', ItemButton.fromStack(inputs.get(4)))
                .addIngredient('5', ItemButton.fromStack(inputs.get(5)))
                .addIngredient('6', ItemButton.fromStack(inputs.get(6)))
                .addIngredient('7', ItemButton.fromStack(inputs.get(7)))
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
