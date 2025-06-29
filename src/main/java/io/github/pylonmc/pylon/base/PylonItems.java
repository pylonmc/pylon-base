package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.*;
import io.github.pylonmc.pylon.base.items.fluid.*;
import io.github.pylonmc.pylon.base.items.hydraulic.machines.HydraulicGrindstoneTurner;
import io.github.pylonmc.pylon.base.items.hydraulic.machines.HydraulicHammerHead;
import io.github.pylonmc.pylon.base.items.hydraulic.machines.HydraulicMixingAttachment;
import io.github.pylonmc.pylon.base.items.Press;
import io.github.pylonmc.pylon.base.items.hydraulic.machines.HydraulicPressPiston;
import io.github.pylonmc.pylon.base.items.hydraulic.purification.CoalFiredPurificationTower;
import io.github.pylonmc.pylon.base.items.hydraulic.purification.SolarPurificationTower;
import io.github.pylonmc.pylon.base.items.multiblocks.*;
import io.github.pylonmc.pylon.base.items.multiblocks.smelting.*;
import io.github.pylonmc.pylon.base.items.research.Loupe;
import io.github.pylonmc.pylon.base.items.research.ResearchPack;
import io.github.pylonmc.pylon.base.items.tools.Hammer;
import io.github.pylonmc.pylon.base.items.tools.LumberAxe;
import io.github.pylonmc.pylon.base.items.tools.portable.PortableCraftingTable;
import io.github.pylonmc.pylon.base.items.tools.portable.PortableDustbin;
import io.github.pylonmc.pylon.base.items.tools.portable.PortableEnderChest;
import io.github.pylonmc.pylon.base.items.tools.watering.Sprinkler;
import io.github.pylonmc.pylon.base.items.tools.watering.WateringCan;
import io.github.pylonmc.pylon.base.items.weapons.BeheadingSword;
import io.github.pylonmc.pylon.base.items.weapons.RecoilArrow;
import io.github.pylonmc.pylon.base.util.RecipeUtils;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.research.Research;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@SuppressWarnings({"UnstableApiUsage", "OverlyComplexClass"})
public final class PylonItems {

    private PylonItems() {
        throw new AssertionError("Utility class");
    }

    //<editor-fold desc="Dusts" defaultstate=collapsed>
    public static final NamespacedKey COPPER_DUST_KEY = pylonKey("copper_dust");
    public static final ItemStack COPPER_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, COPPER_DUST_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, COPPER_DUST);
        GuidePages.RESOURCES.addItem(COPPER_DUST_KEY);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                COPPER_DUST_KEY,
                new ItemStack(Material.COPPER_INGOT),
                COPPER_DUST,
                2,
                Material.COPPER_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey CRUSHED_RAW_COPPER_KEY = pylonKey("crushed_raw_copper");
    public static final ItemStack CRUSHED_RAW_COPPER = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, CRUSHED_RAW_COPPER_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_COPPER);
        GuidePages.RESOURCES.addItem(CRUSHED_RAW_COPPER_KEY);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                CRUSHED_RAW_COPPER_KEY,
                new ItemStack(Material.RAW_COPPER),
                CRUSHED_RAW_COPPER,
                2,
                Material.RAW_COPPER_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey GOLD_DUST_KEY = pylonKey("gold_dust");
    public static final ItemStack GOLD_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, GOLD_DUST_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, GOLD_DUST);
        GuidePages.RESOURCES.addItem(GOLD_DUST_KEY);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                GOLD_DUST_KEY,
                new ItemStack(Material.GOLD_INGOT),
                GOLD_DUST,
                2,
                Material.GOLD_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey CRUSHED_RAW_GOLD_KEY = pylonKey("crushed_raw_gold");
    public static final ItemStack CRUSHED_RAW_GOLD = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, CRUSHED_RAW_GOLD_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_GOLD);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                CRUSHED_RAW_GOLD_KEY,
                new ItemStack(Material.RAW_GOLD),
                CRUSHED_RAW_COPPER,
                2,
                Material.RAW_GOLD_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey IRON_DUST_KEY = pylonKey("iron_dust");
    public static final ItemStack IRON_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, IRON_DUST_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, IRON_DUST);
        GuidePages.RESOURCES.addItem(IRON_DUST_KEY);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                IRON_DUST_KEY,
                new ItemStack(Material.IRON_INGOT),
                IRON_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey QUARTZ_DUST_KEY = pylonKey("quartz_dust");
    public static final ItemStack QUARTZ_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, QUARTZ_DUST_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, QUARTZ_DUST);
        GuidePages.RESOURCES.addItem(QUARTZ_DUST_KEY);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                QUARTZ_DUST_KEY,
                new ItemStack(Material.QUARTZ),
                QUARTZ_DUST,
                4,
                Material.QUARTZ_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey DIAMOND_DUST_KEY = pylonKey("diamond_dust");
    public static final ItemStack DIAMOND_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, DIAMOND_DUST_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, DIAMOND_DUST);
        GuidePages.RESOURCES.addItem(DIAMOND_DUST_KEY);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                DIAMOND_DUST_KEY,
                new ItemStack(Material.DIAMOND),
                DIAMOND_DUST,
                6,
                Material.DIAMOND_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey EMERALD_DUST_KEY = pylonKey("emerald_dust");
    public static final ItemStack EMERALD_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, EMERALD_DUST_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, EMERALD_DUST);
        GuidePages.RESOURCES.addItem(EMERALD_DUST_KEY);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                EMERALD_DUST_KEY,
                new ItemStack(Material.EMERALD),
                EMERALD_DUST,
                6,
                Material.EMERALD_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey CRUSHED_RAW_IRON_KEY = pylonKey("crushed_raw_iron");
    public static final ItemStack CRUSHED_RAW_IRON = ItemStackBuilder.pylonItem(Material.GUNPOWDER, CRUSHED_RAW_IRON_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_IRON);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                CRUSHED_RAW_IRON_KEY,
                new ItemStack(Material.RAW_IRON),
                CRUSHED_RAW_IRON,
                2,
                Material.RAW_IRON_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey SILVER_INGOT_KEY = pylonKey("silver_ingot");
    public static final ItemStack SILVER_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, SILVER_INGOT_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, SILVER_INGOT);
    }

    public static final NamespacedKey SILVER_DUST_KEY = pylonKey("silver_dust");
    public static final ItemStack SILVER_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, SILVER_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, SILVER_DUST);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                SILVER_DUST_KEY,
                SILVER_INGOT,
                SILVER_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey ZINC_INGOT_KEY = pylonKey("zinc_ingot");
    public static final ItemStack ZINC_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, ZINC_INGOT_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, ZINC_INGOT);
    }

    public static final NamespacedKey CRUSHED_RAW_ZINC_KEY = pylonKey("crushed_raw_zinc");
    public static final ItemStack CRUSHED_RAW_ZINC = ItemStackBuilder.pylonItem(Material.GUNPOWDER, CRUSHED_RAW_ZINC_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_ZINC);
    }

    public static final NamespacedKey ZINC_DUST_KEY = pylonKey("zinc_dust");
    public static final ItemStack ZINC_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, ZINC_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, ZINC_DUST);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                ZINC_DUST_KEY,
                ZINC_INGOT,
                ZINC_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey ROCK_DUST_KEY = pylonKey("rock_dust");
    public static final ItemStack ROCK_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, ROCK_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, ROCK_DUST);
        GuidePages.RESOURCES.addItem(ROCK_DUST_KEY);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                ROCK_DUST_KEY,
                new ItemStack(Material.COBBLESTONE),
                ROCK_DUST.asQuantity(2),
                2,
                Material.COBBLESTONE.createBlockData()
        ));
    }

    public static final NamespacedKey LEAD_INGOT_KEY = pylonKey("lead_ingot");
    public static final ItemStack LEAD_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, LEAD_INGOT_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, LEAD_INGOT);
    }

    public static final NamespacedKey CRUSHED_RAW_LEAD_KEY = pylonKey("crushed_raw_lead");
    public static final ItemStack CRUSHED_RAW_LEAD = ItemStackBuilder.pylonItem(Material.GUNPOWDER, CRUSHED_RAW_LEAD_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_LEAD);
    }

    public static final NamespacedKey LEAD_DUST_KEY = pylonKey("lead_dust");
    public static final ItemStack LEAD_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, LEAD_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, LEAD_DUST);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                LEAD_DUST_KEY,
                LEAD_INGOT,
                LEAD_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey TIN_INGOT_KEY = pylonKey("tin_ingot");
    public static final ItemStack TIN_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, TIN_INGOT_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_INGOT);
    }

    public static final NamespacedKey CRUSHED_RAW_TIN_KEY = pylonKey("crushed_raw_tin");
    public static final ItemStack CRUSHED_RAW_TIN = ItemStackBuilder.pylonItem(Material.GUNPOWDER, CRUSHED_RAW_TIN_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_TIN);
    }

    public static final NamespacedKey TIN_DUST_KEY = pylonKey("tin_dust");
    public static final ItemStack TIN_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, TIN_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_DUST);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                TIN_DUST_KEY,
                TIN_INGOT,
                TIN_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey COBALT_INGOT_KEY = pylonKey("cobalt_ingot");
    public static final ItemStack COBALT_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, COBALT_INGOT_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, COBALT_INGOT);
    }

    public static final NamespacedKey COBALT_DUST_KEY = pylonKey("cobalt_dust");
    public static final ItemStack COBALT_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, COBALT_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, COBALT_DUST);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                COBALT_DUST_KEY,
                COBALT_INGOT,
                COBALT_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey NICKEL_INGOT_KEY = pylonKey("nickel_ingot");
    public static final ItemStack NICKEL_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, NICKEL_INGOT_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, NICKEL_INGOT);
    }

    public static final NamespacedKey NICKEL_DUST_KEY = pylonKey("nickel_dust");
    public static final ItemStack NICKEL_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, NICKEL_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, NICKEL_DUST);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                NICKEL_DUST_KEY,
                NICKEL_INGOT,
                NICKEL_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey COAL_DUST_KEY = pylonKey("coal_dust");
    public static final ItemStack COAL_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, COAL_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, COAL_DUST);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                pylonKey("coal_dust_from_coal"),
                new ItemStack(Material.COAL),
                COAL_DUST,
                2,
                Material.COAL_BLOCK.createBlockData()
        ));
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                pylonKey("coal_dust_from_charcoal"),
                new ItemStack(Material.CHARCOAL),
                COAL_DUST,
                2,
                Material.COAL_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey CARBON_DUST_KEY = pylonKey("carbon_dust");
    public static final ItemStack CARBON_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, CARBON_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, CARBON_DUST);
    }

    // Not technically a dust but whatever
    public static final NamespacedKey SULFUR_KEY = pylonKey("sulfur");
    public static final ItemStack SULFUR = ItemStackBuilder.pylonItem(Material.YELLOW_DYE, SULFUR_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, SULFUR);
        GuidePages.RESOURCES.addItem(SULFUR_KEY);
    }

    public static final NamespacedKey BRONZE_INGOT_KEY = pylonKey("bronze_ingot");
    public static final ItemStack BRONZE_INGOT = ItemStackBuilder.pylonItem(Material.GOLD_INGOT, BRONZE_INGOT_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_INGOT);
    }

    public static final NamespacedKey BRONZE_DUST_KEY = pylonKey("bronze_dust");
    public static final ItemStack BRONZE_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BRONZE_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_DUST);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BRONZE_DUST_KEY,
                BRONZE_INGOT,
                BRONZE_DUST,
                2,
                Material.GOLD_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey BRASS_INGOT_KEY = pylonKey("brass_ingot");
    public static final ItemStack BRASS_INGOT = ItemStackBuilder.pylonItem(Material.GOLD_INGOT, BRASS_INGOT_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRASS_INGOT);
    }

    public static final NamespacedKey BRASS_DUST_KEY = pylonKey("brass_dust");
    public static final ItemStack BRASS_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BRASS_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRASS_DUST);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                BRASS_DUST_KEY,
                BRASS_INGOT,
                BRASS_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final NamespacedKey STEEL_INGOT_KEY = pylonKey("steel_ingot");
    public static final ItemStack STEEL_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, STEEL_INGOT_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_INGOT);
    }

    public static final NamespacedKey STEEL_DUST_KEY = pylonKey("steel_dust");
    public static final ItemStack STEEL_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, STEEL_DUST_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_DUST);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                STEEL_DUST_KEY,
                STEEL_INGOT,
                STEEL_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }
    // </editor-fold>

    //<editor-fold desc="Sheets" defaultstate=collapsed>
    public static final NamespacedKey COPPER_SHEET_KEY = pylonKey("copper_sheet");
    public static final ItemStack COPPER_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, COPPER_SHEET_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, COPPER_SHEET);
        GuidePages.COMPONENTS.addItem(SULFUR_KEY);
        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                COPPER_SHEET_KEY,
                new ItemStack(Material.COPPER_INGOT),
                COPPER_SHEET,
                MiningLevel.STONE,
                0.25f
        ));
    }

    public static final NamespacedKey GOLD_SHEET_KEY = pylonKey("gold_sheet");
    public static final ItemStack GOLD_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, GOLD_SHEET_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, GOLD_SHEET);
        GuidePages.COMPONENTS.addItem(GOLD_SHEET_KEY);
        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                GOLD_SHEET_KEY,
                new ItemStack(Material.GOLD_INGOT),
                GOLD_SHEET,
                MiningLevel.STONE,
                0.25f
        ));
    }

    public static final NamespacedKey IRON_SHEET_KEY = pylonKey("iron_sheet");
    public static final ItemStack IRON_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, IRON_SHEET_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, IRON_SHEET);
        GuidePages.COMPONENTS.addItem(IRON_SHEET_KEY);
        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                IRON_SHEET_KEY,
                new ItemStack(Material.IRON_INGOT),
                IRON_SHEET,
                MiningLevel.IRON,
                0.25f
        ));
    }
    //</editor-fold>

    //<editor-fold desc="Hammers" defaultstate=collapsed>
    static {
        PylonItem.register(Hammer.class, Hammer.HAMMER_STONE_STACK);
        GuidePages.TOOLS.addItem(Hammer.HAMMER_STONE_KEY);
        RecipeType.VANILLA_SHAPED.addRecipe(Hammer.getRecipe(Hammer.HAMMER_STONE_KEY, Hammer.HAMMER_STONE_STACK, Material.COBBLESTONE));
    }

    static {
        PylonItem.register(Hammer.class, Hammer.HAMMER_IRON_STACK);
        GuidePages.TOOLS.addItem(Hammer.HAMMER_IRON_KEY);
        RecipeType.VANILLA_SHAPED.addRecipe(Hammer.getRecipe(Hammer.HAMMER_IRON_KEY, Hammer.HAMMER_IRON_STACK, Material.IRON_INGOT));
    }

    static {
        PylonItem.register(Hammer.class, Hammer.HAMMER_DIAMOND_STACK);
        GuidePages.TOOLS.addItem(Hammer.HAMMER_DIAMOND_KEY);
        RecipeType.VANILLA_SHAPED.addRecipe(Hammer.getRecipe(Hammer.HAMMER_DIAMOND_KEY, Hammer.HAMMER_DIAMOND_STACK, Material.DIAMOND));
    }

    static {
        new Research(
                pylonKey("newtons_second_law"),
                Material.STONE_PICKAXE,
                5L,
                Hammer.HAMMER_STONE_KEY,
                Hammer.HAMMER_IRON_KEY,
                Hammer.HAMMER_DIAMOND_KEY
        ).register();
    }
    //</editor-fold>

    // <editor-fold desc="Misc" defaultstate=collapsed>
    static {
        PylonItem.register(WateringCan.class, WateringCan.STACK);
        GuidePages.TOOLS.addItem(WateringCan.KEY);
        ShapedRecipe recipe = new ShapedRecipe(WateringCan.KEY, WateringCan.STACK)
                .shape("  S", "S S", " S ")
                .setIngredient('S', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        new Research(
                pylonKey("plant_growth"),
                Material.BUCKET,
                5L,
                WateringCan.KEY
        ).register();
    }

    static {
        PylonItem.register(MonsterJerky.class, MonsterJerky.STACK);
        GuidePages.FOOD.addItem(MonsterJerky.KEY);

        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                pylonKey("monster_jerky_furnace"),
                MonsterJerky.STACK,
                Material.ROTTEN_FLESH,
                MonsterJerky.COOKING_XP,
                RecipeUtils.DEFAULT_FURNACE_TIME_TICKS
        );
        furnaceRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeType.VANILLA_FURNACE.addRecipe(furnaceRecipe);

        SmokingRecipe smokingRecipe = new SmokingRecipe(
                pylonKey("monster_jerky_smoker"),
                MonsterJerky.STACK,
                Material.ROTTEN_FLESH,
                MonsterJerky.COOKING_XP,
                RecipeUtils.DEFAULT_SMOKER_TIME_TICKS
        );
        smokingRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeType.VANILLA_SMOKING.addRecipe(smokingRecipe);

        ShapedRecipe leatherRecipe = new ShapedRecipe(pylonKey("leather"), new ItemStack(Material.LEATHER))
                .shape("RR ", "RR ", "   ")
                .setIngredient('R', MonsterJerky.STACK);
        leatherRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(leatherRecipe);

        new Research(
                pylonKey("food_preservation"),
                Material.ROTTEN_FLESH,
                2L,
                MonsterJerky.KEY
        ).register();
    }

    //<editor-fold desc="Ferroduralum" defaultstate=collapsed>
    public static final NamespacedKey RAW_FERRODURALUM_KEY = pylonKey("raw_ferroduralum");
    public static final ItemStack RAW_FERRODURALUM = ItemStackBuilder.pylonItem(Material.RAW_GOLD, RAW_FERRODURALUM_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, RAW_FERRODURALUM);
        GuidePages.RESOURCES.addItem(RAW_FERRODURALUM_KEY);
        ShapedRecipe recipe = new ShapedRecipe(RAW_FERRODURALUM_KEY, RAW_FERRODURALUM)
                .shape("CGR", "   ", "   ")
                .setIngredient('C', Material.COPPER_ORE)
                .setIngredient('G', Material.GOLD_ORE)
                .setIngredient('R', Material.REDSTONE);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey FERRODURALUM_INGOT_KEY = pylonKey("ferroduralum_ingot");
    public static final ItemStack FERRODURALUM_INGOT = ItemStackBuilder.pylonItem(Material.GOLD_INGOT, FERRODURALUM_INGOT_KEY)
            .build();
    public static final float FERRODURALUM_INGOT_COOKING_XP = Settings.get(FERRODURALUM_INGOT_KEY)
            .getOrThrow("cooking.xp", Double.class)
            .floatValue();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_INGOT);
        GuidePages.RESOURCES.addItem(FERRODURALUM_INGOT_KEY);
        FurnaceRecipe recipe = new FurnaceRecipe(
                FERRODURALUM_INGOT_KEY,
                FERRODURALUM_INGOT,
                new RecipeChoice.ExactChoice(RAW_FERRODURALUM),
                FERRODURALUM_INGOT_COOKING_XP,
                RecipeUtils.DEFAULT_FURNACE_TIME_TICKS
        );
        recipe.setCategory(CookingBookCategory.MISC);
        RecipeType.VANILLA_FURNACE.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("primitive_alloying"),
                Material.IRON_INGOT,
                8L,
                RAW_FERRODURALUM_KEY,
                FERRODURALUM_INGOT_KEY
        ).register();
    }

    public static final NamespacedKey FERRODURALUM_SHEET_KEY = pylonKey("ferroduralum_sheet");
    public static final ItemStack FERRODURALUM_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, FERRODURALUM_SHEET_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_SHEET);
        GuidePages.COMPONENTS.addItem(FERRODURALUM_SHEET_KEY);
        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                FERRODURALUM_SHEET_KEY,
                FERRODURALUM_INGOT,
                FERRODURALUM_SHEET,
                MiningLevel.IRON,
                0.25f
        ));
    }

    static {
        new Research(
                pylonKey("metal_ductility"),
                Material.PAPER,
                5L,
                COPPER_SHEET_KEY,
                GOLD_SHEET_KEY,
                IRON_SHEET_KEY,
                FERRODURALUM_SHEET_KEY
        ).register();
    }

    public static final NamespacedKey FERRODURALUM_SWORD_KEY = pylonKey("ferroduralum_sword");
    public static final ItemStack FERRODURALUM_SWORD = ItemStackBuilder.pylonItem(Material.GOLDEN_SWORD, FERRODURALUM_SWORD_KEY)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                            pylonKey("ferroduralum_sword_damage"),
                            0.15,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 300)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_SWORD);
        GuidePages.WEAPONS.addItem(FERRODURALUM_SWORD_KEY);
        ShapedRecipe recipe = new ShapedRecipe(FERRODURALUM_SWORD_KEY, FERRODURALUM_SWORD)
                .shape(" F ", " F ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey FERRODURALUM_AXE_KEY = pylonKey("ferroduralum_axe");
    public static final ItemStack FERRODURALUM_AXE = ItemStackBuilder.pylonItem(Material.GOLDEN_AXE, FERRODURALUM_AXE_KEY)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                            pylonKey("ferroduralum_axe_speed"),
                            0.15,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 300)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_AXE);
        GuidePages.TOOLS.addItem(FERRODURALUM_AXE_KEY);
        ShapedRecipe recipe = new ShapedRecipe(FERRODURALUM_AXE_KEY, FERRODURALUM_AXE)
                .shape("FF ", "FS ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
        RecipeType.VANILLA_SHAPED.addRecipe(RecipeUtils.reflect(recipe));
    }

    public static final NamespacedKey FERRODURALUM_PICKAXE_KEY = pylonKey("ferroduralum_pickaxe");
    public static final ItemStack FERRODURALUM_PICKAXE = ItemStackBuilder.pylonItem(Material.GOLDEN_PICKAXE, FERRODURALUM_PICKAXE_KEY)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                            pylonKey("ferroduralum_pickaxe_speed"),
                            0.15,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 300)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_PICKAXE);
        GuidePages.TOOLS.addItem(FERRODURALUM_PICKAXE_KEY);
        ShapedRecipe recipe = new ShapedRecipe(FERRODURALUM_PICKAXE_KEY, FERRODURALUM_PICKAXE)
                .shape("FFF", " S ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey FERRODURALUM_SHOVEL_KEY = pylonKey("ferroduralum_shovel");
    public static final ItemStack FERRODURALUM_SHOVEL = ItemStackBuilder.pylonItem(Material.GOLDEN_SHOVEL, FERRODURALUM_SHOVEL_KEY)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                            pylonKey("ferroduralum_shovel_speed"),
                            0.15,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 300)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_SHOVEL);
        GuidePages.TOOLS.addItem(FERRODURALUM_SHOVEL_KEY);
        ShapedRecipe recipe = new ShapedRecipe(FERRODURALUM_SHOVEL_KEY, FERRODURALUM_SHOVEL)
                .shape(" F ", " S ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey FERRODURALUM_HOE_KEY = pylonKey("ferroduralum_hoe");
    public static final ItemStack FERRODURALUM_HOE = ItemStackBuilder.pylonItem(Material.GOLDEN_HOE, FERRODURALUM_HOE_KEY)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                            pylonKey("ferroduralum_hoe_speed"),
                            0.15,
                            AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 300)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_HOE);
        GuidePages.TOOLS.addItem(FERRODURALUM_HOE_KEY);
        ShapedRecipe recipe = new ShapedRecipe(FERRODURALUM_HOE_KEY, FERRODURALUM_HOE)
                .shape("FF ", " S ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
        RecipeType.VANILLA_SHAPED.addRecipe(RecipeUtils.reflect(recipe));
    }

    static {
        new Research(
                pylonKey("primitive_alloy_tools"),
                Material.GOLDEN_AXE,
                10L,
                FERRODURALUM_SWORD_KEY,
                FERRODURALUM_AXE_KEY,
                FERRODURALUM_PICKAXE_KEY,
                FERRODURALUM_SHOVEL_KEY,
                FERRODURALUM_HOE_KEY
        ).register();
    }

    public static final NamespacedKey FERRODURALUM_HELMET_KEY = pylonKey("ferroduralum_helmet");
    public static final ItemStack FERRODURALUM_HELMET = ItemStackBuilder.pylonItem(Material.GOLDEN_HELMET, FERRODURALUM_HELMET_KEY)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            pylonKey("ferroduralum_helmet_armor"),
                            2.5,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.HEAD
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            pylonKey("ferroduralum_helmet_toughness"),
                            1,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.HEAD
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 190)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_HELMET);
        GuidePages.ARMOUR.addItem(FERRODURALUM_HELMET_KEY);
        ShapedRecipe recipe = new ShapedRecipe(FERRODURALUM_HELMET_KEY, FERRODURALUM_HELMET)
                .shape("FFF", "F F", "   ")
                .setIngredient('F', FERRODURALUM_INGOT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey FERRODURALUM_CHESTPLATE_KEY = pylonKey("ferroduralum_chestplate");
    public static final ItemStack FERRODURALUM_CHESTPLATE = ItemStackBuilder.pylonItem(Material.GOLDEN_CHESTPLATE, FERRODURALUM_CHESTPLATE_KEY)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            pylonKey("ferroduralum_chestplate_armor"),
                            7,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.CHEST
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            pylonKey("ferroduralum_chestplate_toughness"),
                            1,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.CHEST
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 276)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_CHESTPLATE);
        GuidePages.ARMOUR.addItem(FERRODURALUM_CHESTPLATE_KEY);
        ShapedRecipe recipe = new ShapedRecipe(FERRODURALUM_CHESTPLATE_KEY, FERRODURALUM_CHESTPLATE)
                .shape("F F", "FFF", "FFF")
                .setIngredient('F', FERRODURALUM_INGOT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey FERRODURALUM_LEGGINGS_KEY = pylonKey("ferroduralum_leggings");
    public static final ItemStack FERRODURALUM_LEGGINGS = ItemStackBuilder.pylonItem(Material.GOLDEN_LEGGINGS, FERRODURALUM_LEGGINGS_KEY)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            pylonKey("ferroduralum_leggings_armor"),
                            5.5,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.LEGS
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            pylonKey("ferroduralum_leggings_toughness"),
                            1,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.LEGS
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 259)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_LEGGINGS);
        GuidePages.ARMOUR.addItem(FERRODURALUM_LEGGINGS_KEY);
        ShapedRecipe recipe = new ShapedRecipe(FERRODURALUM_LEGGINGS_KEY, FERRODURALUM_LEGGINGS)
                .shape("FFF", "F F", "F F")
                .setIngredient('F', FERRODURALUM_INGOT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey FERRODURALUM_BOOTS_KEY = pylonKey("ferroduralum_boots");
    public static final ItemStack FERRODURALUM_BOOTS = ItemStackBuilder.pylonItem(Material.GOLDEN_BOOTS, FERRODURALUM_BOOTS_KEY)
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            pylonKey("ferroduralum_boots_armor"),
                            2.5,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.FEET
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            pylonKey("ferroduralum_boots_toughness"),
                            1,
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.FEET
                    ))
                    .build())
            .set(DataComponentTypes.MAX_DAMAGE, 225)
            .build();

    static {
        PylonItem.register(PylonItem.class, FERRODURALUM_BOOTS);
        GuidePages.ARMOUR.addItem(FERRODURALUM_BOOTS_KEY);
        ShapedRecipe recipe = new ShapedRecipe(FERRODURALUM_BOOTS_KEY, FERRODURALUM_BOOTS)
                .shape("F F", "F F", "   ")
                .setIngredient('F', FERRODURALUM_INGOT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("primitive_alloy_armor"),
                Material.GOLDEN_HELMET,
                10L,
                FERRODURALUM_HELMET_KEY,
                FERRODURALUM_CHESTPLATE_KEY,
                FERRODURALUM_LEGGINGS_KEY,
                FERRODURALUM_BOOTS_KEY
        ).register();
    }
    //</editor-fold>

    public static final NamespacedKey COMPRESSED_WOOD_KEY = pylonKey("compressed_wood");
    public static final ItemStack COMPRESSED_WOOD = ItemStackBuilder.pylonItem(Material.OAK_WOOD, COMPRESSED_WOOD_KEY)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, COMPRESSED_WOOD);
        GuidePages.COMPONENTS.addItem(COMPRESSED_WOOD_KEY);
        ShapedRecipe recipe = new ShapedRecipe(COMPRESSED_WOOD_KEY, COMPRESSED_WOOD)
                .shape("WWW", "WWW", "WWW")
                .setIngredient('W', new RecipeChoice.MaterialChoice(Tag.LOGS));
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    //<editor-fold desc="Portable Items" defaultstate=collapsed>
    public static final NamespacedKey PORTABILITY_CATALYST_KEY = pylonKey("portability_catalyst");
    public static final ItemStack PORTABILITY_CATALYST = ItemStackBuilder.pylonItem(Material.AMETHYST_SHARD, PORTABILITY_CATALYST_KEY)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, PORTABILITY_CATALYST);
        GuidePages.COMPONENTS.addItem(PORTABILITY_CATALYST_KEY);
        ShapedRecipe recipe = new ShapedRecipe(PORTABILITY_CATALYST_KEY, PORTABILITY_CATALYST)
                .shape("RRR", "RPR", "RRR")
                .setIngredient('R', Material.REDSTONE_BLOCK)
                .setIngredient('P', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey COMPRESSED_OBSIDIAN_KEY = pylonKey("compressed_obsidian");
    public static final ItemStack COMPRESSED_OBSIDIAN = ItemStackBuilder.pylonItem(Material.OBSIDIAN, COMPRESSED_OBSIDIAN_KEY)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, COMPRESSED_OBSIDIAN);
        GuidePages.COMPONENTS.addItem(COMPRESSED_OBSIDIAN_KEY);
        ShapedRecipe recipe = new ShapedRecipe(COMPRESSED_OBSIDIAN_KEY, COMPRESSED_OBSIDIAN)
                .shape("OOO", "OOO", "OOO")
                .setIngredient('O', Material.OBSIDIAN);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("compression"),
                Material.OBSIDIAN,
                5L,
                COMPRESSED_WOOD_KEY,
                COMPRESSED_OBSIDIAN_KEY
        ).register();
    }

    static {
        PylonItem.register(PortableCraftingTable.class, PortableCraftingTable.STACK);
        GuidePages.TOOLS.addItem(PortableCraftingTable.KEY);
        ShapedRecipe recipe = new ShapedRecipe(PortableCraftingTable.KEY, PortableCraftingTable.STACK)
                .shape("WWW", "WCW", "   ")
                .setIngredient('W', COMPRESSED_WOOD)
                .setIngredient('C', PORTABILITY_CATALYST);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(PortableDustbin.class, PortableDustbin.STACK);
        GuidePages.TOOLS.addItem(PortableDustbin.KEY);
        ShapedRecipe recipe = new ShapedRecipe(PortableDustbin.KEY, PortableDustbin.STACK)
                .shape("CCC", "IAI", "III")
                .setIngredient('I', IRON_SHEET)
                .setIngredient('C', Material.CACTUS)
                .setIngredient('A', PORTABILITY_CATALYST);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(PortableEnderChest.class, PortableEnderChest.STACK);
        GuidePages.TOOLS.addItem(PortableEnderChest.KEY);
        ShapedRecipe recipe = new ShapedRecipe(PortableEnderChest.KEY, PortableEnderChest.STACK)
                .shape("OOO", "OEO", "OOO")
                .setIngredient('O', COMPRESSED_OBSIDIAN)
                .setIngredient('E', PORTABILITY_CATALYST);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }
    //</editor-fold>

    static {
        new Research(
                pylonKey("portability"),
                Material.CRAFTING_TABLE,
                7L,
                PORTABILITY_CATALYST_KEY,
                PortableCraftingTable.KEY,
                PortableDustbin.KEY,
                PortableEnderChest.KEY
        ).register();
    }

    //<editor-fold desc="Medical items" defaultstate=collapsed>
    public static final NamespacedKey FIBER_KEY = pylonKey("fiber");
    public static final ItemStack FIBER = ItemStackBuilder.pylonItem(Material.BAMBOO_MOSAIC, FIBER_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, FIBER);
        GuidePages.COMPONENTS.addItem(FIBER_KEY);
        ShapedRecipe recipe = new ShapedRecipe(FIBER_KEY, FIBER)
                .shape("SSS", "   ", "   ")
                .setIngredient('S', Material.STRING);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey PLASTER_KEY = pylonKey("plaster");
    public static final ItemStack PLASTER = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE_SLAB, PLASTER_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, PLASTER);
        GuidePages.COMPONENTS.addItem(PLASTER_KEY);
        ShapedRecipe recipe = new ShapedRecipe(PLASTER_KEY, PLASTER)
                .shape("CC ", "CC ", "   ")
                .setIngredient('C', Material.CLAY);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey BANDAGE_KEY = pylonKey("bandage");
    public static final ItemStack BANDAGE = ItemStackBuilder.pylonItem(Material.COBWEB, BANDAGE_KEY)
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
        GuidePages.TOOLS.addItem(BANDAGE_KEY);
        ShapedRecipe recipe = new ShapedRecipe(BANDAGE_KEY, BANDAGE)
                .shape("FF ", "FF ", "   ")
                .setIngredient('F', FIBER);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey SPLINT_KEY = pylonKey("splint");
    public static final ItemStack SPLINT = ItemStackBuilder.pylonItem(Material.STICK, SPLINT_KEY)
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
        GuidePages.TOOLS.addItem(SPLINT_KEY);
        ShapedRecipe recipe = new ShapedRecipe(SPLINT_KEY, SPLINT)
                .shape("PPP", "   ", "PPP")
                .setIngredient('P', PLASTER);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("first_aid"),
                Material.COBWEB,
                5L,
                BANDAGE_KEY,
                SPLINT_KEY
        ).register();
    }

    public static final NamespacedKey DISINFECTANT_KEY = pylonKey("disinfectant");
    public static final ItemStack DISINFECTANT = ItemStackBuilder.pylonItem(Material.BREWER_POTTERY_SHERD, DISINFECTANT_KEY)
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
        GuidePages.TOOLS.addItem(DISINFECTANT_KEY);
        ShapedRecipe recipe = new ShapedRecipe(DISINFECTANT_KEY, DISINFECTANT)
                .shape("DDD", "D D", "DDD")
                .setIngredient('D', Material.DRIPSTONE_BLOCK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey MEDKIT_KEY = pylonKey("medkit");
    public static final ItemStack MEDKIT = ItemStackBuilder.pylonItem(Material.SHULKER_SHELL, MEDKIT_KEY)
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
        GuidePages.TOOLS.addItem(MEDKIT_KEY);
        ShapedRecipe recipe = new ShapedRecipe(MEDKIT_KEY, MEDKIT)
                .shape("PFP", "DDD", "PFP")
                .setIngredient('P', PLASTER)
                .setIngredient('D', DISINFECTANT)
                .setIngredient('F', FIBER);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }
    //</editor-fold>

    static {
        new Research(
                pylonKey("medicine"),
                Material.BREWER_POTTERY_SHERD,
                10L,
                DISINFECTANT_KEY,
                MEDKIT_KEY
        ).register();
    }

    public static final ItemStack SPRINKLER = ItemStackBuilder.pylonItem(Material.FLOWER_POT, Sprinkler.KEY)
            .build();

    static {
        PylonItem.register(Sprinkler.Item.class, SPRINKLER, Sprinkler.KEY);
        GuidePages.FLUID_MACHINES.addItem(Sprinkler.KEY);
        ShapedRecipe recipe = new ShapedRecipe(Sprinkler.KEY, SPRINKLER)
                .shape("B B", "B B", "FRF")
                .setIngredient('B', new ItemStack(Material.BRICK))
                .setIngredient('F', FERRODURALUM_INGOT)
                .setIngredient('R', new ItemStack(Material.REPEATER));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        new Research(
                pylonKey("plant_growth_automated"),
                Material.FLOWER_POT,
                10L,
                Sprinkler.KEY
        ).register();
    }

    static {
        PylonItem.register(RecoilArrow.class, RecoilArrow.STACK);
        GuidePages.WEAPONS.addItem(RecoilArrow.KEY);
        ItemStack output = RecoilArrow.STACK.clone();
        output.setAmount(8);
        ShapedRecipe recipe = new ShapedRecipe(RecoilArrow.KEY, RecoilArrow.STACK)
                .shape("SSS", "SAS", "SSS")
                .setIngredient('S', Material.SLIME_BALL)
                .setIngredient('A', Material.ARROW);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        new Research(
                pylonKey("newtons_third_law"),
                Material.ARROW,
                15L,
                RecoilArrow.KEY
        ).register();
    }

    static {
        PylonItem.register(LumberAxe.class, LumberAxe.STACK);
        GuidePages.TOOLS.addItem(LumberAxe.KEY);
        ShapedRecipe recipe = new ShapedRecipe(LumberAxe.KEY, LumberAxe.STACK)
                .shape("WWW", "WAW", "III")
                .setIngredient('W', COMPRESSED_WOOD)
                .setIngredient('A', Material.WOODEN_AXE)
                .setIngredient('I', Material.IRON_BLOCK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        new Research(
                pylonKey("gravity"),
                Material.WOODEN_AXE,
                10L,
                LumberAxe.KEY
        ).register();
    }

    public static final ItemStack GRINDSTONE = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE_SLAB, Grindstone.KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, GRINDSTONE, Grindstone.KEY);
        GuidePages.MANUAL_MACHINES.addItem(Grindstone.KEY);
        ShapedRecipe recipe = new ShapedRecipe(Grindstone.KEY, GRINDSTONE)
                .shape("STS", "   ", "   ")
                .setIngredient('T', new ItemStack(Material.SMOOTH_STONE))
                .setIngredient('S', new ItemStack(Material.SMOOTH_STONE_SLAB));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack GRINDSTONE_HANDLE = ItemStackBuilder.pylonItem(Material.OAK_FENCE, GrindstoneHandle.KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, GRINDSTONE_HANDLE, GrindstoneHandle.KEY);
        GuidePages.MANUAL_MACHINES.addItem(GrindstoneHandle.KEY);
        ShapedRecipe recipe = new ShapedRecipe(GrindstoneHandle.KEY, GRINDSTONE_HANDLE)
                .shape("F  ", "F  ", "F  ")
                .setIngredient('F', new RecipeChoice.MaterialChoice(Tag.FENCES));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("grinding"),
                Material.SMOOTH_STONE_SLAB,
                5L,
                Grindstone.KEY,
                GrindstoneHandle.KEY
        ).register();
    }

    public static final NamespacedKey FLOUR_KEY = pylonKey("flour");
    public static final ItemStack FLOUR = ItemStackBuilder.pylonItem(Material.SUGAR, FLOUR_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, FLOUR);
        GuidePages.RESOURCES.addItem(FLOUR_KEY);
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                FLOUR_KEY,
                new ItemStack(Material.WHEAT, 2),
                FLOUR,
                2,
                Material.WHEAT.createBlockData(data -> {
                    Ageable ageable = (Ageable) data;
                    ageable.setAge(ageable.getMaximumAge());
                })
        ));
    }

    public static final NamespacedKey DOUGH_KEY = pylonKey("dough");
    public static final ItemStack DOUGH = ItemStackBuilder.pylonItem(Material.YELLOW_DYE, DOUGH_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, DOUGH);
        GuidePages.RESOURCES.addItem(DOUGH_KEY);

        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                DOUGH_KEY,
                Map.of(new RecipeChoice.ExactChoice(FLOUR), 1),
                DOUGH,
                false,
                PylonFluids.WATER,
                333
        ));

        FurnaceRecipe furnaceBreadRecipe = new FurnaceRecipe(
                pylonKey("bread_from_dough_furnace"),
                new ItemStack(Material.BREAD),
                new RecipeChoice.ExactChoice(DOUGH),
                0.2F,
                10 * 20
        );
        furnaceBreadRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeType.VANILLA_FURNACE.addRecipe(furnaceBreadRecipe);

        SmokingRecipe smokerBreadRecipe = new SmokingRecipe(
                pylonKey("bread_from_dough_smoker"),
                new ItemStack(Material.BREAD),
                new RecipeChoice.ExactChoice(DOUGH),
                0.2F,
                5 * 20);
        smokerBreadRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeType.VANILLA_SMOKING.addRecipe(smokerBreadRecipe);

        new Research(
                pylonKey("baking"),
                Material.YELLOW_DYE,
                2L,
                FLOUR_KEY,
                DOUGH_KEY
        ).register();
    }

    static {
        PylonItem.register(HealthTalisman.class, HealthTalisman.HEALTH_TALISMAN_SIMPLE_STACK);
        GuidePages.TOOLS.addItem(HealthTalisman.HEALTH_TALISMAN_SIMPLE_KEY);
        ShapedRecipe recipe = new ShapedRecipe(HealthTalisman.HEALTH_TALISMAN_SIMPLE_KEY, HealthTalisman.HEALTH_TALISMAN_SIMPLE_STACK)
                .shape("GGG", "GRG", "GGG")
                .setIngredient('G', Material.GLISTERING_MELON_SLICE)
                .setIngredient('R', Material.REDSTONE);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(HealthTalisman.class, HealthTalisman.HEALTH_TALISMAN_ADVANCED_STACK);
        GuidePages.TOOLS.addItem(HealthTalisman.HEALTH_TALISMAN_ADVANCED_KEY);
        ShapedRecipe recipe = new ShapedRecipe(HealthTalisman.HEALTH_TALISMAN_ADVANCED_KEY, HealthTalisman.HEALTH_TALISMAN_ADVANCED_STACK)
                .shape("SSS", "SSS", "SSS")
                .setIngredient('S', HealthTalisman.HEALTH_TALISMAN_SIMPLE_STACK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(HealthTalisman.class, HealthTalisman.HEALTH_TALISMAN_ULTIMATE_STACK);
        GuidePages.TOOLS.addItem(HealthTalisman.HEALTH_TALISMAN_ULTIMATE_KEY);
        ShapedRecipe recipe = new ShapedRecipe(HealthTalisman.HEALTH_TALISMAN_ULTIMATE_KEY, HealthTalisman.HEALTH_TALISMAN_ULTIMATE_STACK)
                .shape("AAA", "AAA", "AAA")
                .setIngredient('A', HealthTalisman.HEALTH_TALISMAN_ADVANCED_STACK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("better_health"),
                Material.AMETHYST_SHARD,
                10L,
                HealthTalisman.HEALTH_TALISMAN_SIMPLE_KEY,
                HealthTalisman.HEALTH_TALISMAN_ADVANCED_KEY,
                HealthTalisman.HEALTH_TALISMAN_ULTIMATE_KEY
        ).register();
    }

    public static final ItemStack MIXING_POT = ItemStackBuilder.pylonItem(Material.CAULDRON, MixingPot.KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, MIXING_POT, MixingPot.KEY);
        GuidePages.MANUAL_MACHINES.addItem(MixingPot.KEY);
        ShapedRecipe recipe = new ShapedRecipe(MixingPot.KEY, MIXING_POT)
                .shape("f f", "f f", "fff")
                .setIngredient('f', FERRODURALUM_INGOT);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        new Research(
                pylonKey("homogeneity"),
                Material.CAULDRON,
                6L,
                MixingPot.KEY
        ).register();
    }

    public static final NamespacedKey SHIMMER_DUST_1_KEY = pylonKey("shimmer_dust_1");
    public static final ItemStack SHIMMER_DUST_1 = ItemStackBuilder.pylonItem(Material.SUGAR, SHIMMER_DUST_1_KEY)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_1);
        GuidePages.RESOURCES.addItem(SHIMMER_DUST_1_KEY);
        ShapelessRecipe recipe = new ShapelessRecipe(SHIMMER_DUST_1_KEY, SHIMMER_DUST_1)
                .addIngredient(COPPER_DUST)
                .addIngredient(Material.FLINT)
                .addIngredient(Material.CLAY_BALL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }

    public static final NamespacedKey SHIMMER_DUST_2_KEY = pylonKey("shimmer_dust_2");
    public static final ItemStack SHIMMER_DUST_2 = ItemStackBuilder.pylonItem(Material.SUGAR, SHIMMER_DUST_2_KEY)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_2);
        GuidePages.RESOURCES.addItem(SHIMMER_DUST_2_KEY);
        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                SHIMMER_DUST_2_KEY,
                Map.of(
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_1), 1,
                        new RecipeChoice.ExactChoice(GOLD_DUST), 1,
                        new RecipeChoice.ExactChoice(new ItemStack(Material.REDSTONE)), 1
                ),
                SHIMMER_DUST_2,
                false,
                PylonFluids.WATER,
                333
        ));
    }

    public static final ItemStack ENRICHED_NETHERRACK = ItemStackBuilder.pylonItem(Material.NETHERRACK, EnrichedNetherrack.KEY)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, ENRICHED_NETHERRACK, EnrichedNetherrack.KEY);
        GuidePages.COMPONENTS.addItem(EnrichedNetherrack.KEY);
        ShapedRecipe recipe = new ShapedRecipe(EnrichedNetherrack.KEY, ENRICHED_NETHERRACK)
                .shape(" s ", "sns", " s ")
                .setIngredient('n', new ItemStack(Material.NETHERRACK))
                .setIngredient('s', SHIMMER_DUST_2);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey COVALENT_BINDER_KEY = pylonKey("covalent_binder");
    public static final ItemStack COVALENT_BINDER = ItemStackBuilder.pylonItem(Material.LIGHT_BLUE_DYE, COVALENT_BINDER_KEY)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, COVALENT_BINDER);
        GuidePages.RESOURCES.addItem(COVALENT_BINDER_KEY);
        ItemStack output = COVALENT_BINDER.clone();
        output.setAmount(6);
        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                COVALENT_BINDER_KEY,
                Map.of(
                        new RecipeChoice.ExactChoice(new ItemStack(Material.GUNPOWDER)), 4,
                        new RecipeChoice.ExactChoice(new ItemStack(Material.EMERALD)), 1,
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_1), 1
                ),
                output,
                true,
                PylonFluids.WATER,
                1000
        ));
    }

    public static final NamespacedKey SHIMMER_DUST_3_KEY = pylonKey("shimmer_dust_3");
    public static final ItemStack SHIMMER_DUST_3 = ItemStackBuilder.pylonItem(Material.SUGAR, SHIMMER_DUST_3_KEY)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_3);
        GuidePages.RESOURCES.addItem(SHIMMER_DUST_3_KEY);
        MagicAltar.Recipe.RECIPE_TYPE.addRecipe(new MagicAltar.Recipe(
                SHIMMER_DUST_3_KEY,
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

    public static final NamespacedKey SHIMMER_SKULL_KEY = pylonKey("shimmer_skull");
    public static final ItemStack SHIMMER_SKULL = ItemStackBuilder.pylonItem(Material.WITHER_SKELETON_SKULL, SHIMMER_SKULL_KEY)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, SHIMMER_SKULL);
        GuidePages.COMPONENTS.addItem(SHIMMER_SKULL_KEY);
        MagicAltar.Recipe.RECIPE_TYPE.addRecipe(new MagicAltar.Recipe(
                SHIMMER_SKULL_KEY,
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

    static {
        new Research(
                pylonKey("glitter"),
                Material.SUGAR,
                5L,
                SHIMMER_DUST_1_KEY,
                SHIMMER_DUST_2_KEY,
                SHIMMER_DUST_3_KEY,
                SHIMMER_SKULL_KEY
        ).register();
    }

    static {
        PylonItem.register(BeheadingSword.class, BeheadingSword.STACK);
        GuidePages.WEAPONS.addItem(BeheadingSword.KEY);
        ShapedRecipe recipe = new ShapedRecipe(BeheadingSword.KEY, BeheadingSword.STACK)
                .shape(" B ", " S ", " K ")
                .setIngredient('B', Material.BLAZE_ROD)
                .setIngredient('S', Material.DIAMOND_SWORD)
                .setIngredient('K', SHIMMER_SKULL);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        new Research(
                pylonKey("french_revolution"),
                Material.DIAMOND_SWORD,
                10L,
                BeheadingSword.KEY
        ).register();
    }

    public static final ItemStack PEDESTAL = ItemStackBuilder.pylonItem(Material.STONE_BRICK_WALL, Pedestal.PEDESTAL_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, PEDESTAL, Pedestal.PEDESTAL_KEY);
        GuidePages.BUILDING.addItem(Pedestal.PEDESTAL_KEY);
        ShapedRecipe recipe = new ShapedRecipe(Pedestal.PEDESTAL_KEY, PEDESTAL)
                .shape("s  ", "s  ", "s  ")
                .setIngredient('s', new ItemStack(Material.STONE_BRICK_WALL));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        new Research(
                pylonKey("showing_off"),
                Material.STONE_BRICK_WALL,
                2L,
                Pedestal.PEDESTAL_KEY
        ).register();
    }

    public static final ItemStack MAGIC_PEDESTAL = ItemStackBuilder.pylonItem(Material.MOSSY_STONE_BRICK_WALL, Pedestal.MAGIC_PEDESTAL_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, MAGIC_PEDESTAL, Pedestal.MAGIC_PEDESTAL_KEY);
        GuidePages.MANUAL_MACHINES.addItem(Pedestal.MAGIC_PEDESTAL_KEY);
        ShapedRecipe recipe = new ShapedRecipe(Pedestal.MAGIC_PEDESTAL_KEY, MAGIC_PEDESTAL)
                .shape("c c", " p ", "c c")
                .setIngredient('p', PEDESTAL)
                .setIngredient('c', COVALENT_BINDER);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack MAGIC_ALTAR = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE_SLAB, MagicAltar.KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, MAGIC_ALTAR, MagicAltar.KEY);
        GuidePages.MANUAL_MACHINES.addItem(MagicAltar.KEY);
        ShapedRecipe recipe = new ShapedRecipe(MagicAltar.KEY, MAGIC_ALTAR)
                .shape("   ", "dpd", "dsd")
                .setIngredient('p', PEDESTAL)
                .setIngredient('s', new ItemStack(Material.SMOOTH_STONE_SLAB))
                .setIngredient('d', SHIMMER_DUST_2);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("magic"),
                Material.LIGHT_BLUE_DYE,
                6L,
                Pedestal.MAGIC_PEDESTAL_KEY,
                MagicAltar.KEY,
                COVALENT_BINDER_KEY
        ).register();
    }

    public static final NamespacedKey WITHER_PROOF_OBSIDIAN_KEY = pylonKey("wither_proof_obsidian");
    public static final ItemStack WITHER_PROOF_OBSIDIAN = ItemStackBuilder.pylonItem(Material.OBSIDIAN, WITHER_PROOF_OBSIDIAN_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, WITHER_PROOF_OBSIDIAN);
        GuidePages.BUILDING.addItem(WITHER_PROOF_OBSIDIAN_KEY);
        ShapedRecipe recipe = new ShapedRecipe(WITHER_PROOF_OBSIDIAN_KEY, WITHER_PROOF_OBSIDIAN)
                .shape("fbf", "bob", "fbf")
                .setIngredient('f', FERRODURALUM_INGOT)
                .setIngredient('b', new ItemStack(Material.IRON_BARS))
                .setIngredient('o', COMPRESSED_OBSIDIAN);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }
    // </editor-fold>

    // <editor-fold desc="Fluids" defaultstate=collapsed>
    static {
        PylonItem.register(FluidPipe.class, FluidPipe.PIPE_WOOD_STACK);
        GuidePages.FLUID_MACHINES.addItem(FluidPipe.PIPE_WOOD_KEY);
        ItemStack output = new ItemStack(FluidPipe.PIPE_WOOD_STACK);
        output.setAmount(4);
        ShapedRecipe recipe = new ShapedRecipe(FluidPipe.PIPE_WOOD_KEY, output)
                .shape("www", "   ", "www")
                .setIngredient('w', new RecipeChoice.MaterialChoice(Tag.PLANKS));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(FluidPipe.class, FluidPipe.PIPE_COPPER_STACK);
        GuidePages.FLUID_MACHINES.addItem(FluidPipe.PIPE_COPPER_KEY);
        ItemStack output = new ItemStack(FluidPipe.PIPE_COPPER_STACK);
        output.setAmount(4);
        ShapedRecipe recipe = new ShapedRecipe(FluidPipe.PIPE_COPPER_KEY, output)
                .shape("ccc", "   ", "ccc")
                .setIngredient('c', COPPER_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(FluidPipe.class, FluidPipe.PIPE_OBSIDIAN_STACK);
        GuidePages.FLUID_MACHINES.addItem(FluidPipe.PIPE_OBSIDIAN_KEY);
        ItemStack output = new ItemStack(FluidPipe.PIPE_OBSIDIAN_STACK);
        output.setAmount(4);
        ShapedRecipe recipe = new ShapedRecipe(FluidPipe.PIPE_OBSIDIAN_KEY, output)
                .shape("ooo", "   ", "ooo")
                .setIngredient('o', new ItemStack(Material.OBSIDIAN));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(
                PortableFluidTank.Item.class,
                PortableFluidTank.Item.PORTABLE_FLUID_TANK_WOOD_STACK,
                PortableFluidTank.PORTABLE_FLUID_TANK_WOOD_KEY
        );
        GuidePages.FLUID_MACHINES.addItem(PortableFluidTank.PORTABLE_FLUID_TANK_WOOD_KEY);
        ShapedRecipe recipe = new ShapedRecipe(PortableFluidTank.PORTABLE_FLUID_TANK_WOOD_KEY, PortableFluidTank.Item.PORTABLE_FLUID_TANK_WOOD_STACK)
                .shape("gwg", "w w", "gwg")
                .setIngredient('w', new RecipeChoice.MaterialChoice(Tag.PLANKS))
                .setIngredient('g', new ItemStack(Material.GLASS));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(
                PortableFluidTank.Item.class,
                PortableFluidTank.Item.PORTABLE_FLUID_TANK_COPPER_STACK,
                PortableFluidTank.PORTABLE_FLUID_TANK_COPPER_KEY
        );
        GuidePages.FLUID_MACHINES.addItem(PortableFluidTank.PORTABLE_FLUID_TANK_COPPER_KEY);
        ShapedRecipe recipe = new ShapedRecipe(PortableFluidTank.PORTABLE_FLUID_TANK_COPPER_KEY, PortableFluidTank.Item.PORTABLE_FLUID_TANK_COPPER_STACK)
                .shape("gcg", "c c", "gcg")
                .setIngredient('c', COPPER_SHEET)
                .setIngredient('g', new ItemStack(Material.GLASS));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack WATER_PUMP = ItemStackBuilder.pylonItem(Material.BLUE_TERRACOTTA, WaterPump.KEY)
            .build();

    static {
        PylonItem.register(WaterPump.Item.class, WATER_PUMP, WaterPump.KEY);
        GuidePages.FLUID_MACHINES.addItem(WaterPump.KEY);
        ShapedRecipe recipe = new ShapedRecipe(WaterPump.KEY, WATER_PUMP)
                .shape("iii", "ibi", "ipi")
                .setIngredient('i', IRON_SHEET)
                .setIngredient('p', FluidPipe.PIPE_COPPER_STACK)
                .setIngredient('b', new ItemStack(Material.BUCKET));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_VALVE = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, FluidValve.KEY)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_VALVE, FluidValve.KEY);
        GuidePages.FLUID_MACHINES.addItem(FluidValve.KEY);
    }

    public static final ItemStack FLUID_FILTER = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, FluidFilter.KEY)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_FILTER, FluidFilter.KEY);
        GuidePages.FLUID_MACHINES.addItem(FluidFilter.KEY);
    }

    public static final ItemStack FLUID_METER = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, FluidMeter.KEY)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_METER, FluidMeter.KEY);
        GuidePages.FLUID_MACHINES.addItem(FluidMeter.KEY);
    }

    public static final ItemStack WATER_PLACER = ItemStackBuilder.pylonItem(Material.DISPENSER, FluidPlacer.WATER_PLACER_KEY)
            .build();
    static {
        PylonItem.register(FluidPlacer.Item.class, WATER_PLACER, FluidPlacer.WATER_PLACER_KEY);
        GuidePages.FLUID_MACHINES.addItem(FluidPlacer.WATER_PLACER_KEY);
    }

    public static final ItemStack LAVA_PLACER = ItemStackBuilder.pylonItem(Material.DISPENSER, FluidPlacer.LAVA_PLACER_KEY)
            .build();
    static {
        PylonItem.register(FluidPlacer.Item.class, LAVA_PLACER, FluidPlacer.LAVA_PLACER_KEY);
        GuidePages.FLUID_MACHINES.addItem(FluidPlacer.LAVA_PLACER_KEY);
    }

    public static final ItemStack WATER_DRAINER = ItemStackBuilder.pylonItem(Material.DISPENSER, FluidDrainer.WATER_DRAINER_KEY)
            .build();
    static {
        PylonItem.register(FluidDrainer.Item.class, WATER_DRAINER, FluidDrainer.WATER_DRAINER_KEY);
        GuidePages.FLUID_MACHINES.addItem(FluidDrainer.WATER_DRAINER_KEY);
    }

    public static final ItemStack LAVA_DRAINER = ItemStackBuilder.pylonItem(Material.DISPENSER, FluidDrainer.LAVA_DRAINER_KEY)
            .build();
    static {
        PylonItem.register(FluidDrainer.Item.class, LAVA_DRAINER, FluidDrainer.LAVA_DRAINER_KEY);
        GuidePages.FLUID_MACHINES.addItem(FluidDrainer.LAVA_DRAINER_KEY);
    }

    public static final ItemStack FLUID_VOIDER_1 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, FluidVoider.FLUID_VOIDER_1_KEY)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_1, FluidVoider.FLUID_VOIDER_1_KEY);
        GuidePages.FLUID_MACHINES.addItem(FluidVoider.FLUID_VOIDER_1_KEY);
    }

    public static final ItemStack FLUID_VOIDER_2 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, FluidVoider.FLUID_VOIDER_2_KEY)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_2, FluidVoider.FLUID_VOIDER_2_KEY);
        GuidePages.FLUID_MACHINES.addItem(FluidVoider.FLUID_VOIDER_2_KEY);
    }

    public static final ItemStack FLUID_VOIDER_3 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, FluidVoider.FLUID_VOIDER_3_KEY)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_3, FluidVoider.FLUID_VOIDER_3_KEY);
        GuidePages.FLUID_MACHINES.addItem(FluidVoider.FLUID_VOIDER_3_KEY);
    }

    static {
        PylonItem.register(Loupe.class, Loupe.STACK);
        GuidePages.RESEARCH.addItem(Loupe.KEY);
        ShapedRecipe recipe = new ShapedRecipe(Loupe.KEY, Loupe.STACK)
                .shape(" C ", "CGC", " C ")
                .setIngredient('C', Material.COPPER_INGOT)
                .setIngredient('G', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(ResearchPack.class, ResearchPack.RESEARCH_PACK_1_STACK);
        GuidePages.RESEARCH.addItem(ResearchPack.RESEARCH_PACK_1_KEY);
        // TODO recipe when fluid api is done
    }

    public static final ItemStack DIMENSIONAL_BARREL = ItemStackBuilder.pylonItem(Material.BARREL, DimensionalBarrel.KEY)
            .build();
    static {
        PylonItem.register(DimensionalBarrel.Item.class, DIMENSIONAL_BARREL, DimensionalBarrel.KEY);
        GuidePages.BUILDING.addItem(DimensionalBarrel.KEY);
        ShapedRecipe recipe = new ShapedRecipe(DimensionalBarrel.KEY, DIMENSIONAL_BARREL)
                .shape("CBC", "BEB", "CBC")
                .setIngredient('C', COVALENT_BINDER)
                .setIngredient('B', Material.BARREL)
                .setIngredient('E', Material.ENDER_EYE);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SLURRY_STRAINER = ItemStackBuilder.pylonItem(Material.COPPER_GRATE, FluidStrainer.KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, SLURRY_STRAINER, FluidStrainer.KEY);
        ShapedRecipe recipe = new ShapedRecipe(FluidStrainer.KEY, SLURRY_STRAINER)
                .shape("c c", " C ", "c c")
                .setIngredient('c', COPPER_SHEET)
                .setIngredient('C', Material.COPPER_GRATE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

    }
    // </editor-fold>

    //<editor-fold desc="Smeltery" defaultstate="collapsed">
    public static final NamespacedKey REFRACTORY_BRICK_KEY = pylonKey("refractory_brick");
    public static final ItemStack REFRACTORY_BRICK = ItemStackBuilder.pylonItem(Material.DEEPSLATE_TILES, REFRACTORY_BRICK_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, REFRACTORY_BRICK, PylonBlocks.REFRACTORY_BRICK_KEY);
        GuidePages.SMELTING.addItem(PylonBlocks.REFRACTORY_BRICK_KEY);
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("refractory_brick"), REFRACTORY_BRICK.asQuantity(4))
                .shape("BBB", "NRN", "BBB")
                .setIngredient('B', Material.BRICK)
                .setIngredient('N', Material.NETHER_BRICK)
                .setIngredient('R', Material.RED_NETHER_BRICKS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey SMELTERY_CONTROLLER_KEY = pylonKey("smeltery_controller");
    public static final ItemStack SMELTERY_CONTROLLER = ItemStackBuilder.pylonItem(Material.BLAST_FURNACE, SMELTERY_CONTROLLER_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_CONTROLLER, SmelteryController.KEY);
        GuidePages.SMELTING.addItem(SmelteryController.KEY);
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("smeltery_controller"), SMELTERY_CONTROLLER)
                .shape("RBR", "BFB", "RBR")
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('F', Material.BLAST_FURNACE)
                .setIngredient('R', Material.REDSTONE);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey SMELTERY_INPUT_HATCH_KEY = pylonKey("smeltery_input_hatch");
    public static final ItemStack SMELTERY_INPUT_HATCH = ItemStackBuilder.pylonItem(Material.LIGHT_BLUE_TERRACOTTA, SMELTERY_INPUT_HATCH_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_INPUT_HATCH, SmelteryInputHatch.KEY);
        GuidePages.SMELTING.addItem(SmelteryInputHatch.KEY);
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("smeltery_input_hatch"), SMELTERY_INPUT_HATCH)
                .shape("IBI", "B B", "IBI")
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('I', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey SMELTERY_OUTPUT_HATCH_KEY = pylonKey("smeltery_output_hatch");
    public static final ItemStack SMELTERY_OUTPUT_HATCH = ItemStackBuilder.pylonItem(Material.ORANGE_TERRACOTTA, SMELTERY_OUTPUT_HATCH_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_OUTPUT_HATCH, SmelteryOutputHatch.KEY);
        GuidePages.SMELTING.addItem(SmelteryOutputHatch.KEY);
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("smeltery_output_hatch"), SMELTERY_OUTPUT_HATCH)
                .shape("IBI", "BPB", "IBI")
                .setIngredient('P', FluidPipe.PIPE_OBSIDIAN_STACK)
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('I', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey SMELTERY_HOPPER_KEY = pylonKey("smeltery_hopper");
    public static final ItemStack SMELTERY_HOPPER = ItemStackBuilder.pylonItem(Material.HOPPER, SMELTERY_HOPPER_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_HOPPER, SmelteryHopper.KEY);
        GuidePages.SMELTING.addItem(SmelteryHopper.KEY);
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("smeltery_hopper"), SMELTERY_HOPPER)
                .shape("I I", "IBI", " B ")
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('I', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey SMELTERY_CASTER_KEY = pylonKey("smeltery_caster");
    public static final ItemStack SMELTERY_CASTER = ItemStackBuilder.pylonItem(Material.BRICKS, SMELTERY_CASTER_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_CASTER, SmelteryCaster.KEY);
        GuidePages.SMELTING.addItem(SmelteryCaster.KEY);
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("smeltery_caster"), SMELTERY_CASTER)
                .shape("B B", "BPB", "B B")
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('P', Material.FLOWER_POT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final NamespacedKey SMELTERY_BURNER_KEY = pylonKey("smeltery_burner");
    public static final ItemStack SMELTERY_BURNER = ItemStackBuilder.pylonItem(Material.FURNACE, SMELTERY_BURNER_KEY)
            .build();

    static {
        PylonItem.register(PylonItem.class, SMELTERY_BURNER, SmelteryBurner.KEY);
        GuidePages.SMELTING.addItem(SmelteryBurner.KEY);
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("smeltery_burner"), SMELTERY_BURNER)
                .shape("BBB", "BFB", "BBB")
                .setIngredient('B', REFRACTORY_BRICK)
                .setIngredient('F', Material.FURNACE);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }
    // </editor-fold>

    static {
        PylonItem.register(ExplosiveTarget.Item.class, ExplosiveTarget.EXPLOSIVE_TARGET_STACK, ExplosiveTarget.EXPLOSIVE_TARGET_KEY);
        GuidePages.BUILDING.addItem(ExplosiveTarget.EXPLOSIVE_TARGET_KEY);
        ShapedRecipe recipe = new ShapedRecipe(ExplosiveTarget.EXPLOSIVE_TARGET_KEY, ExplosiveTarget.EXPLOSIVE_TARGET_STACK);
        recipe.shape(
                "TTT",
                "TXT",
                "TTT"
        );
        recipe.setIngredient('T', Material.TNT);
        recipe.setIngredient('X', Material.TARGET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(ExplosiveTarget.Item.class, ExplosiveTarget.EXPLOSIVE_TARGET_FIERY_STACK, ExplosiveTarget.EXPLOSIVE_TARGET_FIERY_KEY);
        GuidePages.BUILDING.addItem(ExplosiveTarget.EXPLOSIVE_TARGET_FIERY_KEY);
        ShapelessRecipe recipe = new ShapelessRecipe(ExplosiveTarget.EXPLOSIVE_TARGET_FIERY_KEY, ExplosiveTarget.EXPLOSIVE_TARGET_FIERY_STACK);
        recipe.addIngredient(ExplosiveTarget.EXPLOSIVE_TARGET_STACK);
        recipe.addIngredient(Material.FIRE_CHARGE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }

    static {
        PylonItem.register(ExplosiveTarget.Item.class, ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_STACK, ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_KEY);
        GuidePages.BUILDING.addItem(ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_KEY);
        ShapelessRecipe recipe = new ShapelessRecipe(ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_KEY, ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_STACK);
        recipe.addIngredient(4, ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_STACK);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }

    static {
        PylonItem.register(ExplosiveTarget.Item.class, ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_FIERY_STACK, ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_FIERY_KEY);
        GuidePages.BUILDING.addItem(ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_FIERY_KEY);
        ShapelessRecipe recipe = new ShapelessRecipe(ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_FIERY_KEY, ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_FIERY_STACK);
        recipe.addIngredient(ExplosiveTarget.EXPLOSIVE_TARGET_SUPER_STACK);
        recipe.addIngredient(Material.FIRE_CHARGE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }

    static {
        PylonItem.register(Immobilizer.Item.class, Immobilizer.STACK, Immobilizer.KEY);
        GuidePages.BUILDING.addItem(Immobilizer.KEY);
        ShapedRecipe recipe = new ShapedRecipe(Immobilizer.KEY, Immobilizer.STACK);
        recipe.shape(
                "NNN",
                "DCD",
                "DDD"
        );
        recipe.setIngredient('N', Material.NETHER_STAR);
        recipe.setIngredient('D', SHIMMER_DUST_3);
        recipe.setIngredient('C', Material.HEAVY_CORE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(Elevator.Item.class, Elevator.ELEVATOR_1_STACK, Elevator.ELEVATOR_1_KEY);
        ShapedRecipe recipe = new ShapedRecipe(Elevator.ELEVATOR_1_KEY, Elevator.ELEVATOR_1_STACK)
                .shape("   ", "QPQ", "   ")
                .setIngredient('Q', Material.QUARTZ_BLOCK)
                .setIngredient('P', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(Elevator.Item.class, Elevator.ELEVATOR_2_STACK, Elevator.ELEVATOR_2_KEY);
        ShapedRecipe recipe = new ShapedRecipe(Elevator.ELEVATOR_2_KEY, Elevator.ELEVATOR_2_STACK)
                .shape("PPP", "PEP", "PPP")
                .setIngredient('E', Elevator.ELEVATOR_1_STACK)
                .setIngredient('P', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    static {
        PylonItem.register(Elevator.Item.class, Elevator.ELEVATOR_3_STACK, Elevator.ELEVATOR_3_KEY);
        ShapedRecipe recipe = new ShapedRecipe(Elevator.ELEVATOR_3_KEY, Elevator.ELEVATOR_3_STACK)
                .shape("PPP", "PEP", "PPP")
                .setIngredient('E', Elevator.ELEVATOR_2_STACK)
                .setIngredient('P', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PRESS = ItemStackBuilder.pylonItem(Material.COMPOSTER, Press.KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, PRESS, Press.KEY);
    }

    public static final ItemStack HYDRAULIC_GRINDSTONE_TURNER = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE, HydraulicGrindstoneTurner.KEY)
            .build();
    static {
        PylonItem.register(HydraulicGrindstoneTurner.Item.class, HYDRAULIC_GRINDSTONE_TURNER, HydraulicGrindstoneTurner.KEY);
    }

    public static final ItemStack HYDRAULIC_MIXING_ATTACHMENT = ItemStackBuilder.pylonItem(Material.GRAY_CONCRETE, HydraulicMixingAttachment.KEY)
            .build();
    static {
        PylonItem.register(HydraulicMixingAttachment.Item.class, HYDRAULIC_MIXING_ATTACHMENT, HydraulicMixingAttachment.KEY);
    }

    public static final ItemStack HYDRAULIC_PRESS_PISTON = ItemStackBuilder.pylonItem(Material.BROWN_CONCRETE, HydraulicPressPiston.KEY)
            .build();
    static {
        PylonItem.register(HydraulicPressPiston.Item.class, HYDRAULIC_PRESS_PISTON, HydraulicPressPiston.KEY);
    }

    public static final ItemStack HYDRAULIC_HAMMER_HEAD = ItemStackBuilder.pylonItem(Material.STONE_BRICKS, HydraulicHammerHead.KEY)
            .build();
    static {
        PylonItem.register(HydraulicHammerHead.Item.class, HYDRAULIC_HAMMER_HEAD, HydraulicHammerHead.KEY);
    }

    public static final ItemStack SOLAR_LENS = ItemStackBuilder.pylonItem(Material.GLASS_PANE, PylonBlocks.SOLAR_LENS_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, SOLAR_LENS, PylonBlocks.SOLAR_LENS_KEY);
    }

    public static final ItemStack PURIFICATION_TOWER_GLASS = ItemStackBuilder.pylonItem(Material.LIGHT_GRAY_STAINED_GLASS, PylonBlocks.PURIFICATION_TOWER_GLASS_KEY)
            .build();
    static {
        PylonItem.register(PylonItem.class, PURIFICATION_TOWER_GLASS, PylonBlocks.PURIFICATION_TOWER_GLASS_KEY);
    }

    public static final ItemStack PURIFICATION_TOWER_CAP = ItemStackBuilder.pylonItem(Material.QUARTZ_SLAB, PylonBlocks.PURIFICATION_TOWER_CAP)
            .build();
    static {
        PylonItem.register(PylonItem.class, PURIFICATION_TOWER_CAP, PylonBlocks.PURIFICATION_TOWER_CAP);
    }

    static {
        PylonItem.register(SolarPurificationTower.Item.class, SolarPurificationTower.SOLAR_PURIFICATION_TOWER_1_STACK, SolarPurificationTower.SOLAR_PURIFICATION_TOWER_1_KEY);
    }

    static {
        PylonItem.register(SolarPurificationTower.Item.class, SolarPurificationTower.SOLAR_PURIFICATION_TOWER_2_STACK, SolarPurificationTower.SOLAR_PURIFICATION_TOWER_2_KEY);
    }

    static {
        PylonItem.register(SolarPurificationTower.Item.class, SolarPurificationTower.SOLAR_PURIFICATION_TOWER_3_STACK, SolarPurificationTower.SOLAR_PURIFICATION_TOWER_3_KEY);
    }

    static {
        PylonItem.register(SolarPurificationTower.Item.class, SolarPurificationTower.SOLAR_PURIFICATION_TOWER_4_STACK, SolarPurificationTower.SOLAR_PURIFICATION_TOWER_4_KEY);
    }

    static {
        PylonItem.register(SolarPurificationTower.Item.class, SolarPurificationTower.SOLAR_PURIFICATION_TOWER_5_STACK, SolarPurificationTower.SOLAR_PURIFICATION_TOWER_5_KEY);
    }

    static {
        PylonItem.register(CoalFiredPurificationTower.Item.class, CoalFiredPurificationTower.COAL_FIRED_PURIFICATION_TOWER_STACK, CoalFiredPurificationTower.COAL_FIRED_PURIFICATION_TOWER_KEY);
    }

    private static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }

    // Calling this method forces all the static blocks to run, which initializes our items
    public static void initialize() {
    }
}
