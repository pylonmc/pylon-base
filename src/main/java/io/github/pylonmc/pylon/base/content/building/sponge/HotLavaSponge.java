package io.github.pylonmc.pylon.base.content.building.sponge;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * HotLavaSponge is a powerful sponge that can absorb lava but has special behavior.
 * <p>
 * When placed in water, it may:
 * <ul>
 *   <li>turn into obsidian/li>
 *   <li>turn back into a {@link LavaSponge}</li>
 * </ul>
 * </p>
 *
 * @author balugaq
 * @see PowerfulSponge
 * @see LavaSponge
 */
public class HotLavaSponge extends PowerfulSponge {
    private static final Config settings = Settings.get(BaseKeys.HOT_LAVA_SPONGE);
    private static final int CHECK_RANGE = settings.getOrThrow("check-range", ConfigAdapter.INT);
    private static final double REUSE_RATE = settings.getOrThrow("reuse-rate", ConfigAdapter.DOUBLE);
    private final Location particleDisplayLoc = getBlock().getLocation().clone().add(0.5, 0.5, 0.5);

    public HotLavaSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public HotLavaSponge(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public boolean isAbsorbable(@NotNull Block block) {
        Material type = block.getType();
        return type == Material.WATER
                || block.getBlockData() instanceof Waterlogged
                || type == Material.WATER_CAULDRON
                || type == Material.SEAGRASS
                || type == Material.TALL_SEAGRASS
                || type == Material.KELP_PLANT
                || type == Material.KELP;
    }

    @Override
    public void absorb(@NotNull Block block) {
        Material type = block.getType();
        if (type == Material.WATER) {
            block.setType(Material.AIR);
        } else if (block.getBlockData() instanceof Waterlogged w) {
            w.setWaterlogged(false);
            block.setBlockData(w);
        } else if (type == Material.WATER_CAULDRON) {
            block.setType(Material.CAULDRON);
        } else if (type == Material.SEAGRASS || type == Material.TALL_SEAGRASS) {
            block.setType(Material.AIR);
        } else if (type == Material.KELP_PLANT || type == Material.KELP) {
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.KELP));
            block.setType(Material.AIR);
        }
    }

    public void tick(double deltaSeconds) {
        new ParticleBuilder(Particle.FLAME)
                .location(particleDisplayLoc)
                .count(3)
                .spawn();

        tryAbsorbNearbyBlocks();
    }

    @Override
    public int getRange() {
        return CHECK_RANGE;
    }

    @Override
    public void toWetSponge(@NotNull Block sponge) {
        BlockStorage.breakBlock(sponge, new BlockBreakContext.PluginBreak(sponge, false));
        if (ThreadLocalRandom.current().nextDouble() > REUSE_RATE) {
            // 90% chance of becoming unusable obsidian
            sponge.setType(Material.OBSIDIAN);
            new ParticleBuilder(Particle.FLAME)
                    .location(particleDisplayLoc)
                    .count(20)
                    .spawn();
            new ParticleBuilder(Particle.SMOKE)
                    .location(particleDisplayLoc)
                    .count(50)
                    .spawn();
        } else {
            // 10% chance of reusing the sponge
            BlockStorage.placeBlock(sponge, BaseKeys.LAVA_SPONGE);
        }
    }

    /**
     * @author balugaq
     */
    public static class Item extends PylonItem {
        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("check-range", UnitFormat.BLOCKS.format(CHECK_RANGE).decimalPlaces(1)),
                    PylonArgument.of("reuse-rate", UnitFormat.PERCENT.format(REUSE_RATE * 100).decimalPlaces(1))
            );
        }
    }
}