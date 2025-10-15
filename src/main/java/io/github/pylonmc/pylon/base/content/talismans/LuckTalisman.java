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

public class LuckTalisman extends Talisman {
    public static final NamespacedKey LUCK_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "luck_talisman");
    public final double luckBonus = getSettings().getOrThrow("luck-bonus", ConfigAdapter.DOUBLE);
    public final int level = getSettings().getOrThrow("level", ConfigAdapter.INT);
    private final AttributeModifier luckModifier = new AttributeModifier(
            LUCK_TALISMAN_KEY,
            luckBonus,
            AttributeModifier.Operation.ADD_NUMBER
    );

    public LuckTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void applyEffect(@NotNull Player player) {
        super.applyEffect(player);
        AttributeInstance playerLuck = player.getAttribute(Attribute.LUCK);
        Preconditions.checkNotNull(playerLuck);
        playerLuck.addModifier(luckModifier);
    }

    @Override
    public void removeEffect(@NotNull Player player) {
        super.removeEffect(player);
        AttributeInstance playerLuck = player.getAttribute(Attribute.LUCK);
        Preconditions.checkNotNull(playerLuck);
        playerLuck.removeModifier(LUCK_TALISMAN_KEY);
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("bonus_luck", Component.text(luckBonus)));
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return LUCK_TALISMAN_KEY;
    }
}
