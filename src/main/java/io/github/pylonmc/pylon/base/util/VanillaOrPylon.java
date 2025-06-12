package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jspecify.annotations.NullMarked;

import java.util.Objects;

@NullMarked
public sealed interface VanillaOrPylon {

    boolean matches(ItemStack stack);
    RecipeChoice asRecipeChoice();

    record Vanilla(ItemStack stack) implements VanillaOrPylon {

        public Vanilla(Material material) {
            this(new ItemStack(material));
        }

        @Override
        public boolean matches(ItemStack stack) {
            return this.stack.isSimilar(stack); // will fail for Pylon items since they have extra data components
        }

        @Override
        public RecipeChoice asRecipeChoice() {
            return new RecipeChoice.MaterialChoice(stack.getType());
        }
    }

    record Pylon(PylonItemSchema item) implements VanillaOrPylon {

        public Pylon(ItemStack item) {
            this(Objects.requireNonNull(PylonItem.fromStack(item)).getSchema());
        }

        @Override
        public boolean matches(ItemStack stack) {
            PylonItem pylonItem = PylonItem.fromStack(stack);
            return pylonItem != null && pylonItem.getSchema().getKey().equals(item.getKey());
        }

        @Override
        public RecipeChoice asRecipeChoice() {
            return new RecipeChoice.ExactChoice(item.getItemStack());
        }
    }
}
