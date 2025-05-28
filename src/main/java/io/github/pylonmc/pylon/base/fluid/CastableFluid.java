package io.github.pylonmc.pylon.base.fluid;

import io.github.pylonmc.pylon.core.fluid.PylonFluidTag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record CastableFluid(@NotNull ItemStack castResult, double castTemperature) implements PylonFluidTag {
}
