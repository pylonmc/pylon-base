package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.base.util.BlockUtils;
import io.github.pylonmc.pylon.base.util.RecipeUtils;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Tool;
import io.github.pylonmc.pylon.core.persistence.blockstorage.BlockStorage;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
            BreakAttachedWood(event.getBlock(), event.getPlayer(), event.getPlayer().getInventory().getItemInMainHand());
            event.setCancelled(true); // Stop vanilla logic
        }

        @Override
        public void onUsedToDamageBlock(@NotNull BlockDamageEvent event) {
            // Intentionally blank, have to implement
        }

        private void BreakAttachedWood(Block block, Player player, ItemStack tool){
            // Recursive function, for every adjacent block check if it's a log, if so delete it and give the drop to the player and check all its adjacent blocks
            if(!Tag.LOGS.isTagged(block.getType()) || BlockStorage.isPylonBlock(new BlockPosition(block))){
                return;
            }
            BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
            if (!blockBreakEvent.callEvent()) {
                return;
            }
            if(blockBreakEvent.isDropItems()) {
                ArrayList<org.bukkit.entity.Item> itemsDropped = new ArrayList<>();
                for (ItemStack itemStack : block.getDrops(tool)) {
                    org.bukkit.entity.Item item = block.getWorld().dropItem(block.getLocation(), itemStack);
                    itemsDropped.add(item);
                }
                new BlockDropItemEvent(block, block.getState(), player, itemsDropped).callEvent();
            }
            block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getBlockData());
            block.getWorld().spawnParticle(Particle.BLOCK, block.getLocation(), 5);
            block.setType(Material.AIR);
            new PlayerItemDamageEvent(player, tool, 1, 1).callEvent();
            for (BlockFace face : BlockUtils.IMMEDIATE_FACES_WITH_DIAGONALS) {
                BreakAttachedWood(block.getRelative(face), player, tool);
            }
        }
    }
}
