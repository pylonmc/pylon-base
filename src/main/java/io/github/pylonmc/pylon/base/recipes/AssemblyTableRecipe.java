package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.config.ConfigSection;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public record AssemblyTableRecipe(
    NamespacedKey key,
    RecipeFormation recipe,
    List<ItemStack> results,
    List<Step> steps
) implements PylonRecipe {

    @Getter
    public static class RecipeFormation {
        private final String[] rows;
        private final Map<Character, RecipeChoice> ingredients = new HashMap<>();

        public RecipeFormation(String... shape) {
            Preconditions.checkNotNull(shape);
            Preconditions.checkArgument(shape.length > 0 && shape.length < 4);

            int lastLen = -1;
            for (String row : shape) {
                Preconditions.checkArgument(row != null, "Shape cannot have null rows");
                Preconditions.checkArgument(!row.isEmpty() && row.length() < 4, "Crafting rows should be 1, 2, or 3 characters, not ", row.length());

                Preconditions.checkArgument(lastLen == -1 || lastLen == row.length(), "Crafting recipes must be rectangular");
                lastLen = row.length();
            }

            rows = Arrays.copyOf(shape, shape.length);
        }

        public void setIngredient(char c, RecipeChoice choice) {
            ingredients.put(c, choice);
        }

        public boolean check(ItemStack[] array) {
            RecipeChoice[] grid = new RecipeChoice[9];
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    int position = x + y * 3;
                    RecipeChoice choice = getChoice(x, y);
                    grid[position] = choice;
                }
            }

            for (int i = 0; i < 9; i++) {
                RecipeChoice choice = grid[i];
                if (choice == null) continue;
                ItemStack stack = array[i];
                if (stack == null) stack = ItemStack.empty();

                if (!choice.test(stack)) {
                    return false;
                }
            }

            return true;
        }

        public RecipeChoice getChoice(int x, int y) {
            var character = this.rows[y].charAt(x);
            return this.ingredients.get(character);
        }

        public Item getDisplaySlot(int x, int y) {
            return ItemButton.from(getChoice(x, y));
        }
    }

    public static final RecipeType<AssemblyTableRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("assembly_table")) {
        @Override
        protected @NotNull AssemblyTableRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            List<ItemStack> results = section.getOrThrow("results", ConfigAdapter.LIST.from(ConfigAdapter.ITEM_STACK));

            // region Recipe
            var recipeSection = section.getSection("ingredients");
            Map<Character, RecipeInput.Item> ingredientKey = recipeSection.getOrThrow("key", ConfigAdapter.MAP.from(ConfigAdapter.CHAR, ConfigAdapter.RECIPE_INPUT_ITEM));

            List<String> pattern = recipeSection.getOrThrow("pattern", ConfigAdapter.LIST.from(ConfigAdapter.STRING));

            RecipeFormation recipe = new RecipeFormation(pattern.toArray(new String[0]));

            // Convert List<String> to String[]
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

    public static List<AssemblyTableRecipe> findRecipes(ItemStack[] items) {
        List<AssemblyTableRecipe> recipes = new ArrayList<>();
        for (AssemblyTableRecipe recipe : RECIPE_TYPE.getRecipes()) {
            if (recipe.recipe.check(items)) {
                recipes.add(recipe);
            }
        }

        return recipes;
    }

    public static AssemblyTableRecipe findRecipe(ItemStack[] items, int offset) {
        List<AssemblyTableRecipe> recipes = findRecipes(items);
        if (recipes.isEmpty()) return null;

        return recipes.get(offset % recipes.size());
    }

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        var exactChoiceList = recipe.getIngredients().values().stream()
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
            .addIngredient('$', this.getItemStep())
            .build();

        int height = recipe.getRows().length;
        int width = recipe.getRows()[0].length();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                gui.setItem(10 + x + 9 * y, recipe.getDisplaySlot(x, y));
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

    public Item getItemStep() {
        return new ItemButton(
            steps.stream()
                .map(Step::asStack)
                .toArray(ItemStack[]::new)
        );
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
            return asStack(uses);
        }

        public ItemStack asStack(int times) {
            ItemStack output;
            // todo: allow proper translation somewhere appropriate once we made sure this works
            if (tool.getNamespace().equals("minecraft")) {
                output = ItemStackBuilder.of(Registry.MATERIAL.get(tool))
                    .lore("Use it " + times + " times")
                    .build();
            } else {
                PylonItemSchema pis = PylonRegistry.ITEMS.get(tool);
                output = ItemStackBuilder.of(pis.getItemStack())
                    .lore("Use it " + times + " times")
                    .build();
            }

            return output;
        }
    }

    /**
     * Points to a specific step in a step array and the relative used amount
     */
    @Data
    @AllArgsConstructor
    public static class ActionStep {
        private int step;
        private int usedAmount;

        public long toLong() {
            return ((long) step << 32) | (usedAmount & 0xFFFFFFFFL);
        }

        public ActionStep(long packed) {
            this.step = (int) (packed >> 32);
            this.usedAmount = (int) packed;
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