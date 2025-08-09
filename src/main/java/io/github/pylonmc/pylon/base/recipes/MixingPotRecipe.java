package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.ArrayList;
import java.util.List;

import static io.github.pylonmc.pylon.core.util.ItemUtils.isPylonSimilar;

/**
 * @param inputItems the items needed inside the mixing pot (max. 7) (respects item amount)
 * @param inputFluid the fluid that must be in the mixing pot already
 * @param inputFluidAmount the amount of fluid that must be in the mixing pot already
 * @param output the item or fluid that will be output
 * @param requiresEnrichedFire whether enriched fire must be placed below to do this recipe
 */
public record MixingPotRecipe(
        @NotNull NamespacedKey key,
        @NotNull List<ItemStack> inputItems,
        @NotNull PylonFluid inputFluid,
        double inputFluidAmount,
        @NotNull FluidOrItem output,
        boolean requiresEnrichedFire
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<MixingPotRecipe> RECIPE_TYPE = new RecipeType<>(
            new NamespacedKey(PylonBase.getInstance(), "mixing_pot")
    );

    static {
        PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
    }

    public boolean matches(
            List<ItemStack> input,
            boolean isEnrichedFire,
            PylonFluid fluid,
            double fluidAmount
    ) {
        if (requiresEnrichedFire && !isEnrichedFire
                || fluidAmount < this.inputFluidAmount - 1.0e-9
                || !fluid.equals(this.inputFluid)
        ) {
            return false;
        }

        for (ItemStack inputStack : this.inputItems) {
            boolean anyMatches = false;
            for (ItemStack stack : input) {
                if (isPylonSimilar(inputStack, stack) && stack.getAmount() >= inputStack.getAmount()) {
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
    public @NotNull List<FluidOrItem> getInputs() {
        List<FluidOrItem> inputs = new ArrayList<>();
        inputs.add(FluidOrItem.of(inputFluid, inputFluidAmount));
        inputs.addAll(inputItems.stream()
                .map(FluidOrItem::of)
                .toList()
        );
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
                .addIngredient('f', new FluidButton(inputFluid.getKey(), inputFluidAmount))
                .addIngredient('m', ItemButton.fromStack(BaseItems.MIXING_POT))
                .addIngredient('i', requiresEnrichedFire
                        ? ItemButton.fromStack(BaseItems.ENRICHED_NETHERRACK)
                        : GuiItems.background()
                );

        if (output instanceof FluidOrItem.Item item) {
            builder.addIngredient('o', ItemButton.fromStack(item.item()));
        }
        if (output instanceof FluidOrItem.Fluid fluid) {
            builder.addIngredient('o', new FluidButton(fluid.fluid().getKey(), fluid.amountMillibuckets()));
        }

        Gui gui = builder.build();

        int i = 0;
        for (ItemStack stack : inputItems) {
            gui.setItem(10 + ((i / 3) * 9) + i % 3, ItemButton.fromStack(stack));
            i++;
        }

        return gui;
    }
}