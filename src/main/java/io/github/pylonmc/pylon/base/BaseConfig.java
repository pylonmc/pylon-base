package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.core.config.Config;


public final class BaseConfig {

    private static final Config config = new Config(PylonBase.getInstance(), "config.yml");

    private BaseConfig() {}
}
