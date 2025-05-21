package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.fluid.items.WaterPump;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeMarker;
import io.github.pylonmc.pylon.base.items.fluid.items.FluidTank;
import io.github.pylonmc.pylon.base.items.DimensionalBarrel;
import io.github.pylonmc.pylon.base.items.multiblocks.*;
import io.github.pylonmc.pylon.base.items.tools.watering.Sprinkler;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.SimplePylonBlock;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public final class PylonBlocks {

    private PylonBlocks() {
        throw new AssertionError("Utility class");
    }

    public static final Sprinkler.SprinklerBlock.Schema SPRINKLER = new Sprinkler.SprinklerBlock.Schema(
            pylonKey("sprinkler"),
            Material.FLOWER_POT,
            Sprinkler.SprinklerBlock.class
    );
    static {
        SPRINKLER.register();
    }

    public static final PylonBlockSchema PEDESTAL = new PylonBlockSchema(
            pylonKey("pedestal"),
            Material.STONE_BRICK_WALL,
            Pedestal.PedestalBlock.class
    );
    static {
        PEDESTAL.register();
    }

    public static final PylonBlockSchema MAGIC_PEDESTAL = new PylonBlockSchema(
            pylonKey("magic_pedestal"),
            Material.MOSSY_STONE_BRICK_WALL,
            Pedestal.PedestalBlock.class
    );
    static {
        MAGIC_PEDESTAL.register();
    }

    public static final MagicAltar.MagicAltarBlock.Schema MAGIC_ALTAR = new MagicAltar.MagicAltarBlock.Schema(
            pylonKey("magic_altar"),
            Material.SMOOTH_STONE_SLAB
    );
    static {
        MAGIC_ALTAR.register();
    }

    public static final PylonBlockSchema GRINDSTONE = new PylonBlockSchema(
            pylonKey("grindstone"),
            Material.SMOOTH_STONE_SLAB,
            Grindstone.GrindstoneBlock.class
    );
    static {
        GRINDSTONE.register();
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

    public static final Grindstone.GrindstoneBlock.Schema GRINDSTONE_HANDLE = new Grindstone.GrindstoneBlock.Schema(
            pylonKey("grindstone_handle"),
            Material.OAK_FENCE,
            GrindstoneHandle.GrindstoneHandleBlock.class
    );
    static {
        GRINDSTONE_HANDLE.register();
    }

    public static final EnrichedNetherrack.Schema ENRICHED_NETHERRACK = new EnrichedNetherrack.Schema(
            pylonKey("enriched_netherrack"),
            Material.NETHERRACK,
            EnrichedNetherrack.EnrichedNetherrackBlock.class
    );
    static {
        ENRICHED_NETHERRACK.register();
    }

    public static final PylonBlockSchema MIXING_POT = new PylonBlockSchema(
            pylonKey("mixing_pot"),
            Material.CAULDRON,
            MixingPot.MixingPotBlock.class
    );
    static {
        MIXING_POT.register();
    }

    public static final PylonBlockSchema WITHER_PROOF_OBSIDIAN = new PylonBlockSchema(
            pylonKey("wither_proof_obsidian"),
            Material.OBSIDIAN,
            SimplePylonBlock.class
    );
    static {
        WITHER_PROOF_OBSIDIAN.register();
    }

    public static final DimensionalBarrel.DimensionalBarrelBlock.Schema DIMENSIONAL_BARREL = new DimensionalBarrel.DimensionalBarrelBlock.Schema(
            pylonKey("dimensional_barrel"),
            Material.BARREL,
            DimensionalBarrel.DimensionalBarrelBlock.class
    );
    static {
        DIMENSIONAL_BARREL.register();
    }

    public static final PylonBlockSchema FLUID_PIPE_MARKER = new PylonBlockSchema(
            pylonKey("fluid_pipe_marker"),
            Material.STRUCTURE_VOID,
            FluidPipeMarker.class
    );
    static {
        FLUID_PIPE_MARKER.register();
    }

    public static final PylonBlockSchema FLUID_PIPE_CONNECTOR = new PylonBlockSchema(
            pylonKey("fluid_pipe_connector"),
            Material.STRUCTURE_VOID,
            FluidPipeConnector.class
    );
    static {
        FLUID_PIPE_CONNECTOR.register();
    }

    // TODO block settings
    public static final FluidTank.Schema FLUID_TANK_WOODEN = new FluidTank.Schema(
            pylonKey("fluid_tank_wooden"),
            Material.BROWN_STAINED_GLASS
    );
    static {
        FLUID_TANK_WOODEN.register();
    }

    public static final FluidTank.Schema FLUID_TANK_COPPER = new FluidTank.Schema(
            pylonKey("fluid_tank_copper"),
            Material.ORANGE_STAINED_GLASS
    );
    static {
        FLUID_TANK_COPPER.register();
    }

    public static final WaterPump.Schema WATER_PUMP = new WaterPump.Schema(
            pylonKey("water_pump"),
            Material.BLUE_TERRACOTTA
    );
    static {
        WATER_PUMP.register();
    }

    public static void initialize() {}
}
