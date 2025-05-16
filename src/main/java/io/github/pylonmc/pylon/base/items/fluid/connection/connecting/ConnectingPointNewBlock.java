package io.github.pylonmc.pylon.base.items.fluid.connection.connecting;

import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;


public class ConnectingPointNewBlock implements ConnectingPoint {

    @Getter private final BlockPosition position;

    public ConnectingPointNewBlock(@NotNull BlockPosition position) {
        this.position = position;
    }
}
