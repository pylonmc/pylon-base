package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class MonsterJerky extends PylonItem {

    public static final NamespacedKey KEY = pylonKey("monster_jerky");

    public static final int NUTRITION = getSettings(KEY).getOrThrow("nutrition", Integer.class);
    public static final float SATURATION = getSettings(KEY).getOrThrow("saturation", Double.class).floatValue();
    public static final float COOKING_XP = getSettings(KEY).getOrThrow("cooking.xp", Double.class).floatValue();

    public static final ItemStack ITEM_STACK = ItemStackBuilder.pylonItem(Material.ROTTEN_FLESH, KEY)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable().build())
            .set(DataComponentTypes.FOOD, FoodProperties.food()
                    .canAlwaysEat(false)
                    .nutrition(NUTRITION)
                    .saturation(SATURATION)
                    .build()
            )
            .build();

    public MonsterJerky(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }
}
