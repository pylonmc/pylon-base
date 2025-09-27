package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3d;


@UtilityClass
public class BaseUtils {

    public static @NotNull NamespacedKey baseKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }

    public final Color METAL_GRAY = Color.fromRGB(0xaaaaaa);

    public @NotNull Color colorFromTemperature(double celsius) {
        double temp = (celsius + 273.15) / 100.0;
        double red, green, blue;

        // ✨ magic ✨
        if (temp <= 66) {
            red = 255;
            green = 99.4708025861 * Math.log(temp) - 161.1195681661;
            blue = temp <= 19 ? 0 : 138.5177312231 * Math.log(temp - 10) - 305.0447927307;
        } else {
            red = 329.698727446 * Math.pow(temp - 60, -0.1332047592);
            green = 288.1221695283 * Math.pow(temp - 60, -0.0755148492);
            blue = 255;
        }

        Color thermalColor = Color.fromRGB(
                clampAndRound(red),
                clampAndRound(green),
                clampAndRound(blue)
        );

        if (celsius < 450) {
            return METAL_GRAY;
        }

        if (celsius > 650) {
            return thermalColor;
        }

        // Interpolate between gray and the thermal color
        double t = (celsius - 450) / 200.0;
        int r = (int) Math.round(Color.GRAY.getRed() + (thermalColor.getRed() - Color.GRAY.getRed()) * t);
        int g = (int) Math.round(Color.GRAY.getGreen() + (thermalColor.getGreen() - Color.GRAY.getGreen()) * t);
        int b = (int) Math.round(Color.GRAY.getBlue() + (thermalColor.getBlue() - Color.GRAY.getBlue()) * t);
        return Color.fromRGB(r, g, b);
    }

    private int clampAndRound(double value) {
        int rounded = (int) Math.round(value);
        return Math.max(0, Math.min(255, rounded));
    }

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

    public @NotNull Vector3d getDisplacement(@NotNull Location source, @NotNull Location target) {
        return new Vector3d(target.toVector().toVector3f()).sub(source.toVector().toVector3f());
    }

    public @NotNull Vector3d getDirection(@NotNull Location source, @NotNull Location target) {
        return getDisplacement(source, target).normalize();
    }

    public @NotNull Component createFluidAmountBar(double amount, double capacity, int bars, TextColor fluidColor) {
        int filledBars = (int) Math.round(bars * amount / capacity);
        return Component.translatable("pylon.pylonbase.gui.fluid_amount_bar.text").arguments(
                PylonArgument.of("filled_bars", Component.text("|".repeat(filledBars)).color(fluidColor)),
                PylonArgument.of("empty_bars", "|".repeat(bars - filledBars)),
                PylonArgument.of("amount", Math.round(amount)),
                PylonArgument.of("capacity", UnitFormat.MILLIBUCKETS.format(Math.round(capacity)))
        );
    }

    public void animate(ItemDisplay display, int delay, int duration, Matrix4f matrix) {
        display.setInterpolationDelay(delay);
        display.setInterpolationDuration(duration);
        display.setTransformationMatrix(matrix);
    }

    public void animate(ItemDisplay display, int duration, Matrix4f matrix) {
        animate(display, 0, duration, matrix);
    }
}
