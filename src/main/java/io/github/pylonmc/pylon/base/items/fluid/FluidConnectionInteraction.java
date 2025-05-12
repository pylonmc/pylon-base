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
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;


public class FluidConnectionInteraction extends PylonEntity<PylonEntitySchema, Interaction> implements PylonUnloadEntity {

    private static final NamespacedKey CONNECTION_POINT_KEY = KeyUtils.pylonKey("connection_point");
    private static final NamespacedKey FACE_KEY = KeyUtils.pylonKey("face");

    @Getter private final FluidConnectionPoint point;
    @Getter private final BlockFace face;

    @SuppressWarnings("unused")
    public FluidConnectionInteraction(@NotNull PylonEntitySchema schema, @NotNull Interaction entity) {
        super(schema, entity);
        this.point = entity.getPersistentDataContainer().get(CONNECTION_POINT_KEY, PylonSerializers.FLUID_CONNECTION_POINT);
        this.face = entity.getPersistentDataContainer().get(FACE_KEY, PylonSerializers.BLOCK_FACE);
        FluidManager.add(point);
    }

    public FluidConnectionInteraction(@NotNull FluidConnectionPoint point, @NotNull BlockFace face) {
        super(PylonEntities.FLUID_CONNECTION_POINT_INTERACTION, makeInteraction(point, face.getDirection().clone().multiply(0.5)));
        this.point = point;
        this.face = face;
        FluidManager.add(point);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(CONNECTION_POINT_KEY, PylonSerializers.FLUID_CONNECTION_POINT, point);
        pdc.set(FACE_KEY, PylonSerializers.BLOCK_FACE, face);
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
    public static @NotNull FluidConnectionInteraction make(@NotNull FluidConnectionPoint point, @NotNull BlockFace face) {
        FluidConnectionInteraction interaction = new FluidConnectionInteraction(point, face);
        EntityStorage.add(interaction);
        return interaction;
    }

    @Override
    public void onUnload(@NotNull PylonEntityUnloadEvent event) {
        FluidManager.remove(point);
    }
}
