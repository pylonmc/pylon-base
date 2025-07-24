package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.building.DimensionalBarrel;
import io.github.pylonmc.pylon.base.content.building.Elevator;
import io.github.pylonmc.pylon.base.content.building.ExplosiveTarget;
import io.github.pylonmc.pylon.base.content.building.Immobilizer;
import io.github.pylonmc.pylon.base.content.combat.BeheadingSword;
import io.github.pylonmc.pylon.base.content.combat.IceArrow;
import io.github.pylonmc.pylon.base.content.combat.RecoilArrow;
import io.github.pylonmc.pylon.base.content.machines.fluid.*;
import io.github.pylonmc.pylon.base.content.machines.hydraulics.*;
import io.github.pylonmc.pylon.base.content.machines.simple.*;
import io.github.pylonmc.pylon.base.content.science.Loupe;
import io.github.pylonmc.pylon.base.content.science.ResearchPack;
import io.github.pylonmc.pylon.base.content.tools.*;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


@SuppressWarnings({"UnstableApiUsage", "OverlyComplexClass"})
public final class BaseItems {

    private BaseItems() {
        throw new AssertionError("Utility class");
    }

    //<editor-fold desc="Dusts" defaultstate=collapsed>
    public static final ItemStack COPPER_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.COPPER_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, COPPER_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.COPPER_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.COPPER_DUST,
                new ItemStack(Material.COPPER_INGOT),
                COPPER_DUST,
                2,
                Material.COPPER_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack CRUSHED_RAW_COPPER = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.CRUSHED_RAW_COPPER)
            .build();

    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_COPPER);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_COPPER);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.CRUSHED_RAW_COPPER,
                new ItemStack(Material.RAW_COPPER),
                CRUSHED_RAW_COPPER,
                2,
                Material.RAW_COPPER_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack GOLD_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.GOLD_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, GOLD_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.GOLD_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.GOLD_DUST,
                new ItemStack(Material.GOLD_INGOT),
                GOLD_DUST,
                2,
                Material.GOLD_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack CRUSHED_RAW_GOLD = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.CRUSHED_RAW_GOLD)
            .build();

    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_GOLD);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_GOLD);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.CRUSHED_RAW_GOLD,
                new ItemStack(Material.RAW_GOLD),
                CRUSHED_RAW_COPPER,
                2,
                Material.RAW_GOLD_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack IRON_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.IRON_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, IRON_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.IRON_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.IRON_DUST,
                new ItemStack(Material.IRON_INGOT),
                IRON_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack QUARTZ_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.QUARTZ_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, QUARTZ_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.QUARTZ_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.QUARTZ_DUST,
                new ItemStack(Material.QUARTZ),
                QUARTZ_DUST,
                4,
                Material.QUARTZ_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack DIAMOND_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.DIAMOND_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, DIAMOND_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.DIAMOND_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.DIAMOND_DUST,
                new ItemStack(Material.DIAMOND),
                DIAMOND_DUST,
                6,
                Material.DIAMOND_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack EMERALD_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.EMERALD_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, EMERALD_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.EMERALD_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.EMERALD_DUST,
                new ItemStack(Material.EMERALD),
                EMERALD_DUST,
                6,
                Material.EMERALD_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack CRUSHED_RAW_IRON = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.CRUSHED_RAW_IRON)
            .build();

    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_IRON);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_IRON);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.CRUSHED_RAW_IRON,
                new ItemStack(Material.RAW_IRON),
                CRUSHED_RAW_IRON,
                2,
                Material.RAW_IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack SILVER_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.SILVER_INGOT)
            .build();

    static {
        PylonItem.register(PylonItem.class, SILVER_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.SILVER_INGOT);
    }

    public static final ItemStack SILVER_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.SILVER_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, SILVER_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.SILVER_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.SILVER_DUST,
                SILVER_INGOT,
                SILVER_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack ZINC_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.ZINC_INGOT)
            .build();

    static {
        PylonItem.register(PylonItem.class, ZINC_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.ZINC_INGOT);
    }

    public static final ItemStack CRUSHED_RAW_ZINC = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.CRUSHED_RAW_ZINC)
            .build();

    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_ZINC);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_ZINC);
    }

    public static final ItemStack ZINC_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.ZINC_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, ZINC_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.ZINC_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.ZINC_DUST,
                ZINC_INGOT,
                ZINC_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack ROCK_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.ROCK_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, ROCK_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.ROCK_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.ROCK_DUST,
                new ItemStack(Material.COBBLESTONE),
                ROCK_DUST.asQuantity(2),
                2,
                Material.COBBLESTONE.createBlockData()
        ));
    }

    public static final ItemStack LEAD_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.LEAD_INGOT)
            .build();

    static {
        PylonItem.register(PylonItem.class, LEAD_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.LEAD_INGOT);
    }

    public static final ItemStack CRUSHED_RAW_LEAD = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.CRUSHED_RAW_LEAD)
            .build();

    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_LEAD);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_LEAD);
    }

    public static final ItemStack LEAD_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.LEAD_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, LEAD_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.LEAD_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.LEAD_DUST,
                LEAD_INGOT,
                LEAD_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack TIN_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.TIN_INGOT)
            .build();

    static {
        PylonItem.register(PylonItem.class, TIN_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.TIN_INGOT);
    }

    public static final ItemStack CRUSHED_RAW_TIN = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.CRUSHED_RAW_TIN)
            .build();

    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_TIN);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_TIN);
    }

    public static final ItemStack TIN_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.TIN_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, TIN_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.TIN_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.TIN_DUST,
                TIN_INGOT,
                TIN_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack COBALT_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.COBALT_INGOT)
            .build();

    static {
        PylonItem.register(PylonItem.class, COBALT_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.COBALT_INGOT);
    }

    public static final ItemStack COBALT_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.COBALT_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, COBALT_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.COBALT_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.COBALT_DUST,
                COBALT_INGOT,
                COBALT_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack NICKEL_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.NICKEL_INGOT)
            .build();

    static {
        PylonItem.register(PylonItem.class, NICKEL_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.NICKEL_INGOT);
    }

    public static final ItemStack NICKEL_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.NICKEL_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, NICKEL_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.NICKEL_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.NICKEL_DUST,
                NICKEL_INGOT,
                NICKEL_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack COAL_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.COAL_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, COAL_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.COAL_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                baseKey("coal_dust_from_coal"),
                new ItemStack(Material.COAL),
                COAL_DUST,
                2,
                Material.COAL_BLOCK.createBlockData()
        ));

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                baseKey("coal_dust_from_charcoal"),
                new ItemStack(Material.CHARCOAL),
                COAL_DUST,
                2,
                Material.COAL_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack CARBON_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.CARBON_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, CARBON_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.CARBON_DUST);
    }

    // Not technically a dust but whatever
    public static final ItemStack SULFUR = ItemStackBuilder.pylonItem(Material.YELLOW_DYE, BaseKeys.SULFUR)
            .build();

    static {
        PylonItem.register(PylonItem.class, SULFUR);
        BasePages.RESOURCES.addItem(BaseKeys.SULFUR);
    }

    public static final ItemStack BRONZE_INGOT = ItemStackBuilder.pylonItem(Material.GOLD_INGOT, BaseKeys.BRONZE_INGOT)
            .build();

    static {
        PylonItem.register(PylonItem.class, BRONZE_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.BRONZE_INGOT);
    }

    public static final ItemStack BRONZE_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.BRONZE_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, BRONZE_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.BRONZE_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.BRONZE_DUST,
                BRONZE_INGOT,
                BRONZE_DUST,
                2,
                Material.GOLD_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack BRASS_INGOT = ItemStackBuilder.pylonItem(Material.GOLD_INGOT, BaseKeys.BRASS_INGOT)
            .build();

    static {
        PylonItem.register(PylonItem.class, BRASS_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.BRASS_INGOT);
    }

    public static final ItemStack BRASS_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.BRASS_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, BRASS_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.BRASS_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.BRASS_DUST,
                BRASS_INGOT,
                BRASS_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack STEEL_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.STEEL_INGOT)
            .build();

    static {
        PylonItem.register(PylonItem.class, STEEL_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.STEEL_INGOT);
    }

    public static final ItemStack STEEL_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.STEEL_DUST)
            .build();

    static {
        PylonItem.register(PylonItem.class, STEEL_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.STEEL_DUST);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.STEEL_DUST,
                STEEL_INGOT,
                STEEL_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }
    // </editor-fold>

    //<editor-fold desc="Sheets" defaultstate=collapsed>
    public static final ItemStack COPPER_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.COPPER_SHEET)
            .build();

    static {
        PylonItem.register(PylonItem.class, COPPER_SHEET);
        BasePages.RESOURCES.addItem(BaseKeys.COPPER_SHEET);

        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                BaseKeys.COPPER_SHEET,
                new ItemStack(Material.COPPER_INGOT),
                COPPER_SHEET,
                MiningLevel.STONE,
                0.25f
        ));
    }

    public static final ItemStack GOLD_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.GOLD_SHEET)
            .build();

    static {
        PylonItem.register(PylonItem.class, GOLD_SHEET);
        BasePages.COMPONENTS.addItem(BaseKeys.GOLD_SHEET);

        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                BaseKeys.GOLD_SHEET,
                new ItemStack(Material.GOLD_INGOT),
                GOLD_SHEET,
                MiningLevel.STONE,
                0.25f
        ));
    }

    public static final ItemStack IRON_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.IRON_SHEET)
            .build();

    static {
        PylonItem.register(PylonItem.class, IRON_SHEET);
        BasePages.COMPONENTS.addItem(BaseKeys.IRON_SHEET);

        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                BaseKeys.IRON_SHEET,
                new ItemStack(Material.IRON_INGOT),
                IRON_SHEET,
                MiningLevel.IRON,
                0.25f
        ));
    }
    //</editor-fold>

    //<editor-fold desc="Hammers" defaultstate=collapsed>
    public static final ItemStack HAMMER_STONE = Hammer.createItemStack(
            BaseKeys.HAMMER_STONE, Material.STONE_PICKAXE, (1.0 / 3) - 4, 1, 1
    );

    static {
        PylonItem.register(Hammer.class, HAMMER_STONE);
        BasePages.TOOLS.addItem(BaseKeys.HAMMER_STONE);

        RecipeType.VANILLA_SHAPED.addRecipe(Hammer.getRecipe(
                BaseKeys.HAMMER_STONE,
                HAMMER_STONE,
                Material.COBBLESTONE
        ));
    }

    public static final ItemStack HAMMER_IRON = Hammer.createItemStack(
            BaseKeys.HAMMER_IRON, Material.IRON_PICKAXE, (1.0 / 2) - 4, 1.5, 3
    );

    static {
        PylonItem.register(Hammer.class, HAMMER_IRON);
        BasePages.TOOLS.addItem(BaseKeys.HAMMER_IRON);

        RecipeType.VANILLA_SHAPED.addRecipe(Hammer.getRecipe(
                BaseKeys.HAMMER_IRON,
                HAMMER_IRON,
                Material.IRON_INGOT
        ));
    }

    public static final ItemStack HAMMER_DIAMOND = Hammer.createItemStack(
            BaseKeys.HAMMER_DIAMOND, Material.DIAMOND_PICKAXE, (1.0 / 1) - 4, 2, 5
    );

    static {
        PylonItem.register(Hammer.class, HAMMER_DIAMOND);
        BasePages.TOOLS.addItem(BaseKeys.HAMMER_DIAMOND);

        RecipeType.VANILLA_SHAPED.addRecipe(Hammer.getRecipe(
                BaseKeys.HAMMER_DIAMOND,
                HAMMER_DIAMOND,
                Material.DIAMOND
        ));
    }
    //</editor-fold>

    // <editor-fold desc="Misc" defaultstate=collapsed>
    public static final ItemStack WATERING_CAN = ItemStackBuilder.pylonItem(Material.BUCKET, BaseKeys.WATERING_CAN)
            .build();

    static {
        PylonItem.register(WateringCan.class, WATERING_CAN);
        BasePages.TOOLS.addItem(BaseKeys.WATERING_CAN);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.WATERING_CAN, WATERING_CAN)
                .shape("  S", "S S", " S ")
                .setIngredient('S', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack MONSTER_JERKY = ItemStackBuilder.pylonItem(Material.ROTTEN_FLESH, BaseKeys.MONSTER_JERKY)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable().build())
            .set(DataComponentTypes.FOOD, FoodProperties.food()
                    .canAlwaysEat(false)
                    .nutrition(Settings.get(BaseKeys.MONSTER_JERKY).getOrThrow("nutrition", Integer.class))
                    .saturation(Settings.get(BaseKeys.MONSTER_JERKY).getOrThrow("saturation", Double.class).floatValue())
                    .build()
            )
            .build();

    static {
        PylonItem.register(PylonItem.class, MONSTER_JERKY);
        BasePages.FOOD.addItem(BaseKeys.MONSTER_JERKY);

        float cookingXp = Settings.get(BaseKeys.MONSTER_JERKY).getOrThrow("cooking.xp", Double.class).floatValue();

        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                baseKey("monster_jerky_furnace"),
                MONSTER_JERKY,
                Material.ROTTEN_FLESH,
                cookingXp,
                BaseUtils.DEFAULT_FURNACE_TIME_TICKS
        );
        furnaceRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeType.VANILLA_FURNACE.addRecipe(furnaceRecipe);

        SmokingRecipe smokingRecipe = new SmokingRecipe(
                baseKey("monster_jerky_smoker"),
                MONSTER_JERKY,
                Material.ROTTEN_FLESH,
                cookingXp,
                BaseUtils.DEFAULT_SMOKER_TIME_TICKS
        );
        smokingRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeType.VANILLA_SMOKING.addRecipe(smokingRecipe);

        ShapedRecipe leatherRecipe = new ShapedRecipe(baseKey("leather"), new ItemStack(Material.LEATHER))
                .shape("RR ", "RR ", "   ")
                .setIngredient('R', MONSTER_JERKY);
        leatherRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(leatherRecipe);
    }

    //<editor-fold desc="Ferroduralum" defaultstate=collapsed>
    public static final ItemStack RAW_FERRODURALUM = ItemStackBuilder.pylonItem(Material.RAW_GOLD, BaseKeys.RAW_FERRODURALUM)
            .build();

    static {
        PylonItem.register(PylonItem.class, RAW_FERRODURALUM);
        BasePages.RESOURCES.addItem(BaseKeys.RAW_FERRODURALUM);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.RAW_FERRODURALUM, RAW_FERRODURALUM)
                .shape("CGR", "   ", "   ")
                .setIngredient('C', Material.COPPER_ORE)
                .setIngredient('G', Material.GOLD_ORE)
                .setIngredient('R', Material.REDSTONE);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FERRODURALUM_INGOT = ItemStackBuilder.pylonItem(Material.GOLD_INGOT, BaseKeys.FERRODURALUM_INGOT)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.FERRODURALUM_INGOT);

        float cookingXp = Settings.get(BaseKeys.FERRODURALUM_INGOT)
                .getOrThrow("cooking.xp", Double.class)
                .floatValue();

        FurnaceRecipe recipe = new FurnaceRecipe(
                BaseKeys.FERRODURALUM_INGOT,
                FERRODURALUM_INGOT,
                new RecipeChoice.ExactChoice(RAW_FERRODURALUM),
                cookingXp,
                BaseUtils.DEFAULT_FURNACE_TIME_TICKS
        );
        recipe.setCategory(CookingBookCategory.MISC);
        RecipeType.VANILLA_FURNACE.addRecipe(recipe);
    }

    public static final ItemStack FERRODURALUM_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.FERRODURALUM_SHEET)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_SHEET);
        BasePages.COMPONENTS.addItem(BaseKeys.FERRODURALUM_SHEET);

        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                BaseKeys.FERRODURALUM_SHEET,
                FERRODURALUM_INGOT,
                FERRODURALUM_SHEET,
                MiningLevel.IRON,
                0.25f
        ));
    }

    public static final ItemStack FERRODURALUM_SWORD = ItemStackBuilder.pylonItem(Material.GOLDEN_SWORD, BaseKeys.FERRODURALUM_SWORD)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                            baseKey("ferroduralum_sword_damage"),
                            0.15,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 300)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_SWORD);
        BasePages.COMBAT.addItem(BaseKeys.FERRODURALUM_SWORD);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FERRODURALUM_SWORD, FERRODURALUM_SWORD)
                .shape(" F ", " F ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FERRODURALUM_AXE = ItemStackBuilder.pylonItem(Material.GOLDEN_AXE, BaseKeys.FERRODURALUM_AXE)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                            baseKey("ferroduralum_axe_speed"),
                            0.15,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 300)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_AXE);
        BasePages.TOOLS.addItem(BaseKeys.FERRODURALUM_AXE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FERRODURALUM_AXE, FERRODURALUM_AXE)
                .shape("FF ", "FS ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
        RecipeType.VANILLA_SHAPED.addRecipe(BaseUtils.reflectRecipe(recipe));
    }

    public static final ItemStack FERRODURALUM_PICKAXE = ItemStackBuilder.pylonItem(Material.GOLDEN_PICKAXE, BaseKeys.FERRODURALUM_PICKAXE)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                            baseKey("ferroduralum_pickaxe_speed"),
                            0.15,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 300)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_PICKAXE);
        BasePages.TOOLS.addItem(BaseKeys.FERRODURALUM_PICKAXE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FERRODURALUM_PICKAXE, FERRODURALUM_PICKAXE)
                .shape("FFF", " S ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FERRODURALUM_SHOVEL = ItemStackBuilder.pylonItem(Material.GOLDEN_SHOVEL, BaseKeys.FERRODURALUM_SHOVEL)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                            baseKey("ferroduralum_shovel_speed"),
                            0.15,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 300)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_SHOVEL);
        BasePages.TOOLS.addItem(BaseKeys.FERRODURALUM_SHOVEL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FERRODURALUM_SHOVEL, FERRODURALUM_SHOVEL)
                .shape(" F ", " S ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FERRODURALUM_HOE = ItemStackBuilder.pylonItem(Material.GOLDEN_HOE, BaseKeys.FERRODURALUM_HOE)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                            baseKey("ferroduralum_hoe_speed"),
                            0.15,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 300)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_HOE);
        BasePages.TOOLS.addItem(BaseKeys.FERRODURALUM_HOE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FERRODURALUM_HOE, FERRODURALUM_HOE)
                .shape("FF ", " S ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
        RecipeType.VANILLA_SHAPED.addRecipe(BaseUtils.reflectRecipe(recipe));
    }

    public static final ItemStack FERRODURALUM_HELMET = ItemStackBuilder.pylonItem(Material.GOLDEN_HELMET, BaseKeys.FERRODURALUM_HELMET)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            baseKey("ferroduralum_helmet_armor"),
                            2.5,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.HEAD
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            baseKey("ferroduralum_helmet_toughness"),
                            1,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.HEAD
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 190)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_HELMET);
        BasePages.ARMOUR.addItem(BaseKeys.FERRODURALUM_HELMET);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FERRODURALUM_HELMET, FERRODURALUM_HELMET)
                .shape("FFF", "F F", "   ")
                .setIngredient('F', FERRODURALUM_INGOT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FERRODURALUM_CHESTPLATE = ItemStackBuilder.pylonItem(Material.GOLDEN_CHESTPLATE, BaseKeys.FERRODURALUM_CHESTPLATE)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            baseKey("ferroduralum_chestplate_armor"),
                            7,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.CHEST
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            baseKey("ferroduralum_chestplate_toughness"),
                            1,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.CHEST
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 276)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_CHESTPLATE);
        BasePages.ARMOUR.addItem(BaseKeys.FERRODURALUM_CHESTPLATE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FERRODURALUM_CHESTPLATE, FERRODURALUM_CHESTPLATE)
                .shape("F F", "FFF", "FFF")
                .setIngredient('F', FERRODURALUM_INGOT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FERRODURALUM_LEGGINGS = ItemStackBuilder.pylonItem(Material.GOLDEN_LEGGINGS, BaseKeys.FERRODURALUM_LEGGINGS)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            baseKey("ferroduralum_leggings_armor"),
                            5.5,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.LEGS
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            baseKey("ferroduralum_leggings_toughness"),
                            1,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.LEGS
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 259)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_LEGGINGS);
        BasePages.ARMOUR.addItem(BaseKeys.FERRODURALUM_LEGGINGS);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FERRODURALUM_LEGGINGS, FERRODURALUM_LEGGINGS)
                .shape("FFF", "F F", "F F")
                .setIngredient('F', FERRODURALUM_INGOT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FERRODURALUM_BOOTS = ItemStackBuilder.pylonItem(Material.GOLDEN_BOOTS, BaseKeys.FERRODURALUM_BOOTS)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            baseKey("ferroduralum_boots_armor"),
                            2.5,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.FEET
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            baseKey("ferroduralum_boots_toughness"),
                            1,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.FEET
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 225)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_BOOTS);
        BasePages.ARMOUR.addItem(BaseKeys.FERRODURALUM_BOOTS);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FERRODURALUM_BOOTS, FERRODURALUM_BOOTS)
                .shape("F F", "F F", "   ")
                .setIngredient('F', FERRODURALUM_INGOT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }
    //</editor-fold>

    public static final ItemStack COMPRESSED_WOOD = ItemStackBuilder.pylonItem(Material.OAK_WOOD, BaseKeys.COMPRESSED_WOOD)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, COMPRESSED_WOOD);
        BasePages.COMPONENTS.addItem(BaseKeys.COMPRESSED_WOOD);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.COMPRESSED_WOOD, COMPRESSED_WOOD)
                .shape("WWW", "WWW", "WWW")
                .setIngredient('W', new RecipeChoice.MaterialChoice(Tag.LOGS));
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    //<editor-fold desc="Portable Items" defaultstate=collapsed>
    public static final ItemStack PORTABILITY_CATALYST = ItemStackBuilder.pylonItem(Material.AMETHYST_SHARD, BaseKeys.PORTABILITY_CATALYST)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, PORTABILITY_CATALYST);
        BasePages.COMPONENTS.addItem(BaseKeys.PORTABILITY_CATALYST);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABILITY_CATALYST, PORTABILITY_CATALYST)
                .shape("RRR", "RPR", "RRR")
                .setIngredient('R', Material.REDSTONE_BLOCK)
                .setIngredient('P', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack COMPRESSED_OBSIDIAN = ItemStackBuilder.pylonItem(Material.OBSIDIAN, BaseKeys.COMPRESSED_OBSIDIAN)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, COMPRESSED_OBSIDIAN);
        BasePages.COMPONENTS.addItem(BaseKeys.COMPRESSED_OBSIDIAN);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.COMPRESSED_OBSIDIAN, COMPRESSED_OBSIDIAN)
                .shape("OOO", "OOO", "OOO")
                .setIngredient('O', Material.OBSIDIAN);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_CRAFTING_TABLE = ItemStackBuilder.pylonItem(Material.CRAFTING_TABLE, BaseKeys.PORTABLE_CRAFTING_TABLE)
            .build();

    static {
        PylonItem.register(PortableCraftingTable.class, PORTABLE_CRAFTING_TABLE);
        BasePages.TOOLS.addItem(BaseKeys.PORTABLE_CRAFTING_TABLE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_CRAFTING_TABLE, PORTABLE_CRAFTING_TABLE)
                .shape("WWW", "WCW", "   ")
                .setIngredient('W', COMPRESSED_WOOD)
                .setIngredient('C', PORTABILITY_CATALYST);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_DUSTBIN = ItemStackBuilder.pylonItem(Material.CAULDRON, BaseKeys.PORTABLE_DUSTBIN)
            .build();

    static {
        PylonItem.register(PortableDustbin.class, PORTABLE_DUSTBIN);
        BasePages.TOOLS.addItem(BaseKeys.PORTABLE_DUSTBIN);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_DUSTBIN, PORTABLE_DUSTBIN)
                .shape("CCC", "IAI", "III")
                .setIngredient('I', IRON_SHEET)
                .setIngredient('C', Material.CACTUS)
                .setIngredient('A', PORTABILITY_CATALYST);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_ENDER_CHEST = ItemStackBuilder.pylonItem(Material.ENDER_CHEST, BaseKeys.PORTABLE_ENDER_CHEST)
            .build();

    static {
        PylonItem.register(PortableEnderChest.class, PORTABLE_ENDER_CHEST);
        BasePages.TOOLS.addItem(BaseKeys.PORTABLE_ENDER_CHEST);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_ENDER_CHEST, PORTABLE_ENDER_CHEST)
                .shape("OOO", "OEO", "OOO")
                .setIngredient('O', COMPRESSED_OBSIDIAN)
                .setIngredient('E', PORTABILITY_CATALYST);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }
    //</editor-fold>

    //<editor-fold desc="Medical items" defaultstate=collapsed>
    public static final ItemStack FIBER = ItemStackBuilder.pylonItem(Material.BAMBOO_MOSAIC, BaseKeys.FIBER)
            .build();

    static {
        PylonItem.register(PylonItem.class, FIBER);
        BasePages.COMPONENTS.addItem(BaseKeys.FIBER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FIBER, FIBER)
                .shape("SSS", "   ", "   ")
                .setIngredient('S', Material.STRING);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PLASTER = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE_SLAB, BaseKeys.PLASTER)
            .build();

    static {
        PylonItem.register(PylonItem.class, PLASTER);
        BasePages.COMPONENTS.addItem(BaseKeys.PLASTER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PLASTER, PLASTER)
                .shape("CC ", "CC ", "   ")
                .setIngredient('C', Material.CLAY);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack BANDAGE = ItemStackBuilder.pylonItem(Material.COBWEB, BaseKeys.BANDAGE)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                            new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1, true)
                    ), 1))
                    .consumeSeconds(1.25f)
                    .animation(ItemUseAnimation.BOW)
                    .hasConsumeParticles(false)
                    .build())
            .build();

    static {
        PylonItem.register(PylonItem.class, BANDAGE);
        BasePages.TOOLS.addItem(BaseKeys.BANDAGE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.BANDAGE, BANDAGE)
                .shape("FF ", "FF ", "   ")
                .setIngredient('F', FIBER);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SPLINT = ItemStackBuilder.pylonItem(Material.STICK, BaseKeys.SPLINT)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                            new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 2, true)
                    ), 1))
                    .consumeSeconds(3.0f)
                    .animation(ItemUseAnimation.BOW)
                    .hasConsumeParticles(false)
                    .build())
            .build();

    static {
        PylonItem.register(PylonItem.class, SPLINT);
        BasePages.TOOLS.addItem(BaseKeys.SPLINT);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.SPLINT, SPLINT)
                .shape("PPP", "   ", "PPP")
                .setIngredient('P', PLASTER);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack DISINFECTANT = ItemStackBuilder.pylonItem(Material.BREWER_POTTERY_SHERD, BaseKeys.DISINFECTANT)
            // Using the actual potion material doesn't let you set the name properly, gives you a
            // class string of a nonexistant potion type for some reason
            .set(DataComponentTypes.ITEM_MODEL, Material.POTION.getKey())
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .hasConsumeParticles(false)
                    .consumeSeconds(3.0f)
                    .animation(ItemUseAnimation.BOW)
                    .addEffect(ConsumeEffect.clearAllStatusEffects())
                    .build())
            .build();

    static {
        PylonItem.register(PylonItem.class, DISINFECTANT);
        BasePages.TOOLS.addItem(BaseKeys.DISINFECTANT);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.DISINFECTANT, DISINFECTANT)
                .shape("DDD", "D D", "DDD")
                .setIngredient('D', Material.DRIPSTONE_BLOCK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack MEDKIT = ItemStackBuilder.pylonItem(Material.SHULKER_SHELL, BaseKeys.MEDKIT)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .consumeSeconds(7.0f)
                    .animation(ItemUseAnimation.BOW)
                    .hasConsumeParticles(false)
                    .addEffect(ConsumeEffect.clearAllStatusEffects())
                    .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                            new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 3, true)
                    ), 1))
            )
            .build();

    static {
        PylonItem.register(PylonItem.class, MEDKIT);
        BasePages.TOOLS.addItem(BaseKeys.MEDKIT);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.MEDKIT, MEDKIT)
                .shape("PFP", "DDD", "PFP")
                .setIngredient('P', PLASTER)
                .setIngredient('D', DISINFECTANT)
                .setIngredient('F', FIBER);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }
    //</editor-fold>

    public static final ItemStack SPRINKLER = ItemStackBuilder.pylonItem(Material.FLOWER_POT, BaseKeys.SPRINKLER)
            .build();

    static {
        PylonItem.register(Sprinkler.Item.class, SPRINKLER, BaseKeys.SPRINKLER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.SPRINKLER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.SPRINKLER, SPRINKLER)
                .shape("B B", "B B", "FRF")
                .setIngredient('B', new ItemStack(Material.BRICK))
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('R', new ItemStack(Material.REPEATER));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack RECOIL_ARROW = ItemStackBuilder.pylonItem(Material.ARROW, BaseKeys.RECOIL_ARROW)
            .build();

    static {
        PylonItem.register(RecoilArrow.class, RECOIL_ARROW);
        BasePages.COMBAT.addItem(BaseKeys.RECOIL_ARROW);

        ItemStack output = RECOIL_ARROW.clone();
        output.setAmount(8);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.RECOIL_ARROW, RECOIL_ARROW)
                .shape("SSS", "SAS", "SSS")
                .setIngredient('S', Material.SLIME_BALL)
                .setIngredient('A', Material.ARROW);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack LUMBER_AXE = ItemStackBuilder.pylonItem(Material.WOODEN_AXE, BaseKeys.LUMBER_AXE)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.LUMBER_AXE).getOrThrow("durability", Integer.class))
            .build();

    static {
        PylonItem.register(LumberAxe.class, LUMBER_AXE);
        BasePages.TOOLS.addItem(BaseKeys.LUMBER_AXE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.LUMBER_AXE, LUMBER_AXE)
                .shape("WWW", "WAW", "III")
                .setIngredient('W', COMPRESSED_WOOD)
                .setIngredient('A', Material.WOODEN_AXE)
                .setIngredient('I', Material.IRON_BLOCK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack GRINDSTONE = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE_SLAB, BaseKeys.GRINDSTONE)
            .build();

    static {
        PylonItem.register(PylonItem.class, GRINDSTONE, BaseKeys.GRINDSTONE);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.GRINDSTONE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.GRINDSTONE, GRINDSTONE)
                .shape("STS", "   ", "   ")
                .setIngredient('T', new ItemStack(Material.SMOOTH_STONE))
                .setIngredient('S', new ItemStack(Material.SMOOTH_STONE_SLAB));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack GRINDSTONE_HANDLE = ItemStackBuilder.pylonItem(Material.OAK_FENCE, BaseKeys.GRINDSTONE_HANDLE)
            .build();

    static {
        PylonItem.register(PylonItem.class, GRINDSTONE_HANDLE, BaseKeys.GRINDSTONE_HANDLE);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.GRINDSTONE_HANDLE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.GRINDSTONE_HANDLE, GRINDSTONE_HANDLE)
                .shape("F  ", "F  ", "F  ")
                .setIngredient('F', new RecipeChoice.MaterialChoice(Tag.FENCES));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLOUR = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.FLOUR)
            .build();

    static {
        PylonItem.register(PylonItem.class, FLOUR);
        BasePages.RESOURCES.addItem(BaseKeys.FLOUR);

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BaseKeys.FLOUR,
                new ItemStack(Material.WHEAT, 2),
                FLOUR,
                2,
                Material.WHEAT.createBlockData(data -> {
                    Ageable ageable = (Ageable) data;
                    ageable.setAge(ageable.getMaximumAge());
                })
        ));
    }

    public static final ItemStack DOUGH = ItemStackBuilder.pylonItem(Material.YELLOW_DYE, BaseKeys.DOUGH)
            .build();

    static {
        PylonItem.register(PylonItem.class, DOUGH);
        BasePages.RESOURCES.addItem(BaseKeys.DOUGH);

        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                BaseKeys.DOUGH,
                Map.of(new RecipeChoice.ExactChoice(FLOUR), 1),
                DOUGH,
                false,
                BaseFluids.WATER,
                333
        ));

        FurnaceRecipe furnaceBreadRecipe = new FurnaceRecipe(
                baseKey("bread_from_dough_furnace"),
                new ItemStack(Material.BREAD),
                new RecipeChoice.ExactChoice(DOUGH),
                0.2F,
                10 * 20
        );
        furnaceBreadRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeType.VANILLA_FURNACE.addRecipe(furnaceBreadRecipe);

        SmokingRecipe smokerBreadRecipe = new SmokingRecipe(
                baseKey("bread_from_dough_smoker"),
                new ItemStack(Material.BREAD),
                new RecipeChoice.ExactChoice(DOUGH),
                0.2F,
                5 * 20);
        smokerBreadRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeType.VANILLA_SMOKING.addRecipe(smokerBreadRecipe);
    }

    public static final ItemStack HEALTH_TALISMAN_SIMPLE = ItemStackBuilder.pylonItem(Material.AMETHYST_SHARD, BaseKeys.HEALTH_TALISMAN_SIMPLE)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();

    static {
        PylonItem.register(HealthTalisman.class, HEALTH_TALISMAN_SIMPLE);
        BasePages.TOOLS.addItem(BaseKeys.HEALTH_TALISMAN_SIMPLE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HEALTH_TALISMAN_SIMPLE, HEALTH_TALISMAN_SIMPLE)
                .shape("GGG", "GRG", "GGG")
                .setIngredient('G', Material.GLISTERING_MELON_SLICE)
                .setIngredient('R', Material.REDSTONE);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HEALTH_TALISMAN_ADVANCED = ItemStackBuilder.pylonItem(Material.AMETHYST_CLUSTER, BaseKeys.HEALTH_TALISMAN_ADVANCED)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();

    static {
        PylonItem.register(HealthTalisman.class, HEALTH_TALISMAN_ADVANCED);
        BasePages.TOOLS.addItem(BaseKeys.HEALTH_TALISMAN_ADVANCED);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HEALTH_TALISMAN_ADVANCED, HEALTH_TALISMAN_ADVANCED)
                .shape("SSS", "SSS", "SSS")
                .setIngredient('S', HEALTH_TALISMAN_SIMPLE);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HEALTH_TALISMAN_ULTIMATE = ItemStackBuilder.pylonItem(Material.BUDDING_AMETHYST, BaseKeys.HEALTH_TALISMAN_ULTIMATE)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();

    static {
        PylonItem.register(HealthTalisman.class, HEALTH_TALISMAN_ULTIMATE);
        BasePages.TOOLS.addItem(BaseKeys.HEALTH_TALISMAN_ULTIMATE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HEALTH_TALISMAN_ULTIMATE, HEALTH_TALISMAN_ULTIMATE)
                .shape("AAA", "AAA", "AAA")
                .setIngredient('A', HEALTH_TALISMAN_ADVANCED);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack MIXING_POT = ItemStackBuilder.pylonItem(Material.CAULDRON, BaseKeys.MIXING_POT)
            .build();

    static {
        PylonItem.register(PylonItem.class, MIXING_POT, BaseKeys.MIXING_POT);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.MIXING_POT);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.MIXING_POT, MIXING_POT)
                .shape("f f", "f f", "fff")
                .setIngredient('f', FERRODURALUM_INGOT);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SHIMMER_DUST_1 = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.SHIMMER_DUST_1)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_1);
        BasePages.RESOURCES.addItem(BaseKeys.SHIMMER_DUST_1);

        ShapelessRecipe recipe = new ShapelessRecipe(BaseKeys.SHIMMER_DUST_1, SHIMMER_DUST_1)
                .addIngredient(COPPER_DUST)
                .addIngredient(Material.FLINT)
                .addIngredient(Material.CLAY_BALL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }

    public static final ItemStack COVALENT_BINDER = ItemStackBuilder.pylonItem(Material.LIGHT_BLUE_DYE, BaseKeys.COVALENT_BINDER)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, COVALENT_BINDER);
        BasePages.RESOURCES.addItem(BaseKeys.COVALENT_BINDER);
        ItemStack output = COVALENT_BINDER.clone();
        output.setAmount(6);
        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                BaseKeys.COVALENT_BINDER,
                Map.of(
                        new RecipeChoice.ExactChoice(new ItemStack(Material.GUNPOWDER)), 4,
                        new RecipeChoice.ExactChoice(new ItemStack(Material.EMERALD)), 1,
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_1), 1
                ),
                output,
                true,
                BaseFluids.WATER,
                1000
        ));
    }


    public static final ItemStack SHIMMER_DUST_2 = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.SHIMMER_DUST_2)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_2);
        BasePages.RESOURCES.addItem(BaseKeys.SHIMMER_DUST_2);

        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                BaseKeys.SHIMMER_DUST_2,
                Map.of(
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_1), 1,
                        new RecipeChoice.ExactChoice(GOLD_DUST), 1,
                        new RecipeChoice.ExactChoice(new ItemStack(Material.REDSTONE)), 1
                ),
                SHIMMER_DUST_2,
                false,
                BaseFluids.WATER,
                333
        ));
    }

    public static final ItemStack SHIMMER_DUST_3 = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.SHIMMER_DUST_3)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_3);
        BasePages.RESOURCES.addItem(BaseKeys.SHIMMER_DUST_3);

        MagicAltar.Recipe.RECIPE_TYPE.addRecipe(new MagicAltar.Recipe(
                BaseKeys.SHIMMER_DUST_3,
                new ArrayList<>(Arrays.asList(
                        new RecipeChoice.ExactChoice(new ItemStack(Material.REDSTONE_BLOCK)),
                        null,
                        new RecipeChoice.ExactChoice(COVALENT_BINDER),
                        new RecipeChoice.ExactChoice(COVALENT_BINDER),
                        null,
                        new RecipeChoice.ExactChoice(COVALENT_BINDER),
                        new RecipeChoice.ExactChoice(COVALENT_BINDER),
                        null
                )),
                new RecipeChoice.ExactChoice(SHIMMER_DUST_2),
                SHIMMER_DUST_3,
                5
        ));
    }


    public static final ItemStack ENRICHED_NETHERRACK = ItemStackBuilder.pylonItem(Material.NETHERRACK, BaseKeys.ENRICHED_NETHERRACK)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, ENRICHED_NETHERRACK, BaseKeys.ENRICHED_NETHERRACK);
        BasePages.COMPONENTS.addItem(BaseKeys.ENRICHED_NETHERRACK);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ENRICHED_NETHERRACK, ENRICHED_NETHERRACK)
                .shape(" s ", "sns", " s ")
                .setIngredient('n', new ItemStack(Material.NETHERRACK))
                .setIngredient('s', SHIMMER_DUST_2);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SHIMMER_SKULL = ItemStackBuilder.pylonItem(Material.WITHER_SKELETON_SKULL, BaseKeys.SHIMMER_SKULL)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, SHIMMER_SKULL);
        BasePages.COMPONENTS.addItem(BaseKeys.SHIMMER_SKULL);

        MagicAltar.Recipe.RECIPE_TYPE.addRecipe(new MagicAltar.Recipe(
                BaseKeys.SHIMMER_SKULL,
                new ArrayList<>(Arrays.asList(
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_3),
                        null,
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_3),
                        null,
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_3),
                        null,
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_3),
                        null
                )),
                new RecipeChoice.ExactChoice(new ItemStack(Material.WITHER_SKELETON_SKULL)),
                SHIMMER_SKULL,
                30
        ));
    }

    public static final ItemStack BEHEADING_SWORD = ItemStackBuilder.pylonItem(Material.DIAMOND_SWORD, BaseKeys.BEHEADING_SWORD)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BEHEADING_SWORD).getOrThrow("durability", Integer.class))
            .build();

    static {
        PylonItem.register(BeheadingSword.class, BEHEADING_SWORD);
        BasePages.COMBAT.addItem(BaseKeys.BEHEADING_SWORD);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.BEHEADING_SWORD, BEHEADING_SWORD)
                .shape(" B ", " S ", " K ")
                .setIngredient('B', Material.BLAZE_ROD)
                .setIngredient('S', Material.DIAMOND_SWORD)
                .setIngredient('K', SHIMMER_SKULL);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PEDESTAL = ItemStackBuilder.pylonItem(Material.STONE_BRICK_WALL, BaseKeys.PEDESTAL)
            .build();

    static {
        PylonItem.register(PylonItem.class, PEDESTAL, BaseKeys.PEDESTAL);
        BasePages.BUILDING.addItem(BaseKeys.PEDESTAL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PEDESTAL, PEDESTAL)
                .shape("s  ", "s  ", "s  ")
                .setIngredient('s', new ItemStack(Material.STONE_BRICK_WALL));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack MAGIC_PEDESTAL = ItemStackBuilder.pylonItem(Material.MOSSY_STONE_BRICK_WALL, BaseKeys.MAGIC_PEDESTAL)
            .build();

    static {
        PylonItem.register(PylonItem.class, MAGIC_PEDESTAL, BaseKeys.MAGIC_PEDESTAL);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.MAGIC_PEDESTAL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.MAGIC_PEDESTAL, MAGIC_PEDESTAL)
                .shape("c c", " p ", "c c")
                .setIngredient('p', PEDESTAL)
                .setIngredient('c', COVALENT_BINDER);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack MAGIC_ALTAR = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE_SLAB, BaseKeys.MAGIC_ALTAR)
            .build();

    static {
        PylonItem.register(PylonItem.class, MAGIC_ALTAR, BaseKeys.MAGIC_ALTAR);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.MAGIC_ALTAR);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.MAGIC_ALTAR, MAGIC_ALTAR)
                .shape("   ", "dpd", "dsd")
                .setIngredient('p', PEDESTAL)
                .setIngredient('s', new ItemStack(Material.SMOOTH_STONE_SLAB))
                .setIngredient('d', SHIMMER_DUST_2);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack WITHER_PROOF_OBSIDIAN = ItemStackBuilder.pylonItem(Material.OBSIDIAN, BaseKeys.WITHER_PROOF_OBSIDIAN)
            .build();

    static {
        PylonItem.register(PylonItem.class, WITHER_PROOF_OBSIDIAN, BaseKeys.WITHER_PROOF_OBSIDIAN);
        BasePages.BUILDING.addItem(BaseKeys.WITHER_PROOF_OBSIDIAN);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.WITHER_PROOF_OBSIDIAN, WITHER_PROOF_OBSIDIAN)
                .shape("fbf", "bob", "fbf")
                .setIngredient('f', FERRODURALUM_INGOT)
                .setIngredient('b', new ItemStack(Material.IRON_BARS))
                .setIngredient('o', COMPRESSED_OBSIDIAN);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }
    // </editor-fold>

    // <editor-fold desc="Fluids" defaultstate=collapsed>
    public static final ItemStack FLUID_PIPE_WOOD = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_WOOD)
            .set(DataComponentTypes.ITEM_MODEL,
                    Material.getMaterial(
                            Settings.get(BaseKeys.FLUID_PIPE_WOOD).getOrThrow("material", String.class).toUpperCase()
                    ).getKey()
            )
            .build();

    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_WOOD);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_PIPE_WOOD);

        ItemStack output = new ItemStack(FLUID_PIPE_WOOD);
        output.setAmount(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_WOOD, output)
                .shape("www", "   ", "www")
                .setIngredient('w', new RecipeChoice.MaterialChoice(Tag.PLANKS));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_PIPE_COPPER = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_COPPER)
            .set(DataComponentTypes.ITEM_MODEL,
                    Material.getMaterial(
                            Settings.get(BaseKeys.FLUID_PIPE_COPPER).getOrThrow("material", String.class).toUpperCase()
                    ).getKey()
            )
            .build();

    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_COPPER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_PIPE_COPPER);

        ItemStack output = new ItemStack(FLUID_PIPE_COPPER);
        output.setAmount(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_COPPER, output)
                .shape("ccc", "   ", "ccc")
                .setIngredient('c', COPPER_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_PIPE_OBSIDIAN = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_OBSIDIAN)
            .set(DataComponentTypes.ITEM_MODEL,
                    Material.getMaterial(
                            Settings.get(BaseKeys.FLUID_PIPE_OBSIDIAN).getOrThrow("material", String.class).toUpperCase()
                    ).getKey()
            )
            .build();

    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_OBSIDIAN);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_PIPE_OBSIDIAN);

        ItemStack output = new ItemStack(FLUID_PIPE_OBSIDIAN);
        output.setAmount(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_OBSIDIAN, output)
                .shape("ooo", "   ", "ooo")
                .setIngredient('o', new ItemStack(Material.OBSIDIAN));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_WOOD
            = ItemStackBuilder.pylonItem(Material.BROWN_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_WOOD)
            .editPdc(pdc -> pdc.set(PortableFluidTank.FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0))
            .build();

    static {
        PylonItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_WOOD,
                BaseKeys.PORTABLE_FLUID_TANK_WOOD
        );
        BasePages.FLUID_MACHINES.addItem(BaseKeys.PORTABLE_FLUID_TANK_WOOD);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_FLUID_TANK_WOOD, PORTABLE_FLUID_TANK_WOOD)
                .shape("gwg", "w w", "gwg")
                .setIngredient('w', new RecipeChoice.MaterialChoice(Tag.PLANKS))
                .setIngredient('g', new ItemStack(Material.GLASS));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_COPPER
            = ItemStackBuilder.pylonItem(Material.ORANGE_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_COPPER)
            .editPdc(pdc -> pdc.set(PortableFluidTank.FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0))
            .build();

    static {
        PylonItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_COPPER,
                BaseKeys.PORTABLE_FLUID_TANK_COPPER
        );
        BasePages.FLUID_MACHINES.addItem(BaseKeys.PORTABLE_FLUID_TANK_COPPER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_FLUID_TANK_COPPER, PORTABLE_FLUID_TANK_COPPER)
                .shape("gcg", "c c", "gcg")
                .setIngredient('c', COPPER_SHEET)
                .setIngredient('g', new ItemStack(Material.GLASS));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack WATER_PUMP = ItemStackBuilder.pylonItem(Material.BLUE_TERRACOTTA, BaseKeys.WATER_PUMP)
            .build();

    static {
        PylonItem.register(WaterPump.Item.class, WATER_PUMP, BaseKeys.WATER_PUMP);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.WATER_PUMP);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.WATER_PUMP, WATER_PUMP)
                .shape("iii", "ibi", "ipi")
                .setIngredient('i', IRON_SHEET)
                .setIngredient('p', FLUID_PIPE_COPPER)
                .setIngredient('b', new ItemStack(Material.BUCKET));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_VALVE = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VALVE)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_TERRACOTTA.getKey())
            .build();

    static {
        PylonItem.register(PylonItem.class, FLUID_VALVE, BaseKeys.FLUID_VALVE);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_VALVE);
    }

    public static final ItemStack FLUID_FILTER = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_FILTER)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_TERRACOTTA.getKey())
            .build();

    static {
        PylonItem.register(PylonItem.class, FLUID_FILTER, BaseKeys.FLUID_FILTER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_FILTER);
    }

    public static final ItemStack FLUID_METER = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_METER)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_TERRACOTTA.getKey())
            .build();

    static {
        PylonItem.register(PylonItem.class, FLUID_METER, BaseKeys.FLUID_METER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_METER);
    }

    public static final ItemStack WATER_PLACER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.WATER_PLACER)
            .build();

    static {
        PylonItem.register(FluidPlacer.Item.class, WATER_PLACER, BaseKeys.WATER_PLACER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.WATER_PLACER);
    }

    public static final ItemStack LAVA_PLACER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.LAVA_PLACER)
            .build();

    static {
        PylonItem.register(FluidPlacer.Item.class, LAVA_PLACER, BaseKeys.LAVA_PLACER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.LAVA_PLACER);
    }

    public static final ItemStack WATER_DRAINER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.WATER_DRAINER)
            .build();

    static {
        PylonItem.register(FluidDrainer.Item.class, WATER_DRAINER, BaseKeys.WATER_DRAINER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.WATER_DRAINER);
    }

    public static final ItemStack LAVA_DRAINER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.LAVA_DRAINER)
            .build();

    static {
        PylonItem.register(FluidDrainer.Item.class, LAVA_DRAINER, BaseKeys.LAVA_DRAINER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.LAVA_DRAINER);
    }

    public static final ItemStack FLUID_VOIDER_1 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_1)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();

    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_1, BaseKeys.FLUID_VOIDER_1);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_VOIDER_1);
    }

    public static final ItemStack FLUID_VOIDER_2 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_2)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();

    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_2, BaseKeys.FLUID_VOIDER_2);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_VOIDER_2);
    }

    public static final ItemStack FLUID_VOIDER_3 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_3)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();

    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_3, BaseKeys.FLUID_VOIDER_3);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_VOIDER_3);
    }

    public static final ItemStack LOUPE = ItemStackBuilder.pylonItem(Material.GLASS_PANE, BaseKeys.LOUPE)
            .set(DataComponentTypes.CONSUMABLE, io.papermc.paper.datacomponent.item.Consumable.consumable()
                    .animation(ItemUseAnimation.SPYGLASS)
                    .hasConsumeParticles(false)
                    .consumeSeconds(3)
            )
            .build();

    static {
        PylonItem.register(Loupe.class, LOUPE);
        BasePages.SCIENCE.addItem(BaseKeys.LOUPE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.LOUPE, LOUPE)
                .shape(" C ", "CGC", " C ")
                .setIngredient('C', Material.COPPER_INGOT)
                .setIngredient('G', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack RESEARCH_PACK_1 = ItemStackBuilder.pylonItem(Material.BOOK, BaseKeys.RESEARCH_PACK_1)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();

    static {
        PylonItem.register(ResearchPack.class, RESEARCH_PACK_1);
        BasePages.SCIENCE.addItem(BaseKeys.RESEARCH_PACK_1);
    }

    public static final ItemStack DIMENSIONAL_BARREL = ItemStackBuilder.pylonItem(Material.BARREL, BaseKeys.DIMENSIONAL_BARREL)
            .build();

    static {
        PylonItem.register(DimensionalBarrel.Item.class, DIMENSIONAL_BARREL, BaseKeys.DIMENSIONAL_BARREL);
        BasePages.BUILDING.addItem(BaseKeys.DIMENSIONAL_BARREL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.DIMENSIONAL_BARREL, DIMENSIONAL_BARREL)
                .shape("CBC", "BEB", "CBC")
                .setIngredient('C', COVALENT_BINDER)
                .setIngredient('B', Material.BARREL)
                .setIngredient('E', Material.ENDER_EYE);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SLURRY_STRAINER = ItemStackBuilder.pylonItem(Material.COPPER_GRATE, BaseKeys.FLUID_STRAINER)
            .build();

    static {
        PylonItem.register(PylonItem.class, SLURRY_STRAINER, BaseKeys.FLUID_STRAINER);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.FLUID_STRAINER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_STRAINER, SLURRY_STRAINER)
                .shape("c c", " C ", "c c")
                .setIngredient('c', COPPER_SHEET)
                .setIngredient('C', Material.COPPER_GRATE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

    }
    // </editor-fold>

    //<editor-fold desc="Smeltery" defaultstate="collapsed">
    public static final ItemStack REFRACTORY_BRICK = ItemStackBuilder.pylonItem(Material.DEEPSLATE_TILES, BaseKeys.REFRACTORY_BRICK)
            .build();

    static {
        PylonItem.register(PylonItem.class, REFRACTORY_BRICK, BaseKeys.REFRACTORY_BRICK);
        BasePages.SMELTING.addItem(BaseKeys.REFRACTORY_BRICK);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("refractory_brick"), REFRACTORY_BRICK.asQuantity(4))
                .shape("BBB", "NRN", "BBB")
                .setIngredient('B', Material.BRICK)
                .setIngredient('N', Material.NETHER_BRICK)
                .setIngredient('R', Material.RED_NETHER_BRICKS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_CONTROLLER = ItemStackBuilder.pylonItem(Material.BLAST_FURNACE, BaseKeys.SMELTERY_CONTROLLER)
            .build();

    static {
        PylonItem.register(PylonItem.class, SMELTERY_CONTROLLER, BaseKeys.SMELTERY_CONTROLLER);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_CONTROLLER);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_controller"), SMELTERY_CONTROLLER)
                .shape("RBR", "BFB", "RBR")
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('F', Material.BLAST_FURNACE)
                .setIngredient('R', Material.REDSTONE);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_INPUT_HATCH = ItemStackBuilder.pylonItem(Material.LIGHT_BLUE_TERRACOTTA, BaseKeys.SMELTERY_INPUT_HATCH)
            .build();

    static {
        PylonItem.register(PylonItem.class, SMELTERY_INPUT_HATCH, BaseKeys.SMELTERY_INPUT_HATCH);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_INPUT_HATCH);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_input_hatch"), SMELTERY_INPUT_HATCH)
                .shape("IBI", "B B", "IBI")
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('I', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_OUTPUT_HATCH = ItemStackBuilder.pylonItem(Material.ORANGE_TERRACOTTA, BaseKeys.SMELTERY_OUTPUT_HATCH)
            .build();

    static {
        PylonItem.register(PylonItem.class, SMELTERY_OUTPUT_HATCH, BaseKeys.SMELTERY_OUTPUT_HATCH);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_OUTPUT_HATCH);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_output_hatch"), SMELTERY_OUTPUT_HATCH)
                .shape("IBI", "BPB", "IBI")
                .setIngredient('P', FLUID_PIPE_OBSIDIAN)
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('I', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_HOPPER = ItemStackBuilder.pylonItem(Material.HOPPER, BaseKeys.SMELTERY_HOPPER)
            .build();

    static {
        PylonItem.register(PylonItem.class, SMELTERY_HOPPER, BaseKeys.SMELTERY_HOPPER);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_HOPPER);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_hopper"), SMELTERY_HOPPER)
                .shape("I I", "IBI", " B ")
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('I', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_CASTER = ItemStackBuilder.pylonItem(Material.BRICKS, BaseKeys.SMELTERY_CASTER)
            .build();

    static {
        PylonItem.register(PylonItem.class, SMELTERY_CASTER, BaseKeys.SMELTERY_CASTER);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_CASTER);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_caster"), SMELTERY_CASTER)
                .shape("B B", "BPB", "B B")
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('P', Material.FLOWER_POT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_BURNER = ItemStackBuilder.pylonItem(Material.FURNACE, BaseKeys.SMELTERY_BURNER)
            .build();

    static {
        PylonItem.register(PylonItem.class, SMELTERY_BURNER, BaseKeys.SMELTERY_BURNER);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_BURNER);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_burner"), SMELTERY_BURNER)
                .shape("BBB", "BFB", "BBB")
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('F', Material.FURNACE);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }
    // </editor-fold>


    public static final ItemStack EXPLOSIVE_TARGET = ItemStackBuilder.pylonItem(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET)
            .build();

    static {
        PylonItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET, BaseKeys.EXPLOSIVE_TARGET);
        BasePages.BUILDING.addItem(BaseKeys.EXPLOSIVE_TARGET);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.EXPLOSIVE_TARGET, EXPLOSIVE_TARGET)
                .shape(
                        "TTT",
                        "TXT",
                        "TTT"
                )
                .setIngredient('T', Material.TNT)
                .setIngredient('X', Material.TARGET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack EXPLOSIVE_TARGET_FIERY = ItemStackBuilder.pylonItem(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET_FIERY)
            .build();

    static {
        PylonItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET_FIERY, BaseKeys.EXPLOSIVE_TARGET_FIERY);
        BasePages.BUILDING.addItem(BaseKeys.EXPLOSIVE_TARGET_FIERY);

        ShapelessRecipe recipe = new ShapelessRecipe(BaseKeys.EXPLOSIVE_TARGET_FIERY, EXPLOSIVE_TARGET_FIERY)
                .addIngredient(EXPLOSIVE_TARGET)
                .addIngredient(Material.FIRE_CHARGE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }

    public static final ItemStack EXPLOSIVE_TARGET_SUPER = ItemStackBuilder.pylonItem(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET_SUPER)
            .build();

    static {
        PylonItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET_SUPER, BaseKeys.EXPLOSIVE_TARGET_SUPER);
        BasePages.BUILDING.addItem(BaseKeys.EXPLOSIVE_TARGET_SUPER);

        ShapelessRecipe recipe = new ShapelessRecipe(BaseKeys.EXPLOSIVE_TARGET_SUPER, EXPLOSIVE_TARGET_SUPER)
                .addIngredient(4, EXPLOSIVE_TARGET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }

    public static final ItemStack EXPLOSIVE_TARGET_SUPER_FIERY = ItemStackBuilder.pylonItem(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY)
            .build();

    static {
        PylonItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET_SUPER_FIERY, BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY);
        BasePages.BUILDING.addItem(BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY);

        ShapelessRecipe recipe = new ShapelessRecipe(BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY, EXPLOSIVE_TARGET_SUPER_FIERY)
                .addIngredient(EXPLOSIVE_TARGET_SUPER)
                .addIngredient(Material.FIRE_CHARGE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }

    public static final ItemStack IMMOBILIZER = ItemStackBuilder.pylonItem(Material.PISTON, BaseKeys.IMMOBILIZER)
            .build();

    static {
        PylonItem.register(Immobilizer.Item.class, IMMOBILIZER, BaseKeys.IMMOBILIZER);
        BasePages.BUILDING.addItem(BaseKeys.IMMOBILIZER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.IMMOBILIZER, IMMOBILIZER)
                .shape(
                        "NNN",
                        "DCD",
                        "DDD"
                )
                .setIngredient('N', Material.NETHER_STAR)
                .setIngredient('D', SHIMMER_DUST_3)
                .setIngredient('C', Material.HEAVY_CORE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack ELEVATOR_1 = ItemStackBuilder.pylonItem(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_1)
            .build();

    static {
        PylonItem.register(Elevator.Item.class, ELEVATOR_1, BaseKeys.ELEVATOR_1);
        BasePages.BUILDING.addItem(BaseKeys.ELEVATOR_1);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ELEVATOR_1, ELEVATOR_1)
                .shape("   ", "QPQ", "   ")
                .setIngredient('Q', Material.QUARTZ_BLOCK)
                .setIngredient('P', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack ELEVATOR_2 = ItemStackBuilder.pylonItem(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_2)
            .build();

    static {
        PylonItem.register(Elevator.Item.class, ELEVATOR_2, BaseKeys.ELEVATOR_2);
        BasePages.BUILDING.addItem(BaseKeys.ELEVATOR_2);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ELEVATOR_2, ELEVATOR_2)
                .shape("PPP", "PEP", "PPP")
                .setIngredient('E', ELEVATOR_1)
                .setIngredient('P', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack ELEVATOR_3 = ItemStackBuilder.pylonItem(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_3)
            .build();

    static {
        PylonItem.register(Elevator.Item.class, ELEVATOR_3, BaseKeys.ELEVATOR_3);
        BasePages.BUILDING.addItem(BaseKeys.ELEVATOR_3);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ELEVATOR_3, ELEVATOR_3)
                .shape("PPP", "PEP", "PPP")
                .setIngredient('E', ELEVATOR_2)
                .setIngredient('P', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PRESS = ItemStackBuilder.pylonItem(Material.COMPOSTER, BaseKeys.PRESS)
            .build();

    static {
        PylonItem.register(Press.PressItem.class, PRESS, BaseKeys.PRESS);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.PRESS);
    }

    public static final ItemStack HYDRAULIC_GRINDSTONE_TURNER = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE, BaseKeys.HYDRAULIC_GRINDSTONE_TURNER)
            .build();

    static {
        PylonItem.register(HydraulicGrindstoneTurner.Item.class, HYDRAULIC_GRINDSTONE_TURNER, BaseKeys.HYDRAULIC_GRINDSTONE_TURNER);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_GRINDSTONE_TURNER);
    }

    public static final ItemStack HYDRAULIC_MIXING_ATTACHMENT = ItemStackBuilder.pylonItem(Material.GRAY_CONCRETE, BaseKeys.HYDRAULIC_MIXING_ATTACHMENT)
            .build();

    static {
        PylonItem.register(HydraulicMixingAttachment.Item.class, HYDRAULIC_MIXING_ATTACHMENT, BaseKeys.HYDRAULIC_MIXING_ATTACHMENT);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_MIXING_ATTACHMENT);
    }

    public static final ItemStack HYDRAULIC_PRESS_PISTON = ItemStackBuilder.pylonItem(Material.BROWN_CONCRETE, BaseKeys.HYDRAULIC_PRESS_PISTON)
            .build();

    static {
        PylonItem.register(HydraulicPressPiston.Item.class, HYDRAULIC_PRESS_PISTON, BaseKeys.HYDRAULIC_PRESS_PISTON);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_PRESS_PISTON);
    }

    public static final ItemStack HYDRAULIC_HAMMER_HEAD = ItemStackBuilder.pylonItem(Material.STONE_BRICKS, BaseKeys.HYDRAULIC_HAMMER_HEAD)
            .build();

    static {
        PylonItem.register(HydraulicHammerHead.Item.class, HYDRAULIC_HAMMER_HEAD, BaseKeys.HYDRAULIC_HAMMER_HEAD);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_HAMMER_HEAD);
    }

    public static final ItemStack SOLAR_LENS = ItemStackBuilder.pylonItem(Material.GLASS_PANE, BaseKeys.SOLAR_LENS)
            .build();

    static {
        PylonItem.register(PylonItem.class, SOLAR_LENS, BaseKeys.SOLAR_LENS);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_LENS);
    }

    public static final ItemStack PURIFICATION_TOWER_GLASS = ItemStackBuilder.pylonItem(Material.LIGHT_GRAY_STAINED_GLASS, BaseKeys.PURIFICATION_TOWER_GLASS)
            .build();

    static {
        PylonItem.register(PylonItem.class, PURIFICATION_TOWER_GLASS, BaseKeys.PURIFICATION_TOWER_GLASS);
        BasePages.HYDRAULICS.addItem(BaseKeys.PURIFICATION_TOWER_GLASS);
    }

    public static final ItemStack PURIFICATION_TOWER_CAP = ItemStackBuilder.pylonItem(Material.QUARTZ_SLAB, BaseKeys.PURIFICATION_TOWER_CAP)
            .build();

    static {
        PylonItem.register(PylonItem.class, PURIFICATION_TOWER_CAP, BaseKeys.PURIFICATION_TOWER_CAP);
        BasePages.HYDRAULICS.addItem(BaseKeys.PURIFICATION_TOWER_CAP);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_1 = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, BaseKeys.SOLAR_PURIFICATION_TOWER_1)
            .build();

    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_1, BaseKeys.SOLAR_PURIFICATION_TOWER_1);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_PURIFICATION_TOWER_1);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_2 = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, BaseKeys.SOLAR_PURIFICATION_TOWER_2)
            .build();

    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_2, BaseKeys.SOLAR_PURIFICATION_TOWER_2);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_PURIFICATION_TOWER_2);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_3 = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, BaseKeys.SOLAR_PURIFICATION_TOWER_3)
            .build();

    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_3, BaseKeys.SOLAR_PURIFICATION_TOWER_3);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_PURIFICATION_TOWER_3);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_4 = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, BaseKeys.SOLAR_PURIFICATION_TOWER_4)
            .build();

    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_4, BaseKeys.SOLAR_PURIFICATION_TOWER_4);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_PURIFICATION_TOWER_4);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_5 = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, BaseKeys.SOLAR_PURIFICATION_TOWER_5)
            .build();

    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_5, BaseKeys.SOLAR_PURIFICATION_TOWER_5);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_PURIFICATION_TOWER_5);
    }

    public static final ItemStack COAL_FIRED_PURIFICATION_TOWER = ItemStackBuilder.pylonItem(Material.BLACK_CONCRETE, BaseKeys.COAL_FIRED_PURIFICATION_TOWER)
            .build();

    static {
        PylonItem.register(CoalFiredPurificationTower.Item.class, COAL_FIRED_PURIFICATION_TOWER, BaseKeys.COAL_FIRED_PURIFICATION_TOWER);
        BasePages.HYDRAULICS.addItem(BaseKeys.COAL_FIRED_PURIFICATION_TOWER);
    }

    public static final ItemStack ICE_ARROW = ItemStackBuilder.pylonItem(Material.ARROW, BaseKeys.ICE_ARROW).build();
    static {
        PylonItem.register(IceArrow.class, ICE_ARROW, BaseKeys.ICE_ARROW);
        BasePages.COMBAT.addItem(BaseKeys.ICE_ARROW);

        ItemStack arrowResult = ICE_ARROW.clone();
        arrowResult.setAmount(8);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ICE_ARROW, arrowResult);
        recipe.shape(
                "III",
                "AAA",
                "III"
        );
        recipe.setIngredient('I', Material.PACKED_ICE);
        recipe.setIngredient('A', Material.ARROW);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack MYSTICAL_FOOD_ENHANCER_SIMPLE = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.MYSTICAL_FOOD_ENHANCER_SIMPLE).build();

    static {
        PylonItem.register(PylonItem.class, MYSTICAL_FOOD_ENHANCER_SIMPLE, BaseKeys.MYSTICAL_FOOD_ENHANCER_SIMPLE);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.MYSTICAL_FOOD_ENHANCER_SIMPLE, MYSTICAL_FOOD_ENHANCER_SIMPLE)
                .shape("GPG", "GBG", "GGG")
                .setIngredient('G', Material.GLASS)
                .setIngredient('P', Material.PISTON)
                .setIngredient('B', Material.BOWL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack MYSTICAL_FOOD_ENHANCER_HANDLE = ItemStackBuilder.pylonItem(Material.LEVER, BaseKeys.MYSTICAL_FOOD_ENHANCER_HANDLE).build();

    static {
        PylonItem.register(PylonItem.class, MYSTICAL_FOOD_ENHANCER_HANDLE, BaseKeys.MYSTICAL_FOOD_ENHANCER_HANDLE);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.MYSTICAL_FOOD_ENHANCER_HANDLE, MYSTICAL_FOOD_ENHANCER_HANDLE)
                .shape("CCC", "CLC", "CCC")
                .setIngredient('C', Material.COBBLESTONE)
                .setIngredient('L', Material.LEVER);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack GOLDEN_PIE = ItemStackBuilder.pylonItem(Material.PUMPKIN_PIE, BaseKeys.GOLDEN_PIE)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .set(DataComponentTypes.FOOD, FoodProperties.food()
                    .canAlwaysEat(Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("canAlwaysEat", Boolean.class))
                    .nutrition(Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("nutrition", Integer.class))
                    .saturation(Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("saturation", Double.class).floatValue())
                    .build())
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                            new PotionEffect(PotionEffectType.ABSORPTION, Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("absorption-duration-ticks", Integer.class),
                                    Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("absorption-strength", Integer.class)),
                            new PotionEffect(PotionEffectType.REGENERATION, Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("regeneration-duration-ticks", Integer.class),
                                    Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("regeneration-strength", Integer.class)),
                            new PotionEffect(PotionEffectType.JUMP_BOOST, Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("jumpboost-duration-ticks", Integer.class),
                                    Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("jumpboost-strength", Integer.class)),
                            new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("fireres-duration-ticks", Integer.class), 1),
                            new PotionEffect(PotionEffectType.WATER_BREATHING, Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("waterbreathing-duration-ticks", Integer.class), 1),
                            new PotionEffect(PotionEffectType.LUCK, Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("luck-duration-ticks", Integer.class),
                                    Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("luck-strength", Integer.class)),
                            new PotionEffect(PotionEffectType.RESISTANCE, Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("resistance-duration-ticks", Integer.class),
                                    Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("resistance-strength", Integer.class)),
                            new PotionEffect(PotionEffectType.SPEED, Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("speed-duration-ticks", Integer.class),
                                    Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("speed-strength", Integer.class)),
                            new PotionEffect(PotionEffectType.STRENGTH, Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("strength-duration-ticks", Integer.class),
                                    Settings.get(BaseKeys.GOLDEN_PIE).getOrThrow("strength-strength", Integer.class))
                    ), 1))
                    .build())
            .build();

    static {
        PylonItem.register(PylonItem.class, GOLDEN_PIE);
        MysticalFoodEnhancer.SimpleRecipe goldenPie = new MysticalFoodEnhancer.SimpleRecipe(
                BaseKeys.GOLDEN_PIE,
                Map.of(new RecipeChoice.MaterialChoice(Material.ENCHANTED_GOLDEN_APPLE), 1,
                        new RecipeChoice.MaterialChoice(Material.PUMPKIN_PIE), 1),
                GOLDEN_PIE
        );
        MysticalFoodEnhancer.SimpleRecipe.RECIPE_TYPE.addRecipe(goldenPie);
    }

    // Calling this method forces all the static blocks to run, which initializes our items
    public static void initialize() {
    }
}
