package io.github.pylonmc.pylon.base.content.talismans;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.content.tools.base.Talisman;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class AttributeTalisman extends Talisman {
    public final int level = getSettings().getOrThrow("level", ConfigAdapter.INT);
    public final double attrBonus = getSettings().getOrThrow("attr-bonus", ConfigAdapter.DOUBLE);
    private final AttributeModifier modifier = new AttributeModifier(
            getTalismanKey(),
            attrBonus,
            AttributeModifier.Operation.ADD_NUMBER
    );

    public AttributeTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void applyEffect(@NotNull Player player) {
        super.applyEffect(player);
        AttributeInstance attr = player.getAttribute(getAttribute());
        Preconditions.checkNotNull(attr);
        attr.addModifier(modifier);
    }

    @Override
    public void removeEffect(@NotNull Player player) {
        super.removeEffect(player);
        AttributeInstance attr = player.getAttribute(getAttribute());
        Preconditions.checkNotNull(attr);
        attr.removeModifier(getTalismanKey());
    }

    @Override
    public int getLevel() {
        return level;
    }

    abstract Attribute getAttribute();
}
