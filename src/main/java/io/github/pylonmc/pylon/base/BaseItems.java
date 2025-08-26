package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.building.Elevator;
import io.github.pylonmc.pylon.base.content.building.ExplosiveTarget;
import io.github.pylonmc.pylon.base.content.building.Immobilizer;
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
import io.github.pylonmc.pylon.base.content.resources.RefractoryMix;
import io.github.pylonmc.pylon.base.content.science.Loupe;
import io.github.pylonmc.pylon.base.content.science.ResearchPack;
import io.github.pylonmc.pylon.base.content.tools.*;
import io.github.pylonmc.pylon.base.recipes.*;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.github.pylonmc.pylon.core.util.WeightedSet;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import kotlin.Pair;
import net.kyori.adventure.util.TriState;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.*;


@SuppressWarnings({"UnstableApiUsage", "OverlyComplexClass"})
public final class BaseItems {

    private BaseItems() {
        throw new AssertionError("Utility class");
    }

    //<editor-fold desc="Dusts" defaultstate=collapsed>
    public static final ItemStack ROCK_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.ROCK_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, ROCK_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.ROCK_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.ROCK_DUST,
                new ItemStack(Material.COBBLESTONE),
                ROCK_DUST.asQuantity(1),
                2,
                Material.COBBLESTONE.createBlockData()
        ));

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.ROCK_DUST,
                new ItemStack(Material.ANDESITE),
                ROCK_DUST.asQuantity(1),
                2,
                Material.ANDESITE.createBlockData()
        ));

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.ROCK_DUST,
                new ItemStack(Material.GRANITE),
                ROCK_DUST.asQuantity(1),
                2,
                Material.GRANITE.createBlockData()
        ));

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.ROCK_DUST,
                new ItemStack(Material.DIORITE),
                ROCK_DUST.asQuantity(1),
                2,
                Material.DIORITE.createBlockData()
        ));
    }

    public static final ItemStack OBSIDIAN_CHIP = ItemStackBuilder.pylonItem(Material.POLISHED_BLACKSTONE_BUTTON, BaseKeys.OBSIDIAN_CHIP )
            .build();
    static {
        PylonItem.register(PylonItem.class, OBSIDIAN_CHIP );
        BasePages.RESOURCES.addItem(BaseKeys.OBSIDIAN_CHIP );

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
                BaseKeys.OBSIDIAN_CHIP,
                new ItemStack(Material.OBSIDIAN),
                OBSIDIAN_CHIP .asQuantity(3),
                MiningLevel.DIAMOND,
                0.2F
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

    public static final ItemStack CARBON = ItemStackBuilder.pylonItem(Material.CHARCOAL, BaseKeys.CARBON)
            .build();
    static {
        PylonItem.register(PylonItem.class, CARBON);
        BasePages.RESOURCES.addItem(BaseKeys.CARBON);

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                baseKey("coal_to_carbon"),
                List.of(COAL_DUST.asQuantity(2)),
                List.of(CARBON)
        ));
    }

    public static final ItemStack SULFUR = ItemStackBuilder.pylonItem(Material.YELLOW_DYE, BaseKeys.SULFUR)
            .build();
    static {
        PylonItem.register(PylonItem.class, SULFUR);
        BasePages.RESOURCES.addItem(BaseKeys.SULFUR);

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                baseKey("redstone_smelting"),
                List.of(new ItemStack(Material.REDSTONE)),
                List.of(SULFUR)
        ));
    }

    public static final ItemStack GYPSUM = ItemStackBuilder.pylonItem(Material.QUARTZ, BaseKeys.GYPSUM)
            .build();
    static {
        PylonItem.register(PylonItem.class, GYPSUM);
        BasePages.RESOURCES.addItem(BaseKeys.GYPSUM);
    }

    public static final ItemStack GYPSUM_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.GYPSUM_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, GYPSUM_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.GYPSUM_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("gypsum_dust_from_gypsum"),
                GYPSUM,
                GYPSUM_DUST,
                8,
                Material.QUARTZ_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack COPPER_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.COPPER_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, COPPER_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.COPPER_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("copper_dust_from_copper_ingot"),
                new ItemStack(Material.COPPER_INGOT),
                COPPER_DUST,
                2,
                Material.COPPER_BLOCK.createBlockData()
        ));

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("copper_dust_from_raw_copper"),
                new ItemStack(Material.RAW_COPPER),
                COPPER_DUST,
                2,
                Material.RAW_COPPER_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack CRUSHED_RAW_COPPER = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.CRUSHED_RAW_COPPER)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_COPPER);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_COPPER);

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
                BaseKeys.CRUSHED_RAW_COPPER,
                new ItemStack(Material.RAW_COPPER),
                CRUSHED_RAW_COPPER.asQuantity(2),
                MiningLevel.STONE,
                0.75F
        ));
    }

    public static final ItemStack IRON_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.IRON_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, IRON_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.IRON_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("iron_dust_from_iron_ingot"),
                new ItemStack(Material.IRON_INGOT),
                IRON_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("iron_dust_from_raw_iron"),
                new ItemStack(Material.RAW_IRON),
                IRON_DUST,
                2,
                Material.RAW_IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack CRUSHED_RAW_IRON = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.CRUSHED_RAW_IRON)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_IRON);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_IRON);

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
                BaseKeys.CRUSHED_RAW_IRON,
                new ItemStack(Material.RAW_IRON),
                CRUSHED_RAW_IRON.asQuantity(2),
                MiningLevel.STONE,
                0.75F
        ));
    }

    public static final ItemStack GOLD_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.GOLD_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, GOLD_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.GOLD_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("gold_dust_from_gold_ingot"),
                new ItemStack(Material.GOLD_INGOT),
                GOLD_DUST,
                2,
                Material.GOLD_BLOCK.createBlockData()
        ));

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("gold_dust_from_raw_gold"),
                new ItemStack(Material.RAW_GOLD),
                GOLD_DUST,
                2,
                Material.RAW_GOLD_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack CRUSHED_RAW_GOLD = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.CRUSHED_RAW_GOLD)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_GOLD);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_GOLD);

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
                BaseKeys.CRUSHED_RAW_GOLD,
                new ItemStack(Material.RAW_GOLD),
                CRUSHED_RAW_GOLD.asQuantity(2),
                MiningLevel.IRON,
                0.75F
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

    public static final ItemStack RAW_TIN = ItemStackBuilder.pylonItem(Material.RAW_IRON, BaseKeys.RAW_TIN)
            .build();
    static {
        PylonItem.register(PylonItem.class, RAW_TIN);
        BasePages.RESOURCES.addItem(BaseKeys.RAW_TIN);
    }

    public static final ItemStack TIN_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.TIN_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.TIN_INGOT);

        float cookingXp = Settings.get(BaseKeys.MONSTER_JERKY).getOrThrow("cooking.xp", ConfigAdapter.FLOAT);

        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                baseKey("tin_ingot_furnace"),
                TIN_INGOT,
                new RecipeChoice.ExactChoice(RAW_TIN),
                cookingXp,
                BaseUtils.DEFAULT_FURNACE_TIME_TICKS
        );
        furnaceRecipe.setCategory(CookingBookCategory.MISC);
        RecipeType.VANILLA_FURNACE.addRecipe(furnaceRecipe);

        BlastingRecipe blastingRecipe = new BlastingRecipe(
                baseKey("tin_ingot_blasting"),
                TIN_INGOT,
                new RecipeChoice.ExactChoice(RAW_TIN),
                cookingXp,
                BaseUtils.DEFAULT_BLAST_FURNACE_TIME_TICKS
        );
        blastingRecipe.setCategory(CookingBookCategory.MISC);
        RecipeType.VANILLA_BLASTING.addRecipe(blastingRecipe);
    }

    public static final ItemStack TIN_NUGGET = ItemStackBuilder.pylonItem(Material.IRON_NUGGET, BaseKeys.TIN_NUGGET)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_NUGGET);
        BasePages.RESOURCES.addItem(BaseKeys.TIN_NUGGET);

        ShapelessRecipe nuggetsRecipe = new ShapelessRecipe(baseKey("tin_nuggets_from_tin_ingot"), TIN_NUGGET.asQuantity(9))
                .addIngredient(TIN_INGOT);
        nuggetsRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(nuggetsRecipe);

        ShapedRecipe ingotRecipe = new ShapedRecipe(baseKey("tin_ingot_from_tin_nuggets"), TIN_INGOT)
                .shape(
                        "nnn",
                        "nnn",
                        "nnn"
                )
                .setIngredient('n', TIN_NUGGET);
        ingotRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(ingotRecipe);
    }

    public static final ItemStack TIN_BLOCK = ItemStackBuilder.pylonItem(Material.IRON_BLOCK, BaseKeys.TIN_BLOCK)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_BLOCK, BaseKeys.TIN_BLOCK);
        BasePages.RESOURCES.addItem(BaseKeys.TIN_BLOCK);

        ShapelessRecipe ingotsRecipe = new ShapelessRecipe(baseKey("tin_ingots_from_tin_block"), TIN_INGOT.asQuantity(9))
                .addIngredient(TIN_BLOCK);
        ingotsRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(ingotsRecipe);

        ShapedRecipe blockRecipe = new ShapedRecipe(baseKey("tin_block_from_tin_ingots"), TIN_BLOCK)
                .shape(
                        "nnn",
                        "nnn",
                        "nnn"
                )
                .setIngredient('n', TIN_INGOT);
        blockRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(blockRecipe);
    }

    public static final ItemStack CRUSHED_RAW_TIN = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.CRUSHED_RAW_TIN)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_TIN);
        BasePages.RESOURCES.addItem(BaseKeys.CRUSHED_RAW_TIN);

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
                BaseKeys.CRUSHED_RAW_TIN,
                RAW_TIN,
                CRUSHED_RAW_TIN.asQuantity(2),
                MiningLevel.STONE,
                0.75F
        ));
    }

    public static final ItemStack TIN_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.TIN_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_DUST);
        BasePages.RESOURCES.addItem(BaseKeys.TIN_DUST);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("tin_dust_from_tin_ingot"),
                TIN_INGOT,
                TIN_DUST,
                2,
                Material.IRON_BLOCK.createBlockData()
        ));

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                baseKey("tin_dust_from_raw_tin"),
                RAW_TIN,
                TIN_DUST,
                2,
                Material.RAW_IRON_BLOCK.createBlockData()
        ));
    }

    public static final ItemStack BRONZE_INGOT = ItemStackBuilder.pylonItem(Material.COPPER_INGOT, BaseKeys.BRONZE_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.BRONZE_INGOT);

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                baseKey("bronze"),
                List.of(new ItemStack(Material.COPPER_INGOT, 2), TIN_INGOT),
                List.of(BRONZE_INGOT.asQuantity(2))
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
                Material.BRICKS.createBlockData()
        ));
    }

    public static final ItemStack BRONZE_NUGGET = ItemStackBuilder.pylonItem(Material.ARMADILLO_SCUTE, BaseKeys.BRONZE_NUGGET)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_NUGGET);
        BasePages.RESOURCES.addItem(BaseKeys.BRONZE_NUGGET);

        ShapelessRecipe nuggetsRecipe = new ShapelessRecipe(baseKey("bronze_nuggets_from_bronze_ingot"), BRONZE_NUGGET.asQuantity(9))
                .addIngredient(BRONZE_INGOT);
        nuggetsRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(nuggetsRecipe);

        ShapedRecipe ingotRecipe = new ShapedRecipe(baseKey("bronze_ingot_from_bronze_nuggets"), BRONZE_INGOT)
                .shape(
                        "nnn",
                        "nnn",
                        "nnn"
                )
                .setIngredient('n', BRONZE_NUGGET);
        ingotRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(ingotRecipe);
    }

    public static final ItemStack BRONZE_BLOCK = ItemStackBuilder.pylonItem(Material.COPPER_BLOCK, BaseKeys.BRONZE_BLOCK)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_BLOCK, BaseKeys.BRONZE_BLOCK);
        BasePages.RESOURCES.addItem(BaseKeys.BRONZE_BLOCK);

        ShapelessRecipe ingotsRecipe = new ShapelessRecipe(baseKey("bronze_ingots_from_bronze_block"), BRONZE_INGOT.asQuantity(9))
                .addIngredient(BRONZE_BLOCK);
        ingotsRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(ingotsRecipe);

        ShapedRecipe blockRecipe = new ShapedRecipe(baseKey("bronze_block_from_bronze_ingots"), BRONZE_BLOCK)
                .shape(
                        "nnn",
                        "nnn",
                        "nnn"
                )
                .setIngredient('n', BRONZE_INGOT);
        blockRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(blockRecipe);
    }

    public static final ItemStack STEEL_INGOT = ItemStackBuilder.pylonItem(Material.NETHERITE_INGOT, BaseKeys.STEEL_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.STEEL_INGOT);
    }

    public static final ItemStack STEEL_NUGGET = ItemStackBuilder.pylonItem(Material.NETHERITE_SCRAP, BaseKeys.STEEL_NUGGET)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_NUGGET);
        BasePages.RESOURCES.addItem(BaseKeys.STEEL_NUGGET);

        ShapelessRecipe nuggetsRecipe = new ShapelessRecipe(baseKey("steel_nuggets_from_steel_ingot"), STEEL_NUGGET.asQuantity(9))
                .addIngredient(STEEL_INGOT);
        nuggetsRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(nuggetsRecipe);

        ShapedRecipe ingotRecipe = new ShapedRecipe(baseKey("steel_ingot_from_steel_nuggets"), STEEL_INGOT)
                .shape(
                        "nnn",
                        "nnn",
                        "nnn"
                )
                .setIngredient('n', STEEL_NUGGET);
        ingotRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(ingotRecipe);
    }

    public static final ItemStack STEEL_BLOCK = ItemStackBuilder.pylonItem(Material.NETHERITE_BLOCK, BaseKeys.STEEL_BLOCK)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_BLOCK, BaseKeys.STEEL_BLOCK);
        BasePages.RESOURCES.addItem(BaseKeys.STEEL_BLOCK);

        ShapelessRecipe ingotsRecipe = new ShapelessRecipe(baseKey("steel_ingots_from_steel_block"), STEEL_INGOT.asQuantity(9))
                .addIngredient(STEEL_BLOCK);
        ingotsRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(ingotsRecipe);

        ShapedRecipe blockRecipe = new ShapedRecipe(baseKey("steel_block_from_steel_ingots"), STEEL_BLOCK)
                .shape(
                        "nnn",
                        "nnn",
                        "nnn"
                )
                .setIngredient('n', STEEL_INGOT);
        blockRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(blockRecipe);
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

    public static final ItemStack NICKEL_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.NICKEL_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, NICKEL_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.NICKEL_INGOT);
    }

    public static final ItemStack NICKEL_NUGGET = ItemStackBuilder.pylonItem(Material.IRON_NUGGET, BaseKeys.NICKEL_NUGGET)
            .build();
    static {
        PylonItem.register(PylonItem.class, NICKEL_NUGGET);
        BasePages.RESOURCES.addItem(BaseKeys.NICKEL_NUGGET);

        ShapelessRecipe nuggetsRecipe = new ShapelessRecipe(baseKey("nickel_nuggets_from_nickel_ingot"), NICKEL_NUGGET.asQuantity(9))
                .addIngredient(NICKEL_INGOT);
        nuggetsRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(nuggetsRecipe);

        ShapedRecipe ingotRecipe = new ShapedRecipe(baseKey("nickel_ingot_from_nickel_nuggets"), NICKEL_INGOT)
                .shape(
                        "nnn",
                        "nnn",
                        "nnn"
                )
                .setIngredient('n', NICKEL_NUGGET);
        ingotRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(ingotRecipe);
    }

    public static final ItemStack NICKEL_BLOCK = ItemStackBuilder.pylonItem(Material.IRON_BLOCK, BaseKeys.NICKEL_BLOCK)
            .build();
    static {
        PylonItem.register(PylonItem.class, NICKEL_BLOCK, BaseKeys.NICKEL_BLOCK);
        BasePages.RESOURCES.addItem(BaseKeys.NICKEL_BLOCK);

        ShapelessRecipe ingotsRecipe = new ShapelessRecipe(baseKey("nickel_ingots_from_nickel_block"), NICKEL_INGOT.asQuantity(9))
                .addIngredient(NICKEL_BLOCK);
        ingotsRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(ingotsRecipe);

        ShapedRecipe blockRecipe = new ShapedRecipe(baseKey("nickel_block_from_nickel_ingots"), NICKEL_BLOCK)
                .shape(
                        "nnn",
                        "nnn",
                        "nnn"
                )
                .setIngredient('n', NICKEL_INGOT);
        blockRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(blockRecipe);
    }

    public static final ItemStack NICKEL_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.NICKEL_DUST)
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

    public static final ItemStack COBALT_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.COBALT_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, COBALT_INGOT);
        BasePages.RESOURCES.addItem(BaseKeys.COBALT_INGOT);
    }

    public static final ItemStack COBALT_NUGGET = ItemStackBuilder.pylonItem(Material.IRON_NUGGET, BaseKeys.COBALT_NUGGET)
            .build();
    static {
        PylonItem.register(PylonItem.class, COBALT_NUGGET);
        BasePages.RESOURCES.addItem(BaseKeys.COBALT_NUGGET);

        ShapelessRecipe nuggetsRecipe = new ShapelessRecipe(baseKey("cobalt_nuggets_from_cobalt_ingot"), COBALT_NUGGET.asQuantity(9))
                .addIngredient(COBALT_INGOT);
        nuggetsRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(nuggetsRecipe);

        ShapedRecipe ingotRecipe = new ShapedRecipe(baseKey("cobalt_ingot_from_cobalt_nuggets"), COBALT_INGOT)
                .shape(
                        "nnn",
                        "nnn",
                        "nnn"
                )
                .setIngredient('n', COBALT_NUGGET);
        ingotRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(ingotRecipe);
    }

    public static final ItemStack COBALT_BLOCK = ItemStackBuilder.pylonItem(Material.IRON_BLOCK, BaseKeys.COBALT_BLOCK)
            .build();
    static {
        PylonItem.register(PylonItem.class, COBALT_BLOCK, BaseKeys.COBALT_BLOCK);
        BasePages.RESOURCES.addItem(BaseKeys.COBALT_BLOCK);

        ShapelessRecipe ingotsRecipe = new ShapelessRecipe(baseKey("cobalt_ingots_from_cobalt_block"), COBALT_INGOT.asQuantity(9))
                .addIngredient(COBALT_BLOCK);
        ingotsRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(ingotsRecipe);

        ShapedRecipe blockRecipe = new ShapedRecipe(baseKey("cobalt_block_from_cobalt_ingots"), COBALT_BLOCK)
                .shape(
                        "nnn",
                        "nnn",
                        "nnn"
                )
                .setIngredient('n', COBALT_INGOT);
        blockRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(blockRecipe);
    }

    public static final ItemStack COBALT_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.COBALT_DUST)
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
    // </editor-fold>

    //<editor-fold desc="Sheets" defaultstate=collapsed>
    public static final ItemStack COPPER_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.COPPER_SHEET)
            .build();
    static {
        PylonItem.register(PylonItem.class, COPPER_SHEET);
        BasePages.COMPONENTS.addItem(BaseKeys.COPPER_SHEET);

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

    public static final ItemStack TIN_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.TIN_SHEET)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_SHEET);
        BasePages.COMPONENTS.addItem(BaseKeys.TIN_SHEET);

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
                BaseKeys.TIN_SHEET,
                TIN_INGOT,
                TIN_SHEET,
                MiningLevel.IRON,
                0.25f
        ));
    }

    public static final ItemStack BRONZE_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.BRONZE_SHEET)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_SHEET);
        BasePages.COMPONENTS.addItem(BaseKeys.BRONZE_SHEET);

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
                BaseKeys.BRONZE_SHEET,
                BRONZE_INGOT,
                BRONZE_SHEET,
                MiningLevel.IRON,
                0.25f
        ));
    }

    public static final ItemStack STEEL_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.STEEL_SHEET)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_SHEET);
        BasePages.COMPONENTS.addItem(BaseKeys.STEEL_SHEET);

        HammerRecipe.RECIPE_TYPE.addRecipe(new HammerRecipe(
                BaseKeys.STEEL_SHEET,
                STEEL_INGOT,
                STEEL_SHEET,
                MiningLevel.DIAMOND,
                0.25f
        ));
    }
    //</editor-fold>

    //<editor-fold desc="Hammers" defaultstate=collapsed>
    public static final ItemStack HAMMER_STONE = Hammer.createItemStack(
            BaseKeys.HAMMER_STONE, Material.STONE_PICKAXE, (1.0 / 3) - 4, 1, 1
    ).set(DataComponentTypes.MAX_DAMAGE, 66).build();
    static {
        PylonItem.register(Hammer.class, HAMMER_STONE);
        BasePages.TOOLS.addItem(BaseKeys.HAMMER_STONE);

        RecipeType.VANILLA_SHAPED.addRecipe(Hammer.getRecipe(
                BaseKeys.HAMMER_STONE,
                HAMMER_STONE,
                new ItemStack(Material.COBBLESTONE)
        ));
    }

    public static final ItemStack HAMMER_IRON = Hammer.createItemStack(
            BaseKeys.HAMMER_IRON, Material.IRON_PICKAXE, (1.0 / 2) - 4, 1.5, 3
    ).set(DataComponentTypes.MAX_DAMAGE, 125).build();
    static {
        PylonItem.register(Hammer.class, HAMMER_IRON);
        BasePages.TOOLS.addItem(BaseKeys.HAMMER_IRON);

        RecipeType.VANILLA_SHAPED.addRecipe(Hammer.getRecipe(
                BaseKeys.HAMMER_IRON,
                HAMMER_IRON,
                new ItemStack(Material.IRON_INGOT)
        ));
    }

    public static final ItemStack HAMMER_DIAMOND = Hammer.createItemStack(
            BaseKeys.HAMMER_DIAMOND, Material.DIAMOND_PICKAXE, (1.0/ 1) - 4, 2, 5
    ).set(DataComponentTypes.MAX_DAMAGE, 781).build();
    static {
        PylonItem.register(Hammer.class, HAMMER_DIAMOND);
        BasePages.TOOLS.addItem(BaseKeys.HAMMER_DIAMOND);

        RecipeType.VANILLA_SHAPED.addRecipe(Hammer.getRecipe(
                BaseKeys.HAMMER_DIAMOND,
                HAMMER_DIAMOND,
                new ItemStack(Material.DIAMOND)
        ));
    }
    //</editor-fold>

    //<editor-fold desc="Bronze tools/armour" defaultstate=collapsed>
    public static final ItemStack BRONZE_SWORD = ItemStackBuilder.pylonItem(Material.GOLDEN_SWORD, BaseKeys.BRONZE_SWORD)
            .set(DataComponentTypes.MAX_DAMAGE, 500)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_SWORD);
        BasePages.COMBAT.addItem(BaseKeys.BRONZE_SWORD);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.BRONZE_SWORD, BRONZE_SWORD)
                .shape(" I ", " I ", " S ")
                .setIngredient('I', BRONZE_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack BRONZE_AXE = ItemStackBuilder.pylonItem(Material.GOLDEN_AXE, BaseKeys.BRONZE_AXE)
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
        PylonItem.register(PylonItem.class, BRONZE_AXE);
        BasePages.TOOLS.addItem(BaseKeys.BRONZE_AXE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.BRONZE_AXE, BRONZE_AXE)
                .shape("FF ", "FS ", " S ")
                .setIngredient('F', BRONZE_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
        RecipeType.VANILLA_SHAPED.addRecipe(BaseUtils.reflectRecipe(recipe));
    }

    public static final ItemStack BRONZE_PICKAXE = ItemStackBuilder.pylonItem(Material.GOLDEN_PICKAXE, BaseKeys.BRONZE_PICKAXE)
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
        PylonItem.register(PylonItem.class, BRONZE_PICKAXE);
        BasePages.TOOLS.addItem(BaseKeys.BRONZE_PICKAXE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.BRONZE_PICKAXE, BRONZE_PICKAXE)
                .shape("FFF", " S ", " S ")
                .setIngredient('F', BRONZE_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack BRONZE_SHOVEL = ItemStackBuilder.pylonItem(Material.GOLDEN_SHOVEL, BaseKeys.BRONZE_SHOVEL)
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
        PylonItem.register(PylonItem.class, BRONZE_SHOVEL);
        BasePages.TOOLS.addItem(BaseKeys.BRONZE_SHOVEL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.BRONZE_SHOVEL, BRONZE_SHOVEL)
                .shape(" F ", " S ", " S ")
                .setIngredient('F', BRONZE_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack BRONZE_HOE = ItemStackBuilder.pylonItem(Material.GOLDEN_HOE, BaseKeys.BRONZE_HOE)
            .set(DataComponentTypes.MAX_DAMAGE, 500)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_HOE);
        BasePages.TOOLS.addItem(BaseKeys.BRONZE_HOE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.BRONZE_HOE, BRONZE_HOE)
                .shape("FF ", " S ", " S ")
                .setIngredient('F', BRONZE_INGOT)
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
        RecipeType.VANILLA_SHAPED.addRecipe(BaseUtils.reflectRecipe(recipe));
    }
    //</editor-fold>

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
                    .nutrition(Settings.get(BaseKeys.MONSTER_JERKY).getOrThrow("nutrition", ConfigAdapter.INT))
                    .saturation(Settings.get(BaseKeys.MONSTER_JERKY).getOrThrow("saturation", ConfigAdapter.DOUBLE).floatValue())
                    .build()
            )
            .build();
    static {
        PylonItem.register(PylonItem.class, MONSTER_JERKY);
        BasePages.FOOD.addItem(BaseKeys.MONSTER_JERKY);

        float cookingXp = Settings.get(BaseKeys.MONSTER_JERKY).getOrThrow("cooking.xp", ConfigAdapter.DOUBLE).floatValue();

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
                List.of(gunpowderInput, new ItemStack(Material.SHROOMLIGHT), SHIMMER_DUST_1),
                BaseFluids.WATER,
                1000,
                FluidOrItem.of(output),
                true
        ));
    }

    public static final ItemStack SHIMMER_DUST_2 = ItemStackBuilder.pylonItem(Material.REDSTONE, BaseKeys.SHIMMER_DUST_2)
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

    public static final ItemStack SHIMMER_DUST_3 = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.SHIMMER_DUST_3)
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

    //<editor-fold desc="Portable Items" defaultstate=collapsed>
    public static final ItemStack PORTABILITY_CATALYST = ItemStackBuilder.pylonItem(Material.AMETHYST_SHARD, BaseKeys.PORTABILITY_CATALYST)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(PylonItem.class, PORTABILITY_CATALYST);
        BasePages.COMPONENTS.addItem(BaseKeys.PORTABILITY_CATALYST);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABILITY_CATALYST, PORTABILITY_CATALYST)
                .shape("SBS", "BPB", "SBS")
                .setIngredient('S', SHIMMER_DUST_1)
                .setIngredient('B', Material.REDSTONE_BLOCK)
                .setIngredient('P', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_CRAFTING_TABLE = ItemStackBuilder.pylonItem(Material.CRAFTING_TABLE, BaseKeys.PORTABLE_CRAFTING_TABLE)
            .build();
    static {
        PylonItem.register(PortableCraftingTable.class, PORTABLE_CRAFTING_TABLE);
        BasePages.TOOLS.addItem(BaseKeys.PORTABLE_CRAFTING_TABLE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_CRAFTING_TABLE, PORTABLE_CRAFTING_TABLE)
                .shape(" T ", "TPT", " T ")
                .setIngredient('T', new ItemStack(Material.CRAFTING_TABLE))
                .setIngredient('P', PORTABILITY_CATALYST);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_DUSTBIN = ItemStackBuilder.pylonItem(Material.CAULDRON, BaseKeys.PORTABLE_DUSTBIN)
            .build();
    static {
        PylonItem.register(PortableDustbin.class, PORTABLE_DUSTBIN);
        BasePages.TOOLS.addItem(BaseKeys.PORTABLE_DUSTBIN);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_DUSTBIN, PORTABLE_DUSTBIN)
                .shape("   ", "ICI", "IPI")
                .setIngredient('I', IRON_SHEET)
                .setIngredient('C', Material.CACTUS)
                .setIngredient('P', PORTABILITY_CATALYST);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_ENDER_CHEST = ItemStackBuilder.pylonItem(Material.ENDER_CHEST, BaseKeys.PORTABLE_ENDER_CHEST)
            .build();
    static {
        PylonItem.register(PortableEnderChest.class, PORTABLE_ENDER_CHEST);
        BasePages.TOOLS.addItem(BaseKeys.PORTABLE_ENDER_CHEST);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_ENDER_CHEST, PORTABLE_ENDER_CHEST)
                .shape("OEO", "OCO", "OOO")
                .setIngredient('O', new ItemStack(Material.OBSIDIAN))
                .setIngredient('E', new ItemStack(Material.ENDER_CHEST))
                .setIngredient('C', PORTABILITY_CATALYST);
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

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.FIBER,
                new ItemStack(Material.STRING),
                FIBER.asQuantity(2),
                2,
                Material.WHITE_WOOL.createBlockData()
        ));
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
                .shape("S S", "LTL", "S S")
                .setIngredient('S', new ItemStack(Material.STICK))
                .setIngredient('L', new ItemStack(Material.LEATHER))
                .setIngredient('T', new ItemStack(Material.STRING));
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

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.DISINFECTANT,
                List.of(
                        new ItemStack(Material.GLASS_BOTTLE),
                        new ItemStack(Material.NETHER_WART, 3),
                        new ItemStack(Material.DANDELION)
                ),
                BaseFluids.PLANT_OIL,
                100,
                FluidOrItem.of(DISINFECTANT),
                false
        ));
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

        ShapelessRecipe recipe = new ShapelessRecipe(BaseKeys.MEDKIT, MEDKIT)
                .addIngredient(BANDAGE)
                .addIngredient(SPLINT)
                .addIngredient(DISINFECTANT);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }
    //</editor-fold>

    public static final ItemStack LUMBER_AXE = ItemStackBuilder.pylonItem(Material.WOODEN_AXE, BaseKeys.LUMBER_AXE)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.LUMBER_AXE).getOrThrow("durability", ConfigAdapter.INT))
            .build();
    static {
        PylonItem.register(LumberAxe.class, LUMBER_AXE);
        BasePages.TOOLS.addItem(BaseKeys.LUMBER_AXE);

        MagicAltarRecipe.RECIPE_TYPE.addRecipe(new MagicAltarRecipe(
                BaseKeys.LUMBER_AXE,
                new ArrayList<>(Arrays.asList(
                        SHIMMER_DUST_2,
                        null,
                        null,
                        GOLD_DUST,
                        null,
                        null,
                        DIAMOND_DUST,
                        null
                )),
                new ItemStack(Material.WOODEN_AXE),
                LUMBER_AXE,
                45
        ));
    }

    public static final ItemStack BRICK_MOLD = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.BRICK_MOLD)
            .set(DataComponentTypes.ITEM_MODEL, Material.OAK_FENCE_GATE.getKey())
            .build();
    static {
        PylonItem.register(BrickMold.class, BRICK_MOLD);
        BasePages.TOOLS.addItem(BaseKeys.BRICK_MOLD);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.BRICK_MOLD, BRICK_MOLD)
                .shape("sss", "sgs", "sss")
                .setIngredient('s', Material.STICK)
                .setIngredient('g', new RecipeChoice.MaterialChoice(Tag.FENCE_GATES));
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
                .setIngredient('T', Material.SMOOTH_STONE)
                .setIngredient('S', Material.SMOOTH_STONE_SLAB);
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
                new ItemStack(Material.WHEAT),
                FLOUR,
                1,
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
                List.of(FLOUR.asQuantity(2)),
                BaseFluids.WATER,
                250,
                FluidOrItem.of(DOUGH),
                false
        ));

        FurnaceRecipe furnaceBreadRecipe = new FurnaceRecipe(
                baseKey("bread_from_dough_furnace"),
                new ItemStack(Material.BREAD),
                new RecipeChoice.ExactChoice(DOUGH),
                0.2F,
                DEFAULT_FURNACE_TIME_TICKS
        );
        furnaceBreadRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeType.VANILLA_FURNACE.addRecipe(furnaceBreadRecipe);

        SmokingRecipe smokerBreadRecipe = new SmokingRecipe(
                baseKey("bread_from_dough_smoker"),
                new ItemStack(Material.BREAD),
                new RecipeChoice.ExactChoice(DOUGH),
                0.2F,
                DEFAULT_SMOKER_TIME_TICKS
        );
        smokerBreadRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeType.VANILLA_SMOKING.addRecipe(smokerBreadRecipe);
    }

    public static final ItemStack MIXING_POT = ItemStackBuilder.pylonItem(Material.CAULDRON, BaseKeys.MIXING_POT)
            .build();
    static {
        PylonItem.register(PylonItem.class, MIXING_POT, BaseKeys.MIXING_POT);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.MIXING_POT);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.MIXING_POT, MIXING_POT)
                .shape("f f", "f f", "fff")
                .setIngredient('f', BRONZE_INGOT);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
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

    public static final ItemStack IGNEOUS_COMPOSITE = ItemStackBuilder.pylonItem(Material.OBSIDIAN, BaseKeys.IGNEOUS_COMPOSITE)
            .build();
    static {
        PylonItem.register(PylonItem.class, IGNEOUS_COMPOSITE, BaseKeys.IGNEOUS_COMPOSITE);
        BasePages.BUILDING.addItem(BaseKeys.IGNEOUS_COMPOSITE);

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.IGNEOUS_COMPOSITE,
                List.of(new ItemStack(Material.NETHER_BRICK, 4), COVALENT_BINDER),
                BaseFluids.OBSCYRA,
                1000.0,
                FluidOrItem.of(IGNEOUS_COMPOSITE),
                true
        ));
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
                .setIngredient('R', TIN_DUST);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HEALTH_TALISMAN_ADVANCED = ItemStackBuilder.pylonItem(Material.AMETHYST_CLUSTER, BaseKeys.HEALTH_TALISMAN_ADVANCED)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    static {
        PylonItem.register(HealthTalisman.class, HEALTH_TALISMAN_ADVANCED);
        BasePages.TOOLS.addItem(BaseKeys.HEALTH_TALISMAN_ADVANCED);

        MagicAltarRecipe.RECIPE_TYPE.addRecipe(new MagicAltarRecipe(
                BaseKeys.HEALTH_TALISMAN_ADVANCED,
                new ArrayList<>(Arrays.asList(
                        null,
                        HEALTH_TALISMAN_SIMPLE,
                        CARBON,
                        HEALTH_TALISMAN_SIMPLE,
                        BRONZE_DUST,
                        HEALTH_TALISMAN_SIMPLE,
                        null,
                        HEALTH_TALISMAN_SIMPLE
                )),
                DIAMOND_DUST,
                HEALTH_TALISMAN_ADVANCED,
                35
        ));
    }

    public static final ItemStack HEALTH_TALISMAN_ULTIMATE = ItemStackBuilder.pylonItem(Material.BUDDING_AMETHYST, BaseKeys.HEALTH_TALISMAN_ULTIMATE)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    static {
        PylonItem.register(HealthTalisman.class, HEALTH_TALISMAN_ULTIMATE);
        BasePages.TOOLS.addItem(BaseKeys.HEALTH_TALISMAN_ULTIMATE);

        MagicAltarRecipe.RECIPE_TYPE.addRecipe(new MagicAltarRecipe(
                BaseKeys.HEALTH_TALISMAN_ULTIMATE,
                new ArrayList<>(Arrays.asList(
                        HEALTH_TALISMAN_ADVANCED,
                        HEALTH_TALISMAN_ADVANCED,
                        HEALTH_TALISMAN_ADVANCED,
                        HEALTH_TALISMAN_ADVANCED,
                        HEALTH_TALISMAN_ADVANCED,
                        null,
                        EMERALD_DUST,
                        null
                )),
                SHIMMER_SKULL,
                HEALTH_TALISMAN_ULTIMATE,
                90
        ));
    }

    public static final ItemStack BEHEADING_SWORD = ItemStackBuilder.pylonItem(Material.DIAMOND_SWORD, BaseKeys.BEHEADING_SWORD)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BEHEADING_SWORD).getOrThrow("durability", ConfigAdapter.INT))
            .build();
    static {
        PylonItem.register(BeheadingSword.class, BEHEADING_SWORD);
        BasePages.COMBAT.addItem(BaseKeys.BEHEADING_SWORD);

        MagicAltarRecipe.RECIPE_TYPE.addRecipe(new MagicAltarRecipe(
                BaseKeys.BEHEADING_SWORD,
                new ArrayList<>(Arrays.asList(
                        SHIMMER_SKULL,
                        null,
                        SHIMMER_SKULL,
                        null,
                        SHIMMER_SKULL,
                        null,
                        null,
                        null
                )),
                new ItemStack(Material.DIAMOND_SWORD),
                BEHEADING_SWORD,
                85
        ));
    }

    public static final ItemStack PEDESTAL = ItemStackBuilder.pylonItem(Material.STONE_BRICK_WALL, BaseKeys.PEDESTAL)
            .build();
    static {
        PylonItem.register(PylonItem.class, PEDESTAL, BaseKeys.PEDESTAL);
        BasePages.BUILDING.addItem(BaseKeys.PEDESTAL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PEDESTAL, PEDESTAL)
                .shape("   ", "s  ", "s  ")
                .setIngredient('s', Material.STONE_BRICK_WALL);
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
                .shape(" i ", "dpd", "dsd")
                .setIngredient('p', PEDESTAL)
                .setIngredient('s', new ItemStack(Material.SMOOTH_STONE_SLAB))
                .setIngredient('d', SHIMMER_DUST_2)
                .setIngredient('i', DIAMOND_DUST);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_PIPE_WOOD = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_WOOD)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_WOOD).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_WOOD);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_PIPE_WOOD);

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
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_COPPER).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_COPPER);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_PIPE_COPPER);

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

    public static final ItemStack FLUID_PIPE_TIN = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_TIN)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_TIN).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_TIN);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_PIPE_TIN);

        ItemStack shapedOutput = new ItemStack(FLUID_PIPE_TIN).asQuantity(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_TIN, shapedOutput)
                .shape("ccc", "   ", "ccc")
                .setIngredient('c', TIN_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        ItemStack benderOutput = new ItemStack(FLUID_PIPE_TIN);
        benderOutput.setAmount(9);
        PipeBendingRecipe.RECIPE_TYPE.addRecipe(new PipeBendingRecipe(
                BaseKeys.FLUID_PIPE_TIN,
                TIN_BLOCK,
                benderOutput,
                Material.IRON_BLOCK.createBlockData(),
                360
        ));
    }

    public static final ItemStack FLUID_PIPE_IRON = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_IRON)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_IRON).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_IRON);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_PIPE_IRON);

        ItemStack shapedOutput = new ItemStack(FLUID_PIPE_IRON).asQuantity(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_IRON, shapedOutput)
                .shape("ccc", "   ", "ccc")
                .setIngredient('c', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        ItemStack benderOutput = new ItemStack(FLUID_PIPE_IRON);
        benderOutput.setAmount(9);
        PipeBendingRecipe.RECIPE_TYPE.addRecipe(new PipeBendingRecipe(
                BaseKeys.FLUID_PIPE_IRON,
                new ItemStack(Material.IRON_BLOCK),
                benderOutput,
                Material.IRON_BLOCK.createBlockData(),
                360
        ));
    }

    public static final ItemStack FLUID_PIPE_BRONZE = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_BRONZE)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_BRONZE).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_BRONZE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_PIPE_BRONZE);

        ItemStack shapedOutput = new ItemStack(FLUID_PIPE_BRONZE).asQuantity(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_BRONZE, shapedOutput)
                .shape("ccc", "   ", "ccc")
                .setIngredient('c', BRONZE_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        ItemStack benderOutput = new ItemStack(FLUID_PIPE_BRONZE);
        benderOutput.setAmount(9);
        PipeBendingRecipe.RECIPE_TYPE.addRecipe(new PipeBendingRecipe(
                BaseKeys.FLUID_PIPE_BRONZE,
                BRONZE_BLOCK,
                benderOutput,
                Material.COPPER_BLOCK.createBlockData(),
                360
        ));
    }

    public static final ItemStack FLUID_PIPE_IGNEOUS_COMPOSITE = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_IGNEOUS_COMPOSITE)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_IGNEOUS_COMPOSITE).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_IGNEOUS_COMPOSITE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_PIPE_IGNEOUS_COMPOSITE);

        ItemStack output = new ItemStack(FLUID_PIPE_IGNEOUS_COMPOSITE).asQuantity(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_IGNEOUS_COMPOSITE, output)
                .shape("ooo", "   ", "ooo")
                .setIngredient('o', IGNEOUS_COMPOSITE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        PipeBendingRecipe.RECIPE_TYPE.addRecipe(new PipeBendingRecipe(
                BaseKeys.FLUID_PIPE_IGNEOUS_COMPOSITE,
                IGNEOUS_COMPOSITE,
                FLUID_PIPE_IGNEOUS_COMPOSITE,
                Material.OBSIDIAN.createBlockData(),
                40
        ));
    }

    public static final ItemStack FLUID_PIPE_STEEL = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_STEEL)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_STEEL).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_STEEL);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_PIPE_STEEL);

        ItemStack shapedOutput = new ItemStack(FLUID_PIPE_STEEL).asQuantity(4);
        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_PIPE_STEEL, shapedOutput)
                .shape("ccc", "   ", "ccc")
                .setIngredient('c', STEEL_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);

        ItemStack benderOutput = new ItemStack(FLUID_PIPE_STEEL);
        benderOutput.setAmount(9);
        PipeBendingRecipe.RECIPE_TYPE.addRecipe(new PipeBendingRecipe(
                BaseKeys.FLUID_PIPE_STEEL,
                STEEL_BLOCK,
                benderOutput,
                Material.NETHERITE_BLOCK.createBlockData(),
                360
        ));
    }

    public static final ItemStack FLUID_PIPE_CREATIVE = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_CREATIVE)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_CREATIVE).getOrThrow("material", ConfigAdapter.MATERIAL).key()
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
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.PORTABLE_FLUID_TANK_WOOD);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_FLUID_TANK_WOOD, PORTABLE_FLUID_TANK_WOOD)
                .shape("cgc", "g g", "cgc")
                .setIngredient('c', new RecipeChoice.MaterialChoice(Tag.PLANKS))
                .setIngredient('g', Material.GLASS);
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
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.PORTABLE_FLUID_TANK_COPPER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_FLUID_TANK_COPPER, PORTABLE_FLUID_TANK_COPPER)
                .shape("cgc", "g g", "cgc")
                .setIngredient('c', COPPER_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_TIN
            = ItemStackBuilder.pylonItem(Material.GREEN_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_TIN)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0))
            .build();
    static {
        PylonItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_TIN,
                BaseKeys.PORTABLE_FLUID_TANK_TIN
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.PORTABLE_FLUID_TANK_TIN);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_FLUID_TANK_TIN, PORTABLE_FLUID_TANK_TIN)
                .shape("cgc", "g g", "cgc")
                .setIngredient('c', TIN_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_IRON
            = ItemStackBuilder.pylonItem(Material.LIGHT_GRAY_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_IRON)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0))
            .build();
    static {
        PylonItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_IRON,
                BaseKeys.PORTABLE_FLUID_TANK_IRON
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.PORTABLE_FLUID_TANK_IRON);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_FLUID_TANK_IRON, PORTABLE_FLUID_TANK_IRON)
                .shape("cgc", "g g", "cgc")
                .setIngredient('c', IRON_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_BRONZE
            = ItemStackBuilder.pylonItem(Material.ORANGE_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_BRONZE)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0))
            .build();
    static {
        PylonItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_BRONZE,
                BaseKeys.PORTABLE_FLUID_TANK_BRONZE
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.PORTABLE_FLUID_TANK_BRONZE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_FLUID_TANK_BRONZE, PORTABLE_FLUID_TANK_BRONZE)
                .shape("cgc", "g g", "cgc")
                .setIngredient('c', BRONZE_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE
            = ItemStackBuilder.pylonItem(Material.BLACK_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0))
            .build();
    static {
        PylonItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE,
                BaseKeys.PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE, PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE)
                .shape("cgc", "g g", "cgc")
                .setIngredient('c', IGNEOUS_COMPOSITE)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_STEEL
            = ItemStackBuilder.pylonItem(Material.GRAY_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_STEEL)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0))
            .build();
    static {
        PylonItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_STEEL,
                BaseKeys.PORTABLE_FLUID_TANK_STEEL
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.PORTABLE_FLUID_TANK_STEEL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PORTABLE_FLUID_TANK_STEEL, PORTABLE_FLUID_TANK_STEEL)
                .shape("cgc", "g g", "cgc")
                .setIngredient('c', STEEL_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_TANK
            = ItemStackBuilder.pylonItem(Material.GRAY_TERRACOTTA, BaseKeys.FLUID_TANK)
            .build();
    static {
        PylonItem.register(FluidTank.Item.class, FLUID_TANK, BaseKeys.FLUID_TANK);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_TANK);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_TANK, FLUID_TANK)
                .shape("   ", "iii", "ibi")
                .setIngredient('b', BRONZE_BLOCK)
                .setIngredient('i', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_TANK_CASING_WOOD
            = ItemStackBuilder.pylonItem(Material.BROWN_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_WOOD)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_WOOD, BaseKeys.FLUID_TANK_CASING_WOOD);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_TANK_CASING_WOOD);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_TANK_CASING_WOOD, FLUID_TANK_CASING_WOOD)
                .shape("c c", "g g", "c c")
                .setIngredient('c', new RecipeChoice.MaterialChoice(Tag.PLANKS))
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_TANK_CASING_COPPER
            = ItemStackBuilder.pylonItem(Material.ORANGE_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_COPPER)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_COPPER, BaseKeys.FLUID_TANK_CASING_COPPER);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_TANK_CASING_COPPER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_TANK_CASING_COPPER, FLUID_TANK_CASING_COPPER)
                .shape("c c", "g g", "c c")
                .setIngredient('c', COPPER_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_TANK_CASING_TIN
            = ItemStackBuilder.pylonItem(Material.GREEN_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_TIN)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_TIN, BaseKeys.FLUID_TANK_CASING_TIN);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_TANK_CASING_TIN);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_TANK_CASING_TIN, FLUID_TANK_CASING_TIN)
                .shape("c c", "g g", "c c")
                .setIngredient('c', TIN_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_TANK_CASING_IRON
            = ItemStackBuilder.pylonItem(Material.LIGHT_GRAY_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_IRON)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_IRON, BaseKeys.FLUID_TANK_CASING_IRON);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_TANK_CASING_IRON);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_TANK_CASING_IRON, FLUID_TANK_CASING_IRON)
                .shape("c c", "g g", "c c")
                .setIngredient('c', IRON_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_TANK_CASING_BRONZE
            = ItemStackBuilder.pylonItem(Material.ORANGE_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_BRONZE)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_BRONZE, BaseKeys.FLUID_TANK_CASING_BRONZE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_TANK_CASING_BRONZE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_TANK_CASING_BRONZE, FLUID_TANK_CASING_BRONZE)
                .shape("c c", "g g", "c c")
                .setIngredient('c', BRONZE_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_TANK_CASING_IGNEOUS_COMPOSITE
            = ItemStackBuilder.pylonItem(Material.BLACK_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_IGNEOUS_COMPOSITE)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_IGNEOUS_COMPOSITE, BaseKeys.FLUID_TANK_CASING_IGNEOUS_COMPOSITE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_TANK_CASING_IGNEOUS_COMPOSITE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_TANK_CASING_IGNEOUS_COMPOSITE, FLUID_TANK_CASING_IGNEOUS_COMPOSITE)
                .shape("c c", "g g", "c c")
                .setIngredient('c', IGNEOUS_COMPOSITE)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_TANK_CASING_STEEL
            = ItemStackBuilder.pylonItem(Material.GRAY_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_STEEL)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_STEEL, BaseKeys.FLUID_TANK_CASING_STEEL);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(BaseKeys.FLUID_TANK_CASING_STEEL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_TANK_CASING_STEEL, FLUID_TANK_CASING_STEEL)
                .shape("c c", "g g", "c c")
                .setIngredient('c', STEEL_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack ROTOR = ItemStackBuilder.pylonItem(Material.IRON_TRAPDOOR, BaseKeys.ROTOR)
            .build();
    static {
        PylonItem.register(PylonItem.class, ROTOR);
        BasePages.COMPONENTS.addItem(BaseKeys.ROTOR);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ROTOR, ROTOR)
                .shape(
                        " i ",
                        "isi",
                        " i "
                )
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('s', IRON_SHEET);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack BACKFLOW_VALVE = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.BACKFLOW_VALVE)
            .build();
    static {
        PylonItem.register(PylonItem.class, BACKFLOW_VALVE);
        BasePages.COMPONENTS.addItem(BaseKeys.BACKFLOW_VALVE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.BACKFLOW_VALVE, BACKFLOW_VALVE.asQuantity(2))
                .shape(
                        " r ",
                        "psp",
                        " r "
                )
                .setIngredient('p', FLUID_PIPE_IRON)
                .setIngredient('s', IRON_SHEET)
                .setIngredient('r', Material.REDSTONE);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack ANALOGUE_DISPLAY = ItemStackBuilder.pylonItem(Material.BLACK_STAINED_GLASS_PANE, BaseKeys.ANALOGUE_DISPLAY)
            .build();
    static {
        PylonItem.register(PylonItem.class, ANALOGUE_DISPLAY);
        BasePages.COMPONENTS.addItem(BaseKeys.ANALOGUE_DISPLAY);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ANALOGUE_DISPLAY, ANALOGUE_DISPLAY)
                .shape(
                        "ggg",
                        "rbc",
                        "ggg"
                )
                .setIngredient('g', Material.GLASS)
                .setIngredient('b', Material.BLACK_DYE)
                .setIngredient('r', Material.REPEATER)
                .setIngredient('c', Material.COMPARATOR);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FILTER_MESH = ItemStackBuilder.pylonItem(Material.IRON_BARS, BaseKeys.FILTER_MESH)
            .build();
    static {
        PylonItem.register(PylonItem.class, FILTER_MESH);
        BasePages.COMPONENTS.addItem(BaseKeys.FILTER_MESH);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FILTER_MESH, FILTER_MESH)
                .shape(
                        "fff",
                        "fbf",
                        "fff"
                )
                .setIngredient('f', FIBER)
                .setIngredient('b', Material.IRON_BARS);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack NOZZLE = ItemStackBuilder.pylonItem(Material.LEVER, BaseKeys.NOZZLE)
            .build();
    static {
        PylonItem.register(PylonItem.class, NOZZLE);
        BasePages.COMPONENTS.addItem(BaseKeys.NOZZLE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.NOZZLE, NOZZLE)
                .shape(
                        "   ",
                        "ppb",
                        "   "
                )
                .setIngredient('p', FLUID_PIPE_COPPER)
                .setIngredient('b', Material.STONE_BUTTON);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack ABYSSAL_CATALYST = ItemStackBuilder.pylonItem(Material.BLACK_CANDLE, BaseKeys.ABYSSAL_CATALYST)
            .build();
    static {
        PylonItem.register(PylonItem.class, ABYSSAL_CATALYST);
        BasePages.COMPONENTS.addItem(BaseKeys.ABYSSAL_CATALYST);

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.ABYSSAL_CATALYST,
                List.of(new ItemStack(Material.SOUL_SOIL, 4)),
                BaseFluids.OBSCYRA,
                250,
                FluidOrItem.of(ABYSSAL_CATALYST),
                true
        ));
    }

    public static final ItemStack HYDRAULIC_MOTOR = ItemStackBuilder.pylonItem(Material.PISTON, BaseKeys.HYDRAULIC_MOTOR)
            .build();
    static {
        PylonItem.register(PylonItem.class, HYDRAULIC_MOTOR);
        BasePages.COMPONENTS.addItem(BaseKeys.HYDRAULIC_MOTOR);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HYDRAULIC_MOTOR, HYDRAULIC_MOTOR)
                .shape(
                        "sis",
                        "nrb",
                        "sis"
                )
                .setIngredient('i', Material.PISTON)
                .setIngredient('n', NOZZLE)
                .setIngredient('r', ROTOR)
                .setIngredient('b', BACKFLOW_VALVE)
                .setIngredient('s', BRONZE_SHEET);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack AXLE = ItemStackBuilder.pylonItem(Material.OAK_FENCE, BaseKeys.AXLE)
            .build();
    static {
        PylonItem.register(PylonItem.class, AXLE);
        BasePages.COMPONENTS.addItem(BaseKeys.AXLE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.AXLE, AXLE)
                .shape(
                        "f  ",
                        "f  ",
                        "f  "
                )
                .setIngredient('f', new RecipeChoice.MaterialChoice(Tag.FENCES));
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SAWBLADE = ItemStackBuilder.pylonItem(Material.IRON_BARS, BaseKeys.SAWBLADE)
            .build();
    static {
        PylonItem.register(PylonItem.class, SAWBLADE);
        BasePages.COMPONENTS.addItem(BaseKeys.SAWBLADE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.SAWBLADE, SAWBLADE)
                .shape(
                        " i ",
                        "ibi",
                        " i "
                )
                .setIngredient('b', BRONZE_BLOCK)
                .setIngredient('i', BRONZE_INGOT);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack WEIGHTED_SHAFT = ItemStackBuilder.pylonItem(Material.DEEPSLATE_TILE_WALL, BaseKeys.WEIGHTED_SHAFT)
            .build();
    static {
        PylonItem.register(PylonItem.class, WEIGHTED_SHAFT);
        BasePages.COMPONENTS.addItem(BaseKeys.WEIGHTED_SHAFT);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.WEIGHTED_SHAFT, WEIGHTED_SHAFT)
                .shape(
                        "ii ",
                        "ii ",
                        "ii "
                )
                .setIngredient('i', Material.IRON_INGOT);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack COPPER_DRILL_BIT = ItemStackBuilder.pylonItem(Material.LIGHTNING_ROD, BaseKeys.COPPER_DRILL_BIT)
            .build();
    static {
        PylonItem.register(PylonItem.class, COPPER_DRILL_BIT);
        BasePages.COMPONENTS.addItem(BaseKeys.COPPER_DRILL_BIT);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.COPPER_DRILL_BIT, COPPER_DRILL_BIT)
                .shape(
                        "   ",
                        "cic",
                        " c "
                )
                .setIngredient('c', Material.COPPER_INGOT)
                .setIngredient('i', Material.IRON_BLOCK);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack BRONZE_DRILL_BIT = ItemStackBuilder.pylonItem(Material.LIGHTNING_ROD, BaseKeys.BRONZE_DRILL_BIT)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_DRILL_BIT);
        BasePages.COMPONENTS.addItem(BaseKeys.BRONZE_DRILL_BIT);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.BRONZE_DRILL_BIT, BRONZE_DRILL_BIT)
                .shape(
                        "   ",
                        "bib",
                        " b "
                )
                .setIngredient('b', BRONZE_INGOT)
                .setIngredient('i', Material.IRON_BLOCK);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack WATER_PUMP = ItemStackBuilder.pylonItem(Material.BLUE_TERRACOTTA, BaseKeys.WATER_PUMP)
            .build();
    static {
        PylonItem.register(WaterPump.Item.class, WATER_PUMP, BaseKeys.WATER_PUMP);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.WATER_PUMP);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.WATER_PUMP, WATER_PUMP)
                .shape(" b ", "iri", "ipi")
                .setIngredient('i', IRON_SHEET)
                .setIngredient('p', FLUID_PIPE_COPPER)
                .setIngredient('b', Material.BUCKET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_VALVE = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VALVE)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_VALVE, BaseKeys.FLUID_VALVE);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_VALVE);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_VALVE, FLUID_VALVE)
                .shape(" l ", "bpb", "   ")
                .setIngredient('l', new ItemStack(Material.LEVER))
                .setIngredient('p', FLUID_PIPE_TIN)
                .setIngredient('b', BACKFLOW_VALVE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_FILTER = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_FILTER)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_FILTER, BaseKeys.FLUID_FILTER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_FILTER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_FILTER, FLUID_FILTER)
                .shape(" c ", "pfb", "   ")
                .setIngredient('c', new ItemStack(Material.COMPARATOR))
                .setIngredient('p', FLUID_PIPE_TIN)
                .setIngredient('f', FILTER_MESH)
                .setIngredient('b', BACKFLOW_VALVE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_METER = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_METER)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_METER, BaseKeys.FLUID_METER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_METER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_METER, FLUID_METER)
                .shape(" a ", "rfr", " b ")
                .setIngredient('a', ANALOGUE_DISPLAY)
                .setIngredient('f', FLUID_FILTER)
                .setIngredient('r', Material.REPEATER)
                .setIngredient('b', Material.BUCKET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack WATER_PLACER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.WATER_PLACER)
            .build();
    static {
        PylonItem.register(FluidPlacer.Item.class, WATER_PLACER, BaseKeys.WATER_PLACER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.WATER_PLACER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.WATER_PLACER, WATER_PLACER)
                .shape("ibi", "pdn", "ibi")
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('p', FLUID_PIPE_COPPER)
                .setIngredient('d', Material.DISPENSER)
                .setIngredient('n', NOZZLE)
                .setIngredient('b', Material.BLUE_DYE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack LAVA_PLACER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.LAVA_PLACER)
            .build();
    static {
        PylonItem.register(FluidPlacer.Item.class, LAVA_PLACER, BaseKeys.LAVA_PLACER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.LAVA_PLACER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.LAVA_PLACER, LAVA_PLACER)
                .shape("ioi", "pdn", "ioi")
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('p', FLUID_PIPE_COPPER)
                .setIngredient('d', Material.DISPENSER)
                .setIngredient('n', NOZZLE)
                .setIngredient('o', Material.ORANGE_DYE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack WATER_DRAINER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.WATER_DRAINER)
            .build();
    static {
        PylonItem.register(FluidDrainer.Item.class, WATER_DRAINER, BaseKeys.WATER_DRAINER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.WATER_DRAINER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.WATER_DRAINER, WATER_DRAINER)
                .shape("ibi", "pdr", "ibi")
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('p', FLUID_PIPE_COPPER)
                .setIngredient('d', Material.DISPENSER)
                .setIngredient('r', ROTOR)
                .setIngredient('b', Material.BLUE_DYE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack LAVA_DRAINER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.LAVA_DRAINER)
            .build();
    static {
        PylonItem.register(FluidDrainer.Item.class, LAVA_DRAINER, BaseKeys.LAVA_DRAINER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.LAVA_DRAINER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.LAVA_DRAINER, LAVA_DRAINER)
                .shape("ioi", "pdr", "ioi")
                .setIngredient('i', Material.IRON_INGOT)
                .setIngredient('p', FLUID_PIPE_COPPER)
                .setIngredient('d', Material.DISPENSER)
                .setIngredient('r', ROTOR)
                .setIngredient('o', Material.ORANGE_DYE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_VOIDER_1 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_1)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_1, BaseKeys.FLUID_VOIDER_1);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_VOIDER_1);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_VOIDER_1, FLUID_VOIDER_1)
                .shape(" p ", "iai", " i ")
                .setIngredient('i', IGNEOUS_COMPOSITE)
                .setIngredient('p', FLUID_PIPE_TIN)
                .setIngredient('a', ABYSSAL_CATALYST);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack FLUID_VOIDER_2 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_2)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_2, BaseKeys.FLUID_VOIDER_2);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_VOIDER_2);

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.MIXING_POT,
                List.of(FLUID_VOIDER_1),
                BaseFluids.OBSCYRA,
                500,
                FluidOrItem.of(FLUID_VOIDER_2),
                true
        ));
    }

    public static final ItemStack FLUID_VOIDER_3 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_3)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_3, BaseKeys.FLUID_VOIDER_3);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_VOIDER_3);

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.MIXING_POT,
                List.of(FLUID_VOIDER_2),
                BaseFluids.OBSCYRA,
                500,
                FluidOrItem.of(FLUID_VOIDER_3),
                true
        ));
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
                .setIngredient('G', Material.GLASS_PANE);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack RESEARCH_PACK_1 = ItemStackBuilder.pylonItem(Material.RED_BANNER, BaseKeys.RESEARCH_PACK_1)
            .set(DataComponentTypes.MAX_STACK_SIZE, 3)
            .build();
    static {
        PylonItem.register(ResearchPack.class, RESEARCH_PACK_1);
        BasePages.SCIENCE.addItem(BaseKeys.RESEARCH_PACK_1);

        ShapelessRecipe recipe = new ShapelessRecipe(BaseKeys.RESEARCH_PACK_1, RESEARCH_PACK_1)
                .addIngredient(BRONZE_INGOT)
                .addIngredient(SHIMMER_DUST_2)
                .addIngredient(MONSTER_JERKY)
                .addIngredient(SULFUR);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }

    public static final ItemStack RESEARCH_PACK_2 = ItemStackBuilder.pylonItem(Material.LIME_BANNER, BaseKeys.RESEARCH_PACK_2)
            .set(DataComponentTypes.MAX_STACK_SIZE, 3)
            .build();
    static {
        PylonItem.register(ResearchPack.class, RESEARCH_PACK_2);
        BasePages.SCIENCE.addItem(BaseKeys.RESEARCH_PACK_2);

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.RESEARCH_PACK_2,
                List.of(HYDRAULIC_MOTOR, CARBON, LOUPE),
                BaseFluids.OBSCYRA,
                500,
                FluidOrItem.of(RESEARCH_PACK_2),
                true
        ));
    }

    public static final ItemStack FLUID_STRAINER = ItemStackBuilder.pylonItem(Material.COPPER_GRATE, BaseKeys.FLUID_STRAINER)
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_STRAINER, BaseKeys.FLUID_STRAINER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.FLUID_STRAINER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.FLUID_STRAINER, FLUID_STRAINER)
                .shape("cbc", " f ", "c c")
                .setIngredient('b', BACKFLOW_VALVE)
                .setIngredient('c', COPPER_SHEET)
                .setIngredient('f', FILTER_MESH);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SPRINKLER = ItemStackBuilder.pylonItem(Material.FLOWER_POT, BaseKeys.SPRINKLER)
            .build();
    static {
        PylonItem.register(Sprinkler.Item.class, SPRINKLER, BaseKeys.SPRINKLER);
        BasePages.FLUID_MACHINES.addItem(BaseKeys.SPRINKLER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.SPRINKLER, SPRINKLER)
                .shape(" f ", "bub", "brb")
                .setIngredient('b', Material.BRICK)
                .setIngredient('u', Material.BUCKET)
                .setIngredient('f', FLUID_PIPE_COPPER)
                .setIngredient('r', Material.REPEATER);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    //<editor-fold desc="Smeltery" defaultstate="collapsed">
    public static final ItemStack REFRACTORY_MIX = ItemStackBuilder.pylonItem(Material.SMOOTH_RED_SANDSTONE, BaseKeys.REFRACTORY_MIX)
            .build();
    static {
        PylonItem.register(PylonItem.class, REFRACTORY_MIX, BaseKeys.REFRACTORY_MIX);
        BasePages.RESOURCES.addItem(BaseKeys.REFRACTORY_MIX);

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.REFRACTORY_MIX,
                List.of(
                        IRON_DUST,
                        new ItemStack(Material.RED_NETHER_BRICKS),
                        new ItemStack(Material.SAND, 2),
                        new ItemStack(Material.GRAVEL, 2),
                        GYPSUM_DUST.asQuantity(3),
                        new ItemStack(Material.CLAY_BALL, 6)
                ),
                BaseFluids.OBSCYRA,
                1000.0,
                FluidOrItem.of(REFRACTORY_MIX.asQuantity(4)),
                true
        ));
    }

    public static final ItemStack UNFIRED_REFRACTORY_BRICK = ItemStackBuilder.pylonItem(Material.BRICK, BaseKeys.UNFIRED_REFRACTORY_BRICK)
            .build();
    static {
        PylonItem.register(PylonItem.class, UNFIRED_REFRACTORY_BRICK, BaseKeys.UNFIRED_REFRACTORY_BRICK);
        BasePages.RESOURCES.addItem(BaseKeys.UNFIRED_REFRACTORY_BRICK);

        MoldingDisplayRecipe.RECIPE_TYPE.addRecipe(new MoldingDisplayRecipe(
                BaseKeys.UNFIRED_REFRACTORY_BRICK,
                REFRACTORY_MIX,
                UNFIRED_REFRACTORY_BRICK,
                RefractoryMix.TOTAL_MOLDING_CLICKS
        ));
    }

    public static final ItemStack REFRACTORY_BRICK = ItemStackBuilder.pylonItem(Material.NETHERITE_INGOT, BaseKeys.REFRACTORY_BRICK)
            .build();
    static {
        PylonItem.register(PylonItem.class, REFRACTORY_BRICK, BaseKeys.REFRACTORY_BRICK);
        BasePages.RESOURCES.addItem(BaseKeys.REFRACTORY_BRICK);

        PitKilnRecipe.RECIPE_TYPE.addRecipe(new PitKilnRecipe(
                BaseKeys.REFRACTORY_BRICK,
                List.of(UNFIRED_REFRACTORY_BRICK),
                List.of(REFRACTORY_BRICK)
        ));
    }

    public static final ItemStack REFRACTORY_BRICKS = ItemStackBuilder.pylonItem(Material.DEEPSLATE_TILES, BaseKeys.REFRACTORY_BRICKS)
            .build();
    static {
        PylonItem.register(PylonItem.class, REFRACTORY_BRICKS, BaseKeys.REFRACTORY_BRICKS);
        BasePages.SMELTING.addItem(BaseKeys.REFRACTORY_BRICKS);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.REFRACTORY_BRICKS, REFRACTORY_BRICKS)
                .shape("bb ", "bb ", "   ")
                .setIngredient('b', REFRACTORY_BRICK);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_CONTROLLER = ItemStackBuilder.pylonItem(Material.BLAST_FURNACE, BaseKeys.SMELTERY_CONTROLLER)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_CONTROLLER, BaseKeys.SMELTERY_CONTROLLER);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_CONTROLLER);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_controller"), SMELTERY_CONTROLLER)
                .shape("rbr", "bfb", "sbr")
                .setIngredient('b', REFRACTORY_BRICKS)
                .setIngredient('f', Material.BLAST_FURNACE)
                .setIngredient('r', BRONZE_BLOCK);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_INPUT_HATCH = ItemStackBuilder.pylonItem(Material.LIGHT_BLUE_TERRACOTTA, BaseKeys.SMELTERY_INPUT_HATCH)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_INPUT_HATCH, BaseKeys.SMELTERY_INPUT_HATCH);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_INPUT_HATCH);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_input_hatch"), SMELTERY_INPUT_HATCH)
                .shape("ibi", "pvb", "ibi")
                .setIngredient('b', REFRACTORY_BRICK)
                .setIngredient('i', IRON_SHEET)
                .setIngredient('v', BACKFLOW_VALVE)
                .setIngredient('p', FLUID_PIPE_IGNEOUS_COMPOSITE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_OUTPUT_HATCH = ItemStackBuilder.pylonItem(Material.ORANGE_TERRACOTTA, BaseKeys.SMELTERY_OUTPUT_HATCH)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_OUTPUT_HATCH, BaseKeys.SMELTERY_OUTPUT_HATCH);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_OUTPUT_HATCH);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_output_hatch"), SMELTERY_OUTPUT_HATCH)
                .shape("ibi", "bvp", "ibi")
                .setIngredient('b', REFRACTORY_BRICK)
                .setIngredient('i', IRON_SHEET)
                .setIngredient('v', BACKFLOW_VALVE)
                .setIngredient('p', FLUID_PIPE_IGNEOUS_COMPOSITE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_HOPPER = ItemStackBuilder.pylonItem(Material.HOPPER, BaseKeys.SMELTERY_HOPPER)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_HOPPER, BaseKeys.SMELTERY_HOPPER);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_HOPPER);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_hopper"), SMELTERY_HOPPER)
                .shape("bhb", "bcb", " b ")
                .setIngredient('b', REFRACTORY_BRICK)
                .setIngredient('h', Material.HOPPER)
                .setIngredient('c', Material.CHEST);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_CASTER = ItemStackBuilder.pylonItem(Material.BRICKS, BaseKeys.SMELTERY_CASTER)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_CASTER, BaseKeys.SMELTERY_CASTER);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_CASTER);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_caster"), SMELTERY_CASTER)
                .shape("bpb", "bfb", "bbb")
                .setIngredient('b', REFRACTORY_BRICK)
                .setIngredient('p', FLUID_PIPE_IGNEOUS_COMPOSITE)
                .setIngredient('f', Material.FLOWER_POT);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SMELTERY_BURNER = ItemStackBuilder.pylonItem(Material.FURNACE, BaseKeys.SMELTERY_BURNER)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_BURNER, BaseKeys.SMELTERY_BURNER);
        BasePages.SMELTING.addItem(BaseKeys.SMELTERY_BURNER);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("smeltery_burner"), SMELTERY_BURNER)
                .shape("bbb", "sfs", "bbb")
                .setIngredient('b', REFRACTORY_BRICK)
                .setIngredient('s', IRON_SHEET)
                .setIngredient('f', Material.FURNACE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PIT_KILN = ItemStackBuilder.pylonItem(Material.DECORATED_POT, BaseKeys.PIT_KILN)
            .build();
    static {
        PylonItem.register(PitKiln.Item.class, PIT_KILN, BaseKeys.PIT_KILN);
        BasePages.SMELTING.addItem(BaseKeys.PIT_KILN);

        ShapedRecipe recipe = new ShapedRecipe(baseKey("pit_kiln"), PIT_KILN)
                .shape("b b", "b b", "bbb")
                .setIngredient('b', Material.BRICKS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
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
                        " g ",
                        "txt",
                        " g "
                )
                .setIngredient('t', Material.TNT)
                .setIngredient('g', Material.GUNPOWDER)
                .setIngredient('x', Material.TARGET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack EXPLOSIVE_TARGET_FIERY = ItemStackBuilder.pylonItem(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET_FIERY)
            .build();
    static {
        PylonItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET_FIERY, BaseKeys.EXPLOSIVE_TARGET_FIERY);
        BasePages.BUILDING.addItem(BaseKeys.EXPLOSIVE_TARGET_FIERY);

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.EXPLOSIVE_TARGET_FIERY,
                List.of(EXPLOSIVE_TARGET, new ItemStack(Material.FIRE_CHARGE, 2)),
                BaseFluids.PLANT_OIL,
                500,
                FluidOrItem.of(EXPLOSIVE_TARGET_FIERY),
                false
        ));
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

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY,
                List.of(EXPLOSIVE_TARGET, new ItemStack(Material.FIRE_CHARGE, 4)),
                BaseFluids.PLANT_OIL,
                1000,
                FluidOrItem.of(EXPLOSIVE_TARGET_SUPER_FIERY),
                false
        ));
    }

    public static final ItemStack IMMOBILIZER = ItemStackBuilder.pylonItem(Material.PISTON, BaseKeys.IMMOBILIZER)
            .build();
    static {
        PylonItem.register(Immobilizer.Item.class, IMMOBILIZER, BaseKeys.IMMOBILIZER);
        BasePages.BUILDING.addItem(BaseKeys.IMMOBILIZER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.IMMOBILIZER, IMMOBILIZER)
                .shape(
                        "dsd",
                        "dgd",
                        "drd"
                )
                .setIngredient('s', Material.DAYLIGHT_DETECTOR)
                .setIngredient('d', SHIMMER_DUST_3)
                .setIngredient('r', Material.REDSTONE_BLOCK)
                .setIngredient('g', Material.HEAVY_CORE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack ELEVATOR_1 = ItemStackBuilder.pylonItem(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_1)
            .build();
    static {
        PylonItem.register(Elevator.Item.class, ELEVATOR_1, BaseKeys.ELEVATOR_1);
        BasePages.BUILDING.addItem(BaseKeys.ELEVATOR_1);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ELEVATOR_1, ELEVATOR_1)
                .shape(" s ", "qpq", " s ")
                .setIngredient('q', Material.QUARTZ_BLOCK)
                .setIngredient('s', SHIMMER_DUST_1)
                .setIngredient('p', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack ELEVATOR_2 = ItemStackBuilder.pylonItem(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_2)
            .build();
    static {
        PylonItem.register(Elevator.Item.class, ELEVATOR_2, BaseKeys.ELEVATOR_2);
        BasePages.BUILDING.addItem(BaseKeys.ELEVATOR_2);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ELEVATOR_2, ELEVATOR_2)
                .shape(" s ", "pep", " s ")
                .setIngredient('e', ELEVATOR_1)
                .setIngredient('s', SHIMMER_DUST_2)
                .setIngredient('p', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack ELEVATOR_3 = ItemStackBuilder.pylonItem(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_3)
            .build();
    static {
        PylonItem.register(Elevator.Item.class, ELEVATOR_3, BaseKeys.ELEVATOR_3);
        BasePages.BUILDING.addItem(BaseKeys.ELEVATOR_3);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ELEVATOR_3, ELEVATOR_3)
                .shape(" s ", "pep", " s ")
                .setIngredient('e', ELEVATOR_2)
                .setIngredient('s', SHIMMER_DUST_3)
                .setIngredient('p', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PRESS = ItemStackBuilder.pylonItem(Material.COMPOSTER, BaseKeys.PRESS)
            .build();
    static {
        PylonItem.register(Press.PressItem.class, PRESS, BaseKeys.PRESS);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.PRESS);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PRESS, PRESS)
                .shape("bpb", "b b", "bbb")
                .setIngredient('p', Material.PISTON)
                .setIngredient('b', Material.BRICK);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HYDRAULIC_GRINDSTONE_TURNER = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE, BaseKeys.HYDRAULIC_GRINDSTONE_TURNER)
            .build();
    static {
        PylonItem.register(HydraulicGrindstoneTurner.Item.class, HYDRAULIC_GRINDSTONE_TURNER, BaseKeys.HYDRAULIC_GRINDSTONE_TURNER);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_GRINDSTONE_TURNER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HYDRAULIC_GRINDSTONE_TURNER, HYDRAULIC_GRINDSTONE_TURNER)
                .shape("sas", "pep", "sss")
                .setIngredient('s', Material.SMOOTH_STONE)
                .setIngredient('a', AXLE)
                .setIngredient('e', HYDRAULIC_MOTOR)
                .setIngredient('p', FLUID_PIPE_BRONZE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HYDRAULIC_MIXING_ATTACHMENT = ItemStackBuilder.pylonItem(Material.CHISELED_STONE_BRICKS, BaseKeys.HYDRAULIC_MIXING_ATTACHMENT)
            .build();
    static {
        PylonItem.register(HydraulicMixingAttachment.Item.class, HYDRAULIC_MIXING_ATTACHMENT, BaseKeys.HYDRAULIC_MIXING_ATTACHMENT);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_MIXING_ATTACHMENT);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HYDRAULIC_MIXING_ATTACHMENT, HYDRAULIC_MIXING_ATTACHMENT)
                .shape("ccc", "pep", "cwc")
                .setIngredient('c', Material.CHISELED_STONE_BRICKS)
                .setIngredient('e', HYDRAULIC_MOTOR)
                .setIngredient('p', FLUID_PIPE_BRONZE)
                .setIngredient('w', Material.LIGHT_GRAY_CONCRETE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HYDRAULIC_PRESS_PISTON = ItemStackBuilder.pylonItem(Material.BROWN_TERRACOTTA, BaseKeys.HYDRAULIC_PRESS_PISTON)
            .build();
    static {
        PylonItem.register(HydraulicPressPiston.Item.class, HYDRAULIC_PRESS_PISTON, BaseKeys.HYDRAULIC_PRESS_PISTON);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_PRESS_PISTON);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HYDRAULIC_PRESS_PISTON, HYDRAULIC_PRESS_PISTON)
                .shape("ttt", "pep", "tit")
                .setIngredient('t', Material.TERRACOTTA)
                .setIngredient('e', HYDRAULIC_MOTOR)
                .setIngredient('p', FLUID_PIPE_BRONZE)
                .setIngredient('i', Material.PISTON);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HYDRAULIC_HAMMER_HEAD = ItemStackBuilder.pylonItem(Material.STONE_BRICKS, BaseKeys.HYDRAULIC_HAMMER_HEAD)
            .build();
    static {
        PylonItem.register(HydraulicHammerHead.Item.class, HYDRAULIC_HAMMER_HEAD, BaseKeys.HYDRAULIC_HAMMER_HEAD);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_HAMMER_HEAD);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HYDRAULIC_HAMMER_HEAD, HYDRAULIC_HAMMER_HEAD)
                .shape("sws", "pep", "sws")
                .setIngredient('s', Material.STONE_BRICKS)
                .setIngredient('e', HYDRAULIC_MOTOR)
                .setIngredient('p', FLUID_PIPE_BRONZE)
                .setIngredient('w', WEIGHTED_SHAFT);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HYDRAULIC_PIPE_BENDER = ItemStackBuilder.pylonItem(Material.WAXED_CHISELED_COPPER, BaseKeys.HYDRAULIC_PIPE_BENDER)
            .build();
    static {
        PylonItem.register(HydraulicPipeBender.Item.class, HYDRAULIC_PIPE_BENDER, BaseKeys.HYDRAULIC_PIPE_BENDER);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_PIPE_BENDER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HYDRAULIC_PIPE_BENDER, HYDRAULIC_PIPE_BENDER)
                .shape("lbl", "pep", "bbb")
                .setIngredient('l', Material.LEVER)
                .setIngredient('b', BRONZE_SHEET)
                .setIngredient('e', HYDRAULIC_MOTOR)
                .setIngredient('p', FLUID_PIPE_BRONZE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HYDRAULIC_TABLE_SAW = ItemStackBuilder.pylonItem(Material.WAXED_CUT_COPPER, BaseKeys.HYDRAULIC_TABLE_SAW)
            .build();
    static {
        PylonItem.register(HydraulicTableSaw.Item.class, HYDRAULIC_TABLE_SAW, BaseKeys.HYDRAULIC_TABLE_SAW);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_TABLE_SAW);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HYDRAULIC_TABLE_SAW, HYDRAULIC_TABLE_SAW)
                .shape("btb", "pep", "bbb")
                .setIngredient('b', BRONZE_SHEET)
                .setIngredient('e', HYDRAULIC_MOTOR)
                .setIngredient('p', FLUID_PIPE_BRONZE)
                .setIngredient('t', SAWBLADE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SOLAR_LENS = ItemStackBuilder.pylonItem(Material.GLASS_PANE, BaseKeys.SOLAR_LENS)
            .build();
    static {
        PylonItem.register(PylonItem.class, SOLAR_LENS, BaseKeys.SOLAR_LENS);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_LENS);

        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                BaseKeys.SOLAR_LENS,
                List.of(new ItemStack(Material.GLASS_PANE, 4)),
                BaseFluids.REFLECTOR_FLUID,
                250,
                FluidOrItem.of(SOLAR_LENS),
                false
        ));
    }

    public static final ItemStack PURIFICATION_TOWER_GLASS = ItemStackBuilder.pylonItem(Material.LIGHT_GRAY_STAINED_GLASS, BaseKeys.PURIFICATION_TOWER_GLASS)
            .build();
    static {
        PylonItem.register(PylonItem.class, PURIFICATION_TOWER_GLASS, BaseKeys.PURIFICATION_TOWER_GLASS);
        BasePages.HYDRAULICS.addItem(BaseKeys.PURIFICATION_TOWER_GLASS);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PURIFICATION_TOWER_GLASS, PURIFICATION_TOWER_GLASS)
                .shape("gbg", "gbg", "gbg")
                .setIngredient('b', BRONZE_SHEET)
                .setIngredient('g', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack PURIFICATION_TOWER_CAP = ItemStackBuilder.pylonItem(Material.WAXED_CUT_COPPER_SLAB, BaseKeys.PURIFICATION_TOWER_CAP)
            .build();
    static {
        PylonItem.register(PylonItem.class, PURIFICATION_TOWER_CAP, BaseKeys.PURIFICATION_TOWER_CAP);
        BasePages.HYDRAULICS.addItem(BaseKeys.PURIFICATION_TOWER_CAP);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.PURIFICATION_TOWER_CAP, PURIFICATION_TOWER_CAP)
                .shape("   ", "sss", "gsg")
                .setIngredient('g', Material.GLASS)
                .setIngredient('s', BRONZE_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_1 = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_1)
            .build();
    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_1, BaseKeys.SOLAR_PURIFICATION_TOWER_1);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_PURIFICATION_TOWER_1);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.SOLAR_PURIFICATION_TOWER_1, SOLAR_PURIFICATION_TOWER_1)
                .shape("cpc", "fpf", "csc")
                .setIngredient('c', Material.COPPER_INGOT)
                .setIngredient('s', BRONZE_SHEET)
                .setIngredient('p', FLUID_PIPE_BRONZE)
                .setIngredient('f', FLUID_FILTER);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_2 = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_2)
            .build();
    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_2, BaseKeys.SOLAR_PURIFICATION_TOWER_2);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_PURIFICATION_TOWER_2);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.SOLAR_PURIFICATION_TOWER_2, SOLAR_PURIFICATION_TOWER_2)
                .shape("csc", "sps", "csc")
                .setIngredient('p', SOLAR_PURIFICATION_TOWER_1)
                .setIngredient('c', Material.COPPER_INGOT)
                .setIngredient('s', BRONZE_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_3 = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_3)
            .build();
    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_3, BaseKeys.SOLAR_PURIFICATION_TOWER_3);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_PURIFICATION_TOWER_3);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.SOLAR_PURIFICATION_TOWER_3, SOLAR_PURIFICATION_TOWER_3)
                .shape("csc", "sps", "csc")
                .setIngredient('p', SOLAR_PURIFICATION_TOWER_2)
                .setIngredient('c', Material.COPPER_INGOT)
                .setIngredient('s', BRONZE_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_4 = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_4)
            .build();
    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_4, BaseKeys.SOLAR_PURIFICATION_TOWER_4);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_PURIFICATION_TOWER_4);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.SOLAR_PURIFICATION_TOWER_4, SOLAR_PURIFICATION_TOWER_4)
                .shape("csc", "sps", "csc")
                .setIngredient('p', SOLAR_PURIFICATION_TOWER_3)
                .setIngredient('c', Material.COPPER_INGOT)
                .setIngredient('s', BRONZE_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_5 = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_5)
            .build();
    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_5, BaseKeys.SOLAR_PURIFICATION_TOWER_5);
        BasePages.HYDRAULICS.addItem(BaseKeys.SOLAR_PURIFICATION_TOWER_5);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.SOLAR_PURIFICATION_TOWER_5, SOLAR_PURIFICATION_TOWER_5)
                .shape("csc", "sps", "csc")
                .setIngredient('p', SOLAR_PURIFICATION_TOWER_4)
                .setIngredient('c', Material.COPPER_INGOT)
                .setIngredient('s', BRONZE_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack COAL_FIRED_PURIFICATION_TOWER = ItemStackBuilder.pylonItem(Material.BLAST_FURNACE, BaseKeys.COAL_FIRED_PURIFICATION_TOWER)
            .build();
    static {
        PylonItem.register(CoalFiredPurificationTower.Item.class, COAL_FIRED_PURIFICATION_TOWER, BaseKeys.COAL_FIRED_PURIFICATION_TOWER);
        BasePages.HYDRAULICS.addItem(BaseKeys.COAL_FIRED_PURIFICATION_TOWER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.COAL_FIRED_PURIFICATION_TOWER, COAL_FIRED_PURIFICATION_TOWER)
                .shape("cpc", "fpf", "cuc")
                .setIngredient('c', Material.COPPER_INGOT)
                .setIngredient('p', FLUID_PIPE_BRONZE)
                .setIngredient('f', FLUID_FILTER)
                .setIngredient('u', Material.BLAST_FURNACE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack ICE_ARROW = ItemStackBuilder.pylonItem(Material.ARROW, BaseKeys.ICE_ARROW).build();
    static {
        PylonItem.register(IceArrow.class, ICE_ARROW, BaseKeys.ICE_ARROW);
        BasePages.COMBAT.addItem(BaseKeys.ICE_ARROW);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.ICE_ARROW, ICE_ARROW.asQuantity(8))
                .shape(
                        "aaa",
                        "aia",
                        "aaa"
                )
                .setIngredient('i', Material.BLUE_ICE)
                .setIngredient('a', Material.ARROW);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack RECOIL_ARROW = ItemStackBuilder.pylonItem(Material.ARROW, BaseKeys.RECOIL_ARROW)
            .build();
    static {
        PylonItem.register(RecoilArrow.class, RECOIL_ARROW);
        BasePages.COMBAT.addItem(BaseKeys.RECOIL_ARROW);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.RECOIL_ARROW, RECOIL_ARROW.asQuantity(8))
                .shape("aaa", "asa", "aaa")
                .setIngredient('s', Material.SLIME_BALL)
                .setIngredient('a', Material.ARROW);
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
        BasePages.TOOLS.addItem(BaseKeys.FIREPROOF_RUNE);

        MagicAltarRecipe.RECIPE_TYPE.addRecipe(new MagicAltarRecipe(
                BaseKeys.FIREPROOF_RUNE,
                new ArrayList<>(Arrays.asList(
                        new ItemStack(Material.LAVA_BUCKET),
                        new ItemStack(Material.MAGMA_BLOCK),
                        SHIMMER_DUST_3,
                        new ItemStack(Material.FIRE_CHARGE),
                        SHIMMER_DUST_3,
                        null,
                        SHIMMER_DUST_3,
                        SHIMMER_DUST_3
                )),
                new ItemStack(Material.MAGMA_CREAM),
                FIREPROOF_RUNE,
                70
        ));
    }

    public static final ItemStack MANUAL_CORE_DRILL_LEVER = ItemStackBuilder.pylonItem(Material.LEVER, BaseKeys.MANUAL_CORE_DRILL_LEVER)
            .build();
    static {
        PylonItem.register(PylonItem.class, MANUAL_CORE_DRILL_LEVER, BaseKeys.MANUAL_CORE_DRILL_LEVER);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.MANUAL_CORE_DRILL_LEVER);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.MANUAL_CORE_DRILL_LEVER, MANUAL_CORE_DRILL_LEVER)
                .shape(" l ", "lpl", " l ")
                .setIngredient('l', Material.LEVER)
                .setIngredient('p', new RecipeChoice.MaterialChoice(Tag.PLANKS));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack MANUAL_CORE_DRILL = ItemStackBuilder.pylonItem(Material.CHISELED_STONE_BRICKS, BaseKeys.MANUAL_CORE_DRILL)
            .build();
    static {
        PylonItem.register(CoreDrill.Item.class, MANUAL_CORE_DRILL, BaseKeys.MANUAL_CORE_DRILL);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.MANUAL_CORE_DRILL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.MANUAL_CORE_DRILL, MANUAL_CORE_DRILL)
                .shape("bsb", "scs", "bdb")
                .setIngredient('s', COPPER_SHEET)
                .setIngredient('c', Material.COPPER_BLOCK)
                .setIngredient('b', Material.CHISELED_STONE_BRICKS)
                .setIngredient('d', COPPER_DRILL_BIT);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack IMPROVED_MANUAL_CORE_DRILL = ItemStackBuilder.pylonItem(Material.WAXED_OXIDIZED_COPPER, BaseKeys.IMPROVED_MANUAL_CORE_DRILL)
            .build();
    static {
        PylonItem.register(ImprovedManualCoreDrill.Item.class, IMPROVED_MANUAL_CORE_DRILL, BaseKeys.IMPROVED_MANUAL_CORE_DRILL);
        BasePages.SIMPLE_MACHINES.addItem(BaseKeys.IMPROVED_MANUAL_CORE_DRILL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.IMPROVED_MANUAL_CORE_DRILL, IMPROVED_MANUAL_CORE_DRILL)
                .shape("t t", " m ", "t t")
                .setIngredient('m', MANUAL_CORE_DRILL)
                .setIngredient('t', TIN_SHEET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HYDRAULIC_CORE_DRILL = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BULB, BaseKeys.HYDRAULIC_CORE_DRILL)
            .build();
    static {
        PylonItem.register(HydraulicCoreDrill.Item.class, HYDRAULIC_CORE_DRILL, BaseKeys.HYDRAULIC_CORE_DRILL);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_CORE_DRILL);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HYDRAULIC_CORE_DRILL, HYDRAULIC_CORE_DRILL)
                .shape("bhb", "php", "bdb")
                .setIngredient('b', BRONZE_SHEET)
                .setIngredient('h', HYDRAULIC_MOTOR)
                .setIngredient('p', FLUID_PIPE_BRONZE)
                .setIngredient('d', BRONZE_DRILL_BIT);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HYDRAULIC_CORE_DRILL_INPUT_HATCH = ItemStackBuilder.pylonItem(Material.LIGHT_BLUE_TERRACOTTA, BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH)
            .build();
    static {
        PylonItem.register(PylonItem.class, HYDRAULIC_CORE_DRILL_INPUT_HATCH, BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH, HYDRAULIC_CORE_DRILL_INPUT_HATCH)
                .shape("t t", "po ", "t t")
                .setIngredient('t', Material.TERRACOTTA)
                .setIngredient('p', FLUID_PIPE_BRONZE)
                .setIngredient('o', BACKFLOW_VALVE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack HYDRAULIC_CORE_DRILL_OUTPUT_HATCH = ItemStackBuilder.pylonItem(Material.ORANGE_TERRACOTTA, BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH)
            .build();
    static {
        PylonItem.register(PylonItem.class, HYDRAULIC_CORE_DRILL_OUTPUT_HATCH, BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH);
        BasePages.HYDRAULICS.addItem(BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH);

        ShapedRecipe recipe = new ShapedRecipe(BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH, HYDRAULIC_CORE_DRILL_OUTPUT_HATCH)
                .shape("t t", " op", "t t")
                .setIngredient('t', Material.TERRACOTTA)
                .setIngredient('p', FLUID_PIPE_BRONZE)
                .setIngredient('o', BACKFLOW_VALVE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeType.VANILLA_SHAPED.addRecipe(recipe);
    }

    public static final ItemStack SHALLOW_CORE_CHUNK = ItemStackBuilder.pylonItem(Material.FIREWORK_STAR, BaseKeys.SHALLOW_CORE_CHUNK)
            .build();
    static {
        PylonItem.register(PylonItem.class, SHALLOW_CORE_CHUNK, BaseKeys.SHALLOW_CORE_CHUNK);
        BasePages.RESOURCES.addItem(BaseKeys.SHALLOW_CORE_CHUNK);

        DrillingDisplayRecipe.RECIPE_TYPE.addRecipe(new DrillingDisplayRecipe(
                BaseKeys.SHALLOW_CORE_CHUNK,
                MANUAL_CORE_DRILL,
                SHALLOW_CORE_CHUNK
        ));

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.SHALLOW_CORE_CHUNK,
                SHALLOW_CORE_CHUNK,
                new WeightedSet<>(
                        new Pair<>(new ItemStack(Material.COAL), 0.5f),
                        new Pair<>(new ItemStack(Material.RAW_COPPER), 0.4f),
                        new Pair<>(CRUSHED_RAW_TIN, 0.3f)
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

        DrillingDisplayRecipe.RECIPE_TYPE.addRecipe(new DrillingDisplayRecipe(
                BaseKeys.SUBSURFACE_CORE_CHUNK,
                IMPROVED_MANUAL_CORE_DRILL,
                SUBSURFACE_CORE_CHUNK
        ));

        ItemStack tinOutput = CRUSHED_RAW_TIN.clone().asQuantity(2);

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.SUBSURFACE_CORE_CHUNK,
                SUBSURFACE_CORE_CHUNK,
                new WeightedSet<>(
                        new Pair<>(new ItemStack(Material.COAL, 2), 0.3f),
                        new Pair<>(new ItemStack(Material.RAW_COPPER, 2), 0.25f),
                        new Pair<>(tinOutput, 0.2f),
                        new Pair<>(new ItemStack(Material.RAW_IRON), 0.4f)
                ),
                8,
                Material.STONE.createBlockData()
        ));
    }

    public static final ItemStack INTERMEDIATE_CORE_CHUNK = ItemStackBuilder.pylonItem(Material.FIREWORK_STAR, BaseKeys.INTERMEDIATE_CORE_CHUNK)
            .build();
    static {
        PylonItem.register(PylonItem.class, INTERMEDIATE_CORE_CHUNK, BaseKeys.INTERMEDIATE_CORE_CHUNK);
        BasePages.RESOURCES.addItem(BaseKeys.INTERMEDIATE_CORE_CHUNK);

        DrillingDisplayRecipe.RECIPE_TYPE.addRecipe(new DrillingDisplayRecipe(
                BaseKeys.INTERMEDIATE_CORE_CHUNK,
                HYDRAULIC_CORE_DRILL,
                INTERMEDIATE_CORE_CHUNK
        ));

        GrindstoneRecipe.RECIPE_TYPE.addRecipe(new GrindstoneRecipe(
                BaseKeys.INTERMEDIATE_CORE_CHUNK,
                INTERMEDIATE_CORE_CHUNK,
                new WeightedSet<>(
                        new Pair<>(new ItemStack(Material.COAL, 3), 0.4f),
                        new Pair<>(new ItemStack(Material.RAW_COPPER, 2), 0.6f),
                        new Pair<>(CRUSHED_RAW_TIN.asQuantity(2), 0.5f),
                        new Pair<>(new ItemStack(Material.RAW_IRON, 2), 0.3f),
                        new Pair<>(new ItemStack(Material.RAW_GOLD), 0.25f),
                        new Pair<>(GYPSUM, 0.1f)
                ),
                10,
                Material.STONE.createBlockData()
        ));
    }

    public static final ItemStack CLEANSING_POTION = ItemStackBuilder.pylonItem(Material.SPLASH_POTION, BaseKeys.CLEANSING_POTION)
            .set(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents()
                    .customColor(Color.FUCHSIA)
                    .build())
            .build();
    static {
        PylonItem.register(CleansingPotion.class, CLEANSING_POTION);
        BasePages.TOOLS.addItem(BaseKeys.CLEANSING_POTION);
        ItemStack healingPotion = ItemStackBuilder.of(Material.SPLASH_POTION)
                .set(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents()
                        .potion(PotionType.HEALING)
                        .build())
                .build();
        ShapelessRecipe recipe = new ShapelessRecipe(BaseKeys.CLEANSING_POTION, CLEANSING_POTION)
                .addIngredient(healingPotion)
                .addIngredient(DISINFECTANT);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeType.VANILLA_SHAPELESS.addRecipe(recipe);
    }

    // Calling this method forces all the static blocks to run, which initializes our items
    public static void initialize() {
    }
}
