package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class HealthTalismanTicker extends BukkitRunnable {
    private static final NamespacedKey healthBoostedKey = new NamespacedKey(PylonBase.getInstance(), "health_boosted");

    @Override
    public void run() {
        for(Player player : PylonBase.getInstance().getServer().getOnlinePlayers()){
            boolean foundItem = false;
            for(ItemStack itemStack : player.getInventory()){
                PylonItem<?> pylonItem = PylonItem.fromStack(itemStack);
                if(pylonItem instanceof HealthTalisman.HealthTalismanItem){
                    if(!player.getPersistentDataContainer().has(healthBoostedKey)){
                        HealthTalisman.HealthTalismanItem talisman = ((HealthTalisman.HealthTalismanItem)pylonItem);
                        player.setMaxHealth(player.getMaxHealth() + talisman.getHealthIncrease());
                        player.getPersistentDataContainer().set(healthBoostedKey, PersistentDataType.INTEGER, talisman.getHealthIncrease());
                    }
                    foundItem = true;
                    break;
                }
            }
            if(foundItem == false){
                if(player.getPersistentDataContainer().has(healthBoostedKey)){
                    // intellij lies, no NPE since I check if the key exists above
                    player.setMaxHealth(player.getMaxHealth() - player.getPersistentDataContainer().get(healthBoostedKey, PersistentDataType.INTEGER));
                    player.getPersistentDataContainer().remove(healthBoostedKey);
                }
            }
        }
    }
}
