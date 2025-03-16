package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class PortableDustbin extends PylonItemSchema {
    public PortableDustbin(NamespacedKey id, Class<? extends PylonItem<? extends PortableDustbin>> itemClass, ItemStack template){
        super(id, itemClass, template);
    }

    public static class Item extends PylonItem<PortableDustbin> implements Interactor{
        public Item(PortableDustbin schema, ItemStack itemStack){ super(schema, itemStack); }

        @Override
        public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
            var menu = MenuType.GENERIC_9X6.create(event.getPlayer(), Component.text("Portable Dustbin"));
            event.getPlayer().openInventory(menu);
        }
    }
}
