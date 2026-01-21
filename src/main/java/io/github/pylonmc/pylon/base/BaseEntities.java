package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.util.DisplayProjectile;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import org.bukkit.entity.ItemDisplay;


public class BaseEntities {

    private BaseEntities() {
        throw new AssertionError("Utility class");
    }

    static {
        PylonEntity.register(BaseKeys.DISPLAY_PROJECTILE, ItemDisplay.class, DisplayProjectile.class, false);
    }

    // Calling this method forces all the static blocks to run, which initializes our items
    public static void initialize() {}
}
