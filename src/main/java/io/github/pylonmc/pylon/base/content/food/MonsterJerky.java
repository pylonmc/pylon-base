package io.github.pylonmc.pylon.base.content.food;

import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;

import static io.github.pylonmc.pylon.base.util.BaseUtils.pylonKey;


@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class MonsterJerky extends PylonItem {

    public static final NamespacedKey KEY = pylonKey("monster_jerky");

    public static final int NUTRITION = Settings.get(KEY).getOrThrow("nutrition", Integer.class);
    public static final float SATURATION = Settings.get(KEY).getOrThrow("saturation", Double.class).floatValue();
    public static final float COOKING_XP = Settings.get(KEY).getOrThrow("cooking.xp", Double.class).floatValue();

    public static final ItemStack STACK = ItemStackBuilder.pylonItem(Material.ROTTEN_FLESH, KEY)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable().build())
            .set(DataComponentTypes.FOOD, FoodProperties.food()
                    .canAlwaysEat(false)
                    .nutrition(NUTRITION)
                    .saturation(SATURATION)
                    .build()
            )
            .build();

    public MonsterJerky(@NotNull ItemStack stack) {
        super(stack);
    }
}
