package io.github.pylonmc.pylon.base.entities;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;


public final class SimpleTextDisplay extends PylonEntity<TextDisplay> {

    @SuppressWarnings("unused")
    public SimpleTextDisplay(@NotNull TextDisplay entity) {
        super(BaseKeys.SIMPLE_ITEM_DISPLAY, entity);
    }
}
