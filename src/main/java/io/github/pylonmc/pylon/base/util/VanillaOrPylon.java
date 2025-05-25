package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed interface VanillaOrPylon {

    boolean matches(ItemStack stack);

    record Vanilla(ItemStack stack) implements VanillaOrPylon {

        public Vanilla(Material material) {
            this(new ItemStack(material));
        }

        @Override
        public boolean matches(ItemStack stack) {
            return this.stack.isSimilar(stack); // will fail for Pylon items since they have extra data components
        }
    }

    record Pylon(PylonItemSchema item) implements VanillaOrPylon {
        @Override
        public boolean matches(ItemStack stack) {
            PylonItem pylonItem = PylonItem.fromStack(stack);
            return pylonItem != null && pylonItem.getSchema().getKey().equals(item.getKey());
        }
    }
}
