package io.github.pylonmc.pylon.base.content.talismans;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.tools.base.Talisman;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WaterBreathingTalisman extends Talisman {
    public static final NamespacedKey WATER_BREATHING_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "water_breathing_talisman");
    public final int level = getSettings().getOrThrow("level", ConfigAdapter.INT);
    public final double oxygenBoost = getSettings().getOrThrow("oxygen-boost", ConfigAdapter.DOUBLE);
    public final AttributeModifier breathingModifier = new AttributeModifier(
            WATER_BREATHING_TALISMAN_KEY,
            oxygenBoost,
            AttributeModifier.Operation.ADD_NUMBER
    );

    public WaterBreathingTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void applyEffect(@NotNull Player player) {
        super.applyEffect(player);
        AttributeInstance playerOxygen = player.getAttribute(Attribute.OXYGEN_BONUS);
        Preconditions.checkNotNull(playerOxygen);
        playerOxygen.addModifier(breathingModifier);
    }

    @Override
    public void removeEffect(@NotNull Player player) {
        super.removeEffect(player);
        AttributeInstance playerOxygen = player.getAttribute(Attribute.OXYGEN_BONUS);
        Preconditions.checkNotNull(playerOxygen);
        playerOxygen.removeModifier(WATER_BREATHING_TALISMAN_KEY);
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("bonus_oxygen", Component.text(oxygenBoost)));
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return WATER_BREATHING_TALISMAN_KEY;
    }
}
