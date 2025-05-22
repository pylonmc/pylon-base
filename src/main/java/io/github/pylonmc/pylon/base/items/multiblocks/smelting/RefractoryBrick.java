package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class RefractoryBrick extends SmelteryComponent<PylonBlockSchema> {

    public RefractoryBrick(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
        super(schema, block, pdc);
    }

    public RefractoryBrick(PylonBlockSchema schema, Block block, BlockCreateContext context) {
        super(schema, block, context);
    }
}
