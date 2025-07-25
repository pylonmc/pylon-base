package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class HydraulicCoreDrillHatch extends PylonBlock
        implements PylonFluidBufferBlock, PylonMultiblock, PylonEntityHolderBlock {

    @SuppressWarnings("unused")
    public HydraulicCoreDrillHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public HydraulicCoreDrillHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Set<ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock()));
    }

    @Override
    public boolean checkFormed() {
        PylonBlock aboveBlock = BlockStorage.get(getBlock().getRelative(BlockFace.UP));
        return aboveBlock != null && aboveBlock
                .getKey()
                .equals(BaseKeys.FLUID_TANK_CASING_COPPER);
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return getBlock().getLocation().equals(otherBlock.getLocation());
    }

    @Override
    public boolean setFluid(@NotNull PylonFluid fluid, double amount) {
        boolean result = PylonFluidBufferBlock.super.setFluid(fluid, amount);
        float scale = (float) (0.9 * fluidAmount(fluid) / fluidCapacity(fluid));
        if (scale < 1.0e-9) {
            scale = 0.0F;
        }
        getFluidDisplay().getEntity().setTransformationMatrix(new TransformBuilder()
                .translate(0.0, -0.45 + scale / 2, 0.0)
                .scale(0.9, scale, 0.9)
                .buildForItemDisplay()
        );
        return result;
    }

    public @NotNull SimpleItemDisplay getFluidDisplay() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "fluid");
    }
}
