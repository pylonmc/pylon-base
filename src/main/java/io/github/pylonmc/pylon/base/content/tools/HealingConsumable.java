package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonConsumable;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class HealingConsumable extends PylonItem implements PylonConsumable {
    private final double consumeSeconds = getSettings().getOrThrow("consume-seconds", ConfigAdapter.DOUBLE);
    private final double healAmount = getSettings().getOrThrow("heal-amount", ConfigAdapter.DOUBLE);
    public HealingConsumable(@NotNull ItemStack stack){ super(stack); }

    @Override
    public @NotNull List<PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("consume-seconds", UnitFormat.SECONDS.format(consumeSeconds)),
                PylonArgument.of("heal-amount", UnitFormat.HEARTS.format(healAmount))
        );
    }

    @Override
    public void onConsumed(@NotNull PlayerItemConsumeEvent event) {
        event.getPlayer().heal(healAmount);
    }
}
