package io.github.pylonmc.pylon.content.tools;

import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.base.RebarInteractor;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class PortableEnderChest extends RebarItem implements RebarInteractor {

    public PortableEnderChest(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToClick(@NotNull PlayerInteractEvent event, @NotNull EventPriority priority) {
        // check against right click
        event.getPlayer().openInventory(event.getPlayer().getEnderChest());
    }
}
