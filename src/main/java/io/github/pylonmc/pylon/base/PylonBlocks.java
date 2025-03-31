package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.MagicAltar;
import io.github.pylonmc.pylon.base.items.Sprinkler;
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

    static void register() {
        SPRINKLER.register();
        MAGIC_ALTAR.register();
    }
}
