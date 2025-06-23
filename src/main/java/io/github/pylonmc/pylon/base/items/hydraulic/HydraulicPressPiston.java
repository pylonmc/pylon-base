package io.github.pylonmc.pylon.base.items.hydraulic;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.items.Press;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class HydraulicPressPiston extends SimpleHydraulicMachine implements PylonMultiblock, PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("hydraulic_press_piston");

    public static final double HYDRAULIC_FLUID_MB_PER_CRAFT = Settings.get(KEY).getOrThrow("hydraulic-fluid-mb-per-craft", Integer.class);
    public static final double DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT = Settings.get(KEY).getOrThrow("dirty-hydraulic-fluid-mb-per-craft", Integer.class);
    public static final double HYDRAULIC_FLUID_BUFFER = HYDRAULIC_FLUID_MB_PER_CRAFT * 2;
    public static final double DIRTY_HYDRAULIC_FLUID_BUFFER = DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT * 2;

    public static final int TICK_INTERVAL = Settings.get(KEY).getOrThrow("tick-interval", Integer.class);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(HYDRAULIC_FLUID_MB_PER_CRAFT),
                    "dirty_hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT)
            );
        }
    }

    @SuppressWarnings("unused")
    public HydraulicPressPiston(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public HydraulicPressPiston(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    double getHydraulicFluidBuffer() {
        return HYDRAULIC_FLUID_BUFFER;
    }

    @Override
    double getDirtyHydraulicFluidBuffer() {
        return DIRTY_HYDRAULIC_FLUID_BUFFER;
    }

    @Override
    public @NotNull Map<String, PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        Map<String, PylonEntity<?>> entities = super.createEntities(context);
        entities.put("press_piston_shaft", new PistonShaftEntity(getBlock()));
        return entities;
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock().getChunk()));
    }

    @Override
    public boolean checkFormed() {
        return BlockStorage.get(getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN)) instanceof Press;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return otherBlock.getLocation().equals(getBlock().getLocation());
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return TICK_INTERVAL;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
            return;
        }

        Press press = BlockStorage.getAs(Press.class, getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN));
        Preconditions.checkState(press != null);

        if (canStartCraft(HYDRAULIC_FLUID_MB_PER_CRAFT, DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT) && press.tryStartRecipe(null)) {
            startCraft(HYDRAULIC_FLUID_MB_PER_CRAFT, DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT);
            getPistonShaft().goDown();
            Bukkit.getScheduler().runTaskLater(
                    PylonBase.getInstance(),
                    () -> getPistonShaft().goUp(),
                    Press.TIME_PER_ITEM_TICKS - Press.RETURN_TO_START_TIME_TICKS
            );
        }
    }

    public @NotNull HydraulicPressPiston.PistonShaftEntity getPistonShaft() {
        return Objects.requireNonNull(getHeldEntity(PistonShaftEntity.class, "press_piston_shaft"));
    }

    public static class PistonShaftEntity extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("press_piston_shaft");

        @SuppressWarnings("unused")
        public PistonShaftEntity(@NotNull ItemDisplay entity) {
            super(entity);
        }

        public PistonShaftEntity(@NotNull  Block block) {
            super(
                    KEY,
                    new ItemDisplayBuilder()
                            .material(Material.SPRUCE_LOG)
                            .transformation(new TransformBuilder()
                                    .translate(0, 0.2, 0)
                                    .scale(0.3, 1.6, 0.3))
                            .build(block.getLocation().toCenterLocation().add(0, -1, 0))
            );
        }

        public void goDown() {
            getEntity().setTransformationMatrix(getTransformation(-0.3));
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(Press.TIME_PER_ITEM_TICKS - Press.RETURN_TO_START_TIME_TICKS);
        }

        public void goUp() {
            getEntity().setTransformationMatrix(getTransformation(1.6));
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(Press.RETURN_TO_START_TIME_TICKS);
        }

        private static @NotNull Matrix4f getTransformation(double yTranslation) {
            return new TransformBuilder()
                    .translate(0, yTranslation, 0)
                    .scale(0.3, yTranslation, 0.3)
                    .buildForItemDisplay();

        }
    }
}
