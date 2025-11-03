package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.machines.smelting.Slurry;
import io.github.pylonmc.pylon.base.recipes.CastingRecipe;
import io.github.pylonmc.pylon.base.recipes.MeltingRecipe;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

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

    public static final PylonFluid OBSCYRA = new PylonFluid(
            baseKey("obscyra"),
            Material.BLACK_CONCRETE
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid SULFUR = new PylonFluid(
            baseKey("sulfur"),
            Material.YELLOW_TERRACOTTA
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid MERCURY = new PylonFluid(
            baseKey("mercury"),
            Material.CYAN_TERRACOTTA
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid COPPER = new PylonFluid(
            baseKey("copper"),
            Material.TERRACOTTA
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid GOLD = new PylonFluid(
            baseKey("gold"),
            Material.YELLOW_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid IRON = new PylonFluid(
            baseKey("iron"),
            Material.RED_TERRACOTTA
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid TIN = new PylonFluid(
            baseKey("tin"),
            Material.GREEN_TERRACOTTA
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid COBALT = new PylonFluid(
            baseKey("cobalt"),
            Material.BLUE_TERRACOTTA
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid NICKEL = new PylonFluid(
            baseKey("nickel"),
            Material.WHITE_TERRACOTTA
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid BRONZE = new PylonFluid(
            baseKey("bronze"),
            Material.BROWN_CONCRETE
    ).addTag(FluidTemperature.HOT);

    public static final PylonFluid STEEL = new PylonFluid(
            baseKey("steel"),
            Material.GRAY_CONCRETE
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
            BaseItems.CARBON
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

    public static final PylonFluid RAW_TIN_SLURRY = new Slurry(
            baseKey("slurry_raw_tin"),
            BaseItems.CRUSHED_RAW_TIN
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

    public static final PylonFluid REFLECTOR_FLUID = new PylonFluid(
            baseKey("reflector_fluid"),
            Material.WHITE_CONCRETE_POWDER
    ).addTag(FluidTemperature.NORMAL);

    public static final PylonFluid BIODIESEL = new PylonFluid(
            baseKey("biodiesel"),
            Material.YELLOW_CONCRETE
    ).addTag(FluidTemperature.NORMAL);

    // TODO refactor into static blocks as in BaseItems
    public static void initialize() {
        WATER.register();

        LAVA.register();

        OBSCYRA.register();

        SULFUR.register();

        MERCURY.register();

        COPPER.register();
        addMetalRecipes(
                COPPER,
                1083,
                new ItemStack(Material.COPPER_INGOT),
                BaseItems.COPPER_DUST,
                null,
                new ItemStack(Material.COPPER_BLOCK)
        );

        GOLD.register();
        addMetalRecipes(
                GOLD,
                1064,
                new ItemStack(Material.GOLD_INGOT),
                BaseItems.GOLD_DUST,
                null,
                new ItemStack(Material.GOLD_BLOCK)
        );

        IRON.register();
        addMetalRecipes(
                IRON,
                1064,
                new ItemStack(Material.IRON_INGOT),
                BaseItems.IRON_DUST,
                new ItemStack(Material.IRON_NUGGET),
                new ItemStack(Material.IRON_BLOCK)
        );

        TIN.register();
        addMetalRecipes(
                TIN,
                231.9,
                BaseItems.TIN_INGOT,
                BaseItems.TIN_DUST,
                BaseItems.TIN_NUGGET,
                BaseItems.TIN_BLOCK
        );

        COBALT.register();
        addMetalRecipes(
                COBALT,
                1495,
                BaseItems.COBALT_INGOT,
                BaseItems.COBALT_DUST,
                BaseItems.COBALT_NUGGET,
                BaseItems.COBALT_BLOCK
        );

        NICKEL.register();
        addMetalRecipes(
                NICKEL,
                1455,
                BaseItems.NICKEL_INGOT,
                BaseItems.NICKEL_DUST,
                BaseItems.NICKEL_NUGGET,
                BaseItems.NICKEL_BLOCK
        );

        BRONZE.register();
        addMetalRecipes(
                BRONZE,
                950,
                BaseItems.BRONZE_INGOT,
                BaseItems.BRONZE_DUST,
                BaseItems.BRONZE_NUGGET,
                BaseItems.BRONZE_BLOCK
        );

        STEEL.register();
        addMetalRecipes(
                STEEL,
                950,
                BaseItems.STEEL_INGOT,
                BaseItems.STEEL_DUST,
                BaseItems.STEEL_NUGGET,
                BaseItems.STEEL_BLOCK
        );

        SLURRY.register();

        COAL_SLURRY.register();

        CARBON_SLURRY.register();

        RAW_COPPER_SLURRY.register();

        RAW_GOLD_SLURRY.register();

        RAW_IRON_SLURRY.register();

        RAW_TIN_SLURRY.register();

        REDSTONE_SLURRY.register();

        PLANT_OIL.register();

        HYDRAULIC_FLUID.register();

        DIRTY_HYDRAULIC_FLUID.register();

        REFLECTOR_FLUID.register();

        BIODIESEL.register();
    }

    private static void addMetalRecipes(
            PylonFluid fluid,
            double temperature,
            ItemStack ingot,
            ItemStack dust,
            @Nullable ItemStack nugget,
            ItemStack block
    ) {
        CastingRecipe.RECIPE_TYPE.addRecipe(new CastingRecipe(
                NamespacedKey.fromString(fluid.getKey() + "_to_ingot"),
                RecipeInput.of(fluid, 144.0),
                ingot,
                temperature
        ));

        MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                NamespacedKey.fromString(fluid.getKey() + "_from_ingot"),
                RecipeInput.of(ingot),
                fluid,
                144.0,
                temperature
        ));

        MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                NamespacedKey.fromString(fluid.getKey() + "_from_dust"),
                RecipeInput.of(dust),
                fluid,
                144.0,
                temperature
        ));

        if (nugget != null) {
            MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                    NamespacedKey.fromString(fluid.getKey() + "_from_nugget"),
                    RecipeInput.of(nugget),
                    fluid,
                    16.0,
                    temperature
            ));
        }

        MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                NamespacedKey.fromString(fluid.getKey() + "_from_block"),
                RecipeInput.of(block),
                fluid,
                1296.0,
                temperature
        ));
    }
}
