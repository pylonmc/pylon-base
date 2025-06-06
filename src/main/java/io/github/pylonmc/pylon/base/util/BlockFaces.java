package io.github.pylonmc.pylon.base.util;

import org.bukkit.block.BlockFace;

public final class BlockFaces {
    private BlockFaces() {
        throw new AssertionError("Utility class");
    }

    public static final BlockFace[] ORTHOGONAL = {
            BlockFace.NORTH,
            BlockFace.EAST,
            BlockFace.SOUTH,
            BlockFace.WEST,
            BlockFace.UP,
            BlockFace.DOWN
    };
}
