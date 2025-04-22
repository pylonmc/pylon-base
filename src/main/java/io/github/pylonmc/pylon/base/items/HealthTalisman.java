package io.github.pylonmc.pylon.base.items;

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
import java.util.function.Function;

public class HealthTalisman extends PylonItemSchema {

    public final int maxHealthBoost = getSettings().getOrThrow("max-health-boost", Integer.class);
    private static final NamespacedKey healthBoostedKey = new NamespacedKey(PylonBase.getInstance(), "talisman_health_boosted");
    public final AttributeModifier healthModifier = new AttributeModifier(
            healthBoostedKey,
            maxHealthBoost,
            AttributeModifier.Operation.ADD_NUMBER
    );

    public HealthTalisman(NamespacedKey key, Function<NamespacedKey, ItemStack> templateSupplier) {
        super(key, HealthTalismanItem.class, templateSupplier);
        if (template.getMaxStackSize() != 1) {
            throw new IllegalArgumentException("Max stack size for health talisman must be equal to 1");
        }
    }

    public static class HealthTalismanItem extends PylonItem<HealthTalisman> {

        public HealthTalismanItem(HealthTalisman schema, ItemStack stack) {
            super(schema, stack);
        }

        @Override
        public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
            return Map.of("health-boost", Component.text(getSchema().maxHealthBoost));
        }
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
                Integer playerHealthBoost = playerPDC.get(healthBoostedKey, PersistentDataType.INTEGER);
                for (ItemStack itemStack : player.getInventory()) {
                    PylonItem<?> pylonItem = PylonItem.fromStack(itemStack);
                    if (!(pylonItem instanceof HealthTalismanItem talisman)) {
                        continue;
                    }
                    if (playerHealthBoost == null) {
                        playerHealth.addModifier(talisman.getSchema().healthModifier);
                        playerPDC.set(healthBoostedKey, PersistentDataType.INTEGER, talisman.getSchema().maxHealthBoost);
                        foundItem = true;
                    } else if (playerHealthBoost < talisman.getSchema().maxHealthBoost) {
                        playerHealth.removeModifier(healthBoostedKey);
                        playerHealth.addModifier(talisman.getSchema().healthModifier);
                        playerPDC.set(healthBoostedKey, PersistentDataType.INTEGER, talisman.getSchema().maxHealthBoost);
                        foundItem = true;
                    } else if (talisman.getSchema().maxHealthBoost == playerHealthBoost) {
                        foundItem = true;
                    }
                }
                if (!foundItem && playerHealthBoost != null) {
                    playerHealth.removeModifier(healthBoostedKey);
                    playerPDC.remove(healthBoostedKey);
                }
            }
        }
    }
}
