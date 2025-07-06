package io.github.pylonmc.pylon.base.fluid.pipe.connection.connecting;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeDisplay;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeMarker;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Set;
import java.util.UUID;


public record ConnectingPointPipeMarker(@NotNull FluidPipeMarker marker) implements ConnectingPoint {

    public ConnectingPointPipeMarker(@NotNull FluidPipeMarker marker) {
        this.marker = marker;
    }

    @Override
    public @NotNull BlockPosition position() {
        return new BlockPosition(marker.getBlock());
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
        return BlockStorage.get(marker.getBlock()) instanceof FluidPipeMarker;
    }

    @Override
    public @NotNull FluidConnectionInteraction create() {
        // get from/to interactions and pipe display associated with the marker
        FluidPipeDisplay pipeDisplay = marker.getPipeDisplay();
        Preconditions.checkState(pipeDisplay != null);
        FluidConnectionInteraction from = marker.getFrom();
        Preconditions.checkState(from != null);
        FluidConnectionInteraction to = marker.getTo();
        Preconditions.checkState(to != null);

        // disconnect from/to
        ConnectingService.disconnect(from, to, false);

        // place connector
        FluidPipeConnector connector
                = (FluidPipeConnector) BlockStorage.placeBlock(marker.getBlock(), BaseKeys.FLUID_PIPE_CONNECTOR);
        Preconditions.checkState(connector != null);
        FluidConnectionInteraction connectorInteraction = connector.getFluidConnectionInteraction();
        Preconditions.checkState(connectorInteraction != null);

        // connect connector to from/to
        ConnectingService.connect(
                new ConnectingPointInteraction(from),
                new ConnectingPointInteraction(connectorInteraction),
                pipeDisplay.getPipe()
        );
        ConnectingService.connect(
                new ConnectingPointInteraction(to),
                new ConnectingPointInteraction(connectorInteraction),
                pipeDisplay.getPipe()
        );

        return connectorInteraction;
    }

    @Override
    public @NotNull Set<UUID> getConnectedInteractions() {
        return Set.of(marker.getFromId(), marker.getToId());
    }
}
