package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.BaseUtils.pylonKey;


public class PortableDustbin extends PylonItem implements Interactor{

    public static final NamespacedKey KEY = pylonKey("portable_dustbin");
    public static final ItemStack STACK = ItemStackBuilder.pylonItem(Material.CAULDRON, KEY)
            .build();

    public PortableDustbin(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        var menu = MenuType.GENERIC_9X6.create(event.getPlayer(), Component.text("Portable Dustbin"));
        event.getPlayer().openInventory(menu);
    }
}
