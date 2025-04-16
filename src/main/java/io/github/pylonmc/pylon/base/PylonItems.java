package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.*;
import io.github.pylonmc.pylon.base.misc.WaterCauldronRightClickRecipe;
import io.github.pylonmc.pylon.base.items.watering.Sprinkler;
import io.github.pylonmc.pylon.base.items.watering.WateringCan;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimpleItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
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
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.*;
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

    public static final PylonItemSchema COPPER_DUST = new PylonItemSchema(
            pylonKey("copper_dust"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.of(Material.GLOWSTONE_DUST).defaultTranslatableName(key).build()
    );

    public static final PylonItemSchema GOLD_DUST = new PylonItemSchema(
            pylonKey("gold_dust"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.of(Material.GLOWSTONE_DUST).defaultTranslatableName(key).build()
    );

    public static final PylonItemSchema COPPER_SHEET = new SimpleItemSchema<>(
            pylonKey("copper_sheet"),
            key -> ItemStackBuilder.of(Material.PAPER)
                    .defaultTranslatableName(key)
                    .build(),
            Hammer.Recipe.RECIPE_TYPE,
            sheet -> new Hammer.Recipe(
                    pylonKey("copper_sheet"),
                    List.of(new ItemStack(Material.COPPER_INGOT)),
                    sheet,
                    MiningLevel.STONE,
                    0.25f
            )
    );

    public static final PylonItemSchema GOLD_SHEET = new SimpleItemSchema<>(
            pylonKey("gold_sheet"),
            key -> ItemStackBuilder.of(Material.PAPER)
                    .defaultTranslatableName(key)
                    .build(),
            Hammer.Recipe.RECIPE_TYPE,
            sheet -> new Hammer.Recipe(
                    pylonKey("gold_sheet"),
                    List.of(new ItemStack(Material.GOLD_INGOT)),
                    sheet,
                    MiningLevel.STONE,
                    0.25f
            )
    );

    public static final PylonItemSchema IRON_SHEET = new SimpleItemSchema<>(
            pylonKey("iron_sheet"),
            key -> ItemStackBuilder.of(Material.PAPER)
                    .defaultTranslatableName(key)
                    .build(),
            Hammer.Recipe.RECIPE_TYPE,
            sheet -> new Hammer.Recipe(
                    pylonKey("iron_sheet"),
                    List.of(new ItemStack(Material.IRON_INGOT)),
                    sheet,
                    MiningLevel.IRON,
                    0.25f
            )
    );



    //<editor-fold desc="Hammers" defaultstate=collapsed>
    public static final Hammer.Schema STONE_HAMMER = new Hammer.Schema(
            pylonKey("hammer_stone"),
            Material.COBBLESTONE,
            Material.STONE,
            Material.STONE_PICKAXE,
            MiningLevel.STONE,
            (1.0 / 3) - 4,
            1,
            1
    );

    public static final Hammer.Schema IRON_HAMMER = new Hammer.Schema(
            pylonKey("hammer_iron"),
            Material.IRON_INGOT,
            Material.IRON_BLOCK,
            Material.IRON_PICKAXE,
            MiningLevel.IRON,
            (1.0 / 2) - 4,
            1.5,
            3
    );

    public static final Hammer.Schema DIAMOND_HAMMER = new Hammer.Schema(
            pylonKey("hammer_diamond"),
            Material.DIAMOND,
            Material.DIAMOND_BLOCK,
            Material.DIAMOND_PICKAXE,
            MiningLevel.DIAMOND,
            1 - 4,
            2,
            5
    );

    public static final WateringCan WATERING_CAN = new WateringCan(
            pylonKey("watering_can"),
            WateringCan.WateringCanItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.BUCKET, key).build()
    );

    public static final MonsterJerky MONSTER_JERKY = new MonsterJerky(
            pylonKey("monster_jerky"),
            SimplePylonItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.ROTTEN_FLESH, key)
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable().build())
                    .build()
    );

    public static final PylonItemSchema RAW_FERRODURALUM = new SimpleItemSchema<>(
            pylonKey("raw_ferroduralum"),
            key -> ItemStackBuilder.defaultBuilder(Material.RAW_GOLD, key).build(),
            RecipeTypes.VANILLA_CRAFTING,
            ferroduralum -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("raw_ferroduralum"), ferroduralum);
                recipe.shape(
                        "CIR",
                        "   ",
                        "   "
                );
                recipe.setIngredient('C', Material.COPPER_ORE);
                recipe.setIngredient('I', Material.GOLD_ORE);
                recipe.setIngredient('R', Material.REDSTONE);
                recipe.setCategory(CraftingBookCategory.MISC);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_INGOT = new SimpleItemSchema<>(
            pylonKey("ferroduralum_ingot"),
            key -> ItemStackBuilder.defaultBuilder(Material.GOLD_INGOT, key).build(),
            RecipeTypes.VANILLA_FURNACE,
            ingot -> {
                FurnaceRecipe recipe = new FurnaceRecipe(pylonKey("ferroduralum_ingot"), ingot, new RecipeChoice.ExactChoice(RAW_FERRODURALUM.getItemStack()), 0.25f, 10 * 20);
                recipe.setCategory(CookingBookCategory.MISC);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_SHEET = new SimpleItemSchema<>(
            pylonKey("ferroduralum_sheet"),
            ItemStackBuilder.of(Material.PAPER).name("Ferroduralum Sheet").build(),
            Hammer.Recipe.RECIPE_TYPE,
            sheet -> new Hammer.Recipe(
                    pylonKey("ferroduralum_sheet"),
                    List.of(FERRODURALUM_INGOT.getItemStack()),
                    sheet,
                    MiningLevel.IRON,
                    0.25f
            )
    );

    public static final PylonItemSchema FERRODURALUM_SWORD = new SimpleItemSchema<>(
            pylonKey("ferroduralum_sword"),
            key -> ItemStackBuilder.of(Material.GOLDEN_SWORD)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                                    pylonKey("ferroduralum_sword_damage"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
                    .set(DataComponentTypes.MAX_DAMAGE, 300)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            sword -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_sword"), sword);
                recipe.shape(
                        " F ",
                        " F ",
                        " S "
                );
                recipe.setIngredient('F', FERRODURALUM_INGOT.getItemStack());
                recipe.setIngredient('S', Material.STICK);
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_AXE = new SimpleItemSchema<>(
            pylonKey("ferroduralum_axe"),
            key -> ItemStackBuilder.of(Material.GOLDEN_AXE)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_axe_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
                    .set(DataComponentTypes.MAX_DAMAGE, 300)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            axe -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_axe"), axe);
                recipe.shape(
                        "FF ",
                        "FS ",
                        " S "
                );
                recipe.setIngredient('F', FERRODURALUM_INGOT.getItemStack());
                recipe.setIngredient('S', Material.STICK);
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_PICKAXE = new SimpleItemSchema<>(
            pylonKey("ferroduralum_pickaxe"),
            key -> ItemStackBuilder.of(Material.GOLDEN_PICKAXE)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_pickaxe_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
                    .set(DataComponentTypes.MAX_DAMAGE, 300)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            pick -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_pickaxe"), pick);
                recipe.shape(
                        "FFF",
                        " S ",
                        " S "
                );
                recipe.setIngredient('F', FERRODURALUM_INGOT.getItemStack());
                recipe.setIngredient('S', Material.STICK);
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_SHOVEL = new SimpleItemSchema<>(
            pylonKey("ferroduralum_shovel"),
            key -> ItemStackBuilder.of(Material.GOLDEN_SHOVEL)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_shovel_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
                    .set(DataComponentTypes.MAX_DAMAGE, 300)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            shovel -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_shovel"), shovel);
                recipe.shape(
                        " F ",
                        " S ",
                        " S "
                );
                recipe.setIngredient('F', FERRODURALUM_INGOT.getItemStack());
                recipe.setIngredient('S', Material.STICK);
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_HOE = new SimpleItemSchema<>(
            pylonKey("ferroduralum_hoe"),
            key -> ItemStackBuilder.of(Material.GOLDEN_HOE)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_hoe_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
                    .set(DataComponentTypes.MAX_DAMAGE, 300)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            hoe -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_hoe"), hoe);
                recipe.shape(
                        "FF ",
                        " S ",
                        " S "
                );
                recipe.setIngredient('F', FERRODURALUM_INGOT.getItemStack());
                recipe.setIngredient('S', Material.STICK);
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_HELMET = new SimpleItemSchema<>(
            pylonKey("ferroduralum_helmet"),
            key -> ItemStackBuilder.of(Material.GOLDEN_HELMET)
                    .defaultTranslatableName(key)
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
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            helmet -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_helmet"), helmet);
                recipe.shape(
                        "FFF",
                        "F F",
                        "   "
                );
                recipe.setIngredient('F', FERRODURALUM_INGOT.getItemStack());
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_CHESTPLATE = new SimpleItemSchema<>(
            pylonKey("ferroduralum_chestplate"),
            key -> ItemStackBuilder.of(Material.GOLDEN_CHESTPLATE)
                    .defaultTranslatableName(key)
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
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            chestplate -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_chestplate"), chestplate);
                recipe.shape(
                        "F F",
                        "FFF",
                        "FFF"
                );
                recipe.setIngredient('F', FERRODURALUM_INGOT.getItemStack());
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_LEGGINGS = new SimpleItemSchema<>(
            pylonKey("ferroduralum_leggings"),
            key -> ItemStackBuilder.of(Material.GOLDEN_LEGGINGS)
                    .defaultTranslatableName(key)
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
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            leggings -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_leggings"), leggings);
                recipe.shape(
                        "FFF",
                        "F F",
                        "F F"
                );
                recipe.setIngredient('F', FERRODURALUM_INGOT.getItemStack());
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_BOOTS = new SimpleItemSchema<>(
            pylonKey("ferroduralum_boots"),
            key -> ItemStackBuilder.of(Material.GOLDEN_BOOTS)
                    .defaultTranslatableName(key)
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
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            boots -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_boots"), boots);
                recipe.shape(
                        "F F",
                        "F F",
                        "   "
                );
                recipe.setIngredient('F', FERRODURALUM_INGOT.getItemStack());
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema COMPRESSED_WOOD = new SimpleItemSchema<>(
            pylonKey("compressed_wood"),
            key -> ItemStackBuilder.of(Material.OAK_WOOD)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            compressedWood -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("compressed_wood"), compressedWood);
                recipe.shape(
                        "WWW",
                        "WWW",
                        "WWW"
                );
                recipe.setIngredient('W',
                        new RecipeChoice.MaterialChoice(Tag.LOGS));
                recipe.setCategory(CraftingBookCategory.MISC);
                return recipe;
            }
    );

    public static final PylonItemSchema PORTABILITY_CATALYST = new SimpleItemSchema<>(
            pylonKey("portability_catalyst"),
            key -> ItemStackBuilder.of(Material.AMETHYST_SHARD)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            catalyst -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("portability_catalyst"), catalyst);
                recipe.shape(
                        "RRR",
                        "RPR",
                        "RRR"
                );
                recipe.setIngredient('R', Material.REDSTONE_BLOCK);
                recipe.setIngredient('P', Material.ENDER_PEARL);
                recipe.setCategory(CraftingBookCategory.MISC);
                return recipe;
            }
    );

    public static final PylonItemSchema COMPRESSED_OBSIDIAN = new SimpleItemSchema<>(
            pylonKey("compressed_obsidian"),
            key -> ItemStackBuilder.of(Material.OBSIDIAN)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            enchantedobi -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("compressed_obsidian"), enchantedobi);
                recipe.shape(
                        "OOO",
                        "OOO",
                        "OOO"
                );
                recipe.setIngredient('O', Material.OBSIDIAN);
                recipe.setCategory(CraftingBookCategory.MISC);
                return recipe;
            }
    );

    public static final PortableCraftingTable PORTABLE_CRAFTING_TABLE = new PortableCraftingTable(
            pylonKey("portable_crafting_table"),
            PortableCraftingTable.Item.class,
            key -> ItemStackBuilder.defaultBuilder(Material.CRAFTING_TABLE, key).build()
    );

    public static final PortableDustbin PORTABLE_DUSTBIN = new PortableDustbin(
            pylonKey("portable_dustbin"),
            PortableDustbin.Item.class,
            key -> ItemStackBuilder.defaultBuilder(Material.CAULDRON, key).build()
    );

    public static final PortableEnderChest PORTABLE_ENDER_CHEST = new PortableEnderChest(
            pylonKey("portable_ender_chest"),
            PortableEnderChest.Item.class,
            key -> ItemStackBuilder.defaultBuilder(Material.ENDER_CHEST, key).build()
    );

    public static final PylonItemSchema FIBER = new SimpleItemSchema<>(
            pylonKey("fiber"),
            key -> ItemStackBuilder.of(Material.BAMBOO_MOSAIC)
                    .defaultTranslatableName(key)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            fiber -> {
                    ShapedRecipe recipe = new ShapedRecipe(pylonKey("fiber"), fiber)
                    .shape("SSS", "   ", "   ").setIngredient('S', Material.STRING);
                    recipe.setCategory(CraftingBookCategory.MISC);
                    return recipe;
            }
    );

    public static final PylonItemSchema PLASTER = new SimpleItemSchema<>(
            pylonKey("plaster"),
            key -> ItemStackBuilder.of(Material.SMOOTH_STONE_SLAB)
                    .defaultTranslatableName(key)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            plaster -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("plaster"), plaster).shape("CC ", "CC ", "   ").setIngredient('C', Material.CLAY);
                recipe.setCategory(CraftingBookCategory.MISC);
                return recipe;
            }
    );

    public static final PylonItemSchema BANDAGE = new SimpleItemSchema<>(
            pylonKey("bandage"),
            key -> ItemStackBuilder.defaultBuilder(Material.COBWEB, key)
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                                    new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1, true)
                            ), 1))
                            .consumeSeconds(1.25f)
                            .animation(ItemUseAnimation.BOW)
                            .hasConsumeParticles(false)
                            .build())
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            bandage -> {
                    ShapedRecipe recipe = new ShapedRecipe(pylonKey("bandage"), bandage)
                    .shape("FF ", "FF ", "   ").setIngredient('F', FIBER.getItemStack());
                    recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                    return recipe;
            }
    );

    public static final PylonItemSchema SPLINT = new SimpleItemSchema<>(
            pylonKey("splint"),
            key -> ItemStackBuilder.defaultBuilder(Material.STICK, key)
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                                    new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 2, true)
                            ), 1))
                            .consumeSeconds(3.0f)
                            .animation(ItemUseAnimation.BOW)
                            .hasConsumeParticles(false)
                            .build())
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            splint -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("splint"), splint)
                        .shape("PPP", "   ", "PPP").setIngredient('P', PLASTER.getItemStack());
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema DISINFECTANT = new SimpleItemSchema<>(
            pylonKey("disinfectant"),
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
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            disinfectant -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("disinfectant"), disinfectant)
                        .shape("DDD", "D D", "DDD").setIngredient('D', Material.DRIPSTONE_BLOCK);
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final PylonItemSchema MEDKIT = new SimpleItemSchema<>(
            pylonKey("medkit"),
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
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            medkit -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("medkit"), medkit)
                        .shape("PFP", "DDD", "PFP").setIngredient('P', PLASTER.getItemStack())
                        .setIngredient('D', DISINFECTANT.getItemStack())
                        .setIngredient('F', FIBER.getItemStack());
                recipe.setCategory(CraftingBookCategory.EQUIPMENT);
                return recipe;
            }
    );

    public static final Sprinkler.SprinklerItem.Schema SPRINKLER = new Sprinkler.SprinklerItem.Schema(
            pylonKey("sprinkler"),
            Sprinkler.SprinklerItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.FLOWER_POT, key).build()
    );

    public static final RecoilArrow RECOIL_ARROW = new RecoilArrow(
            pylonKey("recoil_arrow"),
            RecoilArrow.Item.class,
            key -> ItemStackBuilder.defaultBuilder(Material.ARROW, key)
                    .amount(8)
                    .build(),
            0.75f
    );

    public static final Pedestal.PedestalItem.Schema PEDESTAL = new Pedestal.PedestalItem.Schema(
            pylonKey("pedestal"),
            key -> ItemStackBuilder.defaultBuilder(Material.STONE_BRICK_WALL, key).build(),
            PylonBlocks.PEDESTAL
    );

    public static final Pedestal.PedestalItem.Schema MAGIC_PEDESTAL = new Pedestal.PedestalItem.Schema(
            pylonKey("magic_pedestal"),
            key -> ItemStackBuilder.defaultBuilder(Material.MOSSY_STONE_BRICK_WALL, key).build(),
            PylonBlocks.MAGIC_PEDESTAL
    );

    public static final MagicAltar.MagicAltarItem.Schema MAGIC_ALTAR = new MagicAltar.MagicAltarItem.Schema(
            pylonKey("magic_altar"),
            key -> ItemStackBuilder.defaultBuilder(Material.SMOOTH_STONE_SLAB, key).build()
    );

    public static final PylonItemSchema GRINDSTONE = new PylonItemSchema(
            pylonKey("grindstone"),
            Grindstone.GrindstoneItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.SMOOTH_STONE_SLAB, key).build()
    );

    public static final PylonItemSchema GRINDSTONE_HANDLE = new PylonItemSchema(
            pylonKey("grindstone_handle"),
            GrindstoneHandle.GrindstoneHandleItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.OAK_FENCE, key).build()
    );

    // TODO recipe refactor will clean this up
    private static final BlockData wheatGrindingBlockData = Material.WHEAT.createBlockData(data -> {
        Ageable ageable = (Ageable) data;
        ageable.setAge(ageable.getMaximumAge());
    });
    public static final PylonItemSchema FLOUR = new SimpleItemSchema<>(
            pylonKey("flour"),
            key -> ItemStackBuilder.of(Material.SUGAR)
                    .defaultTranslatableName(key)
                    .build(),
            Grindstone.Recipe.RECIPE_TYPE,
            item -> new Grindstone.Recipe(
                    pylonKey("flour"),
                    new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)),
                    2,
                    item,
                    2,
                    wheatGrindingBlockData
            )
    );

    public static final PylonItemSchema DOUGH = new SimpleItemSchema<>(
            pylonKey("dough"),
            key -> ItemStackBuilder.of(Material.YELLOW_DYE)
                    .defaultTranslatableName(key)
                    .build(),
            WaterCauldronRightClickRecipe.RECIPE_TYPE,
            item -> new WaterCauldronRightClickRecipe(
                    pylonKey("dough"),
                    new RecipeChoice.ExactChoice(FLOUR.getItemStack()),
                    item
            )
    );

    public static final PylonItemSchema MIXING_POT = new PylonItemSchema(
            pylonKey("mixing_pot"),
            MixingPot.MixingPotItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.CAULDRON, key).build()
    );

    public static final PylonItemSchema ENRICHED_NETHERRACK = new PylonItemSchema(
            pylonKey("enriched_netherrack"),
            EnrichedNetherrack.EnrichedNetherrackItem.class,
            key -> ItemStackBuilder.defaultBuilder(Material.NETHERRACK, key).build()
    );

    public static final PylonItemSchema SHIMMER_DUST_1 = new SimpleItemSchema<>(
            pylonKey("shimmer_dust_1"),
            key -> ItemStackBuilder.of(Material.SUGAR)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            item -> new ShapelessRecipe(pylonKey("shimmer_dust_1"), item)
                    .addIngredient(COPPER_DUST.getItemStack())
                    .addIngredient(Material.FLINT)
                    .addIngredient(Material.CLAY_BALL)
    );

    public static final PylonItemSchema SHIMMER_DUST_2 = new SimpleItemSchema<>(
            pylonKey("shimmer_dust_2"),
            key -> ItemStackBuilder.of(Material.SUGAR)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build(),
            MixingPot.Recipe.RECIPE_TYPE,
            item -> new MixingPot.Recipe(
                    pylonKey("shimmer_dust_2"),
                    Map.of(
                            new RecipeChoice.ExactChoice(SHIMMER_DUST_1.getItemStack()), 1,
                            new RecipeChoice.ExactChoice(GOLD_DUST.getItemStack()), 1,
                            new RecipeChoice.ExactChoice(new ItemStack(Material.REDSTONE)), 1
                    ),
                    item,
                    false,
                    1
            )
    );

    public static final PylonItemSchema COVALENT_BINDER = new SimpleItemSchema<>(
            pylonKey("covalent_binder"),
            key -> ItemStackBuilder.of(Material.LIGHT_BLUE_DYE)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build(),
            MixingPot.Recipe.RECIPE_TYPE,
            item -> {
                ItemStack output = item.clone();
                output.setAmount(6);
                return new MixingPot.Recipe(
                        pylonKey("covalent_binder"),
                        Map.of(
                                new RecipeChoice.ExactChoice(new ItemStack(Material.GUNPOWDER)), 4,
                                new RecipeChoice.ExactChoice(new ItemStack(Material.EMERALD)), 1,
                                new RecipeChoice.ExactChoice(SHIMMER_DUST_1.getItemStack()), 1
                        ),
                        output,
                        true,
                        3
                );
            }
    );

    public static final PylonItemSchema SHIMMER_DUST_3 = new SimpleItemSchema<>(
            pylonKey("shimmer_dust_3"),
            key -> ItemStackBuilder.of(Material.SUGAR)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build(),
            MagicAltar.Recipe.RECIPE_TYPE,
            item -> new MagicAltar.Recipe(
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
                    item,
                    5
            )
    );

    public static final PylonItemSchema SHIMMER_SKULL = new SimpleItemSchema<>(
            pylonKey("shimmer_skull"),
            key -> ItemStackBuilder.of(Material.WITHER_SKELETON_SKULL)
                    .defaultTranslatableName(key)
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build(),
            MagicAltar.Recipe.RECIPE_TYPE,
            item -> new MagicAltar.Recipe(
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
                    item,
                    30
            )
    );

    //</editor-fold>

    static void register() {
        COPPER_DUST.register();
        GOLD_DUST.register();
        COPPER_SHEET.register();
        GOLD_SHEET.register();
        IRON_SHEET.register();
        STONE_HAMMER.register();
        IRON_HAMMER.register();
        DIAMOND_HAMMER.register();
        MONSTER_JERKY.register();
        WATERING_CAN.register();
        SPRINKLER.register();
        COMPRESSED_WOOD.register();
        PORTABLE_CRAFTING_TABLE.register();
        PORTABLE_DUSTBIN.register();
        PORTABLE_ENDER_CHEST.register();
        FIBER.register();
        BANDAGE.register();
        PLASTER.register();
        SPLINT.register();
        DISINFECTANT.register();
        MEDKIT.register();
        RAW_FERRODURALUM.register();
        FERRODURALUM_INGOT.register();
        FERRODURALUM_SHEET.register();
        FERRODURALUM_SWORD.register();
        FERRODURALUM_AXE.register();
        FERRODURALUM_PICKAXE.register();
        FERRODURALUM_SHOVEL.register();
        FERRODURALUM_HOE.register();
        FERRODURALUM_HELMET.register();
        FERRODURALUM_CHESTPLATE.register();
        FERRODURALUM_LEGGINGS.register();
        FERRODURALUM_BOOTS.register();
        RECOIL_ARROW.register();
        PEDESTAL.register();
        MAGIC_PEDESTAL.register();
        MAGIC_ALTAR.register();
        GRINDSTONE.register();
        GRINDSTONE_HANDLE.register();
        FLOUR.register();
        DOUGH.register();
        ENRICHED_NETHERRACK.register();
        MIXING_POT.register();
        SHIMMER_DUST_1.register();
        SHIMMER_DUST_2.register();
        COVALENT_BINDER.register();
        SHIMMER_DUST_3.register();
        SHIMMER_SKULL.register();

        // TODO recipe refactor
        RecipeTypes.VANILLA_CRAFTING.addRecipe(
                new ShapedRecipe(pylonKey("enriched_netherrack"), ENRICHED_NETHERRACK.getItemStack())
                        .shape(
                                " s ",
                                "sns",
                                " s "
                        )
                        .setIngredient('n', new ItemStack(Material.NETHERRACK))
                        .setIngredient('s', SHIMMER_DUST_2.getItemStack())
        );

        // TODO recipe refactor
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(
                new Grindstone.Recipe(
                        pylonKey("copper_dust_from_copper_ingot"),
                        new RecipeChoice.ExactChoice(new ItemStack(Material.COPPER_INGOT)),
                        1,
                        COPPER_DUST.getItemStack(),
                        2,
                        Material.COPPER_BLOCK.createBlockData()
                )
        );

        // TODO recipe refactor
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(
                new Grindstone.Recipe(
                        pylonKey("copper_dust_from_raw_copper"),
                        new RecipeChoice.ExactChoice(new ItemStack(Material.RAW_COPPER)),
                        1,
                        COPPER_DUST.getItemStack(),
                        2,
                        Material.COPPER_BLOCK.createBlockData()
                )
        );

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(
                new Grindstone.Recipe(
                        pylonKey("gold_dust_from_gold_ingot"),
                        new RecipeChoice.ExactChoice(new ItemStack(Material.GOLD_INGOT)),
                        1,
                        GOLD_DUST.getItemStack(),
                        2,
                        Material.GOLD_BLOCK.createBlockData()
                )
        );

        Grindstone.Recipe.RECIPE_TYPE.addRecipe(
                new Grindstone.Recipe(
                        pylonKey("gold_dust_from_raw_gold"),
                        new RecipeChoice.ExactChoice(new ItemStack(Material.RAW_GOLD)),
                        1,
                        GOLD_DUST.getItemStack(),
                        2,
                        Material.GOLD_BLOCK.createBlockData()
                )
        );

        // TODO recipe refactor
        RecipeTypes.VANILLA_CRAFTING.addRecipe(
                new ShapedRecipe(pylonKey("magic_pedestal"), MAGIC_PEDESTAL.getItemStack())
                        .shape(
                                "c c",
                                " p ",
                                "c c"
                        )
                        .setIngredient('p', PEDESTAL.getItemStack())
                        .setIngredient('c', COVALENT_BINDER.getItemStack())
        );

        // TODO recipe refactor
        RecipeTypes.VANILLA_CRAFTING.addRecipe(
                new ShapedRecipe(pylonKey("magic_altar"), MAGIC_ALTAR.getItemStack())
                        .shape(
                                "   ",
                                "dpd",
                                "dsd"
                        )
                        .setIngredient('p', PEDESTAL.getItemStack())
                        .setIngredient('s', new ItemStack(Material.SMOOTH_STONE_SLAB))
                        .setIngredient('d', SHIMMER_DUST_2.getItemStack())
        );

        // TODO recipe refactor
        FurnaceRecipe furnaceDoughRecipe = new FurnaceRecipe(
                pylonKey("bread_from_dough_furnace"),
                new ItemStack(Material.BREAD),
                new RecipeChoice.ExactChoice(DOUGH.getItemStack()),
                0.2F,
                10 * 20);
        furnaceDoughRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_FURNACE.addRecipe(furnaceDoughRecipe);

        // TODO recipe refactor
        SmokingRecipe smokerDoughRecipe = new SmokingRecipe(
                pylonKey("bread_from_dough_smoker"),
                new ItemStack(Material.BREAD),
                new RecipeChoice.ExactChoice(DOUGH.getItemStack()),
                0.2F,
                5 * 20);
        smokerDoughRecipe.setCategory(CookingBookCategory.FOOD);
        RecipeTypes.VANILLA_SMOKING.addRecipe(smokerDoughRecipe);
    }

    private static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }
}
