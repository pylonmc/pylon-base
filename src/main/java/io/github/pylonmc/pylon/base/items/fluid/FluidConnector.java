package io.github.pylonmc.pylon.base.items.fluid;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.event.PrePylonBlockBreakEvent;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;


public class FluidConnector extends PylonBlock<PylonBlockSchema> implements PylonEntityHolderBlock {

    private final Map<String, UUID> entities;

    @SuppressWarnings("unused")
    public FluidConnector(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
        super(schema, block);

        FluidConnectionPoint point = new FluidConnectionPoint(block, "connector", FluidConnectionPoint.Type.CONNECTOR);

        entities = Map.of(
                "connector", FluidConnectionInteraction.make(point).getUuid()
        );
    }


    @SuppressWarnings("unused")
    public FluidConnector(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
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

    public static class FluidConnectorBreakListener implements Listener {
        @EventHandler
        private static void handle(@NotNull PrePylonBlockBreakEvent event) {
            if (!(event.getContext() instanceof BlockBreakContext.PluginBreak)) {
                if (event.getPylonBlock() instanceof FluidConnector) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
