package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public record StrainingRecipe(
        @NotNull NamespacedKey key,
        @NotNull PylonFluid inputFluid,
        double inputAmount,
        @NotNull PylonFluid outputFluid,
        @NotNull ItemStack outputItem
) implements PylonRecipe {

    public static final RecipeType<StrainingRecipe> RECIPE_TYPE = new RecipeType<>(
            baseKey("fluid_strainer")
    );

    static {
        RECIPE_TYPE.register();
    }

    public static final PersistentDataType<?, StrainingRecipe> DATA_TYPE =
            PylonSerializers.KEYED.keyedTypeFrom(StrainingRecipe.class, RECIPE_TYPE::getRecipeOrThrow);

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
        return List.of(inputFluid);
    }

    @Override
    public @NotNull List<@NotNull ItemStack> getOutputItems() {
        return List.of(outputItem);
    }

    @Override
    public @NotNull List<@NotNull PylonFluid> getOutputFluids() {
        return List.of(outputFluid);
    }

    @Override
    public @NotNull Gui display() {
        // TODO
        return null;
    }
}