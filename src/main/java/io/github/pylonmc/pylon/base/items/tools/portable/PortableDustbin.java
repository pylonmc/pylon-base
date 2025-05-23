package io.github.pylonmc.pylon.base.items.tools.portable;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class PortableDustbin extends PylonItem implements Interactor{

    public static final NamespacedKey KEY = pylonKey("portable_dustbin");
    public static final ItemStack ITEM_STACK = ItemStackBuilder.defaultBuilder(Material.CAULDRON, KEY)
            .build();

    public PortableDustbin (PylonItemSchema schema, ItemStack stack) {
        super(schema, stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        var menu = MenuType.GENERIC_9X6.create(event.getPlayer(), Component.text("Portable Dustbin"));
        event.getPlayer().openInventory(menu);
    }
}
