package io.github.pylonmc.pylon.base.misc;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.items.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
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


public class PipeConnectorService implements Listener {

    // Maps player doing the connection to the origin of the connection
    private static final Map<Player, FluidConnectionPoint> connectionsInProgress = new HashMap<>();

    public static void startConnection(@NotNull Player player, @NotNull FluidConnectionPoint startPoint) {
        connectionsInProgress.put(player, startPoint);
    }

    public static void finishConnection(@NotNull Player player, @NotNull FluidConnectionPoint endPoint) {
        FluidConnectionPoint startPoint = connectionsInProgress.remove(player);
        Preconditions.checkState(startPoint != null, "Attempt to finish a connection with no start point");
        FluidManager.connect(startPoint, endPoint);
    }

    public static void cancelConnection(@NotNull Player player) {
        connectionsInProgress.remove(player);
    }

    public static @Nullable FluidConnectionPoint getOrigin(@NotNull Player player) {
        return connectionsInProgress.get(player);
    }

    @EventHandler
    public static void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        cancelConnection(event.getPlayer());
    }

    @EventHandler
    public static void scrollEvent(@NotNull PlayerItemHeldEvent event) {
        ItemStack heldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        if (PylonItem.fromStack(heldItem) instanceof FluidPipe) {
            cancelConnection(event.getPlayer());
        }
    }
}
