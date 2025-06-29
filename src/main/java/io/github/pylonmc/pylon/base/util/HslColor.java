package io.github.pylonmc.pylon.base.util;

import org.bukkit.Color;
import org.jetbrains.annotations.NotNull;

// Conversion code helpfully provided by https://gist.github.com/ciembor/1494530
public record HslColor(double hue, double saturation, double lightness) {

    public static @NotNull HslColor fromRgb(@NotNull Color color) {
        double red = color.getRed() / 255.0;
        double green = color.getGreen() / 255.0;
        double blue = color.getBlue() / 255.0;

        double min = Math.min(Math.min(red, green), blue);
        double max = Math.max(Math.max(red, green), blue);

        double lightness = (min + max) / 2.0;
        double saturation;
        double hue;

        if (min == max) {
            saturation = 0.0;
            hue = 0.0;
        } else {
            double delta = max - min;
            saturation = lightness < 0.5 ? delta / (max + min) : delta / (2.0 - max - min);

            if (max == red) {
                hue = (green - blue) / delta + (green < blue ? 6.0 : 0.0);
            } else if (max == green) {
                hue = (blue - red) / delta + 2.0;
            } else {
                hue = (red - green) / delta + 4.0;
            }
            hue /= 6.0;
        }

        return new HslColor(hue, saturation, lightness);
    }

    public @NotNull Color toRgb() {
        double r, g, b;

        if (saturation == 0) {
            r = g = b = lightness * 255;
        } else {
            double q = lightness < 0.5 ? lightness * (1 + saturation) : lightness + saturation - lightness * saturation;
            double p = 2 * lightness - q;

            r = hueToRgb(p, q, hue + 1.0 / 3.0) * 255;
            g = hueToRgb(p, q, hue) * 255;
            b = hueToRgb(p, q, hue - 1.0 / 3.0) * 255;
        }

        return Color.fromRGB((int) r, (int) g, (int) b);
    }

    private double hueToRgb(double p, double q, double t) {
        if (t < 0) {
            t += 1;
        }
        if (t > 1) {
            t -= 1;
        }
        if (t < 1.0 / 6.0) {
            return p + (q - p) * 6 * t;
        }
        if (t < 1.0 / 2.0) {
            return q;
        }
        if (t < 2.0 / 3.0) {
            return p + (q - p) * (2.0 / 3.0 - t) * 6;
        }
        return p;
    }
}
