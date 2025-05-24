package io.github.pylonmc.pylon.base.items.fluid.items.tank;

import io.github.pylonmc.pylon.base.items.fluid.items.FluidTank;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidTankWood extends FluidTank {

    public static final NamespacedKey KEY =  pylonKey("fluid_tank_wood");

    public static final double CAPACITY = getSettings(KEY).getOrThrow("capacity", Double.class);
    public static final long MIN_TEMP = getSettings(KEY).getOrThrow("temperature.min", Integer.class);
    public static final long MAX_TEMP = getSettings(KEY).getOrThrow("temperature.max", Integer.class);

    @SuppressWarnings("unused")
    public FluidTankWood(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
        super(schema, block, context);
    }

    @SuppressWarnings("unused")
    public FluidTankWood(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(schema, block, pdc);
    }

    @Override
    public double getCapacity() {
        return CAPACITY;
    }

    @Override
    public long getMinTemp() {
        return MIN_TEMP;
    }

    @Override
    public long getMaxTemp() {
        return MAX_TEMP;
    }
}
