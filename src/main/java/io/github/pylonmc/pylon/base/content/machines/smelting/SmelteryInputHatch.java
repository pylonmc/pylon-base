package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class SmelteryInputHatch extends SmelteryComponent implements PylonFluidBlock, PylonEntityHolderBlock {
    public static final double FLOW_RATE = Settings.get(BaseKeys.SMELTERY_INPUT_HATCH).getOrThrow("flow-rate", Double.class);

    @SuppressWarnings("unused")
    public SmelteryInputHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public SmelteryInputHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        return Map.of(
                "input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.SOUTH, 0.5F, true)
        );
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid, double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller == null || !fluid.hasTag(FluidTemperature.class)) return 0.0;
        return Math.min(controller.getCapacity() - controller.getTotalFluid(), FLOW_RATE * deltaSeconds);
    }

    @Override
    public void onFluidAdded(@NotNull PylonFluid fluid, double amount) {
        SmelteryController controller = getController();
        if (controller == null) return;
        controller.addFluid(fluid, amount);
    }
}
