package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.waila.Waila;
import kotlin.Pair;
import lombok.Getter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FluidTankCasing extends PylonBlock {

    public static class Item extends PylonItem {

        @Getter
        private final double capacity = getSettings().getOrThrow("capacity", ConfigAdapter.DOUBLE);

        @Getter
        private final List<FluidTemperature> allowedTemperatures = getSettings().getOrThrow(
                "allowed-temperatures",
                ConfigAdapter.LIST.from(ConfigAdapter.FLUID_TEMPERATURE)
        );

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("capacity", UnitFormat.MILLIBUCKETS.format(capacity)),
                    PylonArgument.of("temperatures", Component.join(
                            JoinConfiguration.separator(Component.text(", ")),
                            allowedTemperatures.stream()
                                    .map(FluidTemperature::getValueText)
                                    .collect(Collectors.toList())
                    ))
            );
        }
    }

    @Getter
    private final double capacity = getSettings().getOrThrow("capacity", ConfigAdapter.DOUBLE);

    @Getter
    private final List<FluidTemperature> allowedTemperatures = getSettings().getOrThrow(
            "allowed-temperatures",
            ConfigAdapter.LIST.from(ConfigAdapter.FLUID_TEMPERATURE)
    );

    @Getter
    private Shape shape = Shape.SINGLE;

    public FluidTankCasing(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public FluidTankCasing(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Map<String, Pair<String, Integer>> getBlockTextureProperties() {
        var properties = super.getBlockTextureProperties();
        properties.put("shape", new Pair<>(shape.name().toLowerCase(), Shape.values().length));
        return properties;
    }

    public void setShape(@NotNull Shape shape) {
        this.shape = shape;
        refreshBlockTextureItem();
    }

    public void reset() {
        setShape(Shape.SINGLE);
        Waila.removeWailaOverride(new BlockPosition(getBlock()));
    }

    public enum Shape {
        SINGLE,
        BOTTOM,
        MIDDLE,
        TOP
    }
}
