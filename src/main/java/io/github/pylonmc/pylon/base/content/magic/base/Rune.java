package io.github.pylonmc.pylon.base.content.magic.base;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonArrow;
import io.github.pylonmc.pylon.core.item.base.PylonBow;
import io.github.pylonmc.pylon.core.item.base.PylonBucket;
import io.github.pylonmc.pylon.core.item.base.PylonTool;
import io.github.pylonmc.pylon.core.item.base.PylonWeapon;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * @author balugaq
 */
public abstract class Rune extends PylonItem {
    public Rune(@NotNull ItemStack stack) {
        super(stack);
    }

    public static final double CHECK_RANGE = Settings.get(BaseKeys.RUNE).getOrThrow("check-range", Double.class);

    // These can be applied with runes
    public static final List<Class<?>> DEFAULT_APPLICABLES = List.of(
            PylonArrow.class,
            PylonBow.class,
            PylonBucket.class,
            PylonTool.class,
            PylonWeapon.class
    );

    /**
     * Checks if the rune is applicable to the target item.
     * @param event The event
     * @param rune The rune item, amount may be > 1
     * @param target The item to handle, amount may be > 1
     * @return true if applicable, false otherwise
     */
    public boolean isApplicableToTarget(@NotNull PlayerDropItemEvent event, @NotNull ItemStack rune, @NotNull ItemStack target) {
        PylonItem instance = PylonItem.fromStack(target);
        if (instance instanceof RuneApplicable checker) {
            if (checker.applicableToTarget(event, rune, target)) {
                return true;
            }
        }

        return DEFAULT_APPLICABLES.stream().anyMatch(clazz -> clazz.isInstance(instance));
    }

    /**
     * Handles contacting between an item and a rune.
     * @param event The event
     * @param rune The rune item, amount may be > 1
     * @param target The item to handle, amount may be > 1
     */
    public abstract void onContactItem(@NotNull PlayerDropItemEvent event, @NotNull ItemStack rune, @NotNull ItemStack target);

    public static class RuneListener implements Listener {
        @EventHandler
        void onRuneDrop(@NotNull PlayerDropItemEvent event) {
            Player player = event.getPlayer();
            Item runeEntity = event.getItemDrop();
            ItemStack runeStack = runeEntity.getItemStack();
            PylonItem runeInstance = PylonItem.fromStack(runeStack);
            if (!(runeInstance instanceof Rune rune)) {
                return;
            }

            // Force run synchronously for entity handling
            PylonBase.runSync(() -> {
                Block lookingAt = player.getTargetBlockExact((int) Math.ceil(CHECK_RANGE), FluidCollisionMode.NEVER);
                if (lookingAt == null) {
                    // The player may drop runes in the sky, skip it.
                    return;
                }

                Collection<Item> nearbyEntities = player.getWorld().getNearbyEntitiesByType(Item.class, lookingAt.getLocation(), CHECK_RANGE, item -> rune.isApplicableToTarget(event, runeStack, item.getItemStack()));
                Item targetEntity = nearbyEntities
                        .stream()
                        .findFirst()
                        .orElse(null);

                if (targetEntity == null) {
                    // No target, skip it.
                    return;
                }

                ItemStack target = targetEntity.getItemStack();

                // All actions are handled by devs
                rune.onContactItem(event, runeStack, target);
                runeEntity.setItemStack(runeStack);
                targetEntity.setItemStack(target);
            });
        }
    }
}
