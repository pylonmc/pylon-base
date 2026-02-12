package io.github.pylonmc.pylon.content.tools;

import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.base.RebarInteractor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;


public class PortableDustbin extends RebarItem implements RebarInteractor {

    public PortableDustbin(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToClick(@NotNull PlayerInteractEvent event, @NotNull EventPriority priority) {
        // check against right click
        Inventory menu = Bukkit.createInventory(null, 54, Component.translatable("pylon.gui.portable-dustbin"));
        event.getPlayer().openInventory(menu);
    }
}
