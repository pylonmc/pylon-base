package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.UnclickableInventory;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public record PitKilnRecipe(
        @NotNull NamespacedKey key,
        @NotNull List<ItemStack> input,
        @NotNull List<ItemStack> output
) implements PylonRecipe {

    public static final RecipeType<PitKilnRecipe> RECIPE_TYPE = new RecipeType<>(
            baseKey("pit_kiln_recipe")
    );

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getInputs() {
        return input.stream().map(FluidOrItem::of).toList();
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
        return output.stream().map(FluidOrItem::of).toList();
    }

    @Override
    public @NotNull Gui display() {
        UnclickableInventory inputs = new UnclickableInventory(9);
        for (ItemStack item : input) {
            inputs.addItem(item);
        }

        UnclickableInventory outputs = new UnclickableInventory(9);
        for (ItemStack item : output) {
            outputs.addItem(item);
        }

        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# , , # # # . . #",
                        "# , , # p # . . #",
                        "# , , # # # . . #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient(',', inputs)
                .addIngredient('.', outputs)
                .addIngredient('p', BaseItems.PIT_KILN)
                .build();
    }
}
