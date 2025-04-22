package io.github.pylonmc.pylon.base.items;

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
import org.bukkit.inventory.CraftingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ExplosiveTarget {
    private ExplosiveTarget() { throw new AssertionError("Container class"); }

    public static class ExplosiveTargetItem extends PylonItem<ExplosiveTargetItem.Schema> implements BlockPlacer {

        @Override
        public @NotNull ExplosiveTargetBlock.Schema getBlockSchema() {
            return getSchema().blockSchema;
        }

        public static class Schema extends PylonItemSchema {
            private final ExplosiveTargetBlock.Schema blockSchema;
            public Schema(
                    @NotNull NamespacedKey key,
                    @NotNull Function<NamespacedKey, ItemStack> template,
                    @NotNull Function<ItemStack, CraftingRecipe> recipe,
                    ExplosiveTargetBlock.Schema blockSchema
                    ) {
                super(key, ExplosiveTargetItem.class, template);
                this.blockSchema = blockSchema;
                RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe.apply(this.template));
            }
        }

        public ExplosiveTargetItem(@NotNull Schema schema, @NotNull ItemStack itemStack) { super(schema, itemStack); }

        @Override
        public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
            return Map.of(
                    "explosion-power", Component.text(getSchema().blockSchema.explosivePower)
            );
        }
    }

    public static class ExplosiveTargetBlock extends PylonBlock<ExplosiveTargetBlock.Schema> implements PylonTargetBlock {

        public static class Schema extends PylonBlockSchema {
            public final double explosivePower = getSettings().getOrThrow("explosive-power", Double.class);
            public final boolean createsFire = getSettings().getOrThrow("creates-fire", Boolean.class);
            public Schema(NamespacedKey key,
                          Material material){
                super(key, material, ExplosiveTargetBlock.class);
            }
        }

        @SuppressWarnings("unused")
        public ExplosiveTargetBlock(ExplosiveTargetBlock.Schema schema, Block block, BlockCreateContext context) { super(schema, block); }

        @SuppressWarnings("unused")
        public ExplosiveTargetBlock(ExplosiveTargetBlock.Schema schema, Block block, PersistentDataContainer pdc) { super(schema, block); }

        @Override
        public void onHit(@NotNull TargetHitEvent event) {
            event.setCancelled(true);
            // Returns false if BlockExplodeEvent is cancelled
            if(!Objects.requireNonNull(event.getHitBlock()).getWorld().createExplosion(event.getHitBlock().getLocation(),
                    (float) getSchema().explosivePower,
                    getSchema().createsFire)){
                return;
            }
            BlockStorage.breakBlock(getBlock());
        }
    }
}
