package io.github.pylonmc.pylon.base.items.multiblocks;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;


public final class EnrichedNetherrack {

    public static final class Schema extends PylonBlockSchema {

        @Getter private final int tickRate = getSettings().getOrThrow("tick-rate", Integer.class);

        public Schema(NamespacedKey key, Material material, Class<? extends PylonBlock<?>> blockClass) {
            super(key, material, blockClass);
        }
    }

    private EnrichedNetherrack() {
        throw new AssertionError("Container class");
    }

    public static class EnrichedNetherrackBlock extends PylonBlock<EnrichedNetherrack.Schema> implements PylonTickingBlock {

        public EnrichedNetherrackBlock(@NotNull EnrichedNetherrack.Schema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
            super(schema, block);
        }

        public EnrichedNetherrackBlock(@NotNull EnrichedNetherrack.Schema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
            super(schema, block);
        }

        @Override
        public int getCustomTickRate(int globalTickRate) {
            return getSchema().tickRate;
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