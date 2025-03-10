package io.github.pylonmc.pylon.base.items.basichealing;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import io.github.pylonmc.pylon.core.item.base.Consumable;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
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
import static io.github.pylonmc.pylon.base.PylonItems.FIBER;
import static io.github.pylonmc.pylon.base.PylonItems.DISINFECTANT;

public class Medkit extends PylonItemSchema {
    public static final float CONSUME_TIME = 7.0f; // in secs
    public static final float USE_COOLDOWN = 30.0f; // in secs

    public static final List<PotionEffect> CONSUME_EFFECTS = List.of(
            new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 2, true),
            new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 1, true),
            new PotionEffect(PotionEffectType.RESISTANCE, 10 * 20, 1, true));

    public Medkit(NamespacedKey id, Class<? extends SimplePylonItem> itemClass, ItemStack template){
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
}
