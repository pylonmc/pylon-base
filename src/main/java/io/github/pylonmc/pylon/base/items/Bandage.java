package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import io.github.pylonmc.pylon.core.item.base.Consumable;
import io.github.pylonmc.pylon.core.item.base.EntityInteractor;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Objects;

import static io.github.pylonmc.pylon.base.PylonItems.FIBER;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class Bandage extends PylonItemSchema {
    public static final double BANDAGE_HEAL_AMOUNT = 3;
    public static final float COOLDOWN = 15;
    public Bandage(NamespacedKey id, Class<? extends PylonItem<? extends PylonItemSchema>> itemClass, ItemStack template){
        super(id, itemClass, template);
        ShapedRecipe craftingRecipe = new ShapedRecipe(id, template);
        craftingRecipe.shape(
                "FF ",
                "FF ",
                "   "
        );
        craftingRecipe.setIngredient('F', FIBER.getItemStack());
        craftingRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(craftingRecipe);
    }

    public static class Item extends PylonItem<Bandage> implements BlockInteractor {
        public Item(Bandage schema, ItemStack itemStack) { super(schema, itemStack); }

        @Override
        public void onUsedToRightClickBlock(@NotNull PlayerInteractEvent event) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.getPlayer().heal(BANDAGE_HEAL_AMOUNT, EntityRegainHealthEvent.RegainReason.CUSTOM);
            event.getPlayer().getInventory().removeItem(Objects.requireNonNull(event.getItem()));
            event.getPlayer().updateInventory();
        }
    }
}
