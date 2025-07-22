package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.machines.smelting.Slurry;
import io.github.pylonmc.pylon.base.recipes.CastingRecipe;
import io.github.pylonmc.pylon.base.recipes.MeltingRecipe;
import io.github.pylonmc.pylon.base.recipes.MixingPotRecipe;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class BaseFluids {

    private BaseFluids() {
        throw new AssertionError("Utility class");
    }

    public static final PylonFluid WATER = new PylonFluid(
            baseKey("water"),
            Material.BLUE_CONCRETE
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid LAVA = new PylonFluid(
            baseKey("lava"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid SULFUR = new PylonFluid(
            baseKey("sulfur"),
            Material.YELLOW_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid MERCURY = new PylonFluid(
            baseKey("mercury"),
            Material.GRAY_CONCRETE
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid COPPER = new PylonFluid(
            baseKey("copper"),
            Material.TERRACOTTA
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid GOLD = new PylonFluid(
            baseKey("gold"),
            Material.GOLD_BLOCK
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid IRON = new PylonFluid(
            baseKey("iron"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid SILVER = new PylonFluid(
            baseKey("silver"),
            Material.WHITE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid LEAD = new PylonFluid(
            baseKey("lead"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid TIN = new PylonFluid(
            baseKey("tin"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid ZINC = new PylonFluid(
            baseKey("zinc"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid COBALT = new PylonFluid(
            baseKey("cobalt"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid NICKEL = new PylonFluid(
            baseKey("nickel"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid BRONZE = new PylonFluid(
            baseKey("bronze"),
            Material.BROWN_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid BRASS = new PylonFluid(
            baseKey("brass"),
            Material.GOLD_BLOCK
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid STEEL = new PylonFluid(
            baseKey("steel"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid SLURRY = new PylonFluid(
            baseKey("slurry"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid COAL_SLURRY = new Slurry(
            baseKey("slurry_coal"),
            BaseItems.COAL_DUST
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid CARBON_SLURRY = new Slurry(
            baseKey("slurry_carbon"),
            BaseItems.CARBON_DUST
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_COPPER_SLURRY = new Slurry(
            baseKey("slurry_raw_copper"),
            BaseItems.CRUSHED_RAW_COPPER
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_GOLD_SLURRY = new Slurry(
            baseKey("slurry_raw_gold"),
            BaseItems.CRUSHED_RAW_GOLD
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_IRON_SLURRY = new Slurry(
            baseKey("slurry_raw_iron"),
            BaseItems.CRUSHED_RAW_IRON
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_LEAD_SLURRY = new Slurry(
            baseKey("slurry_raw_lead"),
            BaseItems.CRUSHED_RAW_LEAD
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_TIN_SLURRY = new Slurry(
            baseKey("slurry_raw_tin"),
            BaseItems.CRUSHED_RAW_TIN
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid RAW_ZINC_SLURRY = new Slurry(
            baseKey("slurry_raw_zinc"),
            BaseItems.CRUSHED_RAW_ZINC
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid REDSTONE_SLURRY = new Slurry(
            baseKey("slurry_redstone"),
            new ItemStack(Material.REDSTONE)
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid PLANT_OIL = new PylonFluid(
            baseKey("plant_oil"),
            Material.YELLOW_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid HYDRAULIC_FLUID = new PylonFluid(
            baseKey("hydraulic_fluid"),
            Material.BLUE_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);


    public static final PylonFluid DIRTY_HYDRAULIC_FLUID = new PylonFluid(
            baseKey("dirty_hydraulic_fluid"),
            Material.BROWN_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);

    public static void initialize() {
        WATER.register();
        LAVA.register();
        SULFUR.register();
        addSolidForms(SULFUR, 112.8, BaseItems.SULFUR);
        COPPER.register();
        addSolidForms(COPPER, 1083, new ItemStack(Material.COPPER_INGOT), BaseItems.COPPER_DUST);
        GOLD.register();
        addSolidForms(GOLD, 1064, new ItemStack(Material.GOLD_INGOT), BaseItems.GOLD_DUST);
        IRON.register();
        addSolidForms(IRON, 1538, new ItemStack(Material.IRON_INGOT), BaseItems.IRON_DUST);
        SILVER.register();
        addSolidForms(SILVER, 961.8, BaseItems.SILVER_INGOT, BaseItems.SILVER_DUST);
        LEAD.register();
        addSolidForms(LEAD, 327.5, BaseItems.LEAD_INGOT, BaseItems.LEAD_DUST);
        TIN.register();
        addSolidForms(TIN, 231.9, BaseItems.TIN_INGOT, BaseItems.TIN_DUST);
        ZINC.register();
        addSolidForms(ZINC, 419.5, BaseItems.ZINC_INGOT, BaseItems.ZINC_DUST);
        COBALT.register();
        addSolidForms(COBALT, 1495, BaseItems.COBALT_INGOT, BaseItems.COBALT_DUST);
        NICKEL.register();
        addSolidForms(NICKEL, 1455, BaseItems.NICKEL_INGOT, BaseItems.NICKEL_DUST);
        BRONZE.register();
        addSolidForms(BRONZE, 950, BaseItems.BRONZE_INGOT, BaseItems.BRONZE_DUST);
        BRASS.register();
        addSolidForms(BRASS, 900, BaseItems.BRASS_INGOT, BaseItems.BRASS_DUST);
        STEEL.register();
        addSolidForms(STEEL, 1540, BaseItems.STEEL_INGOT, BaseItems.STEEL_DUST);
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
        PLANT_OIL.register();
        HYDRAULIC_FLUID.register();
        DIRTY_HYDRAULIC_FLUID.register();

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                SLURRY.getKey(),
                List.of(BaseItems.ROCK_DUST),
                WATER,
                1000,
                FluidOrItem.of(SLURRY, 1000),
                false
        ));

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                HYDRAULIC_FLUID.getKey(),
                List.of(BaseItems.SHIMMER_DUST_1),
                PLANT_OIL,
                1000,
                FluidOrItem.of(HYDRAULIC_FLUID, 1000),
                false
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
