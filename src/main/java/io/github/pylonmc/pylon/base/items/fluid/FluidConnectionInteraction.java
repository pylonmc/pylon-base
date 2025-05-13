package io.github.pylonmc.pylon.base.items.fluid;

import io.github.pylonmc.pylon.base.PylonEntities;
import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import io.github.pylonmc.pylon.core.entity.base.PylonUnloadEntity;
import io.github.pylonmc.pylon.core.entity.display.InteractionBuilder;
import io.github.pylonmc.pylon.core.event.PylonEntityUnloadEvent;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;


public class FluidConnectionInteraction extends PylonEntity<PylonEntitySchema, Interaction> implements PylonUnloadEntity {

    private static final NamespacedKey CONNECTION_POINT_KEY = KeyUtils.pylonKey("connection_point");
    private static final NamespacedKey DISPLAY_KEY = KeyUtils.pylonKey("display");
    private static final NamespacedKey FACE_KEY = KeyUtils.pylonKey("face");
    private static final NamespacedKey RADIUS_KEY = KeyUtils.pylonKey("radius");

    @Getter private final FluidConnectionPoint point;
    @Getter private final UUID display;
    @Nullable @Getter private final BlockFace face;
    @Nullable @Getter private final Float radius;

    @SuppressWarnings("unused")
    public FluidConnectionInteraction(@NotNull PylonEntitySchema schema, @NotNull Interaction entity) {
        super(schema, entity);
        this.point = entity.getPersistentDataContainer().get(CONNECTION_POINT_KEY, PylonSerializers.FLUID_CONNECTION_POINT);
        this.display = entity.getPersistentDataContainer().get(DISPLAY_KEY, PylonSerializers.UUID);
        this.face = entity.getPersistentDataContainer().get(FACE_KEY, PylonSerializers.BLOCK_FACE);
        this.radius = entity.getPersistentDataContainer().get(RADIUS_KEY, PylonSerializers.FLOAT);
        FluidManager.add(point);
    }

    private FluidConnectionInteraction(@NotNull FluidConnectionPoint point, @NotNull BlockFace face, float radius) {
        super(PylonEntities.FLUID_CONNECTION_POINT_INTERACTION, makeInteraction(point, face.getDirection().clone().multiply(radius)));
        this.point = point;
        this.display = FluidConnectionDisplay.make(point, face, radius).getUuid();
        this.face = face;
        this.radius = radius;
        FluidManager.add(point);
    }

    private FluidConnectionInteraction(@NotNull FluidConnectionPoint point) {
        super(PylonEntities.FLUID_CONNECTION_POINT_INTERACTION, makeInteraction(point, new Vector(0, 0, 0)));
        this.point = point;
        this.display = FluidConnectionDisplay.make(point).getUuid();
        this.face = null;
        this.radius = null;
        FluidManager.add(point);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(CONNECTION_POINT_KEY, PylonSerializers.FLUID_CONNECTION_POINT, point);
        PdcUtils.setNullable(pdc, FACE_KEY, PylonSerializers.BLOCK_FACE, face);
        PdcUtils.setNullable(pdc, RADIUS_KEY, PylonSerializers.FLOAT, radius);
    }

    private static @NotNull Interaction makeInteraction(@NotNull FluidConnectionPoint point, @NotNull Vector translation) {
        return new InteractionBuilder()
                .width(0.12F)
                .height(0.12F)
                .build(point.getPosition().getLocation().toCenterLocation().add(translation));
    }

    /**
     * Convenience function that constructs the interaction, but then also adds it to EntityStorage
     */
    public static @NotNull FluidConnectionInteraction make(
            @NotNull FluidConnectionPoint point,
            @NotNull BlockFace face,
            float radius
    ) {
        FluidConnectionInteraction interaction = new FluidConnectionInteraction(point, face, radius);
        EntityStorage.add(interaction);
        return interaction;
    }

    /**
     * Convenience function that constructs the interaction, but then also adds it to EntityStorage
     */
    public static @NotNull FluidConnectionInteraction make(@NotNull FluidConnectionPoint point) {
        FluidConnectionInteraction interaction = new FluidConnectionInteraction(point);
        EntityStorage.add(interaction);
        return interaction;
    }

    @Override
    public void onUnload(@NotNull PylonEntityUnloadEvent event) {
        FluidManager.remove(point);
        FluidConnectionDisplay displayEntity = EntityStorage.getAs(FluidConnectionDisplay.class, display);
        if (getEntity().isDead()) {
            displayEntity.getEntity().remove();
        }
    }
}
