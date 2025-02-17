package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.Hammer;
import io.github.pylonmc.pylon.base.util.MiningLevel;
import io.github.pylonmc.pylon.core.item.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class PylonItems {

    private PylonItems() {
        throw new AssertionError("Utility class");
    }

    public static final PylonItemSchema STONE_HAMMER = new Hammer(
            pylonKey("stone_hammer"),
            Hammer.Item.class,
            new ItemStackBuilder(Material.STONE_PICKAXE)
                    .set(DataComponentTypes.ITEM_NAME, Component.text("Stone Hammer"))
                    .set(DataComponentTypes.LORE, ItemLore.lore()
                            .addLine(Component.text("A hammer made of stone"))
                            .addLine(Component.text("Level: stone").color(NamedTextColor.YELLOW))
                    )
                    .build(),
            MiningLevel.STONE
    );

    private static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }
}
