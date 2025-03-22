package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.Hammer;
import io.github.pylonmc.pylon.base.items.MonsterJerky;
import io.github.pylonmc.pylon.base.items.PortableDustbin;
import io.github.pylonmc.pylon.base.items.PortableEnderChest;
import io.github.pylonmc.pylon.base.items.PortableCraftingTable;
import io.github.pylonmc.pylon.base.items.WateringCan;
import io.github.pylonmc.pylon.core.item.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimpleItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.recipe.RecipeTypes;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
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
            Hammer.class,
            new ItemStackBuilder(Material.STONE_PICKAXE)
                    .name("Stone Hammer")
                    .lore(
                            "Throw an item on top of a <yellow>stone block",
                            "and <yellow>right click</yellow> to use the hammer on it.",
                            "Higher tier hammers are more likely to succeed!"
                    )
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                                    pylonKey("hammer_attack_speed"),
                                    (1.0 / 3) - 4,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                            .addModifier(Attribute.ATTACK_KNOCKBACK, new AttributeModifier(
                                    pylonKey("hammer_attack_knockback"),
                                    1,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                            .addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                                    pylonKey("hammer_attack_damage"),
                                    1,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                    )
                    .build(),
            MiningLevel.STONE,
            Material.STONE,
            new RecipeChoice.MaterialChoice(Tag.ITEMS_STONE_TOOL_MATERIALS)
    );

    public static final Hammer.Schema IRON_HAMMER = new Hammer.Schema(
            pylonKey("iron_hammer"),
            Hammer.class,
            new ItemStackBuilder(Material.IRON_PICKAXE)
                    .name("Iron Hammer")
                    .lore(
                            "Throw an item on top of an <yellow>iron block",
                            "and <yellow>right click</yellow> to use the hammer on it.",
                            "Higher tier hammers are more likely to succeed!"
                    )
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                                    pylonKey("hammer_attack_speed"),
                                    (1.0 / 2) - 4,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                            .addModifier(Attribute.ATTACK_KNOCKBACK, new AttributeModifier(
                                    pylonKey("hammer_attack_knockback"),
                                    1.5,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                            .addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                                    pylonKey("hammer_attack_damage"),
                                    3,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                    )
                    .build(),
            MiningLevel.IRON,
            Material.IRON_BLOCK,
            new RecipeChoice.MaterialChoice(Material.IRON_INGOT)
    );

    public static final Hammer.Schema DIAMOND_HAMMER = new Hammer.Schema(
            pylonKey("diamond_hammer"),
            Hammer.class,
            new ItemStackBuilder(Material.DIAMOND_PICKAXE)
                    .name("Diamond Hammer")
                    .lore(
                            "Throw an item on top of a <yellow>diamond block",
                            "and <yellow>right click</yellow> to use the hammer on it.",
                            "Higher tier hammers are more likely to succeed!"
                    )
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                                    pylonKey("hammer_attack_speed"),
                                    1 - 4,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                            .addModifier(Attribute.ATTACK_KNOCKBACK, new AttributeModifier(
                                    pylonKey("hammer_attack_knockback"),
                                    2,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                            .addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                                    pylonKey("hammer_attack_damage"),
                                    5,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                    )
                    .build(),
            MiningLevel.DIAMOND,
            Material.DIAMOND_BLOCK,
            new RecipeChoice.MaterialChoice(Material.DIAMOND)
    );

    public static final PylonItemSchema WATERING_CAN = new PylonItemSchema(
            pylonKey("watering_can"),
            WateringCan.class,
            new ItemStackBuilder(Material.BUCKET)
                    .name("Watering Can")
                    .lore(
                            "<yellow>Right click</yellow> to speed up growth of crops, saplings,",
                            "and sugar cane.",
                            "Slow, but efficient at watering lots of crops at once.",
                            "Range: <aqua>" + WateringCan.RANGE + " blocks"
                    )
                    .build()
    );

    public static final MonsterJerky MONSTER_JERKY = new MonsterJerky(
            pylonKey("monster_jerky"),
            SimplePylonItem.class,
            new ItemStackBuilder(Material.ROTTEN_FLESH)
                    .name("Monster Jerky")
                    .lore("A slightly tastier and tougher version of rotten flesh.")
                    .set(DataComponentTypes.FOOD,  FoodProperties.food()
                            .canAlwaysEat(MonsterJerky.DEFAULT_CAN_ALWAYS_EAT)
                            .nutrition(MonsterJerky.DEFAULT_NUTRITION)
                            .saturation(MonsterJerky.DEFAULT_SATURATION)
                            .build())
                    .set(DataComponentTypes.CONSUMABLE, Consumable.consumable().build())
                    .build()
    );

    public static final PylonItemSchema COMPRESSED_WOOD = new SimpleItemSchema<>(
            pylonKey("compressed_wood"),
            new ItemStackBuilder(Material.OAK_WOOD)
                    .name("Compressed wood")
                    .lore("Crafting material. ")
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
                    .lore("Crafting material.")
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
                    .lore("A crafting material.")
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
                    .lore("<yellow>Right-Click</yellow> to open a",
                            "crafting table interface.")
                    .build()
    );

    public static final PortableDustbin PORTABLE_DUSTBIN = new PortableDustbin(
            pylonKey("portable_dustbin"),
            PortableDustbin.Item.class,
            new ItemStackBuilder(Material.CAULDRON)
                    .name("Portable Dustbin")
                    .lore("Deletes unneeded items.",
                            "<yellow>Right-Click</yellow> to use.")
                    .build()
    );

    public static final PortableEnderChest PORTABLE_ENDER_CHEST = new PortableEnderChest(
            pylonKey("portable_ender_chest"),
            PortableEnderChest.Item.class,
            new ItemStackBuilder(Material.ENDER_CHEST)
                    .name("Portable Enderchest")
                    .lore("<yellow>Right-Click</yellow> to open",
                            "your enderchest.")
                    .build()
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
        COMPRESSED_WOOD.register();
        PORTABLE_CRAFTING_TABLE.register();
        PORTABLE_DUSTBIN.register();
        PORTABLE_ENDER_CHEST.register();
    }

    private static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }
}
