package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonTargetBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.papermc.paper.event.block.TargetHitEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
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
            private final double explosionPower = getSettings().getOrThrow("explosion-power", Double.class);
            private final boolean createsFire = getSettings().getOrThrow("fire-enabled", Boolean.class);

            public Schema(
                    @NotNull NamespacedKey key,
                    @NotNull Class<? extends PylonItem<? extends PylonItemSchema>> itemClass,
                    @NotNull Function<NamespacedKey, ItemStack> template,
                    @NotNull Function<ItemStack, ShapedRecipe> recipe
                    ) {
                super(key, itemClass, template);
                RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe.apply(this.template));
            }
        }

        public ExplosiveTargetItem(@NotNull Schema schema, @NotNull ItemStack itemStack) { super(schema, itemStack); }

        @Override
        public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
            return Map.of("explosion-power", Component.text(getSchema().explosionPower),
                    "fire-enabled", getSchema().createsFire ? Component.text("Does") : Component.text("Doesn't"));
        }
    }

    public static class ExplosiveTargetBlock extends PylonBlock<ExplosiveTargetBlock.Schema> implements PylonTargetBlock {

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
            event.setCancelled(true);
            BlockStorage.breakBlock(getBlock());
            // This is called when a target block is hit, so this should never be null
            Objects.requireNonNull(event.getHitBlock()).getWorld().createExplosion(event.getHitBlock().getLocation(),
                    (float) PylonItems.EXPLOSIVE_TARGET.explosionPower,
                    PylonItems.EXPLOSIVE_TARGET.createsFire);
        }
    }
}
