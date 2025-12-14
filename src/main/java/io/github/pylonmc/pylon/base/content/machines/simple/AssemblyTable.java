package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.base.recipes.AssemblyTableRecipe;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
public class AssemblyTable extends PylonBlock implements PylonEntityHolderBlock, PylonBreakHandler, PylonInteractBlock {
    public static final NamespacedKey CRAFTING_INVENTORY_KEY = baseKey("assembly_table_crafting_inventory");
    public static final NamespacedKey OUTPUT_INVENTORY_KEY = baseKey("assembly_table_output_inventory");

    public static final NamespacedKey DISPLAY_KEY = baseKey("assembly_table_display");
    public static final NamespacedKey CURRENT_RECIPE_KEY = baseKey("assembly_table_current_recipe");
    public static final NamespacedKey CURRENT_PROGRESS_KEY = baseKey("assembly_table_current_step");

    private final VirtualInventory craftInventory;
    private final VirtualInventory outputInventory;
    private final VirtualInventory stepDisplay = new VirtualInventory(1);

    private boolean displayPhase;
    private AssemblyTableRecipe currentRecipe;
    private AssemblyTableRecipe.ActionStep currentProgress;

    private int offset = 1;

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
        this.craftInventory.setPostUpdateHandler(this::updateInputGrid);

        this.outputInventory = new VirtualInventory(9);
        this.outputInventory.setPreUpdateHandler(this::cancelOutput);

        this.stepDisplay.setPreUpdateHandler(this::cancelEverything);

        this.displayPhase = false;

        this.currentRecipe = null;
        this.currentProgress = null;
        updateStep();
    }

    @SuppressWarnings("unused")
    public AssemblyTable(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        byte[] craftingSerialized = pdc.get(CRAFTING_INVENTORY_KEY, PersistentDataType.BYTE_ARRAY);
        this.craftInventory = VirtualInventory.deserialize(craftingSerialized);
        this.craftInventory.setPreUpdateHandler(this::cancelInput);
        this.craftInventory.setPostUpdateHandler(this::updateInputGrid);

        byte[] outputSerialized = pdc.get(OUTPUT_INVENTORY_KEY, PersistentDataType.BYTE_ARRAY);
        this.outputInventory = VirtualInventory.deserialize(outputSerialized);
        this.outputInventory.setPreUpdateHandler(this::cancelOutput);

        this.stepDisplay.setPreUpdateHandler(this::cancelEverything);

        this.displayPhase = pdc.get(DISPLAY_KEY, PersistentDataType.BOOLEAN);

        String currentRecipeString = pdc.get(CURRENT_RECIPE_KEY, PersistentDataType.STRING);
        NamespacedKey currentRecipeKey = currentRecipeString == null ? null : NamespacedKey.fromString(currentRecipeString);
        this.currentRecipe = currentRecipeKey == null ? null : AssemblyTableRecipe.RECIPE_TYPE.getRecipe(currentRecipeKey);

        Long currentStepLong = pdc.get(CURRENT_PROGRESS_KEY, PersistentDataType.LONG);
        this.currentProgress = currentStepLong == null ? null : new AssemblyTableRecipe.ActionStep(currentStepLong);
        updateStep();
    }

    //todo: entities don't load properly

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        byte[] craftingSerialized = craftInventory.serialize();
        byte[] outputSerialized = outputInventory.serialize();

        pdc.set(CRAFTING_INVENTORY_KEY, PersistentDataType.BYTE_ARRAY, craftingSerialized);
        pdc.set(OUTPUT_INVENTORY_KEY, PersistentDataType.BYTE_ARRAY, outputSerialized);
        pdc.set(DISPLAY_KEY, PersistentDataType.BOOLEAN, displayPhase);

        if (currentRecipe != null) {
            pdc.set(CURRENT_RECIPE_KEY, PersistentDataType.STRING, currentRecipe.getKey().toString());
        } else {
            pdc.remove(CURRENT_RECIPE_KEY);
        }

        if (currentProgress != null) {
            pdc.set(CURRENT_PROGRESS_KEY, PersistentDataType.LONG, currentProgress.toLong());
        } else {
            pdc.remove(CURRENT_PROGRESS_KEY);
        }
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

    public AssemblyTableRecipe.Step getStep() {
        return currentRecipe.steps().get(currentProgress.getStep());
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
            .addIngredient('#', GuiItems.backgroundBlack());

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
            //todo: message recipe change
            player.sendMessage("offset changed");
            offset++; // left click changes selected recipe, if any
            updateInputGrid();
        } else {
            progressRecipe(mainHand, player, true);
        }
    }

    //<editor-fold desc="Recipe handling">
    public void progressRecipe(@NotNull ItemStack item, Player player, boolean shouldDamage) {
        if (currentRecipe == null || currentProgress == null) return;

        PylonItem pylonItem = PylonItem.fromStack(item);
        NamespacedKey key = pylonItem != null ? pylonItem.getKey() : item.getType().getKey();

        AssemblyTableRecipe.Step current = this.getStep();
        if (!current.tool().equals(key)) {
            player.sendMessage("invalid tool");
            //todo: message that the tool is invalid
            return;
        }

        if (shouldDamage && current.damageConsume()) {
            PylonUtils.damageItem(item, 1, player.getWorld());
        }

        int newUsedAmount = currentProgress.getUsedAmount() + 1;
        int remainingAmount = current.uses() - newUsedAmount;
        if (remainingAmount == 0) {
            int newStep = currentProgress.getStep() + 1;
            if (newStep < currentRecipe.steps().size()) {
                //todo: message
                player.sendMessage("next step");
                currentProgress.setStep(newStep);
                updateStep();
            } else {
                //todo: message
                player.sendMessage("recipe completed");
                completeRecipe();
            }
        } else {
            //todo: message
            player.sendMessage("added progress");
            currentProgress.setUsedAmount(newUsedAmount);
            updateStep();
        }
    }

    public void completeRecipe() {
        this.displayPhase = false;

        clearInventory(this.craftInventory);
        updateInventory(this.outputInventory, currentRecipe.results());

        this.currentRecipe = null;
        this.currentProgress = null;

        updateStep();
        removeAllEntities();
    }
    //</editor-fold>

    private void updateStep() {
        updateStepItem();
        updateStepDisplay();
    }

    private void updateStepDisplay() {
        if (currentRecipe == null || currentProgress == null) {
            removeAllEntities();
            return;
        }

        AssemblyTableRecipe.Step current = this.getStep();
        current.removeDisplays().forEach(this::removeEntity);

        Location up = getBlock().getRelative(BlockFace.UP).getLocation()
            .toCenterLocation()
            .add(0, -0.495, 0);
        current.addDisplays().forEach(data -> {
            String name = data.name();
            double[] positions = data.position();
            double[] scale = data.scale();

            addEntityIfMissing(name, new BlockDisplayBuilder()
                .material(data.material())
                .transformation(new TransformBuilder()
                    .translate(positions[0], 0, positions[1])
                    .scale(scale[0], 0, scale[1])
                )
                .build(up)
            );

            if (data.mirrorX()) {
                addEntityIfMissing(name + "$mirror_x", new BlockDisplayBuilder()
                    .material(data.material())
                    .transformation(new TransformBuilder()
                        .translate(-positions[0], 0, positions[1])
                        .scale(scale[0], 0, scale[1])
                    )
                    .build(up)
                );
            }

            if (data.mirrorZ()) {
                addEntityIfMissing(name + "$mirror_z", new BlockDisplayBuilder()
                    .material(data.material())
                    .transformation(new TransformBuilder()
                        .translate(positions[0], 0, -positions[1])
                        .scale(scale[0], 0, scale[1])
                    )
                    .build(up)
                );
            }
        });
    }

    private void addEntityIfMissing(String name, Entity entity) {
        if (getHeldEntityUuid(name) != null) return;
        addEntity(name, entity);
    }

    //<editor-fold desc="Inventory event handlers">
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
            AssemblyTableRecipe.Step current = this.getStep();
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

    private void updateInputGrid() {
        this.offset = 1;
        this.currentRecipe = AssemblyTableRecipe.findRecipe(craftInventory.getItems(), offset);
        if (this.currentRecipe == null) {
            this.displayPhase = false;
            this.currentProgress = null;
            clearInventory(this.outputInventory);
        } else {
            this.displayPhase = true;
            this.currentProgress = new AssemblyTableRecipe.ActionStep(0, 0);
            updateInventory(this.outputInventory, currentRecipe.results());
        }

        updateStep();
    }

    private void clearInventory(VirtualInventory inventory) {
        for (int i = 0; i < 9; i++) {
            inventory.setItem(UpdateReason.SUPPRESSED, i, null);
        }
    }
    //</editor-fold>
}
