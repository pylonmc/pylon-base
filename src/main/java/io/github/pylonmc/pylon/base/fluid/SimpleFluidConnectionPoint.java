package io.github.pylonmc.pylon.base.fluid;

import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import org.bukkit.block.BlockFace;

import java.util.Locale;

public record SimpleFluidConnectionPoint(
        String name,
        FluidConnectionPoint.Type type,
        BlockFace face,
        float radius
) {
    public SimpleFluidConnectionPoint(FluidConnectionPoint.Type type, BlockFace face, float radius) {
        this(
                type.name().toLowerCase(Locale.ROOT) + "_" + face.name().toLowerCase(Locale.ROOT),
                type,
                face,
                radius
        );
    }

    public SimpleFluidConnectionPoint(String name, FluidConnectionPoint.Type type, BlockFace face) {
        this(name, type, face, 0.5f);
    }

    public SimpleFluidConnectionPoint(FluidConnectionPoint.Type type, BlockFace face) {
        this(type, face, 0.5f);
    }
}
