package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import io.github.pylonmc.pylon.core.persistence.blockstorage.BlockStorage;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

// TODO move to pylon-core
public abstract class PylonBlockItem<S extends PylonItemSchema> extends PylonItem<S> implements BlockInteractor {

    private final PylonBlockSchema blockSchema;

    public PylonBlockItem(PylonBlockSchema blockSchema, S itemSchema, ItemStack stack) {
        super(itemSchema, stack);
        this.blockSchema = blockSchema;
    }

    @Override
    public void onUsedToRightClickBlock(@NotNull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        assert block != null;
        if (!block.isReplaceable()) {
            block = block.getRelative(event.getBlockFace());
        }
        if (!(block.getType().isAir() || block.isReplaceable())) return;
        event.getItem().subtract();
        block.breakNaturally();
        BlockStorage.set(block, blockSchema);
    }
}
