package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import lombok.Getter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.Style;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
    private String shape = "single";

    private BlockPosition fluidTank = null;

    public FluidTankCasing(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public FluidTankCasing(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @Nullable ItemStack getBlockTextureItem() {
        return ItemStackBuilder.of(super.getBlockTextureItem())
                .addCustomModelDataString(shape)
                .build();
    }

    public FluidTank getFluidTank() {
        if (fluidTank == null) {
            return null;
        }

        FluidTank tank = BlockStorage.getAs(FluidTank.class, fluidTank.getBlock());
        if (tank == null) {
            fluidTank = null;
            return null;
        } else {
            return tank;
        }
    }

    public void setShape(@NotNull String shape) {
        this.shape = shape;
        refreshBlockTextureItem();
    }

    public void setFluidTank(FluidTank fluidTank) {
        this.fluidTank = fluidTank == null ? null : new BlockPosition(fluidTank.getBlock());
    }

    public void reset() {
        fluidTank = null;
        setShape("single");
    }

    @Override
    public @Nullable WailaConfig getWaila(@NotNull Player player) {
        Component info;
        FluidTank fluidTank = getFluidTank();
        if (fluidTank == null || fluidTank.getFluidType() == null) {
            info = Component.translatable("pylon.pylonbase.waila.fluid_tank.empty");
        } else {
            info = Component.translatable(
                    "pylon.pylonbase.waila.fluid_tank.filled",
                    PylonArgument.of("amount", Math.round(fluidTank.getFluidAmount())),
                    PylonArgument.of("capacity", UnitFormat.MILLIBUCKETS.format(fluidTank.getFluidCapacity())
                            .decimalPlaces(0)
                            .unitStyle(Style.empty())
                    ),
                    PylonArgument.of("fluid", fluidTank.getFluidType().getName())
            );
        }
        return new WailaConfig(getDefaultWailaTranslationKey().arguments(PylonArgument.of("info", info)));
    }
}
