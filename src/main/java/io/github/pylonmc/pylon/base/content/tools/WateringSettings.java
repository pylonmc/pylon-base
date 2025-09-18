package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

public record WateringSettings(
        int horizontalRange,
        int verticalRange,
        double cropChance,
        double sugarCaneChance,
        double cactusChance,
        double saplingChance,
        double particleChance,
        @NotNull Sound sound
) {
    public static WateringSettings fromConfig(ConfigSection config) {
        return new WateringSettings(
                config.getOrThrow("range.horizontal", ConfigAdapter.INT),
                config.getOrThrow("range.vertical", ConfigAdapter.INT),
                config.getOrThrow("chances.crops", ConfigAdapter.DOUBLE),
                config.getOrThrow("chances.sugar-cane", ConfigAdapter.DOUBLE),
                config.getOrThrow("chances.cactus", ConfigAdapter.DOUBLE),
                config.getOrThrow("chances.sapling", ConfigAdapter.DOUBLE),
                config.getOrThrow("particle-chance", ConfigAdapter.DOUBLE),
                config.getOrThrow("sound", ConfigAdapter.SOUND)
        );
    }
}
