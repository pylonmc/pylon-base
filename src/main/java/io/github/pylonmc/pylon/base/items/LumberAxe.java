package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.base.util.RecipeUtils;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Tool;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

public class LumberAxe extends PylonItemSchema {
    public LumberAxe(NamespacedKey key, Class<? extends PylonItem<? extends LumberAxe>> itemClass, ItemStack template){
        super(key, itemClass, template);
        ShapedRecipe recipe = new ShapedRecipe(key, template);
        recipe.shape(
                "WW ",
                "WS ",
                " S "
        );
        recipe.setIngredient('W', PylonItems.COMPRESSED_WOOD.getItemStack());
        recipe.setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(RecipeUtils.reflect(recipe));
    }

    public static class Item extends PylonItem<LumberAxe> implements Tool {
        public Item(LumberAxe schema, ItemStack itemStack) { super(schema, itemStack); }

        @Override
        public void onUsedToBreakBlock(@NotNull BlockBreakEvent event) {
            BreakAttachedWood(event.getBlock(), event.getPlayer());
            event.setCancelled(true);
        }

        @Override
        public void onUsedToDamageBlock(@NotNull BlockDamageEvent event) {

        }

        private void BreakAttachedWood(Block block, Player player){
            // Recursive function, for every adjacent block check if it's a log, if so delete it and give the drop to the player and check all its adjacent blocks
            if(block.getType() == Material.OAK_LOG) {
                player.give(block.getDrops(getStack()));
                block.setType(Material.AIR);
                for(BlockFace face : BlockFace.values()){
                    BreakAttachedWood(block.getRelative(face), player);
                }
            }
        }
    }
}
