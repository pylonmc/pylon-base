package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class WetPowerfulLavaSponge extends PowerfulSponge {
    public static final Random random = new Random();
    public final int CHECK_RANGE = getSettings().getOrThrow("check_range", Integer.class);

    public WetPowerfulLavaSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
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
        if (random.nextDouble() > 0.1) {
            sponge.setType(Material.OBSIDIAN);
            Location explodeLoc = sponge.getLocation().add(0.5, 0.5, 0.5);
            BaseUtils.spawnParticle(Particle.FLAME, explodeLoc, 20);
            BaseUtils.spawnParticle(Particle.SMOKE, explodeLoc, 50);
        } else {
            BlockStorage.placeBlock(sponge, BaseKeys.POWERFUL_LAVA_SPONGE);
        }
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
