package io.github.pylonmc.pylon.base.items.fluid.connection.connecting;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipe;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ConnectingService implements Listener {

    // Maps player doing the connection to the origin of the connection
    private static final Map<Player, ConnectingInProgress> connectionsInProgress = new HashMap<>();

    public static void startConnection(
            @NotNull Player player,
            @NotNull FluidConnectionInteraction startPoint,
            @NotNull FluidPipe.Schema pipe
    ) {
        Preconditions.checkState(!connectionsInProgress.containsKey(player));
        connectionsInProgress.put(player, new ConnectingInProgress(player, startPoint, pipe));
    }

    public static @Nullable UUID finishConnection(@NotNull Player player) {
        ConnectingInProgress connectingInProgress = connectionsInProgress.get(player);
        Preconditions.checkState(connectingInProgress != null, "Attempt to finish a connection with no start point");
        UUID uuid = connectingInProgress.finish();
        if (uuid != null) {
            connectionsInProgress.remove(player);
        }
        return uuid;
    }

    public static void cancelConnection(@NotNull Player player) {
        connectionsInProgress.remove(player).cancel();
    }

    public static boolean isConnecting(@NotNull Player player) {
        return connectionsInProgress.get(player) != null;
    }

    public static void cleanup() {
        for (Player player : connectionsInProgress.keySet()) {
            cancelConnection(player);
        }
    }

    @EventHandler
    public static void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        if (connectionsInProgress.containsKey(event.getPlayer())) {
            cancelConnection(event.getPlayer());
        }
    }

    @EventHandler
    public static void scrollEvent(@NotNull PlayerItemHeldEvent event) {
        ItemStack heldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        if (PylonItem.fromStack(heldItem) instanceof FluidPipe) {
            if (connectionsInProgress.containsKey(event.getPlayer())) {
                cancelConnection(event.getPlayer());
            }
        }
    }
}
