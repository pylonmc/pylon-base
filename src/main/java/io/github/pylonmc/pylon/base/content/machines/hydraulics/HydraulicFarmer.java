package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
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
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class HydraulicFarmer extends PylonBlock
        implements PylonEntityHolderBlock, PylonTickingBlock, PylonFluidBufferBlock {

    private static final Map<Material, Material> CROPS = Map.of(
            Material.CARROT, Material.CARROTS,
            Material.POTATO, Material.POTATOES,
            Material.WHEAT_SEEDS, Material.WHEAT,
            Material.BEETROOT_SEEDS, Material.BEETROOTS,
            Material.PUMPKIN_SEEDS, Material.PUMPKIN_STEM,
            Material.MELON_SEEDS, Material.MELON_STEM,
            Material.NETHER_WART, Material.NETHER_WART
    );

    private static final Set<Material> IGNORE = EnumSet.of(Material.PUMPKIN_STEM, Material.MELON_STEM);
    private static final Set<Material> BREAK = EnumSet.of(Material.PUMPKIN, Material.MELON);

    private static final Config settings = Settings.get(BaseKeys.HYDRAULIC_FARMER);
    public static final int RADIUS = settings.getOrThrow("radius", ConfigAdapter.INT);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", ConfigAdapter.INT);
    public static final double HYDRAULIC_FLUID_USAGE = settings.getOrThrow("hydraulic-fluid-usage", ConfigAdapter.DOUBLE);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("radius", UnitFormat.BLOCKS.format(RADIUS)),
                    PylonArgument.of("tick-interval", UnitFormat.SECONDS.format(TICK_INTERVAL / 20.0)),
                    PylonArgument.of("hydraulic-fluid-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(HYDRAULIC_FLUID_USAGE))
            );
        }
    }

    @SuppressWarnings("unused")
    public HydraulicFarmer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setTickInterval(TICK_INTERVAL);

        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));

        int seconds = getTickInterval() / 20;
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_USAGE * seconds, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, HYDRAULIC_FLUID_USAGE * seconds, false, true);
    }

    @SuppressWarnings("unused")
    public HydraulicFarmer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }


    @Override
    public void tick(double deltaSeconds) {
        double hydraulicFluidUsed = HYDRAULIC_FLUID_USAGE * getTickInterval() / 20.0;

        if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidUsed
                || fluidCapacity(BaseFluids.DIRTY_HYDRAULIC_FLUID) < hydraulicFluidUsed
        ) {
            return;
        }

        var tiles = getFarmingTiles();
        // Attempt to break crops
        for (var tile : tiles) {
            Block cropBlock = tile.getCropBlock();
            Material cropType = cropBlock.getType();
            if (IGNORE.contains(cropType)) continue;

            if (CROPS.containsValue(cropType)
                    && cropBlock.getBlockData() instanceof Ageable ageable
                    && ageable.getAge() == ageable.getMaximumAge()
            ) {
                // maybe instead of breaking them and leaving them be, also put them in the chest above
                cropBlock.breakNaturally();
                removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidUsed);
                addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidUsed);
                return;
            } else if (BREAK.contains(cropType)) {
                cropBlock.breakNaturally();
                removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidUsed);
                addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidUsed);
                return;
            }
        }

        List<ItemStack> stacks = getItemsFromChest();
        EnumMap<FarmingTileType, ItemStack> tileToCrop = new EnumMap<>(FarmingTileType.class);

        for (ItemStack stack : stacks) {
            if (stack == null) continue;
            if (tileToCrop.size() == 2) break;

            Material type = stack.getType();
            if (CROPS.containsKey(type)) {
                FarmingTileType tileType = FarmingTileType.getTile(type);
                if (tileToCrop.containsKey(tileType)) continue;

                tileToCrop.put(tileType, stack);
            }
        }

        if (tileToCrop.isEmpty()) {
            return;
        }

        // Attempt plant crops
        for (var tile : tiles) {
            Block cropBlock = tile.getCropBlock();
            FarmingTileType type = tile.getType();

            if (cropBlock.isEmpty() && type != FarmingTileType.NONE) {
                ItemStack planted = tileToCrop.get(type);
                if (planted == null) {
                    continue;
                }

                cropBlock.setType(CROPS.get(planted.getType()));
                planted.subtract();
                removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidUsed);
                addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidUsed);
                return;
            }
        }
    }

    private enum FarmingTileType {
        SOUL_SAND,
        FARMLAND,
        NONE;

        private static final EnumMap<FarmingTileType, Set<Material>> ALLOWED_CROPS;

        static {
            ALLOWED_CROPS = new EnumMap<>(FarmingTileType.class);
            ALLOWED_CROPS.put(
                SOUL_SAND, EnumSet.of(Material.NETHER_WART)
            );

            ALLOWED_CROPS.put(
                FARMLAND, EnumSet.of(
                    Material.CARROT,
                    Material.POTATO,
                    Material.WHEAT_SEEDS,
                    Material.BEETROOT_SEEDS,
                    Material.PUMPKIN_SEEDS,
                    Material.MELON_SEEDS
                )
            );
        }

        private static FarmingTileType from(Material mat) {
            if (mat == Material.FARMLAND) return FARMLAND;
            if (mat == Material.SOUL_SAND) return SOUL_SAND;

            return NONE;
        }

        private static FarmingTileType getTile(Material mat) {
            for (var entry : ALLOWED_CROPS.entrySet()) {
                if (entry.getValue().contains(mat)) return entry.getKey();
            }

            return null;
        }
    }

    private class FarmingTile {
        @Getter
        private final Block cropBlock;
        private FarmingTileType type = null;

        private FarmingTile(Block cropBlock) {
            this.cropBlock = cropBlock;
        }

        // lazy access
        public FarmingTileType getType() {
            if (type == null) {
                Block farmlandBlock = cropBlock.getRelative(BlockFace.DOWN);
                this.type = FarmingTileType.from(farmlandBlock.getType());
            }

            return type;
        }
    }

    private List<FarmingTile> getFarmingTiles() {
        int diameter = 2 * RADIUS + 1;
        ArrayList<FarmingTile> tiles = new ArrayList<>(diameter * diameter);

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                Block cropBlock = getBlock().getRelative(x, 0, z);

                tiles.add(
                    new FarmingTile(
                        cropBlock
                    )
                );
            }
        }

        return tiles;
    }

    private List<ItemStack> getItemsFromChest() {
        BlockState stateAbove = getBlock().getRelative(0, 1, 0).getState();
        if (!(stateAbove instanceof BlockInventoryHolder blockInventoryHolder)) {
            return Collections.emptyList();
        }

        ArrayList<ItemStack> stacks = new ArrayList<>(54);
        InventoryHolder holder = blockInventoryHolder.getInventory().getHolder();
        if (holder == null) {
            return Collections.emptyList();
        }


        if (holder instanceof DoubleChest doubleChest) {
            InventoryHolder leftSide = doubleChest.getLeftSide();
            if (leftSide != null) {
                for (ItemStack item : leftSide.getInventory()) {
                    stacks.add(item);
                }
            }

            InventoryHolder rightSide = doubleChest.getRightSide();
            if (rightSide != null) {
                for (ItemStack item : rightSide.getInventory()) {
                    stacks.add(item);
                }
            }
        } else {
            for (ItemStack item : holder.getInventory()) {
                stacks.add(item);
            }
        }
        return stacks;
    }

    @Override
    public @Nullable BlockFace getFacing() {
        return PylonFluidBufferBlock.super.getFacing();
    }
}
