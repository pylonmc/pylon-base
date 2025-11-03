package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.tools.base.Talisman;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class EnchantingTalisman extends Talisman {
    public final int level = getSettings().getOrThrow("level", ConfigAdapter.INT);
    public final double bonusLevelChance = getSettings().getOrThrow("bonus-level-chance", ConfigAdapter.DOUBLE);
    public static final NamespacedKey ENCHANTING_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "enchanting_talisman");
    private static final NamespacedKey ENCHANTING_TALISMAN_BONUS_KEY = new NamespacedKey(PylonBase.getInstance(), "enchanting_talisman_bonus");

    public EnchantingTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void applyEffect(@NotNull Player player) {
        super.applyEffect(player);
        player.getPersistentDataContainer().set(ENCHANTING_TALISMAN_BONUS_KEY, PersistentDataType.DOUBLE, bonusLevelChance);
    }

    @Override
    public void removeEffect(@NotNull Player player) {
        super.removeEffect(player);
        player.getPersistentDataContainer().remove(ENCHANTING_TALISMAN_BONUS_KEY);
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("bonus_level_chance", UnitFormat.PERCENT.format(bonusLevelChance * 100))
        );
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return ENCHANTING_TALISMAN_KEY;
    }

    public static class EnchantingListener implements Listener {
        @EventHandler
        public void onPreEnchant(PrepareItemEnchantEvent event) {
            if (!event.getEnchanter().getPersistentDataContainer().has(ENCHANTING_TALISMAN_BONUS_KEY)) {
                return;
            }
            @SuppressWarnings("DataFlowIssue")
            double bonusLevelChance = event.getEnchanter().getPersistentDataContainer().get(ENCHANTING_TALISMAN_BONUS_KEY, PersistentDataType.DOUBLE);
            Random randGen = new Random(event.getView().getEnchantmentSeed());
            for (EnchantmentOffer offer : event.getOffers()) {
                if (offer == null) continue;
                if (randGen.nextDouble() > bonusLevelChance) {
                    return;
                }
                offer.setEnchantmentLevel(Math.min(offer.getEnchantmentLevel() + 1, offer.getEnchantment().getMaxLevel()));
            }
        }
    }
}
