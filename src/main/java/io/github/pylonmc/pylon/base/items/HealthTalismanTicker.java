package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class HealthTalismanTicker extends BukkitRunnable {
    private static final NamespacedKey healthBoostedKey = new NamespacedKey(PylonBase.getInstance(), "health_boosted");

    @Override
    // Suppresses warnings from doing PDC.has() and then .get() and assuming .get is not null, and assuming that the player has the max health attribute
    @SuppressWarnings("DataFlowIssue")
    public void run() {
        for (Player player : PylonBase.getInstance().getServer().getOnlinePlayers()) {
            boolean foundItem = false;
            for (ItemStack itemStack : player.getInventory()) {
                PylonItem<?> pylonItem = PylonItem.fromStack(itemStack);
                if (!(pylonItem instanceof HealthTalisman.HealthTalismanItem)) {
                    continue;
                }
                HealthTalisman.HealthTalismanItem talisman = ((HealthTalisman.HealthTalismanItem) pylonItem);
                if (!player.getPersistentDataContainer().has(healthBoostedKey)) {
                    player.getAttribute(Attribute.MAX_HEALTH).addModifier(new AttributeModifier(
                            healthBoostedKey,
                            talisman.getHealthIncrease(),
                            AttributeModifier.Operation.ADD_NUMBER
                    ));
                    player.getPersistentDataContainer().set(healthBoostedKey, PersistentDataType.INTEGER, talisman.getHealthIncrease());
                    foundItem = true;
                }
                else if (player.getPersistentDataContainer().has(healthBoostedKey) &&
                        player.getPersistentDataContainer().get(healthBoostedKey, PersistentDataType.INTEGER) < talisman.getHealthIncrease()) {
                    player.getAttribute(Attribute.MAX_HEALTH).removeModifier(healthBoostedKey);
                    player.getAttribute(Attribute.MAX_HEALTH).addModifier(new AttributeModifier(
                            healthBoostedKey,
                            talisman.getHealthIncrease(),
                            AttributeModifier.Operation.ADD_NUMBER
                    ));
                    player.getPersistentDataContainer().set(healthBoostedKey, PersistentDataType.INTEGER, talisman.getHealthIncrease());
                    foundItem = true;
                }
                else if (talisman.getHealthIncrease() == player.getPersistentDataContainer().get(healthBoostedKey, PersistentDataType.INTEGER)) {
                    foundItem = true;
                }
            }
            if (!foundItem && player.getPersistentDataContainer().has(healthBoostedKey)) {
                player.getAttribute(Attribute.MAX_HEALTH).removeModifier(healthBoostedKey);
                player.getPersistentDataContainer().remove(healthBoostedKey);
            }
        }
    }
}
