package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.EnrichedNetherrack;
import io.github.pylonmc.pylon.base.items.Grindstone;
import io.github.pylonmc.pylon.base.items.GrindstoneHandle;
import io.github.pylonmc.pylon.base.items.MixingPot;
import io.github.pylonmc.pylon.base.items.Pedestal;
import io.github.pylonmc.pylon.base.items.MagicAltar;
import io.github.pylonmc.pylon.base.items.fluid.FluidTank;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.base.items.watering.Sprinkler;
import org.bukkit.Material;

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
    }

    public static final PylonBlockSchema GRINDSTONE_HANDLE = new PylonBlockSchema(
            pylonKey("grindstone_handle"),
            Material.OAK_FENCE,
            GrindstoneHandle.GrindstoneHandleBlock.class
    );
    static {
        GRINDSTONE_HANDLE.register();
    }

    public static final PylonBlockSchema ENRICHED_NETHERRACK = new PylonBlockSchema(
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

    public static final FluidTank.Schema FLUID_TANK_1 = new FluidTank.Schema(
            pylonKey("fluid_tank_1"),
            Material.GLASS,
            4000,
            1000
    );
    static {
        FLUID_TANK_1.register();
    }

    public static void initialize() {}
}
