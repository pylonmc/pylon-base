package io.github.pylonmc.pylon.base.fluid.pipe.connection.connecting;

import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;
import java.util.UUID;


public interface ConnectingPoint {

    /**
     * Block the point is tied to
     */
    @NotNull
    BlockPosition position();

    /**
     * Where the connecting point physically exists relative to its parent block
     */
    @NotNull Vector3f offset();

    /**
     * Which direction we can create the pipe in. If null, the pipe can be in any direction.
     */
    @Nullable BlockFace allowedFace();

    /**
     * Has something changed (eg a block being removed) that means we can't use this point any more?
     */
    boolean isStillValid();

    /**
     * Perform logic to make this one of the points of a new pipe, for example by splitting an
     * existing pipe or placing a new connection point. This should always yield a new
     * connection point which we can connect the pipe to.
     */
    @NotNull FluidConnectionInteraction create();

    @NotNull Set<UUID> getConnectedInteractions();
}
