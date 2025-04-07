package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.Pedestal;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import org.bukkit.entity.ItemDisplay;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public final class PylonEntities {

    private PylonEntities() {
        throw new AssertionError("Utility class");
    }

    public static final PylonEntitySchema PEDESTAL_ITEM = new PylonEntitySchema(
            pylonKey("pedestal_item"),
            ItemDisplay.class,
            Pedestal.PedestalEntity.class
    );

    static void register() {
        PEDESTAL_ITEM.register();
    }
}
