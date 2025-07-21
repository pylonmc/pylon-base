package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import kotlin.Pair;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class SmelteryOutputHatch extends SmelteryComponent
        implements PylonEntityHolderBlock, PylonFluidBlock {

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
    public @NotNull Map<@NotNull String, @NotNull PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        return Map.of(
                "output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.NORTH, 0.5F, true)
        );
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller == null) return Map.of();

        Pair<PylonFluid, Double> supplied = controller.getBottomFluid();
        return supplied == null
                ? Map.of()
                : Map.of(supplied.getFirst(), Math.min(supplied.getSecond(), FLOW_RATE * deltaSeconds));
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {
        SmelteryController controller = getController();
        if (controller == null) return;
        controller.removeFluid(fluid, amount);
    }
}
