package io.github.pylonmc.pylon.base.items.fluid.pipe;

import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.LineBuilder;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.UUID;


public class FluidPipeDisplay extends PylonEntity<PylonEntitySchema, ItemDisplay> {

    private static final NamespacedKey SEGMENT_1_KEY = KeyUtils.pylonKey("segment_1");
    private static final NamespacedKey SEGMENT_2_KEY = KeyUtils.pylonKey("segment_2");

    @Getter private final UUID from;
    @Getter private final UUID to;

    public FluidPipeDisplay(@NotNull PylonEntitySchema schema, @NotNull ItemDisplay entity) {
        super(schema, entity);
        from = entity.getPersistentDataContainer().get(SEGMENT_1_KEY, PylonSerializers.UUID);
        to = entity.getPersistentDataContainer().get(SEGMENT_2_KEY, PylonSerializers.UUID);
    }

    public FluidPipeDisplay(
            @NotNull PylonEntitySchema schema,
            @NotNull PylonItemSchema pipe,
            @NotNull FluidConnectionInteraction from,
            @NotNull FluidConnectionInteraction to
    ) {
        super(schema, makeDisplay(pipe, from, to));
        this.from = from.getUuid();
        this.to = to.getUuid();
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(SEGMENT_1_KEY, PylonSerializers.UUID, from);
        pdc.set(SEGMENT_2_KEY, PylonSerializers.UUID, to);
    }

    private static @NotNull ItemDisplay makeDisplay(
            @NotNull PylonItemSchema pipe,
            @NotNull FluidConnectionInteraction from,
            @NotNull FluidConnectionInteraction to
    ) {
        float height = from.getEntity().getInteractionHeight();
        Location fromLocation = from.getEntity().getLocation().add(0, height / 2, 0);
        Location toLocation = to.getEntity().getLocation().add(0, height / 2, 0);
        Vector3f offset = toLocation.subtract(fromLocation).toVector().toVector3f();

        return new ItemDisplayBuilder()
                .transformation(new LineBuilder()
                        .from(0, 0, 0)
                        .to(offset)
                        .thickness(0.1)
                        .build()
                        .buildForItemDisplay()
                )
                .material(pipe.getItemStack().getType())
                .build(fromLocation);
    }

    /**
     * Convenience function that constructs the display, but then also adds it to EntityStorage
     */
    public static @NotNull FluidPipeDisplay make(
            @NotNull PylonEntitySchema schema,
            @NotNull PylonItemSchema pipe,
            @NotNull FluidConnectionInteraction from,
            @NotNull FluidConnectionInteraction to
    ) {
        FluidPipeDisplay display = new FluidPipeDisplay(schema, pipe, from, to);
        EntityStorage.add(display);
        return display;
    }
}
