package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3d;

import java.util.function.Consumer;

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
        return spawnUnitSquareTextDisplay(location, color, display -> {});
    }

    public @NotNull TextDisplay spawnUnitSquareTextDisplay(@NotNull Location location, @NotNull Color color, Consumer<TextDisplay> initializer) {
        return location.getWorld().spawn(location, TextDisplay.class, display -> {
            display.setTransformationMatrix( // https://github.com/TheCymaera/minecraft-hologram/blob/d67eb43308df61bdfe7283c6821312cca5f9dea9/src/main/java/com/heledron/hologram/utilities/rendering/textDisplays.kt#L15
                    new Matrix4f()
                            .translate(-0.1f + .5f, -0.5f + .5f, 0f)
                            .scale(8.0f, 4.0f, 1f)
            );
            display.text(Component.text(" "));
            display.setBackgroundColor(color);
            initializer.accept(display);
        });
    }

    public @NotNull Vector3d getDisplacement(@NotNull Location source, @NotNull Location target) {
        return new Vector3d(target.toVector().toVector3f()).sub(source.toVector().toVector3f());
    }

    public @NotNull Vector3d getDirection(@NotNull Location source, @NotNull Location target) {
        return getDisplacement(source, target).normalize();
    }

    public @NotNull Component createBar(double proportion, int bars, TextColor color) {
        int filledBars = (int) Math.round(bars * proportion);
        return Component.text("|".repeat(filledBars)).color(color)
                .append(Component.text("|".repeat(bars - filledBars)).color(NamedTextColor.GRAY));
    }

    public @NotNull Component createProgressBar(double progress, int bars, TextColor color) {
        int filledBars = (int) Math.round(bars * progress);
        return Component.translatable("pylon.pylonbase.gui.progress_bar.text").arguments(
                PylonArgument.of("filled_bars", Component.text("|".repeat(filledBars)).color(color)),
                PylonArgument.of("empty_bars", "|".repeat(bars - filledBars)),
                PylonArgument.of("progress", UnitFormat.PERCENT.format(progress * 100))
        );
    }

    public @NotNull Component createProgressBar(double amount, double max, int bars, TextColor color) {
        return createProgressBar(amount / max, bars, color);
    }

    public @NotNull Component createFluidAmountBar(double amount, double capacity, int bars, TextColor fluidColor) {
        int filledBars = Math.max(0, (int) Math.round(bars * amount / capacity));
        return Component.translatable("pylon.pylonbase.gui.fluid_amount_bar.text").arguments(
                PylonArgument.of("filled_bars", Component.text("|".repeat(filledBars)).color(fluidColor)),
                PylonArgument.of("empty_bars", Component.text("|".repeat(bars - filledBars)).color(NamedTextColor.GRAY)),
                PylonArgument.of("amount", Math.round(amount)),
                PylonArgument.of("capacity", UnitFormat.MILLIBUCKETS.format(Math.round(capacity)))
        );
    }

    /**
     * @param display if null nothing gets done
     */
    public void animate(@Nullable ItemDisplay display, int delay, int duration, Matrix4f matrix) {
        if (display == null) return;

        display.setInterpolationDelay(delay);
        display.setInterpolationDuration(duration);
        display.setTransformationMatrix(matrix);
    }

    /**
     * @param display if null nothing gets done
     */
    public void animate(@Nullable ItemDisplay display, int duration, Matrix4f matrix) {
        if (display == null) return;

        animate(display, 0, duration, matrix);
    }

    public boolean shouldBreakBlockUsingTool(@NotNull Block block, @NotNull ItemStack tool) {
        return !block.getType().isAir()
                && !(block.getState() instanceof BlockInventoryHolder)
                && !BlockStorage.isPylonBlock(block)
                && block.getType().getHardness() >= 0
                && block.isPreferredTool(tool)
                && tool.hasData(DataComponentTypes.TOOL)
                && tool.hasData(DataComponentTypes.DAMAGE);
    }
}
