package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.rebar.i18n.RebarArgument;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WaterBreathingTalisman extends AttributeTalisman {
    public static final NamespacedKey WATER_BREATHING_TALISMAN_KEY = BaseUtils.baseKey("water_breathing_talisman");

    public WaterBreathingTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    protected Attribute getAttribute() {
        return Attribute.OXYGEN_BONUS;
    }

    @Override
    public @NotNull List<@NotNull RebarArgument> getPlaceholders() {
        return List.of(RebarArgument.of("bonus_oxygen", UnitFormat.PERCENT.format(attrBonus * 100).decimalPlaces(2)));
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return WATER_BREATHING_TALISMAN_KEY;
    }
}
