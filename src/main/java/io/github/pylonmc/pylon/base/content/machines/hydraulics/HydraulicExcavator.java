package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;


public class HydraulicExcavator extends PylonBlock implements PylonTickingBlock {

    public static final Config settings = Settings.get(BaseKeys.HYDRAULIC_CORE_DRILL);
    public static final int RADIUS = settings.getOrThrow("radius", Integer.class);
    public static final int DEPTH = settings.getOrThrow("depth", Integer.class);
    public static final double SPEED_SECONDS_PER_BLOCK = settings.getOrThrow("speed_seconds_per_block", Double.class);
    public static final int HYDRAULIC_FLUID_INPUT_MB_PER_SECOND = settings.getOrThrow("hydraulic-fluid-input-mb-per-second", Integer.class);
    public static final int DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND = settings.getOrThrow("dirty-hydraulic-fluid-output-mb-per-second", Integer.class);

    public HydraulicExcavator(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public HydraulicExcavator(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void tick(double deltaSeconds) {

    }
}
