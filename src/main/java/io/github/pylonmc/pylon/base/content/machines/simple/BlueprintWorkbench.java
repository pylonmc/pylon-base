package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.base.recipes.BlueprintWorkbenchRecipe;
import io.github.pylonmc.pylon.base.recipes.intermediate.Step;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Bukkit;
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
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.ItemPostUpdateEvent;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.window.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

@Getter
public class BlueprintWorkbench extends PylonBlock implements PylonEntityHolderBlock, PylonBreakHandler, PylonInteractBlock {
    public static final NamespacedKey CRAFTING_INVENTORY_KEY = baseKey("blueprint_workbench_crafting_inventory");
    public static final NamespacedKey OUTPUT_INVENTORY_KEY = baseKey("blueprint_workbench_output_inventory");

    public static final NamespacedKey DISPLAY_KEY = baseKey("blueprint_workbench_display");
    public static final NamespacedKey CURRENT_RECIPE_KEY = baseKey("blueprint_workbench_current_recipe");
    public static final NamespacedKey CURRENT_PROGRESS_KEY = baseKey("blueprint_workbench_current_step");

    private final VirtualInventory craftInventory;
    private final VirtualInventory outputInventory;
    private final VirtualInventory stepDisplay = new VirtualInventory(1);

    private boolean displayPhase;
    private BlueprintWorkbenchRecipe currentRecipe;
    private Step.ActionStep currentProgress;

    private int offset = 1;

    private Step.StateDisplay currentStateDisplay = null;

    public static class Item extends PylonItem {
        public Item(@NotNull ItemStack stack) {
            super(stack);
        }
    }

    @SuppressWarnings("unused")
    public BlueprintWorkbench(@NotNull Block block, @NotNull BlockCreateContext context) {
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

        this.currentStateDisplay = Step.StateDisplay.init(this);
        updateStep();
    }

    @SuppressWarnings("unused")
    public BlueprintWorkbench(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        this.craftInventory = pdc.get(CRAFTING_INVENTORY_KEY, PylonSerializers.VIRTUAL_INVENTORY);
        this.craftInventory.setPreUpdateHandler(this::cancelInput);
        this.craftInventory.setPostUpdateHandler(this::updateInputGrid);

        this.outputInventory = pdc.get(OUTPUT_INVENTORY_KEY, PylonSerializers.VIRTUAL_INVENTORY);
        this.outputInventory.setPreUpdateHandler(this::cancelOutput);

        this.stepDisplay.setPreUpdateHandler(this::cancelEverything);

        this.displayPhase = pdc.get(DISPLAY_KEY, PylonSerializers.BOOLEAN);

        NamespacedKey currentRecipeKey = pdc.get(CURRENT_RECIPE_KEY, PylonSerializers.NAMESPACED_KEY);
        this.currentRecipe = currentRecipeKey == null ? null : BlueprintWorkbenchRecipe.RECIPE_TYPE.getRecipe(currentRecipeKey);

        Long currentStepLong = pdc.get(CURRENT_PROGRESS_KEY, PylonSerializers.LONG);
        this.currentProgress = currentStepLong == null ? null : new Step.ActionStep(currentStepLong);
        this.currentStateDisplay = Step.StateDisplay.load(this);
    }

    @Override
    protected void postLoad() {
        updateStep();
    }

    //todo: entities don't load properly

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(CRAFTING_INVENTORY_KEY, PylonSerializers.VIRTUAL_INVENTORY, craftInventory);
        pdc.set(OUTPUT_INVENTORY_KEY, PylonSerializers.VIRTUAL_INVENTORY, outputInventory);
        pdc.set(DISPLAY_KEY, PylonSerializers.BOOLEAN, displayPhase);

        PylonUtils.setNullable(pdc, CURRENT_RECIPE_KEY, PylonSerializers.NAMESPACED_KEY, currentRecipe == null ? null : currentRecipe.getKey());
        PylonUtils.setNullable(pdc, CURRENT_PROGRESS_KEY, PylonSerializers.LONG, currentProgress == null ? null : currentProgress.toLong());
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

    public Step getStep() {
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
            sendMessage(player, "progress_recipe.offset_changed");
            offset++; // left click changes selected recipe, if any
            updateInputGrid();
        } else {
            boolean outcome = progressRecipe(mainHand, player, true);
            if (outcome) {
                event.setCancelled(true);
            }
        }
    }

    //<editor-fold desc="Recipe handling">
    public boolean progressRecipe(@NotNull ItemStack item, Player player, boolean shouldDamage) {
        if (currentRecipe == null || currentProgress == null) return false;

        PylonItem pylonItem = PylonItem.fromStack(item);
        NamespacedKey key = pylonItem != null ? pylonItem.getKey() : item.getType().getKey();

        Step current = this.getStep();
        if (!current.tool().equals(key)) {
            sendMessage(player, "progress_recipe.invalid_tool");
            return false;
        }

        if (shouldDamage && current.damageConsume()) {
            PylonUtils.damageItem(item, 1, player.getWorld());
        }

        int newUsedAmount = currentProgress.getUsedAmount() + 1;
        int remainingAmount = current.uses() - newUsedAmount;
        if (remainingAmount == 0) {
            int newStep = currentProgress.getStep() + 1;
            if (newStep < currentRecipe.steps().size()) {
                sendMessage(player, "progress_recipe.next_step");
                currentProgress.setStep(newStep);
                updateStep();
            } else {
                sendMessage(player, "progress_recipe.recipe_completed");
                completeRecipe();
            }
        } else {
            sendMessage(player, "progress_recipe.added_progress");
            currentProgress.setUsedAmount(newUsedAmount);
            updateStep();
        }

        return true;
    }

    public void completeRecipe() {
        this.displayPhase = false;

        clearInventory(this.craftInventory);
        updateInventory(this.outputInventory, currentRecipe.results());

        this.currentRecipe = null;
        this.currentProgress = null;

        updateStep();
        removeAddedEntities();
    }
    //</editor-fold>

    private void updateStep() {
        updateStepItem();
        updateStepDisplay();

        if (currentRecipe != null) {
            this.currentStateDisplay.update(getStep(), currentProgress);
        } else {
            this.currentStateDisplay.setVisibility(false);
        }
    }

    private void updateStepDisplay() {
        if (currentRecipe == null || currentProgress == null) {
            removeAddedEntities();
            return;
        }

        Step current = this.getStep();
        current.removeDisplays().forEach(this::removeEntity);

        Location up = getBlock().getRelative(BlockFace.UP).getLocation()
            .toCenterLocation()
            .add(0, -0.495, 0);
        current.addDisplays().forEach(data -> {
            String name = data.name();
            double[] positions = data.position();
            double[] scale = data.scale();

            addEntityIfMissing(name, () -> new BlockDisplayBuilder()
                .material(data.material())
                .transformation(new TransformBuilder()
                    .translate(positions[0], 0, positions[1])
                    .scale(scale[0], 0, scale[1])
                )
                .build(up)
            );

            if (data.mirrorX()) {
                addEntityIfMissing(name + "$mirror_x", () -> new BlockDisplayBuilder()
                    .material(data.material())
                    .transformation(new TransformBuilder()
                        .translate(-positions[0], 0, positions[1])
                        .scale(scale[0], 0, scale[1])
                    )
                    .build(up)
                );
            }

            if (data.mirrorZ()) {
                addEntityIfMissing(name + "$mirror_z", () -> new BlockDisplayBuilder()
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

    private void addEntityIfMissing(String name, Supplier<Entity> entity) {
        if (getHeldEntityUuid(name) != null) return;
        addEntity(name, entity.get());
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

    private void updateInputGrid() {
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

    private void clearInventory(VirtualInventory inventory) {
        for (int i = 0; i < 9; i++) {
            inventory.setItem(UpdateReason.SUPPRESSED, i, null);
        }
    }
    //</editor-fold>

    private TranslatableComponent getMessage(String key) {
        String fullKey = "pylon." + getKey().getNamespace() + ".item." + getKey().getKey() + "." + key;
        return Component.translatable(fullKey);
    }

    private void sendMessage(Player player, String key) {
        player.sendMessage(getMessage(key));
    }

    public void removeAddedEntities() {
        ArrayList<UUID> entities = new ArrayList<>(getHeldEntities().size());
        for (var entry : this.getHeldEntities().entrySet()) {
            if (Step.StateDisplay.ENTITY_IDENTIFIERS.contains(entry.getKey())) continue;
            entities.add(entry.getValue());
        }

        for (UUID entityId : entities) {
            Entity entity = Bukkit.getEntity(entityId);
            if (entity != null && entity.isValid()) entity.remove();
        }
    }

}
