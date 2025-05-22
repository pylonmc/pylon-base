package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.base.fluid.PylonFluidInteractionBlock;
import io.github.pylonmc.pylon.base.fluid.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.base.util.BlockFaces;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public class SmelteryInputHatch extends SmelteryComponent<PylonBlockSchema> implements PylonFluidInteractionBlock {

    public SmelteryInputHatch(PylonBlockSchema schema, Block block, BlockCreateContext context) {
        super(schema, block, context);
    }

    public SmelteryInputHatch(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
        super(schema, block, pdc);
    }

    @Override
    public List<SimpleFluidConnectionPoint> createFluidConnectionPoints() {
        return Arrays.stream(BlockFaces.ORTHOGONAL)
                .map(face -> new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, face))
                .toList();
    }

    @Override
    public Map<PylonFluid, Long> getRequestedFluids(String connectionPoint) {
        SmelteryController controller = getController();
        if (controller == null) return Map.of();

        long allowed = controller.getCapacity() - controller.getTotalFluid();
        Map<PylonFluid, Long> requested = new HashMap<>();
        for (PylonFluid fluid : PylonRegistry.FLUIDS) {
            if (fluid.hasTag(FluidTemperature.class)) {
                requested.put(fluid, allowed);
            }
        }
        return requested;
    }

    @Override
    public void addFluid(String connectionPoint, PylonFluid fluid, long amount) {
        SmelteryController controller = getController();
        if (controller == null) return;
        controller.addFluid(fluid, amount);
    }
}
