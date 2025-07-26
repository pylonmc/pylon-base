package io.github.pylonmc.pylon.base.content.food;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class GoldenPie extends PylonItem {
    public final int nutrition = getSettings().getOrThrow("nutrition", Integer.class);
    public final float saturation = getSettings().getOrThrow("saturation", Double.class).floatValue();
    public final int absorptionStrength = getSettings().getOrThrow("absorption-strength", Integer.class);
    public final int absorptionDurationTicks = getSettings().getOrThrow("absorption-duration", Integer.class);
    public final int regenerationStrength = getSettings().getOrThrow("regeneration-strength", Integer.class);
    public final int regenerationDurationTicks = getSettings().getOrThrow("regeneration-duration", Integer.class);
    public final int jumpBoostStrength = getSettings().getOrThrow("jumpboost-strength", Integer.class);
    public final int jumpBoostDurationTicks = getSettings().getOrThrow("jumpboost-duration", Integer.class);
    public final int fireResDurationTicks = getSettings().getOrThrow("fireres-duration", Integer.class);
    public final int waterBreathingDurationTicks = getSettings().getOrThrow("waterbreathing-duration", Integer.class);
    public final int luckStrength = getSettings().getOrThrow("luck-strength", Integer.class);
    public final int luckDurationTicks = getSettings().getOrThrow("luck-duration", Integer.class);
    public final int resistanceStrength = getSettings().getOrThrow("resistance-strength", Integer.class);
    public final int resistanceDurationTicks = getSettings().getOrThrow("resistance-duration", Integer.class);
    public final int speedStrength = getSettings().getOrThrow("speed-strength", Integer.class);
    public final int speedDurationTicks = getSettings().getOrThrow("speed-duration", Integer.class);
    public final int strengthStrength = getSettings().getOrThrow("strength-strength", Integer.class);
    public final int strengthDurationTicks = getSettings().getOrThrow("strength-duration", Integer.class);

    public GoldenPie(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
        // Map.of only goes up to 10
        return Map.ofEntries(
                Map.entry("nutrition", Component.text(nutrition)),
                Map.entry("saturation", Component.text(saturation)),
                Map.entry("absorption-strength", Component.text(absorptionStrength)),
                Map.entry("absorption-duration", UnitFormat.SECONDS.format(absorptionDurationTicks)),
                Map.entry("regeneration-strength", Component.text(regenerationStrength)),
                Map.entry("regeneration-duration", UnitFormat.SECONDS.format(regenerationDurationTicks)),
                Map.entry("jumpboost-strength", Component.text(jumpBoostStrength)),
                Map.entry("jumpboost-duration", UnitFormat.SECONDS.format(jumpBoostDurationTicks)),
                Map.entry("fireres-duration", UnitFormat.SECONDS.format(fireResDurationTicks)),
                Map.entry("waterbreathing-duration", UnitFormat.SECONDS.format(waterBreathingDurationTicks)),
                Map.entry("luck-strength", Component.text(luckStrength)),
                Map.entry("luck-duration", UnitFormat.SECONDS.format(luckDurationTicks)),
                Map.entry("resistance-strength", Component.text(resistanceStrength)),
                Map.entry("resistance-duration", UnitFormat.SECONDS.format(resistanceDurationTicks)),
                Map.entry("speed-strength", Component.text(speedStrength)),
                Map.entry("speed-duration", UnitFormat.SECONDS.format(speedDurationTicks)),
                Map.entry("strength-strength", Component.text(strengthStrength)),
                Map.entry("strength-duration", UnitFormat.SECONDS.format(strengthDurationTicks))
        );
    }
}