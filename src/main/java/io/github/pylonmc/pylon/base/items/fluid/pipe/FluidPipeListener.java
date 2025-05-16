package io.github.pylonmc.pylon.base.items.fluid.pipe;

import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.event.PrePylonBlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;


public class FluidPipeListener implements Listener {
    @EventHandler
    private static void handle(@NotNull PrePylonBlockBreakEvent event) {
        if (!(event.getContext() instanceof BlockBreakContext.PluginBreak)) {
            if (event.getPylonBlock() instanceof FluidPipeConnector || event.getPylonBlock() instanceof FluidPipeMarker) {
                event.setCancelled(true);
            }
        }
    }
}
