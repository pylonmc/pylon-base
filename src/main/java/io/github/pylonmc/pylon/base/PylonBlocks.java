package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.Pedestal;
import io.github.pylonmc.pylon.base.items.MagicAltar;
import io.github.pylonmc.pylon.base.items.Sprinkler;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
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

    public static final MagicAltar.MagicAltarBlock.Schema MAGIC_ALTAR = new MagicAltar.MagicAltarBlock.Schema(
            pylonKey("magic_altar"),
            Material.SMOOTH_STONE_SLAB,
            MagicAltar.MagicAltarBlock.class
    );

    public static final PylonBlockSchema PEDESTAL = new PylonBlockSchema(
            pylonKey("pedestal"),
            Material.STONE_BRICK_WALL,
            Pedestal.PedestalBlock.class
    );

    static void register() {
        SPRINKLER.register();
        PEDESTAL.register();
        MAGIC_ALTAR.register();
    }
}
