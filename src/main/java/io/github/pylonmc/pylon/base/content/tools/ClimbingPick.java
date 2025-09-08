package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

import static java.lang.Math.pow;

public class ClimbingPick extends PylonItem implements PylonInteractor {
    private static final NamespacedKey HOOKED_KEY = new NamespacedKey(PylonBase.getInstance(), "climbing_pick_hooked");
    private final double jumpSpeed = getSettings().getOrThrow("jump-speed", Double.class);
    private final double hookRange = getSettings().getOrThrow("hook-range", Double.class);
    private final double hookRangeSquared = pow(hookRange, 2);

    public ClimbingPick(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        event.setCancelled(true);
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getPlayer().getPersistentDataContainer().has(HOOKED_KEY)) return;
        double distSquared = event.getClickedBlock().getLocation().clone().subtract(event.getPlayer().getEyeLocation()).toVector().toVector3f().lengthSquared();
        if (distSquared < hookRangeSquared) {
            PlayerJumpListener listener = new PlayerJumpListener(event.getPlayer(), (float) jumpSpeed);
            Bukkit.getPluginManager().registerEvents(listener, PylonBase.getInstance());
        }
    }

    @Override
    public @NotNull List<PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("jump-speed", UnitFormat.BLOCKS_PER_SECOND.format(jumpSpeed)),
                PylonArgument.of("hook-range", UnitFormat.BLOCKS.format(hookRange))
        );
    }

    public static class PlayerJumpListener implements Listener {
        private final Player player;
        private final float jumpSpeed;
        private final boolean isAllowedFlight;
        private final boolean hasGravity;

        public PlayerJumpListener(Player player, float jumpSpeed) {
            this.player = player;
            this.jumpSpeed = jumpSpeed;
            this.isAllowedFlight = player.getAllowFlight();
            this.hasGravity = player.hasGravity();
            player.setGravity(false);
            player.setAllowFlight(true);
            player.getPersistentDataContainer().set(HOOKED_KEY, PersistentDataType.BOOLEAN, true);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (event.getPlayer() == player && event.hasExplicitlyChangedPosition() && !event.getPlayer().isJumping()) {
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onStartFly(PlayerToggleFlightEvent event) {
            if (event.getPlayer() == player) {
                event.setCancelled(true);
                // Trigger a "jump"
                player.setVelocity(Vector.fromJOML(new Vector3f(0.0f, jumpSpeed, 0.0f)));
                dispose();
            }
        }

        @EventHandler
        public void onSneak(PlayerToggleSneakEvent event) {
            if (event.getPlayer() == player && event.isSneaking()) {
                dispose();
            }
        }

        @EventHandler
        public void onPlayerLeave(PlayerQuitEvent event) {
            if (event.getPlayer() == player) {
                dispose();
            }
        }

        @EventHandler
        public void onPluginDisable(PluginDisableEvent event) {
            if (event.getPlugin() == PylonBase.getInstance()) {
                dispose();
            }
        }

        private void dispose() {
            player.setGravity(hasGravity);
            player.setAllowFlight(isAllowedFlight);
            player.getPersistentDataContainer().remove(HOOKED_KEY);
            PlayerMoveEvent.getHandlerList().unregister(this);
            PlayerToggleSneakEvent.getHandlerList().unregister(this);
            PlayerQuitEvent.getHandlerList().unregister(this);
            PlayerToggleFlightEvent.getHandlerList().unregister(this);
            PluginDisableEvent.getHandlerList().unregister(this);
        }
    }
}
