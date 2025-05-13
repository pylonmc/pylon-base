package io.github.pylonmc.pylon.base.util.gui;

import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import org.bukkit.Material;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.impl.SimpleItem;

public final class DefaultItems {
    private DefaultItems() {
        throw new AssertionError("Utility class");
    }

    public static Item background() {
        return new SimpleItem(ItemStackBuilder.of(Material.GRAY_STAINED_GLASS_PANE).name(""));
    }

    public static Item scrollUp() {
        return new PylonScrollItem(-1, "up");
    }

    public static Item scrollDown() {
        return new PylonScrollItem(1, "down");
    }

    public static Item scrollLeft() {
        return new PylonScrollItem(-1, "left");
    }

    public static Item scrollRight() {
        return new PylonScrollItem(1, "right");
    }
}
