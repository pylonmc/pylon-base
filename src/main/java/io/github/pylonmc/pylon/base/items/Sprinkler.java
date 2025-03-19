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
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;


public interface Sprinkler {

    int RANGE = 4;

    class SprinklerItem extends PylonItem<SprinklerItem.Schema> implements BlockPlacer {

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return PylonBlocks.SPRINKLER;
        }

        public static class Schema extends PylonItemSchema {

            public Schema(
                    @NotNull NamespacedKey key,
                    @NotNull Class<? extends PylonItem<? extends PylonItemSchema>> itemClass,
                    @NotNull ItemStack template
            ) {
                super(key, itemClass, template);
            }
        }

        public SprinklerItem(@NotNull Schema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }
    }

    class SprinklerBlock extends PylonBlock<SprinklerBlock.Schema> implements Ticking {

        public static class Schema extends PylonBlockSchema {

            public Schema(
                    @NotNull NamespacedKey key,
                    @NotNull Material material,
                    @NotNull Class<? extends PylonBlock<?>> blockClass
            ) {
                super(key, material, blockClass);
            }
        }

        public SprinklerBlock(Schema schema, Block block, BlockCreateContext context) {
            super(schema, block);
        }

        public SprinklerBlock(Schema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);
        }

        @Override
        public int getCustomTickRate(int globalTickRate) {
            return 5;
        }

        @Override
        public void tick(double deltaSeconds) {
            if (getBlock().getRelative(BlockFace.DOWN).getType() != Material.WATER) {
                return;
            }

            WateringCan.water(getBlock(), RANGE);

            new ParticleBuilder(Particle.SPLASH)
                    .count(5)
                    .location(getBlock().getLocation().add(0.5, 0.5, 0.5))
                    .spawn();
        }
    }
}
