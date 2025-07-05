package io.github.pylonmc.pylon.base.items.hydraulic.machines;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.items.multiblocks.MixingPot;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import lombok.Getter;
import net.kyori.adventure.text.Component;
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


public class HydraulicMixingAttachment extends SimpleHydraulicMachine implements PylonMultiblock, PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("hydraulic_mixing_attachment");
    public static final NamespacedKey COOLDOWN_TIME_REMAINING_KEY = pylonKey("cooldown_time_remaining");

    public static final int COOLDOWN_TICKS = Settings.get(KEY).getOrThrow("cooldown-ticks", Integer.class);

    public static final int DOWN_ANIMATION_TIME_TICKS = Settings.get(KEY).getOrThrow("down-animation-time-ticks", Integer.class);
    public static final int UP_ANIMATION_TIME_TICKS = Settings.get(KEY).getOrThrow("up-animation-time-ticks", Integer.class);

    public static final double HYDRAULIC_FLUID_MB_PER_CRAFT = Settings.get(KEY).getOrThrow("hydraulic-fluid-mb-per-craft", Integer.class);
    public static final double DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT = Settings.get(KEY).getOrThrow("dirty-hydraulic-fluid-mb-per-craft", Integer.class);
    public static final double HYDRAULIC_FLUID_BUFFER = HYDRAULIC_FLUID_MB_PER_CRAFT * 2;
    public static final double DIRTY_HYDRAULIC_FLUID_BUFFER = DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT * 2;

    public static final int TICK_INTERVAL = Settings.get(KEY).getOrThrow("tick-interval", Integer.class);

    public static final Component MISSING_MIXING_POT = Component.translatable("pylon.pylonbase.message.hydraulic_status.missing_mixing_pot");

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "cooldown", UnitFormat.SECONDS.format(COOLDOWN_TICKS / 20.0),
                    "hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(HYDRAULIC_FLUID_MB_PER_CRAFT),
                    "dirty_hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT)
            );
        }
    }

    @Getter private double cooldownTimeRemaining;
    @Getter private Component status = IDLE;

    @SuppressWarnings("unused")
    public HydraulicMixingAttachment(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        cooldownTimeRemaining = 0.0;
    }

    @SuppressWarnings("unused")
    public HydraulicMixingAttachment(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        cooldownTimeRemaining = pdc.get(COOLDOWN_TIME_REMAINING_KEY, PylonSerializers.DOUBLE);
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
    public void write(@NotNull PersistentDataContainer pdc) {
        super.write(pdc);
        pdc.set(COOLDOWN_TIME_REMAINING_KEY, PylonSerializers.DOUBLE, cooldownTimeRemaining);
    }

    @Override
    public @NotNull Map<String, PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        Map<String, PylonEntity<?>> entities = super.createEntities(context);
        entities.put("mixing_attachment_shaft", new ShaftEntity(getBlock()));
        return entities;
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock().getChunk()));
    }

    @Override
    public boolean checkFormed() {
        return BlockStorage.get(getBlock().getRelative(BlockFace.DOWN, 2)) instanceof MixingPot;
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
            status = MISSING_MIXING_POT;
            return;
        }

        cooldownTimeRemaining = Math.max(0, cooldownTimeRemaining - deltaSeconds);

        if (cooldownTimeRemaining > 1.0e-5) {
            status = WORKING;
            return;
        }

        MixingPot mixingPot = BlockStorage.getAs(MixingPot.class, getBlock().getRelative(BlockFace.DOWN, 2));
        Preconditions.checkState(mixingPot != null);

        if (hydraulicFluidAmount < HYDRAULIC_FLUID_MB_PER_CRAFT) {
            status = NOT_ENOUGH_HYDRAULIC_FLUID;
            return;
        }

        if (getRemainingDirtyCapacity() < DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT) {
            status = DIRTY_HYDRAULIC_FLUID_BUFFER_FULL;
            return;
        }

        if (!mixingPot.tryDoRecipe(null)) {
            status = IDLE;
            return;
        }

        startCraft(HYDRAULIC_FLUID_MB_PER_CRAFT, DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT);
        cooldownTimeRemaining = COOLDOWN_TICKS / 20.0;
        getMotorShaft().goDown();
        Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> getMotorShaft().goUp(), DOWN_ANIMATION_TIME_TICKS);
        status = WORKING;
    }

    public @NotNull HydraulicMixingAttachment.ShaftEntity getMotorShaft() {
        return Objects.requireNonNull(getHeldEntity(ShaftEntity.class, "mixing_attachment_shaft"));
    }

    public static class ShaftEntity extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("mixing_attachment_shaft");

        @SuppressWarnings("unused")
        public ShaftEntity(@NotNull ItemDisplay entity) {
            super(entity);
        }

        public ShaftEntity(@NotNull  Block block) {
            super(KEY, new ItemDisplayBuilder()
                            .material(Material.LIGHT_GRAY_CONCRETE)
                            .transformation(getTransformation(0.7))
                            .build(block.getLocation().toCenterLocation().add(0, -1, 0))
            );
        }

        public void goDown() {
            getEntity().setTransformationMatrix(getTransformation(0.2));
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(DOWN_ANIMATION_TIME_TICKS);
        }

        public void goUp() {
            getEntity().setTransformationMatrix(getTransformation(0.7));
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(UP_ANIMATION_TIME_TICKS);
        }

        private static @NotNull Matrix4f getTransformation(double yTranslation) {
            return new TransformBuilder()
                    .translate(0, yTranslation, 0)
                    .scale(0.2, 1.5, 0.2)
                    .buildForItemDisplay();

        }
    }
}
