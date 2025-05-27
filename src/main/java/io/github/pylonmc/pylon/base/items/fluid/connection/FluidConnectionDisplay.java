package io.github.pylonmc.pylon.base.items.fluid.connection;

import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidConnectionDisplay extends PylonEntity<ItemDisplay> {

    public static final NamespacedKey KEY = pylonKey("fluid_connection_display");

    @SuppressWarnings("unused")
    public FluidConnectionDisplay(@NotNull ItemDisplay entity) {
        super(entity);
    }

    public FluidConnectionDisplay(@NotNull FluidConnectionPoint point, @NotNull BlockFace face, float radius) {
        super(KEY, makeDisplay(point, face.getDirection().clone().multiply(radius).toVector3d()));
    }

    public FluidConnectionDisplay(@NotNull FluidConnectionPoint point) {
        super(KEY, makeDisplay(point, new Vector3d(0, 0, 0)));
    }

    private static @NotNull Material materialFromType(@NotNull FluidConnectionPoint.Type type) {
        return switch (type) {
            case INPUT -> Material.LIME_CONCRETE;
            case OUTPUT -> Material.RED_CONCRETE;
            case CONNECTOR -> Material.GRAY_CONCRETE;
        };
    }

    private static @NotNull ItemDisplay makeDisplay(@NotNull FluidConnectionPoint point, @NotNull Vector3d translation) {
        return new ItemDisplayBuilder()
                .material(materialFromType(point.getType()))
                .brightness(7)
                .transformation(new TransformBuilder()
                        .translate(translation)
                        .scale(FluidConnectionInteraction.POINT_SIZE))
                .build(point.getPosition().getLocation().toCenterLocation());
    }

    /**
     * Convenience function that constructs the display, but then also adds it to EntityStorage
     */
    public static @NotNull FluidConnectionDisplay make(@NotNull FluidConnectionPoint point, @NotNull BlockFace face, float radius) {
        FluidConnectionDisplay display = new FluidConnectionDisplay(point, face, radius);
        EntityStorage.add(display);
        return display;
    }

    /**
     * Convenience function that constructs the display, but then also adds it to EntityStorage
     */
    public static @NotNull FluidConnectionDisplay make(@NotNull FluidConnectionPoint point) {
        FluidConnectionDisplay display = new FluidConnectionDisplay(point);
        EntityStorage.add(display);
        return display;
    }
}
