package io.github.pylonmc.pylon.base.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public final class ColorUtils {

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
        } else if (celsius > 650) {
            return thermalColor;
        } else {
            // Interpolate between gray and the thermal color
            double t = (celsius - 450) / 200.0;
            int r = (int) Math.round(Color.GRAY.getRed() + (thermalColor.getRed() - Color.GRAY.getRed()) * t);
            int g = (int) Math.round(Color.GRAY.getGreen() + (thermalColor.getGreen() - Color.GRAY.getGreen()) * t);
            int b = (int) Math.round(Color.GRAY.getBlue() + (thermalColor.getBlue() - Color.GRAY.getBlue()) * t);
            return Color.fromRGB(r, g, b);
        }
    }

    private int clampAndRound(double value) {
        int rounded = (int) Math.round(value);
        return Math.max(0, Math.min(255, rounded));
    }
}
