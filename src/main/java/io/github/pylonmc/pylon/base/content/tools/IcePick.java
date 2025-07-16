package io.github.pylonmc.pylon.base.content.tools;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.core.util.PylonUtils.vectorToBlockFace;

public class IcePick extends PylonItem implements PylonInteractor {
    public IcePick(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        System.out.println("froze player");
        event.getPlayer().setGravity(false);
        PlayerJumpListener listener = new PlayerJumpListener(event.getPlayer());
        Bukkit.getPluginManager().registerEvents(listener, PylonBase.getInstance());
    }

    public static class PlayerJumpListener implements Listener {
        private final Player player;
        public PlayerJumpListener(Player player){
            this.player = player;
        }

        @EventHandler
        public void onJump(PlayerJumpEvent event){
            if(event.getPlayer() == player) {
                System.out.println("unfroze player");
                event.getPlayer().setGravity(false);
            }
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event){
            if(event.getPlayer() == player && vectorToBlockFace(event.getTo().getDirection()) != BlockFace.UP && event.hasExplicitlyChangedBlock()){
                event.setCancelled(true);
            }
        }
    }
}
