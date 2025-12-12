package io.github.pylonmc.pylon.base.recipes;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.content.machines.simple.Crucible;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.recipe.ConfigurableRecipeType;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public record CrucibleRecipe(
    @NotNull NamespacedKey key,
    @NotNull RecipeInput.Item input,
    @NotNull FluidOrItem.Fluid output
) implements PylonRecipe {

    private static List<ItemStack> HEAT_SOURCES = null;

    public static final RecipeType<CrucibleRecipe> RECIPE_TYPE = new ConfigurableRecipeType<>(baseKey("crucible")) {
        @Override
        protected @NotNull CrucibleRecipe loadRecipe(@NotNull NamespacedKey key, @NotNull ConfigSection section) {
            FluidOrItem output = section.getOrThrow("output", ConfigAdapter.FLUID_OR_ITEM);
            if (!(output instanceof FluidOrItem.Fluid fluidOutput)) {
                throw new IllegalArgumentException("In crucible recipe output must be a fluid.");
            }

            return new CrucibleRecipe(
                key,
                new RecipeInput.Item(section.getOrThrow("input-item", ConfigAdapter.RECIPE_INPUT_ITEM).getItems(), 1),
                fluidOutput
            );
        }
    };

    @Override
    public @NotNull List<@NotNull RecipeInput> getInputs() {
        return List.of(input);
    }

    @Override
    public @NotNull List<@NotNull FluidOrItem> getResults() {
        return List.of(output);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class HeatManager {
        public static final HeatManager INSTANCE = new HeatManager();

        private Set<NamespacedKey> HEATED_BLOCKS = null;
        public Set<NamespacedKey> getHeatedBlocks() {
            if (HEATED_BLOCKS == null) {
                HEATED_BLOCKS = new HashSet<>();
                for (Material material : Crucible.VANILLA_BLOCK_HEAT_MAP.keySet())  {
                    HEATED_BLOCKS.add(material.getKey());
                }

                for (PylonBlockSchema schema : PylonRegistry.BLOCKS) {
                    if (Crucible.HeatedBlock.class.isAssignableFrom(schema.getBlockClass())) {
                        HEATED_BLOCKS.add(schema.getKey());
                    }
                }
            }

            return HEATED_BLOCKS;
        }
    }

    public static List<ItemStack> getHeatSources() {
        if (HEAT_SOURCES == null) {
            HEAT_SOURCES = new ArrayList<>();
            for (NamespacedKey key : HeatManager.INSTANCE.getHeatedBlocks()) {
                if (key.getNamespace().equals("minecraft")) {
                    HEAT_SOURCES.add(makeItemVanilla(key));
                } else {
                    HEAT_SOURCES.add(makeItemPylon(key));
                }
            }
        }

        return HEAT_SOURCES;
    }

    @Override
    public @NotNull Gui display() {

        return Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# # # # i # # # #",
                "# # # # m # # o #",
                "# # # # h # # # #",
                "# # # # # # # # #"
            )
            .addIngredient('#', GuiItems.backgroundBlack())
            .addIngredient('i', ItemButton.from(input))
            .addIngredient('m', ItemButton.from(BaseItems.CRUCIBLE))
            .addIngredient('h', new ItemButton(getHeatSources()))
            .addIngredient('o', new FluidButton(output.amountMillibuckets(), output.fluid())
        ).build();
    }

    private static @NotNull ItemStack makeItemVanilla(@NotNull NamespacedKey materialKey) {
        Material material = Registry.MATERIAL.get(materialKey);
        if (material == null) {
            ItemStack stack = new ItemStack(Material.BARRIER);
            stack.setData(DataComponentTypes.ITEM_NAME, Component.text("ERROR: " + materialKey));
            return stack;
        }

        if (material.isItem()) return new ItemStack(material);

        ItemStack stack = new ItemStack(Material.BARRIER);
        stack.setData(DataComponentTypes.ITEM_NAME, Component.translatable(material.translationKey()));
        return stack;
    }

    private static @NotNull ItemStack makeItemPylon(@NotNull NamespacedKey blockKey) {
        PylonItemSchema item = PylonRegistry.ITEMS.get(blockKey);
        if (item == null) {
            PylonBlockSchema block = PylonRegistry.BLOCKS.get(blockKey);
            if (block == null) {
                throw new UnsupportedOperationException("Can't make pylon item, no block or item available for such key");
            }
            ItemStack stack = new ItemStack(Material.BARRIER);
            stack.setData(DataComponentTypes.ITEM_NAME, Component.text(block.getKey().toString()));
            return stack;
        }

        return item.getItemStack();
    }



    public boolean matches(ItemStack inputItem) {
        return input.matches(inputItem);
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
