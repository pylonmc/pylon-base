package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.content.machines.simple.Grindstone;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.ConfigurableRecipeType;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.WeightedSet;
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

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * @param input the input item (respects amount)
 * @param results the result items and their corresponding probabilities
 *                (respects item amount) (maximum 9 items)
 * @param cycles the number of full rotations needed to complete the recipe
 * @param particleBlockData the block data to use for the particles shown while grinding
 */
public record GrindstoneRecipe(
        @NotNull NamespacedKey key,
        @NotNull ItemStack input,
        @NotNull WeightedSet<ItemStack> results,
        int cycles,
        @NotNull BlockData particleBlockData
) implements PylonRecipe {

    public GrindstoneRecipe(
            NamespacedKey key,
            ItemStack input,
            ItemStack result,
            int cycles,
            BlockData particleBlockData
    ) {
        this(key, input, new WeightedSet<>(result, 1f), cycles, particleBlockData);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<GrindstoneRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("grindstone")) {
        @Override
        protected @NotNull GrindstoneRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new GrindstoneRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("results", ConfigAdapter.WEIGHTED_SET.from(ConfigAdapter.ITEM_STACK)),
                    section.getOrThrow("cycles", ConfigAdapter.INT),
                    section.getOrThrow("particle-data", ConfigAdapter.BLOCK_DATA)
            );
        }
    };

    public int timeTicks() {
        return cycles * Grindstone.CYCLE_DURATION_TICKS;
    }

    @Override
    public @NotNull List<FluidOrItem> getInputs() {
        return List.of(FluidOrItem.of(input));
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return results.getElements().stream()
                .map(FluidOrItem::of)
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
                .addIngredient('c', GuiItems.progressCyclingItem(cycles * Grindstone.CYCLE_DURATION_TICKS,
                        ItemStackBuilder.of(Material.CLOCK)
                                .name(net.kyori.adventure.text.Component.translatable(
                                        "pylon.pylonbase.guide.recipe.grindstone.timeTicks",
                                        PylonArgument.of("timeTicks", UnitFormat.SECONDS.format(cycles * Grindstone.CYCLE_DURATION_TICKS / 20))
                                ))
                ));

        int i = 1;
        for (WeightedSet.WeightedElement<ItemStack> element : results) {
            ItemStack stack = element.element().clone();
            List<Component> lore = element.element().lore();
            if (lore == null) {
                lore = new ArrayList<>();
            }
            lore.add(Component.newline());
            lore.add(Component.translatable(
                    "pylon.pylonbase.guide.recipe.grindstone.chance",
                    PylonArgument.of(
                            "chance",
                            UnitFormat.PERCENT.format(Math.round(element.weight() * 100)))
            ));
            stack.lore(lore);
            gui.addIngredient((char) ('0' + i), ItemButton.fromStack(stack));
            i++;
        }

        return gui.build();
    }
}