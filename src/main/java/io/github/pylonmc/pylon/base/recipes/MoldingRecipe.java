package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.rebar.config.ConfigSection;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.guide.button.ItemButton;
import io.github.pylonmc.rebar.i18n.PylonArgument;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import io.github.pylonmc.rebar.recipe.*;
import io.github.pylonmc.rebar.util.gui.GuiItems;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public record MoldingRecipe(
        NamespacedKey key,
        ItemStack input,
        ItemStack result,
        int moldingCycles
) implements PylonRecipe {

    public static final RecipeType<MoldingRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("molding")) {
        @Override
        protected @NotNull MoldingRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new MoldingRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK)
                            .asQuantity(section.getOrThrow("resultAmount", ConfigAdapter.INT)),
                    section.getOrThrow("cycles", ConfigAdapter.INT)
            );
        }
    };

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        return List.of(RecipeInput.of(input));
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(result));
    }

    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# # i # m # o # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('i', new ItemButton(input))
                .addIngredient('m', new ItemButton(ItemStackBuilder.of(BaseItems.BRICK_MOLD)
                        .clearLore()
                        .lore(Component.translatable("pylon.pylonbase.guide.recipe.molding")
                                .arguments(PylonArgument.of("molding-cycles", moldingCycles)))
                        .build()
                ))
                .addIngredient('o', new ItemButton(result))
                .build();
    }
}
