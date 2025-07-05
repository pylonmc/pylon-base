package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.pylonKey;

public record CastingRecipe(
        @NotNull NamespacedKey key,
        @NotNull PylonFluid fluid,
        @NotNull ItemStack result,
        double temperature
) implements PylonRecipe {

    public static final double CAST_AMOUNT = 250;

    public static final RecipeType<CastingRecipe> RECIPE_TYPE = new RecipeType<>(
            pylonKey("cast_recipe")
    );

    static {
        RECIPE_TYPE.register();
    }

    public static @Nullable CastingRecipe getCastRecipeFor(@NotNull PylonFluid fluid) {
        for (CastingRecipe recipe : RECIPE_TYPE) {
            if (recipe.fluid.equals(fluid)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<@NotNull RecipeChoice> getInputItems() {
        return List.of();
    }

    @Override
    public @NotNull List<@NotNull PylonFluid> getInputFluids() {
        return List.of(fluid);
    }

    @Override
    public @NotNull List<@NotNull ItemStack> getOutputItems() {
        return List.of(result);
    }

    @Override
    public @NotNull List<@NotNull PylonFluid> getOutputFluids() {
        return List.of();
    }

    @Override
    public @NotNull Gui display() {
        // TODO
        return null;
    }
}
