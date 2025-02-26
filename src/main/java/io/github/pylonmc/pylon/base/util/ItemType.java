package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

// TODO move to pylon-core
@NotNullByDefault
public sealed interface ItemType {

    boolean matches(ItemStack stack);

    record Vanilla(ItemStack template) implements ItemType {

        public Vanilla(Material material) {
            this(new ItemStack(material));
        }

        @Override
        public boolean matches(ItemStack stack) {
            return stack.isSimilar(template);
        }
    }

    record Pylon(NamespacedKey key) implements ItemType {
        @Override
        public boolean matches(ItemStack stack) {
            PylonItem<?> item = PylonItem.fromStack(stack);
            return item != null && item.getSchema().getKey().equals(key);
        }
    }
}
