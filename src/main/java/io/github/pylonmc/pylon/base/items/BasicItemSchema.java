package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

// TODO move to pylon-core
public class BasicItemSchema<R extends Keyed> extends PylonItemSchema {
    public BasicItemSchema(
            @NotNull NamespacedKey id,
            @NotNull ItemStack template,
            @NotNull RecipeType<R> recipeType,
            @NotNull Function<ItemStack, R> recipe
    ) {
        super(id, SimplePylonItem.class, template);
        recipeType.addRecipe(recipe.apply(template));
    }
}
