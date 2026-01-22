package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.recipe.*;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public record ForgingDisplayRecipe(
        NamespacedKey key,
        ItemStack input,
        ItemStack result
) implements PylonRecipe {

    public static final RecipeType<ForgingDisplayRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("forging_display")) {
        @Override
        protected @NotNull ForgingDisplayRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new ForgingDisplayRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.ITEM_STACK),
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
        return Gui.builder()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # h # # # #",
                        "# # # # i # r # #",
                        "# # # # b # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('h', new ItemButton(BaseItems.STONE_HAMMER, BaseItems.IRON_HAMMER, BaseItems.DIAMOND_HAMMER, BaseItems.TONGS))
                .addIngredient('i', input)
                .addIngredient('b', new ItemButton(BaseItems.BRONZE_ANVIL))
                .addIngredient('r', result)
                .build();
    }
}
