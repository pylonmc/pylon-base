package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PowerfulWaterSponge extends PowerfulSponge {
    public final int CHECK_RANGE = getSettings().getOrThrow("check_range", Integer.class);

    public PowerfulWaterSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @Override
    public boolean isAbsorbable(@NotNull Block block) {
        return block.getType() == Material.WATER
                || block.getBlockData() instanceof Waterlogged
                || block.getType() == Material.WATER_CAULDRON;
    }

    @Override
    public void absorb(@NotNull Block block) {
        if (block.getType() == Material.WATER) {
            block.setType(Material.AIR);
        } else if (block.getBlockData() instanceof Waterlogged w) {
            w.setWaterlogged(false);
            block.setBlockData(w);
        } else if (block.getType() == Material.WATER_CAULDRON) {
            block.setType(Material.CAULDRON);
        }
    }

    @Override
    public int getRange() {
        return CHECK_RANGE;
    }

    public void toWetSponge(@NotNull Block sponge) {
        BlockStorage.breakBlock(sponge);
        BlockStorage.placeBlock(sponge, BaseKeys.WET_POWERFUL_WATER_SPONGE);
    }

    public static class Item extends PylonItem {

        public final int CHECK_RANGE = getSettings().getOrThrow("check_range", Integer.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("check_range", UnitFormat.BLOCKS.format(CHECK_RANGE).decimalPlaces(1))
            );
        }
    }
}
