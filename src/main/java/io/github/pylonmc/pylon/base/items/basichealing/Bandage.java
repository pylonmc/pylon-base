package io.github.pylonmc.pylon.base.items.basichealing;

import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.item.base.Bow;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static io.github.pylonmc.pylon.base.PylonItems.FIBER;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class Bandage extends PylonItemSchema {
    public static final float CONSUME_TIME = 1.25f; // in seconds
    public static final List<PotionEffect> CONSUME_EFFECTS = Arrays.stream(new PotionEffect[]{
            new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1, true)}).toList();

    public Bandage(NamespacedKey id, Class<? extends SimplePylonItem> itemClass, ItemStack template){
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
}
