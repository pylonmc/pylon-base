package io.github.pylonmc.pylon.base.items.watering;

import io.github.pylonmc.pylon.core.config.ConfigSection;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public record WateringSettings(
        int horizontalRange,
        int verticalRange,
        double cropChance,
        double sugarCaneChance,
        double cactusChance,
        double saplingChance,
        @NotNull Sound sound
) {
    public static WateringSettings fromConfig(ConfigSection config) {
        return new WateringSettings(
                config.getOrThrow("range.horizontal", Integer.class),
                config.getOrThrow("range.vertical", Integer.class),
                config.getOrThrow("chances.crops", Double.class),
                config.getOrThrow("chances.sugar_cane", Double.class),
                config.getOrThrow("chances.cactus", Double.class),
                config.getOrThrow("chances.sapling", Double.class),
                Registry.SOUNDS.get(NamespacedKey.fromString(config.getOrThrow("sound", String.class)))
        );
    }
}
