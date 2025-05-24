package io.github.pylonmc.pylon.base.items.fluid.connection.connecting;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.base.PylonEntities;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.fluid.pipe.item.FluidPipe;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeDisplay;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeMarker;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.pylonmc.pylon.base.items.fluid.connection.connecting.ConnectingTask.blocksOnPath;


public class ConnectingService implements Listener {

    // Maps player doing the connection to the origin of the connection
    private static final Map<Player, ConnectingTask> connectionsInProgress = new HashMap<>();

    public static void startConnection(
            @NotNull Player player,
            @NotNull ConnectingPoint startPoint,
            @NotNull FluidPipe pipe
    ) {
        Preconditions.checkState(!connectionsInProgress.containsKey(player));
        connectionsInProgress.put(player, new ConnectingTask(player, startPoint, pipe));
    }

    public static void cancelConnection(@NotNull Player player) {
        Preconditions.checkState(connectionsInProgress.containsKey(player));
        connectionsInProgress.remove(player).cancel();
    }

    public static @Nullable UUID placeConnection(@NotNull Player player) {
        ConnectingTask connectingTask = connectionsInProgress.get(player);
        Preconditions.checkState(connectingTask != null);

        ConnectingTask.Result result = connectingTask.finish();
        if (result == null) {
            return null;
        }

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.getInventory().getItem(EquipmentSlot.HAND).subtract(result.pipesUsed());
        }

        connectionsInProgress.remove(player);

        PylonItem pylonItem = PylonItem.fromStack(player.getInventory().getItem(EquipmentSlot.HAND));
        if (result.to().getFace() == null && pylonItem instanceof FluidPipe) {
            // start new connection from the point we just placed if it didn't have a face
            // if it does have a face, we can't go any further so don't bother starting a new connection
            ConnectingPointInteraction connectingPoint = new ConnectingPointInteraction(result.to());
            connectionsInProgress.put(player, new ConnectingTask(player, connectingPoint, connectingTask.getPipe()));
        }
        return result.to().getPoint().getSegment();
    }

    public static boolean isConnecting(@NotNull Player player) {
        return connectionsInProgress.containsKey(player);
    }

    public static void cleanup() {
        for (Player player : connectionsInProgress.keySet()) {
            cancelConnection(player);
        }
    }

    private static boolean taskContainsBlocksInChunk(@NotNull ConnectingTask task, @NotNull ChunkPosition chunk) {
        return task.getFrom().position().getChunk().equals(chunk) || task.getTo().position().getChunk().equals(chunk);
    }

    public static @NotNull FluidPipeDisplay connect(
            @NotNull ConnectingPoint from,
            @NotNull ConnectingPoint to,
            @NotNull FluidPipe pipe
    ) {
        FluidConnectionInteraction originInteraction = from.create();
        FluidConnectionInteraction targetInteraction = to.create();

        int pipeAmount = ConnectingTask.pipesUsed(from.position(), to.position());

        FluidPipeDisplay pipeDisplay
                = FluidPipeDisplay.make(PylonEntities.FLUID_PIPE_DISPLAY, pipe, pipeAmount, originInteraction, targetInteraction);

        originInteraction.getConnectedPipeDisplays().add(pipeDisplay.getUuid());
        targetInteraction.getConnectedPipeDisplays().add(pipeDisplay.getUuid());

        for (Block block : blocksOnPath(from.position(), to.position())) {
            FluidPipeMarker marker = (FluidPipeMarker) BlockStorage.placeBlock(block, PylonBlocks.FLUID_PIPE_MARKER);
            Preconditions.checkState(marker != null);
            marker.setPipeDisplay(pipeDisplay.getUuid());
            marker.setFrom(originInteraction.getUuid());
            marker.setTo(targetInteraction.getUuid());
        }

        FluidManager.connect(originInteraction.getPoint(), targetInteraction.getPoint());

        return pipeDisplay;
    }

    public static void disconnect(
            @NotNull FluidConnectionInteraction from,
            @NotNull FluidConnectionInteraction to,
            boolean removeEmptyConnectors
    ) {
        // the pipe display will be the only common display between the two points
        Collection<UUID> pipeDisplaySet = new HashSet<>(from.getConnectedPipeDisplays());
        pipeDisplaySet.retainAll(to.getConnectedPipeDisplays());
        Preconditions.checkState(pipeDisplaySet.size() == 1);

        FluidPipeDisplay pipeDisplay = EntityStorage.getAs(FluidPipeDisplay.class, pipeDisplaySet.iterator().next());
        Preconditions.checkState(pipeDisplay != null);

        pipeDisplay.getEntity().remove();

        FluidManager.disconnect(from.getPoint(), to.getPoint());

        // remove markers
        for (Block block : blocksOnPath(from.getPoint().getPosition(), to.getPoint().getPosition())) {
            BlockStorage.breakBlock(block);
        }

        // remove the deleted pipe from interactions
        from.getConnectedPipeDisplays().remove(pipeDisplay.getUuid());
        to.getConnectedPipeDisplays().remove(pipeDisplay.getUuid());

        // delete connectors if they're now empty
        if (BlockStorage.get(from.getPoint().getPosition()) instanceof FluidPipeConnector connector
                && removeEmptyConnectors
                && from.getConnectedPipeDisplays().isEmpty()
        ) {
            BlockStorage.breakBlock(connector.getBlock());
        }
        if (BlockStorage.get(to.getPoint().getPosition()) instanceof FluidPipeConnector connector
                && removeEmptyConnectors
                && to.getConnectedPipeDisplays().isEmpty()
        ) {
            BlockStorage.breakBlock(connector.getBlock());
        }
    }

    @EventHandler
    private static void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        if (connectionsInProgress.containsKey(event.getPlayer())) {
            cancelConnection(event.getPlayer());
        }
    }

    @EventHandler
    private static void onPlayerScroll(@NotNull PlayerItemHeldEvent event) {
        ItemStack heldItem = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        if (PylonItem.fromStack(heldItem) instanceof FluidPipe && connectionsInProgress.containsKey(event.getPlayer())) {
            cancelConnection(event.getPlayer());
        }
    }

    /**
     * Intended to prevent issues if players teleport away while placing a pipe
     */
    @EventHandler
    private static void onChunkUnload(@NotNull ChunkUnloadEvent event) {
        List<ConnectingTask> toRemove = connectionsInProgress.values()
                .stream()
                .filter(task -> taskContainsBlocksInChunk(task, new ChunkPosition(event.getChunk())))
                .toList();
        for (ConnectingTask task : toRemove) {
            task.cancel();
        }
        connectionsInProgress.values().removeAll(toRemove);
    }
}
