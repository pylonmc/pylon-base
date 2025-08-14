package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.core.config.Config;


public final class BaseConfig {

    private static final Config config = new Config(PylonBase.getInstance(), "config.yml");
    public static final double RUNE_CHECK_RANGE = config.getOrThrow("rune-check-getRange", Double.class);

    private BaseConfig() {}
}
