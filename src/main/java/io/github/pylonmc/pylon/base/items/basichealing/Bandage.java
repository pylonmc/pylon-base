package io.github.pylonmc.pylon.base.items.basichealing;

import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.items.NoArrowBows;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import io.github.pylonmc.pylon.core.item.base.Bow;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
    public static final double BANDAGE_HEAL_AMOUNT = 6;
    public static final double ATTACK_SPEED = 3;
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

    public static class Item extends PylonItem<Bandage> implements Bow {
        public Item(Bandage schema, ItemStack itemStack) { super(schema, itemStack); }

        @Override
        public void onBowReady(@NotNull PlayerReadyArrowEvent event) {
            event.setCancelled(true);
            event.getPlayer().heal(BANDAGE_HEAL_AMOUNT, EntityRegainHealthEvent.RegainReason.CUSTOM);
            event.getPlayer().getInventory().removeItem(Objects.requireNonNull(event.getBow()));
            event.getPlayer().getInventory().removeItem(Objects.requireNonNull(event.getArrow()));
            event.getPlayer().updateInventory();
            PylonBase.getNoArrowBowsMechanics().returnItem(event.getPlayer());
        }

        @Override
        public void onBowFired(@NotNull EntityShootBowEvent event) {
            event.setCancelled(true);
            event.getProjectile().remove();
        }
    }
}
