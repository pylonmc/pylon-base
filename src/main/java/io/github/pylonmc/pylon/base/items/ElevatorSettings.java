package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.config.ConfigSection;


public record ElevatorSettings(
        int elevatorFirstRange,
        int elevatorSecondRange,
        int elevatorThirdRange
) {
    public static ElevatorSettings fromConfig(ConfigSection config) {
        return new ElevatorSettings(
                config.getOrThrow("range.first", Integer.class),
                config.getOrThrow("range.second", Integer.class),
                config.getOrThrow("range.third", Integer.class)
        );
    }
}
