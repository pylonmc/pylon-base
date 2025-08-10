package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonTool;
import io.github.pylonmc.pylon.core.util.BlockUtils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;


@SuppressWarnings("UnstableApiUsage")
public class LumberAxe extends PylonItem implements PylonTool {

    public LumberAxe( @NotNull ItemStack stack) {
        super(stack);
    }

    private final Set<Event> eventsToIgnore = HashSet.newHashSet(0);

    @Override
    public void onUsedToBreakBlock(@NotNull BlockBreakEvent event) {
        if (eventsToIgnore.contains(event)) {
            eventsToIgnore.remove(event);
            return;
        }
        breakAttachedWood(event.getBlock(), event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
        event.setCancelled(true); // Stop vanilla logic
    }

    private void breakAttachedWood(Block block, Player player, ItemStack tool) {
        // Recursive function, for every adjacent block check if it's a log, if so delete it and give the drop to the player and check all its adjacent blocks
        if (!Tag.LOGS.isTagged(block.getType()) || BlockStorage.isPylonBlock(block)) {
            return;
        }
        BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
        eventsToIgnore.add(blockBreakEvent);
        if (!blockBreakEvent.callEvent()) {
            return;
        }
        BlockState blockState = block.getState();
        Collection<ItemStack> drops = block.getDrops(tool);
        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getBlockData());
        block.setType(Material.AIR);
        if (blockBreakEvent.isDropItems()) {
            List<Item> itemsDropped = new ArrayList<>();
            for (ItemStack itemStack : drops) {
                Item item = block.getWorld().dropItem(block.getLocation(), itemStack);
                itemsDropped.add(item);
            }
            if (!new BlockDropItemEvent(block, blockState, player, itemsDropped).callEvent()) {
                for (Item item : itemsDropped) {
                    item.remove();
                }
            }
        }
        player.damageItemStack(tool, 1);
        for (BlockFace face : BlockUtils.IMMEDIATE_FACES) {
            breakAttachedWood(block.getRelative(face), player, tool);
        }
    }
}
