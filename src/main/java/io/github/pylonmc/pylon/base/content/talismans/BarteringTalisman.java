package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.talismans.base.PDCKeyTalisman;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.PiglinBarterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public class BarteringTalisman extends PDCKeyTalisman<Float, Float> {
    public static final NamespacedKey BARTERING_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "bartering_talisman");
    public static final NamespacedKey BARTERING_TALISMAN_NO_CONSUME_KEY = new NamespacedKey(PylonBase.getInstance(), "bartering_talisman_no_consume_chance");
    public final float chanceToNotConsumeInput = getSettings().getOrThrow("chance-to-not-consume-input", ConfigAdapter.FLOAT);

    public BarteringTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull NamespacedKey getPdcEffectKey() {
        return BARTERING_TALISMAN_NO_CONSUME_KEY;
    }

    @Override
    public @NotNull PersistentDataType<Float, Float> getPdcType() {
        return PersistentDataType.FLOAT;
    }

    @Override
    public @NotNull Float getPdcValue() {
        return chanceToNotConsumeInput;
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("chance_to_not_consume_input", UnitFormat.PERCENT.format(chanceToNotConsumeInput * 100, 2)));
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return BARTERING_TALISMAN_KEY;
    }

    public static final class BarteringTalismanListener implements Listener {
        @EventHandler
        public void onBarter(PiglinBarterEvent event) {
            Optional<Entity> player = event.getEntity().getNearbyEntities(15, 5, 15).stream().filter(
                    entity -> entity.getType() == EntityType.PLAYER
            ).findFirst();
            if (player.isEmpty()) {
                return;
            }
            float chance = player.get().getPersistentDataContainer().get(BARTERING_TALISMAN_NO_CONSUME_KEY, PersistentDataType.FLOAT);
            if (ThreadLocalRandom.current().nextFloat() > chance) {
                return;
            }
            Item item = event.getEntity().getWorld().dropItem(event.getEntity().getLocation(), event.getInput().clone());
            if (!new EntityDropItemEvent(event.getEntity(), item).callEvent()) {
                item.remove();
            }
        }
    }
}
