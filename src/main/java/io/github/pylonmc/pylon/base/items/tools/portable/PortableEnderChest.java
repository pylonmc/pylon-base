package io.github.pylonmc.pylon.base.items.tools.portable;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class PortableEnderChest extends PylonItem implements PylonInteractor {

    public static final NamespacedKey KEY = pylonKey("portable_ender_chest");
    public static final ItemStack STACK = ItemStackBuilder.pylonItem(Material.ENDER_CHEST, KEY)
            .build();

    public PortableEnderChest(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        event.getPlayer().openInventory(event.getPlayer().getEnderChest());
    }
}
