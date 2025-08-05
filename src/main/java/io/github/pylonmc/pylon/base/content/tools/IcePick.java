package io.github.pylonmc.pylon.base.content.tools;

import com.destroystokyo.paper.event.player.PlayerConnectionCloseEvent;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;

import static io.github.pylonmc.pylon.core.util.PylonUtils.vectorToBlockFace;
import static java.lang.Math.*;

public class IcePick extends PylonItem implements PylonInteractor {
    public final double jumpSpeed = getSettings().getOrThrow("jump-speed", Double.class);
    public final double hookRange = getSettings().getOrThrow("hook-range", Double.class);
    public final double hookRangeSquared = pow(hookRange, 2);

    public IcePick(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event){
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        double distSquared = event.getClickedBlock().getLocation().clone().subtract(event.getPlayer().getLocation()).toVector().toVector3f().lengthSquared();
        if(distSquared < hookRangeSquared) {
            event.getPlayer().setGravity(false);
            event.getPlayer().setAllowFlight(true);
            PlayerJumpListener listener = new PlayerJumpListener(event.getPlayer(), (float) jumpSpeed);
            Bukkit.getPluginManager().registerEvents(listener, PylonBase.getInstance());
        }
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("jump-speed", UnitFormat.BLOCKS_PER_SECOND.format(jumpSpeed)),
                PylonArgument.of("hook-range", UnitFormat.BLOCKS.format(hookRange))
        );
    }

    public static class PlayerJumpListener implements Listener {
        private final Player player;
        private final float jumpSpeed;
        public PlayerJumpListener(Player player, float jumpSpeed){
            this.player = player;
            this.jumpSpeed = jumpSpeed;
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event){
            if(event.getPlayer() == player && event.hasExplicitlyChangedPosition() && !event.getPlayer().isJumping()){
                event.setCancelled(true);
            }
        }

        @EventHandler
        public void onStartFly(PlayerToggleFlightEvent event){
            if(event.getPlayer() == player){
                event.setCancelled(true);
                // Trigger a "jump"
                player.setVelocity(Vector.fromJOML(new Vector3f(0.0f, jumpSpeed, 0.0f)));
                Dispose();
            }
        }

        @EventHandler
        public void onSneak(PlayerToggleSneakEvent event){
            if(event.getPlayer() == player && event.isSneaking()){
                Dispose();
            }
        }

        @EventHandler
        public void onPlayerLeave(PlayerQuitEvent event) {
            if (event.getPlayer() == player) {
                Dispose();
            }
        }

        @EventHandler
        public void OnPluginDisable(PluginDisableEvent event){
            if(event.getPlugin() == PylonBase.getInstance()){
                Dispose();
            }
        }

        private void Dispose(){
            player.setGravity(true);
            player.setAllowFlight(false);
            PlayerMoveEvent.getHandlerList().unregister(this);
            PlayerToggleSneakEvent.getHandlerList().unregister(this);
            PlayerQuitEvent.getHandlerList().unregister(this);
            PlayerToggleFlightEvent.getHandlerList().unregister(this);
            PluginDisableEvent.getHandlerList().unregister(this);
        }
    }
}
