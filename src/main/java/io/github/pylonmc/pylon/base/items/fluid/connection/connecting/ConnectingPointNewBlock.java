package io.github.pylonmc.pylon.base.items.fluid.connection.connecting;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeConnector;
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
    @NotNull public BlockPosition position() {
        return position;
    }

    @Override
    @NotNull public Vector3f offset() {
        return new Vector3f(0, 0, 0);
    }

    @Override
    @Nullable public BlockFace allowedFace() {
        return null;
    }

    @Override
    public boolean isStillValid() {
        return true;
    }

    @Override
    @NotNull public FluidConnectionInteraction create() {
        FluidPipeConnector connector = (FluidPipeConnector) BlockStorage.placeBlock(position, PylonBlocks.FLUID_PIPE_CONNECTOR);
        Preconditions.checkState(connector != null);
        FluidConnectionInteraction interaction = connector.getFluidConnectionInteraction();
        Preconditions.checkState(interaction != null);
        return interaction;
    }

    @Override
    public @NotNull Set<UUID> getConnectedInteractions() {
        return Set.of();
    }
}
