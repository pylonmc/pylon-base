package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.content.tools.Hammer;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.recipe.*;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.ArrayList;
import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

/**
 * @param input  the input item (setting the itemstack to have an amount that's not 1 will have no effect)
 * @param result the output item (respects amount)
 * @param level  the minimum hammer mining level
 * @param chance the chance to succeed per attempt
 */
public record HammerRecipe(
        @NotNull NamespacedKey key,
        @NotNull RecipeInput.Item input,
        @NotNull ItemStack result,
        @NotNull MiningLevel level,
        float chance
) implements PylonRecipe {

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    public static final RecipeType<HammerRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("hammer")) {

        private static final ConfigAdapter<MiningLevel> MINING_LEVEL_ADAPTER = ConfigAdapter.ENUM.from(MiningLevel.class);

        @Override
        protected @NotNull HammerRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            return new HammerRecipe(
                    key,
                    section.getOrThrow("input", ConfigAdapter.RECIPE_INPUT_ITEM),
                    section.getOrThrow("result", ConfigAdapter.ITEM_STACK),
                    section.getOrThrow("mining-level", MINING_LEVEL_ADAPTER),
                    section.getOrThrow("chance", ConfigAdapter.FLOAT)
            );
        }
    };

    @Override
    public @NotNull List<RecipeInput> getInputs() {
        return List.of(input);
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(result));
    }

    @Override
    public @NotNull Gui display() {
        return Gui.builder()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# # # i h o # # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('i', ItemButton.from(input))
                .addIngredient('h', new ItemButton(getHammers()))
                .addIngredient('o', ItemButton.from(result))
                .build();
    }

    public float getChanceFor(@NotNull MiningLevel hammerLevel) {
        if (!hammerLevel.isAtLeast(level)) {
            return 0f;
        }
        // Each tier is twice as likely to succeed as the previous one
        return chance * (1 << hammerLevel.getNumericalLevel() - level.getNumericalLevel());
    }

    private List<ItemStack> getHammers() {
        List<ItemStack> hammers = new ArrayList<>();
        for (PylonItemSchema itemSchema : PylonRegistry.ITEMS.getValues()) {
            ItemStack stack = itemSchema.getItemStack();
            PylonItem item = PylonItem.fromStack(stack);
            if (item instanceof Hammer hammer) {
                float chance = Math.min(1, getChanceFor(hammer.miningLevel));
                if (chance <= 0f) continue;
                List<Component> lore = stack.lore();
                Preconditions.checkNotNull(lore);
                lore.add(Component.empty());
                lore.add(Component.translatable("pylon.pylonbase.guide.recipe.hammer",
                        PylonArgument.of("chance", UnitFormat.PERCENT.format(chance * 100f).significantFigures(3))
                ));
                stack.lore(lore);
                hammers.add(stack);
            }
        }
        return hammers;
    }
}