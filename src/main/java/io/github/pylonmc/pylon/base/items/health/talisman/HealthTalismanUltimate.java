package io.github.pylonmc.pylon.base.items.health.talisman;

import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class HealthTalismanUltimate extends HealthTalisman {

    public static final NamespacedKey KEY = pylonKey("health_talisman_ultimate");

    public static final int MAX_HEALTH_BOOST = getSettings(KEY).getOrThrow("max-health-boost", Integer.class);

    public static final ItemStack ITEM_STACK = ItemStackBuilder.pylonItem(Material.BUDDING_AMETHYST, KEY)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();

    public HealthTalismanUltimate(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    @Override
    int getMaxHealthBoost() {
        return MAX_HEALTH_BOOST;
    }
}
