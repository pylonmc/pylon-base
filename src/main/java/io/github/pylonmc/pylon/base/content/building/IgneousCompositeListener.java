package io.github.pylonmc.pylon.base.content.building;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.rebar.block.BlockStorage;
import io.github.pylonmc.rebar.block.PylonBlock;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.jetbrains.annotations.NotNull;


public class IgneousCompositeListener implements Listener {

    @EventHandler
    private static void onExplode(@NotNull EntityChangeBlockEvent event) {
        if (event.getEntityType() != EntityType.WITHER) return;
        PylonBlock block = BlockStorage.get(event.getBlock());
        if (block != null && block.getKey().equals(BaseKeys.IGNEOUS_COMPOSITE)) {
            event.setCancelled(true);
        }
    }
}
