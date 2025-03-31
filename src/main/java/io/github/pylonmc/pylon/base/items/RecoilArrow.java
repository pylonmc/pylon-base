package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.base.util.RecipeUtils;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Arrow;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

public class RecoilArrow extends PylonItemSchema {
    public double efficiency;

    public RecoilArrow(NamespacedKey key, Class<? extends PylonItem<? extends RecoilArrow>> itemClass, ItemStack template, double efficiency){
        super(key, itemClass, template);
        ShapedRecipe recipe = new ShapedRecipe(key, template);
        recipe.shape(
                "   ",
                "FIC",
                "   "
        );
        recipe.setIngredient('F', Material.FLINT);
        recipe.setIngredient('I', PylonItems.FIBER.getItemStack());
        recipe.setIngredient('C', Material.FEATHER);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(RecipeUtils.reflect(recipe));
        this.efficiency = efficiency;
    }

    public static class Item extends PylonItem<RecoilArrow> implements Arrow {
        private RecoilArrow schema;
        public Item(RecoilArrow schema, ItemStack itemStack) {
            super(schema, itemStack);
            this.schema = schema;
        }

        @Override
        public void onArrowShotFromBow(@NotNull EntityShootBowEvent event) {
            event.getEntity().setVelocity(event.getProjectile().getVelocity().multiply(-1 * schema.efficiency));
        }

        @Override
        public void onArrowReady(@NotNull PlayerReadyArrowEvent event) {
            // Intentionally blank
        }
    }
}