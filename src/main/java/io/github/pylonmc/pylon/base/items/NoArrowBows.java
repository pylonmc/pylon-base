package io.github.pylonmc.pylon.base.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class NoArrowBows implements Listener {
    HashMap<Player, ItemStack> storedItem = new HashMap<Player, ItemStack>();

    public void returnItem(Player player){
        if(storedItem.containsKey(player)){
            int slot = player.getInventory().getSize() - 1;
            player.getInventory().setItem(slot, storedItem.get(player));
            player.updateInventory();
            storedItem.remove(player);
        }
    }

    @EventHandler
    public void playerItemHeldEvent(PlayerItemHeldEvent e){
        returnItem(e.getPlayer());
    }

    @EventHandler
    public void playerDropItemEvent(PlayerDropItemEvent e){
        returnItem(e.getPlayer());
    }

    @EventHandler
    public void playerInteractEvent(PlayerInteractEvent e){
        System.out.println("Got player interact event");
        Player p = e.getPlayer();
        if(!e.getAction().equals(Action.RIGHT_CLICK_AIR) && !e.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            return;
        }
        if(storedItem.containsKey(p)){
            return;
        }
        if(p.getInventory().getItemInMainHand().getEnchantments().get(Enchantment.INFINITY) != 2){
            return;
        }
        int slot = p.getInventory().getSize() - 1;
        ItemStack item = p.getInventory().getItem(slot);
        storedItem.put(p, item);
        p.getInventory().setItem(slot, new ItemStack(Material.ARROW, 1));
    }
}
