package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.List;
import java.util.Map;


public class HydraulicFarmer extends PylonBlock
        implements PylonSimpleMultiblock, PylonTickingBlock, PylonFluidBufferBlock {

    private static final Map<Material, Material> CROPS = Map.of(
            Material.CARROT, Material.CARROT,
            Material.POTATO, Material.POTATO,
            Material.WHEAT_SEEDS, Material.WHEAT,
            Material.BEETROOT_SEEDS, Material.BEETROOTS,
            Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM,
            Material.MELON_SEEDS, Material.MELON_SEEDS
    );

    private static final Config settings = Settings.get(BaseKeys.HYDRAULIC_FARMER);
    public static final int RADIUS = settings.getOrThrow("radius", ConfigAdapter.INT);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", ConfigAdapter.INT);
    public static final double HYDRAULIC_FLUID_MB_PER_ACTION = settings.getOrThrow("hydraulic-fluid-mb-per-action", ConfigAdapter.DOUBLE);
    public static final double DIRTY_HYDRAULIC_FLUID_MB_PER_ACTION = settings.getOrThrow("dirty-hydraulic-fluid-mb-per-action", ConfigAdapter.DOUBLE);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("radius", UnitFormat.BLOCKS.format(RADIUS)),
                    PylonArgument.of("tick-interval", UnitFormat.SECONDS.format(TICK_INTERVAL / 20.0)),
                    PylonArgument.of("hydraulic-fluid-per-action", UnitFormat.MILLIBUCKETS.format(HYDRAULIC_FLUID_MB_PER_ACTION)),
                    PylonArgument.of("dirty-hydraulic-fluid-per-action", UnitFormat.MILLIBUCKETS.format(DIRTY_HYDRAULIC_FLUID_MB_PER_ACTION))
            );
        }
    }

    @SuppressWarnings("unused")
    public HydraulicFarmer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setTickInterval(TICK_INTERVAL);

        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));

        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_MB_PER_ACTION * 2, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, DIRTY_HYDRAULIC_FLUID_MB_PER_ACTION * 2, false, true);
    }

    @SuppressWarnings("unused")
    public HydraulicFarmer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Map<@NotNull Vector3i, @NotNull MultiblockComponent> getComponents() {
        return Map.of(
                new Vector3i(0, 1, 0), new VanillaMultiblockComponent(Material.CHEST)
        );
    }


    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
            return;
        }

        if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < HYDRAULIC_FLUID_MB_PER_ACTION
                || fluidCapacity(BaseFluids.DIRTY_HYDRAULIC_FLUID) < DIRTY_HYDRAULIC_FLUID_MB_PER_ACTION
        ) {
            return;
        }

        // Attempt to break crops
        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                Block cropBlock = getBlock().getRelative(x, 0, z);
                if (CROPS.containsValue(cropBlock.getType())
                        && cropBlock.getBlockData() instanceof Ageable ageable
                        && ageable.getAge() == ageable.getMaximumAge()
                ) {
                    cropBlock.breakNaturally();
                    removeFluid(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_MB_PER_ACTION);
                    addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, DIRTY_HYDRAULIC_FLUID_MB_PER_ACTION);
                    return;
                }
            }
        }

        // Attempt to plant crops
        Chest chest = (Chest) getBlock().getRelative(0, 1, 0).getState();
        ItemStack cropToPlantStack = null;
        for (ItemStack stack : chest.getBlockInventory()) {
            if (stack != null && CROPS.containsKey(stack.getType())) {
                cropToPlantStack = stack;
            }
        }
        if (cropToPlantStack == null) {
            return;
        }

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                Block cropBlock = getBlock().getRelative(x, 0, z);
                Block farmlandBlock = getBlock().getRelative(x, -1, z);
                if (cropBlock.isEmpty() && farmlandBlock.getType() == Material.FARMLAND) {
                    cropBlock.setType(CROPS.get(cropToPlantStack.getType()));
                    cropToPlantStack.subtract();
                    removeFluid(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_MB_PER_ACTION);
                    addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, DIRTY_HYDRAULIC_FLUID_MB_PER_ACTION);
                    return;
                }
            }
        }
    }
}
