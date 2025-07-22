package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public record CastingRecipe(
        @NotNull NamespacedKey key,
        @NotNull PylonFluid fluid,
        @NotNull ItemStack result,
        double temperature
) implements PylonRecipe {

    public static final double CAST_AMOUNT = 250;

    public static final RecipeType<CastingRecipe> RECIPE_TYPE = new RecipeType<>(
            baseKey("cast_recipe")
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
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# c # # i t o # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('c', ItemButton.fromStack(BaseItems.SMELTERY_CASTER))
                .addIngredient('i', new FluidButton(fluid.getKey(), CAST_AMOUNT))
                .addIngredient('t', ItemStackBuilder.of(Material.BLAZE_POWDER)
                        .name(net.kyori.adventure.text.Component.translatable(
                                "pylon.pylonbase.guide.recipe.melting",
                                PylonArgument.of("temperature", UnitFormat.CELSIUS.format(temperature))
                        ))
                )
                .addIngredient('o', ItemButton.fromStack(result))
                .build();
    }
}
