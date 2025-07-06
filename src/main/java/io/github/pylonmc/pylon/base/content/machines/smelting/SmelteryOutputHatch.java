package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.fluid.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import kotlin.Pair;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public final class SmelteryOutputHatch extends SmelteryComponent implements PylonFluidIoBlock {

    public static final double FLOW_RATE = Settings.get(BaseKeys.SMELTERY_OUTPUT_HATCH).getOrThrow("flow-rate", Double.class);

    @SuppressWarnings("unused")
    public SmelteryOutputHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public SmelteryOutputHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public boolean allowVerticalConnectionPoints() {
        return true;
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.OUTPUT, BlockFace.NORTH));
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller == null) return Map.of();

        Pair<PylonFluid, Double> supplied = controller.getBottomFluid();
        return supplied == null
                ? Map.of()
                : Map.of(supplied.getFirst(), Math.min(supplied.getSecond(), FLOW_RATE * deltaSeconds));
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        SmelteryController controller = getController();
        if (controller == null) return;
        controller.removeFluid(fluid, amount);
    }
}
