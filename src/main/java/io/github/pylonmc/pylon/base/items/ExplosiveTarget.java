package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.core.block.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonTargetBlock;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import io.papermc.paper.event.block.TargetHitEvent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class ExplosiveTarget {
    private ExplosiveTarget() { throw new AssertionError("Container class"); }

    public static class ExplosiveTargetItem extends PylonItem<ExplosiveTargetItem.Schema> implements BlockPlacer {

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return PylonBlocks.EXPLOSIVE_TARGET;
        }

        public static class Schema extends PylonItemSchema {

            public Schema(
                    @NotNull NamespacedKey key,
                    @NotNull Class<? extends PylonItem<? extends PylonItemSchema>> itemClass,
                    @NotNull Function<NamespacedKey, ItemStack> template
                    ) {
                super(key, itemClass, template);
            }
        }

        public ExplosiveTargetItem(@NotNull Schema schema, @NotNull ItemStack itemStack) { super(schema, itemStack); }
    }

    public static class ExplosiveTargetBlock extends PylonBlock<ExplosiveTargetBlock.Schema> implements PylonTargetBlock {
        private static final float EXPLOSION_POWER = 15.0f;
        private static final boolean EXPLOSION_HAS_FIRE = true;

        public static class Schema extends PylonBlockSchema {
            public Schema(
                    @NotNull NamespacedKey key,
                    @NotNull Material material,
                    @NotNull Class<? extends PylonBlock<?>> blockClass
            ) {
                super(key, material, blockClass);
            }
        }

        @SuppressWarnings("unused")
        public ExplosiveTargetBlock(Schema schema, Block block, BlockCreateContext context) { super(schema, block); }

        @SuppressWarnings("unused")
        public ExplosiveTargetBlock(Schema schema, Block block, PersistentDataContainer pdc) { super(schema, block); }

        @Override
        public void onHit(@NotNull TargetHitEvent event) {
            // This is called when a target block is hit, so this should never be null
            Objects.requireNonNull(event.getHitBlock()).getWorld().createExplosion(event.getHitBlock().getLocation(), EXPLOSION_POWER, EXPLOSION_HAS_FIRE);
        }
    }
}
