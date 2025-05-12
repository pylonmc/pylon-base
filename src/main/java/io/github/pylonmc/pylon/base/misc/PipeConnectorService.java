package io.github.pylonmc.pylon.base.misc;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.items.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.LineBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;


public class PipeConnectorService implements Listener {

    private static final int MAX_PIPE_PLACEMENT_DISTANCE = 5;

    private static class ConnectionInProgress {
        @Getter private final Player player;
        @Getter private final BukkitTask task;
        @Getter private final FluidConnectionPoint origin;
        @Getter @Nullable private final BlockPosition target;
        @Getter private final ItemDisplay display;

        public ConnectionInProgress(@NotNull Player player, @NotNull FluidConnectionPoint origin) {
            this.player = player;
            this.origin = origin;
            this.target = origin.getPosition();
            this.display = new ItemDisplayBuilder()
                    .material(Material.LIGHT_GRAY_CONCRETE)
                    .brightness(15)
                    .transformation(new TransformBuilder()
                            .translate(0.0, 0.0, 0.0)
                            .scale(0.1F, 0.1F, 0.1F)
                    )
                    .build(origin.getPosition().getLocation().toCenterLocation());

            this.task = Bukkit.getScheduler().runTaskTimer(PylonBase.getInstance(), () -> {
                player.sendActionBar(Component.translatable("pylon.pylonbase.pipe.connecting"));
                Vector3f playerLookPosition = player.getEyeLocation().toVector().toVector3f();
                Vector3f playerLookDirection = player.getEyeLocation().getDirection().toVector3f();
                Vector3f originPosition = origin.getPosition().getLocation().toVector().toVector3f();

                float solutionX = findClosestPointBetweenSkewLines(playerLookPosition, playerLookDirection, originPosition, new Vector3f(1, 0, 0));
                float lambdaX = Math.clamp((int) Math.floor(solutionX), -MAX_PIPE_PLACEMENT_DISTANCE, MAX_PIPE_PLACEMENT_DISTANCE);
                Vector3f targetX = new Vector3f(originPosition)
                        .add(new Vector3f(1, 0, 0).mul(lambdaX))
                        .sub(originPosition);
                float distanceX = findClosestDistanceBetweenLineAndPoint(targetX, playerLookPosition, playerLookDirection);

                display.setTransformationMatrix(new LineBuilder()
                        .from(new Vector3f(0, 0, 0))
                        .to(targetX)
                        .thickness(0.1)
                        .build().buildForItemDisplay());
            }, 0, 1); // TODO make configurable
        }

        public void cancel(@NotNull Component message) {
            task.cancel();
            display.remove();
            player.sendActionBar(message);
        }
    }

    // returns lambda
    // r1 = p1 + lambda*d1 (line 1)
    // r2 = p2 + mu*d2 (line 2)
    // r3 = p3 + phi*d3 (an imagined perpendicular line between them used to solve for closest points)
    private static float findClosestPointBetweenSkewLines(Vector3f p1, Vector3f d1, Vector3f p2, Vector3f d2) {
        Vector3f d3 = new Vector3f(d1).cross(d2);
        // solve for lamdba, mu, phi using the matrix inversion method
        Matrix3f mat = new Matrix3f(d1, new Vector3f(d2).mul(-1), d3)
                .invert();
        Vector3f solution = new Vector3f(p2).sub(p1).mul(mat);
        return solution.y;
    }

    // https://math.stackexchange.com/questions/1905533/find-perpendicular-distance-from-point-to-line-in-3d
    private static float findClosestDistanceBetweenLineAndPoint(Vector3f p, Vector3f p1, Vector3f d1) {
        Vector3f v = new Vector3f(p).sub(p1);
        float t = new Vector3f(v).dot(d1);
        Vector3f closestPoint = new Vector3f(p1).add(new Vector3f(d1).mul(t));
        return (new Vector3f(closestPoint).sub(p)).length();
    }

    // Maps player doing the connection to the origin of the connection
    private static final Map<Player, ConnectionInProgress> connectionsInProgress = new HashMap<>();

    public static void startConnection(@NotNull Player player, @NotNull FluidConnectionPoint startPoint, @NotNull BlockFace face) {
        connectionsInProgress.put(player, new ConnectionInProgress(player, startPoint));
    }

    public static void finishConnection(@NotNull Player player, @NotNull FluidConnectionPoint endPoint) {
        ConnectionInProgress connectionInProgress = connectionsInProgress.remove(player);
        Preconditions.checkState(connectionInProgress != null, "Attempt to finish a connection with no start point");
        connectionInProgress.cancel(Component.translatable("pylon.pylonbase.pipe.connected"));
        if (endPoint == connectionInProgress.origin) {
            return;
        }
        FluidManager.connect(connectionInProgress.origin, endPoint);
    }

    public static void cancelConnection(@NotNull Player player) {
        connectionsInProgress.remove(player).cancel(Component.translatable("pylon.pylonbase.pipe.cancelled"));
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
        cancelConnection(event.getPlayer());
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
