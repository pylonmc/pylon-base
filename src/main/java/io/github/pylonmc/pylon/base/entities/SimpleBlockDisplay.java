package io.github.pylonmc.pylon.base.entities;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import org.bukkit.entity.BlockDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;


public final class SimpleBlockDisplay extends PylonEntity<BlockDisplay> {

    @SuppressWarnings("unused")
    public SimpleBlockDisplay(@NotNull BlockDisplay entity) {
        super(BaseKeys.SIMPLE_BLOCK_DISPLAY, entity);
    }

    public void setTransform(int durationTicks, Matrix4f matrix) {
        getEntity().setTransformationMatrix(matrix);
        getEntity().setInterpolationDelay(0);
        getEntity().setInterpolationDuration(durationTicks);
    }
}
