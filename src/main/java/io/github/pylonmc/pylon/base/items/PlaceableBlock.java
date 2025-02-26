package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import org.jetbrains.annotations.NotNull;

// TODO move to pylon-core
public interface PlaceableBlock {

    @NotNull
    PylonBlockSchema getBlockSchema();
}
