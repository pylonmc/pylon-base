package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.item.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Weapon;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.tag.EntityTags;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class BeheadingSword extends PylonItemSchema {
    private static final ItemStack SILK_TOUCH_BOOK = new ItemStackBuilder(Material.ENCHANTED_BOOK)
            .set(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments()
                    .add(Enchantment.SILK_TOUCH, 1)
                    .build())
            .build();

    public BeheadingSword(NamespacedKey key, Class<? extends PylonItem<? extends BeheadingSword>> itemClass, ItemStack template){
        super(key, itemClass, template);
        ShapedRecipe recipe = new ShapedRecipe(key, template);
        recipe.shape(
                "MHM",
                "MBM",
                "MSM"
        );
        recipe.setIngredient('M', PylonItems.MAGIC_DUST.getItemStack());
        recipe.setIngredient('B', new RecipeChoice.ExactChoice(SILK_TOUCH_BOOK));
        recipe.setIngredient('S', Material.DIAMOND_SWORD);
        // Not allowing player heads to be used since too many plugins give player heads as custom items
        recipe.setIngredient('H', new RecipeChoice.MaterialChoice(Material.CREEPER_HEAD, Material.ZOMBIE_HEAD, Material.PIGLIN_HEAD, Material.DRAGON_HEAD));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static class Item extends PylonItem<BeheadingSword> implements Weapon {
        private static final Map<EntityType, ItemStack> entityHeadMap = Map.of(
                EntityType.CREEPER, new ItemStack(Material.CREEPER_HEAD),
                EntityType.PIGLIN, new ItemStack(Material.PIGLIN_HEAD),
                EntityType.ENDER_DRAGON, new ItemStack(Material.DRAGON_HEAD),
                EntityType.ZOMBIE, new ItemStack(Material.ZOMBIE_HEAD),
                EntityType.SKELETON, new ItemStack(Material.SKELETON_SKULL)
        );

        public Item(BeheadingSword schema, ItemStack itemStack) { super(schema, itemStack); }

        @Override
        public void onUsedToDamageEntity(@NotNull EntityDamageByEntityEvent event) {
            // Intentionally blank
        }

        @Override
        public void onUsedToKillEntity(@NotNull EntityDeathEvent event) {
            if(EntityTags.MINECARTS.isTagged(event.getEntityType())){
                return;
            }
            // This cast is safe because PylonItemListener only calls this listener when the killer is a player
            ItemStack head;
            if(event.getEntity().getType() == EntityType.PLAYER){
                Player killer = ((Player) event.getDamageSource().getCausingEntity());
                head = new ItemStackBuilder(Material.PLAYER_HEAD).build();
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(killer);
                head.setItemMeta(meta);
            }
            else {
                if(!entityHeadMap.containsKey(event.getEntityType())){
                    return;
                }
                head = entityHeadMap.get(event.getEntityType());
            }
            event.getDrops().add(head);
        }
    }
}
