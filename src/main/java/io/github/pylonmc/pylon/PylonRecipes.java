package io.github.pylonmc.pylon;

import io.github.pylonmc.pylon.recipes.BloomeryDisplayRecipe;
import io.github.pylonmc.pylon.recipes.CastingRecipe;
import io.github.pylonmc.pylon.recipes.CrucibleRecipe;
import io.github.pylonmc.pylon.recipes.DrillingDisplayRecipe;
import io.github.pylonmc.pylon.recipes.ForgingDisplayRecipe;
import io.github.pylonmc.pylon.recipes.GrindstoneRecipe;
import io.github.pylonmc.pylon.recipes.HammerRecipe;
import io.github.pylonmc.pylon.recipes.MeltingRecipe;
import io.github.pylonmc.pylon.recipes.MixingPotRecipe;
import io.github.pylonmc.pylon.recipes.MoldingRecipe;
import io.github.pylonmc.pylon.recipes.PipeBendingRecipe;
import io.github.pylonmc.pylon.recipes.PitKilnRecipe;
import io.github.pylonmc.pylon.recipes.PressRecipe;
import io.github.pylonmc.pylon.recipes.ShimmerAltarRecipe;
import io.github.pylonmc.pylon.recipes.SmelteryRecipe;
import io.github.pylonmc.pylon.recipes.StrainingRecipe;
import io.github.pylonmc.pylon.recipes.TableSawRecipe;


public class PylonRecipes {

    private PylonRecipes() {
        throw new AssertionError("Utility class");
    }

    public static void initialize() {
        CastingRecipe.RECIPE_TYPE.register();
        DrillingDisplayRecipe.RECIPE_TYPE.register();
        ForgingDisplayRecipe.RECIPE_TYPE.register();
        BloomeryDisplayRecipe.RECIPE_TYPE.register();
        GrindstoneRecipe.RECIPE_TYPE.register();
        HammerRecipe.RECIPE_TYPE.register();
        ShimmerAltarRecipe.RECIPE_TYPE.register();
        MeltingRecipe.RECIPE_TYPE.register();
        MixingPotRecipe.RECIPE_TYPE.register();
        CrucibleRecipe.RECIPE_TYPE.register();
        MoldingRecipe.RECIPE_TYPE.register();
        PipeBendingRecipe.RECIPE_TYPE.register();
        PressRecipe.RECIPE_TYPE.register();
        SmelteryRecipe.RECIPE_TYPE.register();
        PitKilnRecipe.RECIPE_TYPE.register();
        StrainingRecipe.RECIPE_TYPE.register();
        TableSawRecipe.RECIPE_TYPE.register();
    }
}
