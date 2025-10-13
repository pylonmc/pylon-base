package io.github.pylonmc.pylon.base.content.tools.base;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.config.PylonConfig;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInventoryTicker;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.UUID;
import java.util.WeakHashMap;

public abstract class Talisman extends PylonItem implements PylonInventoryTicker {
    private static final HashMap<NamespacedKey, WeakHashMap<UUID, BukkitTask>> tasks = new HashMap<>();

    public Talisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onTick(@NotNull Player player) {
        tasks.putIfAbsent(getTalismanKey(), new WeakHashMap<>());
        boolean foundItem = false;
        Integer currentTalismanLevel = player.getPersistentDataContainer().get(getTalismanKey(), PersistentDataType.INTEGER);
        if (currentTalismanLevel == null) {
            applyEffect(player);
            foundItem = true;
        } else if (currentTalismanLevel < getLevel()) {
            removeEffect(player);
            applyEffect(player);
            foundItem = true;
        } else if (currentTalismanLevel == getLevel()) {
            foundItem = true;
        }
        BukkitTask toCancel = tasks.get(getTalismanKey()).get(player.getUniqueId());
        if (foundItem) {
            if (toCancel != null) {
                toCancel.cancel();
            }
            tasks.get(getTalismanKey()).put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () ->
                            removeEffect(player),
                    getTickInterval() * PylonConfig.getInventoryTickerBaseRate() + 1));
        }
    }

    @MustBeInvokedByOverriders
    public void applyEffect(@NotNull Player player) {
        player.getPersistentDataContainer().set(getTalismanKey(), PersistentDataType.INTEGER, getLevel());
    }

    @MustBeInvokedByOverriders
    public void removeEffect(@NotNull Player player) {
        player.getPersistentDataContainer().remove(getTalismanKey());
    }

    /**
     * Get the level of the talisman, this is used to determine which talismans should overwrite other ones
     */
    public abstract int getLevel();

    /**
     * Get the generic key of the talisman, should be the same between all talismans of the same type, ie all health talisman levels have the same return value for this.
     */
    public abstract NamespacedKey getTalismanKey();
}
