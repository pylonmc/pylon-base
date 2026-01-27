package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.i18n.RebarArgument;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

public class HungerTalisman extends Talisman {
    private static final NamespacedKey HUNGER_TALISMAN_KEY = BaseUtils.baseKey("hunger_talisman");
    public final int hungerIncrease = getSettings().getOrThrow("hunger-increase", ConfigAdapter.INT);
    public final float saturationIncrease = getSettings().getOrThrow("saturation-increase", ConfigAdapter.FLOAT);
    public final int increasePeriod = getSettings().getOrThrow("period-ticks", ConfigAdapter.INT);
    public final int level = getSettings().getOrThrow("level", ConfigAdapter.INT);
    private static final WeakHashMap<UUID, BukkitTask> hungerTasks = new WeakHashMap<>();

    public HungerTalisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull List<@NotNull RebarArgument> getPlaceholders() {
        return List.of(RebarArgument.of("period", UnitFormat.SECONDS.format(increasePeriod / 20f)),
                RebarArgument.of("saturation_increase", Component.text(saturationIncrease)),
                RebarArgument.of("hunger_increase", Component.text(hungerIncrease)));
    }

    @Override
    public void removeEffect(@NotNull Player player) {
        super.removeEffect(player);
        if (!hungerTasks.containsKey(player.getUniqueId())) {
            return;
        }
        hungerTasks.get(player.getUniqueId()).cancel();
    }

    @Override
    public void applyEffect(@NotNull Player player) {
        super.applyEffect(player);
        if (hungerTasks.containsKey(player.getUniqueId())) {
            hungerTasks.get(player.getUniqueId()).cancel();
        }
        hungerTasks.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(PylonBase.getInstance(), () -> {
            player.setFoodLevel(Math.min(player.getFoodLevel() + hungerIncrease, 20));
            player.setSaturation(Math.min(player.getSaturation() + saturationIncrease, player.getFoodLevel()));
        }, 0, increasePeriod));
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return HUNGER_TALISMAN_KEY;
    }

    public static final class JoinListener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            if (!event.getPlayer().getPersistentDataContainer().has(HUNGER_TALISMAN_KEY)) {
                return;
            }
            int talismanLevel = event.getPlayer().getPersistentDataContainer().get(HUNGER_TALISMAN_KEY, PersistentDataType.INTEGER);
            for (ItemStack stack : event.getPlayer().getInventory()) {
                RebarItem item = RebarItem.fromStack(stack);
                if (!(item instanceof HungerTalisman talisman)) {
                    continue;
                }
                if (talisman.getLevel() != talismanLevel) {
                    continue;
                }
                talisman.applyEffect(event.getPlayer());
            }
        }
    }

}
