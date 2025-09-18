package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.content.machines.simple.Press;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.*;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * @param input input item (assumed to be of item amount 1)
 */
public record PressRecipe(
        @NotNull NamespacedKey key,
        @NotNull RecipeInput.Item input,
        double oilAmount
) implements PylonRecipe {

    public static final RecipeType<PressRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("press")) {
        @Override
        protected @NotNull PressRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new PressRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.RECIPE_INPUT_ITEM),
                    section.getOrThrow("oil-amount", ConfigAdapter.DOUBLE)
            );
        }
    };

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
        return List.of(FluidOrItem.of(BaseFluids.PLANT_OIL, oilAmount));
    }

    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# p # # i c o # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('p', ItemButton.from(BaseItems.PRESS))
                .addIngredient('i', ItemButton.from(input))
                .addIngredient('c', GuiItems.progressCyclingItem(Press.TIME_PER_ITEM_TICKS,
                        ItemStackBuilder.of(Material.CLOCK)
                                .name(net.kyori.adventure.text.Component.translatable(
                                        "pylon.pylonbase.guide.recipe.press",
                                        PylonArgument.of("time", UnitFormat.SECONDS.format(Press.TIME_PER_ITEM_TICKS / 20.0))
                                ))
                ))
                .addIngredient('o', new FluidButton(oilAmount, BaseFluids.PLANT_OIL))
                .build();
    }
}