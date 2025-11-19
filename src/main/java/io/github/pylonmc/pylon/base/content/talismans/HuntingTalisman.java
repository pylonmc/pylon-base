package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.talismans.base.PDCKeyTalisman;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class HuntingTalisman extends PDCKeyTalisman<Double, Double> {
    public final int level = getSettings().getOrThrow("level", ConfigAdapter.INT);
    public final double chanceForExtraItem = getSettings().getOrThrow("chance-for-extra-item", ConfigAdapter.DOUBLE);
    public static final NamespacedKey HUNTING_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "hunting_talisman");
    public static final NamespacedKey HUNTING_TALISMAN_BONUS_KEY = new NamespacedKey(PylonBase.getInstance(), "hunting_talisman_bonus");

    public HuntingTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull NamespacedKey getPdcEffectKey() {
        return HUNTING_TALISMAN_BONUS_KEY;
    }

    @Override
    public @NotNull PersistentDataType<Double, Double> getPdcType() {
        return PersistentDataType.DOUBLE;
    }

    @Override
    public @NotNull Double getPdcValue() {
        return chanceForExtraItem;
    }


    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("bonus_item_chance", UnitFormat.PERCENT.format(chanceForExtraItem * 100, 2))
        );
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return HUNTING_TALISMAN_KEY;
    }

    public static final class HuntingTalismanListener implements Listener {

        @EventHandler
        public void onEntityDeath(EntityDeathEvent event) {
            if (!(event.getDamageSource().getCausingEntity() instanceof Player player)) {
                return;
            }
            if (!player.getPersistentDataContainer().has(HUNTING_TALISMAN_BONUS_KEY)) {
                return;
            }
            if (event.getEntity().getType() == EntityType.PLAYER) {
                return;
            }
            @SuppressWarnings("DataFlowIssue")
            double chanceForExtraItem = player.getPersistentDataContainer().get(HUNTING_TALISMAN_BONUS_KEY, PersistentDataType.DOUBLE);
            for (ItemStack drop : event.getDrops()) {
                if (drop.getItemMeta().hasRarity() && drop.getItemMeta().getRarity().ordinal() >= ItemRarity.RARE.ordinal()) {
                    continue;
                }
                if (ThreadLocalRandom.current().nextDouble() > chanceForExtraItem) {
                    continue;
                }
                drop.setAmount(drop.getAmount() + 1);
            }
        }
    }
}
