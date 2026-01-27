package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.rebar.block.BlockStorage;
import io.github.pylonmc.rebar.item.PylonItem;
import io.github.pylonmc.rebar.item.base.PylonTool;
import io.github.pylonmc.rebar.util.PylonUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Tool;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;


@SuppressWarnings("UnstableApiUsage")
public class LumberAxe extends PylonItem implements PylonTool {

    public LumberAxe( @NotNull ItemStack stack) {
        super(stack);
    }

    private static final Set<Event> eventsToIgnore = Collections.newSetFromMap(new WeakHashMap<>());

    @Override
    public void onUsedToBreakBlock(@NotNull BlockBreakEvent event) {
        if (!Tag.LOGS.isTagged(event.getBlock().getType()) || BlockStorage.isPylonBlock(event.getBlock())) {
            return;
        }
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

        Tool toolComponent = tool.getData(DataComponentTypes.TOOL);
        if (toolComponent != null) {
            PylonUtils.damageItem(tool, toolComponent.damagePerBlock(), player, EquipmentSlot.HAND);
        }

        for (BlockFace face : PylonUtils.IMMEDIATE_FACES) {
            breakAttachedWood(block.getRelative(face), player, tool);
        }
    }
}
