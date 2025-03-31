package io.github.pylonmc.pylon.base.util;

import org.bukkit.block.BlockFace;

public final class BlockUtils {
    private BlockUtils() { throw new AssertionError("Utility class"); }

    public static final BlockFace[] IMMEDIATE_FACES = {
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.SOUTH,
            BlockFace.NORTH
    };
    public static final BlockFace[] IMMEDIATE_FACES_WITH_DIAGONALS = {
            BlockFace.UP,
            BlockFace.DOWN,
            BlockFace.EAST,
            BlockFace.WEST,
            BlockFace.SOUTH,
            BlockFace.NORTH,
            BlockFace.NORTH_EAST,
            BlockFace.NORTH_WEST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH_WEST,
            BlockFace.EAST
    };
}
