package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSponge;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * PowerfulSponge is an abstract base class for powerful sponges
 * which can absorb specific blocks/fluid in an area.
 *
 * @author balugaq
 * @see PylonSponge
 * @see PowerfulWaterSponge
 * @see LavaSponge
 * @see HotLavaSponge
 */
public abstract class PowerfulSponge extends PylonBlock implements PylonTickingBlock {
    public PowerfulSponge(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public PowerfulSponge(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    /**
     * Used to absorb blocks/fluid.
     *
     * @see PowerfulWaterSponge
     * @see LavaSponge
     * @see HotLavaSponge
     */
    public final void tick(double deltaSeconds) {
        spawnParticles();
        Location location = getBlock().getLocation();
        for (BlockFace blockFace : PylonUtils.IMMEDIATE_FACES) {
            if (handler().isAbsorbable(location.clone().add(blockFace.getDirection()).getBlock())) {
                // Found a nearby absorbable block
                onAbsorb(getBlock());
                return;
            }
        }
    }

    /**
     * When this method is called, the sponge must have found a nearby absorbable blocks/fluid.
     *
     * @param sponge the block itself
     */
    private void onAbsorb(@NotNull Block sponge) {
        List<Block> blocks = new ArrayList<>();

        World world = sponge.getWorld();
        WorldBorder border = world.getWorldBorder();
        int centerX = sponge.getX();
        int centerY = sponge.getY();
        int centerZ = sponge.getZ();

        for (int x = centerX - checkRange(); x <= centerX + checkRange(); x++) {
            int remainingX = checkRange() - Math.abs(x - centerX);
            if (remainingX < 0) continue;

            for (int y = centerY - remainingX; y <= centerY + remainingX; y++) {
                int remainingY = remainingX - Math.abs(y - centerY);
                if (remainingY < 0) continue;

                for (int z = centerZ - remainingY; z <= centerZ + remainingY; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!border.isInside(block.getLocation())) continue;
                    if (!handler().isAbsorbable(block)) continue;

                    blocks.add(block);
                }
            }
        }

        for (Block block : blocks) {
            handler().absorb(block);
        }

        updateSponge(sponge);
    }

    /**
     * Transforms the sponge block into its used state.
     *
     * @param sponge The block itself
     */
    @OverridingMethodsMustInvokeSuper
    public void updateSponge(@NotNull Block sponge) {
        BlockStorage.breakBlock(sponge, new BlockBreakContext.PluginBreak(sponge, false));
    }

    /**
     * @see HotLavaSponge
     */
    public void spawnParticles() {
    }

    /**
     * Handles the absorption of blocks.
     */
    public abstract @NotNull AbsorbHandler handler();

    /**
     * @return The Manhattan distance this sponge can absorb blocks/fluid within
     */
    public abstract int checkRange();

    /**
     * Build & Handles the absorption of blocks.
     * @author balugaq
     */
    public static class AbsorbHandler {
        private final Map<Predicate<Block>, Consumer<Block>> HANDLERS = new HashMap<>();
    
        /**
         * Builder function
         */
        @NotNull
        public PowerfulSponge.AbsorbHandler.Builder handle(@NotNull Consumer<Block> consumer) {
            return new Builder(this, consumer);
        }
    
        /**
         * Checks if a block can be absorbed by this sponge.
         *
         * @param block The block to check
         * @return true if the block can be absorbed, false otherwise
         */
        public boolean isAbsorbable(@NotNull Block block) {
            return HANDLERS.keySet().stream().anyMatch(predicate -> predicate.test(block));
        }
    
        /**
         * Absorbs a specific block (changes it to air or appropriate state).
         *
         * @param block The block to absorb
         */
        public void absorb(@NotNull Block block) {
            HANDLERS.entrySet().stream()
                    .filter(entry -> entry.getKey().test(block))
                    .findFirst()
                    .ifPresent(entry -> entry.getValue().accept(block));
        }

        /**
         * @author balugaq
         */
        @RequiredArgsConstructor
        public static class Builder {
            private final AbsorbHandler handler;
            private final Consumer<Block> consumer;
    
            /**
             * Builder function
             */
            @NotNull
            public PowerfulSponge.AbsorbHandler when(@NotNull Material @NotNull ... materials) {
                return when(block -> {
                    for (Material material : materials) {
                        if (block.getType() == material) {
                            return true;
                        }
                    }
                    return false;
                });
            }
    
            /**
             * Builder function
             */
            @NotNull
            public PowerfulSponge.AbsorbHandler when(@NotNull Predicate<Block> predicate) {
                handler.HANDLERS.put(predicate, consumer);
                return handler;
            }
        }
    }
}
