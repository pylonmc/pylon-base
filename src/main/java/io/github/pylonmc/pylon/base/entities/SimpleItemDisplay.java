package io.github.pylonmc.pylon.base.entities;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import org.bukkit.entity.ItemDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;


public final class SimpleItemDisplay extends PylonEntity<ItemDisplay> {

    @SuppressWarnings("unused")
    public SimpleItemDisplay(@NotNull ItemDisplay entity) {
        super(BaseKeys.SIMPLE_ITEM_DISPLAY, entity);
    }

    public void setTransform(int durationTicks, Matrix4f matrix) {
        getEntity().setTransformationMatrix(matrix);
        getEntity().setInterpolationDelay(0);
        getEntity().setInterpolationDuration(durationTicks);
    }
}
