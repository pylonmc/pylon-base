package io.github.pylonmc.pylon.base.content.resources;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class Bloom extends PylonItem {

    public static final long HEAT_TASK_INTERVAL = Settings.get(BaseKeys.BLOOM).getOrThrow("check-interval", ConfigAdapter.LONG);
    public static final int UNPROTECTED_DAMAGE = Settings.get(BaseKeys.BLOOM).getOrThrow("unprotected-damage", ConfigAdapter.INT);

    private static final NamespacedKey TEMPERATURE_KEY = baseKey("temperature");
    public static final int MAX_TEMPERATURE = 12;

    private static final NamespacedKey WORKING_KEY = baseKey("working");
    public static final int MIN_WORKING = -15;
    public static final int MAX_WORKING = 15;

    public Bloom(@NotNull ItemStack stack) {
        super(stack);
    }

    /**
     * Returns a temperature between 0 and 12 inclusive.
     */
    public int getTemperature() {
        return getStack().getPersistentDataContainer().getOrDefault(TEMPERATURE_KEY, PylonSerializers.INTEGER, 0);
    }

    /**
     * @param temperature the temperature to set, must be between 0 and 12 inclusive.
     */
    public void setTemperature(int temperature) {
        Preconditions.checkArgument(temperature >= 0 && temperature <= MAX_TEMPERATURE, "Temperature must be between 0 and 12 inclusive.");
        getStack().editPersistentDataContainer((pdc) -> pdc.set(TEMPERATURE_KEY, PylonSerializers.INTEGER, temperature));
    }

    /**
     * Returns a working between -15 and 15 inclusive.
     */
    public int getWorking() {
        return getStack().getPersistentDataContainer().getOrDefault(WORKING_KEY, PylonSerializers.INTEGER, 0);
    }

    /**
     * @param working the working to set, must be between -15 and 15 inclusive.
     */
    public void setWorking(int working) {
        Preconditions.checkArgument(working >= MIN_WORKING && working <= MAX_WORKING, "Working must be between 0 and 15 inclusive.");
        getStack().editPersistentDataContainer((pdc) -> pdc.set(WORKING_KEY, PylonSerializers.INTEGER, working));
    }

    public static class Listener implements org.bukkit.event.Listener {
        private final Map<UUID, BukkitTask> checkerJobs = new HashMap<>();

        @EventHandler
        private void onPlayerJoin(@NotNull PlayerJoinEvent event) {
            Player player = event.getPlayer();
            Runnable task = () -> {
                PlayerInventory inv = player.getInventory();
                if (PylonUtils.isPylonSimilar(inv.getItemInMainHand(), BaseItems.TONGS)
                        || PylonUtils.isPylonSimilar(inv.getItemInOffHand(), BaseItems.TONGS)) return;
                for (ItemStack item : player.getInventory().getContents()) {
                    if (item == null) continue;
                    if (item.getType() == BaseItems.BLOOM.getType() && PylonUtils.isPylonSimilar(item, BaseItems.BLOOM)) {
                        player.damage(UNPROTECTED_DAMAGE, DamageSource.builder(DamageType.IN_FIRE).build());
                    }
                }
            };
            checkerJobs.put(
                    player.getUniqueId(),
                    Bukkit.getScheduler().runTaskTimer(PylonBase.getInstance(), task, 0L, HEAT_TASK_INTERVAL)
            );
        }

        @EventHandler
        private void onPlayerQuit(@NotNull PlayerQuitEvent event) {
            BukkitTask task = checkerJobs.remove(event.getPlayer().getUniqueId());
            if (task != null) task.cancel();
        }
    }
}
