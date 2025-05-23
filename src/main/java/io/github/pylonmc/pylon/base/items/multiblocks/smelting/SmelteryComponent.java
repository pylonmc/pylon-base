package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class SmelteryComponent<S extends PylonBlockSchema> extends PylonBlock<S> {

    @Setter(AccessLevel.PACKAGE)
    private @Nullable SmelteryController controller = null;

    @SuppressWarnings("unused")
    public SmelteryComponent(S schema, Block block, BlockCreateContext context) {
        super(schema, block);
    }

    @SuppressWarnings("unused")
    public SmelteryComponent(S schema, Block block, PersistentDataContainer pdc) {
        super(schema, block);
    }

    public @Nullable SmelteryController getController() {
        if (controller == null || !controller.isRunning()) return null;
        return controller;
    }
}
