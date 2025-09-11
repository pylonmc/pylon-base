package io.github.pylonmc.pylon.base.recipes.display;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

// TODO use DisplayRecipeType
public record MoldingDisplayRecipe(
        NamespacedKey key,
        ItemStack input,
        ItemStack result,
        int moldClicks
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getInputs() {
        return List.of();
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(result));
    }

    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# # i # m # o # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('i', input)
                .addIngredient('m', BaseItems.BRICK_MOLD)
                .addIngredient('o', result)
                .build();
    }
}
