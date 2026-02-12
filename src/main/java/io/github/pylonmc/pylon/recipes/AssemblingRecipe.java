package io.github.pylonmc.pylon.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.PylonItems;
import io.github.pylonmc.rebar.config.ConfigSection;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.guide.button.ItemButton;
import io.github.pylonmc.rebar.recipe.*;
import io.github.pylonmc.rebar.util.gui.GuiItems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;
import org.joml.Vector3d;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.util.PylonUtils.pylonKey;

public record AssemblingRecipe(
        NamespacedKey key,
        List<RecipeInput.Item> inputs,
        List<ItemStack> results,
        List<Step> steps
) implements RebarRecipe {

    public record AddDisplay(
            String name,
            ItemStack stack,
            Vector2d position,
            Vector3d scale
    ) {}

    public record Step(
            String tool,
            ItemStack icon,
            int clicks,
            List<AddDisplay> addDisplays,
            List<String> removeDisplays
    ) {}

    public static final RecipeType<AssemblingRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(pylonKey("assembling")) {
        @Override
        protected @NotNull AssemblingRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            List<Step> steps = new ArrayList<>();
            for (ConfigSection stepSection : section.getSectionOrThrow("steps").getSections()) {

                List<AddDisplay> addDisplays = new ArrayList<>();
                List<Map<String, Object>> addDisplaysSection = stepSection.get("name", ConfigAdapter.LIST_OF_SECTIONS);
                if (addDisplaysSection != null) {
                    for (ConfigSection addDisplaySection : addDisplaysSection) {
                        addDisplays.add(new AddDisplay(
                                addDisplaySection.getOrThrow("name", ConfigAdapter.STRING),
                                addDisplaySection.getOrThrow("item", ConfigAdapter.ITEM_STACK),
                                addDisplaySection.getOrThrow("position", ConfigAdapter.VECTOR_2D),
                                addDisplaySection.getOrThrow("scale", ConfigAdapter.VECTOR_3D)
                        ));
                    }
                }

                steps.add(new Step(
                        stepSection.getOrThrow("name", ConfigAdapter.STRING),
                        stepSection.getOrThrow("item", ConfigAdapter.ITEM_STACK),
                        stepSection.getOrThrow("clicks", ConfigAdapter.INT),
                        addDisplays,
                        stepSection.get("remove_displays", ConfigAdapter.LIST.from(ConfigAdapter.STRING))
                ));
            }

            List<RecipeInput.Item> inputs = section.getOrThrow("inputs", ConfigAdapter.LIST.from(ConfigAdapter.RECIPE_INPUT_ITEM));
            List<ItemStack> results = section.getOrThrow("results", ConfigAdapter.LIST.from(ConfigAdapter.ITEM_STACK));

            Preconditions.checkArgument(
                    inputs.size() <= 7,
                    "Assembling recipes can have at most 7 inputs - " + key
            );
            Preconditions.checkArgument(
                    results.size() <= 5,
                    "Assembling recipes can have at most 5 results - " + key
            );

            return new AssemblingRecipe(key, inputs, results, steps);
        }
    };

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        return new ArrayList<>(inputs);
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
            return results.stream()
                    .map(FluidOrItem::of)
                    .toList();
    }

    public static @Nullable AssemblingRecipe findRecipe(ItemStack[] items) {
        for (AssemblingRecipe recipe : RECIPE_TYPE.getRecipes()) {

            boolean hasAllIngredients = true;
            for (RecipeInput.Item requiredItem : recipe.inputs) {
                boolean hasIngredient = false;
                for (ItemStack item : items) {
                    if (requiredItem.matches(item)) {
                        hasIngredient = true;
                        break;
                    }
                }

                if (!hasIngredient) {
                    Bukkit.getLogger().severe(requiredItem.getRepresentativeItem().getType().toString());
                    hasAllIngredients = false;
                }
            }

            if (hasAllIngredients) {
                return recipe;
            }
        }

        return null;
    }

    @Override
    public @Nullable Gui display() {
        var gui = Gui.builder()
                .setStructure(
                        "I i i i i i i i I",
                        "# O o o o o o O #",
                        "# # # # # # # # #",
                        "# # # # t # # # #",
                        "# # # # # # # # #",
                        "# s s s s s s s #"
                )
                .addIngredient('I', GuiItems.input())
                .addIngredient('O', GuiItems.output())
                .addIngredient('t', PylonItems.ASSEMBLY_TABLE)
                .build();

        for (int i = 0; i < inputs.size(); i++) {
            RecipeInput.Item input = inputs.get(i);
            if (input != null) {
                gui.setItem(i + 1, 1, new ItemButton(input.getRepresentativeItem()));
            }
        }

        for (int i = 0; i < steps.size(); i++) {
            Step step = steps.get(i);
            if (step != null) {
                gui.setItem(i + 1, 3, Item.simple(step.icon));
            }
        }

        for (int i = 0; i < results.size(); i++) {
            ItemStack result = results.get(i);
            if (result != null) {
                gui.setItem(i + 2, 5, new ItemButton(result));
            }
        }

        return gui;
    }
}
