package io.github.pylonmc.pylon.base.content.building;

import io.github.pylonmc.pylon.base.BaseBlocks;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.event.PrePylonBlockBreakEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

// TODO I think this can be implemented as a normal Pylon block
public class WitherProofObsidianListener implements Listener {

    @EventHandler
    private void onExplode(PrePylonBlockBreakEvent event) {
        var key = event.getPylonBlock().getSchema().getKey();
        if (!key.equals(BaseBlocks.WITHER_PROOF_OBSIDIAN_KEY.getKey())) {
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
