package io.github.pylonmc.pylon.base.items.fluid.connection.connecting;

import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeMarker;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


public class ConnectingPointPipeMarker implements ConnectingPoint {

    @Getter private final FluidPipeMarker marker;

    public ConnectingPointPipeMarker(@NotNull FluidPipeMarker marker) {
        this.marker = marker;
    }
}
