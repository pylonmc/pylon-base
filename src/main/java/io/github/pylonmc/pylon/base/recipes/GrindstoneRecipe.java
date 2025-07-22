package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.machines.simple.Grindstone;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

/**
 * @param input the input item (respects amount)
 * @param result the result item (respects amount)
 * @param cycles the number of full rotations needed to complete the recipe
 * @param particleBlockData the block data to use for the particles shown while grinding
 */
public record GrindstoneRecipe(
        NamespacedKey key,
        ItemStack input,
        ItemStack result,
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
                .addIngredient('o', ItemButton.fromStack(result))
                .build();
    }
}