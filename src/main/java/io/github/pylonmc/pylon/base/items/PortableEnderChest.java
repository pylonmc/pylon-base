package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

public class PortableEnderChest extends PylonItemSchema {
    public PortableEnderChest(NamespacedKey id, Class<? extends PylonItem<? extends PortableEnderChest>> itemClass, ItemStack template){
        super(id, itemClass, template);
        ShapedRecipe recipe = new ShapedRecipe(id, template);
        recipe.shape(
                "OOO",
                "OEO",
                "OOO"
        );
        recipe.setIngredient('O', PylonItems.COMPRESSED_OBSIDIAN.getItemStack());
        recipe.setIngredient('E', Material.ENDER_EYE);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static class Item extends PylonItem<PortableEnderChest> implements Interactor {
        public Item(PortableEnderChest schema, ItemStack itemStack) { super(schema, itemStack); }

        @Override
        public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
            event.getPlayer().openInventory(event.getPlayer().getEnderChest());
        }
    }
}
