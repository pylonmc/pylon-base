package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.*;
import io.github.pylonmc.pylon.base.items.watering.Sprinkler;
import io.github.pylonmc.pylon.base.items.watering.WateringCan;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimpleItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.builder.LoreBuilder;
import io.github.pylonmc.pylon.core.item.builder.Quantity;
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
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@SuppressWarnings("UnstableApiUsage")
public final class PylonItems {

    private PylonItems() {
        throw new AssertionError("Utility class");
    }

    public static final PylonItemSchema COPPER_SHEET = new SimpleItemSchema<>(
            pylonKey("copper_sheet"),
            new ItemStackBuilder(Material.PAPER).name("Copper Sheet").build(),
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
            new ItemStackBuilder(Material.PAPER).name("Gold Sheet").build(),
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
            new ItemStackBuilder(Material.PAPER).name("Iron Sheet").build(),
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
            pylonKey("stone_hammer"),
            "Stone Hammer",
            Material.COBBLESTONE,
            Material.STONE,
            Material.STONE_PICKAXE,
            MiningLevel.STONE,
            (1.0 / 3) - 4,
            1,
            1
    );

    public static final Hammer.Schema IRON_HAMMER = new Hammer.Schema(
            pylonKey("iron_hammer"),
            "Iron Hammer",
            Material.IRON_INGOT,
            Material.IRON_BLOCK,
            Material.IRON_PICKAXE,
            MiningLevel.IRON,
            (1.0 / 2) - 4,
            1.5,
            3
    );

    public static final Hammer.Schema DIAMOND_HAMMER = new Hammer.Schema(
            pylonKey("diamond_hammer"),
            "Diamond Hammer",
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
            new ItemStackBuilder(Material.BUCKET)
                    .name("Watering Can")
                    .lore(new LoreBuilder()
                            .instructionLine("Right click", "to use")
                            .arrow().text(" Speeds up growth of crops, saplings, sugar cane, and cactus").newline()
                            .arrow().text(" Slow, but can water lots of crops at once").newline()
                            .attributeLine("Range", 4, Quantity.BLOCKS) // TODO allow using setting in lore (fix when translation)
                    )
                    .build()
    );

    public static final MonsterJerky MONSTER_JERKY = new MonsterJerky(
            pylonKey("monster_jerky"),
            SimplePylonItem.class,
            new ItemStackBuilder(Material.ROTTEN_FLESH)
                    .name("Monster Jerky")
                    .lore(new LoreBuilder().arrow().text(" Slightly tastier and tougher than rotten flesh"))
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable().build())
                    .build()
    );

    public static final PylonItemSchema FERRODURALUM_ORE = new SimpleItemSchema<>(
            pylonKey("raw_ferroduralum"),
            new ItemStackBuilder(Material.GOLD_ORE)
                    .name("Raw Ferroduralum")
                    .lore("A primitive alloy")
                    .build(),
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
            new ItemStackBuilder(Material.GOLD_INGOT)
                    .name("Ferroduralum ingot")
                    .lore("A primitive alloy")
                    .build(),
            RecipeTypes.VANILLA_FURNACE,
            ingot -> {
                FurnaceRecipe recipe = new FurnaceRecipe(pylonKey("ferroduralum_ingot"), ingot, new RecipeChoice.ExactChoice(FERRODURALUM_ORE.getItemStack()), 0.25f, 10 * 20);
                recipe.setCategory(CookingBookCategory.MISC);
                return recipe;
            }
    );

    public static final PylonItemSchema FERRODURALUM_SWORD = new SimpleItemSchema<>(
            pylonKey("ferroduralum_sword"),
            new ItemStackBuilder(Material.GOLDEN_SWORD)
                    .name("Ferroduralum sword")
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
            new ItemStackBuilder(Material.GOLDEN_AXE)
                    .name("Ferroduralum axe")
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
            new ItemStackBuilder(Material.GOLDEN_PICKAXE)
                    .name("Ferroduralum pickaxe")
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
            new ItemStackBuilder(Material.GOLDEN_SHOVEL)
                    .name("Ferroduralum shovel")
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
            new ItemStackBuilder(Material.GOLDEN_HOE)
                    .name("Ferroduralum hoe")
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
            new ItemStackBuilder(Material.GOLDEN_HELMET)
                    .name("Ferroduralum helmet")
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
            new ItemStackBuilder(Material.GOLDEN_CHESTPLATE)
                    .name("Ferroduralum chestplate")
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
            new ItemStackBuilder(Material.GOLDEN_LEGGINGS)
                    .name("Ferroduralum leggings")
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
            new ItemStackBuilder(Material.GOLDEN_BOOTS)
                    .name("Ferroduralum boots")
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
            new ItemStackBuilder(Material.OAK_WOOD)
                    .name("Compressed wood")
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
            new ItemStackBuilder(Material.AMETHYST_SHARD)
                    .name("Portability Catalyst")
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
            new ItemStackBuilder(Material.OBSIDIAN)
                    .name("Compressed Obsidian")
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
            new ItemStackBuilder(Material.CRAFTING_TABLE)
                    .name("Portable crafting table")
                    .lore(new LoreBuilder().instructionLine("Right click", "to open"))
                    .build()
    );

    public static final PortableDustbin PORTABLE_DUSTBIN = new PortableDustbin(
            pylonKey("portable_dustbin"),
            PortableDustbin.Item.class,
            new ItemStackBuilder(Material.CAULDRON)
                    .name("Portable Dustbin")
                    .lore(new LoreBuilder().instructionLine("Right click", "to open"))
                    .build()
    );

    public static final PortableEnderChest PORTABLE_ENDER_CHEST = new PortableEnderChest(
            pylonKey("portable_ender_chest"),
            PortableEnderChest.Item.class,
            new ItemStackBuilder(Material.ENDER_CHEST)
                    .name("Portable Enderchest")
                    .lore(new LoreBuilder().instructionLine("Right click", "to open"))
                    .build()
    );

    public static final PylonItemSchema FIBER = new SimpleItemSchema<>(
            pylonKey("fiber"),
            new ItemStackBuilder(Material.BAMBOO_MOSAIC)
                    .name("Fiber")
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
            new ItemStackBuilder(Material.SMOOTH_STONE_SLAB)
                    .name("Plaster")
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
            new ItemStackBuilder(Material.COBWEB)
                    .name("Bandage")
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                                    new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1, true)
                            ), 1))
                            .consumeSeconds(1.25f)
                            .animation(ItemUseAnimation.BOW)
                            .hasConsumeParticles(false)
                            .build())
                    .lore(new LoreBuilder()
                            .instructionLine("Right click", "to use")
                            .attributeLine("Heals", 2, Quantity.HEARTS)
                            .attributeLine("Apply time", 1.25, 2, Quantity.SECONDS))
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
            new ItemStackBuilder(Material.STICK)
                    .name("Splint")
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                                    new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 2, true)
                            ), 1))
                            .consumeSeconds(3.0f)
                            .animation(ItemUseAnimation.BOW)
                            .hasConsumeParticles(false)
                            .build())
                    .lore(new LoreBuilder()
                            .instructionLine("Right click", "to use")
                            .attributeLine("Heals", 7, Quantity.HEARTS)
                            .attributeLine("Apply time", 3, Quantity.SECONDS))
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
            new ItemStackBuilder(Material.BREWER_POTTERY_SHERD)
                    .name("<white>Disinfectant")
                    // Using the actual potion material doesn't let you set the name properly, gives you a
                    // class string of a nonexistant potion type for some reason
                    .set(DataComponentTypes.ITEM_MODEL, Material.POTION.getKey())
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .hasConsumeParticles(false)
                            .consumeSeconds(3.0f)
                            .animation(ItemUseAnimation.BOW)
                            .addEffect(ConsumeEffect.clearAllStatusEffects())
                            .build())
                    .lore(new LoreBuilder()
                            .instructionLine("Right click", "to use")
                            .arrow().text(" Clears all effects").newline()
                            .attributeLine("Apply time", 3, Quantity.SECONDS))
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
            new ItemStackBuilder(Material.SHULKER_SHELL)
                    .name("Medkit")
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                            .consumeSeconds(7.0f)
                            .animation(ItemUseAnimation.BOW)
                            .hasConsumeParticles(false)
                            .addEffect(ConsumeEffect.clearAllStatusEffects())
                            .addEffect(ConsumeEffect.applyStatusEffects(List.of(
                                    new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 3, true)
                            ), 1))
                    )
                    .lore(new LoreBuilder()
                                    .instructionLine("Right click", "to use")
                                    .arrow().text(" Clears all effects").newline()
                                    .attributeLine("Heals", 10, Quantity.HEARTS))
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
            new ItemStackBuilder(Material.FLOWER_POT)
                    .name("Sprinkler")
                    .lore(new LoreBuilder()
                            .arrow().text(" Makes crops, saplings, sugar cane, and cactus go brrr").newline()
                            .attributeLine("Range", Sprinkler.HORIZONTAL_RANGE, Quantity.BLOCKS))
                    .build()
    );

    public static final RecoilArrow RECOIL_ARROW = new RecoilArrow(
            pylonKey("recoil_arrow"),
            RecoilArrow.Item.class,
            new ItemStackBuilder(Material.ARROW)
                    .name("Recoil arrow")
                    .lore(new LoreBuilder()
                            .arrow().text(" Sends you backwards at the velocity of your arrow.").newline()
                            .attributeLine("Efficiency", 75, Quantity.PERCENT))
                    .amount(8)
                    .build(),
            0.75f
    );
    //</editor-fold>

    static void register() {
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
        FERRODURALUM_ORE.register();
        FERRODURALUM_INGOT.register();
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
    }

    private static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }
}
