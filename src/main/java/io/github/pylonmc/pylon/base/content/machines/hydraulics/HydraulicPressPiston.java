package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.machines.simple.Press;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Set;


public class HydraulicPressPiston extends PylonBlock implements PylonMultiblock, PylonTickingBlock, PylonFluidBufferBlock {

    private static final Config settings = Settings.get(BaseKeys.HYDRAULIC_PRESS_PISTON);
    public static final double HYDRAULIC_FLUID_PER_CRAFT = settings.getOrThrow("hydraulic-fluid-per-craft", ConfigAdapter.INT);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", ConfigAdapter.INT);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("hydraulic-fluid-per-craft", UnitFormat.MILLIBUCKETS.format(HYDRAULIC_FLUID_PER_CRAFT))
            );
        }
    }

    @SuppressWarnings("unused")
    public HydraulicPressPiston(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(TICK_INTERVAL);
        addEntity("press_piston_shaft", new ItemDisplayBuilder()
                .itemStack(ItemStackBuilder.of(Material.SPRUCE_LOG)
                        .addCustomModelDataString(getKey() + ":press_piston_shaft")
                )
                .transformation(getTransformation(0.0))
                .build(getBlock().getLocation().toCenterLocation().add(0, -1, 0))
        );
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_PER_CRAFT * 2, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, HYDRAULIC_FLUID_PER_CRAFT * 2, false, true);
    }

    @SuppressWarnings("unused")
    public HydraulicPressPiston(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock().getChunk()));
    }

    @Override
    public boolean checkFormed() {
        return BlockStorage.get(getBlock().getRelative(BlockFace.DOWN, 2)) instanceof Press;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return otherBlock.getLocation().equals(getBlock().getRelative(BlockFace.DOWN, 2).getLocation());
    }

    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
            return;
        }

        Press press = BlockStorage.getAs(Press.class, getBlock().getRelative(BlockFace.DOWN, 2));
        Preconditions.checkState(press != null);

        if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < HYDRAULIC_FLUID_PER_CRAFT
                || fluidCapacity(BaseFluids.DIRTY_HYDRAULIC_FLUID) < HYDRAULIC_FLUID_PER_CRAFT
                || !press.tryStartRecipe(null)
        ) {
            return;
        }

        removeFluid(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_PER_CRAFT);
        addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, HYDRAULIC_FLUID_PER_CRAFT);

        BaseUtils.animate(
                getPistonShaft(),
                Press.TIME_PER_ITEM_TICKS - Press.RETURN_TO_START_TIME_TICKS,
                getTransformation(-0.3)
        );
        Bukkit.getScheduler().runTaskLater(
                PylonBase.getInstance(),
                () -> BaseUtils.animate(getPistonShaft(), Press.RETURN_TO_START_TIME_TICKS, getTransformation(0.0)),
                Press.TIME_PER_ITEM_TICKS - Press.RETURN_TO_START_TIME_TICKS
        );
    }

    private static @NotNull Matrix4f getTransformation(double yTranslation) {
        return new TransformBuilder()
                .translate(0, yTranslation, 0)
                .scale(0.3, 1.6, 0.3)
                .buildForItemDisplay();
    }

    public @NotNull ItemDisplay getPistonShaft() {
        return getHeldEntityOrThrow(ItemDisplay.class, "press_piston_shaft");
    }
}
