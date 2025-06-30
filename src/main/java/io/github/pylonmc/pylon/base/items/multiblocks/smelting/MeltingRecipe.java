package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public record MeltingRecipe(
        @NotNull NamespacedKey key,
        @NotNull ItemStack input,
        @NotNull PylonFluid result,
        double temperature
) implements Keyed {

    public static final RecipeType<MeltingRecipe> RECIPE_TYPE = new RecipeType<>(
            pylonKey("melt_recipe")
    );

    static {
        RECIPE_TYPE.register();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
