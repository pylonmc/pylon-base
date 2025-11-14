package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.talismans.base.AttributeTalisman;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WaterBreathingTalisman extends AttributeTalisman {
    public static final NamespacedKey WATER_BREATHING_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "water_breathing_talisman");

    public WaterBreathingTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    protected Attribute getAttribute() {
        return Attribute.OXYGEN_BONUS;
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("bonus_oxygen", UnitFormat.PERCENT.format(attrBonus * 100)));
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return WATER_BREATHING_TALISMAN_KEY;
    }
}
