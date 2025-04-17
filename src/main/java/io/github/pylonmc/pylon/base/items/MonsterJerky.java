package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNullByDefault;

import java.util.function.Function;


@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
public class MonsterJerky extends PylonItemSchema {

    @Getter private final float cookingXp;

    public MonsterJerky(NamespacedKey key, Function<NamespacedKey, ItemStack> templateSupplier) {
        super(key, SimplePylonItem.class, templateSupplier);

        int nutrition = getSettings().getOrThrow("nutrition", Integer.class);
        float saturation = getSettings().getOrThrow("saturation", Double.class).floatValue();
        cookingXp = getSettings().getOrThrow("cooking.xp", Double.class).floatValue();
        template.setData(DataComponentTypes.FOOD, FoodProperties.food()
                .canAlwaysEat(false)
                .nutrition(nutrition)
                .saturation(saturation)
                .build());
    }
}
