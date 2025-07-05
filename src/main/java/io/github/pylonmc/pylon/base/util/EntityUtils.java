package io.github.pylonmc.pylon.base.util;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@UtilityClass
public class EntityUtils {
    public @NotNull TextDisplay spawnUnitSquareTextDisplay(@NotNull Location location, @NotNull Color color) {
        TextDisplay display = location.getWorld().spawn(location, TextDisplay.class);
        display.setTransformationMatrix( // https://github.com/TheCymaera/minecraft-hologram/blob/d67eb43308df61bdfe7283c6821312cca5f9dea9/src/main/java/com/heledron/hologram/utilities/rendering/textDisplays.kt#L15
                new Matrix4f()
                        .translate(-0.1f + .5f, -0.5f + .5f, 0f)
                        .scale(8.0f, 4.0f, 1f)
        );
        display.text(Component.text(" "));
        display.setBackgroundColor(color);
        return display;
    }
}
