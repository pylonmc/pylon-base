package io.github.pylonmc.pylon.base.items.research.pack;

import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class ResearchPack1 extends ResearchPack {

    public static final NamespacedKey KEY = pylonKey("research_pack_1");

    public static final int POINTS = getSettings(KEY).getOrThrow("points", Integer.class);

    public static final ItemStack ITEM_STACK = ItemStackBuilder.pylonItem(Material.BOOK, KEY)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();


    public ResearchPack1(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    @Override
    int getPoints() {
        return POINTS;
    }
}
