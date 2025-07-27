package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.entities.SimpleBlockDisplay;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.base.entities.SimpleTextDisplay;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

public final class BaseEntities {

    private BaseEntities() {
        throw new AssertionError("Utility class");
    }

    public static void initialize() {
        PylonEntity.register(BaseKeys.SIMPLE_ITEM_DISPLAY, ItemDisplay.class, SimpleItemDisplay.class);
        PylonEntity.register(BaseKeys.SIMPLE_BLOCK_DISPLAY, BlockDisplay.class, SimpleBlockDisplay.class);
        PylonEntity.register(BaseKeys.SIMPLE_TEXT_DISPLAY, TextDisplay.class, SimpleTextDisplay.class);
    }
}
