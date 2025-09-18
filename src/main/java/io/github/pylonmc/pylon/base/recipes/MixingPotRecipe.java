package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.recipe.*;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.ArrayList;
import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * @param inputItems the items needed inside the mixing pot (max. 7) (respects item amount)
 * @param inputFluid the fluid that must be in the mixing pot already
 * @param output the item or fluid that will be output
 * @param requiresEnrichedFire whether enriched fire must be placed below to do this recipe
 */
public record MixingPotRecipe(
        @NotNull NamespacedKey key,
        @NotNull List<RecipeInput.Item> inputItems,
        @NotNull RecipeInput.Fluid inputFluid,
        @NotNull FluidOrItem output,
        boolean requiresEnrichedFire
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<MixingPotRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("mixing_pot")) {
        @Override
        protected @NotNull MixingPotRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new MixingPotRecipe(
                    key,
                    section.getOrThrow("input-items", ConfigAdapter.LIST.from(ConfigAdapter.RECIPE_INPUT_ITEM)),
                    section.getOrThrow("input-fluid", ConfigAdapter.RECIPE_INPUT_FLUID),
                    section.getOrThrow("output", ConfigAdapter.FLUID_OR_ITEM),
                    section.get("requires-enriched-fire", ConfigAdapter.BOOLEAN, false)
            );
        }
    };

    public boolean matches(
            List<ItemStack> inputItems,
            boolean isEnrichedFire,
            PylonFluid fluid,
            double fluidAmount
    ) {
        if (requiresEnrichedFire && !isEnrichedFire || !this.inputFluid.matches(fluid, fluidAmount)) {
            return false;
        }

        for (RecipeInput.Item input : this.inputItems) {
            boolean anyMatches = false;
            for (ItemStack stack : inputItems) {
                if (input.matches(stack)) {
                    anyMatches = true;
                    break;
                }
            }
            if (!anyMatches) {
                return false;
            }
        }

        return true;
    }

    @Override
    public @NotNull List<RecipeInput> getInputs() {
        List<RecipeInput> inputs = new ArrayList<>(inputItems);
        inputs.add(inputFluid);
        return inputs;
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return List.of(output);
    }

    @Override
    public @NotNull Gui display() {
        Preconditions.checkState(inputItems.size() <= 7);
        Gui.Builder.Normal builder = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . . # f # # #",
                        "# . . . # m # o #",
                        "# . . . # i # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('f', new FluidButton(inputFluid))
                .addIngredient('m', ItemButton.from(BaseItems.MIXING_POT))
                .addIngredient('i', requiresEnrichedFire
                        ? ItemButton.from(BaseItems.ENRICHED_NETHERRACK)
                        : GuiItems.background()
                );

        builder.addIngredient(
                'o',
                switch (output) {
                    case FluidOrItem.Item item -> ItemButton.from(item.item());
                    case FluidOrItem.Fluid fluid -> new FluidButton(fluid.amountMillibuckets(), fluid.fluid());
                    default -> throw new AssertionError();
                }
        );

        Gui gui = builder.build();

        int i = 0;
        for (RecipeInput.Item input : inputItems) {
            gui.setItem(10 + ((i / 3) * 9) + i % 3, ItemButton.from(input));
            i++;
        }

        return gui;
    }
}