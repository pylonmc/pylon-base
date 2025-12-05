package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.PylonConfig;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.config.adapter.MapConfigAdapter;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.ConfigurableRecipeType;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public record AssemblyTableRecipe(
    NamespacedKey key,
    ShapedRecipe recipe,
    List<ItemStack> results,
    List<Step> steps
) implements PylonRecipe {

    public static final RecipeType<AssemblyTableRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("assembly_table")) {
        @Override
        protected @NotNull AssemblyTableRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            List<ItemStack> results = section.getOrThrow("results", ConfigAdapter.LIST.from(ConfigAdapter.ITEM_STACK));

            // region Recipe
            Map<Character, RecipeInput.Item> ingredientKey = section.getOrThrow("key", ConfigAdapter.MAP.from(ConfigAdapter.CHAR, ConfigAdapter.RECIPE_INPUT_ITEM));

            List<String> pattern = section.getOrThrow("pattern", ConfigAdapter.LIST.from(ConfigAdapter.STRING));

            ItemStack result = section.getOrThrow("result", ConfigAdapter.ITEM_STACK);

            ShapedRecipe recipe = new ShapedRecipe(key, result);

            // Convert List<String> to String[]
            recipe.shape(pattern.toArray(new String[0]));

            for (var entry : ingredientKey.entrySet()) {
                Character character = entry.getKey();
                RecipeInput.Item item = entry.getValue();
                recipe.setIngredient(character, new RecipeChoice.ExactChoice(
                    item.getItems().stream()
                        .map(it -> it.createItemStack().asQuantity(item.getAmount()))
                        .toList()
                    )
                );
            }

            // Optional fields
            CraftingBookCategory category = section.get("category", ConfigAdapter.ENUM.from(CraftingBookCategory.class));
            if (category != null) {
                recipe.setCategory(category);
            }

            String group = section.get("group", ConfigAdapter.STRING);
            if (group != null) {
                recipe.setGroup(group);
            }

            //endregion

            List<Step> steps = section.getOrThrow("steps", ConfigAdapter.LIST.from(Step.ADAPTER));

            return new AssemblyTableRecipe(
                key,
                recipe,
                results,
                steps
            );
        }
    };

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        var exactChoiceList = recipe.getChoiceMap().values().stream()
            .map(RecipeChoice.ExactChoice.class::cast)
            .toList();

        List<RecipeInput> result = new ArrayList<>(9);
        for (var exactChoice : exactChoiceList) {
            result.add(new RecipeInput.Item(1, exactChoice.getChoices().toArray(ItemStack[]::new)));
        }

        return result;
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
        return results.stream().map(FluidOrItem::of).toList();
    }

    @Override
    public @NotNull Gui display() {

        var gui = Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# 0 3 6 # a d g #",
                "# 1 4 7 $ b e h #",
                "# 2 5 8 # c f i #",
                "# # # # # # # # #"
            )
            .addIngredient('#', GuiItems.backgroundBlack())
            .addIngredient('p', BaseItems.PIT_KILN)
            .addIngredient('$', new ItemButton(
                steps.stream()
                    .map(Step::asStack)
                    .toArray(ItemStack[]::new)
                )
            )
            .build();

        int height = recipe.getShape().length;
        int width = recipe.getShape()[0].length();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                gui.setItem(10 + x + 9 * y, getDisplaySlot(recipe, x, y));
            }
        }

        int amount = results.size();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (amount == 0) {
                    break;
                }

                gui.setItem(14 + x + 9 * y, ItemButton.from(
                    results.get(--amount)
                ));
            }

            if (amount == 0) {
                break;
            }
        }

        return gui;
    }

    private Item getDisplaySlot(ShapedRecipe recipe, int x, int y) {
        var character = recipe.getShape()[y].charAt(x);
        return ItemButton.from(recipe.getChoiceMap().get(character));
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    /**
     * @param damageConsume
     *   If true and item has durability -> damage
     *   If true and item has no durability -> consume item
     *   If false -> do nothing
     */
    public record Step(NamespacedKey tool, int uses, boolean damageConsume, List<String> removeDisplays, List<DisplayData> addDisplays) {
        public static final ConfigAdapter<Step> ADAPTER = new ConfigAdapter<>() {
            @Override
            public @NotNull Type getType() {
                return Step.class;
            }

            @Override
            public Step convert(@NotNull Object value) {
                var map = MapConfigAdapter.STRING_TO_ANY.convert(value);
                return new Step(
                    ConfigAdapter.NAMESPACED_KEY.convert(map.get("tool")),
                    ConfigAdapter.INT.convert(map.get("uses") != null ? map.get("uses") : 1),
                    ConfigAdapter.BOOLEAN.convert(map.get("damageConsume") == Boolean.TRUE),
                    ConfigAdapter.LIST.from(ConfigAdapter.STRING).convert(map.get("removeDisplays") != null ? map.get("removeDisplays") : Map.of()),
                    ConfigAdapter.LIST.from(DisplayData.ADAPTER).convert(map.get("addDisplays") != null ? map.get("addDisplays") : Map.of())
                );
            }
        };

        public ItemStack asStack() {
            ItemStack output;
            // todo: allow proper translation somewhere appropriate once we made sure this works
            if (tool.getNamespace().equals("minecraft")) {
                output = ItemStackBuilder.of(Registry.MATERIAL.get(tool))
                    .lore("Use it " + uses + " times")
                    .build();
            } else {
                PylonItemSchema pis = PylonRegistry.ITEMS.get(tool);
                output = ItemStackBuilder.of(pis.getItemStack())
                    .lore("Use it " + uses + " times")
                    .build();
            }

            return output;
        }
    }

    public record DisplayData(String name, Material material, double[] position, double[] scale) {
        public static final ConfigAdapter<DisplayData> ADAPTER = new ConfigAdapter<>() {
            @Override
            public @NotNull Type getType() {
                return DisplayData.class;
            }

            @Override
            public DisplayData convert(@NotNull Object value) {
                var map = MapConfigAdapter.STRING_TO_ANY.convert(value);
                String name = ConfigAdapter.STRING.convert(map.get("name"));
                Material material = ConfigAdapter.MATERIAL.convert(map.get("material"));

                var doubleListAdapter = ConfigAdapter.LIST.from(ConfigAdapter.DOUBLE);
                List<Double> positionList = doubleListAdapter.convert(map.get("position"));
                List<Double> scaleList = doubleListAdapter.convert(map.get("scale"));

                if (positionList.size() != 2) throw new IllegalArgumentException("DisplayData's position must be a 2 double list");
                if (scaleList.size() != 2) throw new IllegalArgumentException("DisplayData's scale must be a 2 double list");

                double[] position = new double[] {positionList.get(0), positionList.get(1)};
                double[] scale = new double[] {scaleList.get(0), scaleList.get(1)};

                return new DisplayData(
                    name,
                    material,
                    position,
                    scale
                );
            }
        };
    }
}