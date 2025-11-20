package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import net.kyori.adventure.text.Component;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;


public class PortableDustbin extends PylonItem implements PylonInteractor {

    public PortableDustbin(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        var menu = MenuType.GENERIC_9X6.create(event.getPlayer(), Component.translatable("pylon.pylonbase.gui.portable-dustbin"));
        event.getPlayer().openInventory(menu);
    }
}
