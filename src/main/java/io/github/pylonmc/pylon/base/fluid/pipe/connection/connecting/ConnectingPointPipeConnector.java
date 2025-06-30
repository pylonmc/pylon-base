package io.github.pylonmc.pylon.base.fluid.pipe.connection.connecting;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;
import java.util.UUID;


public record ConnectingPointPipeConnector(@NotNull FluidPipeConnector connector) implements ConnectingPoint {

    public ConnectingPointPipeConnector(@NotNull FluidPipeConnector connector) {
        this.connector = connector;
    }

    @Override
    public @NotNull BlockPosition position() {
        return new BlockPosition(connector.getBlock());
    }

    @Override
    public @NotNull Vector3f offset() {
        return new Vector3f(0, 0, 0);
    }

    @Override
    public @Nullable  BlockFace allowedFace() {
        return null;
    }

    @Override
    public boolean isStillValid() {
        return BlockStorage.get(connector.getBlock()) instanceof FluidPipeConnector;
    }

    @Override
    public @NotNull FluidConnectionInteraction create() {
        FluidConnectionInteraction interaction = connector.getFluidConnectionInteraction();
        Preconditions.checkState(interaction != null);
        return interaction;
    }

    @Override
    public @NotNull Set<UUID> getConnectedInteractions() {
        FluidConnectionInteraction interaction = connector.getFluidConnectionInteraction();
        Preconditions.checkState(interaction != null);
        return Set.of(interaction.getUuid());
    }
}
