package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.*;
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

    public static final PylonBlockSchema PEDESTAL = new PylonBlockSchema(
            pylonKey("pedestal"),
            Material.STONE_BRICK_WALL,
            Pedestal.PedestalBlock.class
    );

    public static final PylonBlockSchema MAGIC_PEDESTAL = new PylonBlockSchema(
            pylonKey("magic_pedestal"),
            Material.MOSSY_STONE_BRICK_WALL,
            Pedestal.PedestalBlock.class
    );

    public static final MagicAltar.MagicAltarBlock.Schema MAGIC_ALTAR = new MagicAltar.MagicAltarBlock.Schema(
            pylonKey("magic_altar"),
            Material.SMOOTH_STONE_SLAB
    );

    public static final PylonBlockSchema GRINDSTONE = new PylonBlockSchema(
            pylonKey("grindstone"),
            Material.SMOOTH_STONE_SLAB,
            Grindstone.GrindstoneBlock.class
    );

    public static final PylonBlockSchema GRINDSTONE_HANDLE = new PylonBlockSchema(
            pylonKey("grindstone_handle"),
            Material.OAK_FENCE,
            GrindstoneHandle.GrindstoneHandleBlock.class
    );

    public static final ExplosiveTarget.ExplosiveTargetBlock.Schema EXPLOSIVE_TARGET = new ExplosiveTarget.ExplosiveTargetBlock.Schema(
            pylonKey("explosive_target"),
            Material.TARGET,
            5.0f,
            false
    );

    public static final ExplosiveTarget.ExplosiveTargetBlock.Schema FIERY_EXPLOSIVE_TARGET = new ExplosiveTarget.ExplosiveTargetBlock.Schema(
            pylonKey("fiery_explosive_target"),
            Material.TARGET,
            5.0f,
            true
    );

    public static final ExplosiveTarget.ExplosiveTargetBlock.Schema SUPER_EXPLOSIVE_TARGET = new ExplosiveTarget.ExplosiveTargetBlock.Schema(
            pylonKey("super_explosive_target"),
            Material.TARGET,
            15.0f,
            false
    );

    public static final ExplosiveTarget.ExplosiveTargetBlock.Schema SUPER_FIERY_EXPLOSIVE_TARGET = new ExplosiveTarget.ExplosiveTargetBlock.Schema(
            pylonKey("super_fiery_explosive_target"),
            Material.TARGET,
            15.0f,
            true
    );

    static void register() {
        SPRINKLER.register();
        PEDESTAL.register();
        MAGIC_PEDESTAL.register();
        MAGIC_ALTAR.register();
        GRINDSTONE.register();
        GRINDSTONE_HANDLE.register();
        EXPLOSIVE_TARGET.register();
        FIERY_EXPLOSIVE_TARGET.register();
        SUPER_EXPLOSIVE_TARGET.register();
        SUPER_FIERY_EXPLOSIVE_TARGET.register();
    }
}
