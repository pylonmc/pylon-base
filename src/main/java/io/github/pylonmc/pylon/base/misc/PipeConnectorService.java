package io.github.pylonmc.pylon.base.misc;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.base.items.fluid.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.fluid.FluidConnector;
import io.github.pylonmc.pylon.base.items.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.LineBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class PipeConnectorService implements Listener {

    private static final int MAX_PIPE_PLACEMENT_DISTANCE = 5;

    private static class ConnectionInProgress {
        private static final int PLAYER_SIGHT_RANGE = 5;

        @Getter private final Player player;
        @Getter private final BukkitTask task;
        @Getter private final FluidConnectionInteraction origin;
        private final Vector3f originOffset;
        @Nullable private FluidConnectionInteraction target;
        private Vector3f targetOffset;
        @Getter private final ItemDisplay display;

        public ConnectionInProgress(@NotNull Player player, @NotNull FluidConnectionInteraction origin) {
            this.player = player;
            this.origin = origin;

            if (origin.getFace() != null && origin.getRadius() != null) {
                this.originOffset = origin.getFace().getDirection().toVector3f().mul(origin.getRadius());
            } else {
                this.originOffset = new Vector3f(0, 0, 0);
            }

            this.display = new ItemDisplayBuilder()
                    .material(Material.WHITE_CONCRETE)
                    .brightness(15)
                    .transformation(new TransformBuilder()
                            .translate(originOffset)
                            .scale(0.1F, 0.1F, 0.1F)
                    )
                    .build(origin.getPoint().getPosition().getLocation().toCenterLocation());

            this.task = Bukkit.getScheduler().runTaskTimer(PylonBase.getInstance(), () -> {
                recalculateTarget();

                Block block = getTargetBlock();
                if (target != null && origin.getUuid().equals(target.getUuid())) {
                    player.sendActionBar(Component.translatable("pylon.pylonbase.pipe.cannot-connect-to-same-point"));
                    display.setItemStack(new ItemStack(Material.RED_CONCRETE));
                } else if (block != null && !block.getType().isAir()) {
                    player.sendActionBar(Component.translatable("pylon.pylonbase.pipe.cannot-place-in-block"));
                    display.setItemStack(new ItemStack(Material.RED_CONCRETE));
                } else {
                    player.sendActionBar(Component.translatable("pylon.pylonbase.pipe.connecting"));
                    display.setItemStack(new ItemStack(Material.WHITE_CONCRETE));
                }

                display.setTransformationMatrix(new LineBuilder()
                        .from(originOffset)
                        .to(targetOffset)
                        .thickness(0.1)
                        .build().buildForItemDisplay());
            }, 0, 1); // TODO make configurable
        }

        public void cancel() {
            task.cancel();
            display.remove();
            player.sendActionBar(Component.translatable("pylon.pylonbase.pipe.cancelled"));
        }

        // Returns null if target is an entity
        public @Nullable Block getTargetBlock() {
            if (target != null) {
                return null;
            }
            Location originLocation = origin.getPoint().getPosition().getLocation();
            return new BlockPosition(originLocation.add(Vector.fromJOML(targetOffset))).getBlock();
        }

        public @Nullable UUID finish() {
            Block block = getTargetBlock();
            if (block != null) {
                if (!block.getType().isAir()) {
                    return null;
                }
                FluidConnector connector = (FluidConnector) BlockStorage.placeBlock(block, PylonBlocks.FLUID_CONNECTOR);
                target = connector.getFluidConnectionInteraction();
            }

            task.cancel();
            display.remove();

            FluidManager.connect(origin.getPoint(), target.getPoint());
            player.sendActionBar(Component.translatable("pylon.pylonbase.pipe.connected"));
            return origin.getPoint().getSegment();
        }

        private boolean pathIntersectsBlocks() {
            Block originBlock = origin.getPoint().getPosition().getBlock();
            if (targetOffset != null) {
                Block targetBlock = originBlock.get(Vector.fromJOML(targetOffset));
            }
        }

        /**
         * Figures out where the target of the pipe is and adjusts the targetOffset and targetConnection
         */
        private void recalculateTarget() {
            Vector3f playerLookPosition = player.getEyeLocation().toVector().toVector3f();
            Vector3f playerLookDirection = player.getEyeLocation().getDirection().toVector3f();
            Vector3f originPosition = origin.getPoint().getPosition().getLocation().toCenterLocation().toVector().toVector3f();

            if (getTargetEntity(player) instanceof Interaction interaction) {
                if (EntityStorage.get(interaction) instanceof FluidConnectionInteraction newTarget) {
                    Vector3f newTargetOffset = interaction.getLocation()
                            .subtract(origin.getPoint().getPosition().getLocation().toCenterLocation().toVector())
                            .toVector().toVector3f();
                    if (isValidTarget(newTargetOffset, origin.getFace())) {
                        target = newTarget;
                        targetOffset = newTargetOffset;
                        return;
                    }
                }
            }

            target = null;
            targetOffset = new Vector3f(0, 0, 0);
            float distance = Float.MAX_VALUE;

            Vector3f targetX = getTargetOnAxis(playerLookPosition, playerLookDirection, originPosition, new Vector3i(1, 0, 0));
            Vector3f absoluteTargetX = new Vector3f(targetX).add(originPosition);
            float distanceX = findClosestDistanceBetweenLineAndPoint(absoluteTargetX, playerLookPosition, playerLookDirection);
            if (distanceX < distance && isValidTarget(targetX, origin.getFace())) {
                targetOffset = targetX;
                distance = distanceX;
            }

            Vector3f targetY = getTargetOnAxis(playerLookPosition, playerLookDirection, originPosition, new Vector3i(0, 1, 0));
            Vector3f absoluteTargetY = new Vector3f(targetY).add(originPosition);
            float distanceY = findClosestDistanceBetweenLineAndPoint(absoluteTargetY, playerLookPosition, playerLookDirection);
            if (distanceY < distance && isValidTarget(targetY, origin.getFace())) {
                targetOffset = targetY;
                distance = distanceY;
            }

            Vector3f targetZ = getTargetOnAxis(playerLookPosition, playerLookDirection, originPosition, new Vector3i(0, 0, 1));
            Vector3f absoluteTargetZ = new Vector3f(targetZ).add(originPosition);
            float distanceZ = findClosestDistanceBetweenLineAndPoint(absoluteTargetZ, playerLookPosition, playerLookDirection);
            if (distanceZ < distance && isValidTarget(targetZ, origin.getFace())) {
                targetOffset = targetZ;
                distance = distanceZ;
            }
        }

        public static @Nullable Entity getTargetEntity(@NotNull Player player) {
            Vector3f playerLookPosition = player.getEyeLocation().toVector().toVector3f();
            Vector3f playerLookDirection = player.getEyeLocation().getDirection().toVector3f();

            List<Entity> entities = player.getNearbyEntities(PLAYER_SIGHT_RANGE, PLAYER_SIGHT_RANGE, PLAYER_SIGHT_RANGE);

            for (Entity entity : entities) {
                RayTraceResult result = entity.getBoundingBox().rayTrace(
                        Vector.fromJOML(playerLookPosition),
                        Vector.fromJOML(playerLookDirection),
                        PLAYER_SIGHT_RANGE
                );
                if (result != null) {
                    return entity;
                }
            }

            return null;
        }

        private static boolean isValidTarget(@NotNull Vector3f target, @Nullable BlockFace allowedFace) {
            return !target.equals(new Vector3f(0, 0, 0), 1.0e-3F)
                    && (allowedFace == null || new Vector3f(target).normalize().equals(allowedFace.getDirection().toVector3f(), 1.0e-3F));
        }

        // Casts a ray where the player's looking and finds the closest block on a given axis
        private static Vector3f getTargetOnAxis(Vector3f playerLookPosition, Vector3f playerLookDirection, Vector3f origin, Vector3i axis) {
            float solution = findClosestPointBetweenSkewLines(playerLookPosition, playerLookDirection, origin, new Vector3f(axis));
            int lambda = Math.clamp(Math.round(solution), -MAX_PIPE_PLACEMENT_DISTANCE, MAX_PIPE_PLACEMENT_DISTANCE);
            return new Vector3f(axis).mul(lambda);
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
    }

    // Maps player doing the connection to the origin of the connection
    private static final Map<Player, ConnectionInProgress> connectionsInProgress = new HashMap<>();

    public static void startConnection(@NotNull Player player, @NotNull FluidConnectionInteraction startPoint) {
        Preconditions.checkState(!connectionsInProgress.containsKey(player));
        connectionsInProgress.put(player, new ConnectionInProgress(player, startPoint));
    }

    public static @Nullable UUID finishConnection(@NotNull Player player) {
        ConnectionInProgress connectionInProgress = connectionsInProgress.remove(player);
        Preconditions.checkState(connectionInProgress != null, "Attempt to finish a connection with no start point");
        return connectionInProgress.finish();
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
