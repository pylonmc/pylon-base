package io.github.pylonmc.pylon.base.fluid.pipe.connection.connecting;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;
import java.util.UUID;


public record ConnectingPointNewBlock(@NotNull BlockPosition position) implements ConnectingPoint {

    @Override
    public @NotNull BlockPosition position() {
        return position;
    }

    @Override
    public @NotNull Vector3f offset() {
        return new Vector3f(0, 0, 0);
    }

    @Override
    public @Nullable BlockFace allowedFace() {
        return null;
    }

    @Override
    public boolean isStillValid() {
        return true;
    }

    @Override
    public @NotNull FluidConnectionInteraction create() {
        FluidPipeConnector connector = (FluidPipeConnector) BlockStorage.placeBlock(position, BaseKeys.FLUID_PIPE_CONNECTOR);
        Preconditions.checkState(connector != null);
        FluidConnectionInteraction interaction = connector.getFluidConnectionInteraction();
        Preconditions.checkState(interaction != null);
        return interaction;
    }

    @Override
    public @NotNull Set<UUID> getConnectedInteractions() {
        return Set.of();
    }

    @Override
    public @Nullable FluidConnectionInteraction getInteraction() {
        return null;
    }
}
