package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidInteractionBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

@NullMarked
public final class SmelteryInputHatch extends SmelteryComponent implements PylonFluidInteractionBlock {

    public static final NamespacedKey KEY = pylonKey("smeltery_input_hatch");

    // TODO block setting
    private static final double FLOW_RATE = 500;

    public SmelteryInputHatch(Block block, BlockCreateContext context) {
        super(block, context);
    }

    public SmelteryInputHatch(Block block, PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public List<SimpleFluidConnectionPoint> createFluidConnectionPoints(BlockCreateContext context) {
        BlockFace face = context instanceof BlockCreateContext.PlayerPlace ctx ? ctx.getClickedFace() : BlockFace.NORTH;
        return List.of(new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, face));
    }

    @Override
    public Map<PylonFluid, Double> getRequestedFluids(String connectionPoint, double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller == null) return Map.of();

        double allowed = Math.min(controller.getCapacity() - controller.getTotalFluid(), FLOW_RATE * deltaSeconds);
        Map<PylonFluid, Double> requested = new HashMap<>();
        for (PylonFluid fluid : PylonRegistry.FLUIDS) {
            if (fluid.hasTag(FluidTemperature.class)) {
                requested.put(fluid, allowed);
            }
        }
        return requested;
    }

    @Override
    public void addFluid(String connectionPoint, PylonFluid fluid, double amount) {
        SmelteryController controller = getController();
        if (controller == null) return;
        controller.addFluid(fluid, amount);
    }
}
