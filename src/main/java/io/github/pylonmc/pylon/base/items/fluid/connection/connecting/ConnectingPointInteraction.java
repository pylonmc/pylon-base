package io.github.pylonmc.pylon.base.items.fluid.connection.connecting;

import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


public class ConnectingPointInteraction implements ConnectingPoint {

    @Getter private final FluidConnectionInteraction point;

    public ConnectingPointInteraction(@NotNull FluidConnectionInteraction point) {
        this.point = point;
    }
}
