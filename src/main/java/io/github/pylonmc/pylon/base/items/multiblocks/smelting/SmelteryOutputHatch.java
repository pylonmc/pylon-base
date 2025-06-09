package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidInteractionBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import kotlin.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

@NullMarked
public final class SmelteryOutputHatch extends SmelteryComponent implements PylonFluidInteractionBlock  {

    public static final NamespacedKey KEY = pylonKey("smeltery_output_hatch");

    // TODO blocksetting
    private static final double FLOW_RATE = 500;
    
    public SmelteryOutputHatch(Block block, BlockCreateContext context) {
        super(block, context);
    }

    public SmelteryOutputHatch(Block block, PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public List<SimpleFluidConnectionPoint> createFluidConnectionPoints(BlockCreateContext context) {
        BlockFace face = context instanceof BlockCreateContext.PlayerPlace ctx ? ctx.getClickedFace() : BlockFace.NORTH;
        return List.of(new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.OUTPUT, face));
    }

    @Override
    public Map<PylonFluid, Double> getSuppliedFluids(String connectionPoint, double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller == null) return Map.of();

        Pair<PylonFluid, Double> supplied = controller.getBottomFluid();
        return supplied == null
                ? Map.of()
                : Map.of(supplied.getFirst(), Math.min(supplied.getSecond(), FLOW_RATE * deltaSeconds));
    }

    @Override
    public void removeFluid(String connectionPoint, PylonFluid fluid, double amount) {
        SmelteryController controller = getController();
        if (controller == null) return;
        controller.removeFluid(fluid, amount);
    }
}
