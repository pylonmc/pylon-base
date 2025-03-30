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
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

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
            Player killer = ((Player) event.getDamageSource().getCausingEntity());
            if(event.getEntity().getType() == EntityType.PLAYER){
                ItemStack head = new ItemStackBuilder(Material.PLAYER_HEAD).build();
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                meta.setOwningPlayer(killer);
                head.setItemMeta(meta);
                killer.give(head);
            }
            else {
                ItemStack head;
                switch(event.getEntityType()){
                    case EntityType.CREEPER:
                        head = new ItemStackBuilder(Material.CREEPER_HEAD).build();
                        break;
                    case EntityType.ENDER_DRAGON:
                        head = new ItemStackBuilder(Material.DRAGON_HEAD).build();
                        break;
                    case EntityType.PIGLIN:
                        head = new ItemStackBuilder(Material.PIGLIN_HEAD).build();
                        break;
                    case EntityType.ZOMBIE:
                        head = new ItemStackBuilder(Material.ZOMBIE_HEAD).build();
                        break;
                    default:
                        return;
                }
                killer.give(head);
            }
        }
    }
}
