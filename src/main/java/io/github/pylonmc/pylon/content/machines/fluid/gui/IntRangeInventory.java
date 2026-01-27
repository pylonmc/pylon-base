package io.github.pylonmc.pylon.content.machines.fluid.gui;

import io.github.pylonmc.rebar.datatypes.RebarSerializers;
import io.github.pylonmc.rebar.i18n.RebarArgument;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import io.github.pylonmc.rebar.util.gui.GuiItems;
import lombok.Getter;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;

import java.util.function.Supplier;

import static io.github.pylonmc.pylon.util.PylonUtils.pylonKey;

public class IntRangeInventory {

    private static final NamespacedKey VARIATION_KEY = pylonKey("variation");
    private static final int[] VARIATIONS = {1, 5, 10, 25, 50, 100, 200};

    private final TranslatableComponent amountComponent;
    private int amount;

    private final Supplier<@NotNull Integer> maxAmount;

    private final VirtualInventory inventoryIncrease = new VirtualInventory(7);
    private final VirtualInventory inventoryDecrease = new VirtualInventory(7);
    private final VirtualInventory mainItemDisplay = new VirtualInventory(1);

    @Getter
    private final ItemStack mainItem;

    public IntRangeInventory(
        ItemStack item, 
        Supplier<@NotNull Integer> maxAmount, 
        TranslatableComponent incrementComponent,
        TranslatableComponent decrementComponent,
        TranslatableComponent amountComponent
    ) {
        this(item, maxAmount, 0, incrementComponent, decrementComponent, amountComponent);
    }

    public IntRangeInventory(
        ItemStack item,
        Supplier<@NotNull Integer> maxAmount,
        int amount,
        TranslatableComponent incrementComponent,
        TranslatableComponent decrementComponent,
        TranslatableComponent amountComponent
    ) {
        this.mainItem = item;
        this.maxAmount = maxAmount;
        this.amountComponent = amountComponent;
        this.amount = amount;

        inventoryDecrease.setPreUpdateHandler(this::handleUpdating);
        inventoryIncrease.setPreUpdateHandler(this::handleUpdating);
        mainItemDisplay.setPreUpdateHandler(pre -> pre.setCancelled(true));

        ItemStackBuilder baseIncrement = ItemStackBuilder.of(Material.LIME_WOOL);
        ItemStackBuilder baseDecrement = ItemStackBuilder.of(Material.RED_WOOL);
        for (int var : VARIATIONS) {
            ItemStackBuilder increment = baseIncrement
                .name(
                    incrementComponent.arguments(
                        RebarArgument.of("amount", var)
                    )
                )
                .editPdc(pdc -> pdc.set(VARIATION_KEY, RebarSerializers.INTEGER, var));

            ItemStackBuilder decrement = baseDecrement
                .name(
                    decrementComponent.arguments(
                        RebarArgument.of("amount", var)
                    )
                )
                .editPdc(pdc -> pdc.set(VARIATION_KEY, RebarSerializers.INTEGER, -var));

            inventoryIncrease.addItem(UpdateReason.SUPPRESSED, increment.build());
            inventoryDecrease.addItem(UpdateReason.SUPPRESSED, decrement.build());
        }

        mainItemDisplay.setItem(UpdateReason.SUPPRESSED, 0, makeDisplay());
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
            .lore(
                amountComponent.arguments(
                    RebarArgument.of("amount", amount)
                )
            )
            .build();
    }

    private void handleUpdating(@NotNull ItemPreUpdateEvent event) {
        event.setCancelled(true);
        ItemStack old = event.getPreviousItem();
        if (old == null) return;

        Integer variation = old.getPersistentDataContainer().get(VARIATION_KEY, RebarSerializers.INTEGER);
        if (variation == null) return;

        this.add(variation);
        mainItemDisplay.setItem(UpdateReason.SUPPRESSED, 0, makeDisplay());
    }
}