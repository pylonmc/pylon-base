package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.PlaceableBlock;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.persistence.blockstorage.BlockStorage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PylonBase extends JavaPlugin implements Listener {

    private static PylonBase INSTANCE;

    public static PylonBase getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        PylonItems.register();
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
    }

    // TODO move to pylon-core
    @EventHandler
    private void onPlaceBlock(BlockPlaceEvent e) {
        PylonItem<?> item = PylonItem.fromStack(e.getItemInHand());
        if (item == null) return;
        if (item instanceof PlaceableBlock placeableBlock) {
            BlockStorage.set(e.getBlock(), placeableBlock.getBlockSchema());
        } else {
            e.setBuild(false);
        }
    }
}
