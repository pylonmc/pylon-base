package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.HealthTalisman;
import io.github.pylonmc.pylon.base.items.MonsterJerky;
import io.github.pylonmc.pylon.base.items.multiblocks.Grindstone;
import io.github.pylonmc.pylon.base.items.multiblocks.MagicAltar;
import io.github.pylonmc.pylon.base.items.multiblocks.MixingPot;
import io.github.pylonmc.pylon.base.items.research.Loupe;
import io.github.pylonmc.pylon.base.items.research.ResearchPack;
import io.github.pylonmc.pylon.base.items.tools.*;
import io.github.pylonmc.pylon.base.items.tools.watering.Sprinkler;
import io.github.pylonmc.pylon.base.items.tools.watering.WateringCan;
import io.github.pylonmc.pylon.base.items.weapons.BeheadingSword;
import io.github.pylonmc.pylon.base.items.weapons.RecoilArrow;
import io.github.pylonmc.pylon.base.misc.WaterCauldronRightClickRecipe;
import io.github.pylonmc.pylon.base.util.RecipeUtils;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimpleBlockPlacerItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.research.Research;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.Ageable;
import org.bukkit.enchantments.Enchantment;
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


@SuppressWarnings("UnstableApiUsage")
public final class PylonItems {

    private PylonItems() {
        throw new AssertionError("Utility class");
    }

    //<editor-fold desc="Dusts" defaultstate=collapsed>
    public static final PylonItemSchema COPPER_DUST = new PylonItemSchema(
            pylonKey("copper_dust"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GLOWSTONE_DUST, key).build()
    );
    static {
        COPPER_DUST.register();
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                pylonKey("copper_dust_from_copper_ingot"),
                new RecipeChoice.ExactChoice(new ItemStack(Material.COPPER_INGOT)),
                1,
                COPPER_DUST.getItemStack(),
                2,
                Material.COPPER_BLOCK.createBlockData()
        ));
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                pylonKey("copper_dust_from_raw_copper"),
                new RecipeChoice.ExactChoice(new ItemStack(Material.RAW_COPPER)),
                1,
                COPPER_DUST.getItemStack(),
                2,
                Material.COPPER_BLOCK.createBlockData()
        ));
    }

    public static final PylonItemSchema GOLD_DUST = new PylonItemSchema(
            pylonKey("gold_dust"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GLOWSTONE_DUST, key).build()
    );
    static {
        GOLD_DUST.register();
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                pylonKey("gold_dust_from_gold_ingot"),
                new RecipeChoice.ExactChoice(new ItemStack(Material.GOLD_INGOT)),
                1,
                GOLD_DUST.getItemStack(),
                2,
                Material.GOLD_BLOCK.createBlockData()
        ));
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                pylonKey("gold_dust_from_raw_gold"),
                new RecipeChoice.ExactChoice(new ItemStack(Material.RAW_GOLD)),
                1,
                GOLD_DUST.getItemStack(),
                2,
                Material.GOLD_BLOCK.createBlockData()
        ));
    }
    //</editor-fold>

    //<editor-fold desc="Sheets" defaultstate=collapsed>
    public static final PylonItemSchema COPPER_SHEET = new PylonItemSchema(
            pylonKey("copper_sheet"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.PAPER, key).build()
    );
    static {
        COPPER_SHEET.register();
        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                pylonKey("copper_sheet"),
                List.of(new ItemStack(Material.COPPER_INGOT)),
                COPPER_SHEET.getItemStack(),
                MiningLevel.STONE,
                0.25f
        ));
    }

    public static final PylonItemSchema GOLD_SHEET = new PylonItemSchema(
            pylonKey("gold_sheet"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.PAPER, key).build()
    );
    static {
        GOLD_SHEET.register();
        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                pylonKey("gold_sheet"),
                List.of(new ItemStack(Material.GOLD_INGOT)),
                GOLD_SHEET.getItemStack(),
                MiningLevel.STONE,
                0.25f
        ));
    }

    public static final PylonItemSchema IRON_SHEET = new PylonItemSchema(
            pylonKey("iron_sheet"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.PAPER, key).build()

    );
    static {
        IRON_SHEET.register();
        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                pylonKey("iron_sheet"),
                List.of(new ItemStack(Material.IRON_INGOT)),
                IRON_SHEET.getItemStack(),
                MiningLevel.IRON,
                0.25f
        ));
    }
    //</editor-fold>

    //<editor-fold desc="Hammers" defaultstate=collapsed>
    public static final Hammer.Schema STONE_HAMMER = new Hammer.Schema(
            pylonKey("hammer_stone"),
            key -> ItemStackBuilder.defaultBuilder(Material.STONE_PICKAXE, key).build(),
            Material.COBBLESTONE,
            Material.STONE,
            MiningLevel.STONE,
            (1.0 / 3) - 4,
            1,
            1
    );
    static {
        STONE_HAMMER.register();
        RecipeTypes.VANILLA_CRAFTING.addRecipe(STONE_HAMMER.getRecipe());
    }

    public static final Hammer.Schema IRON_HAMMER = new Hammer.Schema(
            pylonKey("hammer_iron"),
            key -> ItemStackBuilder.defaultBuilder(Material.IRON_PICKAXE, key).build(),
            Material.IRON_INGOT,
            Material.IRON_BLOCK,
            MiningLevel.IRON,
            (1.0 / 2) - 4,
            1.5,
            3
    );
    static {
        IRON_HAMMER.register();
        RecipeTypes.VANILLA_CRAFTING.addRecipe(IRON_HAMMER.getRecipe());
    }

    public static final Hammer.Schema DIAMOND_HAMMER = new Hammer.Schema(
            pylonKey("hammer_diamond"),
            key -> ItemStackBuilder.defaultBuilder(Material.DIAMOND_PICKAXE, key).build(),
            Material.DIAMOND,
            Material.DIAMOND_BLOCK,
            MiningLevel.DIAMOND,
            1 - 4,
            2,
            5
    );
    static {
        DIAMOND_HAMMER.register();
        RecipeTypes.VANILLA_CRAFTING.addRecipe(DIAMOND_HAMMER.getRecipe());
    }

    static {
        new Research(
                pylonKey("newtons_second_law"),
                5L,
                STONE_HAMMER,
                IRON_HAMMER,
                DIAMOND_HAMMER
        ).register();
    }
    //</editor-fold>

    public static final WateringCan WATERING_CAN = new WateringCan(
            pylonKey("watering_can"),
            key -> ItemStackBuilder.defaultBuilder(Material.BUCKET, key).build()
    );
    static {
        WATERING_CAN.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("watering_can"), WATERING_CAN.getItemStack())
                .shape("  S", "S S", " S ")
                .setIngredient('S', IRON_SHEET.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);

        new Research(
                pylonKey("plant_growth"),
                5L,
                WATERING_CAN
        ).register();
    }

    public static final MonsterJerky MONSTER_JERKY = new MonsterJerky(
            pylonKey("monster_jerky"),
            key -> ItemStackBuilder.defaultBuilder(Material.ROTTEN_FLESH, key)
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable().build())
                    .build()
    );
    static {
        MONSTER_JERKY.register();

        FurnaceRecipe furnaceRecipe = new FurnaceRecipe(
                pylonKey("monster_jerky_furnace"),
                MONSTER_JERKY.getItemStack(),
                Material.ROTTEN_FLESH,
                MONSTER_JERKY.getCookingXp(),
                RecipeUtils.DEFAULT_FURNACE_TIME_TICKS
        );
        furnaceRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_FURNACE.addRecipe(furnaceRecipe);

        SmokingRecipe smokingRecipe = new SmokingRecipe(
                pylonKey("monster_jerky_smoker"),
                MONSTER_JERKY.getItemStack(),
                Material.ROTTEN_FLESH,
                MONSTER_JERKY.getCookingXp(),
                RecipeUtils.DEFAULT_SMOKER_TIME_TICKS
        );
        smokingRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_SMOKING.addRecipe(smokingRecipe);

        ShapedRecipe leatherRecipe = new ShapedRecipe(pylonKey("leather"), new ItemStack(Material.LEATHER))
                .shape("RR ", "RR ", "   ")
                .setIngredient('R', MONSTER_JERKY.getItemStack());
        leatherRecipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(leatherRecipe);

        new Research(
                pylonKey("food_preservation"),
                2L,
                MONSTER_JERKY
        ).register();
    }

    //<editor-fold desc="Ferroduralum" defaultstate=collapsed>
    public static final PylonItemSchema RAW_FERRODURALUM = new PylonItemSchema(
            pylonKey("raw_ferroduralum"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.RAW_GOLD, key).build()
    );
    static {
        RAW_FERRODURALUM.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("raw_ferroduralum"), RAW_FERRODURALUM.getItemStack())
                .shape("CGR", "   ", "   ")
                .setIngredient('C', Material.COPPER_ORE)
                .setIngredient('G', Material.GOLD_ORE)
                .setIngredient('R', Material.REDSTONE);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema FERRODURALUM_INGOT = new PylonItemSchema(
            pylonKey("ferroduralum_ingot"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GOLD_INGOT, key).build()
    );
    static {
        FERRODURALUM_INGOT.register();
        FurnaceRecipe recipe = new FurnaceRecipe(
                pylonKey("ferroduralum_ingot"),
                FERRODURALUM_INGOT.getItemStack(),
                new RecipeChoice.ExactChoice(RAW_FERRODURALUM.getItemStack()),
                FERRODURALUM_INGOT.getSettings().getOrThrow("cooking.xp", Double.class).floatValue(),
                RecipeUtils.DEFAULT_FURNACE_TIME_TICKS
        );
        recipe.setCategory(CookingBookCategory.MISC);
        RecipeTypes.VANILLA_FURNACE.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("primitive_alloying"),
                8L,
                RAW_FERRODURALUM,
                FERRODURALUM_INGOT
        ).register();
    }

    public static final PylonItemSchema FERRODURALUM_SHEET = new PylonItemSchema(
            pylonKey("ferroduralum_sheet"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.PAPER, key).build()
    );
    static {
        FERRODURALUM_SHEET.register();
        Hammer.Recipe.RECIPE_TYPE.addRecipe(new Hammer.Recipe(
                pylonKey("ferroduralum_sheet"),
                List.of(FERRODURALUM_INGOT.getItemStack()),
                FERRODURALUM_SHEET.getItemStack(),
                MiningLevel.IRON,
                0.25f
        ));
    }

    static {
        new Research(
                pylonKey("metal_ductility"),
                5L,
                COPPER_SHEET,
                GOLD_SHEET,
                IRON_SHEET,
                FERRODURALUM_SHEET
        ).register();
    }

    public static final PylonItemSchema FERRODURALUM_SWORD = new PylonItemSchema(
            pylonKey("ferroduralum_sword"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GOLDEN_SWORD, key)
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                                    pylonKey("ferroduralum_sword_damage"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
                    .set(DataComponentTypes.MAX_DAMAGE, 300)
                    .build()
    );
    static {
        FERRODURALUM_SWORD.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_sword"), FERRODURALUM_SWORD.getItemStack())
                .shape(" F ", " F ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT.getItemStack())
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema FERRODURALUM_AXE = new PylonItemSchema(
            pylonKey("ferroduralum_axe"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GOLDEN_AXE, key)
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_axe_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
                    .set(DataComponentTypes.MAX_DAMAGE, 300)
                    .build()
    );
    static {
        FERRODURALUM_AXE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_axe"), FERRODURALUM_AXE.getItemStack())
                .shape("FF ", "FS ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT.getItemStack())
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema FERRODURALUM_PICKAXE = new PylonItemSchema(
            pylonKey("ferroduralum_pickaxe"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GOLDEN_PICKAXE, key)
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_pickaxe_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
                    .set(DataComponentTypes.MAX_DAMAGE, 300)
                    .build()
    );
    static {
        FERRODURALUM_PICKAXE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_pickaxe"), FERRODURALUM_PICKAXE.getItemStack())
                .shape("FFF", " S ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT.getItemStack())
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema FERRODURALUM_SHOVEL = new PylonItemSchema(
            pylonKey("ferroduralum_shovel"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GOLDEN_SHOVEL, key)
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_shovel_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
                    .set(DataComponentTypes.MAX_DAMAGE, 300)
                    .build()
    );
    static {
        FERRODURALUM_SHOVEL.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_shovel"), FERRODURALUM_SHOVEL.getItemStack())
                .shape(" F ", " S ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT.getItemStack())
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema FERRODURALUM_HOE = new PylonItemSchema(
            pylonKey("ferroduralum_hoe"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GOLDEN_HOE, key)
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_hoe_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
                    .set(DataComponentTypes.MAX_DAMAGE, 300)
                    .build()
    );
    static {
        FERRODURALUM_HOE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_hoe"), FERRODURALUM_HOE.getItemStack())
                .shape("FF ", " S ", " S ")
                .setIngredient('F', FERRODURALUM_INGOT.getItemStack())
                .setIngredient('S', Material.STICK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("primitive_alloy_tools"),
                10L,
                FERRODURALUM_SWORD,
                FERRODURALUM_AXE,
                FERRODURALUM_PICKAXE,
                FERRODURALUM_SHOVEL,
                FERRODURALUM_HOE
        ).register();
    }

    public static final PylonItemSchema FERRODURALUM_HELMET = new PylonItemSchema(
            pylonKey("ferroduralum_helmet"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GOLDEN_HELMET, key)
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
                    .build()
    );
    static {
        FERRODURALUM_HELMET.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_helmet"), FERRODURALUM_HELMET.getItemStack())
                .shape("FFF", "F F", "   ")
                .setIngredient('F', FERRODURALUM_INGOT.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema FERRODURALUM_CHESTPLATE = new PylonItemSchema(
            pylonKey("ferroduralum_chestplate"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GOLDEN_CHESTPLATE, key)
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
                    .build()
    );
    static {
        FERRODURALUM_CHESTPLATE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_chestplate"), FERRODURALUM_CHESTPLATE.getItemStack())
                .shape("F F", "FFF", "FFF")
                .setIngredient('F', FERRODURALUM_INGOT.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema FERRODURALUM_LEGGINGS = new PylonItemSchema(
            pylonKey("ferroduralum_leggings"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GOLDEN_LEGGINGS, key)
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
                    .build()
    );
    static {
        FERRODURALUM_LEGGINGS.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_leggings"), FERRODURALUM_LEGGINGS.getItemStack())
                .shape("FFF", "F F", "F F")
                .setIngredient('F', FERRODURALUM_INGOT.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema FERRODURALUM_BOOTS = new PylonItemSchema(
            pylonKey("ferroduralum_boots"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GOLDEN_BOOTS, key)
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
                    .build()
    );
    static {
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_boots"), FERRODURALUM_BOOTS.getItemStack())
                .shape("F F", "F F", "   ")
                .setIngredient('F', FERRODURALUM_INGOT.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("primitive_alloy_armor"),
                10L,
                FERRODURALUM_HELMET,
                FERRODURALUM_CHESTPLATE,
                FERRODURALUM_LEGGINGS,
                FERRODURALUM_BOOTS
        ).register();
    }
    //</editor-fold>

    public static final PylonItemSchema COMPRESSED_WOOD = new PylonItemSchema(
            pylonKey("compressed_wood"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.OAK_WOOD, key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build()
    );
    static {
        COMPRESSED_WOOD.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("compressed_wood"), COMPRESSED_WOOD.getItemStack())
                .shape("WWW", "WWW", "WWW")
                .setIngredient('W', new RecipeChoice.MaterialChoice(Tag.LOGS));
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    //<editor-fold desc="Portable Items" defaultstate=collapsed>
    public static final PylonItemSchema PORTABILITY_CATALYST = new PylonItemSchema(
            pylonKey("portability_catalyst"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.AMETHYST_SHARD, key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build()
    );
    static {
        PORTABILITY_CATALYST.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("portability_catalyst"), PORTABILITY_CATALYST.getItemStack())
                .shape("RRR", "RPR", "RRR")
                .setIngredient('R', Material.REDSTONE_BLOCK)
                .setIngredient('P', Material.ENDER_PEARL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema COMPRESSED_OBSIDIAN = new PylonItemSchema(
            pylonKey("compressed_obsidian"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.OBSIDIAN, key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build()
    );
    static {
        COMPRESSED_OBSIDIAN.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("compressed_obsidian"), COMPRESSED_OBSIDIAN.getItemStack())
                .shape("OOO", "OOO", "OOO")
                .setIngredient('O', Material.OBSIDIAN);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("compression"),
                5L,
                COMPRESSED_WOOD,
                COMPRESSED_OBSIDIAN
        ).register();
    }

    public static final PylonItemSchema PORTABLE_CRAFTING_TABLE = new PylonItemSchema(
            pylonKey("portable_crafting_table"),
            PortableCraftingTable.class,
            key -> ItemStackBuilder.defaultBuilder(Material.CRAFTING_TABLE, key).build()
    );
    static {
        PORTABLE_CRAFTING_TABLE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("portable_crafting_table"), PORTABLE_CRAFTING_TABLE.getItemStack())
                .shape("WWW", "WCW", "   ")
                .setIngredient('W', COMPRESSED_WOOD.getItemStack())
                .setIngredient('C', PORTABILITY_CATALYST.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema PORTABLE_DUSTBIN = new PylonItemSchema(
            pylonKey("portable_dustbin"),
            PortableDustbin.class,
            key -> ItemStackBuilder.defaultBuilder(Material.CAULDRON, key).build()
    );
    static {
        PORTABLE_DUSTBIN.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("portable_dustbin"), PORTABLE_DUSTBIN.getItemStack())
                .shape("CCC", "IAI", "III")
                .setIngredient('I', IRON_SHEET.getItemStack())
                .setIngredient('C', Material.CACTUS)
                .setIngredient('A', PORTABILITY_CATALYST.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema PORTABLE_ENDER_CHEST = new PylonItemSchema(
            pylonKey("portable_ender_chest"),
            PortableEnderChest.class,
            key -> ItemStackBuilder.defaultBuilder(Material.ENDER_CHEST, key).build()
    );
    static {
        PORTABLE_ENDER_CHEST.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("portable_ender_chest"), PORTABLE_ENDER_CHEST.getItemStack())
                .shape("OOO", "OEO", "OOO")
                .setIngredient('O', COMPRESSED_OBSIDIAN.getItemStack())
                .setIngredient('E', PORTABILITY_CATALYST.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }
    //</editor-fold>

    static {
        new Research(
                pylonKey("portability"),
                7L,
                PORTABILITY_CATALYST,
                PORTABLE_CRAFTING_TABLE,
                PORTABLE_DUSTBIN,
                PORTABLE_ENDER_CHEST
        ).register();
    }

    //<editor-fold desc="Medical items" defaultstate=collapsed>
    public static final PylonItemSchema FIBER = new PylonItemSchema(
            pylonKey("fiber"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.BAMBOO_MOSAIC, key).build()
    );
    static {
        FIBER.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("fiber"), FIBER.getItemStack())
                .shape("SSS", "   ", "   ")
                .setIngredient('S', Material.STRING);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema PLASTER = new PylonItemSchema(
            pylonKey("plaster"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.SMOOTH_STONE_SLAB, key).build()
    );
    static {
        PLASTER.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("plaster"), PLASTER.getItemStack())
                .shape("CC ", "CC ", "   ")
                .setIngredient('C', Material.CLAY);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema BANDAGE = new PylonItemSchema(
            pylonKey("bandage"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.COBWEB, key)
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                                    new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1, true)
                            ), 1))
                            .consumeSeconds(1.25f)
                            .animation(ItemUseAnimation.BOW)
                            .hasConsumeParticles(false)
                            .build())
                    .build()

    );
    static {
        BANDAGE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("bandage"), BANDAGE.getItemStack())
                .shape("FF ", "FF ", "   ")
                .setIngredient('F', FIBER.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema SPLINT = new PylonItemSchema(
            pylonKey("splint"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.STICK, key)
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                                    new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 2, true)
                            ), 1))
                            .consumeSeconds(3.0f)
                            .animation(ItemUseAnimation.BOW)
                            .hasConsumeParticles(false)
                            .build())
                    .build()
    );
    static {
        SPLINT.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("splint"), SPLINT.getItemStack())
                .shape("PPP", "   ", "PPP")
                .setIngredient('P', PLASTER.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("first_aid"),
                5L,
                BANDAGE,
                SPLINT
        ).register();
    }

    public static final PylonItemSchema DISINFECTANT = new PylonItemSchema(
            pylonKey("disinfectant"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.BREWER_POTTERY_SHERD, key)
                    // Using the actual potion material doesn't let you set the name properly, gives you a
                    // class string of a nonexistant potion type for some reason
                    .set(DataComponentTypes.ITEM_MODEL, Material.POTION.getKey())
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .hasConsumeParticles(false)
                            .consumeSeconds(3.0f)
                            .animation(ItemUseAnimation.BOW)
                            .addEffect(ConsumeEffect.clearAllStatusEffects())
                            .build())
                    .build()
    );
    static {
        DISINFECTANT.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("disinfectant"), DISINFECTANT.getItemStack())
                .shape("DDD", "D D", "DDD")
                .setIngredient('D', Material.DRIPSTONE_BLOCK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema MEDKIT = new PylonItemSchema(
            pylonKey("medkit"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.SHULKER_SHELL, key)
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .consumeSeconds(7.0f)
                            .animation(ItemUseAnimation.BOW)
                            .hasConsumeParticles(false)
                            .addEffect(ConsumeEffect.clearAllStatusEffects())
                            .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                                    new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 3, true)
                            ), 1))
                    )
                    .build()
    );
    static {
        MEDKIT.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("medkit"), MEDKIT.getItemStack())
                .shape("PFP", "DDD", "PFP")
                .setIngredient('P', PLASTER.getItemStack())
                .setIngredient('D', DISINFECTANT.getItemStack())
                .setIngredient('F', FIBER.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }
    //</editor-fold>

    static {
        new Research(
                pylonKey("medicine"),
                10L,
                DISINFECTANT,
                MEDKIT
        ).register();
    }

    public static final Sprinkler.SprinklerItem.Schema SPRINKLER = new Sprinkler.SprinklerItem.Schema(
            pylonKey("sprinkler"),
            Sprinkler.SprinklerItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.FLOWER_POT, key).build()
    );
    static {
        SPRINKLER.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("sprinkler"), SPRINKLER.getItemStack())
                .shape("B B", "B B", "FRF")
                .setIngredient('B', new ItemStack(Material.BRICK))
                .setIngredient('F', FERRODURALUM_INGOT.getItemStack())
                .setIngredient('R', new ItemStack(Material.REPEATER));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);

        new Research(
                pylonKey("plant_growth_automated"),
                10L,
                SPRINKLER
        ).register();
    }

    public static final RecoilArrow RECOIL_ARROW = new RecoilArrow(
            pylonKey("recoil_arrow"),
            key -> ItemStackBuilder.defaultBuilder(Material.ARROW, key)
                    .amount(8)
                    .build(),
            0.75f
    );
    static {
        RECOIL_ARROW.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("recoil_arrow"), RECOIL_ARROW.getItemStack())
                .shape("SSS", "SAS", "SSS")
                .setIngredient('S', Material.SLIME_BALL)
                .setIngredient('A', Material.ARROW);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(RecipeUtils.reflect(recipe));

        new Research(
                pylonKey("newtons_third_law"),
                15L,
                RECOIL_ARROW
        ).register();
    }

    public static final LumberAxe LUMBER_AXE = new LumberAxe(
            pylonKey("lumber_axe"),
            key -> ItemStackBuilder.defaultBuilder(Material.WOODEN_AXE, key).build()
    );
    static {
        LUMBER_AXE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("lumber_axe"), LUMBER_AXE.getItemStack())
                .shape("WWW", "WAW", "III")
                .setIngredient('W', COMPRESSED_WOOD.getItemStack())
                .setIngredient('A', Material.WOODEN_AXE)
                .setIngredient('I', Material.IRON_BLOCK);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);

        new Research(
                pylonKey("gravity"),
                10L,
                LUMBER_AXE
        ).register();
    }

    public static final PylonItemSchema MAGIC_DUST = new PylonItemSchema(
            pylonKey("magic_dust"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.AMETHYST_CLUSTER, key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build()
    );
    static {
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("magic_dust"), MAGIC_DUST.getItemStack());
        recipe.shape("AGA", "GGG", "AGA")
                .setIngredient('A', new RecipeChoice.MaterialChoice(
                        Material.AMETHYST_BLOCK,
                        Material.AMETHYST_CLUSTER,
                        Material.LARGE_AMETHYST_BUD,
                        Material.MEDIUM_AMETHYST_BUD,
                        Material.SMALL_AMETHYST_BUD)
                )
                .setIngredient('G', Material.GLOWSTONE);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);

        new Research(
                pylonKey("fairies"),
                5L,
                MAGIC_DUST
        ).register();
    }

    public static final BeheadingSword BEHEADING_SWORD = new BeheadingSword(
            pylonKey("beheading_sword"),
            key -> ItemStackBuilder.defaultBuilder(Material.DIAMOND_SWORD, key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build()
    );
    static {
        BEHEADING_SWORD.register();
        ItemStack silkTouchBook = ItemStackBuilder.of(Material.ENCHANTED_BOOK)
                .set(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments()
                        .add(Enchantment.SILK_TOUCH, 1)
                        .build())
                .build();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("beheading_sword"), BEHEADING_SWORD.getItemStack())
                .shape("MHM", "MBM", "MSM")
                .setIngredient('M', MAGIC_DUST.getItemStack())
                .setIngredient('B', new RecipeChoice.ExactChoice(silkTouchBook))
                .setIngredient('S', Material.DIAMOND_SWORD)
                // Not allowing player heads to be used since too many plugins give player heads as custom items
                .setIngredient('H', new RecipeChoice.MaterialChoice(
                        Material.CREEPER_HEAD,
                        Material.ZOMBIE_HEAD,
                        Material.PIGLIN_HEAD,
                        Material.DRAGON_HEAD
                ));
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);

        new Research(
                pylonKey("french_revolution"),
                10L,
                BEHEADING_SWORD
        ).register();
    }

    public static final PylonItemSchema GRINDSTONE = new SimpleBlockPlacerItemSchema(
            pylonKey("grindstone"),
            key -> ItemStackBuilder.defaultBuilder(Material.SMOOTH_STONE_SLAB, key).build(),
            PylonBlocks.GRINDSTONE
    );
    static {
        GRINDSTONE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("grindstone"), BEHEADING_SWORD.getItemStack())
                .shape("STS", "   ", "   ")
                .setIngredient('T', new ItemStack(Material.SMOOTH_STONE))
                .setIngredient('S', new ItemStack(Material.SMOOTH_STONE_SLAB));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema GRINDSTONE_HANDLE = new SimpleBlockPlacerItemSchema(
            pylonKey("grindstone_handle"),
            key -> ItemStackBuilder.defaultBuilder(Material.OAK_FENCE, key).build(),
            PylonBlocks.GRINDSTONE_HANDLE
    );
    static {
        GRINDSTONE_HANDLE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("grindstone_handle"), BEHEADING_SWORD.getItemStack())
                .shape("F  ", "F  ", "F  ")
                .setIngredient('F', new RecipeChoice.MaterialChoice(Tag.FENCES));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("grinding"),
                5L,
                GRINDSTONE,
                GRINDSTONE_HANDLE
        ).register();
    }

    public static final PylonItemSchema FLOUR = new PylonItemSchema(
            pylonKey("flour"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.SUGAR, key).build()
    );
    static {
        FLOUR.register();
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                pylonKey("flour"),
                new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)),
                2,
                FLOUR.getItemStack(),
                2,
                Material.WHEAT.createBlockData(data -> {
                    Ageable ageable = (Ageable) data;
                    ageable.setAge(ageable.getMaximumAge());
                })
        ));
    }

    public static final PylonItemSchema DOUGH = new PylonItemSchema(
            pylonKey("dough"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.YELLOW_DYE, key).build()
    );
    static {
        DOUGH.register();

        WaterCauldronRightClickRecipe.RECIPE_TYPE.addRecipe(new WaterCauldronRightClickRecipe(
                pylonKey("dough"),
                new RecipeChoice.ExactChoice(FLOUR.getItemStack()),
                DOUGH.getItemStack()
        ));

        FurnaceRecipe furnaceBreadRecipe = new FurnaceRecipe(
                pylonKey("bread_from_dough_furnace"),
                new ItemStack(Material.BREAD),
                new RecipeChoice.ExactChoice(DOUGH.getItemStack()),
                0.2F,
                10 * 20);
        furnaceBreadRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_FURNACE.addRecipe(furnaceBreadRecipe);

        SmokingRecipe smokerBreadRecipe = new SmokingRecipe(
                pylonKey("bread_from_dough_smoker"),
                new ItemStack(Material.BREAD),
                new RecipeChoice.ExactChoice(DOUGH.getItemStack()),
                0.2F,
                5 * 20);
        smokerBreadRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_SMOKING.addRecipe(smokerBreadRecipe);
    }

    static {
        new Research(
                pylonKey("baking"),
                2L,
                FLOUR,
                DOUGH
        ).register();
    }

    public static final HealthTalisman HEALTH_TALISMAN_SIMPLE = new HealthTalisman(
            pylonKey("health_talisman_simple"),
            key -> ItemStackBuilder.defaultBuilder(Material.AMETHYST_SHARD, key)
                    .set(DataComponentTypes.MAX_STACK_SIZE, 1)
                    .build()
    );
    static {
        HEALTH_TALISMAN_SIMPLE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("health_talisman_simple"), HEALTH_TALISMAN_SIMPLE.getItemStack())
                .shape("GGG", "GRG", "GGG")
                .setIngredient('G', Material.GLISTERING_MELON_SLICE)
                .setIngredient('R', Material.REDSTONE);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final HealthTalisman HEALTH_TALISMAN_ADVANCED = new HealthTalisman(
            pylonKey("health_talisman_advanced"),
            key -> ItemStackBuilder.defaultBuilder(Material.AMETHYST_CLUSTER, key)
                    .set(DataComponentTypes.MAX_STACK_SIZE, 1)
                    .build()
    );
    static {
        HEALTH_TALISMAN_ADVANCED.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("health_talisman_advanced"), HEALTH_TALISMAN_ADVANCED.getItemStack())
                .shape("SSS", "SSS", "SSS")
                .setIngredient('S', HEALTH_TALISMAN_SIMPLE.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final HealthTalisman HEALTH_TALISMAN_ULTIMATE = new HealthTalisman(
            pylonKey("health_talisman_ultimate"),
            key -> ItemStackBuilder.defaultBuilder(Material.BUDDING_AMETHYST, key)
                    .set(DataComponentTypes.MAX_STACK_SIZE, 1)
                    .build()
    );
    static {
        HEALTH_TALISMAN_ULTIMATE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("health_talisman_ultimate"), HEALTH_TALISMAN_ULTIMATE.getItemStack())
                .shape("AAA", "AAA", "AAA")
                .setIngredient('A', HEALTH_TALISMAN_ADVANCED.getItemStack());
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("better_health"),
                10L,
                HEALTH_TALISMAN_SIMPLE,
                HEALTH_TALISMAN_ADVANCED,
                HEALTH_TALISMAN_ULTIMATE
        ).register();
    }

    public static final PylonItemSchema MIXING_POT = new SimpleBlockPlacerItemSchema(
            pylonKey("mixing_pot"),
            key -> ItemStackBuilder.defaultBuilder(Material.CAULDRON, key).build(),
            PylonBlocks.MIXING_POT
    );
    static {
        MIXING_POT.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("mixing_pot"), MIXING_POT.getItemStack())
                .shape("f f", "f f", "fff")
                .setIngredient('f', FERRODURALUM_INGOT.getItemStack());
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);

        new Research(
                pylonKey("homogeneity"),
                6L,
                MIXING_POT
        ).register();
    }

    public static final PylonItemSchema SHIMMER_DUST_1 = new PylonItemSchema(
            pylonKey("shimmer_dust_1"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.SUGAR, key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build()
    );
    static {
        SHIMMER_DUST_1.register();
        ShapelessRecipe recipe = new ShapelessRecipe(pylonKey("shimmer_dust_1"), SHIMMER_DUST_1.getItemStack())
                .addIngredient(COPPER_DUST.getItemStack())
                .addIngredient(Material.FLINT)
                .addIngredient(Material.CLAY_BALL);
        recipe.setCategory(CraftingBookCategory.MISC);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema SHIMMER_DUST_2 = new PylonItemSchema(
            pylonKey("shimmer_dust_2"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.SUGAR, key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build()
    );
    static {
        SHIMMER_DUST_2.register();
        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                pylonKey("shimmer_dust_2"),
                Map.of(
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_1.getItemStack()), 1,
                        new RecipeChoice.ExactChoice(GOLD_DUST.getItemStack()), 1,
                        new RecipeChoice.ExactChoice(new ItemStack(Material.REDSTONE)), 1
                ),
                SHIMMER_DUST_2.getItemStack(),
                false,
                1
        ));
    }

    public static final PylonItemSchema ENRICHED_NETHERRACK = new SimpleBlockPlacerItemSchema(
            pylonKey("enriched_netherrack"),
            key -> ItemStackBuilder.defaultBuilder(Material.NETHERRACK, key).build(),
            PylonBlocks.ENRICHED_NETHERRACK
    );
    static {
        ENRICHED_NETHERRACK.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("enriched_netherrack"), ENRICHED_NETHERRACK.getItemStack())
                        .shape(" s ", "sns", " s ")
                        .setIngredient('n', new ItemStack(Material.NETHERRACK))
                        .setIngredient('s', SHIMMER_DUST_2.getItemStack());
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema COVALENT_BINDER = new PylonItemSchema(
            pylonKey("covalent_binder"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.LIGHT_BLUE_DYE, key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build()
    );
    static {
        COVALENT_BINDER.register();
        ItemStack output = COVALENT_BINDER.getItemStack().clone();
        output.setAmount(6);
        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                pylonKey("covalent_binder"),
                Map.of(
                        new RecipeChoice.ExactChoice(new ItemStack(Material.GUNPOWDER)), 4,
                        new RecipeChoice.ExactChoice(new ItemStack(Material.EMERALD)), 1,
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_1.getItemStack()), 1
                ),
                output,
                true,
                3
        ));
    }

    public static final PylonItemSchema SHIMMER_DUST_3 = new PylonItemSchema(
            pylonKey("shimmer_dust_3"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.SUGAR, key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build()

    );
    static {
        SHIMMER_DUST_3.register();
        MagicAltar.Recipe.RECIPE_TYPE.addRecipe(new MagicAltar.Recipe(
                pylonKey("shimmer_dust_3"),
                new ArrayList<>(Arrays.asList(
                        new RecipeChoice.ExactChoice(new ItemStack(Material.REDSTONE_BLOCK)),
                        null,
                        new RecipeChoice.ExactChoice(COVALENT_BINDER.getItemStack()),
                        new RecipeChoice.ExactChoice(COVALENT_BINDER.getItemStack()),
                        null,
                        new RecipeChoice.ExactChoice(COVALENT_BINDER.getItemStack()),
                        new RecipeChoice.ExactChoice(COVALENT_BINDER.getItemStack()),
                        null
                )),
                new RecipeChoice.ExactChoice(SHIMMER_DUST_2.getItemStack()),
                SHIMMER_DUST_3.getItemStack(),
                5
        ));
    }

    public static final PylonItemSchema SHIMMER_SKULL = new PylonItemSchema(
            pylonKey("shimmer_skull"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.WITHER_SKELETON_SKULL, key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build()
    );
    static {
        SHIMMER_SKULL.register();
        MagicAltar.Recipe.RECIPE_TYPE.addRecipe(new MagicAltar.Recipe(
                pylonKey("shimmer_skull"),
                new ArrayList<>(Arrays.asList(
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_3.getItemStack()),
                        null,
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_3.getItemStack()),
                        null,
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_3.getItemStack()),
                        null,
                        new RecipeChoice.ExactChoice(SHIMMER_DUST_3.getItemStack()),
                        null
                )),
                new RecipeChoice.ExactChoice(new ItemStack(Material.WITHER_SKELETON_SKULL)),
                SHIMMER_SKULL.getItemStack(),
                30
        ));
    }

    static {
        new Research(
                pylonKey("glitter"),
                5L,
                SHIMMER_DUST_1,
                SHIMMER_DUST_2,
                SHIMMER_DUST_3,
                SHIMMER_SKULL
        ).register();
    }

    public static final PylonItemSchema PEDESTAL = new SimpleBlockPlacerItemSchema(
            pylonKey("pedestal"),
            key -> ItemStackBuilder.defaultBuilder(Material.STONE_BRICK_WALL, key).build(),
            PylonBlocks.PEDESTAL
    );
    static {
        PEDESTAL.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("pedestal"), PEDESTAL.getItemStack())
                .shape("s  ", "s  ", "s  ")
                .setIngredient('s', new ItemStack(Material.STONE_BRICK_WALL));
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);

        new Research(
                pylonKey("showing_off"),
                2L,
                PEDESTAL
        ).register();
    }

    public static final PylonItemSchema MAGIC_PEDESTAL = new SimpleBlockPlacerItemSchema(
            pylonKey("magic_pedestal"),
            key -> ItemStackBuilder.defaultBuilder(Material.MOSSY_STONE_BRICK_WALL, key).build(),
            PylonBlocks.MAGIC_PEDESTAL
    );
    static {
        MAGIC_PEDESTAL.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("magic_pedestal"), MAGIC_PEDESTAL.getItemStack())
                .shape("c c", " p ", "c c")
                .setIngredient('p', PEDESTAL.getItemStack())
                .setIngredient('c', COVALENT_BINDER.getItemStack());
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final PylonItemSchema MAGIC_ALTAR = new SimpleBlockPlacerItemSchema(
            pylonKey("magic_altar"),
            key -> ItemStackBuilder.defaultBuilder(Material.SMOOTH_STONE_SLAB, key).build(),
            PylonBlocks.MAGIC_ALTAR
    );
    static {
        MAGIC_ALTAR.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("magic_altar"), MAGIC_ALTAR.getItemStack())
                        .shape("   ", "dpd", "dsd")
                        .setIngredient('p', PEDESTAL.getItemStack())
                        .setIngredient('s', new ItemStack(Material.SMOOTH_STONE_SLAB))
                        .setIngredient('d', SHIMMER_DUST_2.getItemStack());
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    static {
        new Research(
                pylonKey("magic"),
                6L,
                MAGIC_PEDESTAL,
                MAGIC_ALTAR,
                COVALENT_BINDER
        ).register();
    }

    public static final Loupe.Schema LOUPE = new Loupe.Schema(
            pylonKey("loupe"),
            Loupe.class,
            key -> ItemStackBuilder.defaultBuilder(Material.GLASS_PANE, key)
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .animation(ItemUseAnimation.SPYGLASS)
                            .hasConsumeParticles(false)
                            .consumeSeconds(3)
                    )
                    .build()
    );

    static {
        LOUPE.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("loupe"), LOUPE.getItemStack())
                .shape(" C ", "CGC", " C ")
                .setIngredient('C', Material.COPPER_INGOT)
                .setIngredient('G', Material.GLASS);
        recipe.setCategory(CraftingBookCategory.EQUIPMENT);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final ResearchPack.Schema RESEARCH_PACK_1 = new ResearchPack.Schema(
            pylonKey("research_pack_1"),
            ResearchPack.class,
            key -> ItemStackBuilder.defaultBuilder(Material.BOOK, key)
                    .set(DataComponentTypes.MAX_STACK_SIZE, 1)
                    .build()
    );
    static {
        RESEARCH_PACK_1.register();
        // TODO recipe when fluid api is done
    }

    public static final ExplosiveTarget.ExplosiveTargetItem.Schema EXPLOSIVE_TARGET = new ExplosiveTarget.ExplosiveTargetItem.Schema(
            pylonKey("explosive_target"),
            key -> ItemStackBuilder.defaultBuilder(Material.TARGET, key)
                    .build(),
            PylonBlocks.EXPLOSIVE_TARGET
    );
    static {
        EXPLOSIVE_TARGET.register();
        ShapedRecipe recipe = new ShapedRecipe(pylonKey("explosive_target"), EXPLOSIVE_TARGET.getItemStack());
        recipe.shape(
                "TTT",
                "TXT",
                "TTT"
        );
        recipe.setIngredient('T', Material.TNT);
        recipe.setIngredient('X', Material.TARGET);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final ExplosiveTarget.ExplosiveTargetItem.Schema FIERY_EXPLOSIVE_TARGET = new ExplosiveTarget.ExplosiveTargetItem.Schema(
            pylonKey("explosive_target_fiery"),
            key -> ItemStackBuilder.defaultBuilder(Material.TARGET, key)
                    .build(),
            PylonBlocks.FIERY_EXPLOSIVE_TARGET
    );
    static {
        FIERY_EXPLOSIVE_TARGET.register();
        ShapelessRecipe recipe = new ShapelessRecipe(pylonKey("explosive_target_fiery"), FIERY_EXPLOSIVE_TARGET.getItemStack());
        recipe.addIngredient(EXPLOSIVE_TARGET.getItemStack());
        recipe.addIngredient(Material.FIRE_CHARGE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final ExplosiveTarget.ExplosiveTargetItem.Schema SUPER_EXPLOSIVE_TARGET = new ExplosiveTarget.ExplosiveTargetItem.Schema(
            pylonKey("explosive_target_super"),
            key -> ItemStackBuilder.defaultBuilder(Material.TARGET, key)
                    .build(),
            PylonBlocks.SUPER_EXPLOSIVE_TARGET
    );
    static {
        SUPER_EXPLOSIVE_TARGET.register();
        ShapelessRecipe recipe = new ShapelessRecipe(pylonKey("explosive_target_super"), SUPER_EXPLOSIVE_TARGET.getItemStack());
        recipe.addIngredient(4, EXPLOSIVE_TARGET.getItemStack());
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    public static final ExplosiveTarget.ExplosiveTargetItem.Schema SUPER_FIERY_EXPLOSIVE_TARGET = new ExplosiveTarget.ExplosiveTargetItem.Schema(
            pylonKey("explosive_target_super_fiery"),
            key -> ItemStackBuilder.defaultBuilder(Material.TARGET, key)
                    .build(),
            PylonBlocks.SUPER_FIERY_EXPLOSIVE_TARGET
    );
    static {
        SUPER_FIERY_EXPLOSIVE_TARGET.register();
        ShapelessRecipe recipe = new ShapelessRecipe(pylonKey("explosive_target_super_fiery"), SUPER_FIERY_EXPLOSIVE_TARGET.getItemStack());
        recipe.addIngredient(SUPER_EXPLOSIVE_TARGET.getItemStack());
        recipe.addIngredient(Material.FIRE_CHARGE);
        recipe.setCategory(CraftingBookCategory.BUILDING);
        RecipeTypes.VANILLA_CRAFTING.addRecipe(recipe);
    }

    private static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }

    // Calling this method forces all the static blocks to run, which initialises our items
    public static void initialize() {}
}
