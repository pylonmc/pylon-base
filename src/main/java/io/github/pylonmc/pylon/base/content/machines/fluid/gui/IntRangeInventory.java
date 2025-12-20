package io.github.pylonmc.pylon.base.content.machines.fluid.gui;

import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import java.util.function.Supplier;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class IntRangeInventory {

    private static final NamespacedKey VARIATION_KEY = baseKey("variation");
    private static final int[] VARIATIONS = {1, 5, 10, 25, 50, 100, 200};

    private int amount = 0;

    private final Supplier<@NotNull Integer> maxAmount;

    private final VirtualInventory inventoryIncrease = new VirtualInventory(7);
    private final VirtualInventory inventoryDecrease = new VirtualInventory(7);
    private final VirtualInventory mainItemDisplay = new VirtualInventory(1);

    @Getter
    private final ItemStack mainItem;

    public IntRangeInventory(ItemStack item, Supplier<@NotNull Integer> maxAmount) {
        this.mainItem = item;
        this.maxAmount = maxAmount;

        inventoryDecrease.setPreUpdateHandler(this::handleUpdating);
        inventoryIncrease.setPreUpdateHandler(this::handleUpdating);
        mainItemDisplay.setPreUpdateHandler(pre -> pre.setCancelled(true));

        ItemStackBuilder baseIncrement = ItemStackBuilder.of(Material.LIME_WOOL);
        ItemStackBuilder baseDecrement = ItemStackBuilder.of(Material.RED_WOOL);
        for (int var : VARIATIONS) {
            ItemStackBuilder increment = baseIncrement
                .name("+ " + var)
                .editPdc(pdc -> pdc.set(VARIATION_KEY, PylonSerializers.INTEGER, var));

            ItemStackBuilder decrement = baseDecrement
                .name("- " + var)
                .editPdc(pdc -> pdc.set(VARIATION_KEY, PylonSerializers.INTEGER, -var));

            inventoryIncrease.addItem(UpdateReason.SUPPRESSED, increment.build());
            inventoryDecrease.addItem(UpdateReason.SUPPRESSED, decrement.build());
        }

        mainItemDisplay.setItem(UpdateReason.SUPPRESSED, 0, makeDisplay());
    }

    public IntRangeInventory(ItemStack item, Supplier<@NotNull Integer> maxAmount, int amount) {
        this(item, maxAmount);
        this.amount = amount;
    }

    private void validate() {
        int obtainedMax = this.maxAmount.get();
        this.amount = Math.max(0, Math.min(this.amount, obtainedMax));
    }

    public void add(int added) {
        this.amount += added;
        validate();
    }

    public int getAmount() {
        validate();
        return this.amount;
    }

    public Gui makeGui() {
        return Gui.normal()
            .setStructure(
                "# i i i i i i i #",
                "# # # # m # # # #",
                "# d d d d d d d #"
            )
            .addIngredient('#', GuiItems.background())
            .addIngredient('i', inventoryIncrease)
            .addIngredient('d', inventoryDecrease)
            .addIngredient('m', mainItemDisplay)
            .build();
    }

    private ItemStack makeDisplay() {
        return ItemStackBuilder.of(mainItem.clone())
            .lore("")
            .lore("Amount = " + amount)
            .build();
    }

    private void handleUpdating(@NotNull ItemPreUpdateEvent event) {
        event.setCancelled(true);
        ItemStack old = event.getPreviousItem();
        if (old == null) return;

        Integer variation = old.getPersistentDataContainer().get(VARIATION_KEY, PylonSerializers.INTEGER);
        if (variation == null) return;

        this.add(variation);
        mainItemDisplay.setItem(UpdateReason.SUPPRESSED, 0, makeDisplay());
    }
}