package io.github.pylonmc.pylon.base;

import io.github.pylonmc.rebar.config.Config;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;


public final class BaseConfig {

    private static final Config config = new Config(PylonBase.getInstance(), "config.yml");
    public static final double RUNE_CHECK_RANGE = config.getOrThrow("rune-check-range", ConfigAdapter.DOUBLE);
    public static final long DEFAULT_TALISMAN_TICK_INTERVAL = config.getOrThrow("default-talisman-tick-interval", ConfigAdapter.LONG);

    private BaseConfig() {}
}
