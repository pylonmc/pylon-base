package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.DimensionalBarrel;
import io.github.pylonmc.pylon.base.items.ExplosiveTarget;
import io.github.pylonmc.pylon.base.items.fluid.items.*;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeMarker;
import io.github.pylonmc.pylon.base.items.multiblocks.*;
import io.github.pylonmc.pylon.base.items.tools.watering.Sprinkler;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public final class PylonBlocks {

    private PylonBlocks() {
        throw new AssertionError("Utility class");
    }

    public static final NamespacedKey WITHER_PROOF_OBSIDIAN_KEY = pylonKey("wither_proof_obsidian");

    public static void initialize() {
        PylonBlock.register(Sprinkler.KEY, Material.FLOWER_POT, Sprinkler.class);
        PylonBlock.register(Pedestal.MAGIC_PEDESTAL_KEY, Material.MOSSY_STONE_BRICK_WALL, Pedestal.class);
        PylonBlock.register(Pedestal.PEDESTAL_KEY, Material.STONE_BRICK_WALL, Pedestal.class);
        PylonBlock.register(MagicAltar.KEY, Material.SMOOTH_STONE_SLAB, MagicAltar.class);
        PylonBlock.register(Grindstone.KEY, Material.SMOOTH_STONE_SLAB, Grindstone.class);
        PylonBlock.register(GrindstoneHandle.KEY, Material.OAK_FENCE, GrindstoneHandle.class);
        PylonBlock.register(EnrichedNetherrack.KEY, Material.NETHERRACK, EnrichedNetherrack.class);
        PylonBlock.register(MixingPot.KEY, Material.CAULDRON, MixingPot.class);
        PylonBlock.register(DimensionalBarrel.KEY, Material.BARREL, DimensionalBarrel.class);
        PylonBlock.register(WITHER_PROOF_OBSIDIAN_KEY, Material.OBSIDIAN, PylonBlock.class);
        PylonBlock.register(FluidPipeMarker.KEY, Material.STRUCTURE_VOID, FluidPipeMarker.class);
        PylonBlock.register(FluidPipeConnector.KEY, Material.STRUCTURE_VOID, FluidPipeConnector.class);
        PylonBlock.register(PortableFluidTank.PORTABLE_FLUID_TANK_WOOD_KEY, Material.BROWN_STAINED_GLASS, PortableFluidTank.class);
        PylonBlock.register(PortableFluidTank.PORTABLE_FLUID_TANK_COPPER_KEY, Material.ORANGE_STAINED_GLASS, PortableFluidTank.class);
        PylonBlock.register(FluidValve.KEY, Material.STRUCTURE_VOID, FluidValve.class);
        PylonBlock.register(WaterPump.KEY, Material.BLUE_TERRACOTTA, WaterPump.class);
        PylonBlock.register(FluidFilter.KEY, Material.STRUCTURE_VOID, FluidFilter.class);
        PylonBlock.register(FluidMeter.KEY, Material.STRUCTURE_VOID, FluidMeter.class);
        PylonBlock.register(FluidPlacer.WATER_PLACER_KEY, Material.DISPENSER, FluidPlacer.class);
        PylonBlock.register(FluidPlacer.LAVA_PLACER_KEY, Material.DISPENSER, FluidPlacer.class);
        PylonBlock.register(FluidDrainer.WATER_DRAINER_KEY, Material.DISPENSER, FluidDrainer.class);
        PylonBlock.register(FluidDrainer.LAVA_DRAINER_KEY, Material.DISPENSER, FluidDrainer.class);
        PylonBlock.register(FluidVoider.FLUID_VOIDER_1_KEY, Material.STRUCTURE_VOID, FluidVoider.class);
        PylonBlock.register(FluidVoider.FLUID_VOIDER_2_KEY, Material.STRUCTURE_VOID, FluidVoider.class);
        PylonBlock.register(FluidVoider.FLUID_VOIDER_3_KEY, Material.STRUCTURE_VOID, FluidVoider.class);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                pylonKey("string_from_bamboo"),
                new RecipeChoice.MaterialChoice(Material.BAMBOO),
                4,
                new ItemStack(Material.STRING),
                3,
                Material.BAMBOO.createBlockData(data -> {
                    Ageable ageable = (Ageable) data;
                    ageable.setAge(ageable.getMaximumAge());
                })
        ));
        PylonBlock.register(ExplosiveTarget.EXPLOSIVE_TARGET_KEY, Material.TARGET, ExplosiveTarget.class);
        PylonBlock.register(ExplosiveTarget.FIERY_EXPLOSIVE_TARGET_KEY, Material.TARGET, ExplosiveTarget.class);
        PylonBlock.register(ExplosiveTarget.SUPER_EXPLOSIVE_TARGET_KEY, Material.TARGET, ExplosiveTarget.class);
        PylonBlock.register(ExplosiveTarget.SUPER_FIERY_EXPLOSIVE_TARGET_KEY, Material.TARGET, ExplosiveTarget.class);
    }
}
