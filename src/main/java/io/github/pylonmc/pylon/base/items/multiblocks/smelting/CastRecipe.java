package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public record CastRecipe(
        @NotNull NamespacedKey key,
        @NotNull PylonFluid fluid,
        @NotNull ItemStack result,
        double temperature
) implements Keyed {

    public static final RecipeType<CastRecipe> RECIPE_TYPE = new RecipeType<>(
            pylonKey("cast_recipe")
    );

    static {
        RECIPE_TYPE.register();
    }

    public static @Nullable CastRecipe getCastRecipeFor(@NotNull PylonFluid fluid) {
        for (CastRecipe recipe : RECIPE_TYPE) {
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
}
