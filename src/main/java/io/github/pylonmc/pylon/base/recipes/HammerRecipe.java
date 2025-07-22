package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.tools.Hammer;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.impl.AutoCycleItem;

import java.util.List;

public record HammerRecipe(
        NamespacedKey key,
        ItemStack input,
        ItemStack result,
        MiningLevel level,
        float chance
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<HammerRecipe> RECIPE_TYPE = new RecipeType<>(
            new NamespacedKey(PylonBase.getInstance(), "hammer")
    );

    static {
        PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
    }

    @Override
    public @NotNull List<@NotNull RecipeChoice> getInputItems() {
        return List.of(new RecipeChoice.ExactChoice(input));
    }

    @Override
    public @NotNull List<@NotNull ItemStack> getOutputItems() {
        return List.of(result);
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