package io.github.pylonmc.pylon.base.items.hydraulic;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.base.items.multiblocks.MixingPot;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import lombok.Getter;
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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class HydraulicMixingAttachment extends PylonBlock implements PylonFluidIoBlock, PylonMultiblock, PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("hydraulic_mixing_attachment");
    public static final NamespacedKey HYDRAULIC_FLUID_AMOUNT_KEY = pylonKey("hydraulic_fluid_amount");
    public static final NamespacedKey DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY = pylonKey("dirty_hydraulic_fluid_amount");
    public static final NamespacedKey COOLDOWN_TIME_REMAINING_KEY = pylonKey("cooldown_time_remaining");

    public static final int COOLDOWN_TICKS = Settings.get(KEY).getOrThrow("cooldown-ticks", Integer.class);

    public static final int DOWN_ANIMATION_TIME_TICKS = Settings.get(KEY).getOrThrow("down-animation-time-ticks", Integer.class);
    public static final int UP_ANIMATION_TIME_TICKS = Settings.get(KEY).getOrThrow("up-animation-time-ticks", Integer.class);

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
                    "cooldown", UnitFormat.SECONDS.format(COOLDOWN_TICKS / 20.0),
                    "hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(HYDRAULIC_FLUID_MB_PER_CRAFT),
                    "dirty_hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT)
            );
        }
    }

    @Getter private double hydraulicFluidAmount;
    @Getter private double dirtyHydraulicFluidAmount;
    @Getter private double cooldownTimeRemaining;

    @SuppressWarnings("unused")
    public HydraulicMixingAttachment(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        hydraulicFluidAmount = 0.0;
        dirtyHydraulicFluidAmount = 0.0;
    }

    @SuppressWarnings("unused")
    public HydraulicMixingAttachment(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        hydraulicFluidAmount = pdc.get(HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
        dirtyHydraulicFluidAmount = pdc.get(DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
        cooldownTimeRemaining = pdc.get(COOLDOWN_TIME_REMAINING_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, hydraulicFluidAmount);
        pdc.set(DIRTY_HYDRAULIC_FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, dirtyHydraulicFluidAmount);
        pdc.set(COOLDOWN_TIME_REMAINING_KEY, PylonSerializers.DOUBLE, cooldownTimeRemaining);
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, BlockFace.NORTH),
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.OUTPUT, BlockFace.SOUTH)
        );
    }

    @Override
    public @NotNull Map<String, PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        Map<String, PylonEntity<?>> entities = PylonFluidIoBlock.super.createEntities(context);
        entities.put("mixing_attachment_shaft", new ShaftEntity(getBlock()));
        return entities;
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(PylonFluids.DIRTY_HYDRAULIC_FLUID, dirtyHydraulicFluidAmount);
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        dirtyHydraulicFluidAmount -= amount;
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(PylonFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER - hydraulicFluidAmount);
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        hydraulicFluidAmount += amount;
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock().getChunk()));
    }

    @Override
    public boolean checkFormed() {
        return BlockStorage.get(getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN)) instanceof MixingPot;
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

        cooldownTimeRemaining = Math.max(0, cooldownTimeRemaining - deltaSeconds);

        if (cooldownTimeRemaining > 1.0e-5) {
            return;
        }

        MixingPot mixingPot = BlockStorage.getAs(MixingPot.class, getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN));
        Preconditions.checkState(mixingPot != null);

        // Check enough hydraulic fluid to finish the craft
        double hydraulicFluidRequired = HYDRAULIC_FLUID_MB_PER_CRAFT;
        if (hydraulicFluidAmount < hydraulicFluidRequired) {
            return;
        }

        // Check enough space for dirty hydraulic fluid
        double dirtyHydraulicFluidOutput = DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT;
        if (dirtyHydraulicFluidOutput > DIRTY_HYDRAULIC_FLUID_BUFFER - DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT) {
            return;
        }

        if (mixingPot.tryDoRecipe(null)) {
            hydraulicFluidAmount -= hydraulicFluidRequired;
            dirtyHydraulicFluidAmount += dirtyHydraulicFluidOutput;
            cooldownTimeRemaining = COOLDOWN_TICKS / 20.0;
            getMotorShaft().goDown();
            Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> getMotorShaft().goUp(), UP_ANIMATION_TIME_TICKS);
        }
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
            super(
                    KEY,
                    new ItemDisplayBuilder()
                            .material(Material.LIGHT_GRAY_CONCRETE)
                            .transformation(new TransformBuilder()
                                    .translate(0, 0.4, 0)
                                    .scale(0.9, 0.1, 0.9))
                            .build(block.getLocation().toCenterLocation())
            );
        }

        public void goDown() {
            getEntity().setTransformationMatrix(new TransformBuilder()
                    .translate(0, -0.8, 0)
                    .scale(0.2, 1.5, 0.2)
                    .buildForItemDisplay());
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(DOWN_ANIMATION_TIME_TICKS);
        }

        public void goUp() {
            getEntity().setTransformationMatrix(new TransformBuilder()
                    .translate(0, -0.3, 0)
                    .scale(0.2, 1.5, 0.2)
                    .buildForItemDisplay());
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(UP_ANIMATION_TIME_TICKS);
        }
    }
}
