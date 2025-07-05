package io.github.pylonmc.pylon.base.items.hydraulic.machines;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public abstract class SimpleHydraulicMachine extends PylonBlock implements PylonFluidIoBlock {

    public static final NamespacedKey HYDRAULIC_FLUID_AMOUNT_KEY = pylonKey("hydraulic_fluid_amount");
    public static final NamespacedKey DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY = pylonKey("dirty_hydraulic_fluid_amount");

    public static final Component NOT_ENOUGH_HYDRAULIC_FLUID = Component.translatable("pylon.pylonbase.message.hydraulic_status.not_enough_hydraulic_fluid");
    public static final Component DIRTY_HYDRAULIC_FLUID_BUFFER_FULL = Component.translatable("pylon.pylonbase.message.hydraulic_status.dirty_hydraulic_fluid_buffer_full");
    public static final Component IDLE = Component.translatable("pylon.pylonbase.message.hydraulic_status.idle");
    public static final Component WORKING = Component.translatable("pylon.pylonbase.message.hydraulic_status.working");

    @Getter protected double hydraulicFluidAmount;
    @Getter protected double dirtyHydraulicFluidAmount;

    @SuppressWarnings("unused")
    protected SimpleHydraulicMachine(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        hydraulicFluidAmount = 0.0;
        dirtyHydraulicFluidAmount = 0.0;
    }

    @SuppressWarnings("unused")
    protected SimpleHydraulicMachine(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        hydraulicFluidAmount = pdc.get(HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
        dirtyHydraulicFluidAmount = pdc.get(DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
    }

    abstract double getHydraulicFluidBuffer();
    abstract double getDirtyHydraulicFluidBuffer();
    abstract Component getStatus();

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, hydraulicFluidAmount);
        pdc.set(DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, dirtyHydraulicFluidAmount);
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(BaseFluids.DIRTY_HYDRAULIC_FLUID, dirtyHydraulicFluidAmount);
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        dirtyHydraulicFluidAmount -= amount;
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(BaseFluids.HYDRAULIC_FLUID, getHydraulicFluidBuffer() - hydraulicFluidAmount);
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        hydraulicFluidAmount += amount;
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, BlockFace.NORTH),
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.OUTPUT, BlockFace.SOUTH)
        );
    }

    public double getRemainingDirtyCapacity() {
        return getDirtyHydraulicFluidBuffer() - dirtyHydraulicFluidAmount;
    }

    /**
     * Assumes that canStartCraft has already been called
     */
    public void startCraft(double inputFluidAmount, double outputFluidAmount) {
        hydraulicFluidAmount -= inputFluidAmount;
        dirtyHydraulicFluidAmount += outputFluidAmount;
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(
                getName(),
                Map.of("status", getStatus())
        );
    }
}
