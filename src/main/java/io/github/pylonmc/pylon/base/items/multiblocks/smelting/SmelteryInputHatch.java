package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public final class SmelteryInputHatch extends SmelteryComponent implements PylonFluidIoBlock {

    public static final NamespacedKey KEY = pylonKey("smeltery_input_hatch");

    public static final double FLOW_RATE = Settings.get(KEY).getOrThrow("flow-rate", Double.class);

    @SuppressWarnings("unused")
    public SmelteryInputHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public SmelteryInputHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public boolean allowVerticalConnectionPoints() {
        return true;
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, BlockFace.NORTH));
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
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
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        SmelteryController controller = getController();
        if (controller == null) return;
        controller.addFluid(fluid, amount);
    }
}
