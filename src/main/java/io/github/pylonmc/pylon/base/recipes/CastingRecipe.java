package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.*;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * @param input the input inputFluid, of which CAST_AMOUNT mB is used per recipe
 * @param result the output of the cast (respects amount)
 * @param temperature the minimum temperature the smeltery must be
 */
public record CastingRecipe(
        @NotNull NamespacedKey key,
        @NotNull RecipeInput.Fluid input,
        @NotNull ItemStack result,
        double temperature
) implements PylonRecipe {

    public static final RecipeType<CastingRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("casting")) {
        @Override
        protected @NotNull CastingRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new CastingRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.RECIPE_INPUT_FLUID),
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("temperature", ConfigAdapter.DOUBLE)
            );
        }
    };

    public static @Nullable CastingRecipe getCastRecipeFor(@NotNull PylonFluid fluid) {
        for (CastingRecipe recipe : RECIPE_TYPE) {
            if (recipe.input.contains(fluid)) {
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
    public @NotNull List<RecipeInput> getInputs() {
        return List.of(input);
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
                        "# # # # # # # # #",
                        "# c # # i t o # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('c', ItemButton.from(BaseItems.SMELTERY_CASTER))
                .addIngredient('i', new FluidButton(input))
                .addIngredient('t', ItemStackBuilder.of(Material.BLAZE_POWDER)
                        .name(net.kyori.adventure.text.Component.translatable(
                                "pylon.pylonbase.guide.recipe.melting",
                                PylonArgument.of("temperature", UnitFormat.CELSIUS.format(temperature))
                        ))
                )
                .addIngredient('o', ItemButton.from(result))
                .build();
    }
}
