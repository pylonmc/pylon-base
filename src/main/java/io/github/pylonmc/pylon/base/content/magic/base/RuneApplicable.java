package io.github.pylonmc.pylon.base.content.magic.base;

import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * This interface defines what item can be applied with runes.
 * Machines are not applicable by default.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 *
 * public class MyCustomWeapon extends PylonItem implements RuneApplicable {
 *     @Override
 *     public boolean isApplicableToTarget(@NotNull PlayerDropItemEvent event, @NotNull ItemStack rune, @NotNull ItemStack target) {
 *         return target.getType() == Material.DIAMOND_SWORD;
 *     }
 * }
 *
 * }</pre>
 *
 * @author balugaq
 */
public interface RuneApplicable {
    /**
     * Called when a rune is dropped on a target item.
     * @param event The event
     * @param rune The rune
     * @param target The target item
     * @return true if the rune is applicable to the target item, false otherwise
     */
    boolean applicableToTarget(@NotNull PlayerDropItemEvent event, @NotNull ItemStack rune, @NotNull ItemStack target);
}
