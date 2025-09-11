package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.content.machines.smelting.PitKiln;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.recipe.ConfigurableRecipeType;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.UnclickableInventory;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public record PitKilnRecipe(
        @NotNull NamespacedKey key,
        @NotNull List<ItemStack> input,
        @NotNull List<ItemStack> output
) implements PylonRecipe {

    public static final RecipeType<PitKilnRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("pit_kiln")) {
        @Override
        protected @NotNull PitKilnRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new PitKilnRecipe(
                    key,
                    section.getOrThrow("inputs", ConfigAdapter.LIST.from(ConfigAdapter.ITEM_STACK)),
                    section.getOrThrow("outputs", ConfigAdapter.LIST.from(ConfigAdapter.ITEM_STACK))
            );
        }
    };

    public PitKilnRecipe {
        Preconditions.checkArgument(!input.isEmpty(), "Input cannot be empty");
        Preconditions.checkArgument(input.size() <= PitKiln.CAPACITY, "Input cannot exceed the pit kiln's capacity of %s".formatted(PitKiln.CAPACITY));
        Preconditions.checkArgument(!output.isEmpty(), "Output cannot be empty");
        Preconditions.checkArgument(output.size() <= input.size(), "Output cannot exceed input size");
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getInputs() {
        return input.stream().map(FluidOrItem::of).toList();
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
        return output.stream().map(FluidOrItem::of).toList();
    }

    @Override
    public @NotNull Gui display() {
        UnclickableInventory inputs = new UnclickableInventory(6);
        for (ItemStack item : input) {
            inputs.addItem(item);
        }

        UnclickableInventory outputs = new UnclickableInventory(6);
        for (ItemStack item : output) {
            outputs.addItem(item);
        }

        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# , , # # # . . #",
                        "# , , # p # . . #",
                        "# , , # # # . . #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient(',', inputs)
                .addIngredient('.', outputs)
                .addIngredient('p', BaseItems.PIT_KILN)
                .build();
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
