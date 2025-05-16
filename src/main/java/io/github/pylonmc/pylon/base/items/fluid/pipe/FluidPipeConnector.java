package io.github.pylonmc.pylon.base.items.fluid.pipe;

import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;


public class FluidPipeConnector extends PylonBlock<PylonBlockSchema> implements PylonEntityHolderBlock {

    private final Map<String, UUID> entities;

    @SuppressWarnings("unused")
    public FluidPipeConnector(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
        super(schema, block);

        FluidConnectionPoint point = new FluidConnectionPoint(block, "connector", FluidConnectionPoint.Type.CONNECTOR);

        entities = Map.of(
                "connector", FluidConnectionInteraction.make(point).getUuid()
        );
    }


    @SuppressWarnings("unused")
    public FluidPipeConnector(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(schema, block);

        entities = loadHeldEntities(pdc);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        saveHeldEntities(pdc);
    }

    @Override
    public @NotNull Map<String, UUID> getHeldEntities() {
        return entities;
    }

    public @Nullable FluidConnectionInteraction getFluidConnectionInteraction() {
        return getHeldEntity(FluidConnectionInteraction.class, "connector");
    }
}
