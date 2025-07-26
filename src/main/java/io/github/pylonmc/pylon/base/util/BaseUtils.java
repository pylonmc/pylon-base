package io.github.pylonmc.pylon.base.util;

import com.destroystokyo.paper.MaterialSetTag;
import io.github.pylonmc.pylon.base.PylonBase;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import xyz.xenondevs.invui.gui.AbstractGui;
import xyz.xenondevs.invui.inventory.Inventory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@UtilityClass
public class BaseUtils {

    public final Color METAL_GRAY = Color.fromRGB(0xaaaaaa);

    public final int DEFAULT_FURNACE_TIME_TICKS = 20 * 10;
    public final int DEFAULT_SMOKER_TIME_TICKS = 20 * 5;
    public final int DEFAULT_BLAST_FURNACE_TIME_TICKS = 20 * 5;

    public final MaterialSetTag SEEDS = new MaterialSetTag(
            baseKey("seeds"),
            Material.WHEAT_SEEDS,
            Material.BEETROOT_SEEDS,
            Material.PUMPKIN_SEEDS,
            Material.MELON_SEEDS,
            Material.TORCHFLOWER_SEEDS
    );

    public static @NotNull NamespacedKey baseKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }

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

    public @NotNull ShapedRecipe reflectRecipe(@NotNull ShapedRecipe recipe) {
        NamespacedKey key = recipe.getKey();
        key = new NamespacedKey(key.getNamespace(), key.getKey() + "_reflected");
        ShapedRecipe reflected = new ShapedRecipe(key, recipe.getResult());
        reflected.setGroup(recipe.getGroup());
        reflected.setCategory(recipe.getCategory());
        String[] shape = Arrays.stream(recipe.getShape()).map(
                line -> {
                    char[] newChars = new char[line.length()];
                    for (int i = line.length() - 1; i >= 0; i--) {
                        newChars[i] = line.charAt(line.length() - 1 - i);
                    }
                    return new String(newChars);
                }
        ).toArray(String[]::new);
        reflected.shape(shape);
        for (Map.Entry<Character, RecipeChoice> entry : recipe.getChoiceMap().entrySet()) {
            reflected.setIngredient(entry.getKey(), entry.getValue());
        }
        return reflected;
    }

    public static boolean matchRecipeChoiceMap(Map<RecipeChoice, Integer> recipe, List<ItemStack> stacks) {
        for (Map.Entry<RecipeChoice, Integer> component : recipe.entrySet()) {
            int sumItems = 0;
            for (ItemStack stack : stacks) {
                if (component.getKey().test(stack)) {
                    sumItems += stack.getAmount();
                }
            }
            if (sumItems < component.getValue()) {
                return false;
            }
        }
        return true;
    }

    public static void removeRecipeChoiceMapFromGui(Map<RecipeChoice, Integer> recipe, AbstractGui gui) {
        for (Map.Entry<RecipeChoice, Integer> component : recipe.entrySet()) {
            int required = component.getValue();
            rmitemloop:
            for (Inventory inventory : gui.getAllInventories()) {
                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack curItem = inventory.getItem(i);
                    if (curItem != null && component.getKey().test(curItem)) {
                        if (required <= curItem.getAmount()) {
                            curItem.subtract(required);
                            inventory.setItemSilently(i, curItem);
                            break rmitemloop;
                        } else {
                            required -= curItem.getAmount();
                            curItem.subtract(curItem.getAmount());
                            inventory.setItemSilently(i, curItem);
                        }
                    }
                }
            }
        }
    }
}
