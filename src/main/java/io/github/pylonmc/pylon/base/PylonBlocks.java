package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.*;
import io.github.pylonmc.pylon.base.items.watering.Sprinkler;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import org.bukkit.Material;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public final class PylonBlocks {

    private PylonBlocks() {
        throw new AssertionError("Utility class");
    }

    public static final Sprinkler.SprinklerBlock.Schema SPRINKLER = new Sprinkler.SprinklerBlock.Schema(
            pylonKey("sprinkler"),
            Material.FLOWER_POT
    );
    static {
        SPRINKLER.register();
    }

    public static final PylonBlockSchema PEDESTAL = PylonBlockSchema.simple(
            pylonKey("pedestal"),
            Material.STONE_BRICK_WALL,
            Pedestal.PedestalBlock::new,
            Pedestal.PedestalBlock::new
    );
    static {
        PEDESTAL.register();
    }

    public static final PylonBlockSchema MAGIC_PEDESTAL = PylonBlockSchema.simple(
            pylonKey("magic_pedestal"),
            Material.MOSSY_STONE_BRICK_WALL,
            Pedestal.PedestalBlock::new,
            Pedestal.PedestalBlock::new
    );
    static {
        MAGIC_PEDESTAL.register();
    }

    public static final PylonBlockSchema MAGIC_ALTAR = PylonBlockSchema.simple(
            pylonKey("magic_altar"),
            Material.SMOOTH_STONE_SLAB,
            MagicAltar.MagicAltarBlock::new,
            MagicAltar.MagicAltarBlock::new
    );
    static {
        MAGIC_ALTAR.register();
    }

    public static final PylonBlockSchema GRINDSTONE = PylonBlockSchema.simple(
            pylonKey("grindstone"),
            Material.SMOOTH_STONE_SLAB,
            Grindstone.GrindstoneBlock::new,
            Grindstone.GrindstoneBlock::new
    );
    static {
        GRINDSTONE.register();
    }

    public static final PylonBlockSchema GRINDSTONE_HANDLE = PylonBlockSchema.simple(
            pylonKey("grindstone_handle"),
            Material.OAK_FENCE,
            GrindstoneHandle.GrindstoneHandleBlock::new
    );
    static {
        GRINDSTONE_HANDLE.register();
    }

    public static final PylonBlockSchema ENRICHED_NETHERRACK = PylonBlockSchema.simple(
            pylonKey("enriched_netherrack"),
            Material.NETHERRACK,
            EnrichedNetherrack.EnrichedNetherrackBlock::new
    );
    static {
        ENRICHED_NETHERRACK.register();
    }

    public static final PylonBlockSchema MIXING_POT = PylonBlockSchema.simple(
            pylonKey("mixing_pot"),
            Material.CAULDRON,
            MixingPot.MixingPotBlock::new
    );
    static {
        MIXING_POT.register();
    }

    public static void initialize() {}
}
