package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.matchRecipeChoiceMap;
import static io.github.pylonmc.pylon.base.util.BaseUtils.removeRecipeChoiceMapFromGui;

public class MysticalFoodEnhancer extends PylonBlock implements PylonSimpleMultiblock, PylonGuiBlock {
    public MysticalFoodEnhancer(Block block, BlockCreateContext context) {
        super(block, context);
        spawnGhostBlocks();
    }

    public MysticalFoodEnhancer(Block block, PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Map<Vector3i, MultiblockComponent> getComponents() {
        return Map.of(new Vector3i(-1, 0, 0), new VanillaMultiblockComponent(Material.PISTON),
                new Vector3i(1, 0, 0), new VanillaMultiblockComponent(Material.PISTON),
                new Vector3i(0, 0, -1), new VanillaMultiblockComponent(Material.PISTON),
                new Vector3i(0, 0, 1), new VanillaMultiblockComponent(Material.PISTON),
                new Vector3i(0, 1, 0), new PylonMultiblockComponent(BaseKeys.MYSTICAL_FOOD_ENHANCER_HANDLE));
    }

    @Override
    @NotNull
    public Gui createGui() {
        return PagedGui.inventories()
                .setStructure(
                        "x x x",
                        "x x x",
                        "x x x"
                )
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addContent(new VirtualInventory(9))
                .build();
    }

    void cook() {
        for (SimpleRecipe recipe : SimpleRecipe.RECIPE_TYPE.getRecipes()) {
            if (matchRecipeChoiceMap(recipe.input, getItems())) {
                Location dropLoc = getBlock().getLocation();
                dropLoc.add(0, 1, 0);
                getBlock().getWorld().dropItem(dropLoc, recipe.output);
                removeRecipeChoiceMapFromGui(recipe.input, getGui());
                break;
            }
        }
    }

    public record SimpleRecipe(
            @NotNull NamespacedKey key,
            @NotNull Map<RecipeChoice, Integer> input,
            @NotNull ItemStack output
    ) implements PylonRecipe {
        public SimpleRecipe {
            if (input.size() > 7) {
                throw new IllegalArgumentException("input map of recipe " + key.toString() + " must be of size 7 or less");
            }
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }

        public static final RecipeType<MysticalFoodEnhancer.SimpleRecipe> RECIPE_TYPE = new RecipeType<>(
                BaseKeys.MYSTICAL_FOOD_ENHANCER_SIMPLE
        );

        static {
            PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
        }

        @Override
        public @NotNull List<@NotNull RecipeChoice> getInputItems() {
            List<@NotNull RecipeChoice> inputs = new ArrayList<RecipeChoice>();
            for (Map.Entry<RecipeChoice, Integer> entry : input.entrySet()) {
                for (int i = 0; i < entry.getValue(); i++) {
                    inputs.add(entry.getKey());
                }
            }
            return inputs;
        }

        @Override
        public @NotNull List<@NotNull ItemStack> getOutputItems() {
            return List.of(output);
        }

        @Override
        public @NotNull Gui display() {
            Gui recipeGui = Gui.normal()
                    .setStructure(
                            "x x x x x x x x x",
                            "x a b c d e f g x",
                            "x 1 2 3 4 5 6 7 x",
                            "x y y y y y y y x",
                            "x x x x o x x x x",
                            "x x x x x x x x x"
                    )
                    .addIngredient('x', GuiItems.backgroundBlack())
                    .addIngredient('y', ItemStack.of(Material.YELLOW_WOOL))
                    .addIngredient('o', output)
                    .build();
            int i = 1;
            for (Map.Entry<RecipeChoice, Integer> input : input.entrySet()) {
                ItemStack amountStack = ItemStack.of(Material.WHITE_STAINED_GLASS);
                amountStack.setAmount(input.getValue());
                recipeGui.setItem(i, 1, ItemButton.fromStack(amountStack));
                recipeGui.setItem(i, 2, ItemButton.fromChoice(input.getKey()));
                i++;
            }
            for (; i <= 7; i++) {
                recipeGui.setItem(i, 1, GuiItems.background());
                recipeGui.setItem(i, 2, GuiItems.background());
            }
            return recipeGui;
        }
    }

    public static class MysticalFoodEnhancerHandle extends PylonBlock implements PylonInteractableBlock {

        public MysticalFoodEnhancerHandle(Block block, BlockCreateContext context) {
            super(block, context);
        }

        public MysticalFoodEnhancerHandle(Block block, PersistentDataContainer pdc) {
            super(block, pdc);
        }

        @Override
        public void onInteract(@NotNull PlayerInteractEvent event) {
            //noinspection ConstantConditions no npe on getClickedBlock since event.getClickedBlock != null is checked by the function that calls onInteract
            MysticalFoodEnhancer processor = BlockStorage.getAs(MysticalFoodEnhancer.class, event.getClickedBlock().getRelative(BlockFace.DOWN));
            if (processor == null) return;
            processor.cook();
        }
    }
}
