package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.fluid.Slurry;
import io.github.pylonmc.pylon.base.items.multiblocks.MixingPot;
import io.github.pylonmc.pylon.base.items.multiblocks.smelting.CastRecipe;
import io.github.pylonmc.pylon.base.items.multiblocks.smelting.MeltRecipe;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
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
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid LAVA = new PylonFluid(
            pylonKey("lava"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid SULFUR = new PylonFluid(
            pylonKey("sulfur"),
            Material.YELLOW_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid MERCURY = new PylonFluid(
            pylonKey("mercury"),
            Material.GRAY_CONCRETE
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid COPPER = new PylonFluid(
            pylonKey("copper"),
            Material.TERRACOTTA
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid GOLD = new PylonFluid(
            pylonKey("gold"),
            Material.GOLD_BLOCK
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid IRON = new PylonFluid(
            pylonKey("iron"),
            Material.WHITE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid SILVER = new PylonFluid(
            pylonKey("silver"),
            Material.WHITE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid SLURRY = new PylonFluid(
            pylonKey("slurry"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid COAL_SLURRY = new Slurry(
            pylonKey("slurry_coal"),
            PylonItems.COAL_DUST
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid COKE_SLURRY = new Slurry(
            pylonKey("slurry_coke"),
            PylonItems.COKE_DUST
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_COPPER_SLURRY = new Slurry(
            pylonKey("slurry_raw_copper"),
            PylonItems.CRUSHED_RAW_COPPER
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_GOLD_SLURRY = new Slurry(
            pylonKey("slurry_raw_gold"),
            PylonItems.CRUSHED_RAW_GOLD
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_IRON_SLURRY = new Slurry(
            pylonKey("slurry_raw_iron"),
            PylonItems.CRUSHED_RAW_IRON
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_SILVER_SLURRY = new Slurry(
            pylonKey("slurry_raw_silver"),
            PylonItems.CRUSHED_RAW_SILVER
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid REDSTONE_SLURRY = new Slurry(
            pylonKey("slurry_redstone"),
            new ItemStack(Material.REDSTONE)
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid PLANT_OIL = new PylonFluid(
            pylonKey("plant_oil"),
            Material.YELLOW_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid HYDRAULIC_FLUID = new PylonFluid(
            pylonKey("hydraulic_fluid"),
            Material.BLUE_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);


    public static final PylonFluid DIRTY_HYDRAULIC_FLUID = new PylonFluid(
            pylonKey("dirty_hydraulic_fluid"),
            Material.BLUE_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);

    public static void initialize() {
        WATER.register();
        LAVA.register();
        SULFUR.register();
        addSolidForms(SULFUR, 112.8, PylonItems.SULFUR);
        COPPER.register();
        addSolidForms(COPPER, 1083, new ItemStack(Material.COPPER_INGOT), PylonItems.COPPER_DUST);
        GOLD.register();
        addSolidForms(GOLD, 1064, new ItemStack(Material.GOLD_INGOT), PylonItems.GOLD_DUST);
        IRON.register();
        addSolidForms(IRON, 1538, new ItemStack(Material.IRON_INGOT), PylonItems.IRON_DUST);
        SILVER.register();
        addSolidForms(SILVER, 961.8, PylonItems.SILVER_INGOT, PylonItems.SILVER_DUST);
        MERCURY.register();
        SLURRY.register();
        COAL_SLURRY.register();
        COKE_SLURRY.register();
        RAW_COPPER_SLURRY.register();
        RAW_GOLD_SLURRY.register();
        RAW_IRON_SLURRY.register();
        RAW_SILVER_SLURRY.register();
        REDSTONE_SLURRY.register();
        PLANT_OIL.register();
        HYDRAULIC_FLUID.register();
        DIRTY_HYDRAULIC_FLUID.register();

        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                SLURRY.getKey(),
                Map.of(new RecipeChoice.ExactChoice(PylonItems.ROCK_DUST), 1),
                SLURRY,
                false,
                WATER,
                1000
        ));

        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                HYDRAULIC_FLUID.getKey(),
                Map.of(new RecipeChoice.ExactChoice(PylonItems.SHIMMER_DUST_1), 1),
                HYDRAULIC_FLUID,
                false,
                PLANT_OIL,
                1000
        ));
    }

    private static void addSolidForms(PylonFluid fluid, double temperature, ItemStack main, ItemStack... additional) {
        NamespacedKey fluidKey = fluid.getKey();
        CastRecipe.RECIPE_TYPE.addRecipe(new CastRecipe(
                fluidKey,
                fluid,
                main,
                temperature
        ));
        MeltRecipe.RECIPE_TYPE.addRecipe(new MeltRecipe(
                fluidKey,
                main,
                fluid,
                temperature
        ));
        for (int i = 0; i < additional.length; i++) {
            ItemStack item = additional[i];
            MeltRecipe.RECIPE_TYPE.addRecipe(new MeltRecipe(
                    new NamespacedKey(fluidKey.namespace(), fluidKey.value() + "_" + i),
                    item,
                    fluid,
                    temperature
            ));
        }
    }
}
