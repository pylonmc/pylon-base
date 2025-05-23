package io.github.pylonmc.pylon.base.fluid.pipe;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NullMarked
public class FluidPipeConnector extends PylonBlock<PylonBlockSchema> implements PylonEntityHolderBlock {

    @SuppressWarnings("unused")
    public FluidPipeConnector(PylonBlockSchema schema, Block block, BlockCreateContext context) {
        super(schema, block);
    }

    @SuppressWarnings("unused")
    public FluidPipeConnector(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
        super(schema, block);
    }

    @Override
    public Map<String, UUID> createEntities(BlockCreateContext context) {
        FluidConnectionPoint point = new FluidConnectionPoint(getBlock(), "connector", FluidConnectionPoint.Type.CONNECTOR);
        return Map.of(
                "connector", FluidConnectionInteraction.make(point).getUuid()
        );
    }

    public @Nullable FluidConnectionInteraction getFluidConnectionInteraction() {
        return getHeldEntity(FluidConnectionInteraction.class, "connector");
    }

    @Override
    public void onBreak(List<ItemStack> drops, BlockBreakContext context) {
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

    public FluidPipe.Schema getPipe() {
        FluidConnectionInteraction interaction = getFluidConnectionInteraction();
        Preconditions.checkState(interaction != null);
        Preconditions.checkState(!interaction.getConnectedPipeDisplays().isEmpty());
        UUID uuid = interaction.getConnectedPipeDisplays().iterator().next();
        FluidPipeDisplay pipeDisplay = EntityStorage.getAs(FluidPipeDisplay.class, uuid);
        Preconditions.checkState(pipeDisplay != null);
        return pipeDisplay.getPipe();
    }
}
