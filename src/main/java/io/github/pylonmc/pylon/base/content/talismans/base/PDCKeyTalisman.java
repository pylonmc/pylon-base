package io.github.pylonmc.pylon.base.content.talismans.base;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public abstract class PDCKeyTalisman<P,C> extends Talisman {

    public PDCKeyTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void applyEffect(@NotNull Player player) {
        super.applyEffect(player);
        player.getPersistentDataContainer().set(getPDCEffectKey(), getPDCType(), getPDCValue());
    }

    @Override
    public void removeEffect(@NotNull Player player) {
        super.removeEffect(player);
        player.getPersistentDataContainer().remove(getPDCEffectKey());
    }

    public abstract @NotNull NamespacedKey getPDCEffectKey();
    public abstract @NotNull PersistentDataType<P,C> getPDCType();
    public abstract @NotNull C getPDCValue();
}
