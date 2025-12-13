package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.base.recipes.AssemblyTableRecipe;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

@Getter
public class AssemblyTable extends PylonBlock implements PylonGuiBlock {
    public static final NamespacedKey CRAFTING_INVENTORY_KEY = baseKey("assembly_table_crafting_inventory");
    public static final NamespacedKey OUTPUT_INVENTORY_KEY = baseKey("assembly_table_output_inventory");

    public static final NamespacedKey DISPLAY_KEY = baseKey("assembly_table_display");
    public static final NamespacedKey CURRENT_RECIPE_KEY = baseKey("assembly_table_current_recipe");
    public static final NamespacedKey CURRENT_STEP_KEY = baseKey("assembly_table_current_step");

    private final VirtualInventory craftInventory;
    private final VirtualInventory outputInventory;

    private boolean displayPhase;
    private AssemblyTableRecipe currentRecipe;
    private AssemblyTableRecipe.ActionStep currentStep;

    private int offset = 0;

    public static class Item extends PylonItem {
        public Item(@NotNull ItemStack stack) {
            super(stack);
        }
    }

    @SuppressWarnings("unused")
    public AssemblyTable(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        this.craftInventory = new VirtualInventory(9);
        this.craftInventory.setPreUpdateHandler(this::cancelInput);
        this.outputInventory = new VirtualInventory(9);
        this.outputInventory.setPreUpdateHandler(this::cancelOutput);
        this.displayPhase = false;

        this.currentRecipe = null;
        this.currentStep = null;
    }

    @SuppressWarnings("unused")
    public AssemblyTable(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        byte[] craftingSerialized = pdc.get(CRAFTING_INVENTORY_KEY, PersistentDataType.BYTE_ARRAY);
        this.craftInventory = VirtualInventory.deserialize(craftingSerialized);
        this.craftInventory.setPreUpdateHandler(this::cancelInput);

        byte[] outputSerialized = pdc.get(OUTPUT_INVENTORY_KEY, PersistentDataType.BYTE_ARRAY);
        this.outputInventory = VirtualInventory.deserialize(outputSerialized);
        this.outputInventory.setPreUpdateHandler(this::cancelOutput);

        this.displayPhase = pdc.get(DISPLAY_KEY, PersistentDataType.BOOLEAN);

        String currentRecipeString = pdc.get(CURRENT_RECIPE_KEY, PersistentDataType.STRING);
        NamespacedKey currentRecipeKey = currentRecipeString == null ? null : NamespacedKey.fromString(currentRecipeString);
        this.currentRecipe = currentRecipeKey == null ? null : AssemblyTableRecipe.RECIPE_TYPE.getRecipe(currentRecipeKey);

        Long currentStepLong = pdc.get(CURRENT_STEP_KEY, PersistentDataType.LONG);
        this.currentStep = currentStepLong == null ? null : new AssemblyTableRecipe.ActionStep(currentStepLong);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        byte[] craftingSerialized = craftInventory.serialize();
        byte[] outputSerialized = outputInventory.serialize();

        pdc.set(CRAFTING_INVENTORY_KEY, PersistentDataType.BYTE_ARRAY, craftingSerialized);
        pdc.set(OUTPUT_INVENTORY_KEY, PersistentDataType.BYTE_ARRAY, outputSerialized);
        pdc.set(DISPLAY_KEY, PersistentDataType.BOOLEAN, displayPhase);

        if (currentRecipe != null)
            pdc.set(CURRENT_RECIPE_KEY, PersistentDataType.STRING, currentRecipe.getKey().toString());

        if (currentStep != null)
            pdc.set(CURRENT_STEP_KEY, PersistentDataType.LONG, currentStep.toLong());
    }

    @Override
    public @NotNull Gui createGui() {
        var builder = Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# x x x # y y y #",
                "# x x x $ y y y #",
                "# x x x # y y y #",
                "# # # # # # # # #"
            )
            .addIngredient('x', craftInventory)
            .addIngredient('y', outputInventory)
            .addIngredient('$', getProcessStep())
            .addIngredient('#', GuiItems.backgroundBlack());

        return builder.build();
    }

    private @NotNull xyz.xenondevs.invui.item.Item getProcessStep() {
        // todo: check step
        return GuiItems.background();
    }

    private void cancelOutput(@NotNull ItemPreUpdateEvent event) {
        if (this.displayPhase || event.isAdd() || event.isSwap()) {
            event.setCancelled(true);
        }
    }

    private void cancelInput(@NotNull ItemPreUpdateEvent event) {
        if (!this.displayPhase && !outputInventory.isEmpty()) {
            event.setCancelled(true);
        }
    }
}
