package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.machines.smelting.Slurry;
import io.github.pylonmc.pylon.base.recipes.CastingRecipe;
import io.github.pylonmc.pylon.base.recipes.MeltingRecipe;
import io.github.pylonmc.pylon.base.recipes.MixingPotRecipe;
import io.github.pylonmc.pylon.base.recipes.SmelteryRecipe;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

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

    public static final PylonFluid SILVER = new PylonFluid(
            baseKey("silver"),
            Material.WHITE_CONCRETE
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

    // TODO refactor into static blocks as in BaseItems
    public static void initialize() {
        WATER.register();

        LAVA.register();

        OBSCYRA.register();
        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                OBSCYRA.getKey(),
                List.of(BaseItems.OBSIDIAN_CHIP.asQuantity(5), new ItemStack(Material.BLAZE_POWDER, 2)),
                WATER,
                1000,
                FluidOrItem.of(OBSCYRA, 1000),
                true
        ));

        SULFUR.register();
        MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                SULFUR.getKey(),
                BaseItems.SULFUR,
                SULFUR,
                144.0,
                112.8
        ));

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

        SmelteryRecipe.RECIPE_TYPE.addRecipe(new SmelteryRecipe(
                baseKey("bronze"),
                Map.of(
                        COPPER, 1.0 - 0.12,
                        TIN, 0.12
                ),
                Map.of(BRONZE, 1.0),
                950
        ));

        STEEL.register();
        addMetalRecipes(
                STEEL,
                950,
                BaseItems.STEEL_INGOT,
                BaseItems.STEEL_DUST,
                BaseItems.STEEL_NUGGET,
                BaseItems.STEEL_BLOCK
        );

        SmelteryRecipe.RECIPE_TYPE.addRecipe(new SmelteryRecipe(
                baseKey("steel"),
                Map.of(
                        IRON, 1.0 - 0.04,
                        CARBON_SLURRY, 0.04
                ),
                Map.of(STEEL, 1.0),
                1540
        ));

        SmelteryRecipe.RECIPE_TYPE.addRecipe(new SmelteryRecipe(
                baseKey("decarburization"), // yes this is a real word
                Map.of(
                        STEEL, 1.0,
                        WATER, 0.1
                ),
                Map.of(IRON, 1.0),
                1540
        ));

        SLURRY.register();
        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                SLURRY.getKey(),
                List.of(BaseItems.ROCK_DUST),
                WATER,
                1000,
                FluidOrItem.of(SLURRY, 1000),
                false
        ));

        COAL_SLURRY.register();
        SmelteryRecipe.RECIPE_TYPE.addRecipe(new SmelteryRecipe(
                baseKey("coal_to_carbon"),
                Map.of(COAL_SLURRY, 1.0),
                Map.of(
                        CARBON_SLURRY, 0.9,
                        SLURRY, 0.1
                ),
                1000
        ));

        CARBON_SLURRY.register();

        RAW_COPPER_SLURRY.register();
        SmelteryRecipe.RECIPE_TYPE.addRecipe(new SmelteryRecipe(
                baseKey("copper_smelting"),
                Map.of(RAW_COPPER_SLURRY, 1.0),
                Map.of(
                        COPPER, 0.5,
                        SLURRY, 0.5
                ),
                1085
        ));

        RAW_GOLD_SLURRY.register();
        SmelteryRecipe.RECIPE_TYPE.addRecipe(new SmelteryRecipe(
                baseKey("gold_smelting"),
                Map.of(
                        RAW_GOLD_SLURRY, 1.0,
                        MERCURY, 1.0
                ),
                Map.of(
                        GOLD, 0.5,
                        SLURRY, 0.3,
                        SILVER, 0.15,
                        RAW_TIN_SLURRY, 0.15,
                        MERCURY, 0.9
                ),
                1064
        ));

        RAW_IRON_SLURRY.register();
        SmelteryRecipe.RECIPE_TYPE.addRecipe(new SmelteryRecipe(
                baseKey("iron_smelting"),
                Map.of(
                        RAW_IRON_SLURRY, 1.0,
                        CARBON_SLURRY, 0.5
                ),
                Map.of(
                        IRON, 1.0,
                        SLURRY, 0.5
                ),
                1540
        ));
        SmelteryRecipe.RECIPE_TYPE.addRecipe(new SmelteryRecipe(
                baseKey("iron_smelting_with_sulfur"),
                Map.of(
                        RAW_IRON_SLURRY, 1.0,
                        CARBON_SLURRY, 0.5,
                        SULFUR, 0.1
                ),
                Map.of(
                        IRON, 1.0,
                        SLURRY, 0.4,
                        COBALT, 0.1,
                        NICKEL, 0.1
                ),
                1540
        ));

        RAW_TIN_SLURRY.register();
        SmelteryRecipe.RECIPE_TYPE.addRecipe(new SmelteryRecipe(
                baseKey("tin_smelting"),
                Map.of(
                        RAW_TIN_SLURRY, 1.0,
                        CARBON_SLURRY, 0.5
                ),
                Map.of(
                        TIN, 1.0,
                        SLURRY, 0.5
                ),
                250
        ));

        REDSTONE_SLURRY.register();
        SmelteryRecipe.RECIPE_TYPE.addRecipe(new SmelteryRecipe(
                baseKey("redstone_decomposition"),
                Map.of(REDSTONE_SLURRY, 1.0),
                Map.of(
                        SULFUR, 0.25,
                        MERCURY, 0.25,
                        SLURRY, 0.5
                ),
                345
        ));

        PLANT_OIL.register();

        HYDRAULIC_FLUID.register();
        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                HYDRAULIC_FLUID.getKey(),
                List.of(BaseItems.SHIMMER_DUST_1),
                PLANT_OIL,
                1000,
                FluidOrItem.of(HYDRAULIC_FLUID, 1000),
                false
        ));

        DIRTY_HYDRAULIC_FLUID.register();

        REFLECTOR_FLUID.register();
        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                REFLECTOR_FLUID.getKey(),
                List.of(BaseItems.QUARTZ_DUST.asQuantity(4)),
                PLANT_OIL,
                1000,
                FluidOrItem.of(REFLECTOR_FLUID, 1000),
                false
        ));
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
                fluid.getKey(),
                fluid,
                144.0,
                ingot,
                temperature
        ));

        MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                fluid.getKey(),
                ingot,
                fluid,
                144.0,
                temperature
        ));

        MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                fluid.getKey(),
                dust,
                fluid,
                144.0,
                temperature
        ));

        if (nugget != null) {
            MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                    fluid.getKey(),
                    nugget,
                    fluid,
                    16.0,
                    temperature
            ));
        }

        MeltingRecipe.RECIPE_TYPE.addRecipe(new MeltingRecipe(
                fluid.getKey(),
                block,
                fluid,
                1296.0,
                temperature
        ));
    }
}
