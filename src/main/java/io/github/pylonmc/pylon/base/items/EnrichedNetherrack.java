package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.core.block.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.Ticking;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;


public final class EnrichedNetherrack {

    private static final int TICK_RATE = 5;

    private EnrichedNetherrack() {
        throw new AssertionError("Container class");
    }

    public static class EnrichedNetherrackItem extends PylonItem<PylonItemSchema> implements BlockPlacer {

        public EnrichedNetherrackItem(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return PylonBlocks.ENRICHED_NETHERRACK;
        }
    }

    public static class EnrichedNetherrackBlock extends PylonBlock<PylonBlockSchema> implements Ticking {

        public EnrichedNetherrackBlock(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
            super(schema, block);
        }

        public EnrichedNetherrackBlock(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
            super(schema, block);
        }

        @Override
        public int getCustomTickRate(int globalTickRate) {
            return TICK_RATE;
        }

        @Override
        public void tick(double deltaSeconds) {
            if (getBlock().getRelative(BlockFace.UP).getType() == Material.FIRE) {
                new ParticleBuilder(Particle.LAVA)
                        .location(getBlock().getLocation().toCenterLocation())
                        .count(3)
                        .spawn();
            }
        }
    }
}
