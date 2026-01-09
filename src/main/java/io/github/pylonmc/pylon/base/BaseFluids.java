package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.machines.smelting.Slurry;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class BaseFluids {

    private BaseFluids() {
        throw new AssertionError("Utility class");
    }

    public static final PylonFluid WATER = new PylonFluid(
            baseKey("water"),
            Material.BLUE_CONCRETE
    ).addTag(FluidTemperature.NORMAL);
    static {
        WATER.register();
    }

    public static final PylonFluid LAVA = new PylonFluid(
            baseKey("lava"),
            Material.ORANGE_CONCRETE
    ).addTag(FluidTemperature.HOT);
    static {
        LAVA.register();
    }

    public static final PylonFluid OBSCYRA = new PylonFluid(
            baseKey("obscyra"),
            Material.BLACK_CONCRETE
    ).addTag(FluidTemperature.NORMAL);
    static {
        OBSCYRA.register();
    }

    public static final PylonFluid SULFUR = new PylonFluid(
            baseKey("sulfur"),
            Material.YELLOW_TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        SULFUR.register();
    }

    public static final PylonFluid MERCURY = new PylonFluid(
            baseKey("mercury"),
            Material.CYAN_TERRACOTTA
    ).addTag(FluidTemperature.NORMAL);
    static {
        MERCURY.register();
    }

    public static final PylonFluid COPPER = new PylonFluid(
            baseKey("copper"),
            Material.TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        COPPER.register();
    }

    public static final PylonFluid GOLD = new PylonFluid(
            baseKey("gold"),
            Material.YELLOW_CONCRETE
    ).addTag(FluidTemperature.HOT);
    static {
        GOLD.register();
    }

    public static final PylonFluid IRON = new PylonFluid(
            baseKey("iron"),
            Material.RED_TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        IRON.register();
    }

    public static final PylonFluid TIN = new PylonFluid(
            baseKey("tin"),
            Material.GREEN_TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        TIN.register();
    }

    public static final PylonFluid COBALT = new PylonFluid(
            baseKey("cobalt"),
            Material.BLUE_TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        COBALT.register();
    }

    public static final PylonFluid NICKEL = new PylonFluid(
            baseKey("nickel"),
            Material.WHITE_TERRACOTTA
    ).addTag(FluidTemperature.HOT);
    static {
        NICKEL.register();
    }

    public static final PylonFluid BRONZE = new PylonFluid(
            baseKey("bronze"),
            Material.BROWN_CONCRETE
    ).addTag(FluidTemperature.HOT);
    static {
        BRONZE.register();
    }

    public static final PylonFluid STEEL = new PylonFluid(
            baseKey("steel"),
            Material.GRAY_CONCRETE
    ).addTag(FluidTemperature.HOT);
    static {
        STEEL.register();
    }

    public static final PylonFluid SLURRY = new PylonFluid(
            baseKey("slurry"),
            Material.LIGHT_GRAY_CONCRETE
    ).addTag(FluidTemperature.NORMAL);
    static {
        SLURRY.register();
    }

    public static final PylonFluid COAL_SLURRY = new Slurry(
            baseKey("slurry_coal"),
            BaseItems.COAL_DUST
    ).addTag(FluidTemperature.NORMAL);
    static {
        COAL_SLURRY.register();
    }

    public static final PylonFluid CARBON_SLURRY = new Slurry(
            baseKey("slurry_carbon"),
            BaseItems.CARBON
    ).addTag(FluidTemperature.NORMAL);
    static {
        CARBON_SLURRY.register();
    }

    public static final PylonFluid SPONGE_IRON_SLURRY = new Slurry(
            baseKey("slurry_sponge_iron"),
            BaseItems.SPONGE_IRON
    ).addTag(FluidTemperature.NORMAL);
    static {
        SPONGE_IRON_SLURRY.register();
    }

    public static final PylonFluid RAW_COPPER_SLURRY = new Slurry(
            baseKey("slurry_raw_copper"),
            BaseItems.CRUSHED_RAW_COPPER
    ).addTag(FluidTemperature.NORMAL);
    static {
        RAW_COPPER_SLURRY.register();
    }

    public static final PylonFluid RAW_GOLD_SLURRY = new Slurry(
            baseKey("slurry_raw_gold"),
            BaseItems.CRUSHED_RAW_GOLD
    ).addTag(FluidTemperature.NORMAL);
    static {
        RAW_GOLD_SLURRY.register();
    }

    public static final PylonFluid RAW_IRON_SLURRY = new Slurry(
            baseKey("slurry_raw_iron"),
            BaseItems.CRUSHED_RAW_IRON
    ).addTag(FluidTemperature.NORMAL);
    static {
        RAW_IRON_SLURRY.register();
    }

    public static final PylonFluid RAW_TIN_SLURRY = new Slurry(
            baseKey("slurry_raw_tin"),
            BaseItems.CRUSHED_RAW_TIN
    ).addTag(FluidTemperature.NORMAL);
    static {
        RAW_TIN_SLURRY.register();
    }

    public static final PylonFluid REDSTONE_SLURRY = new Slurry(
            baseKey("slurry_redstone"),
            new ItemStack(Material.REDSTONE)
    ).addTag(FluidTemperature.NORMAL);
    static {
        REDSTONE_SLURRY.register();
    }

    public static final PylonFluid PLANT_OIL = new PylonFluid(
            baseKey("plant_oil"),
            Material.YELLOW_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);
    static {
        PLANT_OIL.register();
    }

    public static final PylonFluid HYDRAULIC_FLUID = new PylonFluid(
            baseKey("hydraulic_fluid"),
            Material.BLUE_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);
    static {
        HYDRAULIC_FLUID.register();
    }


    public static final PylonFluid DIRTY_HYDRAULIC_FLUID = new PylonFluid(
            baseKey("dirty_hydraulic_fluid"),
            Material.BROWN_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);
    static {
        DIRTY_HYDRAULIC_FLUID.register();
    }

    public static final PylonFluid REFLECTOR_FLUID = new PylonFluid(
            baseKey("reflector_fluid"),
            Material.WHITE_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);
    static {
        REFLECTOR_FLUID.register();
    }

    public static final PylonFluid BIODIESEL = new PylonFluid(
            baseKey("biodiesel"),
            Material.YELLOW_CONCRETE
    ).addTag(FluidTemperature.NORMAL);
    static {
        BIODIESEL.register();
    }

    /**
     * Calling this function will run the static blocks
     */
    static void initialize() {
    }
}
