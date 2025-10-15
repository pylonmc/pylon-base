package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class HealthTalisman extends AttributeTalisman {

    private static final NamespacedKey HEALTH_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "health_talisman");

    public HealthTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return HEALTH_TALISMAN_KEY;
    }

    @Override
    Attribute getAttribute() {
        return Attribute.MAX_HEALTH;
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("health-boost", UnitFormat.HEARTS.format(attrBonus)));
    }
}
