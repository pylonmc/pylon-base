package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.util.MiningLevel;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Hammer extends PylonItemSchema {

    private final MiningLevel miningLevel;

    public Hammer(
            @NotNull NamespacedKey id,
            @NotNull Class<? extends PylonItem<? extends PylonItemSchema>> itemClass,
            @NotNull ItemStack template,
            @NotNull MiningLevel miningLevel
    ) {
        super(id, itemClass, template);
        this.miningLevel = miningLevel;
    }

    public static class Item extends PylonItem<Hammer> implements BlockInteractor {
        public Item(@NotNull Hammer schema, @NotNull ItemStack itemStack) {
            super(schema, itemStack);
        }

        @Override
        public void onUsedToRightClickBlock(@NotNull PlayerInteractEvent event) {

        }
    }
}
