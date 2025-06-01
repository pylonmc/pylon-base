package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionDisplay;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.fluid.items.FluidFilter;
import io.github.pylonmc.pylon.base.items.fluid.items.FluidMeter;
import io.github.pylonmc.pylon.base.items.fluid.items.FluidTank;
import io.github.pylonmc.pylon.base.items.fluid.items.FluidValve;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeDisplay;
import io.github.pylonmc.pylon.base.items.multiblocks.Grindstone;
import io.github.pylonmc.pylon.base.items.multiblocks.Pedestal;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;


public final class PylonEntities {

    private PylonEntities() {
        throw new AssertionError("Utility class");
    }

    public static void initialize() {
        PylonEntity.register(Grindstone.GrindstoneItemEntity.KEY, ItemDisplay.class, Grindstone.GrindstoneItemEntity.class);
        PylonEntity.register(Grindstone.GrindstoneBlockEntity.KEY, ItemDisplay.class, Grindstone.GrindstoneBlockEntity.class);
        PylonEntity.register(FluidConnectionDisplay.KEY, ItemDisplay.class, FluidConnectionDisplay.class);
        PylonEntity.register(FluidConnectionInteraction.KEY, Interaction.class, FluidConnectionInteraction.class);
        PylonEntity.register(Pedestal.PedestalItemEntity.KEY, ItemDisplay.class, Pedestal.PedestalItemEntity.class);
        PylonEntity.register(FluidFilter.FluidDisplay.KEY, ItemDisplay.class, FluidFilter.FluidDisplay.class);
        PylonEntity.register(FluidFilter.MainDisplay.KEY, ItemDisplay.class, FluidFilter.MainDisplay.class);
        PylonEntity.register(FluidPipeDisplay.KEY, ItemDisplay.class, FluidPipeDisplay.class);
        PylonEntity.register(FluidTank.FluidTankEntity.KEY, ItemDisplay.class, FluidTank.FluidTankEntity.class);
        PylonEntity.register(FluidValve.FluidValveDisplay.KEY, ItemDisplay.class, FluidValve.FluidValveDisplay.class);
        PylonEntity.register(FluidMeter.FlowRateDisplay.KEY, TextDisplay.class, FluidMeter.FlowRateDisplay.class);
    }
}
