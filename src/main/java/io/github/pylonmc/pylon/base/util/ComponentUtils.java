package io.github.pylonmc.pylon.base.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.EnumSet;

public class ComponentUtils {

    private ComponentUtils() {
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
}
