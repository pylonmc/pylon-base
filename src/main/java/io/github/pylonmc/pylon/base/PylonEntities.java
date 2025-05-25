package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeDisplay;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionDisplay;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.fluid.FluidTank;
import io.github.pylonmc.pylon.base.items.multiblocks.Grindstone;
import io.github.pylonmc.pylon.base.items.multiblocks.Pedestal;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;


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
        PylonEntity.register(FluidPipeDisplay.KEY, ItemDisplay.class, FluidPipeDisplay.class);
        PylonEntity.register(FluidTank.FluidTankEntity.KEY, ItemDisplay.class, FluidTank.FluidTankEntity.class);
    }
}
