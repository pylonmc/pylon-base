package io.github.pylonmc.pylon;

import io.github.pylonmc.rebar.config.Config;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;


public final class PylonConfig {

    private static final Config config = new Config(Pylon.getInstance(), "config.yml");
    public static final double RUNE_CHECK_RANGE = config.getOrThrow("rune-check-range", ConfigAdapter.DOUBLE);
    public static final long DEFAULT_TALISMAN_TICK_INTERVAL = config.getOrThrow("default-talisman-tick-interval", ConfigAdapter.LONG);

    private PylonConfig() {}
}
