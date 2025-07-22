package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.util.Either;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.Map;

/**
 * Maximum 7 input items
 */
public record MixingPotRecipe(
        @NotNull NamespacedKey key,
        @NotNull Map<RecipeChoice, Integer> input,
        @NotNull Either<ItemStack, PylonFluid> output,
        boolean requiresEnrichedFire,
        @NotNull PylonFluid fluid,
        double fluidAmount
) implements PylonRecipe {

    public MixingPotRecipe(
            @NotNull NamespacedKey key,
            @NotNull Map<RecipeChoice, Integer> input,
            @NotNull ItemStack output,
            boolean requiresEnrichedFire,
            @NotNull PylonFluid fluid,
            double fluidAmount
    ) {
        this(key, input, new Either.Left<>(output), requiresEnrichedFire, fluid, fluidAmount);
    }

    public MixingPotRecipe(
            @NotNull NamespacedKey key,
            @NotNull Map<RecipeChoice, Integer> input,
            @NotNull PylonFluid output,
            boolean requiresEnrichedFire,
            @NotNull PylonFluid fluid,
            double fluidAmount
    ) {
        this(key, input, new Either.Right<>(output), requiresEnrichedFire, fluid, fluidAmount);
    }

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
        if (requiresEnrichedFire && !isEnrichedFire) {
            return false;
        }

        // stupid floating point
        if (fluidAmount < this.fluidAmount - 1.0e-5 || !fluid.equals(this.fluid)) {
            return false;
        }

        for (Map.Entry<RecipeChoice, Integer> choice : this.input.entrySet()) {
            boolean anyMatches = false;
            for (ItemStack stack : input) {
                if (choice.getKey().test(stack) && stack.getAmount() >= choice.getValue()) {
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
    public @NotNull List<@NotNull RecipeChoice> getInputItems() {
        return input.keySet().stream().toList();
    }

    @Override
    public @NotNull List<@NotNull PylonFluid> getInputFluids() {
        return List.of(fluid);
    }

    @Override
    public @NotNull List<@NotNull ItemStack> getOutputItems() {
        if (output instanceof Either.Left<ItemStack, PylonFluid>(ItemStack left)) {
            return List.of(left);
        }
        return List.of();
    }

    @Override
    public @NotNull List<@NotNull PylonFluid> getOutputFluids() {
        if (output instanceof Either.Right<ItemStack, PylonFluid>(PylonFluid right)) {
            return List.of(right);
        }
        return List.of();
    }

    @Override
    public @NotNull Gui display() {
        Preconditions.checkState(input.size() <= 7);
        Gui.Builder.Normal builder = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . . # f # # #",
                        "# . . . # m # o #",
                        "# . . . # i # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('f', new FluidButton(fluid.getKey(), fluidAmount))
                .addIngredient('m', ItemButton.fromStack(BaseItems.MIXING_POT))
                .addIngredient('i', requiresEnrichedFire
                        ? ItemButton.fromStack(BaseItems.ENRICHED_NETHERRACK)
                        : GuiItems.background()
                );

        if (output instanceof Either.Left<ItemStack, PylonFluid>(ItemStack left)) {
            builder.addIngredient('o', ItemButton.fromStack(left));
        }
        if (output instanceof Either.Right<ItemStack, PylonFluid>(PylonFluid right)) {
            builder.addIngredient('o', new FluidButton(right.getKey(), fluidAmount));
        }

        Gui gui = builder.build();

        int i = 0;
        for (Map.Entry<RecipeChoice, Integer> entry : input.entrySet()) {
            ItemStack stack = entry.getKey().getItemStack().clone();
            stack.setAmount(entry.getValue());
            gui.setItem(10 + ((i / 3) * 9) + i % 3, ItemButton.fromStack(stack));
            i++;
        }

        return gui;
    }
}