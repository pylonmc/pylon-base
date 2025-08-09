package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * @param input the input item (assumed to have an amount of one)
 * @param result the output inputFluid, of which MELT_AMOUNT will is produced per recipe
 * @param temperature the minimum temperature the smeltery must be at
 */
public record MeltingRecipe(
        @NotNull NamespacedKey key,
        @NotNull ItemStack input,
        @NotNull PylonFluid result,
        double temperature
) implements PylonRecipe {

    public static final double MELT_AMOUNT
            = Settings.get(BaseKeys.SMELTERY_HOPPER).getOrThrow("melt-amount-mb", Double.class);

    public static final RecipeType<MeltingRecipe> RECIPE_TYPE = new RecipeType<>(
            baseKey("melt_recipe")
    );

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<FluidOrItem> getInputs() {
        return List.of(FluidOrItem.of(input));
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(result, MELT_AMOUNT));
    }

    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# h # # i t o # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('h', ItemButton.fromStack(BaseItems.SMELTERY_HOPPER))
                .addIngredient('i', ItemButton.fromStack(input))
                .addIngredient('t', ItemStackBuilder.of(Material.BLAZE_POWDER)
                        .name(net.kyori.adventure.text.Component.translatable(
                                "pylon.pylonbase.guide.recipe.melting",
                                PylonArgument.of("temperature", UnitFormat.CELSIUS.format(temperature))
                        ))
                )
                .addIngredient('o', new FluidButton(result.getKey(), MELT_AMOUNT))
                .build();
    }
}
