package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.machines.smelting.Slurry;
import io.github.pylonmc.rebar.content.guide.RebarGuide;
import io.github.pylonmc.rebar.fluid.RebarFluid;
import io.github.pylonmc.rebar.fluid.tags.FluidTemperature;
import io.github.pylonmc.rebar.recipe.IngredientCalculator;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class BaseFluids {

    private BaseFluids() {
        throw new AssertionError("Utility class");
    }

    public static final RebarFluid WATER = new RebarFluid(
            baseKey("water"),
            Material.BLUE_CONCRETE
    ).addTag(FluidTemperature.NORMAL);
    static {
        WATER.register();
        IngredientCalculator.addBaseIngredient(WATER);
    }

    public static final RebarFluid LAVA = new RebarFluid(
            baseKey("lava"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);
    static {
        LAVA.register();
        IngredientCalculator.addBaseIngredient(LAVA);
    }

    public static final RebarFluid PLANT_OIL = new RebarFluid(
            baseKey("plant_oil"),
            Material.YELLOW_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);
    static {
        PLANT_OIL.register();
        IngredientCalculator.addBaseIngredient(PLANT_OIL);
    }

    public static final RebarFluid HYDRAULIC_FLUID = new RebarFluid(
            baseKey("hydraulic_fluid"),
            Material.BLUE_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);
    static {
        HYDRAULIC_FLUID.register();
    }


    public static final RebarFluid DIRTY_HYDRAULIC_FLUID = new RebarFluid(
            baseKey("dirty_hydraulic_fluid"),
            Material.BROWN_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);
    static {
        DIRTY_HYDRAULIC_FLUID.register();
    }

    public static final RebarFluid REFLECTOR_FLUID = new RebarFluid(
            baseKey("reflector_fluid"),
            Material.WHITE_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);
    static {
        REFLECTOR_FLUID.register();
    }

    public static final RebarFluid SUGARCANE = new RebarFluid(
            baseKey("sugarcane"),
            Material.LIME_CONCRETE
    ).addTag(FluidTemperature.NORMAL);
    static {
        SUGARCANE.register();
        RebarGuide.hideFluid(SUGARCANE.getKey());
    }

    public static final RebarFluid ETHANOL = new RebarFluid(
            baseKey("ethanol"),
            Material.LIGHT_GRAY_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);
    static {
        ETHANOL.register();
    }

    public static final RebarFluid BIODIESEL = new RebarFluid(
            baseKey("biodiesel"),
            Material.YELLOW_CONCRETE
    ).addTag(FluidTemperature.NORMAL);
    static {
        BIODIESEL.register();
    }

    public static final RebarFluid OBSCYRA = new RebarFluid(
            baseKey("obscyra"),
            Material.BLACK_CONCRETE
    ).addTag(FluidTemperature.NORMAL);
    static {
        OBSCYRA.register();
    }

    public static final RebarFluid SULFUR = new RebarFluid(
            baseKey("sulfur"),
            Material.YELLOW_TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        SULFUR.register();
    }

    public static final RebarFluid MERCURY = new RebarFluid(
            baseKey("mercury"),
            Material.CYAN_TERRACOTTA
    ).addTag(FluidTemperature.NORMAL);
    static {
        MERCURY.register();
    }

    public static final RebarFluid COPPER = new RebarFluid(
            baseKey("copper"),
            Material.TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        COPPER.register();
    }

    public static final RebarFluid IRON = new RebarFluid(
            baseKey("iron"),
            Material.RED_TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        IRON.register();
    }

    public static final RebarFluid GOLD = new RebarFluid(
            baseKey("gold"),
            Material.YELLOW_CONCRETE
    ).addTag(FluidTemperature.HOT);
    static {
        GOLD.register();
    }

    public static final RebarFluid TIN = new RebarFluid(
            baseKey("tin"),
            Material.GREEN_TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        TIN.register();
    }

    public static final RebarFluid BRONZE = new RebarFluid(
            baseKey("bronze"),
            Material.BROWN_CONCRETE
    ).addTag(FluidTemperature.HOT);
    static {
        BRONZE.register();
    }

    public static final RebarFluid STEEL = new RebarFluid(
            baseKey("steel"),
            Material.GRAY_CONCRETE
    ).addTag(FluidTemperature.HOT);
    static {
        STEEL.register();
    }

    public static final RebarFluid COBALT = new RebarFluid(
            baseKey("cobalt"),
            Material.BLUE_TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        COBALT.register();
    }

    public static final RebarFluid NICKEL = new RebarFluid(
            baseKey("nickel"),
            Material.WHITE_TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        NICKEL.register();
    }

    public static final RebarFluid SLURRY = new RebarFluid(
            baseKey("slurry"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.NORMAL);
    static {
        SLURRY.register();
    }

    public static final RebarFluid COAL_SLURRY = new Slurry(
            baseKey("slurry_coal"),
            BaseItems.COAL_DUST
    ).addTag(FluidTemperature.NORMAL);
    static {
        COAL_SLURRY.register();
    }

    public static final RebarFluid CARBON_SLURRY = new Slurry(
            baseKey("slurry_carbon"),
            BaseItems.CARBON
    ).addTag(FluidTemperature.NORMAL);
    static {
        CARBON_SLURRY.register();
    }

    public static final RebarFluid RAW_COPPER_SLURRY = new Slurry(
            baseKey("slurry_raw_copper"),
            BaseItems.CRUSHED_RAW_COPPER
    ).addTag(FluidTemperature.NORMAL);
    static {
        RAW_COPPER_SLURRY.register();
    }

    public static final RebarFluid RAW_IRON_SLURRY = new Slurry(
            baseKey("slurry_raw_iron"),
            BaseItems.CRUSHED_RAW_IRON
    ).addTag(FluidTemperature.NORMAL);
    static {
        RAW_IRON_SLURRY.register();
    }

    public static final RebarFluid RAW_GOLD_SLURRY = new Slurry(
            baseKey("slurry_raw_gold"),
            BaseItems.CRUSHED_RAW_GOLD
    ).addTag(FluidTemperature.NORMAL);
    static {
        RAW_GOLD_SLURRY.register();
    }

    public static final RebarFluid REDSTONE_SLURRY = new Slurry(
            baseKey("slurry_redstone"),
            new ItemStack(Material.REDSTONE)
    ).addTag(FluidTemperature.NORMAL);
    static {
        REDSTONE_SLURRY.register();
    }

    public static final RebarFluid RAW_TIN_SLURRY = new Slurry(
            baseKey("slurry_raw_tin"),
            BaseItems.CRUSHED_RAW_TIN
    ).addTag(FluidTemperature.NORMAL);
    static {
        RAW_TIN_SLURRY.register();
    }

    public static final RebarFluid SPONGE_IRON_SLURRY = new Slurry(
            baseKey("slurry_sponge_iron"),
            BaseItems.SPONGE_IRON
    ).addTag(FluidTemperature.NORMAL);
    static {
        SPONGE_IRON_SLURRY.register();
    }

    /**
     * Calling this function will run the static blocks
     */
    static void initialize() {
    }
}
