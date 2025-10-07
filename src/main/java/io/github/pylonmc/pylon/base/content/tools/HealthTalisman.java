package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.InventoryTickSpeed;
import io.github.pylonmc.pylon.core.item.base.PylonInventoryItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HealthTalisman extends PylonItem implements PylonInventoryItem {

    private static final NamespacedKey HEALTH_BOOSTED_KEY = new NamespacedKey(PylonBase.getInstance(), "talisman_health_boosted");

    private final int maxHealthBoost = getSettings().getOrThrow("max-health-boost", ConfigAdapter.INT);

    public HealthTalisman(@NotNull ItemStack stack) {
        super(stack);
    }
    public final AttributeModifier healthModifier = new AttributeModifier(
            HEALTH_BOOSTED_KEY,
            maxHealthBoost,
            AttributeModifier.Operation.ADD_NUMBER
    );

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("health-boost", UnitFormat.HEARTS.format(maxHealthBoost)));
    }

    @Override
    public void onTick(@NotNull Player player, @NotNull ItemStack stack) {
        boolean foundItem = false;
        PersistentDataContainer playerPDC = player.getPersistentDataContainer();
        AttributeInstance playerHealth = player.getAttribute(Attribute.MAX_HEALTH);
        assert playerHealth != null;
        Integer playerHealthBoost = playerPDC.get(HEALTH_BOOSTED_KEY, PersistentDataType.INTEGER);
        if (playerHealthBoost == null) {
            playerHealth.addModifier(this.healthModifier);
            playerPDC.set(HEALTH_BOOSTED_KEY, PersistentDataType.INTEGER, maxHealthBoost);
            foundItem = true;
        } else if (playerHealthBoost < this.maxHealthBoost) {
            playerHealth.removeModifier(HEALTH_BOOSTED_KEY);
            playerHealth.addModifier(this.healthModifier);
            playerPDC.set(HEALTH_BOOSTED_KEY, PersistentDataType.INTEGER, maxHealthBoost);
            foundItem = true;
        } else if (maxHealthBoost == playerHealthBoost) {
            foundItem = true;
        }
        if (!foundItem) {
            playerHealth.removeModifier(HEALTH_BOOSTED_KEY);
            playerPDC.remove(HEALTH_BOOSTED_KEY);
        }
    }

    @Override
    public @NotNull InventoryTickSpeed getTickSpeed() {
        return InventoryTickSpeed.SLOW;
    }
}
