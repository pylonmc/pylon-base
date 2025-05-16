package io.github.pylonmc.pylon.base.items.fluid.pipe;

import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public class FluidPipeMarker extends PylonBlock<PylonBlockSchema> {

    private static final NamespacedKey FROM_KEY = KeyUtils.pylonKey("from");
    private static final NamespacedKey TO_KEY = KeyUtils.pylonKey("to");

    public UUID from;
    public UUID to;

    @SuppressWarnings("unused")
    public FluidPipeMarker(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
        super(schema, block);
    }

    @SuppressWarnings("unused")
    public FluidPipeMarker(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(schema, block);
        from = pdc.get(FROM_KEY, PylonSerializers.UUID);
        to = pdc.get(TO_KEY, PylonSerializers.UUID);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(FROM_KEY, PylonSerializers.UUID, from);
        pdc.set(TO_KEY, PylonSerializers.UUID, to);
    }
}
