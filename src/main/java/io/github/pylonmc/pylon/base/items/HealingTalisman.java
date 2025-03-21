package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class HealingTalisman extends PylonItemSchema {
    public int healthAmount;
    public HealingTalisman(NamespacedKey id, Class<? extends PylonItem<? extends HealingTalisman>> itemClass, ItemStack template, int healthAmount){
        super(id, itemClass, template);
        this.healthAmount = healthAmount;
    }

    public static class Item extends PylonItem<HealingTalisman> implements InventoryItem {
        HealingTalisman schema;
        public Item(HealingTalisman schema, ItemStack itemStack) {
            super(schema, itemStack);
            this.schema = schema;
        }

        public override void onEnterInventory(HumanEntity player){
            player.setMaxHealth(player.getMaxHealth() + schema.healthAmount);
        }

        public override void onExitInventory(HumanEntity player){
            player.setMaxHealth(player.getMaxHealth() - schema.healthAmount);
        }
    }
}
