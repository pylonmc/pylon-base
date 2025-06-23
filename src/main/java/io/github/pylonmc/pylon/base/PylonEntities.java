package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeDisplay;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionDisplay;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.hydraulic.machines.HydraulicHammerHead;
import io.github.pylonmc.pylon.base.items.hydraulic.machines.HydraulicMixingAttachment;
import io.github.pylonmc.pylon.base.items.Press;
import io.github.pylonmc.pylon.base.items.fluid.*;
import io.github.pylonmc.pylon.base.items.hydraulic.machines.HydraulicPressPiston;
import io.github.pylonmc.pylon.base.items.multiblocks.Grindstone;
import io.github.pylonmc.pylon.base.items.multiblocks.Pedestal;
import io.github.pylonmc.pylon.base.items.multiblocks.smelting.SmelteryController;
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
        PylonEntity.register(PortableFluidTank.FluidTankEntity.KEY, ItemDisplay.class, PortableFluidTank.FluidTankEntity.class);
        PylonEntity.register(FluidValve.FluidValveDisplay.KEY, ItemDisplay.class, FluidValve.FluidValveDisplay.class);
        PylonEntity.register(FluidMeter.FlowRateDisplay.KEY, TextDisplay.class, FluidMeter.FlowRateDisplay.class);
        PylonEntity.register(FluidVoider.MainDisplay.KEY, ItemDisplay.class, FluidVoider.MainDisplay.class);
        PylonEntity.register(SmelteryController.FluidPixelEntity.KEY, TextDisplay.class, SmelteryController.FluidPixelEntity.class);
        PylonEntity.register(Press.PressCoverEntity.KEY, ItemDisplay.class, Press.PressCoverEntity.class);
        PylonEntity.register(HydraulicMixingAttachment.ShaftEntity.KEY, ItemDisplay.class, HydraulicMixingAttachment.ShaftEntity.class);
        PylonEntity.register(HydraulicPressPiston.PistonShaftEntity.KEY, ItemDisplay.class, HydraulicPressPiston.PistonShaftEntity.class);
        PylonEntity.register(HydraulicHammerHead.HammerHeadEntity.KEY, ItemDisplay.class, HydraulicHammerHead.HammerHeadEntity.class);
        PylonEntity.register(HydraulicHammerHead.HammerTipEntity.KEY, ItemDisplay.class, HydraulicHammerHead.HammerTipEntity.class);
    }
}
