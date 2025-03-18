package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class EnchantBooster extends PylonItemSchema {

    public EnchantBooster(NamespacedKey id, Class<? extends PylonItem<? extends EnchantBooster>> itemClass, ItemStack template){
        super(id, itemClass, template);
    }

    public static class Item extends PylonItem<EnchantBooster> implements Interactor {
        public Item(EnchantBooster schema, ItemStack itemStack) {
            super(schema, itemStack);
        }

        private void PrintInfoMsg(Player player){
            player.sendMessage(Component.text("Right-Click with the enchanted book to boost in your off-hand"));
        }

        @Override
        public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
            if(!event.getPlayer().getInventory().getItemInOffHand().isEmpty()){
                var item = event.getPlayer().getInventory().getItemInOffHand();
                if(item.isEmpty() || item.getType() != Material.ENCHANTED_BOOK){
                    PrintInfoMsg(event.getPlayer());
                    return;
                }
                EnchantmentStorageMeta bookmeta = (EnchantmentStorageMeta)item.getItemMeta();
                if(bookmeta.getStoredEnchants().size() != 1){
                    event.getPlayer().sendMessage(Component.text("Only one enchantment can be present on the enchanted book."));
                    return;
                }
                var enchant = bookmeta.getStoredEnchants().entrySet().iterator().next().getKey();
                bookmeta.removeStoredEnchant(enchant);
                bookmeta.addStoredEnchant(enchant, bookmeta.getEnchantLevel(enchant) + 1, true);
                item.setItemMeta(bookmeta);
                event.getPlayer().getInventory().setItemInOffHand(item);
                event.getPlayer().getInventory().removeItem(Objects.requireNonNull(event.getItem()));
            }
            else{
                PrintInfoMsg(event.getPlayer());
                return;
            }
        }
    }
}
