package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.fluid.Slurry;
import io.github.pylonmc.pylon.base.items.multiblocks.MixingPot;
import io.github.pylonmc.pylon.base.util.VanillaOrPylon;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import org.bukkit.Material;
import org.bukkit.inventory.RecipeChoice;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public final class PylonFluids {

    private PylonFluids() {
        throw new AssertionError("Utility class");
    }

    public static final PylonFluid WATER = new PylonFluid(
            pylonKey("water"),
            Material.BLUE_CONCRETE
    ).addTag(new FluidTemperature(20));

    public static final PylonFluid LAVA = new PylonFluid(
            pylonKey("lava"),
            Material.ORANGE_CONCRETE
    ).addTag(new FluidTemperature(850));

    public static final PylonFluid SLURRY = new PylonFluid(
            pylonKey("slurry"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(new FluidTemperature(20));

    public static final PylonFluid REDSTONE_SLURRY = new Slurry(
            pylonKey("redstone_slurry"),
            new VanillaOrPylon.Vanilla(Material.REDSTONE)
    ).addTag(new FluidTemperature(20));

    public static void initialize() {
        WATER.register();
        LAVA.register();
        SLURRY.register();
        REDSTONE_SLURRY.register();

        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                pylonKey("slurry"),
                Map.of(new RecipeChoice.ExactChoice(PylonItems.ROCK_DUST), 1),
                SLURRY,
                false,
                WATER,
                1000
        ));
    }
}
