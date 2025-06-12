package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public sealed interface VanillaOrPylon {

    boolean matches(@NotNull ItemStack stack);
    RecipeChoice asRecipeChoice();

    record Vanilla(@NotNull ItemStack stack) implements VanillaOrPylon {

        public Vanilla(@NotNull Material material) {
            this(new ItemStack(material));
        }

        @Override
        public boolean matches(@NotNull ItemStack stack) {
            return this.stack.isSimilar(stack); // will fail for Pylon items since they have extra data components
        }

        @Override
        public RecipeChoice asRecipeChoice() {
            return new RecipeChoice.MaterialChoice(stack.getType());
        }
    }

    record Pylon(@NotNull PylonItemSchema item) implements VanillaOrPylon {

        public Pylon(@NotNull ItemStack item) {
            this(Objects.requireNonNull(PylonItem.fromStack(item)).getSchema());
        }

        @Override
        public boolean matches(@NotNull ItemStack stack) {
            PylonItem pylonItem = PylonItem.fromStack(stack);
            return pylonItem != null && pylonItem.getSchema().getKey().equals(item.getKey());
        }

        @Override
        public RecipeChoice asRecipeChoice() {
            return new RecipeChoice.ExactChoice(item.getItemStack());
        }
    }
}
