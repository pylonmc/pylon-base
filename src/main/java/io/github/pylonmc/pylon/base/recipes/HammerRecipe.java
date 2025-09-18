package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.content.tools.Hammer;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.*;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AutoCycleItem;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * @param input the input item (setting the itemstack to have an amount that's not 1 will have no effect)
 * @param result the output item (respects amount)
 * @param level the minimum hammer mining level
 * @param chance the chance to succeed per attempt
 */
public record HammerRecipe(
        @NotNull NamespacedKey key,
        @NotNull RecipeInput.Item input,
        @NotNull ItemStack result,
        @NotNull MiningLevel level,
        float chance
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<HammerRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("hammer")) {

        private static final ConfigAdapter<MiningLevel> MINING_LEVEL_ADAPTER = ConfigAdapter.ENUM.from(MiningLevel.class);

        @Override
        protected @NotNull HammerRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new HammerRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.RECIPE_INPUT_ITEM),
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("mining-level", MINING_LEVEL_ADAPTER),
                    section.getOrThrow("chance", ConfigAdapter.FLOAT)
            );
        }
    };

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
                        "# # # i h o # # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('i', ItemButton.from(input))
                .addIngredient('h', new AutoCycleItem(20,
                        Hammer.hammersWithMiningLevelAtLeast(level)
                                .stream()
                                .map(ItemStackBuilder::of)
                                .toList()
                                .toArray(ItemStackBuilder[]::new)
                ))
                .addIngredient('o', ItemButton.from(result))
                .build();
    }
}