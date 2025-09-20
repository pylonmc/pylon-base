package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.machines.simple.MixingPot;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Set;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class HydraulicMixingAttachment extends PylonBlock
        implements PylonMultiblock, PylonTickingBlock, PylonFluidBufferBlock, PylonEntityHolderBlock {

    public static final NamespacedKey COOLDOWN_TIME_REMAINING_KEY = baseKey("cooldown_time_remaining");

    private static final Config settings = Settings.get(BaseKeys.HYDRAULIC_MIXING_ATTACHMENT);
    public static final int COOLDOWN_TICKS = settings.getOrThrow("cooldown-ticks", ConfigAdapter.INT);
    public static final int DOWN_ANIMATION_TIME_TICKS = settings.getOrThrow("down-animation-time-ticks", ConfigAdapter.INT);
    public static final int UP_ANIMATION_TIME_TICKS = settings.getOrThrow("up-animation-time-ticks", ConfigAdapter.INT);
    public static final double HYDRAULIC_FLUID_MB_PER_CRAFT = settings.getOrThrow("hydraulic-fluid-mb-per-craft", ConfigAdapter.INT);
    public static final double DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT = settings.getOrThrow("dirty-hydraulic-fluid-mb-per-craft", ConfigAdapter.INT);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", ConfigAdapter.INT);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("cooldown", UnitFormat.SECONDS.format(COOLDOWN_TICKS / 20.0)),
                    PylonArgument.of("hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(HYDRAULIC_FLUID_MB_PER_CRAFT)),
                    PylonArgument.of("dirty_hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT))
            );
        }
    }

    @Getter private double cooldownTimeRemaining;

    @SuppressWarnings("unused")
    public HydraulicMixingAttachment(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        cooldownTimeRemaining = 0.0;

        setTickInterval(TICK_INTERVAL);

        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));
        addEntity("mixing_attachment_shaft", new SimpleItemDisplay(new ItemDisplayBuilder()
                .material(Material.LIGHT_GRAY_CONCRETE)
                .transformation(getShaftTransformation(0.7))
                .build(getBlock().getLocation().toCenterLocation().add(0, -1, 0))
        ));

        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_MB_PER_CRAFT * 2, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT * 2, false, true);
    }

    @SuppressWarnings("unused")
    public HydraulicMixingAttachment(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        cooldownTimeRemaining = pdc.get(COOLDOWN_TIME_REMAINING_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        super.write(pdc);
        pdc.set(COOLDOWN_TIME_REMAINING_KEY, PylonSerializers.DOUBLE, cooldownTimeRemaining);
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
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
            return;
        }

        cooldownTimeRemaining = Math.max(0, cooldownTimeRemaining - deltaSeconds);

        if (cooldownTimeRemaining > 1.0e-5) {
            return;
        }

        MixingPot mixingPot = BlockStorage.getAs(MixingPot.class, getBlock().getRelative(BlockFace.DOWN, 2));
        Preconditions.checkState(mixingPot != null);

        if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < HYDRAULIC_FLUID_MB_PER_CRAFT
                || fluidCapacity(BaseFluids.DIRTY_HYDRAULIC_FLUID) < DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT
                || !mixingPot.tryDoRecipe(null)
        ) {
            return;
        }

        removeFluid(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_MB_PER_CRAFT);
        addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT);

        cooldownTimeRemaining = COOLDOWN_TICKS / 20.0;

        getMotorShaft().setTransform(DOWN_ANIMATION_TIME_TICKS, getShaftTransformation(0.2));
        Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(),
                () -> getMotorShaft().setTransform(UP_ANIMATION_TIME_TICKS, getShaftTransformation(0.7)),
                DOWN_ANIMATION_TIME_TICKS
        );
    }

    private static @NotNull Matrix4f getShaftTransformation(double yTranslation) {
        return new TransformBuilder()
                .translate(0, yTranslation, 0)
                .scale(0.2, 1.5, 0.2)
                .buildForItemDisplay();
    }

    public @NotNull SimpleItemDisplay getMotorShaft() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "mixing_attachment_shaft");
    }
}
