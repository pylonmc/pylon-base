package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class PortableEnderChest extends PylonItem<PylonItemSchema> implements Interactor {

    public PortableEnderChest(PylonItemSchema schema, ItemStack stack) {
        super(schema, stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        event.getPlayer().openInventory(event.getPlayer().getEnderChest());
    }
}
