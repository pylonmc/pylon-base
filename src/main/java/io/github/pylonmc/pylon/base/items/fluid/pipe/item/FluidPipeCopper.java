package io.github.pylonmc.pylon.base.items.fluid.pipe.item;

import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidPipeCopper extends FluidPipe {

    public static final NamespacedKey KEY = pylonKey("fluid_pipe_copper");

    public static final double FLUID_PER_SECOND = getSettings(KEY).getOrThrow("fluid-per-second", Double.class);
    public static final int MIN_TEMPERATURE = getSettings(KEY).getOrThrow("temperature.min", Integer.class);
    public static final int MAX_TEMPERATURE = getSettings(KEY).getOrThrow("temperature.max", Integer.class);
    public static final Material MATERIAL = Material.BROWN_TERRACOTTA;

    public static final ItemStack ITEM_STACK = ItemStackBuilder.pylonItem(Material.CLAY_BALL, KEY)
            .set(DataComponentTypes.ITEM_MODEL, Material.ORANGE_TERRACOTTA.getKey())
            .build();

    public FluidPipeCopper(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    @Override
    public double getFluidPerSecond() {
        return FLUID_PER_SECOND;
    }

    @Override
    public int getMinTemperature() {
        return MIN_TEMPERATURE;
    }

    @Override
    public int getMaxTemperature() {
        return MAX_TEMPERATURE;
    }

    @Override
    public Material getMaterial() {
        return MATERIAL;
    }
}
