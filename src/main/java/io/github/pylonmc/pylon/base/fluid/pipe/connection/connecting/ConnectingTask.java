package io.github.pylonmc.pylon.base.fluid.pipe.connection.connecting;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.BaseConfig;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeDisplay;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeMarker;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.content.machines.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.LineBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.RoundingMode;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;


public class ConnectingTask {

    public record Result(FluidConnectionInteraction to, int pipesUsed) {}

    @Getter private final Player player;
    @Getter private final BukkitTask task;
    @Getter private final FluidPipe pipe;
    @Getter private final ConnectingPoint from;
    @Getter private ConnectingPoint to;
    @Getter private final ItemDisplay display;
    private boolean isValid;

    public ConnectingTask(
            @NotNull Player player,
            @NotNull ConnectingPoint from,
            @NotNull FluidPipe pipe
    ) {
        this.player = player;
        this.from = from;
        this.pipe = pipe;
        this.display = new ItemDisplayBuilder()
                .material(Material.WHITE_CONCRETE)
                .brightness(15)
                // transformation will be set later, make the block invisible now to prevent flash of white on place
                .transformation(new TransformBuilder().scale(0))
                .build(from.position().getLocation().toCenterLocation());
        this.to = from; // just in case recalculateTarget() in tick() does not get a new target, so target isn't null
        this.task = Bukkit.getScheduler().runTaskTimer(
                PylonBase.getInstance(),
                () -> tick(true),
                0,
                BaseConfig.PIPE_PLACEMENT_TASK_INTERVAL_TICKS
        );
    }

    private void tick(boolean interpolate) {
        PylonItem pylonItem = PylonItem.fromStack(player.getInventory().getItem(EquipmentSlot.HAND));
        if (!(pylonItem instanceof FluidPipe)) {
            ConnectingService.cancelConnection(player);
            return;
        }

        if (!from.isStillValid() || !to.isStillValid()) {
            ConnectingService.cancelConnection(player);
            return;
        }

        BlockPosition previousToPosition = to.position();

        recalculateTo();

        if (player.getGameMode() != GameMode.CREATIVE
                && pipesUsed(from.position(), to.position()) > player.getInventory().getItem(EquipmentSlot.HAND).getAmount()
        ) {
            isValid = false;
            player.sendActionBar(Component.translatable("pylon.pylonbase.message.pipe.not_enough_pipes"));
            display.setItemStack(new ItemStack(Material.RED_CONCRETE));
        } else if (!isPipeTypeValid()) {
            isValid = false;
            player.sendActionBar(Component.translatable("pylon.pylonbase.message.pipe.not_of_same_type"));
            display.setItemStack(new ItemStack(Material.RED_CONCRETE));
        } else if (!isPlacementValid()) {
            isValid = false;
            player.sendActionBar(Component.translatable("pylon.pylonbase.message.pipe.cannot_place_here"));
            display.setItemStack(new ItemStack(Material.RED_CONCRETE));
        } else {
            isValid = true;
            player.sendActionBar(Component.translatable("pylon.pylonbase.message.pipe.connecting"));
            display.setItemStack(new ItemStack(Material.WHITE_CONCRETE));
        }

        Vector3f targetOffset = to.position().getLocation().toVector().toVector3f()
                .add(to.offset())
                .sub(from.position().getLocation().toVector().toVector3f());

        Vector3i difference = to.position().getVector3i().sub(previousToPosition.getVector3i());

        display.setTransformationMatrix(new LineBuilder()
                .from(from.offset())
                .to(targetOffset)
                .thickness(0.101) // not 0.1 to prevent z-fighting with existing pipes
                .build().buildForItemDisplay());

        if (isCardinalDirection(difference) && interpolate) {
            display.setInterpolationDelay(0);
            display.setInterpolationDuration(BaseConfig.PIPE_PLACEMENT_TASK_INTERVAL_TICKS);
        }
    }

    public void cancel() {
        task.cancel();
        display.remove();
        player.sendActionBar(Component.empty());
    }

    public @Nullable ConnectingTask.Result finish() {
        if (!isValid) {
            return null;
        }

        task.cancel();
        display.remove();
        player.sendActionBar(Component.empty());

        FluidPipeDisplay pipeDisplay = ConnectingService.connect(from, to, pipe);
        FluidConnectionInteraction toInteraction = pipeDisplay.getTo();
        Preconditions.checkState(toInteraction != null);
        return new Result(toInteraction, pipesUsed(from.position(), to.position()));
    }

    public boolean pathIntersectsBlocks() {
        return blocksOnPath(from.position(), to.position()).stream().anyMatch(block -> !block.getType().isAir())
                || to instanceof ConnectingPointNewBlock && !to.position().getBlock().getType().isAir();
    }

    public static int pipesUsed(@NotNull BlockPosition from, @NotNull BlockPosition to) {
        return blocksOnPath(from, to).size() + 1;
    }

    private static boolean isCardinalDirection(@NotNull Vector3i vector) {
         return vector.x != 0 && vector.y == 0 && vector.z == 0
                || vector.x == 0 && vector.y != 0 && vector.z == 0
                || vector.x == 0 && vector.y == 0 && vector.z != 0;
    }

    /**
     * Does not include first or last block
     */
    public static @NotNull List<Block> blocksOnPath(@NotNull BlockPosition from, @NotNull BlockPosition to) {
        Block originBlock = from.getBlock();
        Vector3i offset = to.getLocation().toVector().toVector3i()
                .sub(originBlock.getLocation().toVector().toVector3i());

        List<Block> blocks = new ArrayList<>();
        Block block = originBlock;
        // math.round to make it an integer - the length will already be an integer
        for (int i = 0; i < Math.round(offset.length()) - 1; i++) {
            block = block.getRelative(PylonUtils.vectorToBlockFace(offset));
            blocks.add(block);
        }

        return blocks;
    }

    /**
     * Figures out where the target of the pipe is and adjusts the targetOffset and targetConnection
     */
    private void recalculateTo() {
        if (getTargetEntity(player) instanceof Interaction interaction) {
            if (EntityStorage.get(interaction) instanceof FluidConnectionInteraction newTarget) {
                Vector3f newTargetOffset = interaction.getLocation().add(new Vector(0, interaction.getHeight() / 2, 0))
                        .subtract(from.position().getLocation().toCenterLocation().toVector())
                        .toVector().toVector3f();
                Vector3i newTargetDifference = from.position()
                        .minus(newTarget.getPoint().getPosition())
                        .getVector3i();
                if (isValidTarget(newTargetOffset, from.allowedFace()) && isCardinalDirection(newTargetDifference)) {
                    to = new ConnectingPointInteraction(newTarget);
                    return;
                }
            }
        }

        float distance = Float.MAX_VALUE;

        distance = processAxis(new Vector3i(1, 0, 0), distance);
        distance = processAxis(new Vector3i(0, 1, 0), distance);
        processAxis(new Vector3i(0, 0, 1), distance);
    }

    // helper function for recalculateTarget, returns new distance
    private float processAxis(@NotNull Vector3i axis, float distance) {
        Vector3f playerLookPosition = player.getEyeLocation().toVector().toVector3f();
        Vector3f playerLookDirection = player.getEyeLocation().getDirection().toVector3f();
        Vector3f originPosition = from.position().getLocation().toCenterLocation().toVector().toVector3f();

        Vector3f newTarget = getTargetOnAxis(playerLookPosition, playerLookDirection, originPosition, axis);
        Vector3f newAbsoluteTarget = new Vector3f(newTarget).add(originPosition);
        float newDistance = findClosestDistanceBetweenLineAndPoint(newAbsoluteTarget, playerLookPosition, playerLookDirection);

        if (newDistance < distance && isValidTarget(newTarget, from.allowedFace())) {
            BlockPosition newTargetPosition = from.position().plus(new Vector3i(newTarget, RoundingMode.HALF_DOWN));
            PylonBlock newTargetBlock = BlockStorage.get(newTargetPosition);
            if (newTargetBlock instanceof FluidPipeMarker marker) {
                to = new ConnectingPointPipeMarker(marker);
            } else if (newTargetBlock instanceof FluidPipeConnector connector) {
                to = new ConnectingPointPipeConnector(connector);
            } else {
                to = new ConnectingPointNewBlock(newTargetPosition);
            }
            return newDistance;
        }
        return distance;
    }

    public static @Nullable Entity getTargetEntity(@NotNull Player player) {
        Vector3f playerLookPosition = player.getEyeLocation().toVector().toVector3f();
        Vector3f playerLookDirection = player.getEyeLocation().getDirection().toVector3f();

        double range = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE).getValue();
        List<Entity> entities = player.getNearbyEntities(range, range, range);

        for (Entity entity : entities) {
            RayTraceResult result = entity.getBoundingBox().rayTrace(
                    Vector.fromJOML(playerLookPosition),
                    Vector.fromJOML(playerLookDirection),
                    range
            );
            //noinspection VariableNotUsedInsideIf
            if (result != null) {
                return entity;
            }
        }

        return null;
    }

    private boolean isPipeTypeValid() {
        if (to instanceof ConnectingPointPipeMarker(FluidPipeMarker marker)) {
            FluidPipeDisplay pipeDisplay = marker.getPipeDisplay();
            Preconditions.checkState(pipeDisplay != null);
            return pipeDisplay.getPipe().equals(pipe);
        }

        //noinspection SimplifiableIfStatement (not actually simpler)
        if (to instanceof ConnectingPointPipeConnector(FluidPipeConnector connector)) {
            return connector.getPipe().equals(pipe);
        }

        return true;
    }

    private boolean isPlacementValid() {
        Set<UUID> intersection = new HashSet<>(from.getConnectedInteractions());
        intersection.retainAll(to.getConnectedInteractions());
        boolean startAndEndNotAlreadyConnected = intersection.isEmpty();

        boolean startAndEndEmptyIfNewBlock = !(
                from instanceof ConnectingPointNewBlock(BlockPosition position1) && !position1.getBlock().getType().isAir()
                        ||
                to instanceof ConnectingPointNewBlock(BlockPosition position2) && !position2.getBlock().getType().isAir()
        );

        boolean startAndEndNotSameIfNewBlock = !(from instanceof ConnectingPointNewBlock(BlockPosition position1)
            && to instanceof ConnectingPointNewBlock(BlockPosition position2)
            && position1.equals(position2));

        return startAndEndNotAlreadyConnected
                && startAndEndEmptyIfNewBlock
                && startAndEndNotSameIfNewBlock
                && !pathIntersectsBlocks();
    }

    private static boolean isValidTarget(@NotNull Vector3f target, @Nullable BlockFace allowedFace) {
        return !target.equals(new Vector3f(0, 0, 0), 1.0e-3F)
                && (allowedFace == null || new Vector3f(target).normalize().equals(allowedFace.getDirection().toVector3f(), 1.0e-3F));
    }

    // Casts a ray where the player's looking and finds the closest block on a given axis
    private static Vector3f getTargetOnAxis(
            Vector3f playerLookPosition,
            Vector3f playerLookDirection,
            Vector3f origin,
            Vector3i axis
    ) {
        float solution = findClosestPointBetweenSkewLines(playerLookPosition, playerLookDirection, origin, new Vector3f(axis));
        int lambda = Math.clamp(
                Math.round(solution),
                -BaseConfig.PIPE_PLACEMENT_MAX_DISTANCE,
                BaseConfig.PIPE_PLACEMENT_MAX_DISTANCE
        );
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