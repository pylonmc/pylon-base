package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.IdentityHashMap;

public interface FluidTankEntityDisplayer extends PylonEntityHolderBlock, PylonFluidTank {
    class DataHolder {
        private static final IdentityHashMap<PylonFluidTank, Integer> lastDisplayUpdateMap = new IdentityHashMap<>();
    }

    default void createFluidDisplayer() {
        addEntity("fluid", new ItemDisplayBuilder()
            .build(getBlock().getLocation().toCenterLocation().add(0, 0, 0))
        );
    }

    default @NotNull ItemDisplay getFluidDisplay() {
        return getHeldEntityOrThrow(ItemDisplay.class, "fluid");
    }

    @Override
    default void setFluidType(@Nullable PylonFluid fluid) {
        PylonFluidTank.super.setFluidType(fluid);
        getFluidDisplay().setItemStack(fluid == null ? null : fluid.getItem());
    }

    @Override
    default boolean setFluid(double amount) {
        double oldAmount = getFluidAmount();
        boolean result = PylonFluidTank.super.setFluid(amount);
        amount = getFluidAmount();
        int lastDisplayUpdate = DataHolder.lastDisplayUpdateMap.getOrDefault(this, -1);

        if (lastDisplayUpdate == -1 || (result && oldAmount != amount)) {
            float scale = (float) (maxScale() * amount / getFluidCapacity());
            ItemDisplay fluidDisplay = getFluidDisplay();
            fluidDisplay.setInterpolationDelay(Math.min(-3 + (fluidDisplay.getTicksLived() - lastDisplayUpdate), 0));
            fluidDisplay.setInterpolationDuration(4);

            Vector3d tOff = translationOffset();
            Vector3d sOff = scaleOffset();

            fluidDisplay.setTransformationMatrix(new TransformBuilder()
                .translate(tOff.x, tOff.y + scale / 2, tOff.z)
                .scale(sOff.x, sOff.y + scale, sOff.z)
                .buildForItemDisplay()
            );

            DataHolder.lastDisplayUpdateMap.put(this, fluidDisplay.getTicksLived());
        }

        return result;
    }

    default Vector3d translationOffset() {
        return new Vector3d(0, -0.45, 0);
    }

    default Vector3d scaleOffset() {
        return new Vector3d(0.9, 0, 0.9);
    }

    default double maxScale() {
        return 0.9;
    }
}
