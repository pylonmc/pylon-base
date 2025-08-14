package io.github.pylonmc.pylon.base.util;

import com.destroystokyo.paper.MaterialSetTag;
import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.events.FakeBlockBreakEvent;
import io.github.pylonmc.pylon.base.events.FakeBlockPlaceEvent;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Arrays;
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

    public static boolean canPlaceBlock(
            @NotNull Player player,
            @NotNull Block placeBlock
    ) {
        return canPlaceBlock(
                player,
                placeBlock,
                placeBlock
        );
    }

    public static boolean canPlaceBlock(
            @NotNull Player player,
            @NotNull Block placeBlock,
            @NotNull Block blockAgainst
    ) {
        return canPlaceBlock(
                placeBlock,
                placeBlock.getState(),
                blockAgainst,
                player.getInventory().getItemInMainHand(),
                player,
                true, // Why needs a `canBuild` param before check permission
                EquipmentSlot.HAND
        );
    }

    public static boolean canPlaceBlock(
            @NotNull Block placeBlock,
            @NotNull BlockState replacedBlockState,
            @NotNull Block blockAgainst,
            @NotNull ItemStack itemInMainHand,
            @NotNull Player player,
            boolean canBuild,
            @NotNull EquipmentSlot hand
    ) {
        FakeBlockPlaceEvent event = simulateBlockPlace(placeBlock, replacedBlockState, blockAgainst, itemInMainHand, player, canBuild, hand);
        return !event.isCancelled();
    }

    public static @NotNull FakeBlockPlaceEvent simulateBlockPlace(
            @NotNull Block placeBlock,
            @NotNull BlockState replacedBlockState,
            @NotNull Block blockAgainst,
            @NotNull ItemStack itemInMainHand,
            @NotNull Player player,
            boolean canBuild,
            @NotNull EquipmentSlot hand
    ) {
        FakeBlockPlaceEvent event = new FakeBlockPlaceEvent(
                placeBlock,
                replacedBlockState,
                blockAgainst,
                itemInMainHand,
                player,
                canBuild,
                hand
        );
        event.callEvent();
        return event;
    }

    public static boolean canBreakBlock(
            @NotNull Player player,
            @NotNull Block theBlock
    ) {
        FakeBlockBreakEvent event = simulateBlockBreak(player, theBlock);
        return !event.isCancelled();
    }

    public static @NotNull FakeBlockBreakEvent simulateBlockBreak(
            @NotNull Player player,
            @NotNull Block theBlock
    ) {
        FakeBlockBreakEvent event = new FakeBlockBreakEvent(theBlock, player);
        event.callEvent();
        return event;
    }

    // str: world_name;x;y;z
    @SuppressWarnings("SequencedCollectionMethodCanBeUsed")
    @Contract("null -> null; !null -> !null")
    public static Location resolveStr2Loc(@Nullable String str) {
        if (str == null) return null;

        String[] parts = str.split(";");
        if (parts.length != 4) {
            return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
        }

        try {
            World world = Bukkit.getWorld(parts[0]);
            int x = Integer.parseInt(parts[1]);
            int y = Integer.parseInt(parts[2]);
            int z = Integer.parseInt(parts[3]);
            return new Location(world, x, y, z);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid location string", e);
        }
    }

    // str: world_name;x;y;z
    @NotNull
    public static String resolveLoc2str(@NotNull Location location) {
        return location.getWorld().getName() + ";" + location.getBlockX() + ";" + location.getBlockY() + ";" + location.getBlockZ();
    }

    public static void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count) {
        new ParticleBuilder(particle)
                .location(location)
                .offset(0, 0, 0)
                .count(count)
                .spawn();
    }
}
