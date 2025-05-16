package io.github.pylonmc.pylon.base.listeners;

import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.event.PrePylonBlockBreakEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class WitherProofObsidianListener implements Listener {

    @EventHandler
    private void onExplode(PrePylonBlockBreakEvent event) {
        var key = event.getPylonBlock().getSchema().getKey();
        if (!key.equals(PylonBlocks.WITHER_PROOF_OBSIDIAN.getKey())) {
            return;
        }

        if (event.getContext() instanceof BlockBreakContext.EntityExploded entityExploded) {
            var entityType = entityExploded.getEvent().getEntity().getType();
            if (entityType == EntityType.WITHER || entityType == EntityType.WITHER_SKULL) {
                event.setCancelled(true);
            }
        }
    }
}
