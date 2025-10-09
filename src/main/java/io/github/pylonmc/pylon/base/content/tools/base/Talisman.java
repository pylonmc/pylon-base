package io.github.pylonmc.pylon.base.content.tools.base;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInventoryItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.WeakHashMap;

public abstract class Talisman extends PylonItem implements PylonInventoryItem {
    private static final HashMap<NamespacedKey, WeakHashMap<UUID, BukkitTask>> tasks = new HashMap<>();

    public Talisman(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onTick(@NotNull Player player, @NotNull ItemStack stack) {
        tasks.putIfAbsent(GetTalismanKey(), new WeakHashMap<>());
        boolean foundItem = false;
        Integer currentTalismanLevel = player.getPersistentDataContainer().get(GetTalismanKey(), PersistentDataType.INTEGER);
        if (currentTalismanLevel == null) {
            ApplyEffect_(player, stack);
            foundItem = true;
        } else if (currentTalismanLevel < GetLevel()) {
            RemoveEffect_(player, stack);
            ApplyEffect_(player, stack);
            foundItem = true;
        } else if (currentTalismanLevel == GetLevel()) {
            foundItem = true;
        }
        BukkitTask toCancel = tasks.get(GetTalismanKey()).get(player.getUniqueId());
        if (foundItem) {
            if(toCancel != null) {
                toCancel.cancel();
            }
            tasks.get(GetTalismanKey()).put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () ->
                            RemoveEffect_(player, stack),
                    getTickSpeed().getTickRate() + 1));
        }
    }

    private void ApplyEffect_(@NotNull Player player, @NotNull ItemStack stack) {
        ApplyEffect(player, stack);
        player.getPersistentDataContainer().set(GetTalismanKey(), PersistentDataType.INTEGER, GetLevel());
    }

    private void RemoveEffect_(@NotNull Player player, @NotNull ItemStack stack) {
        RemoveEffect(player, stack);
        player.getPersistentDataContainer().remove(GetTalismanKey());
    }

    /**
     * Remove the effect from the player. The implementation of this method must be able to handle removing the effect from other levels of this talisman.
     */
    public abstract void RemoveEffect(@NotNull Player player, @NotNull ItemStack stack);

    /**
     * Apply the effect of this talisman onto the player
     */
    public abstract void ApplyEffect(@NotNull Player player, @NotNull ItemStack stack);

    /**
     * Get the level of the talisman, this is used to determine which talismans should overwrite other ones
     */
    public abstract int GetLevel();

    /**
     * Get the generic key of the talisman, should be the same between all talismans of the same type, ie all health talisman levels have the same return value for this.
     */
    public abstract NamespacedKey GetTalismanKey();
}
