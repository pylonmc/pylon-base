package io.github.pylonmc.pylon.base.content.machines.hydraulics.base;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public abstract class SimplePurificationMachine extends PylonBlock implements PylonFluidBlock, PylonEntityHolderBlock {

    public static final NamespacedKey DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY = baseKey("dirty_hydraulic_fluid_amount");
    public static final NamespacedKey HYDRAULIC_FLUID_AMOUNT_KEY = baseKey("hydraulic_fluid_amount");

    @Getter protected double dirtyHydraulicFluidAmount;
    @Getter protected double hydraulicFluidAmount;

    public static final Component WORKING = Component.translatable("pylon.pylonbase.message.hydraulic_status.not_enough_hydraulic_fluid");
    public static final Component IDLE = Component.translatable("pylon.pylonbase.message.hydraulic_status.not_enough_hydraulic_fluid");
    public static final Component INCOMPLETE = Component.translatable("pylon.pylonbase.message.hydraulic_status.incomplete");

    @SuppressWarnings("unused")
    protected SimplePurificationMachine(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        dirtyHydraulicFluidAmount = 0.0;
        hydraulicFluidAmount = 0.0;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    protected SimplePurificationMachine(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        dirtyHydraulicFluidAmount = pdc.get(DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
        hydraulicFluidAmount = pdc.get(HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
    }

    public abstract double getDirtyHydraulicFluidBuffer();
    public abstract double getHydraulicFluidBuffer();
    public abstract Component getStatus();

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, dirtyHydraulicFluidAmount);
        pdc.set(HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, hydraulicFluidAmount);
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids(double deltaSeconds) {
        return Map.of(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidAmount);
    }

    @Override
    public void removeFluid(@NotNull PylonFluid fluid, double amount) {
        hydraulicFluidAmount -= amount;
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getRequestedFluids(double deltaSeconds) {
        return Map.of(BaseFluids.DIRTY_HYDRAULIC_FLUID, getDirtyHydraulicFluidBuffer() - dirtyHydraulicFluidAmount);
    }

    @Override
    public void addFluid(@NotNull PylonFluid fluid, double amount) {
        dirtyHydraulicFluidAmount += amount;
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        return Map.of(
                "input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH),
                "output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH)
        );
    }

    public void purify(double amount) {
        double toPurify = Math.min(amount, Math.min(dirtyHydraulicFluidAmount, getHydraulicFluidBuffer() - hydraulicFluidAmount));
        dirtyHydraulicFluidAmount -= toPurify;
        hydraulicFluidAmount += toPurify;
    }

    @Override
    public @Nullable WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(getName(PylonArgument.of("status", getStatus())));
    }
}
