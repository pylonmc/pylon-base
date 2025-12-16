package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.base.recipes.BlueprintWorkbenchRecipe;
import io.github.pylonmc.pylon.base.recipes.intermediate.Step;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

@Getter
public class BlueprintWorkbench extends ProceduralCraftingTable<BlueprintWorkbenchRecipe> implements PylonBreakHandler, PylonInteractBlock {
    public static final NamespacedKey CRAFTING_INVENTORY_KEY = baseKey("blueprint_workbench_crafting_inventory");
    public static final NamespacedKey OUTPUT_INVENTORY_KEY = baseKey("blueprint_workbench_output_inventory");

    public static final NamespacedKey DISPLAY_KEY = baseKey("blueprint_workbench_display");

    private final VirtualInventory craftInventory;
    private final VirtualInventory outputInventory;
    private final VirtualInventory stepDisplay = new VirtualInventory(1);

    private boolean displayPhase;

    private int offset = 1;

    private Step.StateDisplay currentStateDisplay = null;

    @SuppressWarnings("unused")
    public BlueprintWorkbench(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        this.craftInventory = new VirtualInventory(9);
        this.craftInventory.setPreUpdateHandler(this::cancelInput);
        this.craftInventory.setPostUpdateHandler(this::updateInputGrid);

        this.outputInventory = new VirtualInventory(9);
        this.outputInventory.setPreUpdateHandler(this::cancelOutput);

        this.stepDisplay.setPreUpdateHandler(this::cancelEverything);

        this.displayPhase = false;

        this.currentStateDisplay = Step.StateDisplay.init(this);
        updateStep();
    }

    @SuppressWarnings("unused")
    public BlueprintWorkbench(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        this.craftInventory = pdc.get(CRAFTING_INVENTORY_KEY, PylonSerializers.VIRTUAL_INVENTORY);
        this.craftInventory.setPreUpdateHandler(this::cancelInput);
        this.craftInventory.setPostUpdateHandler(this::updateInputGrid);

        this.outputInventory = pdc.get(OUTPUT_INVENTORY_KEY, PylonSerializers.VIRTUAL_INVENTORY);
        this.outputInventory.setPreUpdateHandler(this::cancelOutput);

        this.stepDisplay.setPreUpdateHandler(this::cancelEverything);

        this.displayPhase = pdc.get(DISPLAY_KEY, PylonSerializers.BOOLEAN);

        this.currentStateDisplay = Step.StateDisplay.load(this);
    }

    @Override
    protected BlueprintWorkbenchRecipe deserializeRecipeKey(NamespacedKey key) {
        return BlueprintWorkbenchRecipe.RECIPE_TYPE.getRecipe(key);
    }

    @Override
    protected void postLoad() {
        updateStep();
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        super.write(pdc);
        pdc.set(CRAFTING_INVENTORY_KEY, PylonSerializers.VIRTUAL_INVENTORY, craftInventory);
        pdc.set(OUTPUT_INVENTORY_KEY, PylonSerializers.VIRTUAL_INVENTORY, outputInventory);
        pdc.set(DISPLAY_KEY, PylonSerializers.BOOLEAN, displayPhase);
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        for (ItemStack item : craftInventory.getItems()) {
            if (item != null) {
                drops.add(item);
            }
        }

        if (!displayPhase) {
            for (ItemStack item : outputInventory.getItems()) {
                if (item != null) {
                    drops.add(item);
                }
            }
        }
    }

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
            .addIngredient('$', stepDisplay)
            .addIngredient('#', GuiItems.background());

        return builder.build();
    }

    public void handleGui(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()
            || event.useInteractedBlock() == Event.Result.DENY) {
            return;
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        Window.single()
            .setGui(createGui())
            .setTitle(new AdventureComponentWrapper(getNameTranslationKey()))
            .setViewer(event.getPlayer())
            .build()
            .open();
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (this.currentStateDisplay == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getAction().isRightClick()) {
            handleGui(event);
            return;
        }

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.isEmpty()) {
            offset++; // left click changes selected recipe, if any
            updateInputGrid();
        } else {
            if (currentRecipe == null || currentProgress == null) return;
            boolean outcome = progressRecipe(mainHand, player, true);
            if (outcome) {
                event.setCancelled(true);
            }
        }
    }

    //<editor-fold desc="Recipe handling">
    public boolean progressRecipe(@NotNull ItemStack item, Player player, boolean shouldDamage) {
        Step.StepsHolder.Result result = this.currentRecipe.progressRecipe(currentProgress, getStep(), item, player, shouldDamage);

        switch (result) {
            case NEXT_STEP, NEXT_PROGRESS -> updateStep();
            case COMPLETED_RECIPE -> completeRecipe();

        }

        return result.isSuccess();
    }

    public void completeRecipe() {
        this.displayPhase = false;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.craftInventory.getUnsafeItem(i);
            if (stack != null) {
                stack.subtract();
                // invui bug? I am not sure
                if (stack.getAmount() == 0) {
                    this.craftInventory.setItem(UpdateReason.SUPPRESSED, i, null);
                }
            }
        }

        updateInventory(this.outputInventory, currentRecipe.results());

        this.currentRecipe = null;
        this.currentProgress = null;

        updateStep();
        removeAddedEntities();
    }
    //</editor-fold>

    public void updateStep() {
        updateStepItem();
        updateRecipeEntities();

        if (currentRecipe != null) {
            this.currentStateDisplay.update(getStep(), currentProgress);
        } else {
            this.currentStateDisplay.setVisibility(false);
        }
    }

    //<editor-fold desc="Inventory event handlers">
    private void cancelOutput(@NotNull ItemPreUpdateEvent event) {
        if (this.displayPhase || event.isAdd() || event.isSwap()) {
            event.setCancelled(true);
            return;
        }

        updateInputGrid();
    }

    private void cancelInput(@NotNull ItemPreUpdateEvent event) {
        if (!this.displayPhase && !outputInventory.isEmpty()) {
            event.setCancelled(true);
        }
    }

    private void cancelEverything(@NotNull ItemPreUpdateEvent event) {
        UpdateReason reason = event.getUpdateReason();
        if (reason == null || reason.equals(UpdateReason.SUPPRESSED)) return;

        event.setCancelled(true);
    }

    private void updateInputGrid(@NotNull ItemPostUpdateEvent event) {
        updateInputGrid();
    }
    //</editor-fold>

    //<editor-fold desc="Inventory updating methods">
    private void updateStepItem() {
        ItemStack stack;
        if (currentRecipe == null || currentProgress == null) {
            stack = GuiItems.background().getItemProvider().get();
        } else {
            Step current = this.getStep();
            stack = current.asStack(current.uses() - currentProgress.getUsedAmount());
        }

        this.stepDisplay.setItem(UpdateReason.SUPPRESSED, 0, stack);
    }

    private static void updateInventory(VirtualInventory toUpdate, List<ItemStack> itemList) {
        for (int i = 0; i < 9; i++) {
            ItemStack item;
            if (i < itemList.size()) {
                item = itemList.get(i);
            } else {
                item = null;
            }

            toUpdate.setItem(UpdateReason.SUPPRESSED, i, item);
        }
    }

    public void updateInputGrid() {
        this.offset = 1;
        this.currentRecipe = BlueprintWorkbenchRecipe.findRecipe(craftInventory.getItems(), offset);
        if (this.currentRecipe == null) {
            this.displayPhase = false;
            this.currentProgress = null;
            clearInventory(this.outputInventory);
        } else {
            this.displayPhase = true;
            this.currentProgress = new Step.ActionStep(0, 0);
            updateInventory(this.outputInventory, currentRecipe.results());
        }

        updateStep();
    }
    //</editor-fold>
}
