package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.building.*;
import io.github.pylonmc.pylon.base.content.components.EnrichedNetherrack;
import io.github.pylonmc.pylon.base.content.machines.fluid.*;
import io.github.pylonmc.pylon.base.content.machines.hydraulics.*;
import io.github.pylonmc.pylon.base.content.machines.simple.*;
import io.github.pylonmc.pylon.base.content.machines.smelting.*;
import io.github.pylonmc.pylon.base.content.tools.Sprinkler;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeMarker;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import org.bukkit.Material;


public final class BaseBlocks {

    private BaseBlocks() {
        throw new AssertionError("Utility class");
    }


    public static void initialize() {
        PylonBlock.register(BaseKeys.SPRINKLER, Material.FLOWER_POT, Sprinkler.class);
        PylonBlock.register(BaseKeys.MAGIC_PEDESTAL, Material.MOSSY_STONE_BRICK_WALL, Pedestal.class);
        PylonBlock.register(BaseKeys.PEDESTAL, Material.STONE_BRICK_WALL, Pedestal.class);
        PylonBlock.register(BaseKeys.MAGIC_ALTAR, Material.SMOOTH_STONE_SLAB, MagicAltar.class);
        PylonBlock.register(BaseKeys.GRINDSTONE, Material.SMOOTH_STONE_SLAB, Grindstone.class);
        PylonBlock.register(BaseKeys.GRINDSTONE_HANDLE, Material.OAK_FENCE, GrindstoneHandle.class);
        PylonBlock.register(BaseKeys.ENRICHED_NETHERRACK, Material.NETHERRACK, EnrichedNetherrack.class);
        PylonBlock.register(BaseKeys.MIXING_POT, Material.CAULDRON, MixingPot.class);
        PylonBlock.register(BaseKeys.DIMENSIONAL_BARREL, Material.BARREL, DimensionalBarrel.class);
        PylonBlock.register(BaseKeys.WITHER_PROOF_OBSIDIAN, Material.OBSIDIAN, PylonBlock.class);
        PylonBlock.register(BaseKeys.FLUID_PIPE_MARKER, Material.STRUCTURE_VOID, FluidPipeMarker.class);
        PylonBlock.register(BaseKeys.FLUID_PIPE_CONNECTOR, Material.STRUCTURE_VOID, FluidPipeConnector.class);
        PylonBlock.register(BaseKeys.PORTABLE_FLUID_TANK_WOOD, Material.BROWN_STAINED_GLASS, PortableFluidTank.class);
        PylonBlock.register(BaseKeys.PORTABLE_FLUID_TANK_COPPER, Material.ORANGE_STAINED_GLASS, PortableFluidTank.class);
        PylonBlock.register(BaseKeys.FLUID_VALVE, Material.STRUCTURE_VOID, FluidValve.class);
        PylonBlock.register(BaseKeys.WATER_PUMP, Material.BLUE_TERRACOTTA, WaterPump.class);
        PylonBlock.register(BaseKeys.FLUID_FILTER, Material.STRUCTURE_VOID, FluidFilter.class);
        PylonBlock.register(BaseKeys.FLUID_METER, Material.STRUCTURE_VOID, FluidMeter.class);
        PylonBlock.register(BaseKeys.WATER_PLACER, Material.DISPENSER, FluidPlacer.class);
        PylonBlock.register(BaseKeys.LAVA_PLACER, Material.DISPENSER, FluidPlacer.class);
        PylonBlock.register(BaseKeys.WATER_DRAINER, Material.DISPENSER, FluidDrainer.class);
        PylonBlock.register(BaseKeys.LAVA_DRAINER, Material.DISPENSER, FluidDrainer.class);
        PylonBlock.register(BaseKeys.FLUID_VOIDER_1, Material.STRUCTURE_VOID, FluidVoider.class);
        PylonBlock.register(BaseKeys.FLUID_VOIDER_2, Material.STRUCTURE_VOID, FluidVoider.class);
        PylonBlock.register(BaseKeys.FLUID_VOIDER_3, Material.STRUCTURE_VOID, FluidVoider.class);
        PylonBlock.register(BaseKeys.REFRACTORY_BRICK, Material.DEEPSLATE_TILES, SmelteryComponent.class);
        PylonBlock.register(BaseKeys.SMELTERY_CONTROLLER, Material.BLAST_FURNACE, SmelteryController.class);
        PylonBlock.register(BaseKeys.SMELTERY_INPUT_HATCH, Material.LIGHT_BLUE_TERRACOTTA, SmelteryInputHatch.class);
        PylonBlock.register(BaseKeys.SMELTERY_OUTPUT_HATCH, Material.ORANGE_TERRACOTTA, SmelteryOutputHatch.class);
        PylonBlock.register(BaseKeys.SMELTERY_HOPPER, Material.HOPPER, SmelteryHopper.class);
        PylonBlock.register(BaseKeys.SMELTERY_CASTER, Material.BRICKS, SmelteryCaster.class);
        PylonBlock.register(BaseKeys.SMELTERY_BURNER, Material.FURNACE, SmelteryBurner.class);
        PylonBlock.register(BaseKeys.FLUID_STRAINER, Material.COPPER_GRATE, FluidStrainer.class);
        PylonBlock.register(BaseKeys.EXPLOSIVE_TARGET, Material.TARGET, ExplosiveTarget.class);
        PylonBlock.register(BaseKeys.EXPLOSIVE_TARGET_FIERY, Material.TARGET, ExplosiveTarget.class);
        PylonBlock.register(BaseKeys.EXPLOSIVE_TARGET_SUPER, Material.TARGET, ExplosiveTarget.class);
        PylonBlock.register(BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY, Material.TARGET, ExplosiveTarget.class);
        PylonBlock.register(BaseKeys.IMMOBILIZER, Material.PISTON, Immobilizer.class);
        PylonBlock.register(BaseKeys.ELEVATOR_1, Material.SMOOTH_QUARTZ_SLAB, Elevator.class);
        PylonBlock.register(BaseKeys.ELEVATOR_2, Material.SMOOTH_QUARTZ_SLAB, Elevator.class);
        PylonBlock.register(BaseKeys.ELEVATOR_3, Material.SMOOTH_QUARTZ_SLAB, Elevator.class);
        PylonBlock.register(BaseKeys.PRESS, Material.COMPOSTER, Press.class);
        PylonBlock.register(BaseKeys.HYDRAULIC_GRINDSTONE_TURNER, Material.SMOOTH_STONE, HydraulicGrindstoneTurner.class);
        PylonBlock.register(BaseKeys.HYDRAULIC_MIXING_ATTACHMENT, Material.GRAY_CONCRETE, HydraulicMixingAttachment.class);
        PylonBlock.register(BaseKeys.HYDRAULIC_PRESS_PISTON, Material.BROWN_CONCRETE, HydraulicPressPiston.class);
        PylonBlock.register(BaseKeys.HYDRAULIC_HAMMER_HEAD, Material.STONE_BRICKS, HydraulicHammerHead.class);
        PylonBlock.register(BaseKeys.SOLAR_LENS, Material.GLASS_PANE, PylonBlock.class);
        PylonBlock.register(BaseKeys.PURIFICATION_TOWER_GLASS, Material.LIGHT_GRAY_STAINED_GLASS, PylonBlock.class);
        PylonBlock.register(BaseKeys.PURIFICATION_TOWER_CAP, Material.QUARTZ_SLAB, PylonBlock.class);
        PylonBlock.register(BaseKeys.SOLAR_PURIFICATION_TOWER_1, Material.BLACK_CONCRETE, SolarPurificationTower.class);
        PylonBlock.register(BaseKeys.SOLAR_PURIFICATION_TOWER_2, Material.BLACK_CONCRETE, SolarPurificationTower.class);
        PylonBlock.register(BaseKeys.SOLAR_PURIFICATION_TOWER_3, Material.BLACK_CONCRETE, SolarPurificationTower.class);
        PylonBlock.register(BaseKeys.SOLAR_PURIFICATION_TOWER_4, Material.BLACK_CONCRETE, SolarPurificationTower.class);
        PylonBlock.register(BaseKeys.SOLAR_PURIFICATION_TOWER_5, Material.BLACK_CONCRETE, SolarPurificationTower.class);
        PylonBlock.register(BaseKeys.COAL_FIRED_PURIFICATION_TOWER, Material.BLACK_CONCRETE, CoalFiredPurificationTower.class);
        PylonBlock.register(BaseKeys.FOOD_PROCESSOR_SIMPLE, Material.DISPENSER, FoodProcessor.class);
        PylonBlock.register(BaseKeys.FOOD_PROCESSOR_HANDLE, Material.LEVER, FoodProcessor.FoodProcessorHandle.class);
    }
}
