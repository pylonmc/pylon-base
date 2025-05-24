package io.github.pylonmc.pylon.base.items.health.talisman;

import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class HealthTalismanSimple extends HealthTalisman {

    public static final NamespacedKey KEY = pylonKey("health_talisman_simple");

    public static final int MAX_HEALTH_BOOST = getSettings(KEY).getOrThrow("max-health-boost", Integer.class);

    public static final ItemStack ITEM_STACK = ItemStackBuilder.defaultBuilder(Material.AMETHYST_SHARD, KEY)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();

    public HealthTalismanSimple(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    @Override
    int getMaxHealthBoost() {
        return MAX_HEALTH_BOOST;
    }
}
