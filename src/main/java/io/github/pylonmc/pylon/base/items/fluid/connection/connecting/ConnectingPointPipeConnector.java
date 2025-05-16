package io.github.pylonmc.pylon.base.items.fluid.connection.connecting;

import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


public class ConnectingPointPipeConnector implements ConnectingPoint {

    @Getter private final FluidPipeConnector connector;

    public ConnectingPointPipeConnector(@NotNull FluidPipeConnector connector) {
        this.connector = connector;
    }
}
