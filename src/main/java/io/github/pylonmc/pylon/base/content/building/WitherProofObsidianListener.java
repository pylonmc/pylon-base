package io.github.pylonmc.pylon.base.content.building;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.event.PrePylonBlockBreakEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// TODO I think this can be implemented as a normal Pylon block
public class WitherProofObsidianListener implements Listener {

    @EventHandler
    private static void onExplode(PrePylonBlockBreakEvent event) {
        var key = event.getPylonBlock().getSchema().getKey();
        if (!key.equals(BaseKeys.WITHER_PROOF_OBSIDIAN)) {
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
