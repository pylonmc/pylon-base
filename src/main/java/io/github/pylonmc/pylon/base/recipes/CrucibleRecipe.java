package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
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

public record CrucibleRecipe(
    @NotNull NamespacedKey key,
    @NotNull ItemStack input,
    @NotNull FluidOrItem.Fluid output
) implements PylonRecipe {

    public static final RecipeType<CrucibleRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("crucible")) {
        @Override
        protected @NotNull CrucibleRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            FluidOrItem output = section.getOrThrow("output", ConfigAdapter.FLUID_OR_ITEM);
            if (!(output instanceof FluidOrItem.Fluid fluidOutput)) {
                throw new IllegalArgumentException("In crucible recipe output must be a fluid.");
            }

            return new CrucibleRecipe(
                key,
                section.getOrThrow("input-item", ConfigAdapter.ITEM_STACK),
                fluidOutput
            );
        }
    };

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        return List.of(RecipeInput.of(input));
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
        return List.of(output);
    }

    @Override
    public @NotNull Gui display() {
        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# # # # # i # # #",
                "# # . # # m # o #",
                "# # # # # # # # #",
                "# # # # # # # # #"
            )
            .addIngredient('#', GuiItems.backgroundBlack())
            .addIngredient('i', new ItemButton(input))
            .addIngredient('m', ItemButton.from(BaseItems.CRUCIBLE))
            .addIngredient('o', new FluidButton(output.amountMillibuckets(), output.fluid())
        ).build();
    }

    public boolean matches(ItemStack inputItem) {
        if (inputItem.getAmount() < input.getAmount()) return false;
        return input.isSimilar(inputItem);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
