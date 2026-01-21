package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LuckTalisman extends AttributeTalisman {
    public static final NamespacedKey LUCK_TALISMAN_KEY = BaseUtils.baseKey("luck_talisman");

    public LuckTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    protected Attribute getAttribute() {
        return Attribute.LUCK;
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("bonus_luck", Component.text(attrBonus)));
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return LUCK_TALISMAN_KEY;
    }
}
