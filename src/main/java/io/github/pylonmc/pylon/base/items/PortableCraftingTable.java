package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class PortableCraftingTable extends PylonItemSchema {
    public PortableCraftingTable(NamespacedKey id, Class<? extends PylonItem<? extends PylonItemSchema>> itemClass, Function<NamespacedKey, ItemStack> template){
        super(id, itemClass, template);
    }

    @Override
    public void onRegister(@NotNull PylonRegistry<?> registry) {
        ShapedRecipe recipe = new ShapedRecipe(getKey(), getItemStack());
        recipe.shape(
                "WWW",
                "WCW",
                "   "
        );
        recipe.setIngredient('W', PylonItems.COMPRESSED_WOOD.getItemStack());
        recipe.setIngredient('C', PylonItems.PORTABILITY_CATALYST.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static class Item extends PylonItem<PortableCraftingTable> implements Interactor {
        public Item(PortableCraftingTable schema, ItemStack itemStack) { super(schema, itemStack); }

        @Override
        public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
            event.getPlayer().openInventory(MenuType.CRAFTING.create(event.getPlayer()));
        }
    }
}
