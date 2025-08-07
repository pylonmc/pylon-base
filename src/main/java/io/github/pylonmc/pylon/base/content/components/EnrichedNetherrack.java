package io.github.pylonmc.pylon.base.content.components;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;


public final class EnrichedNetherrack extends PylonBlock implements PylonTickingBlock {

    public final int tickRate = getSettings().getOrThrow("tick-rate", Integer.class);

    @SuppressWarnings("unused")
    public EnrichedNetherrack(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public EnrichedNetherrack(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return tickRate;
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