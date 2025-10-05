package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.armor.BronzeArmor;
import io.github.pylonmc.pylon.base.content.building.Elevator;
import io.github.pylonmc.pylon.base.content.building.ExplosiveTarget;
import io.github.pylonmc.pylon.base.content.building.Immobilizer;
import io.github.pylonmc.pylon.base.content.combat.BeheadingSword;
import io.github.pylonmc.pylon.base.content.combat.IceArrow;
import io.github.pylonmc.pylon.base.content.combat.ReactivatedWitherSkull;
import io.github.pylonmc.pylon.base.content.combat.RecoilArrow;
import io.github.pylonmc.pylon.base.content.machines.fluid.*;
import io.github.pylonmc.pylon.base.content.machines.hydraulics.*;
import io.github.pylonmc.pylon.base.content.machines.simple.CoreDrill;
import io.github.pylonmc.pylon.base.content.machines.simple.ImprovedManualCoreDrill;
import io.github.pylonmc.pylon.base.content.machines.simple.Press;
import io.github.pylonmc.pylon.base.content.machines.simple.VacuumHopper;
import io.github.pylonmc.pylon.base.content.machines.smelting.PitKiln;
import io.github.pylonmc.pylon.base.content.tools.FireproofRune;
import io.github.pylonmc.pylon.base.content.science.Loupe;
import io.github.pylonmc.pylon.base.content.science.ResearchPack;
import io.github.pylonmc.pylon.base.content.tools.*;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPipe;
import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.keys.tags.BlockTypeTagKeys;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import io.papermc.paper.registry.set.RegistrySet;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionType;

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
        BasePages.RESOURCES.addItem(ROCK_DUST);
    }

    public static final ItemStack OBSIDIAN_CHIP = ItemStackBuilder.pylonItem(Material.POLISHED_BLACKSTONE_BUTTON, BaseKeys.OBSIDIAN_CHIP)
            .build();
    static {
        PylonItem.register(PylonItem.class, OBSIDIAN_CHIP);
        BasePages.RESOURCES.addItem(OBSIDIAN_CHIP);
    }

    public static final ItemStack COAL_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.COAL_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, COAL_DUST);
        BasePages.RESOURCES.addItem(COAL_DUST);
    }

    public static final ItemStack CARBON = ItemStackBuilder.pylonItem(Material.CHARCOAL, BaseKeys.CARBON)
            .build();
    static {
        PylonItem.register(PylonItem.class, CARBON);
        BasePages.RESOURCES.addItem(CARBON);
    }

    public static final ItemStack SULFUR = ItemStackBuilder.pylonItem(Material.YELLOW_DYE, BaseKeys.SULFUR)
            .build();
    static {
        PylonItem.register(PylonItem.class, SULFUR);
        BasePages.RESOURCES.addItem(SULFUR);
    }

    public static final ItemStack GYPSUM = ItemStackBuilder.pylonItem(Material.QUARTZ, BaseKeys.GYPSUM)
            .build();
    static {
        PylonItem.register(PylonItem.class, GYPSUM);
        BasePages.RESOURCES.addItem(GYPSUM);
    }

    public static final ItemStack GYPSUM_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.GYPSUM_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, GYPSUM_DUST);
        BasePages.RESOURCES.addItem(GYPSUM_DUST);
    }

    public static final ItemStack COPPER_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.COPPER_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, COPPER_DUST);
        BasePages.RESOURCES.addItem(COPPER_DUST);
    }

    public static final ItemStack CRUSHED_RAW_COPPER = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.CRUSHED_RAW_COPPER)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_COPPER);
        BasePages.RESOURCES.addItem(CRUSHED_RAW_COPPER);
    }

    public static final ItemStack IRON_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.IRON_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, IRON_DUST);
        BasePages.RESOURCES.addItem(IRON_DUST);
    }

    public static final ItemStack CRUSHED_RAW_IRON = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.CRUSHED_RAW_IRON)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_IRON);
        BasePages.RESOURCES.addItem(CRUSHED_RAW_IRON);
    }

    public static final ItemStack GOLD_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.GOLD_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, GOLD_DUST);
        BasePages.RESOURCES.addItem(GOLD_DUST);
    }

    public static final ItemStack CRUSHED_RAW_GOLD = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.CRUSHED_RAW_GOLD)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_GOLD);
        BasePages.RESOURCES.addItem(CRUSHED_RAW_GOLD);
    }

    public static final ItemStack QUARTZ_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.QUARTZ_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, QUARTZ_DUST);
        BasePages.RESOURCES.addItem(QUARTZ_DUST);
    }

    public static final ItemStack DIAMOND_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.DIAMOND_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, DIAMOND_DUST);
        BasePages.RESOURCES.addItem(DIAMOND_DUST);
    }

    public static final ItemStack EMERALD_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.EMERALD_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, EMERALD_DUST);
        BasePages.RESOURCES.addItem(EMERALD_DUST);
    }

    public static final ItemStack RAW_TIN = ItemStackBuilder.pylonItem(Material.RAW_IRON, BaseKeys.RAW_TIN)
            .build();
    static {
        PylonItem.register(PylonItem.class, RAW_TIN);
        BasePages.RESOURCES.addItem(RAW_TIN);
    }

    public static final ItemStack TIN_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.TIN_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_INGOT);
        BasePages.RESOURCES.addItem(TIN_INGOT);
    }

    public static final ItemStack TIN_NUGGET = ItemStackBuilder.pylonItem(Material.IRON_NUGGET, BaseKeys.TIN_NUGGET)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_NUGGET);
        BasePages.RESOURCES.addItem(TIN_NUGGET);
    }

    public static final ItemStack TIN_BLOCK = ItemStackBuilder.pylonItem(Material.IRON_BLOCK, BaseKeys.TIN_BLOCK)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_BLOCK, BaseKeys.TIN_BLOCK);
        BasePages.RESOURCES.addItem(TIN_BLOCK);
    }

    public static final ItemStack CRUSHED_RAW_TIN = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.CRUSHED_RAW_TIN)
            .build();
    static {
        PylonItem.register(PylonItem.class, CRUSHED_RAW_TIN);
        BasePages.RESOURCES.addItem(CRUSHED_RAW_TIN);


    }

    public static final ItemStack TIN_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.TIN_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_DUST);
        BasePages.RESOURCES.addItem(TIN_DUST);
    }

    public static final ItemStack BRONZE_INGOT = ItemStackBuilder.pylonItem(Material.COPPER_INGOT, BaseKeys.BRONZE_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_INGOT);
        BasePages.RESOURCES.addItem(BRONZE_INGOT);
    }

    public static final ItemStack BRONZE_DUST = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.BRONZE_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_DUST);
        BasePages.RESOURCES.addItem(BRONZE_DUST);
    }

    public static final ItemStack BRONZE_NUGGET = ItemStackBuilder.pylonItem(Material.ARMADILLO_SCUTE, BaseKeys.BRONZE_NUGGET)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_NUGGET);
        BasePages.RESOURCES.addItem(BRONZE_NUGGET);
    }

    public static final ItemStack BRONZE_BLOCK = ItemStackBuilder.pylonItem(Material.COPPER_BLOCK, BaseKeys.BRONZE_BLOCK)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_BLOCK, BaseKeys.BRONZE_BLOCK);
        BasePages.RESOURCES.addItem(BRONZE_BLOCK);
    }

    public static final ItemStack STEEL_INGOT = ItemStackBuilder.pylonItem(Material.NETHERITE_INGOT, BaseKeys.STEEL_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_INGOT);
        BasePages.RESOURCES.addItem(STEEL_INGOT);
    }

    public static final ItemStack STEEL_NUGGET = ItemStackBuilder.pylonItem(Material.NETHERITE_SCRAP, BaseKeys.STEEL_NUGGET)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_NUGGET);
        BasePages.RESOURCES.addItem(STEEL_NUGGET);
    }

    public static final ItemStack STEEL_BLOCK = ItemStackBuilder.pylonItem(Material.NETHERITE_BLOCK, BaseKeys.STEEL_BLOCK)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_BLOCK, BaseKeys.STEEL_BLOCK);
        BasePages.RESOURCES.addItem(STEEL_BLOCK);
    }

    public static final ItemStack STEEL_DUST = ItemStackBuilder.pylonItem(Material.GUNPOWDER, BaseKeys.STEEL_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_DUST);
        BasePages.RESOURCES.addItem(STEEL_DUST);
    }

    public static final ItemStack NICKEL_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.NICKEL_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, NICKEL_INGOT);
        BasePages.RESOURCES.addItem(NICKEL_INGOT);
    }

    public static final ItemStack NICKEL_NUGGET = ItemStackBuilder.pylonItem(Material.IRON_NUGGET, BaseKeys.NICKEL_NUGGET)
            .build();
    static {
        PylonItem.register(PylonItem.class, NICKEL_NUGGET);
        BasePages.RESOURCES.addItem(NICKEL_NUGGET);
    }

    public static final ItemStack NICKEL_BLOCK = ItemStackBuilder.pylonItem(Material.IRON_BLOCK, BaseKeys.NICKEL_BLOCK)
            .build();
    static {
        PylonItem.register(PylonItem.class, NICKEL_BLOCK, BaseKeys.NICKEL_BLOCK);
        BasePages.RESOURCES.addItem(NICKEL_BLOCK);
    }

    public static final ItemStack NICKEL_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.NICKEL_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, NICKEL_DUST);
        BasePages.RESOURCES.addItem(NICKEL_DUST);
    }

    public static final ItemStack COBALT_INGOT = ItemStackBuilder.pylonItem(Material.IRON_INGOT, BaseKeys.COBALT_INGOT)
            .build();
    static {
        PylonItem.register(PylonItem.class, COBALT_INGOT);
        BasePages.RESOURCES.addItem(COBALT_INGOT);
    }

    public static final ItemStack COBALT_NUGGET = ItemStackBuilder.pylonItem(Material.IRON_NUGGET, BaseKeys.COBALT_NUGGET)
            .build();
    static {
        PylonItem.register(PylonItem.class, COBALT_NUGGET);
        BasePages.RESOURCES.addItem(COBALT_NUGGET);
    }

    public static final ItemStack COBALT_BLOCK = ItemStackBuilder.pylonItem(Material.IRON_BLOCK, BaseKeys.COBALT_BLOCK)
            .build();
    static {
        PylonItem.register(PylonItem.class, COBALT_BLOCK, BaseKeys.COBALT_BLOCK);
        BasePages.RESOURCES.addItem(COBALT_BLOCK);
    }

    public static final ItemStack COBALT_DUST = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.COBALT_DUST)
            .build();
    static {
        PylonItem.register(PylonItem.class, COBALT_DUST);
        BasePages.RESOURCES.addItem(COBALT_DUST);
    }
    // </editor-fold>

    //<editor-fold desc="Sheets" defaultstate=collapsed>
    public static final ItemStack COPPER_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.COPPER_SHEET)
            .build();
    static {
        PylonItem.register(PylonItem.class, COPPER_SHEET);
        BasePages.COMPONENTS.addItem(COPPER_SHEET);
    }

    public static final ItemStack GOLD_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.GOLD_SHEET)
            .build();
    static {
        PylonItem.register(PylonItem.class, GOLD_SHEET);
        BasePages.COMPONENTS.addItem(GOLD_SHEET);
    }

    public static final ItemStack IRON_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.IRON_SHEET)
            .build();
    static {
        PylonItem.register(PylonItem.class, IRON_SHEET);
        BasePages.COMPONENTS.addItem(IRON_SHEET);
    }

    public static final ItemStack TIN_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.TIN_SHEET)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_SHEET);
        BasePages.COMPONENTS.addItem(TIN_SHEET);


    }

    public static final ItemStack BRONZE_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.BRONZE_SHEET)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_SHEET);
        BasePages.COMPONENTS.addItem(BRONZE_SHEET);


    }

    public static final ItemStack STEEL_SHEET = ItemStackBuilder.pylonItem(Material.PAPER, BaseKeys.STEEL_SHEET)
            .build();
    static {
        PylonItem.register(PylonItem.class, STEEL_SHEET);
        BasePages.COMPONENTS.addItem(STEEL_SHEET);
    }
    //</editor-fold>

    //<editor-fold desc="Hammers" defaultstate=collapsed>
    public static final ItemStack HAMMER_STONE = Hammer.createItemStack(
            BaseKeys.HAMMER_STONE,
            Material.STONE_PICKAXE,
            (1.0 / 3) - 4,
            1,
            1
    ).set(DataComponentTypes.MAX_DAMAGE, 66)
        .set(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(
                Settings.get(BaseKeys.HAMMER_STONE).getOrThrow("cooldown-ticks", ConfigAdapter.INT) / 20f
            )
            .cooldownGroup(BaseKeys.HAMMER_STONE.key())
            .build())
        .build();
    static {
        PylonItem.register(Hammer.class, HAMMER_STONE);
        BasePages.TOOLS.addItem(HAMMER_STONE);
    }

    public static final ItemStack HAMMER_IRON = Hammer.createItemStack(
            BaseKeys.HAMMER_IRON,
            Material.IRON_PICKAXE,
            (1.0 / 2) - 4,
            1.5,
            3
    ).set(DataComponentTypes.MAX_DAMAGE, 125)
        .set(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(
                Settings.get(BaseKeys.HAMMER_IRON).getOrThrow("cooldown-ticks", ConfigAdapter.INT) / 20f
            )
            .cooldownGroup(BaseKeys.HAMMER_IRON.key())
            .build())
        .build();
    static {
        PylonItem.register(Hammer.class, HAMMER_IRON);
        BasePages.TOOLS.addItem(HAMMER_IRON);
    }

    public static final ItemStack HAMMER_DIAMOND = Hammer.createItemStack(
            BaseKeys.HAMMER_DIAMOND,
            Material.DIAMOND_PICKAXE,
            (1.0 / 1) - 4,
            2,
            5
    ).set(DataComponentTypes.MAX_DAMAGE, 781)
        .set(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(
                Settings.get(BaseKeys.HAMMER_DIAMOND).getOrThrow("cooldown-ticks", ConfigAdapter.INT) / 20f
            )
            .cooldownGroup(BaseKeys.HAMMER_DIAMOND.key())
            .build())
        .build();
    static {
        PylonItem.register(Hammer.class, HAMMER_DIAMOND);
        BasePages.TOOLS.addItem(HAMMER_DIAMOND);
    }
    //</editor-fold>

    //<editor-fold desc="Bronze tools/armour" defaultstate=collapsed>
    public static final ItemStack BRONZE_SWORD = ItemStackBuilder.pylonItem(Material.GOLDEN_SWORD, BaseKeys.BRONZE_SWORD)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BRONZE_SWORD).getOrThrow("durability", ConfigAdapter.INT))
            .set(DataComponentTypes.WEAPON, Weapon.weapon()
                    .itemDamagePerAttack(Settings.get(BaseKeys.BRONZE_SWORD).getOrThrow("damage", ConfigAdapter.INT))
            )
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_SWORD);
        BasePages.COMBAT.addItem(BRONZE_SWORD);
    }

    public static final ItemStack BRONZE_AXE = ItemStackBuilder.pylonItem(Material.GOLDEN_AXE, BaseKeys.BRONZE_AXE)
            .set(DataComponentTypes.TOOL, Tool.tool()
                    .defaultMiningSpeed(Settings.get(BaseKeys.BRONZE_AXE).getOrThrow("mining-speed", ConfigAdapter.DOUBLE).floatValue())
                    .addRule(Tool.rule(
                            RegistrySet.keySet(BlockTypeTagKeys.MINEABLE_AXE.registryKey()),
                            Settings.get(BaseKeys.BRONZE_AXE).getOrThrow("mining-speed", ConfigAdapter.DOUBLE).floatValue(),
                            TriState.TRUE
                    ))
            )
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BRONZE_AXE).getOrThrow("durability", ConfigAdapter.INT))
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_AXE);
        BasePages.TOOLS.addItem(BRONZE_AXE);
    }

    public static final ItemStack BRONZE_PICKAXE = ItemStackBuilder.pylonItem(Material.GOLDEN_PICKAXE, BaseKeys.BRONZE_PICKAXE)
            .set(DataComponentTypes.TOOL, Tool.tool()
                    .defaultMiningSpeed(Settings.get(BaseKeys.BRONZE_PICKAXE).getOrThrow("mining-speed", ConfigAdapter.DOUBLE).floatValue())
                    .addRule(Tool.rule(
                            RegistrySet.keySet(BlockTypeTagKeys.MINEABLE_PICKAXE.registryKey()),
                            Settings.get(BaseKeys.BRONZE_PICKAXE).getOrThrow("mining-speed", ConfigAdapter.DOUBLE).floatValue(),
                            TriState.TRUE
                    ))
            )
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BRONZE_PICKAXE).getOrThrow("durability", ConfigAdapter.INT))
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_PICKAXE);
        BasePages.TOOLS.addItem(BRONZE_PICKAXE);
    }

    public static final ItemStack BRONZE_SHOVEL = ItemStackBuilder.pylonItem(Material.GOLDEN_SHOVEL, BaseKeys.BRONZE_SHOVEL)
            .set(DataComponentTypes.TOOL, Tool.tool()
                    .defaultMiningSpeed(Settings.get(BaseKeys.BRONZE_SHOVEL).getOrThrow("mining-speed", ConfigAdapter.DOUBLE).floatValue())
                    .addRule(Tool.rule(
                            RegistrySet.keySet(BlockTypeTagKeys.MINEABLE_SHOVEL.registryKey()),
                            Settings.get(BaseKeys.BRONZE_SHOVEL).getOrThrow("mining-speed", ConfigAdapter.DOUBLE).floatValue(),
                            TriState.TRUE
                    ))
            )
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BRONZE_SHOVEL).getOrThrow("durability", ConfigAdapter.INT))
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_SHOVEL);
        BasePages.TOOLS.addItem(BRONZE_SHOVEL);
    }

    public static final ItemStack BRONZE_HOE = ItemStackBuilder.pylonItem(Material.GOLDEN_HOE, BaseKeys.BRONZE_HOE)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BRONZE_HOE).getOrThrow("durability", ConfigAdapter.INT))
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_HOE);
        BasePages.TOOLS.addItem(BRONZE_HOE);
    }
    //</editor-fold>

    public static final ItemStack BRONZE_HELMET = ItemStackBuilder.pylonItem(Material.GOLDEN_HELMET, BaseKeys.BRONZE_HELMET)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BRONZE_HELMET).getOrThrow("durability", ConfigAdapter.INT))
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            BaseKeys.BRONZE_HELMET,
                            Settings.get(BaseKeys.BRONZE_HELMET).getOrThrow("armor", ConfigAdapter.DOUBLE),
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.HEAD
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            BaseKeys.BRONZE_HELMET,
                            Settings.get(BaseKeys.BRONZE_HELMET).getOrThrow("armor-toughness", ConfigAdapter.DOUBLE),
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.HEAD
                    ))
                    .build()
            )
            .build();
    static {
        PylonItem.register(BronzeArmor.class, BRONZE_HELMET);
        BasePages.ARMOUR.addItem(BRONZE_HELMET);
    }

    public static final ItemStack BRONZE_CHESTPLATE = ItemStackBuilder.pylonItem(Material.GOLDEN_CHESTPLATE, BaseKeys.BRONZE_CHESTPLATE)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BRONZE_CHESTPLATE).getOrThrow("durability", ConfigAdapter.INT))
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            BaseKeys.BRONZE_CHESTPLATE,
                            Settings.get(BaseKeys.BRONZE_CHESTPLATE).getOrThrow("armor", ConfigAdapter.DOUBLE),
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.CHEST
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            BaseKeys.BRONZE_CHESTPLATE,
                            Settings.get(BaseKeys.BRONZE_CHESTPLATE).getOrThrow("armor-toughness", ConfigAdapter.DOUBLE),
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.CHEST
                    ))
                    .build()
            )
            .build();
    static {
        PylonItem.register(BronzeArmor.class, BRONZE_CHESTPLATE);
        BasePages.ARMOUR.addItem(BRONZE_CHESTPLATE);
    }

    public static final ItemStack BRONZE_LEGGINGS = ItemStackBuilder.pylonItem(Material.GOLDEN_LEGGINGS, BaseKeys.BRONZE_LEGGINGS)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BRONZE_LEGGINGS).getOrThrow("durability", ConfigAdapter.INT))
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            BaseKeys.BRONZE_LEGGINGS,
                            Settings.get(BaseKeys.BRONZE_LEGGINGS).getOrThrow("armor", ConfigAdapter.DOUBLE),
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.LEGS
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            BaseKeys.BRONZE_LEGGINGS,
                            Settings.get(BaseKeys.BRONZE_LEGGINGS).getOrThrow("armor-toughness", ConfigAdapter.DOUBLE),
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.LEGS
                    ))
                    .build()
            )
            .build();
    static {
        PylonItem.register(BronzeArmor.class, BRONZE_LEGGINGS);
        BasePages.ARMOUR.addItem(BRONZE_LEGGINGS);
    }

    public static final ItemStack BRONZE_BOOTS = ItemStackBuilder.pylonItem(Material.GOLDEN_BOOTS, BaseKeys.BRONZE_BOOTS)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BRONZE_BOOTS).getOrThrow("durability", ConfigAdapter.INT))
            .set(DataComponentTypes.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.itemAttributes()
                    .addModifier(Attribute.ARMOR, new AttributeModifier(
                            BaseKeys.BRONZE_BOOTS,
                            Settings.get(BaseKeys.BRONZE_BOOTS).getOrThrow("armor", ConfigAdapter.DOUBLE),
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.FEET
                    ))
                    .addModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                            BaseKeys.BRONZE_BOOTS,
                            Settings.get(BaseKeys.BRONZE_BOOTS).getOrThrow("armor-toughness", ConfigAdapter.DOUBLE),
                            AttributeModifier.Operation.ADD_NUMBER,
                            EquipmentSlotGroup.FEET
                    ))
                    .build()
            )
            .build();
    static {
        PylonItem.register(BronzeArmor.class, BRONZE_BOOTS);
        BasePages.ARMOUR.addItem(BRONZE_BOOTS);
    }

    public static final ItemStack WATERING_CAN = ItemStackBuilder.pylonItem(Material.BUCKET, BaseKeys.WATERING_CAN)
            .build();
    static {
        PylonItem.register(WateringCan.class, WATERING_CAN);
        BasePages.TOOLS.addItem(WATERING_CAN);
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
        BasePages.FOOD.addItem(MONSTER_JERKY);
    }

    public static final ItemStack SHIMMER_DUST_1 = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.SHIMMER_DUST_1)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_1);
        BasePages.RESOURCES.addItem(SHIMMER_DUST_1);
    }

    public static final ItemStack COVALENT_BINDER = ItemStackBuilder.pylonItem(Material.LIGHT_BLUE_DYE, BaseKeys.COVALENT_BINDER)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(PylonItem.class, COVALENT_BINDER);
        BasePages.RESOURCES.addItem(COVALENT_BINDER);
    }

    public static final ItemStack SHIMMER_DUST_2 = ItemStackBuilder.pylonItem(Material.REDSTONE, BaseKeys.SHIMMER_DUST_2)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_2);
        BasePages.RESOURCES.addItem(SHIMMER_DUST_2);
    }

    public static final ItemStack SHIMMER_DUST_3 = ItemStackBuilder.pylonItem(Material.GLOWSTONE_DUST, BaseKeys.SHIMMER_DUST_3)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(PylonItem.class, SHIMMER_DUST_3);
        BasePages.RESOURCES.addItem(SHIMMER_DUST_3);
    }

    //<editor-fold desc="Portable Items" defaultstate=collapsed>
    public static final ItemStack PORTABILITY_CATALYST = ItemStackBuilder.pylonItem(Material.AMETHYST_SHARD, BaseKeys.PORTABILITY_CATALYST)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(PylonItem.class, PORTABILITY_CATALYST);
        BasePages.COMPONENTS.addItem(PORTABILITY_CATALYST);
    }

    public static final ItemStack PORTABLE_CRAFTING_TABLE = ItemStackBuilder.pylonItem(Material.CRAFTING_TABLE, BaseKeys.PORTABLE_CRAFTING_TABLE)
            .build();
    static {
        PylonItem.register(PortableCraftingTable.class, PORTABLE_CRAFTING_TABLE);
        BasePages.TOOLS.addItem(PORTABLE_CRAFTING_TABLE);
    }

    public static final ItemStack PORTABLE_DUSTBIN = ItemStackBuilder.pylonItem(Material.CAULDRON, BaseKeys.PORTABLE_DUSTBIN)
            .build();
    static {
        PylonItem.register(PortableDustbin.class, PORTABLE_DUSTBIN);
        BasePages.TOOLS.addItem(PORTABLE_DUSTBIN);
    }

    public static final ItemStack PORTABLE_ENDER_CHEST = ItemStackBuilder.pylonItem(Material.ENDER_CHEST, BaseKeys.PORTABLE_ENDER_CHEST)
            .build();
    static {
        PylonItem.register(PortableEnderChest.class, PORTABLE_ENDER_CHEST);
        BasePages.TOOLS.addItem(PORTABLE_ENDER_CHEST);
    }
    //</editor-fold>

    //<editor-fold desc="Medical items" defaultstate=collapsed>
    public static final ItemStack FIBER = ItemStackBuilder.pylonItem(Material.BAMBOO_MOSAIC, BaseKeys.FIBER)
            .build();
    static {
        PylonItem.register(PylonItem.class, FIBER);
        BasePages.COMPONENTS.addItem(FIBER);
    }

    public static final ItemStack BANDAGE = ItemStackBuilder.pylonItem(Material.COBWEB, BaseKeys.BANDAGE)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .consumeSeconds(Settings.get(BaseKeys.BANDAGE).getOrThrow("consume-seconds", ConfigAdapter.DOUBLE).floatValue())
                    .animation(ItemUseAnimation.BOW)
                    .hasConsumeParticles(false)
                    .build())
            .build();
    static {
        PylonItem.register(HealingConsumable.class, BANDAGE);
        BasePages.COMBAT.addItem(BANDAGE);
    }

    public static final ItemStack SPLINT = ItemStackBuilder.pylonItem(Material.STICK, BaseKeys.SPLINT)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .consumeSeconds(Settings.get(BaseKeys.SPLINT).getOrThrow("consume-seconds", ConfigAdapter.DOUBLE).floatValue())
                    .animation(ItemUseAnimation.BOW)
                    .hasConsumeParticles(false)
                    .build())
            .build();
    static {
        PylonItem.register(HealingConsumable.class, SPLINT);
        BasePages.COMBAT.addItem(SPLINT);
    }

    public static final ItemStack DISINFECTANT = ItemStackBuilder.pylonItem(Material.BREWER_POTTERY_SHERD, BaseKeys.DISINFECTANT)
            // Using the actual potion material doesn't let you set the name properly, gives you a
            // class string of a nonexistant potion type for some reason
            .set(DataComponentTypes.ITEM_MODEL, Material.POTION.getKey())
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .hasConsumeParticles(false)
                    .consumeSeconds(Settings.get(BaseKeys.DISINFECTANT).getOrThrow("consume-seconds", ConfigAdapter.DOUBLE).floatValue())
                    .animation(ItemUseAnimation.BOW)
                    .addEffect(ConsumeEffect.clearAllStatusEffects())
                    .build())
            .build();
    static {
        PylonItem.register(HealingConsumable.class, DISINFECTANT);
        BasePages.COMBAT.addItem(DISINFECTANT);
    }

    public static final ItemStack MEDKIT = ItemStackBuilder.pylonItem(Material.SHULKER_SHELL, BaseKeys.MEDKIT)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .consumeSeconds(Settings.get(BaseKeys.MEDKIT).getOrThrow("consume-seconds", ConfigAdapter.DOUBLE).floatValue())
                    .animation(ItemUseAnimation.BOW)
                    .hasConsumeParticles(false)
                    .addEffect(ConsumeEffect.clearAllStatusEffects())
            )
            .build();
    static {
        PylonItem.register(HealingConsumable.class, MEDKIT);
        BasePages.COMBAT.addItem(MEDKIT);
    }
    //</editor-fold>

    public static final ItemStack LUMBER_AXE = ItemStackBuilder.pylonItem(Material.WOODEN_AXE, BaseKeys.LUMBER_AXE)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.LUMBER_AXE).getOrThrow("durability", ConfigAdapter.INT))
            .build();
    static {
        PylonItem.register(LumberAxe.class, LUMBER_AXE);
        BasePages.TOOLS.addItem(LUMBER_AXE);
    }

    public static final ItemStack BRICK_MOLD = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.BRICK_MOLD)
            .set(DataComponentTypes.ITEM_MODEL, Material.OAK_FENCE_GATE.getKey())
            .set(DataComponentTypes.USE_COOLDOWN, UseCooldown
                .useCooldown(
                    Settings.get(BaseKeys.BRICK_MOLD).getOrThrow("cooldown-ticks", ConfigAdapter.INT) / 20f
                )
                .cooldownGroup(BaseKeys.BRICK_MOLD.key())
                .build())
            .build();
    static {
        PylonItem.register(BrickMold.class, BRICK_MOLD);
        BasePages.TOOLS.addItem(BRICK_MOLD);
    }

    @SuppressWarnings("ConstantConditions")
    public static final ItemStack CONFETTI_POPPER = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.CONFETTI_POPPER)
            .set(DataComponentTypes.ITEM_MODEL, Material.FIREWORK_ROCKET.getKey())
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                .consumeSeconds(
                    Settings.get(BaseKeys.CONFETTI_POPPER).getOrThrow("consume-seconds", ConfigAdapter.DOUBLE).floatValue()
                )
                .sound(Registry.SOUNDS.getKey(Sound.ITEM_CROSSBOW_LOADING_START))
                .animation(ItemUseAnimation.TOOT_HORN)
                .hasConsumeParticles(false)
            )
            .set(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(
                    Settings.get(BaseKeys.CONFETTI_POPPER).getOrThrow("cooldown-seconds", ConfigAdapter.DOUBLE).floatValue()
                )
                .cooldownGroup(BaseKeys.CONFETTI_POPPER)
                .build())
            .build();
    static {
        PylonItem.register(ConfettiPopper.class, CONFETTI_POPPER);
        BasePages.TOOLS.addItem(CONFETTI_POPPER);
    }

    public static final ItemStack GRINDSTONE = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE_SLAB, BaseKeys.GRINDSTONE)
            .build();
    static {
        PylonItem.register(PylonItem.class, GRINDSTONE, BaseKeys.GRINDSTONE);
        BasePages.SIMPLE_MACHINES.addItem(GRINDSTONE);
    }

    public static final ItemStack GRINDSTONE_HANDLE = ItemStackBuilder.pylonItem(Material.OAK_FENCE, BaseKeys.GRINDSTONE_HANDLE)
            .build();
    static {
        PylonItem.register(PylonItem.class, GRINDSTONE_HANDLE, BaseKeys.GRINDSTONE_HANDLE);
        BasePages.SIMPLE_MACHINES.addItem(GRINDSTONE_HANDLE);
    }

    public static final ItemStack FLOUR = ItemStackBuilder.pylonItem(Material.SUGAR, BaseKeys.FLOUR)
            .build();
    static {
        PylonItem.register(PylonItem.class, FLOUR);
        BasePages.RESOURCES.addItem(FLOUR);
    }

    public static final ItemStack DOUGH = ItemStackBuilder.pylonItem(Material.YELLOW_DYE, BaseKeys.DOUGH)
            .build();
    static {
        PylonItem.register(PylonItem.class, DOUGH);
        BasePages.RESOURCES.addItem(DOUGH);
    }

    public static final ItemStack MIXING_POT = ItemStackBuilder.pylonItem(Material.CAULDRON, BaseKeys.MIXING_POT)
            .build();
    static {
        PylonItem.register(PylonItem.class, MIXING_POT, BaseKeys.MIXING_POT);
        BasePages.SIMPLE_MACHINES.addItem(MIXING_POT);
    }

    public static final ItemStack ENRICHED_NETHERRACK = ItemStackBuilder.pylonItem(Material.NETHERRACK, BaseKeys.ENRICHED_NETHERRACK)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        PylonItem.register(PylonItem.class, ENRICHED_NETHERRACK, BaseKeys.ENRICHED_NETHERRACK);
        BasePages.COMPONENTS.addItem(ENRICHED_NETHERRACK);
    }

    public static final ItemStack SHIMMER_SKULL = ItemStackBuilder.pylonItem(Material.WITHER_SKELETON_SKULL, BaseKeys.SHIMMER_SKULL)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        PylonItem.register(PylonItem.class, SHIMMER_SKULL);
        BasePages.COMPONENTS.addItem(SHIMMER_SKULL);
    }

    public static final ItemStack IGNEOUS_COMPOSITE = ItemStackBuilder.pylonItem(Material.OBSIDIAN, BaseKeys.IGNEOUS_COMPOSITE)
            .build();
    static {
        PylonItem.register(PylonItem.class, IGNEOUS_COMPOSITE, BaseKeys.IGNEOUS_COMPOSITE);
        BasePages.BUILDING.addItem(IGNEOUS_COMPOSITE);
    }

    public static final ItemStack HEALTH_TALISMAN_SIMPLE = ItemStackBuilder.pylonItem(Material.AMETHYST_SHARD, BaseKeys.HEALTH_TALISMAN_SIMPLE)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    static {
        PylonItem.register(HealthTalisman.class, HEALTH_TALISMAN_SIMPLE);
        BasePages.COMBAT.addItem(HEALTH_TALISMAN_SIMPLE);
    }

    public static final ItemStack HEALTH_TALISMAN_ADVANCED = ItemStackBuilder.pylonItem(Material.AMETHYST_CLUSTER, BaseKeys.HEALTH_TALISMAN_ADVANCED)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    static {
        PylonItem.register(HealthTalisman.class, HEALTH_TALISMAN_ADVANCED);
        BasePages.COMBAT.addItem(HEALTH_TALISMAN_ADVANCED);
    }

    public static final ItemStack HEALTH_TALISMAN_ULTIMATE = ItemStackBuilder.pylonItem(Material.BUDDING_AMETHYST, BaseKeys.HEALTH_TALISMAN_ULTIMATE)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    static {
        PylonItem.register(HealthTalisman.class, HEALTH_TALISMAN_ULTIMATE);
        BasePages.COMBAT.addItem(HEALTH_TALISMAN_ULTIMATE);
    }

    public static final ItemStack BEHEADING_SWORD = ItemStackBuilder.pylonItem(Material.DIAMOND_SWORD, BaseKeys.BEHEADING_SWORD)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.BEHEADING_SWORD).getOrThrow("durability", ConfigAdapter.INT))
            .build();
    static {
        PylonItem.register(BeheadingSword.class, BEHEADING_SWORD);
        BasePages.COMBAT.addItem(BEHEADING_SWORD);
    }

    public static final ItemStack PEDESTAL = ItemStackBuilder.pylonItem(Material.STONE_BRICK_WALL, BaseKeys.PEDESTAL)
            .build();
    static {
        PylonItem.register(PylonItem.class, PEDESTAL, BaseKeys.PEDESTAL);
        BasePages.BUILDING.addItem(PEDESTAL);
    }

    public static final ItemStack MAGIC_PEDESTAL = ItemStackBuilder.pylonItem(Material.MOSSY_STONE_BRICK_WALL, BaseKeys.MAGIC_PEDESTAL)
            .build();
    static {
        PylonItem.register(PylonItem.class, MAGIC_PEDESTAL, BaseKeys.MAGIC_PEDESTAL);
        BasePages.SIMPLE_MACHINES.addItem(MAGIC_PEDESTAL);
    }

    public static final ItemStack MAGIC_ALTAR = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE_SLAB, BaseKeys.MAGIC_ALTAR)
            .build();
    static {
        PylonItem.register(PylonItem.class, MAGIC_ALTAR, BaseKeys.MAGIC_ALTAR);
        BasePages.SIMPLE_MACHINES.addItem(MAGIC_ALTAR);
    }

    public static final ItemStack FLUID_PIPE_WOOD = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_WOOD)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_WOOD).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_WOOD);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_WOOD);
    }

    public static final ItemStack FLUID_PIPE_COPPER = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_COPPER)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_COPPER).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_COPPER);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_COPPER);
    }

    public static final ItemStack FLUID_PIPE_TIN = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_TIN)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_TIN).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_TIN);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_TIN);
    }

    public static final ItemStack FLUID_PIPE_IRON = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_IRON)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_IRON).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_IRON);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_IRON);
    }

    public static final ItemStack FLUID_PIPE_BRONZE = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_BRONZE)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_BRONZE).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_BRONZE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_BRONZE);
    }

    public static final ItemStack FLUID_PIPE_IGNEOUS_COMPOSITE = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_IGNEOUS_COMPOSITE)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_IGNEOUS_COMPOSITE).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_IGNEOUS_COMPOSITE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_IGNEOUS_COMPOSITE);
    }

    public static final ItemStack FLUID_PIPE_STEEL = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_STEEL)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_STEEL).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        PylonItem.register(FluidPipe.class, FLUID_PIPE_STEEL);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_STEEL);
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
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_WOOD);
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
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_COPPER);
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
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_TIN);
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
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_IRON);
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
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_BRONZE);
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
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE);
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
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_STEEL);
    }

    public static final ItemStack FLUID_TANK
            = ItemStackBuilder.pylonItem(Material.GRAY_TERRACOTTA, BaseKeys.FLUID_TANK)
            .build();
    static {
        PylonItem.register(FluidTank.Item.class, FLUID_TANK, BaseKeys.FLUID_TANK);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK);
    }

    public static final ItemStack FLUID_TANK_CASING_WOOD
            = ItemStackBuilder.pylonItem(Material.BROWN_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_WOOD)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_WOOD, BaseKeys.FLUID_TANK_CASING_WOOD);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_WOOD);
    }

    public static final ItemStack FLUID_TANK_CASING_COPPER
            = ItemStackBuilder.pylonItem(Material.ORANGE_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_COPPER)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_COPPER, BaseKeys.FLUID_TANK_CASING_COPPER);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_COPPER);
    }

    public static final ItemStack FLUID_TANK_CASING_TIN
            = ItemStackBuilder.pylonItem(Material.GREEN_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_TIN)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_TIN, BaseKeys.FLUID_TANK_CASING_TIN);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_TIN);
    }

    public static final ItemStack FLUID_TANK_CASING_IRON
            = ItemStackBuilder.pylonItem(Material.LIGHT_GRAY_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_IRON)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_IRON, BaseKeys.FLUID_TANK_CASING_IRON);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_IRON);
    }

    public static final ItemStack FLUID_TANK_CASING_BRONZE
            = ItemStackBuilder.pylonItem(Material.ORANGE_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_BRONZE)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_BRONZE, BaseKeys.FLUID_TANK_CASING_BRONZE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_BRONZE);
    }

    public static final ItemStack FLUID_TANK_CASING_IGNEOUS_COMPOSITE
            = ItemStackBuilder.pylonItem(Material.BLACK_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_IGNEOUS_COMPOSITE)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_IGNEOUS_COMPOSITE, BaseKeys.FLUID_TANK_CASING_IGNEOUS_COMPOSITE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_IGNEOUS_COMPOSITE);
    }

    public static final ItemStack FLUID_TANK_CASING_STEEL
            = ItemStackBuilder.pylonItem(Material.GRAY_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_STEEL)
            .build();
    static {
        PylonItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_STEEL, BaseKeys.FLUID_TANK_CASING_STEEL);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_STEEL);
    }

    public static final ItemStack ROTOR = ItemStackBuilder.pylonItem(Material.IRON_TRAPDOOR, BaseKeys.ROTOR)
            .build();
    static {
        PylonItem.register(PylonItem.class, ROTOR);
        BasePages.COMPONENTS.addItem(ROTOR);
    }

    public static final ItemStack BACKFLOW_VALVE = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.BACKFLOW_VALVE)
            .build();
    static {
        PylonItem.register(PylonItem.class, BACKFLOW_VALVE);
        BasePages.COMPONENTS.addItem(BACKFLOW_VALVE);
    }

    public static final ItemStack ANALOGUE_DISPLAY = ItemStackBuilder.pylonItem(Material.LIME_STAINED_GLASS_PANE, BaseKeys.ANALOGUE_DISPLAY)
            .build();
    static {
        PylonItem.register(PylonItem.class, ANALOGUE_DISPLAY);
        BasePages.COMPONENTS.addItem(ANALOGUE_DISPLAY);
    }

    public static final ItemStack FILTER_MESH = ItemStackBuilder.pylonItem(Material.IRON_BARS, BaseKeys.FILTER_MESH)
            .build();
    static {
        PylonItem.register(PylonItem.class, FILTER_MESH);
        BasePages.COMPONENTS.addItem(FILTER_MESH);
    }

    public static final ItemStack NOZZLE = ItemStackBuilder.pylonItem(Material.LEVER, BaseKeys.NOZZLE)
            .build();
    static {
        PylonItem.register(PylonItem.class, NOZZLE);
        BasePages.COMPONENTS.addItem(NOZZLE);
    }

    public static final ItemStack ABYSSAL_CATALYST = ItemStackBuilder.pylonItem(Material.BLACK_CANDLE, BaseKeys.ABYSSAL_CATALYST)
            .build();
    static {
        PylonItem.register(PylonItem.class, ABYSSAL_CATALYST);
        BasePages.COMPONENTS.addItem(ABYSSAL_CATALYST);
    }

    public static final ItemStack HYDRAULIC_MOTOR = ItemStackBuilder.pylonItem(Material.PISTON, BaseKeys.HYDRAULIC_MOTOR)
            .build();
    static {
        PylonItem.register(PylonItem.class, HYDRAULIC_MOTOR);
        BasePages.COMPONENTS.addItem(HYDRAULIC_MOTOR);
    }

    public static final ItemStack AXLE = ItemStackBuilder.pylonItem(Material.OAK_FENCE, BaseKeys.AXLE)
            .build();
    static {
        PylonItem.register(PylonItem.class, AXLE);
        BasePages.COMPONENTS.addItem(AXLE);
    }

    public static final ItemStack SAWBLADE = ItemStackBuilder.pylonItem(Material.IRON_BARS, BaseKeys.SAWBLADE)
            .build();
    static {
        PylonItem.register(PylonItem.class, SAWBLADE);
        BasePages.COMPONENTS.addItem(SAWBLADE);
    }

    public static final ItemStack WEIGHTED_SHAFT = ItemStackBuilder.pylonItem(Material.DEEPSLATE_TILE_WALL, BaseKeys.WEIGHTED_SHAFT)
            .build();
    static {
        PylonItem.register(PylonItem.class, WEIGHTED_SHAFT);
        BasePages.COMPONENTS.addItem(WEIGHTED_SHAFT);
    }

    public static final ItemStack COPPER_DRILL_BIT = ItemStackBuilder.pylonItem(Material.LIGHTNING_ROD, BaseKeys.COPPER_DRILL_BIT)
            .build();
    static {
        PylonItem.register(PylonItem.class, COPPER_DRILL_BIT);
        BasePages.COMPONENTS.addItem(COPPER_DRILL_BIT);
    }

    public static final ItemStack BRONZE_DRILL_BIT = ItemStackBuilder.pylonItem(Material.LIGHTNING_ROD, BaseKeys.BRONZE_DRILL_BIT)
            .build();
    static {
        PylonItem.register(PylonItem.class, BRONZE_DRILL_BIT);
        BasePages.COMPONENTS.addItem(BRONZE_DRILL_BIT);
    }

    public static final ItemStack WATER_PUMP = ItemStackBuilder.pylonItem(Material.BLUE_TERRACOTTA, BaseKeys.WATER_PUMP)
            .build();
    static {
        PylonItem.register(WaterPump.Item.class, WATER_PUMP, BaseKeys.WATER_PUMP);
        BasePages.FLUID_MACHINES.addItem(WATER_PUMP);
    }

    public static final ItemStack FLUID_VALVE = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VALVE)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_VALVE, BaseKeys.FLUID_VALVE);
        BasePages.FLUID_MACHINES.addItem(FLUID_VALVE);
    }

    public static final ItemStack FLUID_FILTER = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_FILTER)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_FILTER, BaseKeys.FLUID_FILTER);
        BasePages.FLUID_MACHINES.addItem(FLUID_FILTER);
    }

    public static final ItemStack FLUID_METER = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_METER)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_METER, BaseKeys.FLUID_METER);
        BasePages.FLUID_MACHINES.addItem(FLUID_METER);
    }

    public static final ItemStack WATER_PLACER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.WATER_PLACER)
            .build();
    static {
        PylonItem.register(FluidPlacer.Item.class, WATER_PLACER, BaseKeys.WATER_PLACER);
        BasePages.FLUID_MACHINES.addItem(WATER_PLACER);
    }

    public static final ItemStack LAVA_PLACER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.LAVA_PLACER)
            .build();
    static {
        PylonItem.register(FluidPlacer.Item.class, LAVA_PLACER, BaseKeys.LAVA_PLACER);
        BasePages.FLUID_MACHINES.addItem(LAVA_PLACER);
    }

    public static final ItemStack WATER_DRAINER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.WATER_DRAINER)
            .build();
    static {
        PylonItem.register(FluidDrainer.Item.class, WATER_DRAINER, BaseKeys.WATER_DRAINER);
        BasePages.FLUID_MACHINES.addItem(WATER_DRAINER);
    }

    public static final ItemStack LAVA_DRAINER = ItemStackBuilder.pylonItem(Material.DISPENSER, BaseKeys.LAVA_DRAINER)
            .build();
    static {
        PylonItem.register(FluidDrainer.Item.class, LAVA_DRAINER, BaseKeys.LAVA_DRAINER);
        BasePages.FLUID_MACHINES.addItem(LAVA_DRAINER);
    }

    public static final ItemStack FLUID_VOIDER_1 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_1)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_1, BaseKeys.FLUID_VOIDER_1);
        BasePages.FLUID_MACHINES.addItem(FLUID_VOIDER_1);
    }

    public static final ItemStack FLUID_VOIDER_2 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_2)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_2, BaseKeys.FLUID_VOIDER_2);
        BasePages.FLUID_MACHINES.addItem(FLUID_VOIDER_2);
    }

    public static final ItemStack FLUID_VOIDER_3 = ItemStackBuilder.pylonItem(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_3)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        PylonItem.register(FluidVoider.Item.class, FLUID_VOIDER_3, BaseKeys.FLUID_VOIDER_3);
        BasePages.FLUID_MACHINES.addItem(FLUID_VOIDER_3);
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

    public static final ItemStack LOUPE = ItemStackBuilder.pylonItem(Material.CLAY_BALL, BaseKeys.LOUPE)
            .set(DataComponentTypes.ITEM_MODEL, Material.GLASS_PANE.getKey())
            .set(DataComponentTypes.CONSUMABLE, io.papermc.paper.datacomponent.item.Consumable.consumable()
                    .animation(ItemUseAnimation.SPYGLASS)
                    .hasConsumeParticles(false)
                    .consumeSeconds(3)
                    .sound(Registry.SOUNDS.getKey(Sound.BLOCK_AMETHYST_CLUSTER_HIT))
            )
            .set(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(1)
                    .cooldownGroup(BaseKeys.LOUPE.key()))
            .build();
    static {
        PylonItem.register(Loupe.class, LOUPE);
        BasePages.SCIENCE.addItem(LOUPE);
    }

    public static final ItemStack RESEARCH_PACK_1 = ItemStackBuilder.pylonItem(Material.RED_BANNER, BaseKeys.RESEARCH_PACK_1)
            .set(DataComponentTypes.MAX_STACK_SIZE, 3)
            .set(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(
                    Settings.get(BaseKeys.RESEARCH_PACK_1).getOrThrow("cooldown-ticks", ConfigAdapter.INT) / 20f
                )
                .cooldownGroup(BaseKeys.RESEARCH_PACK_1.key())
                .build())
            .build();
    static {
        PylonItem.register(ResearchPack.class, RESEARCH_PACK_1);
        BasePages.SCIENCE.addItem(RESEARCH_PACK_1);
    }

    public static final ItemStack RESEARCH_PACK_2 = ItemStackBuilder.pylonItem(Material.LIME_BANNER, BaseKeys.RESEARCH_PACK_2)
            .set(DataComponentTypes.MAX_STACK_SIZE, 3)
            .set(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(
                    Settings.get(BaseKeys.RESEARCH_PACK_2).getOrThrow("cooldown-ticks", ConfigAdapter.INT) / 20f
                )
                .cooldownGroup(BaseKeys.RESEARCH_PACK_2.key())
                .build())
            .build();
    static {
        PylonItem.register(ResearchPack.class, RESEARCH_PACK_2);
        BasePages.SCIENCE.addItem(RESEARCH_PACK_2);
    }

    public static final ItemStack FLUID_STRAINER = ItemStackBuilder.pylonItem(Material.COPPER_GRATE, BaseKeys.FLUID_STRAINER)
            .build();
    static {
        PylonItem.register(PylonItem.class, FLUID_STRAINER, BaseKeys.FLUID_STRAINER);
        BasePages.FLUID_MACHINES.addItem(FLUID_STRAINER);
    }

    public static final ItemStack SPRINKLER = ItemStackBuilder.pylonItem(Material.FLOWER_POT, BaseKeys.SPRINKLER)
            .build();
    static {
        PylonItem.register(Sprinkler.Item.class, SPRINKLER, BaseKeys.SPRINKLER);
        BasePages.FLUID_MACHINES.addItem(SPRINKLER);
    }

    //<editor-fold desc="Smeltery" defaultstate="collapsed">
    public static final ItemStack REFRACTORY_MIX = ItemStackBuilder.pylonItem(Material.SMOOTH_RED_SANDSTONE, BaseKeys.REFRACTORY_MIX)
            .build();
    static {
        PylonItem.register(PylonItem.class, REFRACTORY_MIX, BaseKeys.REFRACTORY_MIX);
        BasePages.RESOURCES.addItem(REFRACTORY_MIX);
    }

    public static final ItemStack UNFIRED_REFRACTORY_BRICK = ItemStackBuilder.pylonItem(Material.BRICK, BaseKeys.UNFIRED_REFRACTORY_BRICK)
            .build();
    static {
        PylonItem.register(PylonItem.class, UNFIRED_REFRACTORY_BRICK, BaseKeys.UNFIRED_REFRACTORY_BRICK);
        BasePages.RESOURCES.addItem(UNFIRED_REFRACTORY_BRICK);
    }

    public static final ItemStack REFRACTORY_BRICK = ItemStackBuilder.pylonItem(Material.NETHERITE_INGOT, BaseKeys.REFRACTORY_BRICK)
            .build();
    static {
        PylonItem.register(PylonItem.class, REFRACTORY_BRICK, BaseKeys.REFRACTORY_BRICK);
        BasePages.RESOURCES.addItem(REFRACTORY_BRICK);
    }

    public static final ItemStack REFRACTORY_BRICKS = ItemStackBuilder.pylonItem(Material.DEEPSLATE_TILES, BaseKeys.REFRACTORY_BRICKS)
            .build();
    static {
        PylonItem.register(PylonItem.class, REFRACTORY_BRICKS, BaseKeys.REFRACTORY_BRICKS);
        BasePages.SMELTING.addItem(REFRACTORY_BRICKS);
    }

    public static final ItemStack SMELTERY_CONTROLLER = ItemStackBuilder.pylonItem(Material.BLAST_FURNACE, BaseKeys.SMELTERY_CONTROLLER)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_CONTROLLER, BaseKeys.SMELTERY_CONTROLLER);
        BasePages.SMELTING.addItem(SMELTERY_CONTROLLER);
    }

    public static final ItemStack SMELTERY_INPUT_HATCH = ItemStackBuilder.pylonItem(Material.LIGHT_BLUE_TERRACOTTA, BaseKeys.SMELTERY_INPUT_HATCH)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_INPUT_HATCH, BaseKeys.SMELTERY_INPUT_HATCH);
        BasePages.SMELTING.addItem(SMELTERY_INPUT_HATCH);
    }

    public static final ItemStack SMELTERY_OUTPUT_HATCH = ItemStackBuilder.pylonItem(Material.ORANGE_TERRACOTTA, BaseKeys.SMELTERY_OUTPUT_HATCH)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_OUTPUT_HATCH, BaseKeys.SMELTERY_OUTPUT_HATCH);
        BasePages.SMELTING.addItem(SMELTERY_OUTPUT_HATCH);
    }

    public static final ItemStack SMELTERY_HOPPER = ItemStackBuilder.pylonItem(Material.HOPPER, BaseKeys.SMELTERY_HOPPER)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_HOPPER, BaseKeys.SMELTERY_HOPPER);
        BasePages.SMELTING.addItem(SMELTERY_HOPPER);
    }

    public static final ItemStack SMELTERY_CASTER = ItemStackBuilder.pylonItem(Material.BRICKS, BaseKeys.SMELTERY_CASTER)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_CASTER, BaseKeys.SMELTERY_CASTER);
        BasePages.SMELTING.addItem(SMELTERY_CASTER);
    }

    public static final ItemStack SMELTERY_BURNER = ItemStackBuilder.pylonItem(Material.FURNACE, BaseKeys.SMELTERY_BURNER)
            .build();
    static {
        PylonItem.register(PylonItem.class, SMELTERY_BURNER, BaseKeys.SMELTERY_BURNER);
        BasePages.SMELTING.addItem(SMELTERY_BURNER);
    }

    public static final ItemStack PIT_KILN = ItemStackBuilder.pylonItem(Material.DECORATED_POT, BaseKeys.PIT_KILN)
            .build();
    static {
        PylonItem.register(PitKiln.Item.class, PIT_KILN, BaseKeys.PIT_KILN);
        BasePages.SMELTING.addItem(PIT_KILN);
    }
    // </editor-fold>

    public static final ItemStack EXPLOSIVE_TARGET = ItemStackBuilder.pylonItem(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET)
            .build();
    static {
        PylonItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET, BaseKeys.EXPLOSIVE_TARGET);
        BasePages.BUILDING.addItem(EXPLOSIVE_TARGET);
    }

    public static final ItemStack EXPLOSIVE_TARGET_FIERY = ItemStackBuilder.pylonItem(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET_FIERY)
            .build();
    static {
        PylonItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET_FIERY, BaseKeys.EXPLOSIVE_TARGET_FIERY);
        BasePages.BUILDING.addItem(EXPLOSIVE_TARGET_FIERY);
    }

    public static final ItemStack EXPLOSIVE_TARGET_SUPER = ItemStackBuilder.pylonItem(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET_SUPER)
            .build();
    static {
        PylonItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET_SUPER, BaseKeys.EXPLOSIVE_TARGET_SUPER);
        BasePages.BUILDING.addItem(EXPLOSIVE_TARGET_SUPER);
    }

    public static final ItemStack EXPLOSIVE_TARGET_SUPER_FIERY = ItemStackBuilder.pylonItem(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY)
            .build();
    static {
        PylonItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET_SUPER_FIERY, BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY);
        BasePages.BUILDING.addItem(EXPLOSIVE_TARGET_SUPER_FIERY);
    }

    public static final ItemStack IMMOBILIZER = ItemStackBuilder.pylonItem(Material.PISTON, BaseKeys.IMMOBILIZER)
            .build();
    static {
        PylonItem.register(Immobilizer.Item.class, IMMOBILIZER, BaseKeys.IMMOBILIZER);
        BasePages.BUILDING.addItem(IMMOBILIZER);
    }

    public static final ItemStack ELEVATOR_1 = ItemStackBuilder.pylonItem(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_1)
            .build();
    static {
        PylonItem.register(Elevator.Item.class, ELEVATOR_1, BaseKeys.ELEVATOR_1);
        BasePages.BUILDING.addItem(ELEVATOR_1);
    }

    public static final ItemStack ELEVATOR_2 = ItemStackBuilder.pylonItem(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_2)
            .build();
    static {
        PylonItem.register(Elevator.Item.class, ELEVATOR_2, BaseKeys.ELEVATOR_2);
        BasePages.BUILDING.addItem(ELEVATOR_2);
    }

    public static final ItemStack ELEVATOR_3 = ItemStackBuilder.pylonItem(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_3)
            .build();
    static {
        PylonItem.register(Elevator.Item.class, ELEVATOR_3, BaseKeys.ELEVATOR_3);
        BasePages.BUILDING.addItem(ELEVATOR_3);
    }

    public static final ItemStack PRESS = ItemStackBuilder.pylonItem(Material.COMPOSTER, BaseKeys.PRESS)
            .build();
    static {
        PylonItem.register(Press.PressItem.class, PRESS, BaseKeys.PRESS);
        BasePages.SIMPLE_MACHINES.addItem(PRESS);
    }

    public static final ItemStack HYDRAULIC_GRINDSTONE_TURNER = ItemStackBuilder.pylonItem(Material.SMOOTH_STONE, BaseKeys.HYDRAULIC_GRINDSTONE_TURNER)
            .build();
    static {
        PylonItem.register(HydraulicGrindstoneTurner.Item.class, HYDRAULIC_GRINDSTONE_TURNER, BaseKeys.HYDRAULIC_GRINDSTONE_TURNER);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_GRINDSTONE_TURNER);
    }

    public static final ItemStack HYDRAULIC_MIXING_ATTACHMENT = ItemStackBuilder.pylonItem(Material.CHISELED_STONE_BRICKS, BaseKeys.HYDRAULIC_MIXING_ATTACHMENT)
            .build();
    static {
        PylonItem.register(HydraulicMixingAttachment.Item.class, HYDRAULIC_MIXING_ATTACHMENT, BaseKeys.HYDRAULIC_MIXING_ATTACHMENT);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_MIXING_ATTACHMENT);
    }

    public static final ItemStack HYDRAULIC_PRESS_PISTON = ItemStackBuilder.pylonItem(Material.BROWN_TERRACOTTA, BaseKeys.HYDRAULIC_PRESS_PISTON)
            .build();
    static {
        PylonItem.register(HydraulicPressPiston.Item.class, HYDRAULIC_PRESS_PISTON, BaseKeys.HYDRAULIC_PRESS_PISTON);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_PRESS_PISTON);
    }

    public static final ItemStack HYDRAULIC_HAMMER_HEAD = ItemStackBuilder.pylonItem(Material.STONE_BRICKS, BaseKeys.HYDRAULIC_HAMMER_HEAD)
            .build();
    static {
        PylonItem.register(HydraulicHammerHead.Item.class, HYDRAULIC_HAMMER_HEAD, BaseKeys.HYDRAULIC_HAMMER_HEAD);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_HAMMER_HEAD);
    }

    public static final ItemStack HYDRAULIC_PIPE_BENDER = ItemStackBuilder.pylonItem(Material.WAXED_CHISELED_COPPER, BaseKeys.HYDRAULIC_PIPE_BENDER)
            .build();
    static {
        PylonItem.register(HydraulicPipeBender.Item.class, HYDRAULIC_PIPE_BENDER, BaseKeys.HYDRAULIC_PIPE_BENDER);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_PIPE_BENDER);
    }

    public static final ItemStack HYDRAULIC_TABLE_SAW = ItemStackBuilder.pylonItem(Material.WAXED_CUT_COPPER, BaseKeys.HYDRAULIC_TABLE_SAW)
            .build();
    static {
        PylonItem.register(HydraulicTableSaw.Item.class, HYDRAULIC_TABLE_SAW, BaseKeys.HYDRAULIC_TABLE_SAW);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_TABLE_SAW);
    }

    public static final ItemStack SOLAR_LENS = ItemStackBuilder.pylonItem(Material.GLASS_PANE, BaseKeys.SOLAR_LENS)
            .build();
    static {
        PylonItem.register(PylonItem.class, SOLAR_LENS, BaseKeys.SOLAR_LENS);
        BasePages.HYDRAULICS.addItem(SOLAR_LENS);
    }

    public static final ItemStack PURIFICATION_TOWER_GLASS = ItemStackBuilder.pylonItem(Material.LIGHT_GRAY_STAINED_GLASS, BaseKeys.PURIFICATION_TOWER_GLASS)
            .build();
    static {
        PylonItem.register(PylonItem.class, PURIFICATION_TOWER_GLASS, BaseKeys.PURIFICATION_TOWER_GLASS);
        BasePages.HYDRAULICS.addItem(PURIFICATION_TOWER_GLASS);
    }

    public static final ItemStack PURIFICATION_TOWER_CAP = ItemStackBuilder.pylonItem(Material.QUARTZ_SLAB, BaseKeys.PURIFICATION_TOWER_CAP)
            .build();
    static {
        PylonItem.register(PylonItem.class, PURIFICATION_TOWER_CAP, BaseKeys.PURIFICATION_TOWER_CAP);
        BasePages.HYDRAULICS.addItem(PURIFICATION_TOWER_CAP);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_1 = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_1)
            .build();
    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_1, BaseKeys.SOLAR_PURIFICATION_TOWER_1);
        BasePages.HYDRAULICS.addItem(SOLAR_PURIFICATION_TOWER_1);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_2 = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_2)
            .build();
    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_2, BaseKeys.SOLAR_PURIFICATION_TOWER_2);
        BasePages.HYDRAULICS.addItem(SOLAR_PURIFICATION_TOWER_2);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_3 = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_3)
            .build();
    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_3, BaseKeys.SOLAR_PURIFICATION_TOWER_3);
        BasePages.HYDRAULICS.addItem(SOLAR_PURIFICATION_TOWER_3);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_4 = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_4)
            .build();
    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_4, BaseKeys.SOLAR_PURIFICATION_TOWER_4);
        BasePages.HYDRAULICS.addItem(SOLAR_PURIFICATION_TOWER_4);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_5 = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_5)
            .build();
    static {
        PylonItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_5, BaseKeys.SOLAR_PURIFICATION_TOWER_5);
        BasePages.HYDRAULICS.addItem(SOLAR_PURIFICATION_TOWER_5);
    }

    public static final ItemStack COAL_FIRED_PURIFICATION_TOWER = ItemStackBuilder.pylonItem(Material.BLAST_FURNACE, BaseKeys.COAL_FIRED_PURIFICATION_TOWER)
            .build();
    static {
        PylonItem.register(CoalFiredPurificationTower.Item.class, COAL_FIRED_PURIFICATION_TOWER, BaseKeys.COAL_FIRED_PURIFICATION_TOWER);
        BasePages.HYDRAULICS.addItem(COAL_FIRED_PURIFICATION_TOWER);
    }

    public static final ItemStack ICE_ARROW = ItemStackBuilder.pylonItem(Material.ARROW, BaseKeys.ICE_ARROW).build();
    static {
        PylonItem.register(IceArrow.class, ICE_ARROW, BaseKeys.ICE_ARROW);
        BasePages.COMBAT.addItem(ICE_ARROW);
    }

    public static final ItemStack RECOIL_ARROW = ItemStackBuilder.pylonItem(Material.ARROW, BaseKeys.RECOIL_ARROW)
            .build();
    static {
        PylonItem.register(RecoilArrow.class, RECOIL_ARROW);
        BasePages.COMBAT.addItem(RECOIL_ARROW);
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
        BasePages.TOOLS.addItem(FIREPROOF_RUNE);
    }


    public static final ItemStack SHALLOW_CORE_CHUNK = ItemStackBuilder.pylonItem(Material.FIREWORK_STAR, BaseKeys.SHALLOW_CORE_CHUNK)
            .build();
    static {
        PylonItem.register(PylonItem.class, SHALLOW_CORE_CHUNK, BaseKeys.SHALLOW_CORE_CHUNK);
        BasePages.RESOURCES.addItem(SHALLOW_CORE_CHUNK);
    }

    public static final ItemStack SUBSURFACE_CORE_CHUNK = ItemStackBuilder.pylonItem(Material.FIREWORK_STAR, BaseKeys.SUBSURFACE_CORE_CHUNK)
            .build();
    static {
        PylonItem.register(PylonItem.class, SUBSURFACE_CORE_CHUNK, BaseKeys.SUBSURFACE_CORE_CHUNK);
        BasePages.RESOURCES.addItem(SUBSURFACE_CORE_CHUNK);
    }

    public static final ItemStack INTERMEDIATE_CORE_CHUNK = ItemStackBuilder.pylonItem(Material.FIREWORK_STAR, BaseKeys.INTERMEDIATE_CORE_CHUNK)
            .build();
    static {
        PylonItem.register(PylonItem.class, INTERMEDIATE_CORE_CHUNK, BaseKeys.INTERMEDIATE_CORE_CHUNK);
        BasePages.RESOURCES.addItem(INTERMEDIATE_CORE_CHUNK);
    }

    public static final ItemStack MANUAL_CORE_DRILL_LEVER = ItemStackBuilder.pylonItem(Material.LEVER, BaseKeys.MANUAL_CORE_DRILL_LEVER)
            .build();
    static {
        PylonItem.register(PylonItem.class, MANUAL_CORE_DRILL_LEVER, BaseKeys.MANUAL_CORE_DRILL_LEVER);
        BasePages.SIMPLE_MACHINES.addItem(MANUAL_CORE_DRILL_LEVER);
    }

    public static final ItemStack MANUAL_CORE_DRILL = ItemStackBuilder.pylonItem(Material.CHISELED_STONE_BRICKS, BaseKeys.MANUAL_CORE_DRILL)
            .build();
    static {
        PylonItem.register(CoreDrill.Item.class, MANUAL_CORE_DRILL, BaseKeys.MANUAL_CORE_DRILL);
        BasePages.SIMPLE_MACHINES.addItem(MANUAL_CORE_DRILL);
    }

    public static final ItemStack IMPROVED_MANUAL_CORE_DRILL = ItemStackBuilder.pylonItem(Material.WAXED_OXIDIZED_COPPER, BaseKeys.IMPROVED_MANUAL_CORE_DRILL)
            .build();
    static {
        PylonItem.register(ImprovedManualCoreDrill.Item.class, IMPROVED_MANUAL_CORE_DRILL, BaseKeys.IMPROVED_MANUAL_CORE_DRILL);
        BasePages.SIMPLE_MACHINES.addItem(IMPROVED_MANUAL_CORE_DRILL);
    }

    public static final ItemStack HYDRAULIC_CORE_DRILL = ItemStackBuilder.pylonItem(Material.WAXED_COPPER_BULB, BaseKeys.HYDRAULIC_CORE_DRILL)
            .build();
    static {
        PylonItem.register(HydraulicCoreDrill.Item.class, HYDRAULIC_CORE_DRILL, BaseKeys.HYDRAULIC_CORE_DRILL);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_CORE_DRILL);
    }

    public static final ItemStack HYDRAULIC_CORE_DRILL_INPUT_HATCH = ItemStackBuilder.pylonItem(Material.LIGHT_BLUE_TERRACOTTA, BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH)
            .build();
    static {
        PylonItem.register(PylonItem.class, HYDRAULIC_CORE_DRILL_INPUT_HATCH, BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_CORE_DRILL_INPUT_HATCH);
    }

    public static final ItemStack HYDRAULIC_CORE_DRILL_OUTPUT_HATCH = ItemStackBuilder.pylonItem(Material.ORANGE_TERRACOTTA, BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH)
            .build();
    static {
        PylonItem.register(PylonItem.class, HYDRAULIC_CORE_DRILL_OUTPUT_HATCH, BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_CORE_DRILL_OUTPUT_HATCH);
    }

    public static final ItemStack REACTIVATED_WITHER_SKULL = ItemStackBuilder.pylonItem(Material.WITHER_SKELETON_SKULL, BaseKeys.REACTIVATED_WITHER_SKULL)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.REACTIVATED_WITHER_SKULL).getOrThrow("durability", ConfigAdapter.INT))
            .set(DataComponentTypes.DAMAGE, 0)
            .set(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(Settings.get(BaseKeys.REACTIVATED_WITHER_SKULL).getOrThrow("cooldown-ticks", ConfigAdapter.INT))
                    .cooldownGroup(BaseKeys.REACTIVATED_WITHER_SKULL)
                    .build())
            .build();
    static {
        PylonItem.register(ReactivatedWitherSkull.class, REACTIVATED_WITHER_SKULL);
        BasePages.COMBAT.addItem(REACTIVATED_WITHER_SKULL);
    }

    public static final ItemStack HYPER_ACTIVATED_WITHER_SKULL = ItemStackBuilder.pylonItem(Material.WITHER_SKELETON_SKULL, BaseKeys.HYPER_ACTIVATED_WITHER_SKULL)
            .set(DataComponentTypes.MAX_DAMAGE, Settings.get(BaseKeys.HYPER_ACTIVATED_WITHER_SKULL).getOrThrow("durability", ConfigAdapter.INT))
            .set(DataComponentTypes.DAMAGE, 0)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .set(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(Settings.get(BaseKeys.HYPER_ACTIVATED_WITHER_SKULL).getOrThrow("cooldown-ticks", ConfigAdapter.INT))
                    .cooldownGroup(BaseKeys.HYPER_ACTIVATED_WITHER_SKULL)
                    .build())
            .build();
    static {
        PylonItem.register(ReactivatedWitherSkull.class, HYPER_ACTIVATED_WITHER_SKULL);
        BasePages.COMBAT.addItem(HYPER_ACTIVATED_WITHER_SKULL);
    }


    public static final ItemStack CLEANSING_POTION = ItemStackBuilder.pylonItem(Material.SPLASH_POTION, BaseKeys.CLEANSING_POTION)
            .set(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents()
                    .customColor(Color.FUCHSIA)
                    .build())
            .build();
    static {
        PylonItem.register(CleansingPotion.class, CLEANSING_POTION);
        BasePages.TOOLS.addItem(CLEANSING_POTION);

        // This recipe isn't configged because we current have no way to set the healing potion data on it
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

    public static final ItemStack CLIMBING_PICK = ItemStackBuilder.pylonItem(Material.DIAMOND_HOE, BaseKeys.CLIMBING_PICK)
            .build();
    static {
        PylonItem.register(ClimbingPick.class, CLIMBING_PICK);
        BasePages.TOOLS.addItem(CLIMBING_PICK);
    }

    public static final ItemStack VACUUM_HOPPER_1 = ItemStackBuilder.pylonItem(Material.HOPPER, BaseKeys.VACUUM_HOPPER_1)
            .build();
    static {
        PylonItem.register(VacuumHopper.Item.class, VACUUM_HOPPER_1, BaseKeys.VACUUM_HOPPER_1);
        BasePages.SIMPLE_MACHINES.addItem(VACUUM_HOPPER_1);
    }

    public static final ItemStack VACUUM_HOPPER_2 = ItemStackBuilder.pylonItem(Material.HOPPER, BaseKeys.VACUUM_HOPPER_2)
            .build();
    static {
        PylonItem.register(VacuumHopper.Item.class, VACUUM_HOPPER_2, BaseKeys.VACUUM_HOPPER_2);
        BasePages.SIMPLE_MACHINES.addItem(VACUUM_HOPPER_2);
    }

    public static final ItemStack VACUUM_HOPPER_3 = ItemStackBuilder.pylonItem(Material.HOPPER, BaseKeys.VACUUM_HOPPER_3)
            .build();
    static {
        PylonItem.register(VacuumHopper.Item.class, VACUUM_HOPPER_3, BaseKeys.VACUUM_HOPPER_3);
        BasePages.SIMPLE_MACHINES.addItem(VACUUM_HOPPER_3);
    }

    public static final ItemStack VACUUM_HOPPER_4 = ItemStackBuilder.pylonItem(Material.HOPPER, BaseKeys.VACUUM_HOPPER_4)
            .build();
    static {
        PylonItem.register(VacuumHopper.Item.class, VACUUM_HOPPER_4, BaseKeys.VACUUM_HOPPER_4);
        BasePages.SIMPLE_MACHINES.addItem(VACUUM_HOPPER_4);
    }

    public static final ItemStack HYDRAULIC_CANNON = ItemStackBuilder.pylonItem(Material.IRON_HORSE_ARMOR, BaseKeys.HYDRAULIC_CANNON)
            .set(DataComponentTypes.USE_COOLDOWN, UseCooldown
                    .useCooldown(
                            Settings.get(BaseKeys.HYDRAULIC_CANNON).getOrThrow("cooldown-ticks", ConfigAdapter.INT) / 20.0F
                    )
                    .cooldownGroup(BaseKeys.HYDRAULIC_CANNON.key())
                    .build())
            .editPdc(pdc -> {
                pdc.set(BaseFluids.HYDRAULIC_FLUID.getKey(), PylonSerializers.DOUBLE, 0.0);
                pdc.set(BaseFluids.DIRTY_HYDRAULIC_FLUID.getKey(), PylonSerializers.DOUBLE, 0.0);
            })
            .build();
    static {
        PylonItem.register(HydraulicCannon.class, HYDRAULIC_CANNON);
        BasePages.COMBAT.addItem(HYDRAULIC_CANNON);
    }

    public static final ItemStack HYDRAULIC_CANNON_CHAMBER = ItemStackBuilder.pylonItem(Material.SNOWBALL, BaseKeys.HYDRAULIC_CANNON_CHAMBER)
            .build();
    static {
        PylonItem.register(PylonItem.class, HYDRAULIC_CANNON_CHAMBER);
        BasePages.COMPONENTS.addItem(HYDRAULIC_CANNON_CHAMBER);
    }

    public static final ItemStack TIN_PROJECTILE = ItemStackBuilder.pylonItem(Material.IRON_NUGGET, BaseKeys.TIN_PROJECTILE)
            .build();
    static {
        PylonItem.register(PylonItem.class, TIN_PROJECTILE);
        BasePages.COMBAT.addItem(TIN_PROJECTILE);
    }

    public static final ItemStack HYDRAULIC_REFUELING_STATION = ItemStackBuilder.pylonItem(Material.WAXED_CUT_COPPER_SLAB, BaseKeys.HYDRAULIC_REFUELING_STATION)
            .build();
    static {
        PylonItem.register(PylonItem.class, HYDRAULIC_REFUELING_STATION, BaseKeys.HYDRAULIC_REFUELING_STATION);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_REFUELING_STATION);
    }

    public static final ItemStack HYDRAULIC_EXCAVATOR = ItemStackBuilder.pylonItem(Material.WAXED_EXPOSED_CHISELED_COPPER, BaseKeys.HYDRAULIC_EXCAVATOR)
            .build();
    static {
        PylonItem.register(HydraulicExcavator.Item.class, HYDRAULIC_EXCAVATOR, BaseKeys.HYDRAULIC_EXCAVATOR);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_EXCAVATOR);
    }

    public static final ItemStack HYDRAULIC_FARMER = ItemStackBuilder.pylonItem(Material.WAXED_EXPOSED_COPPER_BULB, BaseKeys.HYDRAULIC_FARMER)
            .build();
    static {
        PylonItem.register(HydraulicFarmer.Item.class, HYDRAULIC_FARMER, BaseKeys.HYDRAULIC_FARMER);
        BasePages.HYDRAULICS.addItem(HYDRAULIC_FARMER);
    }

    // Calling this method forces all the static blocks to run, which initializes our items
    public static void initialize() {
    }
}
