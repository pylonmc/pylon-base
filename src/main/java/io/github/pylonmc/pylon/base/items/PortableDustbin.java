package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class PortableDustbin extends PylonItemSchema {
    public PortableDustbin(NamespacedKey id, Class<? extends PylonItem<? extends PortableDustbin>> itemClass, Function<NamespacedKey, ItemStack> template){
        super(id, itemClass, template);
    }

    @Override
    public void onRegister(@NotNull PylonRegistry<?> registry) {
        ShapedRecipe recipe = new ShapedRecipe(getKey(), getItemStack());
        recipe.shape(
                "CCC",
                "IAI",
                "III"
        );
        recipe.setIngredient('I', PylonItems.IRON_SHEET.getItemStack());
        recipe.setIngredient('C', Material.CACTUS);
        recipe.setIngredient('A', PylonItems.PORTABILITY_CATALYST.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static class Item extends PylonItem<PortableDustbin> implements Interactor{
        public Item(PortableDustbin schema, ItemStack itemStack){ super(schema, itemStack); }

        @Override
        public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
            var menu = MenuType.GENERIC_9X6.create(event.getPlayer(), Component.text("Portable Dustbin"));
            event.getPlayer().openInventory(menu);
        }
    }
}
