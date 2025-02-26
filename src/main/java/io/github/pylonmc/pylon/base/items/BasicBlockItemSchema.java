package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.SimplePylonBlock;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.function.Function;

// TODO move to pylon-core
@NotNullByDefault
public class BasicBlockItemSchema<R extends Keyed> extends PylonItemSchema {

    private final PylonBlockSchema blockSchema;

    public BasicBlockItemSchema(
            NamespacedKey id,
            ItemStack template,
            RecipeType<R> recipeType,
            Function<ItemStack, R> recipe
    ) {
        super(id, ItemInstance.class, template);
        blockSchema = new PylonBlockSchema(
                id,
                template.getType(),
                SimplePylonBlock.class
        );
        blockSchema.register();
        recipeType.addRecipe(recipe.apply(template));
    }

    public static class ItemInstance extends PylonItem<BasicBlockItemSchema<?>> implements PlaceableBlock {

        public ItemInstance(BasicBlockItemSchema<?> schema, ItemStack item) {
            super(schema, item);
        }

        @Override
        public PylonBlockSchema getBlockSchema() {
            return getSchema().blockSchema;
        }
    }
}
