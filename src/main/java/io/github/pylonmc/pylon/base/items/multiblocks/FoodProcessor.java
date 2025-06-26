package io.github.pylonmc.pylon.base.items.multiblocks;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInventoryBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.Keyed;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;
import static io.github.pylonmc.pylon.base.util.RecipeUtils.matchRecipeChoiceMap;
import static io.github.pylonmc.pylon.base.util.RecipeUtils.removeRecipeChoiceMapFromGui;

public class FoodProcessor extends PylonInventoryBlock implements PylonSimpleMultiblock {
    public static final NamespacedKey SIMPLE_KEY = pylonKey("food_processor_simple");
    public static final ItemStack SIMPLE_STACK = ItemStackBuilder.pylonItem(Material.DISPENSER, SIMPLE_KEY).build();
    private final Map<String, UUID> entities;

    public FoodProcessor(Block block, BlockCreateContext context){
        super(block);
        entities = new HashMap<>();
        spawnMultiblockGhosts();
    }

    public FoodProcessor(Block block, PersistentDataContainer pdc){
        super(block, pdc);
        entities = loadHeldEntities(pdc);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull UUID> getHeldEntities(){
        return entities;
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc){
        super.write(pdc);
        saveHeldEntities(pdc);
    }

    @Override
    public @NotNull Map<Vector3i, Component> getComponents(){
        return Map.of(new Vector3i(-1, 0, 0), new VanillaComponent(Material.PISTON),
                new Vector3i(1, 0, 0), new VanillaComponent(Material.PISTON),
                new Vector3i(0, 0, -1), new VanillaComponent(Material.PISTON),
                new Vector3i(0, 0, 1), new VanillaComponent(Material.PISTON),
                new Vector3i(0, 1, 0), new PylonComponent(FoodProcessorHandle.KEY));
    }

    @Override
    protected @NotNull Gui createGui(){
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

    void cook(){
        for(SimpleRecipe recipe : SimpleRecipe.RECIPE_TYPE.getRecipes()){
            if(matchRecipeChoiceMap(recipe.input, getItems())){
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
    ) implements Keyed {

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }

        public static final RecipeType<FoodProcessor.SimpleRecipe> RECIPE_TYPE = new RecipeType<>(
                new NamespacedKey(PylonBase.getInstance(), "simple_food_processor")
        );

        static {
            PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
        }
    }

    public static class FoodProcessorHandle extends PylonBlock implements PylonInteractableBlock {
        public static final NamespacedKey KEY = pylonKey("food_processor_handle");
        public static final ItemStack STACK = ItemStackBuilder.pylonItem(Material.LEVER, KEY).build();

        public FoodProcessorHandle(Block block, BlockCreateContext context){ super(block, context); }

        public FoodProcessorHandle(Block block, PersistentDataContainer pdc){ super(block, pdc); }

        @Override
        public void onInteract(@NotNull PlayerInteractEvent event) {
            //noinspection ConstantConditions no npe on getClickedBlock since event.getClickedBlock != null is checked by the function that calls onInteract
            FoodProcessor processor = BlockStorage.getAs(FoodProcessor.class, event.getClickedBlock().getRelative(BlockFace.DOWN));
            if(processor == null) return;
            processor.cook();
        }
    }
}
