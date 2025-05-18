package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import org.bukkit.Material;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public final class PylonFluids {

    private PylonFluids() {
        throw new AssertionError("Utility class");
    }

    public static final PylonFluid WATER = new PylonFluid(
            pylonKey("water"),
            Material.BLUE_CONCRETE
    ).addTag(new FluidTemperature(20));
    static {
        WATER.register();
    }

    public static final PylonFluid LAVA = new PylonFluid(
            pylonKey("lava"),
            Material.ORANGE_CONCRETE
    ).addTag(new FluidTemperature(850));
    static {
        LAVA.register();
    }

    public static void initialize() {}
}
