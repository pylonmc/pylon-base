package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

public class HealthTalismanTicker extends BukkitRunnable {
    private static final NamespacedKey healthBoostedKey = new NamespacedKey(PylonBase.getInstance(), "talisman_health_boosted");

    @Override
    // Suppresses warnings from doing PDC.has() and then .get() and assuming .get is not null, and assuming that the player has the max health attribute
    @SuppressWarnings("DataFlowIssue")
    public void run() {
        for (Player player : PylonBase.getInstance().getServer().getOnlinePlayers()) {
            boolean foundItem = false;
            PersistentDataContainer playerPDC = player.getPersistentDataContainer();
            Integer playerHealthBoost = playerPDC.get(healthBoostedKey, PersistentDataType.INTEGER);
            for (ItemStack itemStack : player.getInventory()) {
                PylonItem<?> pylonItem = PylonItem.fromStack(itemStack);
                if (!(pylonItem instanceof HealthTalisman.HealthTalismanItem talisman)) {
                    continue;
                }
                if (playerHealthBoost == null) {
                    player.getAttribute(Attribute.MAX_HEALTH).addModifier(new AttributeModifier(
                            healthBoostedKey,
                            talisman.getHealthIncrease(),
                            AttributeModifier.Operation.ADD_NUMBER
                    ));
                    playerPDC.set(healthBoostedKey, PersistentDataType.INTEGER, talisman.getHealthIncrease());
                    foundItem = true;
                }
                else if (playerHealthBoost < talisman.getHealthIncrease()) {
                    player.getAttribute(Attribute.MAX_HEALTH).removeModifier(healthBoostedKey);
                    player.getAttribute(Attribute.MAX_HEALTH).addModifier(new AttributeModifier(
                            healthBoostedKey,
                            talisman.getHealthIncrease(),
                            AttributeModifier.Operation.ADD_NUMBER
                    ));
                    playerPDC.set(healthBoostedKey, PersistentDataType.INTEGER, talisman.getHealthIncrease());
                    foundItem = true;
                } else if (talisman.getHealthIncrease() == playerHealthBoost) {
                    foundItem = true;
                }
            }
            if (!foundItem && playerHealthBoost != null) {
                player.getAttribute(Attribute.MAX_HEALTH).removeModifier(healthBoostedKey);
                playerPDC.remove(healthBoostedKey);
            }
        }
    }
}
