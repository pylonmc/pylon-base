package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.Grindstone;
import io.github.pylonmc.pylon.base.items.Pedestal;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionDisplay;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.fluid.items.FluidTank;
import io.github.pylonmc.pylon.base.items.fluid.pipe.FluidPipeDisplay;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import org.bukkit.entity.Interaction;
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
    static {
        PEDESTAL_ITEM.register();
    }

    public static final PylonEntitySchema GRINDSTONE_ITEM = new PylonEntitySchema(
            pylonKey("grindstone_item"),
            ItemDisplay.class,
            Grindstone.GrindstoneItemEntity.class
    );
    static {
        GRINDSTONE_ITEM.register();
    }

    public static final PylonEntitySchema GRINDSTONE_BLOCK = new PylonEntitySchema(
            pylonKey("grindstone_block"),
            ItemDisplay.class,
            Grindstone.GrindstoneBlockEntity.class
    );
    static {
        GRINDSTONE_BLOCK.register();
    }

    public static final PylonEntitySchema FLUID_CONNECTION_POINT_DISPLAY = new PylonEntitySchema(
            pylonKey("fluid_connection_point_display"),
            ItemDisplay.class,
            FluidConnectionDisplay.class
    );
    static {
        FLUID_CONNECTION_POINT_DISPLAY.register();
    }

    public static final PylonEntitySchema FLUID_CONNECTION_POINT_INTERACTION = new PylonEntitySchema(
            pylonKey("fluid_connection_point_interaction"),
            Interaction.class,
            FluidConnectionInteraction.class
    );
    static {
        FLUID_CONNECTION_POINT_INTERACTION.register();
    }

    public static final PylonEntitySchema FLUID_PIPE_DISPLAY = new PylonEntitySchema(
            pylonKey("fluid_pipe_display"),
            ItemDisplay.class,
            FluidPipeDisplay.class
    );
    static {
        FLUID_PIPE_DISPLAY .register();
    }

    public static final PylonEntitySchema FLUID_TANK_DISPLAY = new PylonEntitySchema(
            pylonKey("fluid_tank_display"),
            ItemDisplay.class,
            FluidTank.FluidTankEntity.class
    );
    static {
        FLUID_TANK_DISPLAY.register();
    }

    public static void initialize() {}
}
