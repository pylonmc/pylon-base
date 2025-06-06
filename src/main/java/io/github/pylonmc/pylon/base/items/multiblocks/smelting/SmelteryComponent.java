package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class SmelteryComponent extends PylonBlock {

    @Setter(AccessLevel.PACKAGE)
    private @Nullable SmelteryController controller = null;

    @SuppressWarnings("unused")
    public SmelteryComponent(Block block, BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings("unused")
    public SmelteryComponent(Block block, PersistentDataContainer pdc) {
        super(block);
    }

    public @Nullable SmelteryController getController() {
        if (controller == null || !controller.isRunning()) return null;
        return controller;
    }
}
