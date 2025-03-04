package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.Hammer;
import io.github.pylonmc.pylon.base.items.MonsterJerky;
import io.github.pylonmc.pylon.core.item.BasicItemSchema;
import io.github.pylonmc.pylon.core.item.ItemStackBuilder;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.util.MiningLevel;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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
            MonsterJerky.Item.class,
            new ItemStackBuilder(Material.COOKED_BEEF)
                    .name("Monster Jerky")
                    .lore("A slightly tastier and tougher version of rotten flesh.")
                    .set(DataComponentTypes.ITEM_MODEL, new NamespacedKey("minecraft", "rotten_flesh"))
                    .set(DataComponentTypes.FOOD,  FoodProperties.food()
                            .canAlwaysEat(MonsterJerky.DEFAULT_CAN_ALWAYS_EAT)
                            .nutrition(MonsterJerky.DEFAULT_NUTRITION)
                            .saturation(MonsterJerky.DEFAULT_SATURATION)
                            .build())
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
    }

    private static @NotNull NamespacedKey pylonKey(@NotNull String key) {
        return new NamespacedKey(PylonBase.getInstance(), key);
    }
}
