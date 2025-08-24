package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.building.DimensionalBarrel;
import io.github.pylonmc.pylon.base.content.building.Elevator;
import io.github.pylonmc.pylon.base.content.building.ExplosiveTarget;
import io.github.pylonmc.pylon.base.content.building.Immobilizer;
import io.github.pylonmc.pylon.base.content.building.sponge.PowerfulLavaSponge;
import io.github.pylonmc.pylon.base.content.building.sponge.PowerfulWaterSponge;
import io.github.pylonmc.pylon.base.content.building.sponge.HotLavaSponge;
import io.github.pylonmc.pylon.base.content.building.sponge.WetWaterSponge;
import io.github.pylonmc.pylon.base.content.combat.BeheadingSword;
import io.github.pylonmc.pylon.base.content.combat.IceArrow;
import io.github.pylonmc.pylon.base.content.combat.RecoilArrow;
import io.github.pylonmc.pylon.base.content.machines.fluid.*;
import io.github.pylonmc.pylon.base.content.machines.hydraulics.*;
import io.github.pylonmc.pylon.base.content.machines.simple.CoreDrill;
import io.github.pylonmc.pylon.base.content.machines.simple.ImprovedManualCoreDrill;
import io.github.pylonmc.pylon.base.content.machines.simple.Press;
import io.github.pylonmc.pylon.base.content.machines.smelting.PitKiln;
import io.github.pylonmc.pylon.base.content.magic.FireproofRune;
import io.github.pylonmc.pylon.base.recipes.FireproofRuneRecipe;
import io.github.pylonmc.pylon.base.content.science.Loupe;
import io.github.pylonmc.pylon.base.content.science.ResearchPack;
import io.github.pylonmc.pylon.base.content.tools.*;
import io.github.pylonmc.pylon.base.recipes.*;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.Tool;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.util.TriState;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.SILVER_DUST,
                SILVER_INGOT,
                SILVER_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack CRUSHED_RAW_ZINC = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.CRUSHED_RAW_ZINC)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_ZINC);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_ZINC);
    }

    public static final ItemStack ZINC_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.ZINC_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, ZINC_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.ZINC_INGOT);

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                baseKey("zinc_smelting"),
                List.of(BaseItems.CRUSHED_RAW_ZINC),
                List.of(BaseItems.ZINC_INGOT)
        ));
    }

    public static final ItemStack ZINC_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.ZINC_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, ZINC_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.ZINC_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.ROCK_DUST,
                new ItemStack(Material.COBBLESTONE),
                ROCK_DUST.asQuantity(2),
                2,
                Material.COBBLESTONE.createBlockData()
        ));
    }

    public static final ItemStack CRUSHED_RAW_LEAD = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.CRUSHED_RAW_LEAD)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_LEAD);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_LEAD);
    }

    public static final ItemStack LEAD_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.LEAD_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, LEAD_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.LEAD_INGOT);

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                baseKey("lead_smelting"),
                List.of(BaseItems.CRUSHED_RAW_LEAD),
                List.of(BaseItems.LEAD_INGOT)
        ));
    }

    public static final ItemStack LEAD_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.LEAD_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, LEAD_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.LEAD_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.LEAD_DUST,
                LEAD_INGOT,
                LEAD_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack CRUSHED_RAW_TIN = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.CRUSHED_RAW_TIN)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_TIN);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_TIN);
    }

    public static final ItemStack TIN_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.TIN_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.TIN_INGOT);

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                baseKey("tin_smelting"),
                List.of(BaseItems.CRUSHED_RAW_TIN),
                List.of(BaseItems.TIN_INGOT)
        ));
    }

    public static final ItemStack TIN_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.TIN_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.TIN_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("coal_dust_from_coal"),
                new ItemStack(Material.COAL),
                COAL_DUST,
                2,
                Material.COAL_BLOCK.createBlockData()
        ));

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                baseKey("coal_to_carbon"),
                List.of(BaseItems.COAL_DUST.asQuantity(2)),
                List.of(BaseItems.CARBON_DUST)
        ));
    }

    // Not technically a dust but whatever
    public static final ItemStack SULFUR = ItemStackBuilder.pylonItem(Material.YELLOW_DYE, BaseKeys.SULFUR)
            .build();
    static {
        PylonItem.register(PylonItem.class, SULFUR);
        BasePages.RESOURCES.addItem(BaseKeys.SULFUR);

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                baseKey("redstone_smelting"),
                List.of(new ItemStack(Material.REDSTONE)),
                List.of(BaseItems.SULFUR)
        ));
    }

    public static final ItemStack BRONZE_INGOT = ItemStackBuilder.pylonItem(Material.GOLD_INGOT, BaseKeys.BRONZE_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.BRONZE_INGOT);

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                baseKey("bronze"),
                List.of(new ItemStack(Material.COPPER_INGOT, 2), BaseItems.TIN_INGOT),
                List.of(BaseItems.BRONZE_INGOT)
        ));
    }

    public static final ItemStack BRONZE_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.BRONZE_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.BRONZE_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                baseKey("brass"),
                List.of(new ItemStack(Material.COPPER_INGOT, 2), BaseItems.ZINC_INGOT),
                List.of(BaseItems.BRASS_INGOT)
        ));
    }

    public static final ItemStack BRASS_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.BRASS_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRASS_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.BRASS_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
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

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
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

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
                BaseKeys.IRON_SHEET,
                new ItemStack(Material.IRON_INGOT),
                IRON_SHEET,
                MiningLevel.IRON,
                0.25f
        ));
    }
    //</editor-fold>

    //<editor-fold desc="Hammers" defaultstate=collapsed>
    public static final ItemStack HAMMER_STONE= Hammer.createItemStack(
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

    public static final ItemStack HAMMER_IRON= Hammer.createItemStack(
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
            BaseKeys.HAMMER_DIAMOND, Material.DIAMOND_PICKAXE, (1.0/ 1) - 4, 2, 5
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

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
                BaseKeys.FERRODURALUM_SHEET,
                FERRODURALUM_INGOT,
                FERRODURALUM_SHEET,
                MiningLevel.IRON,
                0.25f
        ));
    }

    public static final ItemStack FERRODURALUM_SWORD = ItemStackBuilder.pylonItem(Material.GOLDEN_SWORD, BaseKeys.FERRODURALUM_SWORD)
            .set(DataComponentTypes.MAX_DAMAGE, 500)
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
            .set(DataComponentTypes.TOOL, Tool.tool()
                    .defaultMiningSpeed(6.5F)
                    .addRule(Tool.rule(
                            RegistrySet.keySet(BlockTypeTagKeys.MINEABLE_AXE.registryKey()),
                            6.5F,
                            TriState.TRUE
                    ))
            )
            .set(DataComponentTypes.MAX_DAMAGE, 500)
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
            .set(DataComponentTypes.TOOL, Tool.tool()
                    .defaultMiningSpeed(6.5F)
                    .addRule(Tool.rule(
                            RegistrySet.keySet(BlockTypeTagKeys.MINEABLE_PICKAXE.registryKey()),
                            6.5F,
                            TriState.TRUE
                    ))
            )
            .set(DataComponentTypes.MAX_DAMAGE, 500)
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
            .set(DataComponentTypes.TOOL, Tool.tool()
                    .defaultMiningSpeed(6.5F)
                    .addRule(Tool.rule(
                            RegistrySet.keySet(BlockTypeTagKeys.MINEABLE_SHOVEL.registryKey()),
                            6.5F,
                            TriState.TRUE
                    ))
            )
            .set(DataComponentTypes.MAX_DAMAGE, 500)
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
            .set(DataComponentTypes.MAX_DAMAGE, 500)
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
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

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.DOUGH,
                List.of(FLOUR),
                BaseFluids.WATER,
                333,
                FluidOrItem.of(DOUGH),
                false
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
        ItemStack gunpowderInput = new ItemStack(Material.GUNPOWDER, 4);
        ItemStack output = COVALENT_BINDER.asQuantity(6);
        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.COVALENT_BINDER,
                List.of(gunpowderInput, new ItemStack(Material.EMERALD), SHIMMER_DUST_1),
                BaseFluids.WATER,
                1000,
                FluidOrItem.of(output),
                true
        ));
    }


    public static final ItemStack SHIMMER_DUST_2 = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.SHIMMER_DUST_2)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_2);
        BasePages.RESOURCES.addItem(BaseKeys.SHIMMER_DUST_2);

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.SHIMMER_DUST_2,
                List.of(
                        SHIMMER_DUST_1,
                        GOLD_DUST,
                        new ItemStack(Material.REDSTONE)
                ),
                BaseFluids.WATER,
                333,
                FluidOrItem.of(SHIMMER_DUST_2),
                false
        ));
    }

    public static final ItemStack SHIMMER_DUST_3 = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.SHIMMER_DUST_3)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_3);
        BasePages.RESOURCES.addItem(BaseKeys.SHIMMER_DUST_3);

        MagicAltarRecipe.RECIPE_TYPE.addRecipe(new MagicAltarRecipe(
                BaseKeys.SHIMMER_DUST_3,
                new ArrayList<>(Arrays.asList(
                        new ItemStack(Material.REDSTONE_BLOCK),
                        null,
                        COVALENT_BINDER,
                        COVALENT_BINDER,
                        null,
                        COVALENT_BINDER,
                        COVALENT_BINDER,
                        null
                )),
                SHIMMER_DUST_2,
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

        MagicAltarRecipe.RECIPE_TYPE.addRecipe(new MagicAltarRecipe(
                BaseKeys.SHIMMER_SKULL,
                new ArrayList<>(Arrays.asList(
                        SHIMMER_DUST_3,
                        null,
                        SHIMMER_DUST_3,
                        null,
                        SHIMMER_DUST_3,
                        null,
                        SHIMMER_DUST_3,
                        null
                )),
                new ItemStack(Material.WITHER_SKELETON_SKULL),
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

        ItemStack shapedOutput = new ItemStack(FLUID_PIPE_WOOD).asQuantity(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_WOOD, shapedOutput)
                .shape("www", "   ", "www")
                .setIngredient('w', new RecipeChoice.MaterialChoice(Tag.PLANKS));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        ItemStack benderOutput = new ItemStack(FLUID_PIPE_WOOD);
        benderOutput.setAmount(4);
        for (Material material : Tag.LOGS.getValues()) {
            PipeBendingRecipe.RECIPE_TYPE.addRecipe(new PipeBendingRecipe(
                    BaseKeys.FLUID_PIPE_WOOD,
                    new ItemStack(material),
                    benderOutput,
                    Material.OAK_LOG.createBlockData(),
                    160
            ));
        }
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

        ItemStack shapedOutput = new ItemStack(FLUID_PIPE_COPPER).asQuantity(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_COPPER, shapedOutput)
                .shape("ccc", "   ", "ccc")
                .setIngredient('c', COPPER_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        ItemStack benderOutput = new ItemStack(FLUID_PIPE_COPPER);
        benderOutput.setAmount(9);
        PipeBendingRecipe.RECIPE_TYPE.addRecipe(new PipeBendingRecipe(
                BaseKeys.FLUID_PIPE_COPPER,
                new ItemStack(Material.COPPER_BLOCK),
                benderOutput,
                Material.COPPER_BLOCK.createBlockData(),
                360
        ));
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

        ItemStack output = new ItemStack(FLUID_PIPE_OBSIDIAN).asQuantity(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_OBSIDIAN, output)
                .shape("ooo", "   ", "ooo")
                .setIngredient('o', new ItemStack(Material.OBSIDIAN));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        PipeBendingRecipe.RECIPE_TYPE.addRecipe(new PipeBendingRecipe(
                BaseKeys.FLUID_PIPE_OBSIDIAN,
                new ItemStack(Material.OBSIDIAN),
                FLUID_PIPE_OBSIDIAN,
                Material.OBSIDIAN.createBlockData(),
                40
        ));
    }

    public static final ItemStack FLUID_PIPE_CREATIVE = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_CREATIVE)
            .set(DataComponentTypes.ITEM_MODEL,
                    Material.getMaterial(
                            Settings.get(BaseKeys.FLUID_PIPE_CREATIVE).getOrThrow("material", String.class).toUpperCase()
                    ).getKey()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_CREATIVE, BaseKeys.FLUID_PIPE_CREATIVE);
        PylonGuide.hideItem(BaseKeys.FLUID_PIPE_CREATIVE);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_WOOD
            = ItemStackBuilder.pylonItem(Material.BROWN_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_WOOD)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0))
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
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0))
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

    public static final ItemStack PORTABLE_FLUID_TANK_OBSIDIAN
            = ItemStackBuilder.pylonItem(Material.BLACK_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_OBSIDIAN)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0))
            .build();
    static {
        PylonItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_OBSIDIAN,
                BaseKeys.PORTABLE_FLUID_TANK_OBSIDIAN
        );
        BasePages.FLUID_MACHINES.addItem(BaseKeys.PORTABLE_FLUID_TANK_OBSIDIAN);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_FLUID_TANK_OBSIDIAN, PORTABLE_FLUID_TANK_OBSIDIAN)
                .shape("gcg", "c c", "gcg")
                .setIngredient('c', Material.OBSIDIAN)
                .setIngredient('g', new ItemStack(Material.GLASS));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_TANK
            = ItemStackBuilder.pylonItem(Material.GRAY_TERRACOTTA, BaseKeys.FLUID_TANK)
            .build();
    static {
        PylonItem.register(FluidTank.Item.class, FLUID_TANK, BaseKeys.FLUID_TANK);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_TANK);
    }

    public static final ItemStack FLUID_TANK_CASING_WOOD
            = ItemStackBuilder.pylonItem(Material.BROWN_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_WOOD)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_WOOD, BaseKeys.FLUID_TANK_CASING_WOOD);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_TANK_CASING_WOOD);
    }

    public static final ItemStack FLUID_TANK_CASING_COPPER
            = ItemStackBuilder.pylonItem(Material.ORANGE_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_COPPER)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_COPPER, BaseKeys.FLUID_TANK_CASING_COPPER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_TANK_CASING_COPPER);
    }

    public static final ItemStack FLUID_TANK_CASING_OBSIDIAN
            = ItemStackBuilder.pylonItem(Material.BLACK_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_OBSIDIAN)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_OBSIDIAN, BaseKeys.FLUID_TANK_CASING_OBSIDIAN);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_TANK_CASING_OBSIDIAN);
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

    public static final ItemStack CREATIVE_FLUID_VOIDER = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.CREATIVE_FLUID_VOIDER)
            .set(DataComponentTypes.ITEM_MODEL, Material.PINK_CONCRETE.getKey())
            .build();
    static {
        PylonItem.register(FluidVoider.Item.class, CREATIVE_FLUID_VOIDER, BaseKeys.CREATIVE_FLUID_VOIDER);
        PylonGuide.hideItem(BaseKeys.CREATIVE_FLUID_VOIDER);
    }

    public static final ItemStack CREATIVE_FLUID_SOURCE = ItemStackBuilder.pylonItem(Material.PINK_CONCRETE, BaseKeys.CREATIVE_FLUID_SOURCE)
            .build();
    static {
        PylonItem.register(PylonItem.class, CREATIVE_FLUID_SOURCE, BaseKeys.CREATIVE_FLUID_SOURCE);
        PylonGuide.hideItem(BaseKeys.CREATIVE_FLUID_SOURCE);
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

    public static final ItemStack FLUID_STRAINER = ItemStackBuilder.pylonItem(Material.COPPER_GRATE, BaseKeys.FLUID_STRAINER)
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_STRAINER, BaseKeys.FLUID_STRAINER);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.FLUID_STRAINER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_STRAINER, FLUID_STRAINER)
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

    public static final ItemStack PIT_KILN = ItemStackBuilder.pylonItem(Material.DECORATED_POT, BaseKeys.PIT_KILN)
            .build();
    static {
        PylonItem.register(PitKiln.Item.class, PIT_KILN, BaseKeys.PIT_KILN);
        BasePages.SMELTING.addItem(BaseKeys.PIT_KILN);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("pit_kiln"), PIT_KILN)
                .shape("B B", "B B", "BBB")
                .setIngredient('B', Material.BRICKS);
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

    public static final ItemStack HYDRAULIC_PIPE_BENDER = ItemStackBuilder.pylonItem(Material.BROWN_TERRACOTTA, BaseKeys.HYDRAULIC_PIPE_BENDER)
            .build();
    static {
        PylonItem.register(HydraulicPipeBender.Item.class, HYDRAULIC_PIPE_BENDER, BaseKeys.HYDRAULIC_PIPE_BENDER);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_PIPE_BENDER);
    }

    public static final ItemStack HYDRAULIC_TABLE_SAW = ItemStackBuilder.pylonItem(Material.COPPER_BLOCK, BaseKeys.HYDRAULIC_TABLE_SAW)
            .build();
    static {
        PylonItem.register(HydraulicTableSaw.Item.class, HYDRAULIC_TABLE_SAW, BaseKeys.HYDRAULIC_TABLE_SAW);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_TABLE_SAW);
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

        ItemStack arrowResult = ICE_ARROW.asQuantity(8);
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

    public static final ItemStack RECOIL_ARROW = ItemStackBuilder.pylonItem(Material.ARROW, BaseKeys.RECOIL_ARROW)
            .build();
    static {
        PylonItem.register(RecoilArrow.class, RECOIL_ARROW);
        BasePages.COMBAT.addItem(BaseKeys.RECOIL_ARROW);

        ItemStack output = RECOIL_ARROW.asQuantity(8);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.RECOIL_ARROW, RECOIL_ARROW)
                .shape("SSS", "SAS", "SSS")
                .setIngredient('S', Material.SLIME_BALL)
                .setIngredient('A', Material.ARROW);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FIREPROOF_RUNE = ItemStackBuilder.pylonItem(Material.FIREWORK_STAR, BaseKeys.FIREPROOF_RUNE)
            .set(
                    DataComponentTypes.DAMAGE_RESISTANT,
                    DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE)
            )
            .set(
                    DataComponentTypes.FIREWORK_EXPLOSION,
                    FireworkEffect.builder().withColor(Color.RED).build()
            )
            .build();
    static {
        PylonItem.register(FireproofRune.class, FIREPROOF_RUNE);
        BasePages.MAGIC.addItem(BaseKeys.FIREPROOF_RUNE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FIREPROOF_RUNE, FIREPROOF_RUNE)
                .shape(
                        "RGR",
                        "GNG",
                        "RGR")
                .setIngredient('R', Material.LAVA_BUCKET)
                .setIngredient('G', Material.GLOWSTONE_DUST)
                .setIngredient('N', Material.NETHER_STAR);

        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack MANUAL_CORE_DRILL_LEVER = ItemStackBuilder.pylonItem(Material.LEVER, BaseKeys.MANUAL_CORE_DRILL_LEVER)
            .build();
    static {
        PylonItem.register(PylonItem.class, MANUAL_CORE_DRILL_LEVER, BaseKeys.MANUAL_CORE_DRILL_LEVER);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.MANUAL_CORE_DRILL_LEVER);
    }

    public static final ItemStack MANUAL_CORE_DRILL = ItemStackBuilder.pylonItem(Material.CHISELED_STONE_BRICKS, BaseKeys.MANUAL_CORE_DRILL)
            .build();
    static {
        PylonItem.register(CoreDrill.Item.class, MANUAL_CORE_DRILL, BaseKeys.MANUAL_CORE_DRILL);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.MANUAL_CORE_DRILL);
    }

    public static final ItemStack IMPROVED_MANUAL_CORE_DRILL = ItemStackBuilder.pylonItem(Material.COPPER_BLOCK, BaseKeys.IMPROVED_MANUAL_CORE_DRILL)
            .build();
    static {
        PylonItem.register(ImprovedManualCoreDrill.Item.class, IMPROVED_MANUAL_CORE_DRILL, BaseKeys.IMPROVED_MANUAL_CORE_DRILL);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.IMPROVED_MANUAL_CORE_DRILL);
    }

    public static final ItemStack HYDRAULIC_CORE_DRILL = ItemStackBuilder.pylonItem(Material.COPPER_BULB, BaseKeys.HYDRAULIC_CORE_DRILL)
            .build();
    static {
        PylonItem.register(HydraulicCoreDrill.Item.class, HYDRAULIC_CORE_DRILL, BaseKeys.HYDRAULIC_CORE_DRILL);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_CORE_DRILL);
    }

    public static final ItemStack HYDRAULIC_CORE_DRILL_INPUT_HATCH = ItemStackBuilder.pylonItem(Material.LIGHT_BLUE_TERRACOTTA, BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH)
            .build();
    static {
        PylonItem.register(PylonItem.class, HYDRAULIC_CORE_DRILL_INPUT_HATCH, BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH);
    }

    public static final ItemStack HYDRAULIC_CORE_DRILL_OUTPUT_HATCH = ItemStackBuilder.pylonItem(Material.ORANGE_TERRACOTTA, BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH)
            .build();
    static {
        PylonItem.register(PylonItem.class, HYDRAULIC_CORE_DRILL_OUTPUT_HATCH, BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH);
    }

    public static final ItemStack SHALLOW_CORE_CHUNK = ItemStackBuilder.pylonItem(Material.FIREWORK_STAR, BaseKeys.SHALLOW_CORE_CHUNK)
            .build();
    static {
        PylonItem.register(PylonItem.class, SHALLOW_CORE_CHUNK, BaseKeys.SHALLOW_CORE_CHUNK);
        BasePages.RESOURCES.addItem(BaseKeys.SHALLOW_CORE_CHUNK);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.SHALLOW_CORE_CHUNK,
                SHALLOW_CORE_CHUNK,
                Map.of(
                        new ItemStack(Material.COAL), 0.5,
                        new ItemStack(Material.RAW_COPPER), 0.4,
                        CRUSHED_RAW_TIN, 0.3
                ),
                6,
                Material.STONE.createBlockData()
        ));
    }

    public static final ItemStack SUBSURFACE_CORE_CHUNK = ItemStackBuilder.pylonItem(Material.FIREWORK_STAR, BaseKeys.SUBSURFACE_CORE_CHUNK)
            .build();
    static {
        PylonItem.register(PylonItem.class, SUBSURFACE_CORE_CHUNK, BaseKeys.SUBSURFACE_CORE_CHUNK);
        BasePages.RESOURCES.addItem(BaseKeys.SUBSURFACE_CORE_CHUNK);

        ItemStack tinOutput = CRUSHED_RAW_TIN.clone().asQuantity(2);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.SUBSURFACE_CORE_CHUNK,
                SUBSURFACE_CORE_CHUNK,
                Map.of(
                        new ItemStack(Material.COAL, 2), 0.3,
                        new ItemStack(Material.RAW_COPPER, 2), 0.25,
                        tinOutput, 0.2,
                        new ItemStack(Material.RAW_IRON), 0.4
                ),
                8,
                Material.STONE.createBlockData()
        ));
    }

    public static final ItemStack INTERMEDIATE_CORE_CHUNK
            = ItemStackBuilder.pylonItem(Material.FIREWORK_STAR, BaseKeys.INTERMEDIATE_CORE_CHUNK)
            .build();
    static {
        PylonItem.register(PylonItem.class, INTERMEDIATE_CORE_CHUNK, BaseKeys.INTERMEDIATE_CORE_CHUNK);
        BasePages.RESOURCES.addItem(BaseKeys.INTERMEDIATE_CORE_CHUNK);

        ItemStack tinOutput = CRUSHED_RAW_TIN.clone().asQuantity(2);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.INTERMEDIATE_CORE_CHUNK,
                INTERMEDIATE_CORE_CHUNK,
                Map.of(
                        new ItemStack(Material.COAL, 3), 0.4,
                        new ItemStack(Material.RAW_COPPER, 2), 0.6,
                        tinOutput, 0.5,
                        new ItemStack(Material.RAW_IRON, 2), 0.3,
                        new ItemStack(Material.RAW_GOLD), 0.25
                ),
                10,
                Material.STONE.createBlockData()
        ));
    }

    // For trigger SpongeAbsorbEvent, sponges' material must be SPONGE
    public static final ItemStack WET_POWERFUL_WATER_SPONGE
            = ItemStackBuilder.pylonItem(Material.WET_SPONGE, BaseKeys.WET_WATER_SPONGE) // A used sponge shouldn't trigger event
            .build();
    static {
        PylonItem.register(PylonItem.class, WET_POWERFUL_WATER_SPONGE, BaseKeys.WET_WATER_SPONGE);
        BasePages.COMPONENTS.addItem(BaseKeys.WET_WATER_SPONGE);
    }

    public static final ItemStack WET_POWERFUL_LAVA_SPONGE
            = ItemStackBuilder.pylonItem(Material.SPONGE, BaseKeys.HOT_LAVA_SPONGE)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(HotLavaSponge.Item.class, WET_POWERFUL_LAVA_SPONGE, BaseKeys.HOT_LAVA_SPONGE);
        BasePages.BUILDING.addItem(BaseKeys.HOT_LAVA_SPONGE);
    }

    public static final ItemStack POWERFUL_WATER_SPONGE
            = ItemStackBuilder.pylonItem(Material.SPONGE, BaseKeys.POWERFUL_WATER_SPONGE)
            .build();
    static {
        PylonItem.register(PowerfulWaterSponge.Item.class, POWERFUL_WATER_SPONGE, BaseKeys.POWERFUL_WATER_SPONGE);
        BasePages.BUILDING.addItem(BaseKeys.POWERFUL_WATER_SPONGE);

        ShapedRecipe shapedRecipe = new ShapedRecipe(BaseKeys.POWERFUL_WATER_SPONGE, POWERFUL_WATER_SPONGE.clone())
                .shape(
                        "SBS",
                        "BBB",
                        "SBS"
                )
                .setIngredient('S', Material.SPONGE)
                .setIngredient('B', Material.BUCKET);
        RecipeType.VANILLA_SHAPED.addRecipe(shapedRecipe);

        BlastingRecipe blastingRecipe = new BlastingRecipe(
                BaseKeys.POWERFUL_WATER_SPONGE_BLASTING,
                POWERFUL_WATER_SPONGE,
                new RecipeChoice.ExactChoice(WET_POWERFUL_WATER_SPONGE),
                1.5f,
                100
        );

        RecipeType.VANILLA_BLASTING.addRecipe(blastingRecipe);
    }

    public static final ItemStack POWERFUL_LAVA_SPONGE
            = ItemStackBuilder.pylonItem(Material.SPONGE, BaseKeys.POWERFUL_LAVA_SPONGE)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(PowerfulLavaSponge.Item.class, POWERFUL_LAVA_SPONGE, BaseKeys.POWERFUL_LAVA_SPONGE);
        BasePages.BUILDING.addItem(BaseKeys.POWERFUL_LAVA_SPONGE);

        // Apply fireproof rune on PowerfulWaterSponge can turn it into PowerfulLaveSponge :D
        FireproofRuneRecipe recipe = FireproofRuneRecipe.of(
                BaseKeys.POWERFUL_LAVA_SPONGE,
                POWERFUL_WATER_SPONGE,
                POWERFUL_LAVA_SPONGE
        );
        FireproofRuneRecipe.RECIPE_TYPE.addRecipe(recipe);
    }

    // Calling this method forces all the static blocks to run, which initializes our items
    public static void initialize() {
    }
}
