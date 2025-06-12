package io.github.pylonmc.pylon.base.fluid.pipe;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.NamespacedKey;
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

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public class FluidPipeConnector extends PylonBlock implements PylonEntityHolderBlock {

    public static final NamespacedKey KEY =  pylonKey("fluid_pipe_connector");

    @SuppressWarnings("unused")
    public FluidPipeConnector(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        FluidConnectionPoint point = new FluidConnectionPoint(block, "connector", FluidConnectionPoint.Type.CONNECTOR);
    }

    @SuppressWarnings("unused")
    public FluidPipeConnector(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public @NotNull Map<String, UUID> createEntities(@NotNull BlockCreateContext context) {
        FluidConnectionPoint point = new FluidConnectionPoint(getBlock(), "connector", FluidConnectionPoint.Type.CONNECTOR);
        return Map.of(
                "connector", FluidConnectionInteraction.make(point).getUuid()
        );
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
        return new WailaConfig(getName(), Map.of("pipe", getPipe().getStack().effectiveName()));
    }

    public PylonItem getPipe() {
        FluidConnectionInteraction interaction = getFluidConnectionInteraction();
        Preconditions.checkState(interaction != null);
        Preconditions.checkState(!interaction.getConnectedPipeDisplays().isEmpty());
        UUID uuid = interaction.getConnectedPipeDisplays().iterator().next();
        FluidPipeDisplay pipeDisplay = EntityStorage.getAs(FluidPipeDisplay.class, uuid);
        Preconditions.checkState(pipeDisplay != null);
        return pipeDisplay.getPipe();
    }
}
