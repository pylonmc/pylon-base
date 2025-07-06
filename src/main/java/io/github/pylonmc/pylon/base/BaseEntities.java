package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.base.entities.SimpleTextDisplay;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeDisplay;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionDisplay;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;

public final class BaseEntities {

    private BaseEntities() {
        throw new AssertionError("Utility class");
    }

    public static void initialize() {
        PylonEntity.register(BaseKeys.SIMPLE_ITEM_DISPLAY, ItemDisplay.class, SimpleItemDisplay.class);
        PylonEntity.register(BaseKeys.SIMPLE_TEXT_DISPLAY, TextDisplay.class, SimpleTextDisplay.class);

        PylonEntity.register(BaseKeys.FLUID_CONNECTION_DISPLAY, ItemDisplay.class, FluidConnectionDisplay.class);
        PylonEntity.register(BaseKeys.FLUID_CONNECTION_INTERACTION, Interaction.class, FluidConnectionInteraction.class);
        PylonEntity.register(BaseKeys.FLUID_PIPE_DISPLAY, ItemDisplay.class, FluidPipeDisplay.class);
    }
}
