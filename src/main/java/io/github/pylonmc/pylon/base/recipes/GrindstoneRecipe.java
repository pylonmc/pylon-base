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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @param input the input item (respects amount)
 * @param results the result items and their corresponding probabilities
 *                (respects item amount) (maximum 9 items)
 * @param cycles the number of full rotations needed to complete the recipe
 * @param particleBlockData the block data to use for the particles shown while grinding
 */
public record GrindstoneRecipe(
        NamespacedKey key,
        ItemStack input,
        Map<ItemStack, Double> results,
        int cycles,
        BlockData particleBlockData
) implements PylonRecipe {

    public GrindstoneRecipe(
            NamespacedKey key,
            ItemStack input,
            ItemStack result,
            int cycles,
            BlockData particleBlockData
    ) {
        this(key, input, Map.of(result, 1.0), cycles, particleBlockData);
    }

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
        return results.keySet()
                .stream()
                .map(result -> (FluidOrItem) FluidOrItem.of(result))
                .toList();
    }

    @Override
    public @NotNull Gui display() {
        Gui.Builder.Normal gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # c # 1 2 3 #",
                        "# i # g # 4 5 6 #",
                        "# # # # # 7 8 9 #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('g', ItemButton.fromStack(BaseItems.GRINDSTONE))
                .addIngredient('i', ItemButton.fromStack(input))
                .addIngredient('c', GuiItems.progressCyclingItem(cycles * Grindstone.CYCLE_TIME_TICKS,
                        ItemStackBuilder.of(Material.CLOCK)
                                .name(net.kyori.adventure.text.Component.translatable(
                                        "pylon.pylonbase.guide.recipe.grindstone.time",
                                        PylonArgument.of("time", UnitFormat.SECONDS.format(cycles * Grindstone.CYCLE_TIME_TICKS / 20))
                                ))
                ));

        int i = 1;
        for (Map.Entry<ItemStack, Double> pair : results.entrySet()) {
            ItemStack stack = pair.getKey().clone();
            List<Component> lore = pair.getKey().lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(Component.newline());
            lore.add(Component.translatable(
                    "pylon.pylonbase.guide.recipe.grindstone.chance",
                    PylonArgument.of(
                            "chance",
                            UnitFormat.PERCENT.format(Math.round(pair.getValue() * 100)))
            ));
            stack.lore(lore);
            gui.addIngredient((char) ('0' + i), ItemButton.fromStack(stack));
            i++;
        }

        return gui.build();
    }
}