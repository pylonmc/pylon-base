package io.github.pylonmc.pylon.base.items.basichealing;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static io.github.pylonmc.pylon.base.PylonItems.PLASTER;

public class Splint extends PylonItemSchema {
    public static final float CONSUME_TIME = 3.0f; // in secs
    public static final List<PotionEffect> CONSUME_EFFECTS = Arrays.stream(new PotionEffect[]{
            new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 2, true)}).toList();

    public Splint(NamespacedKey id, Class<? extends SimplePylonItem> itemClass, ItemStack template){
        super(id, itemClass, template);
        ShapedRecipe craftingRecipe = new ShapedRecipe(id, template);
        craftingRecipe.shape(
                "PPP",
                "   ",
                "PPP"
        );
        craftingRecipe.setIngredient('P', PLASTER.getItemStack());
        craftingRecipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(craftingRecipe);
    }
}
