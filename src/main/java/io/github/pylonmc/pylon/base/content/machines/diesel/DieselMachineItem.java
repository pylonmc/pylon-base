package io.github.pylonmc.pylon.base.content.machines.diesel;

import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class DieselMachineItem extends PylonItem {

    public final double dieselBuffer = getSettings().getOrThrow("diesel-buffer", ConfigAdapter.DOUBLE);
    public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);

    public DieselMachineItem(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    @MustBeInvokedByOverriders
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return new ArrayList<>(List.of(
                PylonArgument.of("diesel-buffer", dieselBuffer),
                PylonArgument.of("diesel-usage", dieselPerSecond)
        ));
    }
}
