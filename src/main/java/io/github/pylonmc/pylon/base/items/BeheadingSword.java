package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.item.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Weapon;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.CustomModelData;
import io.papermc.paper.tag.EntityTags;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class BeheadingSword extends PylonItemSchema {
    public BeheadingSword(NamespacedKey key, Class<? extends PylonItem<? extends BeheadingSword>> itemClass, ItemStack template){
        super(key, itemClass, template);
        // Add recipe here
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
        }
    }
}
