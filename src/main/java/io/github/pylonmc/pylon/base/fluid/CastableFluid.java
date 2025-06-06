package io.github.pylonmc.pylon.base.fluid;

import io.github.pylonmc.pylon.core.fluid.PylonFluidTag;
import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public record CastableFluid(@NonNull ItemStack castResult, double castTemperature) implements PylonFluidTag {
}
