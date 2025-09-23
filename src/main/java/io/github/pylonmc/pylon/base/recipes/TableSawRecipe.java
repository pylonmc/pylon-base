package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.*;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * @param input the input item (respects amount)
 * @param result the output item (respects amount)
 * @param particleData the block data to use for particles
 * @param timeTicks the recipe time in ticks
 */
public record TableSawRecipe(
        @NotNull NamespacedKey key,
        @NotNull ItemStack input,
        @NotNull ItemStack result,
        @NotNull BlockData particleData,
        int timeTicks
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<TableSawRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("table_saw")) {
        @Override
        protected @NotNull TableSawRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new TableSawRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("particle-data", ConfigAdapter.BLOCK_DATA),
                    section.getOrThrow("time-ticks", ConfigAdapter.INT)
            );
        }
    };

    @Override
    public @NotNull List<RecipeInput> getInputs() {
        return List.of(RecipeInput.of(input));
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
                        "# # # i s o # # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('i', ItemButton.from(input))
                .addIngredient('s', GuiItems.progressCyclingItem(timeTicks, ItemStackBuilder.of(BaseItems.HYDRAULIC_TABLE_SAW)))
                .addIngredient('o', ItemButton.from(result))
                .build();
    }
}