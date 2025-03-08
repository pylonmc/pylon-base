package io.github.pylonmc.pylon.base.items.basichealing;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static io.github.pylonmc.pylon.base.PylonItems.PLASTER;
import static io.github.pylonmc.pylon.base.PylonItems.FIBER;
import static io.github.pylonmc.pylon.base.PylonItems.DISINFECTANT;

public class Medkit extends PylonItemSchema {
    public static final double MEDKIT_HEAL_AMOUNT = 20;
    public Medkit(NamespacedKey id, Class<? extends PylonItem<? extends PylonItemSchema>> itemClass, ItemStack template){
        super(id, itemClass, template);
        ShapedRecipe craftingRecipe = new ShapedRecipe(id, template);
        craftingRecipe.shape(
                "PFP",
                "DDD",
                "PFP"
        );
        craftingRecipe.setIngredient('P', PLASTER.getItemStack());
        craftingRecipe.setIngredient('F', FIBER.getItemStack());
        craftingRecipe.setIngredient('D', DISINFECTANT.getItemStack());
        craftingRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(craftingRecipe);
    }

    public static class Item extends PylonItem<Medkit> implements BlockInteractor {
        public Item(Medkit schema, ItemStack itemStack) { super(schema, itemStack); }

        @Override
        public void onUsedToRightClickBlock(@NotNull PlayerInteractEvent event) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.getPlayer().heal(MEDKIT_HEAL_AMOUNT, EntityRegainHealthEvent.RegainReason.CUSTOM);
            event.getPlayer().getInventory().removeItem(Objects.requireNonNull(event.getItem()));
            event.getPlayer().updateInventory();
        }
    }
}
