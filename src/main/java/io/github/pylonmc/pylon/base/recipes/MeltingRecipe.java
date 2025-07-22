package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public record MeltingRecipe(
        @NotNull NamespacedKey key,
        @NotNull ItemStack input,
        @NotNull PylonFluid result,
        double temperature
) implements PylonRecipe {

    public static final RecipeType<MeltingRecipe> RECIPE_TYPE = new RecipeType<>(
            baseKey("melt_recipe")
    );

    static {
        RECIPE_TYPE.register();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<@NotNull RecipeChoice> getInputItems() {
        return List.of(new RecipeChoice.ExactChoice(input));
    }

    @Override
    public @NotNull List<@NotNull PylonFluid> getInputFluids() {
        return List.of();
    }

    @Override
    public @NotNull List<@NotNull ItemStack> getOutputItems() {
        return List.of();
    }

    @Override
    public @NotNull List<@NotNull PylonFluid> getOutputFluids() {
        return List.of(result);
    }

    @Override
    public @NotNull Gui display() {
        // TODO
        return null;
    }
}
