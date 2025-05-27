package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeMarker;
import io.github.pylonmc.pylon.base.items.DimensionalBarrel;
import io.github.pylonmc.pylon.base.items.fluid.FluidTank;
import io.github.pylonmc.pylon.base.items.fluid.WaterPump;
import io.github.pylonmc.pylon.base.items.multiblocks.*;
import io.github.pylonmc.pylon.base.items.multiblocks.smelting.*;
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
    public static final NamespacedKey REFRACTORY_BRICK_KEY = pylonKey("refractory_brick");

    public static void initialize() {
        PylonBlock.register(Sprinkler.KEY, Material.FLOWER_POT, Sprinkler.class);
        PylonBlock.register(Pedestal.KEY_MAGIC, Material.MOSSY_STONE_BRICK_WALL, Pedestal.class);
        PylonBlock.register(Pedestal.KEY_NORMAL, Material.STONE_BRICK_WALL, Pedestal.class);
        PylonBlock.register(MagicAltar.KEY, Material.SMOOTH_STONE_SLAB, MagicAltar.class);
        PylonBlock.register(Grindstone.KEY, Material.SMOOTH_STONE_SLAB, Grindstone.class);
        PylonBlock.register(GrindstoneHandle.KEY, Material.OAK_FENCE, GrindstoneHandle.class);
        PylonBlock.register(EnrichedNetherrack.KEY, Material.NETHERRACK, EnrichedNetherrack.class);
        PylonBlock.register(MixingPot.KEY, Material.CAULDRON, MixingPot.class);
        PylonBlock.register(DimensionalBarrel.KEY, Material.BARREL, DimensionalBarrel.class);
        PylonBlock.register(WITHER_PROOF_OBSIDIAN_KEY, Material.OBSIDIAN, PylonBlock.class);
        PylonBlock.register(FluidPipeMarker.KEY, Material.STRUCTURE_VOID, FluidPipeMarker.class);
        PylonBlock.register(FluidPipeConnector.KEY, Material.STRUCTURE_VOID, FluidPipeConnector.class);
        PylonBlock.register(FluidTank.FLUID_TANK_WOOD_KEY, Material.BROWN_STAINED_GLASS, FluidTank.class);
        PylonBlock.register(FluidTank.FLUID_TANK_COPPER_KEY, Material.ORANGE_STAINED_GLASS, FluidTank.class);
        PylonBlock.register(WaterPump.KEY, Material.BLUE_TERRACOTTA, WaterPump.class);
        PylonBlock.register(REFRACTORY_BRICK_KEY, Material.DEEPSLATE_TILES, SmelteryComponent.class);
        PylonBlock.register(SmelteryController.KEY, Material.BLAST_FURNACE, SmelteryController.class);
        PylonBlock.register(SmelteryInputHatch.KEY, Material.LIGHT_BLUE_TERRACOTTA, SmelteryInputHatch.class);
        PylonBlock.register(SmelteryOutputHatch.KEY, Material.ORANGE_TERRACOTTA, SmelteryOutputHatch.class);
        PylonBlock.register(SmelteryHopper.KEY, Material.HOPPER, SmelteryHopper.class);
        PylonBlock.register(SmelteryCaster.KEY, Material.BRICKS, SmelteryCaster.class);
        PylonBlock.register(SmelteryBurner.KEY, Material.FURNACE, SmelteryBurner.class);

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
    }
}
