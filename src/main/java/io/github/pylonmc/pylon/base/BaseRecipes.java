package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.recipes.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BaseRecipes {

    private BaseRecipes() {
        throw new AssertionError("Utility class");
    }

    public static void initialize() {
        CastingRecipe.RECIPE_TYPE.register();

        DrillingDisplayRecipe.RECIPE_TYPE.register();

        GrindstoneRecipe.RECIPE_TYPE.register();

        HammerRecipe.RECIPE_TYPE.register();

        MagicAltarRecipe.RECIPE_TYPE.register();

        MeltingRecipe.RECIPE_TYPE.register();

        MixingPotRecipe.RECIPE_TYPE.register();

        MoldingDisplayRecipe.RECIPE_TYPE.register();

        PipeBendingRecipe.RECIPE_TYPE.register();

        PressRecipe.RECIPE_TYPE.register();

        SmelteryRecipe.RECIPE_TYPE.register();

        PitKilnRecipe.RECIPE_TYPE.register();

        StrainingRecipe.RECIPE_TYPE.register();

        TableSawRecipe.RECIPE_TYPE.register();

        addPlanksRecipe(Material.OAK_LOG, Material.OAK_PLANKS);
        addPlanksRecipe(Material.SPRUCE_LOG, Material.SPRUCE_PLANKS);
        addPlanksRecipe(Material.BIRCH_LOG, Material.BIRCH_PLANKS);
        addPlanksRecipe(Material.JUNGLE_LOG, Material.JUNGLE_PLANKS);
        addPlanksRecipe(Material.ACACIA_LOG, Material.ACACIA_PLANKS);
        addPlanksRecipe(Material.CHERRY_LOG, Material.CHERRY_PLANKS);
        addPlanksRecipe(Material.PALE_OAK_LOG, Material.PALE_OAK_PLANKS);
        addPlanksRecipe(Material.DARK_OAK_LOG, Material.DARK_OAK_PLANKS);
        addPlanksRecipe(Material.MANGROVE_LOG, Material.MANGROVE_PLANKS);
        addStickRecipe(Material.OAK_PLANKS);
        addStickRecipe(Material.SPRUCE_PLANKS);
        addStickRecipe(Material.BIRCH_PLANKS);
        addStickRecipe(Material.JUNGLE_PLANKS);
        addStickRecipe(Material.ACACIA_PLANKS);
        addStickRecipe(Material.CHERRY_PLANKS);
        addStickRecipe(Material.PALE_OAK_PLANKS);
        addStickRecipe(Material.DARK_OAK_PLANKS);
        addStickRecipe(Material.MANGROVE_PLANKS);
    }

    private static void addPlanksRecipe(Material input, Material output) {
        TableSawRecipe.RECIPE_TYPE.addRecipe(new TableSawRecipe(
                input.getKey(),
                new ItemStack(input),
                new ItemStack(output, 6),
                input.createBlockData(),
                20
        ));
    }

    private static void addStickRecipe(Material input) {
        TableSawRecipe.RECIPE_TYPE.addRecipe(new TableSawRecipe(
                input.getKey(),
                new ItemStack(input),
                new ItemStack(Material.STICK, 6),
                input.createBlockData(),
                20
        ));
    }
}
