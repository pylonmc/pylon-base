package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BreedingTalisman extends Talisman {
    public final float breedingCooldownMultiplier = getSettings().getOrThrow("breeding-cd-multiplier", ConfigAdapter.FLOAT);
    public static final NamespacedKey BREEDING_TALISMAN_KEY = BaseUtils.baseKey("breeding_talisman");
    public static final NamespacedKey BREEDING_TALISMAN_MULTIPLIER_KEY = BaseUtils.baseKey("breeding_talisman_multiplier");

    public BreedingTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("cd_multiplier", UnitFormat.PERCENT.format(breedingCooldownMultiplier * 100).decimalPlaces(2)));
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return BREEDING_TALISMAN_KEY;
    }

    @Override
    public void applyEffect(@NotNull Player player) {
        super.applyEffect(player);
        player.getPersistentDataContainer().set(BREEDING_TALISMAN_MULTIPLIER_KEY, PersistentDataType.FLOAT, breedingCooldownMultiplier);
    }

    @Override
    public void removeEffect(@NotNull Player player) {
        super.removeEffect(player);
        player.getPersistentDataContainer().remove(BREEDING_TALISMAN_MULTIPLIER_KEY);
    }

    public static final class BreedingTalismanListener implements Listener {
        @EventHandler
        public void onBreedEvent(EntityBreedEvent event) {
            if (event.getBreeder() == null || event.getBreeder().getType() != EntityType.PLAYER) {
                return;
            }
            if (!event.getBreeder().getPersistentDataContainer().has(BREEDING_TALISMAN_MULTIPLIER_KEY)) {
                return;
            }
            if (!(event.getFather() instanceof Animals a1) || !(event.getMother() instanceof Animals a2)) {
                return;
            }
            float multiplier = event.getBreeder().getPersistentDataContainer().get(BREEDING_TALISMAN_MULTIPLIER_KEY, PersistentDataType.FLOAT);
            a1.setLoveModeTicks(Math.round(a1.getLoveModeTicks() * multiplier));
            a2.setLoveModeTicks(Math.round(a2.getLoveModeTicks() * multiplier));
        }
    }
}
