package io.github.pylonmc.pylon.base.content.talismans;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.tools.base.Talisman;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HealthTalisman extends Talisman {

    private static final NamespacedKey HEALTH_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "health_talisman");

    private final int maxHealthBoost = getSettings().getOrThrow("max-health-boost", ConfigAdapter.INT);

    public HealthTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    public final AttributeModifier healthModifier = new AttributeModifier(
            HEALTH_TALISMAN_KEY,
            maxHealthBoost,
            AttributeModifier.Operation.ADD_NUMBER
    );

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("health-boost", UnitFormat.HEARTS.format(maxHealthBoost)));
    }

    @Override
    public void removeEffect(@NotNull Player player) {
        super.removeEffect(player);
        AttributeInstance playerHealth = player.getAttribute(Attribute.MAX_HEALTH);
        Preconditions.checkNotNull(playerHealth);
        playerHealth.removeModifier(HEALTH_TALISMAN_KEY);
    }

    @Override
    public void applyEffect(@NotNull Player player) {
        super.applyEffect(player);
        AttributeInstance playerHealth = player.getAttribute(Attribute.MAX_HEALTH);
        Preconditions.checkNotNull(playerHealth);
        playerHealth.addModifier(healthModifier);
    }

    @Override
    public int getLevel() {
        return maxHealthBoost;
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return HEALTH_TALISMAN_KEY;
    }

    @Override
    public long getTickInterval() {
        return 4L;
    }
}
