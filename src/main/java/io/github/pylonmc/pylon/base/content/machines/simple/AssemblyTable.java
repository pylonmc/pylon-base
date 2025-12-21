package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.base.recipes.AssemblyTableRecipe;
import io.github.pylonmc.pylon.base.recipes.BlueprintWorkbenchRecipe;
import io.github.pylonmc.pylon.base.recipes.intermediate.Step;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AssemblyTable extends ProceduralCraftingTable<AssemblyTableRecipe> implements PylonBreakHandler, PylonInteractBlock {
    private final VirtualInventory stepDisplay = new VirtualInventory(1);

    private int offset = 1;

    @SuppressWarnings("unused")
    public AssemblyTable(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        this.craftInventory.setPostUpdateHandler(this::updateInputGrid);
        this.stepDisplay.setPreUpdateHandler(this::cancelEverything);

        updateStep();
    }

    @SuppressWarnings("unused")
    public AssemblyTable(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        this.craftInventory.setPostUpdateHandler(this::updateInputGrid);
        this.stepDisplay.setPreUpdateHandler(this::cancelEverything);
    }

    @Override
    protected AssemblyTableRecipe deserializeRecipeKey(NamespacedKey key) {
        return AssemblyTableRecipe.RECIPE_TYPE.getRecipe(key);
    }

    @Override
    public void updateStep() {
        updateStepItem();
        updateRecipeEntities();

        if (currentRecipe != null) {
            this.currentStateDisplay.update(getStep(), currentProgress);
        } else {
            this.currentStateDisplay.setVisibility(false);
        }
    }

    @Override
    public void completeRecipe() {
        Location above = getBlock().getRelative(BlockFace.UP).getLocation().toCenterLocation();
        currentRecipe.results().forEach(stack ->
            above.getWorld().spawn(above, Item.class, (item) -> item.setItemStack(stack))
        );

        List<ItemStack> requiredItems = new ArrayList<>(currentRecipe.ingredients());
        ItemStack[] unsafeItems = craftInventory.getUnsafeItems();
        for (int i = 0; i < unsafeItems.length; i++) {
            ItemStack item = unsafeItems[i];
            if (item == null) continue;

            Iterator<ItemStack> iterator = requiredItems.iterator();
            while (iterator.hasNext()) {
                ItemStack requirement = iterator.next();

                if (requirement.getAmount() > item.getAmount()) continue;
                if (!requirement.isSimilar(item)) continue;

                item.subtract(requirement.getAmount());
                if (item.getAmount() == 0) {
                    craftInventory.setItem(UpdateReason.SUPPRESSED, i, null);
                }

                iterator.remove();
                break;
            }
        }

        this.currentRecipe = null;
        this.currentProgress = null;

        updateInputGrid(true);
        removeAddedEntities();
    }

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

    private void cancelEverything(@NotNull ItemPreUpdateEvent event) {
        UpdateReason reason = event.getUpdateReason();
        if (reason == null || reason.equals(UpdateReason.SUPPRESSED)) return;

        event.setCancelled(true);
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        for (ItemStack item : craftInventory.getItems()) {
            if (item != null) {
                drops.add(item);
            }
        }
    }

    public @NotNull Gui createGui() {
        var builder = Gui.normal()
            .setStructure(
                "# # # # # # # # #",
                "# # # x x x # # #",
                "# # # x x x # $ #",
                "# # # x x x # # #",
                "# # # # # # # # #"
            )
            .addIngredient('x', craftInventory)
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
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (event.getAction().isRightClick()) {
            handleGui(event);
            return;
        }

        Player player = event.getPlayer();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (mainHand.isEmpty()) {
            offset++; // left click changes selected recipe, if any
            updateInputGrid(true);
        } else {
            if (currentRecipe == null || currentProgress == null) return;
            boolean outcome = progressRecipe(mainHand, player, true);
            if (outcome) {
                event.setCancelled(true);
            }
        }
    }

    private void updateInputGrid(@NotNull ItemPostUpdateEvent event) {
        updateInputGrid(false);
    }

    public void updateInputGrid(boolean preserveOffset) {
        if (!preserveOffset) {
            this.offset = 1;
        }

        this.currentRecipe = AssemblyTableRecipe.findRecipe(craftInventory.getItems(), offset);
        if (this.currentRecipe == null) {
            this.currentProgress = null;
        } else {
            this.currentProgress = new Step.ActionStep(0, 0);
        }

        updateStep();
    }
}
