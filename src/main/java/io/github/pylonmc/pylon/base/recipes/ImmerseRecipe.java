package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.content.building.sponge.PowerfulWaterSponge;
import io.github.pylonmc.pylon.base.content.building.sponge.WetPowerfulWaterSponge;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.ConfigurableRecipeType;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * This recipe represents a recipe that converts a **BLOCK** into another **BLOCK**
 * by immersing it in a fluid and this recipe is for display ONLY.
 * Example: Immersing a {@link PowerfulWaterSponge} in Water can result in {@link WetPowerfulWaterSponge}
 *          The actual code of the sponges is in {@link PowerfulWaterSponge#toDriedSponge(Block)}
 *
 * Note: To express vanilla water/lava, you're supposed to use `pylonbase:water/lava`
 *       instead of `minecraft:water/lava`
 *
 * @param ingredient The ingredient **BLOCK**
 * @param fluid      The fluid
 * @param result     The result **BLOCK**
 * @param chance     The chance of the result, ranged 0.0 to 1.0
 * @author balugaq
 */
public record ImmerseRecipe(
        @NotNull NamespacedKey key,
        @NotNull ItemStack ingredient,
        @NotNull PylonFluid fluid,
        @NotNull ItemStack result,
        float chance
) implements PylonRecipe {
    public static final RecipeType<ImmerseRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("immerse")) {
        @Override
        protected @NotNull ImmerseRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new ImmerseRecipe(
                    key,
                    section.getOrThrow("ingredient", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("fluid", ConfigAdapter.PYLON_FLUID),
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK),
                    section.get("chance", ConfigAdapter.FLOAT, 1.0f)
            );
        }
    };

    /**
     * Creates a new FireproofRuneRecipe with the given parameters.
     *
     * @param key    The namespaced key for this recipe
     * @param input  The ingredient item stack
     * @param result The result item stack
     * @return A new FireproofRuneRecipe instance
     * @throws IllegalArgumentException if ingredient or result is not an item
     */
    public static @NotNull ImmerseRecipe of(
            @NotNull NamespacedKey key,
            @NotNull ItemStack input,
            @NotNull PylonFluid fluid,
            @NotNull ItemStack result,
            float chance
    ) {
        Preconditions.checkArgument(input.getType().isItem(), "Input must be an item");
        Preconditions.checkArgument(result.getType().isItem(), "Result must be an item");

        return new ImmerseRecipe(key, input, fluid, result, chance);
    }


    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# # # i f o # # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('f', ItemStackBuilder.of(Material.BUCKET)
                        .name(Component.translatable(
                                "pylon.pylonbase.guide.recipe.immerse.name",
                                PylonArgument.of("fluid", Component.translatable("pylon.pylonbase.fluid." + fluid.getKey().getKey())))
                        )
                        .lore(Component.translatable(
                                "pylon.pylonbase.guide.recipe.immerse.chance",
                                PylonArgument.of("chance", UnitFormat.PERCENT.format(Math.round(chance * 100)))
                        ))
                )
                .addIngredient('i', ItemButton.from(ingredient))
                .addIngredient('o', ItemButton.from(result))
                .build();
    }

    /**
     * Return the namespaced identifier for this object.
     *
     * @return this object's key
     */
    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        return List.of(RecipeInput.of(ingredient));
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(result));
    }
}
