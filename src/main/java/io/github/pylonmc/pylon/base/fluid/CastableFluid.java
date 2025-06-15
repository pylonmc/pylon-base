package io.github.pylonmc.pylon.base.fluid;

import io.github.pylonmc.pylon.core.fluid.PylonFluidTag;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public record CastableFluid(@NonNull ItemStack castResult, double castTemperature) implements PylonFluidTag {

    @Override
    public @NotNull Component getDisplayText() {
        return Component.translatable(
                "pylon.pylonbase.fluid.castable.display-text",
                PylonArgument.of("result", castResult.getData(DataComponentTypes.ITEM_NAME))
        );
    }
}
