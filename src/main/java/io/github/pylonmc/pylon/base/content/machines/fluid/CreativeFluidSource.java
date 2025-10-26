package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.content.machines.fluid.gui.FluidSelector;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidPointDirectional;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class CreativeFluidSource extends PylonBlock
        implements PylonFluidBlock, PylonEntityHolderBlock, PylonGuiBlock {

    public static final NamespacedKey FLUID_KEY = baseKey("fluid");

    @Getter @Setter private PylonFluid fluid;

    @SuppressWarnings("unused")
    public CreativeFluidSource(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.NORTH, 0.5F, true));
        fluid = null;
    }

    @SuppressWarnings("unused")
    public CreativeFluidSource(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        fluid = pdc.get(FLUID_KEY, PylonSerializers.PYLON_FLUID);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, FLUID_KEY, PylonSerializers.PYLON_FLUID, fluid);
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(double deltaSeconds) {
        return fluid == null ? Map.of() : Map.of(fluid, 1.0e9);
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {}

    @Override
    public @NotNull Gui createGui() {
        return (FluidSelector.make(() -> fluid, this::setFluid));
    }
}
