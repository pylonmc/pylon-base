package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import io.github.pylonmc.pylon.base.util.RecipeUtils;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Arrow;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class RecoilArrow extends PylonItemSchema {
    public final double efficiency;

    public RecoilArrow(NamespacedKey key, Class<? extends PylonItem<? extends RecoilArrow>> itemClass, Function<NamespacedKey, ItemStack> template, double efficiency){
        super(key, itemClass, template);
        this.efficiency = efficiency;
    }

    @Override
    public void onRegister(@NotNull PylonRegistry<?> registry) {
        ShapedRecipe recipe = new ShapedRecipe(getKey(), getItemStack());
        recipe.shape(
                "SSS",
                "SAS",
                "SSS"
        );
        recipe.setIngredient('S', Material.SLIME_BALL);
        recipe.setIngredient('A', Material.ARROW);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(RecipeUtils.reflect(recipe));
    }

    public static class Item extends PylonItem<RecoilArrow> implements Arrow {
        private RecoilArrow schema;
        public Item(RecoilArrow schema, ItemStack itemStack) {
            super(schema, itemStack);
            this.schema = schema;
        }

        @Override
        public void onArrowShotFromBow(@NotNull EntityShootBowEvent event) {
            event.getEntity().setVelocity(event.getEntity().getVelocity().add(event.getProjectile().getVelocity().multiply(-1 * schema.efficiency)));
        }

        @Override
        public void onArrowReady(@NotNull PlayerReadyArrowEvent event) {
            // Intentionally blank
        }
    }
}