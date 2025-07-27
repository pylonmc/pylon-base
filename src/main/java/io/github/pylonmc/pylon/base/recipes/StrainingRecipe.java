package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * @param fluidAmount the amount of fluid per recipe, both input and output
 */
public record StrainingRecipe(
        @NotNull NamespacedKey key,
        @NotNull PylonFluid inputFluid,
        double fluidAmount,
        @NotNull PylonFluid outputFluid,
        @NotNull ItemStack outputItem
) implements PylonRecipe {

    public static final RecipeType<StrainingRecipe> RECIPE_TYPE = new RecipeType<>(
            baseKey("fluid_strainer")
    );

    public static final PersistentDataType<?, StrainingRecipe> DATA_TYPE =
            PylonSerializers.KEYED.keyedTypeFrom(StrainingRecipe.class, RECIPE_TYPE::getRecipeOrThrow);

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<FluidOrItem> getInputs() {
        return List.of(FluidOrItem.of(inputFluid, fluidAmount));
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(outputItem), FluidOrItem.of(outputFluid, fluidAmount));
    }

    @Override
    public @NotNull Gui display() {
        // 'n' can never be an item, it's just set to air to try and make the gui clearer
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # i # # # # #",
                        "# # # s # t # # #",
                        "# # # o # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('i', new FluidButton(inputFluid.getKey(), fluidAmount))
                .addIngredient('s', BaseItems.FLUID_STRAINER)
                .addIngredient('o', new FluidButton(outputFluid.getKey(), fluidAmount))
                .addIngredient('t', ItemButton.fromStack(outputItem))
                .build();
    }
}