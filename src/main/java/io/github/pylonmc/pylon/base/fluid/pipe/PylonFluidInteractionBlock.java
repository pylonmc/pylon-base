package io.github.pylonmc.pylon.base.fluid.pipe;

import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@NullMarked
public interface PylonFluidInteractionBlock extends PylonEntityHolderBlock, PylonFluidBlock {

    List<SimpleFluidConnectionPoint> createFluidConnectionPoints(BlockCreateContext context);

    @Override
    @MustBeInvokedByOverriders
    default Map<String, UUID> createEntities(BlockCreateContext context) {
        List<SimpleFluidConnectionPoint> connectionPoints = createFluidConnectionPoints(context);
        Map<String, UUID> entities = new HashMap<>(connectionPoints.size());
        Player player = null;
        if (context instanceof BlockCreateContext.PlayerPlace ctx) {
            player = ctx.getPlayer();
        }
        for (SimpleFluidConnectionPoint simplePoint : connectionPoints) {
            FluidConnectionPoint point = new FluidConnectionPoint(
                    context.getBlock(),
                    simplePoint.name(),
                    simplePoint.type()
            );
            FluidConnectionInteraction interaction = FluidConnectionInteraction.make(
                    player,
                    point,
                    simplePoint.face(),
                    simplePoint.radius()
            );
            entities.put(simplePoint.name(), interaction.getUuid());
        }
        return entities;
    }
}
