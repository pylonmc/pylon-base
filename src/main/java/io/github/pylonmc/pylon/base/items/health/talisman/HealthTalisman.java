package io.github.pylonmc.pylon.base.items.health.talisman;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class HealthTalisman extends PylonItem {

    private static final NamespacedKey HEALTH_BOOSTED_KEY = new NamespacedKey(PylonBase.getInstance(), "talisman_health_boosted");

    protected HealthTalisman(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    abstract int getMaxHealthBoost();

    public final AttributeModifier healthModifier = new AttributeModifier(
            HEALTH_BOOSTED_KEY,
            getMaxHealthBoost(),
            AttributeModifier.Operation.ADD_NUMBER
    );

    @Override
    public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
        return Map.of("health-boost", Component.text(getMaxHealthBoost()));
    }

    public static class HealthTalismanTicker extends BukkitRunnable {

        @Override
        // Suppresses warnings from doing PDC.has() and then .get() and assuming .get is not null, and assuming that the player has the max health attribute
        @SuppressWarnings("DataFlowIssue")
        public void run() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                boolean foundItem = false;
                PersistentDataContainer playerPDC = player.getPersistentDataContainer();
                AttributeInstance playerHealth = player.getAttribute(Attribute.MAX_HEALTH);
                Integer playerHealthBoost = playerPDC.get(HEALTH_BOOSTED_KEY, PersistentDataType.INTEGER);
                for (ItemStack itemStack : player.getInventory()) {
                    PylonItem pylonItem = fromStack(itemStack);
                    if (!(pylonItem instanceof HealthTalisman talisman)) {
                        continue;
                    }
                    if (playerHealthBoost == null) {
                        playerHealth.addModifier(talisman.healthModifier);
                        playerPDC.set(HEALTH_BOOSTED_KEY, PersistentDataType.INTEGER, talisman.getMaxHealthBoost());
                        foundItem = true;
                    } else if (playerHealthBoost < talisman.getMaxHealthBoost()) {
                        playerHealth.removeModifier(HEALTH_BOOSTED_KEY);
                        playerHealth.addModifier(talisman.healthModifier);
                        playerPDC.set(HEALTH_BOOSTED_KEY, PersistentDataType.INTEGER, talisman.getMaxHealthBoost());
                        foundItem = true;
                    } else if (talisman.getMaxHealthBoost() == playerHealthBoost) {
                        foundItem = true;
                    }
                }
                if (!foundItem && playerHealthBoost != null) {
                    playerHealth.removeModifier(HEALTH_BOOSTED_KEY);
                    playerPDC.remove(HEALTH_BOOSTED_KEY);
                }
            }
        }
    }
}
