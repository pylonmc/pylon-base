package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.tools.Hammer;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeKey;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AutoCycleItem;

import java.util.List;

/**
 * @param input the input item (setting the itemstack to have an amount that's not 1 will have no effect)
 * @param result the output item (respects amount)
 * @param level the minimum hammer mining level
 * @param chance the chance to succeed per attempt
 */
public record HammerRecipe(
        @NotNull @RecipeKey NamespacedKey key,
        @NotNull ItemStack input,
        @NotNull ItemStack result,
        @NotNull MiningLevel level,
        float chance
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<HammerRecipe> RECIPE_TYPE = new RecipeType<>(
            new NamespacedKey(PylonBase.getInstance(), "hammer"),
            HammerRecipe.class
    );

    @Override
    public @NotNull List<FluidOrItem> getInputs() {
        return List.of(FluidOrItem.of(input));
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
                .addIngredient('i', ItemButton.fromStack(input))
                .addIngredient('h', new AutoCycleItem(20,
                        Hammer.hammersWithMiningLevelAtLeast(level)
                                .stream()
                                .map(ItemStackBuilder::of)
                                .toList()
                                .toArray(new ItemStackBuilder[]{})
                ))
                .addIngredient('o', ItemButton.fromStack(result))
                .build();
    }
}