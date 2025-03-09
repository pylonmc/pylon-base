package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.Bandage;
import io.github.pylonmc.pylon.base.items.Fiber;
import io.github.pylonmc.pylon.base.items.Hammer;
import io.github.pylonmc.pylon.base.items.MonsterJerky;
import io.github.pylonmc.pylon.base.items.*;
import io.github.pylonmc.pylon.base.items.basichealing.*;
import io.github.pylonmc.pylon.core.item.BasicItemSchema;
import io.github.pylonmc.pylon.core.item.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.SimplePylonItem;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.BlockItemDataProperties;
import io.papermc.paper.datacomponent.item.Consumable;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class PylonItems {

    private PylonItems() {
        throw new AssertionError("Utility class");
    }

    public static final PylonItemSchema COPPER_SHEET = new BasicItemSchema<>(
            pylonKey("copper_sheet"),
            new ItemStackBuilder(Material.PAPER).name("Copper Sheet").build(),
            Hammer.RECIPE_TYPE,
            sheet -> new Hammer.Recipe(
                    pylonKey("copper_sheet"),
                    List.of(new ItemStack(Material.COPPER_INGOT)),
                    sheet,
                    MiningLevel.STONE,
                    0.25f
            )
    );

    public static final PylonItemSchema GOLD_SHEET = new BasicItemSchema<>(
            pylonKey("gold_sheet"),
            new ItemStackBuilder(Material.PAPER).name("Gold Sheet").build(),
            Hammer.RECIPE_TYPE,
            sheet -> new Hammer.Recipe(
                    pylonKey("gold_sheet"),
                    List.of(new ItemStack(Material.GOLD_INGOT)),
                    sheet,
                    MiningLevel.STONE,
                    0.25f
            )
    );

    public static final PylonItemSchema IRON_SHEET = new BasicItemSchema<>(
            pylonKey("iron_sheet"),
            new ItemStackBuilder(Material.PAPER).name("Iron Sheet").build(),
            Hammer.RECIPE_TYPE,
            sheet -> new Hammer.Recipe(
                    pylonKey("iron_sheet"),
                    List.of(new ItemStack(Material.IRON_INGOT)),
                    sheet,
                    MiningLevel.IRON,
                    0.25f
            )
    );

    //<editor-fold desc="Hammers" defaultstate=collapsed>
    public static final Hammer STONE_HAMMER = new Hammer(
            pylonKey("stone_hammer"),
            Hammer.Item.class,
            new ItemStackBuilder(Material.STONE_PICKAXE)
                    .name("Stone Hammer")
                    .lore(
                            "A hammer made of stone",
                            "Useful as a weapon in a pinch"
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
            new RecipeChoice.MaterialChoice(Tag.ITEMS_STONE_TOOL_MATERIALS)
    );

    public static final Hammer IRON_HAMMER = new Hammer(
            pylonKey("iron_hammer"),
            Hammer.Item.class,
            new ItemStackBuilder(Material.IRON_PICKAXE)
                    .name("Iron Hammer")
                    .lore(
                            "A hammer made of iron",
                            "Stronger than a stone hammer"
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
            new RecipeChoice.MaterialChoice(Material.IRON_INGOT)
    );

    public static final Hammer DIAMOND_HAMMER = new Hammer(
            pylonKey("diamond_hammer"),
            Hammer.Item.class,
            new ItemStackBuilder(Material.DIAMOND_PICKAXE)
                    .name("Diamond Hammer")
                    .lore(
                            "A hammer made of diamond",
                            "Only the richest can afford this"
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
            new RecipeChoice.MaterialChoice(Material.DIAMOND)
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

    public static final Fiber FIBER = new Fiber(
            pylonKey("fiber"),
            SimplePylonItem.class,
            new ItemStackBuilder(Material.BAMBOO_MOSAIC)
                    .name("Fiber")
                    .lore("More durable string for longer-lasting items.")
                    .build()
    );

    public static final Bandage BANDAGE = new Bandage(
            pylonKey("bandage"),
            Bandage.Item.class,
            new ItemStackBuilder(Material.BOW)
                    .name("Bandage")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                                    pylonKey("bandage_attack_speed"),
                                    Bandage.ATTACK_SPEED,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                    )
                    .set(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments()
                            .add(Enchantment.INFINITY, 2)
                    )
                    .lore("Right-Click to heal for " + Bandage.BANDAGE_HEAL_AMOUNT + " hearts")
                    .build()
    );

    public static final Plaster PLASTER = new Plaster(
            pylonKey("plaster"),
            SimplePylonItem.class,
            new ItemStackBuilder(Material.SMOOTH_STONE_SLAB)
                    .name("Plaster")
                    .lore("Condensed form of clay.")
                    .build()
    );

    public static final Splint SPLINT = new Splint(
            pylonKey("splint"),
            Splint.Item.class,
            new ItemStackBuilder(Material.BOW)
                    .name("Splint")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                                    pylonKey("bandage_attack_speed"),
                                    Splint.ATTACK_SPEED,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                    )
                    .set(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments()
                            .add(Enchantment.INFINITY, 2)
                    )
                    .lore("Early-game healing item used to recover from minor to medium injuries.")
                    .build()
    );

    public static final Disinfectant DISINFECTANT = new Disinfectant(
            pylonKey("disinfectant"),
            SimplePylonItem.class,
            new ItemStackBuilder(Material.BREWER_POTTERY_SHERD)
                    .name("Disinfectant")
                    .lore("An item that can either be used on its own to disinfect a wound,",
                            "or combined with other items to create a more powerful med kit. ")
                    .build()
    );

    public static final Medkit MEDKIT = new Medkit(
            pylonKey("medkit"),
            Medkit.Item.class,
            new ItemStackBuilder(Material.BOW)
                    .name("Medkit")
                    .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                            .addModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                                    pylonKey("bandage_attack_speed"),
                                    Medkit.ATTACK_SPEED,
                                    AttributeModifier.Operation.ADD_NUMBER
                            ))
                    )
                    .set(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments()
                            .add(Enchantment.INFINITY, 2)
                    )
                    .lore("An effective healing tool that can be used to treat almost any wound imaginable.",
                    "Never leave home under equipped!")
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
        FIBER.register();
        BANDAGE.register();
        PLASTER.register();
        SPLINT.register();
        DISINFECTANT.register();
        MEDKIT.register();
    }

    private static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }
}