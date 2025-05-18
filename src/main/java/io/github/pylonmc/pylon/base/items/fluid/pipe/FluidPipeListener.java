package io.github.pylonmc.pylon.base.items.fluid.pipe;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;


public class FluidPipeListener implements Listener {

    // prevent being able to place blocks on top of structure voids
    // TODO port this to core...
    // TODO this doesn't work with pylon blocks, ffs what the hell?
    @EventHandler
    private static void handle(@NotNull BlockPlaceEvent event) {
        PylonBlock<?> pylonBlock = BlockStorage.get(event.getBlock());
        if (pylonBlock instanceof FluidPipeConnector || pylonBlock instanceof FluidPipeMarker) {
            event.setCancelled(true);
        }
    }
}
