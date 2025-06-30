package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.fluid.Slurry;
import io.github.pylonmc.pylon.base.items.multiblocks.MixingPot;
import io.github.pylonmc.pylon.base.items.multiblocks.smelting.CastingRecipe;
import io.github.pylonmc.pylon.base.items.multiblocks.smelting.MeltingRecipe;
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
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid SILVER = new PylonFluid(
            pylonKey("silver"),
            Material.WHITE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid LEAD = new PylonFluid(
            pylonKey("lead"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid TIN = new PylonFluid(
            pylonKey("tin"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid ZINC = new PylonFluid(
            pylonKey("zinc"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid COBALT = new PylonFluid(
            pylonKey("cobalt"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid NICKEL = new PylonFluid(
            pylonKey("nickel"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid BRONZE = new PylonFluid(
            pylonKey("bronze"),
            Material.BROWN_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid BRASS = new PylonFluid(
            pylonKey("brass"),
            Material.GOLD_BLOCK
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid STEEL = new PylonFluid(
            pylonKey("steel"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid SLURRY = new PylonFluid(
            pylonKey("slurry"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid COAL_SLURRY = new Slurry(
            pylonKey("slurry_coal"),
            PylonItems.COAL_DUST
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid CARBON_SLURRY = new Slurry(
            pylonKey("slurry_carbon"),
            PylonItems.CARBON_DUST
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

    public static final PylonFluid RAW_LEAD_SLURRY = new Slurry(
            pylonKey("slurry_raw_lead"),
            PylonItems.CRUSHED_RAW_LEAD
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_TIN_SLURRY = new Slurry(
            pylonKey("slurry_raw_tin"),
            PylonItems.CRUSHED_RAW_TIN
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_ZINC_SLURRY = new Slurry(
            pylonKey("slurry_raw_zinc"),
            PylonItems.CRUSHED_RAW_ZINC
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid REDSTONE_SLURRY = new Slurry(
            pylonKey("slurry_redstone"),
            new ItemStack(Material.REDSTONE)
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
        LEAD.register();
        addSolidForms(LEAD, 327.5, PylonItems.LEAD_INGOT, PylonItems.LEAD_DUST);
        TIN.register();
        addSolidForms(TIN, 231.9, PylonItems.TIN_INGOT, PylonItems.TIN_DUST);
        ZINC.register();
        addSolidForms(ZINC, 419.5, PylonItems.ZINC_INGOT, PylonItems.ZINC_DUST);
        COBALT.register();
        addSolidForms(COBALT, 1495, PylonItems.COBALT_INGOT, PylonItems.COBALT_DUST);
        NICKEL.register();
        addSolidForms(NICKEL, 1455, PylonItems.NICKEL_INGOT, PylonItems.NICKEL_DUST);
        BRONZE.register();
        addSolidForms(BRONZE, 950, PylonItems.BRONZE_INGOT, PylonItems.BRONZE_DUST);
        BRASS.register();
        addSolidForms(BRASS, 900, PylonItems.BRASS_INGOT, PylonItems.BRASS_DUST);
        STEEL.register();
        addSolidForms(STEEL, 1540, PylonItems.STEEL_INGOT, PylonItems.STEEL_DUST);
        MERCURY.register();
        SLURRY.register();
        COAL_SLURRY.register();
        CARBON_SLURRY.register();
        RAW_COPPER_SLURRY.register();
        RAW_GOLD_SLURRY.register();
        RAW_IRON_SLURRY.register();
        RAW_LEAD_SLURRY.register();
        RAW_TIN_SLURRY.register();
        RAW_ZINC_SLURRY.register();
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

    private static void addSolidForms(PylonFluid fluid, double temperature, ItemStack main, ItemStack... additional) {
        NamespacedKey fluidKey = fluid.getKey();
        CastingRecipe.RECIPE_TYPE.addRecipe(new CastingRecipe(
                fluidKey,
                fluid,
                main,
                temperature
        ));
        MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                fluidKey,
                main,
                fluid,
                temperature
        ));
        for (int i = 0; i < additional.length; i++) {
            ItemStack item = additional[i];
            MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                    new NamespacedKey(fluidKey.namespace(), fluidKey.value() + "_" + i),
                    item,
                    fluid,
                    temperature
            ));
        }
    }
}
