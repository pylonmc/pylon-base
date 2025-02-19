package io.github.pylonmc.pylon.base.util;

import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.EnumSet;

@SuppressWarnings("UnstableApiUsage")
public class Components {

    private Components() {
        throw new AssertionError("Utility class");
    }

    /**
     * Makes a component that is a line of lore, removing the italics and setting color to gray
     */
    public static Component loreLine(String text) {
        return loreLine(Component.text(text));
    }

    /**
     * Makes a component that is a line of lore, removing the italics and setting color to gray
     */
    public static Component loreLine(Component text) {
        return Component.empty()
                .decorations(EnumSet.allOf(TextDecoration.class), false)
                .color(NamedTextColor.GRAY)
                .append(text);
    }

    /**
     * Creates a component from a string using MiniMessage
     */
    public static Component fromMM(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

    public static ItemLore createLore(String... lines) {
        ItemLore.Builder lore = ItemLore.lore();
        for (String line : lines) {
            lore.addLine(loreLine(fromMM(line)));
        }
        return lore.build();
    }
}
