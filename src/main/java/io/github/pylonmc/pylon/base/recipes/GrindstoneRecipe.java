package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.machines.simple.Grindstone;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

public record GrindstoneRecipe(
        NamespacedKey key,
        ItemStack input,
        ItemStack output,
        int cycles,
        BlockData particleBlockData
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<GrindstoneRecipe> RECIPE_TYPE = new RecipeType<>(
            new NamespacedKey(PylonBase.getInstance(), "grindstone")
    );

    static {
        PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
    }

    public int timeTicks() {
        // add 2*tick rate to account for stone going up and down
        return cycles * Grindstone.CYCLE_TIME_TICKS + 2 * Grindstone.TICK_RATE;
    }

    @Override
    public @NotNull List<@NotNull RecipeChoice> getInputItems() {
        return List.of(new RecipeChoice.ExactChoice(input));
    }

    @Override
    public @NotNull List<@NotNull ItemStack> getOutputItems() {
        return List.of(output);
    }

    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# g # # i c o # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('g', ItemButton.fromStack(BaseItems.GRINDSTONE))
                .addIngredient('i', ItemButton.fromStack(input))
                .addIngredient('c', GuiItems.progressCyclingItem(cycles * Grindstone.CYCLE_TIME_TICKS,
                        ItemStackBuilder.of(Material.CLOCK)
                                .name(net.kyori.adventure.text.Component.translatable(
                                        "pylon.pylonbase.guide.recipe.grindstone",
                                        PylonArgument.of("time", UnitFormat.SECONDS.format(cycles * Grindstone.CYCLE_TIME_TICKS / 20))
                                ))
                ))
                .addIngredient('o', ItemButton.fromStack(output))
                .build();
    }
}