package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.core.config.Config;


@SuppressWarnings("DataFlowIssue")
public final class PylonBaseConfig {

    private static final Config config = new Config(PylonBase.getInstance(), "config.yml");

    public static final int PIPE_PLACEMENT_TASK_INTERVAL_TICKS = config.get("pipe-placement.task-interval-ticks", Integer.class);

    public static final int PIPE_PLACEMENT_MAX_DISTANCE = config.get("pipe-placement.max-distance", Integer.class);

    private PylonBaseConfig() {}
}
