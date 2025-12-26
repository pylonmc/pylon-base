package io.github.pylonmc.pylon.base.content.components;

import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class FluidInputHatch extends FluidHatch {
    public FluidInputHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        createFluidPoint(FluidPointType.INPUT, BlockFace.SOUTH, context, true);
    }

    public FluidInputHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }
}
