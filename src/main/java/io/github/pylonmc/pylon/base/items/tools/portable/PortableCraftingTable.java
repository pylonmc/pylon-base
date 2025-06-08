package io.github.pylonmc.pylon.base.items.tools.portable;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class PortableCraftingTable extends PylonItem implements PylonInteractor {

    public static final NamespacedKey KEY = pylonKey("portable_crafting_table");
    public static final ItemStack STACK = ItemStackBuilder.pylonItem(Material.CRAFTING_TABLE, KEY)
            .build();

    public PortableCraftingTable(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        event.getPlayer().openInventory(MenuType.CRAFTING.create(event.getPlayer()));
    }
}
