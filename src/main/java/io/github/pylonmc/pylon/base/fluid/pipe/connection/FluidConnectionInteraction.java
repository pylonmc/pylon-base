package io.github.pylonmc.pylon.base.fluid.pipe.connection;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeDisplay;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.base.PylonDeathEntity;
import io.github.pylonmc.pylon.core.entity.base.PylonUnloadEntity;
import io.github.pylonmc.pylon.core.entity.display.InteractionBuilder;
import io.github.pylonmc.pylon.core.event.PylonEntityDeathEvent;
import io.github.pylonmc.pylon.core.event.PylonEntityUnloadEvent;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class FluidConnectionInteraction extends PylonEntity<Interaction> implements PylonDeathEntity, PylonUnloadEntity {

    public static final float POINT_SIZE = 0.12F;

    private static final NamespacedKey CONNECTED_PIPE_DISPLAYS_KEY = baseKey("connected_pipe_displays");
    private static final NamespacedKey CONNECTION_POINT_KEY = baseKey("connection_point");
    private static final NamespacedKey DISPLAY_KEY = baseKey("display");
    private static final NamespacedKey FACE_KEY = baseKey("face");
    private static final NamespacedKey RADIUS_KEY = baseKey("radius");

    @Getter private final Set<UUID> connectedPipeDisplays;
    @Getter private final FluidConnectionPoint point;
    private final UUID display;
    @Nullable @Getter private final BlockFace face;
    @Nullable @Getter private final Float radius;

    @SuppressWarnings("unused")
    public FluidConnectionInteraction(@NotNull Interaction entity) {
        super(entity);

        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        this.connectedPipeDisplays = pdc.get(CONNECTED_PIPE_DISPLAYS_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.UUID));
        this.point = pdc.get(CONNECTION_POINT_KEY, PylonSerializers.FLUID_CONNECTION_POINT);
        this.display = pdc.get(DISPLAY_KEY, PylonSerializers.UUID);
        this.face = pdc.get(FACE_KEY, PylonSerializers.BLOCK_FACE);
        this.radius = pdc.get(RADIUS_KEY, PylonSerializers.FLOAT);

        Preconditions.checkState(point != null);
        FluidManager.add(point);
    }

    private FluidConnectionInteraction(@NotNull FluidConnectionPoint point, @NotNull BlockFace face, float radius) {
        super(BaseKeys.FLUID_CONNECTION_INTERACTION, makeInteraction(point, face.getDirection().clone().multiply(radius)));

        this.connectedPipeDisplays = new HashSet<>();
        this.point = point;
        this.display = FluidConnectionDisplay.make(point, face, radius).getUuid();
        this.face = face;
        this.radius = radius;

        FluidManager.add(point);
    }

    private FluidConnectionInteraction(@NotNull FluidConnectionPoint point) {
        super(BaseKeys.FLUID_CONNECTION_INTERACTION, makeInteraction(point, new Vector(0, 0, 0)));

        this.connectedPipeDisplays = new HashSet<>();
        this.point = point;
        this.display = FluidConnectionDisplay.make(point).getUuid();
        this.face = null;
        this.radius = null;

        FluidManager.add(point);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(CONNECTED_PIPE_DISPLAYS_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.UUID), connectedPipeDisplays);
        pdc.set(CONNECTION_POINT_KEY, PylonSerializers.FLUID_CONNECTION_POINT, point);
        PdcUtils.setNullable(pdc, DISPLAY_KEY, PylonSerializers.UUID, display);
        PdcUtils.setNullable(pdc, FACE_KEY, PylonSerializers.BLOCK_FACE, face);
        PdcUtils.setNullable(pdc, RADIUS_KEY, PylonSerializers.FLOAT, radius);
    }

    private static @NotNull Interaction makeInteraction(@NotNull FluidConnectionPoint point, @NotNull Vector translation) {
        return new InteractionBuilder()
                .width(POINT_SIZE)
                .height(POINT_SIZE)
                .build(point.getPosition().getLocation().toCenterLocation().add(translation));
    }

    /**
     * Convenience function that constructs the interaction, but then also adds it to EntityStorage
     */
    public static @NotNull FluidConnectionInteraction make(
            @Nullable Player player,
            @NotNull FluidConnectionPoint point,
            @NotNull BlockFace face,
            float radius,
            boolean allowVertical
    ) {
        BlockFace finalFace = face;
        if (player != null) {
            finalFace = PylonUtils.rotateToPlayerFacing(player, face, allowVertical);
        }
        FluidConnectionInteraction interaction = new FluidConnectionInteraction(point, finalFace, radius);
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
    public void onDeath(@NotNull PylonEntityDeathEvent event) {
        for (UUID uuid : connectedPipeDisplays) {
            FluidPipeDisplay pipeDisplay = EntityStorage.getAs(FluidPipeDisplay.class, uuid);
            if (pipeDisplay != null) {
                pipeDisplay.delete(true, null);
            }
        }
        FluidConnectionDisplay displayEntity = EntityStorage.getAs(FluidConnectionDisplay.class, display);
        Preconditions.checkState(displayEntity != null);
        displayEntity.getEntity().remove();
        FluidManager.remove(point);
    }

    @Override
    public void onUnload(@NotNull PylonEntityUnloadEvent event) {
        FluidManager.unload(point);
    }

    public @Nullable FluidConnectionDisplay getDisplay() {
        return EntityStorage.getAs(FluidConnectionDisplay.class, display);
    }
}