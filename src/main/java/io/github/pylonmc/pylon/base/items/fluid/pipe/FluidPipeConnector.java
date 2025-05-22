package io.github.pylonmc.pylon.base.items.fluid.pipe;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
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

    @Override
    public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
        FluidConnectionInteraction interaction = getFluidConnectionInteraction();
        Preconditions.checkState(interaction != null);
        // Clone to prevent ConcurrentModificationException if pipeDisplay.delete modified connectedPipeDisplays
        for (UUID pipeDisplayId : new HashSet<>(interaction.getConnectedPipeDisplays())) {
            FluidPipeDisplay pipeDisplay = EntityStorage.getAs(FluidPipeDisplay.class, pipeDisplayId);
            // can be null if called from two different location (eg two different connection points removing the display)
            if (pipeDisplay != null) {
                Player player = null;
                if (context instanceof BlockBreakContext.PlayerBreak breakContext) {
                    player = breakContext.getEvent().getPlayer();
                }
                pipeDisplay.delete(true, player);
            }
        }

        PylonEntityHolderBlock.super.onBreak(drops, context);
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(getName(), Map.of("pipe", getPipe().getItemStack().displayName()));
    }

    public @NotNull FluidPipe.Schema getPipe() {
        FluidConnectionInteraction interaction = getFluidConnectionInteraction();
        Preconditions.checkState(interaction != null);
        Preconditions.checkState(!interaction.getConnectedPipeDisplays().isEmpty());
        UUID uuid = interaction.getConnectedPipeDisplays().iterator().next();
        FluidPipeDisplay pipeDisplay = EntityStorage.getAs(FluidPipeDisplay.class, uuid);
        Preconditions.checkState(pipeDisplay != null);
        return pipeDisplay.getPipe();
    }
}
