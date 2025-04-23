package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;


public final class EnrichedNetherrack {

    private static final int TICK_RATE = 5;

    private EnrichedNetherrack() {
        throw new AssertionError("Container class");
    }

    public static class EnrichedNetherrackBlock extends PylonBlock<PylonBlockSchema> implements PylonTickingBlock {

        public EnrichedNetherrackBlock(@NotNull PylonBlockSchema schema, @NotNull Block block) {
            super(schema, block);
        }

        @Override
        public int getCustomTickRate(int globalTickRate) {
            return TICK_RATE * globalTickRate;
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