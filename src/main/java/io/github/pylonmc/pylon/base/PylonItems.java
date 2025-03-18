package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.Hammer;
import io.github.pylonmc.pylon.base.items.MonsterJerky;
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
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.recipe.CookingBookCategory;
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

    public static final PylonItemSchema FERRODURALUM_ORE = new SimpleItemSchema<>(
            pylonKey("ferroduralum_ore"),
            new ItemStackBuilder(Material.GOLD_ORE)
                    .name("Ferroduralum ore")
                    .lore("A crafting material to make",
                            "armor, weapons and tools.")
                    .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
                    .build(),
            RecipeTypes.VANILLA_CRAFTING,
            ferroduralum -> {
                ShapedRecipe recipe = new ShapedRecipe(pylonKey("ferroduralum_ore"), ferroduralum);
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
                    .lore("A crafting material to make",
                            "armor, weapons and tools.")
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
                    .lore("A more powerful iron sword.")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                                    pylonKey("ferroduralum_sword_damage"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
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
                    .lore("A more powerful iron axe")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_axe_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
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
                    .lore("A more powerful iron pickaxe")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_pickaxe_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
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
                    .lore("A more powerful iron shovel.")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_shovel_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
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
                    .lore("A more powerful iron hoe.")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.BLOCK_BREAK_SPEED, new AttributeModifier(
                                    pylonKey("ferroduralum_hoe_speed"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
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
                    .lore("A more powerful iron helmet.")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ARMOR, new AttributeModifier(
                                    pylonKey("ferroduralum_helmet_armor"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
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
                    .lore("A more powerful iron chestplate.")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ARMOR, new AttributeModifier(
                                    pylonKey("ferroduralum_chestplate_armor"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
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
                    .lore("More powerful iron leggings.")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ARMOR, new AttributeModifier(
                                    pylonKey("ferroduralum_leggings_armor"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
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
                    .lore("More powerful iron boots.")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ARMOR, new AttributeModifier(
                                    pylonKey("ferroduralum_boots_armor"),
                                    0.15,
                                    AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            ))
                            .build())
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
    }

    private static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }
}
