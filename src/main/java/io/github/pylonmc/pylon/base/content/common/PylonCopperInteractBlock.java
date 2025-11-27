package io.github.pylonmc.pylon.base.content.common;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface PylonCopperInteractBlock extends PylonInteractBlock {
    @Override
    default void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        EquipmentSlot hand = event.getHand();
        if (hand == null) return;

        ItemStack stack = event.getPlayer().getInventory().getItem(hand);
        Material type = stack.getType();
        if (Tag.ITEMS_AXES.isTagged(type) || type == Material.HONEYCOMB) {
            event.setCancelled(true);
        }
    }
}
