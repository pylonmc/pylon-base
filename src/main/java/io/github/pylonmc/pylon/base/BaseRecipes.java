package io.github.pylonmc.pylon.base;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.recipes.*;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.Settings;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class BaseRecipes {

    private BaseRecipes() {
        throw new AssertionError("Utility class");
    }

    public static void initialize() {
        CastingRecipe.RECIPE_TYPE.register();

        GrindstoneRecipe.RECIPE_TYPE.register();
        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("string_from_bamboo"),
                new ItemStack(Material.BAMBOO, 4),
                new ItemStack(Material.STRING),
                3,
                Material.BAMBOO.createBlockData(data -> {
                    Ageable ageable = (Ageable) data;
                    ageable.setAge(ageable.getMaximumAge());
                })
        ));

        HammerRecipe.RECIPE_TYPE.register();

        MagicAltarRecipe.RECIPE_TYPE.register();

        MeltingRecipe.RECIPE_TYPE.register();

        MixingPotRecipe.RECIPE_TYPE.register();

        PipeBendingRecipe.RECIPE_TYPE.register();

        PressRecipe.RECIPE_TYPE.register();
        ConfigSection config = Settings.get(BaseKeys.PRESS).getSectionOrThrow("oil-amount");
        for (String key : config.getKeys()) {
            Material material = Material.getMaterial(key.toUpperCase());
            Preconditions.checkState(material != null, "No such material " + key);
            int amount = config.get(key, Integer.class);
            PressRecipe.RECIPE_TYPE.addRecipe(new PressRecipe(material.getKey(), new ItemStack(material), amount));
        }

        SmelteryRecipe.RECIPE_TYPE.register();

        StrainingRecipe.RECIPE_TYPE.register();

        TableSawRecipe.RECIPE_TYPE.register();
        addLogRecipe(Material.OAK_LOG, Material.OAK_PLANKS);
        addLogRecipe(Material.SPRUCE_LOG, Material.SPRUCE_PLANKS);
        addLogRecipe(Material.BIRCH_LOG, Material.BIRCH_PLANKS);
        addLogRecipe(Material.JUNGLE_LOG, Material.JUNGLE_PLANKS);
        addLogRecipe(Material.ACACIA_LOG, Material.ACACIA_PLANKS);
        addLogRecipe(Material.CHERRY_LOG, Material.CHERRY_PLANKS);
        addLogRecipe(Material.PALE_OAK_LOG, Material.PALE_OAK_PLANKS);
        addLogRecipe(Material.DARK_OAK_LOG, Material.DARK_OAK_PLANKS);
        addLogRecipe(Material.MANGROVE_LOG, Material.MANGROVE_PLANKS);
        addPlanksRecipe(Material.OAK_PLANKS);
        addPlanksRecipe(Material.SPRUCE_PLANKS);
        addPlanksRecipe(Material.BIRCH_PLANKS);
        addPlanksRecipe(Material.JUNGLE_PLANKS);
        addPlanksRecipe(Material.ACACIA_PLANKS);
        addPlanksRecipe(Material.CHERRY_PLANKS);
        addPlanksRecipe(Material.PALE_OAK_PLANKS);
        addPlanksRecipe(Material.DARK_OAK_PLANKS);
        addPlanksRecipe(Material.MANGROVE_PLANKS);
    }

    private static void addLogRecipe(Material input, Material output) {
        TableSawRecipe.RECIPE_TYPE.addRecipe(new TableSawRecipe(
                input.getKey(),
                new ItemStack(input),
                new ItemStack(output, 6),
                input.createBlockData(),
                20
        ));
    }

    private static void addPlanksRecipe(Material input) {
        TableSawRecipe.RECIPE_TYPE.addRecipe(new TableSawRecipe(
                input.getKey(),
                new ItemStack(input),
                new ItemStack(Material.STICK, 6),
                input.createBlockData(),
                20
        ));
    }
}
