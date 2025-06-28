package io.github.pylonmc.pylon.base.items.hydraulic.purification;

import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public abstract class SimplePurificationMachine extends PylonBlock implements PylonFluidIoBlock {

    public static final NamespacedKey DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY = pylonKey("dirty_hydraulic_fluid_amount");
    public static final NamespacedKey HYDRAULIC_FLUID_AMOUNT_KEY = pylonKey("hydraulic_fluid_amount");

    @Getter protected double dirtyHydraulicFluidAmount;
    @Getter protected double hydraulicFluidAmount;

    @SuppressWarnings("unused")
    protected SimplePurificationMachine(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        dirtyHydraulicFluidAmount = 0.0;
        hydraulicFluidAmount = 0.0;
    }

    @SuppressWarnings("unused")
    protected SimplePurificationMachine(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        dirtyHydraulicFluidAmount = pdc.get(DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
        hydraulicFluidAmount = pdc.get(HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
    }

    abstract double getDirtyHydraulicFluidBuffer();
    abstract double getHydraulicFluidBuffer();

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, dirtyHydraulicFluidAmount);
        pdc.set(HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, hydraulicFluidAmount);
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(PylonFluids.HYDRAULIC_FLUID, hydraulicFluidAmount);
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        hydraulicFluidAmount -= amount;
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(PylonFluids.DIRTY_HYDRAULIC_FLUID, getDirtyHydraulicFluidBuffer() - dirtyHydraulicFluidAmount);
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        dirtyHydraulicFluidAmount += amount;
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, BlockFace.NORTH),
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.OUTPUT, BlockFace.SOUTH)
        );
    }

    public void purify(double amount) {
        double toPurify = Math.min(amount, Math.min(dirtyHydraulicFluidAmount, getHydraulicFluidBuffer() - hydraulicFluidAmount));
        dirtyHydraulicFluidAmount -= toPurify;
        hydraulicFluidAmount += toPurify;
    }
}
