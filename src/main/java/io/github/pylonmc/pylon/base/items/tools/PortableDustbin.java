package io.github.pylonmc.pylon.base.items.tools;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import net.kyori.adventure.text.Component;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;


public class PortableDustbin extends PylonItem<PylonItemSchema> implements Interactor{

    public PortableDustbin (PylonItemSchema schema, ItemStack stack) {
        super(schema, stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        var menu = MenuType.GENERIC_9X6.create(event.getPlayer(), Component.text("Portable Dustbin"));
        event.getPlayer().openInventory(menu);
    }
}
