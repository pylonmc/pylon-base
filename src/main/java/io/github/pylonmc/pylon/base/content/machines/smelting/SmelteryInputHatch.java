package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class SmelteryInputHatch extends SmelteryComponent implements PylonFluidBlock, PylonEntityHolderBlock {
    public final double flowRate = getSettings().getOrThrow("flow-rate", ConfigAdapter.DOUBLE);

    @SuppressWarnings("unused")
    public SmelteryInputHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH, 0.5F, true));
    }

    @SuppressWarnings("unused")
    public SmelteryInputHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid, double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller == null || !fluid.hasTag(FluidTemperature.class)) return 0.0;
        return Math.min(controller.getCapacity() - controller.getTotalFluid(), flowRate * deltaSeconds);
    }

    @Override
    public void onFluidAdded(@NotNull PylonFluid fluid, double amount) {
        SmelteryController controller = getController();
        if (controller == null) return;
        controller.addFluid(fluid, amount);
    }
}
