package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.rebar.config.ConfigSection;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.recipe.*;
import io.github.pylonmc.rebar.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public record DrillingDisplayRecipe(
        NamespacedKey key,
        ItemStack drill,
        ItemStack result
) implements PylonRecipe {

    public static final RecipeType<DrillingDisplayRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("drilling_display")) {
        @Override
        protected @NotNull DrillingDisplayRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new DrillingDisplayRecipe(
                    key,
                    section.getOrThrow("drill", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK)
            );
        }
    };

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        return List.of();
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(result));
    }

    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# # # d # r # # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('d', drill)
                .addIngredient('r', result)
                .build();
    }
}
