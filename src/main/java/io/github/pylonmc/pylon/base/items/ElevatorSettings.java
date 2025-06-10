package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.config.ConfigSection;


public record ElevatorSettings(
        int range
) {
    public static ElevatorSettings fromConfig(ConfigSection config) {
        return new ElevatorSettings(
                config.getOrThrow("range", Integer.class)
        );
    }
}
