package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.content.armor.BronzeArmor;
import io.github.pylonmc.pylon.base.content.building.Elevator;
import io.github.pylonmc.pylon.base.content.building.ExplosiveTarget;
import io.github.pylonmc.pylon.base.content.building.Immobilizer;
import io.github.pylonmc.pylon.base.content.combat.BeheadingSword;
import io.github.pylonmc.pylon.base.content.combat.IceArrow;
import io.github.pylonmc.pylon.base.content.combat.ReactivatedWitherSkull;
import io.github.pylonmc.pylon.base.content.combat.RecoilArrow;
import io.github.pylonmc.pylon.base.content.machines.cargo.*;
import io.github.pylonmc.pylon.base.content.machines.diesel.machines.*;
import io.github.pylonmc.pylon.base.content.machines.diesel.production.Biorefinery;
import io.github.pylonmc.pylon.base.content.machines.diesel.production.Fermenter;
import io.github.pylonmc.pylon.base.content.machines.fluid.*;
import io.github.pylonmc.pylon.base.content.machines.hydraulics.*;
import io.github.pylonmc.pylon.base.content.machines.simple.*;
import io.github.pylonmc.pylon.base.content.machines.smelting.DieselSmelteryHeater;
import io.github.pylonmc.pylon.base.content.machines.smelting.PitKiln;
import io.github.pylonmc.pylon.base.content.resources.IronBloom;
import io.github.pylonmc.pylon.base.content.science.Loupe;
import io.github.pylonmc.pylon.base.content.science.ResearchPack;
import io.github.pylonmc.pylon.base.content.talismans.*;
import io.github.pylonmc.pylon.base.content.tools.*;
import io.github.pylonmc.rebar.config.Settings;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.content.fluid.FluidPipe;
import io.github.pylonmc.rebar.content.guide.RebarGuide;
import io.github.pylonmc.rebar.datatypes.RebarSerializers;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import io.github.pylonmc.rebar.recipe.RecipeType;
import io.github.pylonmc.rebar.util.RebarUtils;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.*;
import io.papermc.paper.datacomponent.item.consumable.ConsumeEffect;
import io.papermc.paper.datacomponent.item.consumable.ItemUseAnimation;
import io.papermc.paper.registry.keys.SoundEventKeys;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import net.kyori.adventure.key.Key;
import org.bukkit.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.recipe.CraftingBookCategory;
import org.bukkit.potion.PotionType;

import java.util.Objects;

@SuppressWarnings({"UnstableApiUsage", "OverlyComplexClass"})
public final class BaseItems {

    private BaseItems() {
        throw new AssertionError("Utility class");
    }

    //<editor-fold desc="Research" defaultstate=collapsed>

    public static final ItemStack LOUPE = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.LOUPE)
            .set(DataComponentTypes.ITEM_MODEL, Material.GLASS_PANE.getKey())
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .animation(ItemUseAnimation.SPYGLASS)
                    .hasConsumeParticles(false)
                    .consumeSeconds(Settings.get(BaseKeys.LOUPE).getOrThrow("use-ticks", ConfigAdapter.INT) / 20.0F)
                    .sound(SoundEventKeys.INTENTIONALLY_EMPTY)
            )
            .set(DataComponentTypes.USE_COOLDOWN, UseCooldown.useCooldown(
                            Settings.get(BaseKeys.LOUPE).getOrThrow("cooldown-ticks", ConfigAdapter.INT))
                    .cooldownGroup(BaseKeys.LOUPE)
            )
            .build();
    static {
        RebarItem.register(Loupe.class, LOUPE);
        BasePages.SCIENCE.addItem(LOUPE);
    }

    public static final ItemStack RESEARCH_PACK_1 = ItemStackBuilder.rebar(Material.RED_BANNER, BaseKeys.RESEARCH_PACK_1)
            .useCooldown(Settings.get(BaseKeys.RESEARCH_PACK_1).getOrThrow("cooldown-ticks", ConfigAdapter.INT), BaseKeys.RESEARCH_PACK_1)
            .set(DataComponentTypes.MAX_STACK_SIZE, 3)
            .build();
    static {
        RebarItem.register(ResearchPack.class, RESEARCH_PACK_1);
        BasePages.SCIENCE.addItem(RESEARCH_PACK_1);
    }

    public static final ItemStack RESEARCH_PACK_2 = ItemStackBuilder.rebar(Material.LIME_BANNER, BaseKeys.RESEARCH_PACK_2)
            .useCooldown(Settings.get(BaseKeys.RESEARCH_PACK_2).getOrThrow("cooldown-ticks", ConfigAdapter.INT), BaseKeys.RESEARCH_PACK_2)
            .set(DataComponentTypes.MAX_STACK_SIZE, 3)
            .build();
    static {
        RebarItem.register(ResearchPack.class, RESEARCH_PACK_2);
        BasePages.SCIENCE.addItem(RESEARCH_PACK_2);
    }

    //</editor-fold>

    //<editor-fold desc="Resources - Metals" defaultstate=collapsed>

    public static final ItemStack COPPER_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.COPPER_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.GLOWSTONE_DUST.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, COPPER_DUST);
        BasePages.METALS.addItem(COPPER_DUST);
    }

    public static final ItemStack CRUSHED_RAW_COPPER = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.CRUSHED_RAW_COPPER)
            .set(DataComponentTypes.ITEM_MODEL, Material.GLOWSTONE_DUST.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, CRUSHED_RAW_COPPER);
        BasePages.METALS.addItem(CRUSHED_RAW_COPPER);
    }

    public static final ItemStack IRON_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.IRON_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.GUNPOWDER.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, IRON_DUST);
        BasePages.METALS.addItem(IRON_DUST);
    }

    public static final ItemStack CRUSHED_RAW_IRON = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.CRUSHED_RAW_IRON)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, CRUSHED_RAW_IRON);
        BasePages.METALS.addItem(CRUSHED_RAW_IRON);
    }

    public static final ItemStack GOLD_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.GOLD_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.GLOWSTONE_DUST.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, GOLD_DUST);
        BasePages.METALS.addItem(GOLD_DUST);
    }

    public static final ItemStack CRUSHED_RAW_GOLD = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.CRUSHED_RAW_GOLD)
            .set(DataComponentTypes.ITEM_MODEL, Material.GLOWSTONE_DUST.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, CRUSHED_RAW_GOLD);
        BasePages.METALS.addItem(CRUSHED_RAW_GOLD);
    }


    public static final ItemStack RAW_TIN = ItemStackBuilder.rebar(Material.RAW_IRON, BaseKeys.RAW_TIN)
            .build();
    static {
        RebarItem.register(RebarItem.class, RAW_TIN);
        BasePages.METALS.addItem(RAW_TIN);
    }

    public static final ItemStack TIN_INGOT = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.TIN_INGOT)
            .set(DataComponentTypes.ITEM_MODEL, Material.IRON_INGOT.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, TIN_INGOT);
        BasePages.METALS.addItem(TIN_INGOT);
    }

    public static final ItemStack TIN_NUGGET = ItemStackBuilder.rebar(Material.IRON_NUGGET, BaseKeys.TIN_NUGGET)
            .build();
    static {
        RebarItem.register(RebarItem.class, TIN_NUGGET);
        BasePages.METALS.addItem(TIN_NUGGET);
    }

    public static final ItemStack TIN_BLOCK = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.TIN_BLOCK)
            .build();
    static {
        RebarItem.register(RebarItem.class, TIN_BLOCK, BaseKeys.TIN_BLOCK);
        BasePages.METALS.addItem(TIN_BLOCK);
    }

    public static final ItemStack CRUSHED_RAW_TIN = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.CRUSHED_RAW_TIN)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, CRUSHED_RAW_TIN);
        BasePages.METALS.addItem(CRUSHED_RAW_TIN);
    }

    public static final ItemStack TIN_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.TIN_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, TIN_DUST);
        BasePages.METALS.addItem(TIN_DUST);
    }

    public static final ItemStack BRONZE_INGOT = ItemStackBuilder.rebar(Material.COPPER_INGOT, BaseKeys.BRONZE_INGOT)
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_INGOT);
        BasePages.METALS.addItem(BRONZE_INGOT);
    }

    public static final ItemStack BRONZE_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.BRONZE_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.GLOWSTONE_DUST.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_DUST);
        BasePages.METALS.addItem(BRONZE_DUST);
    }

    public static final ItemStack BRONZE_NUGGET = ItemStackBuilder.rebar(Material.ARMADILLO_SCUTE, BaseKeys.BRONZE_NUGGET)
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_NUGGET);
        BasePages.METALS.addItem(BRONZE_NUGGET);
    }

    public static final ItemStack BRONZE_BLOCK = ItemStackBuilder.rebar(Material.COPPER_BLOCK, BaseKeys.BRONZE_BLOCK)
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_BLOCK, BaseKeys.BRONZE_BLOCK);
        BasePages.METALS.addItem(BRONZE_BLOCK);
    }

    public static final ItemStack SPONGE_IRON = ItemStackBuilder.rebar(Material.RAW_IRON, BaseKeys.SPONGE_IRON)
            .build();
    static {
        RebarItem.register(RebarItem.class, SPONGE_IRON);
        BasePages.METALS.addItem(SPONGE_IRON);
    }

    public static final ItemStack IRON_BLOOM = ItemStackBuilder.rebar(Material.RAW_IRON, BaseKeys.IRON_BLOOM)
            .build();
    static {
        RebarItem.register(IronBloom.class, IRON_BLOOM);
        BasePages.METALS.addItem(IRON_BLOOM);
    }

    public static final ItemStack WROUGHT_IRON = ItemStackBuilder.rebar(Material.NETHERITE_SCRAP, BaseKeys.WROUGHT_IRON)
            .build();
    static {
        RebarItem.register(RebarItem.class, WROUGHT_IRON);
        BasePages.METALS.addItem(WROUGHT_IRON);
    }

    public static final ItemStack STEEL_INGOT = ItemStackBuilder.rebar(Material.NETHERITE_INGOT, BaseKeys.STEEL_INGOT)
            .build();
    static {
        RebarItem.register(RebarItem.class, STEEL_INGOT);
        BasePages.METALS.addItem(STEEL_INGOT);
    }

    public static final ItemStack STEEL_NUGGET = ItemStackBuilder.rebar(Material.NETHERITE_SCRAP, BaseKeys.STEEL_NUGGET)
            .build();
    static {
        RebarItem.register(RebarItem.class, STEEL_NUGGET);
        BasePages.METALS.addItem(STEEL_NUGGET);
    }

    public static final ItemStack STEEL_BLOCK = ItemStackBuilder.rebar(Material.NETHERITE_BLOCK, BaseKeys.STEEL_BLOCK)
            .build();
    static {
        RebarItem.register(RebarItem.class, STEEL_BLOCK, BaseKeys.STEEL_BLOCK);
        BasePages.METALS.addItem(STEEL_BLOCK);
    }

    public static final ItemStack STEEL_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.STEEL_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.GUNPOWDER.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, STEEL_DUST);
        BasePages.METALS.addItem(STEEL_DUST);
    }

    public static final ItemStack NICKEL_INGOT = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.NICKEL_INGOT)
            .set(DataComponentTypes.ITEM_MODEL, Material.IRON_INGOT.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, NICKEL_INGOT);
        BasePages.METALS.addItem(NICKEL_INGOT);
    }

    public static final ItemStack NICKEL_NUGGET = ItemStackBuilder.rebar(Material.IRON_NUGGET, BaseKeys.NICKEL_NUGGET)
            .build();
    static {
        RebarItem.register(RebarItem.class, NICKEL_NUGGET);
        BasePages.METALS.addItem(NICKEL_NUGGET);
    }

    public static final ItemStack NICKEL_BLOCK = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.NICKEL_BLOCK)
            .build();
    static {
        RebarItem.register(RebarItem.class, NICKEL_BLOCK, BaseKeys.NICKEL_BLOCK);
        BasePages.METALS.addItem(NICKEL_BLOCK);
    }

    public static final ItemStack NICKEL_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.NICKEL_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, NICKEL_DUST);
        BasePages.METALS.addItem(NICKEL_DUST);
    }

    public static final ItemStack COBALT_INGOT = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.COBALT_INGOT)
            .set(DataComponentTypes.ITEM_MODEL, Material.IRON_INGOT.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, COBALT_INGOT);
        BasePages.METALS.addItem(COBALT_INGOT);
    }

    public static final ItemStack COBALT_NUGGET = ItemStackBuilder.rebar(Material.IRON_NUGGET, BaseKeys.COBALT_NUGGET)
            .build();
    static {
        RebarItem.register(RebarItem.class, COBALT_NUGGET);
        BasePages.METALS.addItem(COBALT_NUGGET);
    }

    public static final ItemStack COBALT_BLOCK = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.COBALT_BLOCK)
            .build();
    static {
        RebarItem.register(RebarItem.class, COBALT_BLOCK, BaseKeys.COBALT_BLOCK);
        BasePages.METALS.addItem(COBALT_BLOCK);
    }

    public static final ItemStack COBALT_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.COBALT_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, COBALT_DUST);
        BasePages.METALS.addItem(COBALT_DUST);
    }

    //</editor-fold>

    //<editor-fold desc="Resources - Core Chunks" defaultstate=collapsed>

    public static final ItemStack SHALLOW_CORE_CHUNK = ItemStackBuilder.rebar(Material.FIREWORK_STAR, BaseKeys.SHALLOW_CORE_CHUNK)
            .build();
    static {
        RebarItem.register(RebarItem.class, SHALLOW_CORE_CHUNK, BaseKeys.SHALLOW_CORE_CHUNK);
        BasePages.CORE_CHUNKS.addItem(SHALLOW_CORE_CHUNK);
    }

    public static final ItemStack SUBSURFACE_CORE_CHUNK = ItemStackBuilder.rebar(Material.FIREWORK_STAR, BaseKeys.SUBSURFACE_CORE_CHUNK)
            .build();
    static {
        RebarItem.register(RebarItem.class, SUBSURFACE_CORE_CHUNK, BaseKeys.SUBSURFACE_CORE_CHUNK);
        BasePages.CORE_CHUNKS.addItem(SUBSURFACE_CORE_CHUNK);
    }

    public static final ItemStack INTERMEDIATE_CORE_CHUNK = ItemStackBuilder.rebar(Material.FIREWORK_STAR, BaseKeys.INTERMEDIATE_CORE_CHUNK)
            .build();
    static {
        RebarItem.register(RebarItem.class, INTERMEDIATE_CORE_CHUNK, BaseKeys.INTERMEDIATE_CORE_CHUNK);
        BasePages.CORE_CHUNKS.addItem(INTERMEDIATE_CORE_CHUNK);
    }

    //</editor-fold>

    //<editor-fold desc="Resources - Magic" defaultstate=collapsed>

    public static final ItemStack SHIMMER_DUST_1 = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.SHIMMER_DUST_1)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, SHIMMER_DUST_1);
        BasePages.MAGIC.addItem(SHIMMER_DUST_1);
    }


    public static final ItemStack SHIMMER_DUST_2 = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.SHIMMER_DUST_2)
            .set(DataComponentTypes.ITEM_MODEL, Material.REDSTONE.getKey())
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        RebarItem.register(RebarItem.class, SHIMMER_DUST_2);
        BasePages.MAGIC.addItem(SHIMMER_DUST_2);
    }

    public static final ItemStack SHIMMER_DUST_3 = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.SHIMMER_DUST_3)
            .set(DataComponentTypes.ITEM_MODEL, Material.GLOWSTONE_DUST.getKey())
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        RebarItem.register(RebarItem.class, SHIMMER_DUST_3);
        BasePages.MAGIC.addItem(SHIMMER_DUST_3);
    }

    public static final ItemStack COVALENT_BINDER = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.COVALENT_BINDER)
            .set(DataComponentTypes.ITEM_MODEL, Material.LIGHT_BLUE_DYE.getKey())
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        RebarItem.register(RebarItem.class, COVALENT_BINDER);
        BasePages.MAGIC.addItem(COVALENT_BINDER);
    }

    public static final ItemStack ENRICHED_SOUL_SOIL = ItemStackBuilder.rebar(Material.SOUL_SOIL, BaseKeys.ENRICHED_SOUL_SOIL)
            .build();
    static {
        RebarItem.register(RebarItem.class, ENRICHED_SOUL_SOIL, BaseKeys.ENRICHED_SOUL_SOIL);
        BasePages.MAGIC.addItem(ENRICHED_SOUL_SOIL);
    }

    public static final ItemStack SHIMMER_SKULL = ItemStackBuilder.rebar(Material.WITHER_SKELETON_SKULL, BaseKeys.SHIMMER_SKULL)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();

    static {
        RebarItem.register(RebarItem.class, SHIMMER_SKULL);
        BasePages.MAGIC.addItem(SHIMMER_SKULL);
    }

    //</editor-fold>

    //<editor-fold desc="Resources - Miscellaneous" defaultstate=collapsed>

    public static final ItemStack ROCK_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.ROCK_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.GUNPOWDER.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, ROCK_DUST);
        BasePages.MISCELLANEOUS.addItem(ROCK_DUST);
    }

    public static final ItemStack QUARTZ_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.QUARTZ_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, QUARTZ_DUST);
        BasePages.MISCELLANEOUS.addItem(QUARTZ_DUST);
    }

    public static final ItemStack DIAMOND_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.DIAMOND_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, DIAMOND_DUST);
        BasePages.MISCELLANEOUS.addItem(DIAMOND_DUST);
    }

    public static final ItemStack EMERALD_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.EMERALD_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, EMERALD_DUST);
        BasePages.MISCELLANEOUS.addItem(EMERALD_DUST);
    }

    public static final ItemStack FIBER = ItemStackBuilder.rebar(Material.BAMBOO_MOSAIC, BaseKeys.FIBER)
            .build();
    static {
        RebarItem.register(RebarItem.class, FIBER);
        BasePages.MISCELLANEOUS.addItem(FIBER);
    }

    public static final ItemStack COAL_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.COAL_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.GUNPOWDER.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, COAL_DUST);
        BasePages.MISCELLANEOUS.addItem(COAL_DUST);
    }

    public static final ItemStack CHARCOAL_BLOCK = ItemStackBuilder.rebar(Material.COAL_BLOCK, BaseKeys.CHARCOAL_BLOCK)
            .build();
    static {
        RebarItem.register(RebarItem.class, CHARCOAL_BLOCK, BaseKeys.CHARCOAL_BLOCK);
        BasePages.MISCELLANEOUS.addItem(CHARCOAL_BLOCK);
    }

    public static final ItemStack CARBON = ItemStackBuilder.rebar(Material.CHARCOAL, BaseKeys.CARBON)
            .build();
    static {
        RebarItem.register(RebarItem.class, CARBON);
        BasePages.MISCELLANEOUS.addItem(CARBON);
    }

    public static final ItemStack OBSIDIAN_CHIP = ItemStackBuilder.rebar(Material.POLISHED_BLACKSTONE_BUTTON, BaseKeys.OBSIDIAN_CHIP)
            .build();
    static {
        RebarItem.register(RebarItem.class, OBSIDIAN_CHIP);
        BasePages.MISCELLANEOUS.addItem(OBSIDIAN_CHIP);
    }

    public static final ItemStack SULFUR = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.SULFUR)
            .set(DataComponentTypes.ITEM_MODEL, Material.YELLOW_DYE.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, SULFUR);
        BasePages.MISCELLANEOUS.addItem(SULFUR);
    }

    public static final ItemStack GYPSUM = ItemStackBuilder.rebar(Material.QUARTZ, BaseKeys.GYPSUM)
            .build();
    static {
        RebarItem.register(RebarItem.class, GYPSUM);
        BasePages.MISCELLANEOUS.addItem(GYPSUM);
    }

    public static final ItemStack GYPSUM_DUST = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.GYPSUM_DUST)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, GYPSUM_DUST);
        BasePages.MISCELLANEOUS.addItem(GYPSUM_DUST);
    }

    public static final ItemStack REFRACTORY_MIX = ItemStackBuilder.rebar(Material.SMOOTH_RED_SANDSTONE, BaseKeys.REFRACTORY_MIX)
            .build();
    static {
        RebarItem.register(RebarItem.class, REFRACTORY_MIX, BaseKeys.REFRACTORY_MIX);
        BasePages.MISCELLANEOUS.addItem(REFRACTORY_MIX);
    }

    public static final ItemStack UNFIRED_REFRACTORY_BRICK = ItemStackBuilder.rebar(Material.BRICK, BaseKeys.UNFIRED_REFRACTORY_BRICK)
            .build();
    static {
        RebarItem.register(RebarItem.class, UNFIRED_REFRACTORY_BRICK, BaseKeys.UNFIRED_REFRACTORY_BRICK);
        BasePages.MISCELLANEOUS.addItem(UNFIRED_REFRACTORY_BRICK);
    }

    public static final ItemStack REFRACTORY_BRICK = ItemStackBuilder.rebar(Material.NETHERITE_INGOT, BaseKeys.REFRACTORY_BRICK)
            .build();
    static {
        RebarItem.register(RebarItem.class, REFRACTORY_BRICK, BaseKeys.REFRACTORY_BRICK);
        BasePages.MISCELLANEOUS.addItem(REFRACTORY_BRICK);
    }

    public static final ItemStack REFRACTORY_BRICKS = ItemStackBuilder.rebar(Material.DEEPSLATE_TILES, BaseKeys.REFRACTORY_BRICKS)
            .build();
    static {
        RebarItem.register(RebarItem.class, REFRACTORY_BRICKS, BaseKeys.REFRACTORY_BRICKS);
        BasePages.MISCELLANEOUS.addItem(REFRACTORY_BRICKS);
    }

    // </editor-fold>

    //<editor-fold desc="Components" defaultstate=collapsed>

    public static final ItemStack COPPER_SHEET = ItemStackBuilder.rebar(Material.PAPER, BaseKeys.COPPER_SHEET)
            .build();
    static {
        RebarItem.register(RebarItem.class, COPPER_SHEET);
        BasePages.COMPONENTS.addItem(COPPER_SHEET);
    }

    public static final ItemStack GOLD_SHEET = ItemStackBuilder.rebar(Material.PAPER, BaseKeys.GOLD_SHEET)
            .build();
    static {
        RebarItem.register(RebarItem.class, GOLD_SHEET);
        BasePages.COMPONENTS.addItem(GOLD_SHEET);
    }

    public static final ItemStack IRON_SHEET = ItemStackBuilder.rebar(Material.PAPER, BaseKeys.IRON_SHEET)
            .build();
    static {
        RebarItem.register(RebarItem.class, IRON_SHEET);
        BasePages.COMPONENTS.addItem(IRON_SHEET);
    }

    public static final ItemStack TIN_SHEET = ItemStackBuilder.rebar(Material.PAPER, BaseKeys.TIN_SHEET)
            .build();
    static {
        RebarItem.register(RebarItem.class, TIN_SHEET);
        BasePages.COMPONENTS.addItem(TIN_SHEET);


    }

    public static final ItemStack BRONZE_SHEET = ItemStackBuilder.rebar(Material.PAPER, BaseKeys.BRONZE_SHEET)
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_SHEET);
        BasePages.COMPONENTS.addItem(BRONZE_SHEET);


    }

    public static final ItemStack STEEL_SHEET = ItemStackBuilder.rebar(Material.PAPER, BaseKeys.STEEL_SHEET)
            .build();
    static {
        RebarItem.register(RebarItem.class, STEEL_SHEET);
        BasePages.COMPONENTS.addItem(STEEL_SHEET);
    }


    public static final ItemStack COPPER_DRILL_BIT = ItemStackBuilder.rebar(Material.LIGHTNING_ROD, BaseKeys.COPPER_DRILL_BIT)
            .build();
    static {
        RebarItem.register(RebarItem.class, COPPER_DRILL_BIT);
        BasePages.COMPONENTS.addItem(COPPER_DRILL_BIT);
    }

    public static final ItemStack BRONZE_DRILL_BIT = ItemStackBuilder.rebar(Material.LIGHTNING_ROD, BaseKeys.BRONZE_DRILL_BIT)
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_DRILL_BIT);
        BasePages.COMPONENTS.addItem(BRONZE_DRILL_BIT);
    }

    public static final ItemStack ROTOR = ItemStackBuilder.rebar(Material.IRON_TRAPDOOR, BaseKeys.ROTOR)
            .build();
    static {
        RebarItem.register(RebarItem.class, ROTOR);
        BasePages.COMPONENTS.addItem(ROTOR);
    }

    public static final ItemStack BACKFLOW_VALVE = ItemStackBuilder.rebar(Material.DISPENSER, BaseKeys.BACKFLOW_VALVE)
            .build();
    static {
        RebarItem.register(RebarItem.class, BACKFLOW_VALVE);
        BasePages.COMPONENTS.addItem(BACKFLOW_VALVE);
    }

    public static final ItemStack ANALOGUE_DISPLAY = ItemStackBuilder.rebar(Material.LIME_STAINED_GLASS_PANE, BaseKeys.ANALOGUE_DISPLAY)
            .build();
    static {
        RebarItem.register(RebarItem.class, ANALOGUE_DISPLAY);
        BasePages.COMPONENTS.addItem(ANALOGUE_DISPLAY);
    }

    public static final ItemStack FILTER_MESH = ItemStackBuilder.rebar(Material.IRON_BARS, BaseKeys.FILTER_MESH)
            .build();
    static {
        RebarItem.register(RebarItem.class, FILTER_MESH);
        BasePages.COMPONENTS.addItem(FILTER_MESH);
    }

    public static final ItemStack NOZZLE = ItemStackBuilder.rebar(Material.LEVER, BaseKeys.NOZZLE)
            .build();
    static {
        RebarItem.register(RebarItem.class, NOZZLE);
        BasePages.COMPONENTS.addItem(NOZZLE);
    }

    public static final ItemStack ABYSSAL_CATALYST = ItemStackBuilder.rebar(Material.BLACK_CANDLE, BaseKeys.ABYSSAL_CATALYST)
            .build();
    static {
        RebarItem.register(RebarItem.class, ABYSSAL_CATALYST);
        BasePages.COMPONENTS.addItem(ABYSSAL_CATALYST);
    }

    public static final ItemStack HYDRAULIC_MOTOR = ItemStackBuilder.rebar(Material.PISTON, BaseKeys.HYDRAULIC_MOTOR)
            .build();
    static {
        RebarItem.register(RebarItem.class, HYDRAULIC_MOTOR);
        BasePages.COMPONENTS.addItem(HYDRAULIC_MOTOR);
    }

    public static final ItemStack AXLE = ItemStackBuilder.rebar(Material.OAK_FENCE, BaseKeys.AXLE)
            .build();
    static {
        RebarItem.register(RebarItem.class, AXLE);
        BasePages.COMPONENTS.addItem(AXLE);
    }

    public static final ItemStack SAWBLADE = ItemStackBuilder.rebar(Material.IRON_BARS, BaseKeys.SAWBLADE)
            .build();
    static {
        RebarItem.register(RebarItem.class, SAWBLADE);
        BasePages.COMPONENTS.addItem(SAWBLADE);
    }

    public static final ItemStack WEIGHTED_SHAFT = ItemStackBuilder.rebar(Material.DEEPSLATE_TILE_WALL, BaseKeys.WEIGHTED_SHAFT)
            .build();
    static {
        RebarItem.register(RebarItem.class, WEIGHTED_SHAFT);
        BasePages.COMPONENTS.addItem(WEIGHTED_SHAFT);
    }

    public static final ItemStack HYDRAULIC_CANNON_CHAMBER = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.HYDRAULIC_CANNON_CHAMBER)
            .set(DataComponentTypes.ITEM_MODEL, Material.SNOWBALL.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, HYDRAULIC_CANNON_CHAMBER);
        BasePages.COMPONENTS.addItem(HYDRAULIC_CANNON_CHAMBER);
    }

    public static final ItemStack PORTABILITY_CATALYST = ItemStackBuilder.rebar(Material.AMETHYST_SHARD, BaseKeys.PORTABILITY_CATALYST)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        RebarItem.register(RebarItem.class, PORTABILITY_CATALYST);
        BasePages.COMPONENTS.addItem(PORTABILITY_CATALYST);
    }

    public static final ItemStack FLUID_INPUT_HATCH = ItemStackBuilder.rebar(Material.LIGHT_BLUE_TERRACOTTA, BaseKeys.FLUID_INPUT_HATCH)
            .build();
    static {
        RebarItem.register(RebarItem.class, FLUID_INPUT_HATCH, BaseKeys.FLUID_INPUT_HATCH);
        BasePages.COMPONENTS.addItem(FLUID_INPUT_HATCH);
    }

    public static final ItemStack FLUID_OUTPUT_HATCH = ItemStackBuilder.rebar(Material.ORANGE_TERRACOTTA, BaseKeys.FLUID_OUTPUT_HATCH)
            .build();
    static {
        RebarItem.register(RebarItem.class, FLUID_OUTPUT_HATCH, BaseKeys.FLUID_OUTPUT_HATCH);
        BasePages.COMPONENTS.addItem(FLUID_OUTPUT_HATCH);
    }

    public static final ItemStack ITEM_INPUT_HATCH = ItemStackBuilder.rebar(Material.GREEN_TERRACOTTA, BaseKeys.ITEM_INPUT_HATCH)
            .build();
    static {
        RebarItem.register(RebarItem.class, ITEM_INPUT_HATCH, BaseKeys.ITEM_INPUT_HATCH);
        BasePages.COMPONENTS.addItem(ITEM_INPUT_HATCH);
    }

    public static final ItemStack ITEM_OUTPUT_HATCH = ItemStackBuilder.rebar(Material.RED_TERRACOTTA, BaseKeys.ITEM_OUTPUT_HATCH)
            .build();
    static {
        RebarItem.register(RebarItem.class, ITEM_OUTPUT_HATCH, BaseKeys.ITEM_OUTPUT_HATCH);
        BasePages.COMPONENTS.addItem(ITEM_OUTPUT_HATCH);
    }

    //</editor-fold>

    //<editor-fold desc="Tools" defaultstate=collapsed>
    public static final ItemStack STONE_HAMMER = ItemStackBuilder.pylonWeapon(Material.STONE_PICKAXE, BaseKeys.STONE_HAMMER, true, true, false)
            .noTool().build();
    static {
        RebarItem.register(Hammer.class, STONE_HAMMER);
        BasePages.TOOLS.addItem(STONE_HAMMER);
    }

    public static final ItemStack IRON_HAMMER = ItemStackBuilder.pylonWeapon(Material.IRON_PICKAXE, BaseKeys.IRON_HAMMER, true, true, false)
            .noTool().build();
    static {
        RebarItem.register(Hammer.class, IRON_HAMMER);
        BasePages.TOOLS.addItem(IRON_HAMMER);
    }

    public static final ItemStack DIAMOND_HAMMER = ItemStackBuilder.pylonWeapon(Material.DIAMOND_PICKAXE, BaseKeys.DIAMOND_HAMMER, true, true, false)
            .noTool().build();
    static {
        RebarItem.register(Hammer.class, DIAMOND_HAMMER);
        BasePages.TOOLS.addItem(DIAMOND_HAMMER);
    }

    public static final ItemStack BRONZE_AXE = ItemStackBuilder.pylonToolWeapon(Material.CLAY_BALL, BaseKeys.BRONZE_AXE, RebarUtils.axeMineable(), true, false, true)
            .set(DataComponentTypes.ITEM_MODEL, Material.GOLDEN_AXE.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_AXE);
        BasePages.TOOLS.addItem(BRONZE_AXE);
    }

    public static final ItemStack BRONZE_PICKAXE = ItemStackBuilder.pylonToolWeapon(Material.CLAY_BALL, BaseKeys.BRONZE_PICKAXE, RebarUtils.pickaxeMineable(), true, false, false)
            .set(DataComponentTypes.ITEM_MODEL, Material.GOLDEN_PICKAXE.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_PICKAXE);
        BasePages.TOOLS.addItem(BRONZE_PICKAXE);
    }

    public static final ItemStack BRONZE_SHOVEL = ItemStackBuilder.pylonToolWeapon(Material.CLAY_BALL, BaseKeys.BRONZE_SHOVEL, RebarUtils.shovelMineable(), true, false, false)
            .set(DataComponentTypes.ITEM_MODEL, Material.GOLDEN_SHOVEL.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_SHOVEL);
        BasePages.TOOLS.addItem(BRONZE_SHOVEL);
    }

    public static final ItemStack BRONZE_HOE = ItemStackBuilder.pylonToolWeapon(Material.CLAY_BALL, BaseKeys.BRONZE_HOE, RebarUtils.hoeMineable(), true, false, false)
            .set(DataComponentTypes.ITEM_MODEL, Material.GOLDEN_HOE.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_HOE);
        BasePages.TOOLS.addItem(BRONZE_HOE);
    }

    public static final ItemStack WATERING_CAN = ItemStackBuilder.rebar(Material.BUCKET, BaseKeys.WATERING_CAN)
            .build();
    static {
        RebarItem.register(WateringCan.class, WATERING_CAN);
        BasePages.TOOLS.addItem(WATERING_CAN);
    }

    public static final ItemStack LUMBER_AXE = ItemStackBuilder.rebar(Material.WOODEN_AXE, BaseKeys.LUMBER_AXE)
            .durability(Settings.get(BaseKeys.LUMBER_AXE).getOrThrow("durability", ConfigAdapter.INT))
            .build();
    static {
        RebarItem.register(LumberAxe.class, LUMBER_AXE);
        BasePages.TOOLS.addItem(LUMBER_AXE);
    }

    public static final ItemStack PORTABLE_CRAFTING_TABLE = ItemStackBuilder.rebar(Material.CRAFTING_TABLE, BaseKeys.PORTABLE_CRAFTING_TABLE)
            .build();
    static {
        RebarItem.register(PortableCraftingTable.class, PORTABLE_CRAFTING_TABLE);
        BasePages.TOOLS.addItem(PORTABLE_CRAFTING_TABLE);
    }

    public static final ItemStack PORTABLE_DUSTBIN = ItemStackBuilder.rebar(Material.CAULDRON, BaseKeys.PORTABLE_DUSTBIN)
            .build();
    static {
        RebarItem.register(PortableDustbin.class, PORTABLE_DUSTBIN);
        BasePages.TOOLS.addItem(PORTABLE_DUSTBIN);
    }

    public static final ItemStack PORTABLE_ENDER_CHEST = ItemStackBuilder.rebar(Material.ENDER_CHEST, BaseKeys.PORTABLE_ENDER_CHEST)
            .build();
    static {
        RebarItem.register(PortableEnderChest.class, PORTABLE_ENDER_CHEST);
        BasePages.TOOLS.addItem(PORTABLE_ENDER_CHEST);
    }

    public static final ItemStack CLIMBING_PICK = ItemStackBuilder.rebar(Material.DIAMOND_HOE, BaseKeys.CLIMBING_PICK)
            .build();
    static {
        RebarItem.register(ClimbingPick.class, CLIMBING_PICK);
        BasePages.TOOLS.addItem(CLIMBING_PICK);
    }

    public static final ItemStack BRICK_MOLD = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.BRICK_MOLD)
            .useCooldown(Settings.get(BaseKeys.BRICK_MOLD).getOrThrow("cooldown-ticks", ConfigAdapter.INT), BaseKeys.BRICK_MOLD)
            .set(DataComponentTypes.ITEM_MODEL, Material.OAK_FENCE_GATE.getKey())
            .build();
    static {
        RebarItem.register(BrickMold.class, BRICK_MOLD);
        BasePages.TOOLS.addItem(BRICK_MOLD);
    }

    public static final ItemStack TONGS = ItemStackBuilder.rebar(Material.SHEARS, BaseKeys.TONGS)
            .build();
    static {
        RebarItem.register(Tongs.class, TONGS);
        BasePages.TOOLS.addItem(TONGS);
    }

    public static final ItemStack SHIMMER_MAGNET = ItemStackBuilder.rebar(Material.BREEZE_ROD, BaseKeys.SHIMMER_MAGNET)
        .set(DataComponentTypes.MAX_STACK_SIZE, 1)
        .build();
    static {
        RebarItem.register(ShimmerMagnet.class, SHIMMER_MAGNET);
        BasePages.TOOLS.addItem(SHIMMER_MAGNET);
    }

    public static final ItemStack FIREPROOF_RUNE = ItemStackBuilder.rebar(Material.FIREWORK_STAR, BaseKeys.FIREPROOF_RUNE)
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
        RebarItem.register(FireproofRune.class, FIREPROOF_RUNE);
        BasePages.TOOLS.addItem(FIREPROOF_RUNE);
    }

    public static final ItemStack SOULBOUND_RUNE = ItemStackBuilder.rebar(Material.FIREWORK_STAR, BaseKeys.SOULBOUND_RUNE)
            .set(DataComponentTypes.FIREWORK_EXPLOSION, FireworkEffect.builder()
                    .withColor(Color.PURPLE)
                    .build())
            .build();
    static {
        RebarItem.register(SoulboundRune.class, SOULBOUND_RUNE);
        BasePages.TOOLS.addItem(SOULBOUND_RUNE);
    }

    @SuppressWarnings("ConstantConditions")
    public static final ItemStack CONFETTI_POPPER = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.CONFETTI_POPPER)
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
        RebarItem.register(ConfettiPopper.class, CONFETTI_POPPER);
        BasePages.TOOLS.addItem(CONFETTI_POPPER);
    }

    //</editor-fold>

    //<editor-fold desc="Combat" defaultstate=collapsed>

    public static final ItemStack BRONZE_SWORD = ItemStackBuilder.pylonWeapon(Material.CLAY_BALL, BaseKeys.BRONZE_SWORD, true, false, false)
            .set(DataComponentTypes.ITEM_MODEL, Material.GOLDEN_SWORD.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_SWORD);
        BasePages.COMBAT.addItem(BRONZE_SWORD);
    }

    public static final ItemStack BEHEADING_SWORD = ItemStackBuilder.rebar(Material.DIAMOND_SWORD, BaseKeys.BEHEADING_SWORD)
            .durability(Settings.get(BaseKeys.BEHEADING_SWORD).getOrThrow("durability", ConfigAdapter.INT)) // todo: weapon stats?
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        RebarItem.register(BeheadingSword.class, BEHEADING_SWORD);
        BasePages.COMBAT.addItem(BEHEADING_SWORD);
    }

    public static final ItemStack BANDAGE = ItemStackBuilder.rebar(Material.COBWEB, BaseKeys.BANDAGE)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .consumeSeconds(Settings.get(BaseKeys.BANDAGE).getOrThrow("consume-seconds", ConfigAdapter.DOUBLE).floatValue())
                    .animation(ItemUseAnimation.BOW)
                    .hasConsumeParticles(false)
                    .build())
            .build();
    static {
        RebarItem.register(HealingConsumable.class, BANDAGE);
        BasePages.COMBAT.addItem(BANDAGE);
    }

    public static final ItemStack SPLINT = ItemStackBuilder.rebar(Material.STICK, BaseKeys.SPLINT)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .consumeSeconds(Settings.get(BaseKeys.SPLINT).getOrThrow("consume-seconds", ConfigAdapter.DOUBLE).floatValue())
                    .animation(ItemUseAnimation.BOW)
                    .hasConsumeParticles(false)
                    .build())
            .build();
    static {
        RebarItem.register(HealingConsumable.class, SPLINT);
        BasePages.COMBAT.addItem(SPLINT);
    }

    public static final ItemStack DISINFECTANT = ItemStackBuilder.rebar(Material.BREWER_POTTERY_SHERD, BaseKeys.DISINFECTANT)
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
        RebarItem.register(HealingConsumable.class, DISINFECTANT);
        BasePages.COMBAT.addItem(DISINFECTANT);
    }

    public static final ItemStack MEDKIT = ItemStackBuilder.rebar(Material.SHULKER_SHELL, BaseKeys.MEDKIT)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable()
                    .consumeSeconds(Settings.get(BaseKeys.MEDKIT).getOrThrow("consume-seconds", ConfigAdapter.DOUBLE).floatValue())
                    .animation(ItemUseAnimation.BOW)
                    .hasConsumeParticles(false)
                    .addEffect(ConsumeEffect.clearAllStatusEffects())
            )
            .build();
    static {
        RebarItem.register(HealingConsumable.class, MEDKIT);
        BasePages.COMBAT.addItem(MEDKIT);
    }

    public static final ItemStack REACTIVATED_WITHER_SKULL = ItemStackBuilder.rebar(Material.WITHER_SKELETON_SKULL, BaseKeys.REACTIVATED_WITHER_SKULL)
            .durability(Settings.get(BaseKeys.REACTIVATED_WITHER_SKULL).getOrThrow("durability", ConfigAdapter.INT))
            .useCooldown(Settings.get(BaseKeys.REACTIVATED_WITHER_SKULL).getOrThrow("cooldown-ticks", ConfigAdapter.INT), BaseKeys.REACTIVATED_WITHER_SKULL)
            .build();
    static {
        RebarItem.register(ReactivatedWitherSkull.class, REACTIVATED_WITHER_SKULL);
        BasePages.COMBAT.addItem(REACTIVATED_WITHER_SKULL);
    }

    public static final ItemStack HYPER_ACTIVATED_WITHER_SKULL = ItemStackBuilder.rebar(Material.WITHER_SKELETON_SKULL, BaseKeys.HYPER_ACTIVATED_WITHER_SKULL)
            .durability(Settings.get(BaseKeys.HYPER_ACTIVATED_WITHER_SKULL).getOrThrow("durability", ConfigAdapter.INT))
            .useCooldown(Settings.get(BaseKeys.HYPER_ACTIVATED_WITHER_SKULL).getOrThrow("cooldown-ticks", ConfigAdapter.INT), BaseKeys.REACTIVATED_WITHER_SKULL)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true)
            .build();
    static {
        RebarItem.register(ReactivatedWitherSkull.class, HYPER_ACTIVATED_WITHER_SKULL);
        BasePages.COMBAT.addItem(HYPER_ACTIVATED_WITHER_SKULL);
    }

    public static final ItemStack ICE_ARROW = ItemStackBuilder.rebar(Material.ARROW, BaseKeys.ICE_ARROW).build();
    static {
        RebarItem.register(IceArrow.class, ICE_ARROW, BaseKeys.ICE_ARROW);
        BasePages.COMBAT.addItem(ICE_ARROW);
    }

    public static final ItemStack RECOIL_ARROW = ItemStackBuilder.rebar(Material.ARROW, BaseKeys.RECOIL_ARROW)
            .build();
    static {
        RebarItem.register(RecoilArrow.class, RECOIL_ARROW);
        BasePages.COMBAT.addItem(RECOIL_ARROW);
    }

    public static final ItemStack HYDRAULIC_CANNON = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.HYDRAULIC_CANNON)
            .set(DataComponentTypes.ITEM_MODEL, Material.IRON_HORSE_ARMOR.getKey())
            .set(DataComponentTypes.USE_COOLDOWN, UseCooldown
                    .useCooldown(
                            Settings.get(BaseKeys.HYDRAULIC_CANNON).getOrThrow("cooldown-ticks", ConfigAdapter.INT) / 20.0F
                    )
                    .cooldownGroup(BaseKeys.HYDRAULIC_CANNON.key())
                    .build())
            .editPdc(pdc -> {
                pdc.set(BaseFluids.HYDRAULIC_FLUID.getKey(), RebarSerializers.DOUBLE, 0.0);
                pdc.set(BaseFluids.DIRTY_HYDRAULIC_FLUID.getKey(), RebarSerializers.DOUBLE, 0.0);
            })
            .build();
    static {
        RebarItem.register(HydraulicCannon.class, HYDRAULIC_CANNON);
        BasePages.COMBAT.addItem(HYDRAULIC_CANNON);
    }

    public static final ItemStack TIN_PROJECTILE = ItemStackBuilder.rebar(Material.IRON_NUGGET, BaseKeys.TIN_PROJECTILE)
            .build();
    static {
        RebarItem.register(RebarItem.class, TIN_PROJECTILE);
        BasePages.COMBAT.addItem(TIN_PROJECTILE);
    }
    //</editor-fold>

    //<editor-fold desc="Talismans" defaultstate=collapsed>

    public static final ItemStack HEALTH_TALISMAN_SIMPLE = ItemStackBuilder.rebar(Material.AMETHYST_SHARD, BaseKeys.HEALTH_TALISMAN_SIMPLE)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    static {
        RebarItem.register(HealthTalisman.class, HEALTH_TALISMAN_SIMPLE);
        BasePages.TALISMANS.addItem(HEALTH_TALISMAN_SIMPLE);
    }

    public static final ItemStack HEALTH_TALISMAN_ADVANCED = ItemStackBuilder.rebar(Material.AMETHYST_SHARD, BaseKeys.HEALTH_TALISMAN_ADVANCED)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    static {
        RebarItem.register(HealthTalisman.class, HEALTH_TALISMAN_ADVANCED);
        BasePages.TALISMANS.addItem(HEALTH_TALISMAN_ADVANCED);
    }

    public static final ItemStack HEALTH_TALISMAN_ULTIMATE = ItemStackBuilder.rebar(Material.AMETHYST_SHARD, BaseKeys.HEALTH_TALISMAN_ULTIMATE)
            .set(DataComponentTypes.MAX_STACK_SIZE, 1)
            .build();
    static {
        RebarItem.register(HealthTalisman.class, HEALTH_TALISMAN_ULTIMATE);
        BasePages.TALISMANS.addItem(HEALTH_TALISMAN_ULTIMATE);
    }
    public static final ItemStack HUNGER_TALISMAN_SIMPLE = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.HUNGER_TALISMAN_SIMPLE)
            .set(DataComponentTypes.ITEM_MODEL, Objects.requireNonNull(Material.GOLDEN_APPLE.getDefaultData(DataComponentTypes.ITEM_MODEL)))
            .build();
    static {
        RebarItem.register(HungerTalisman.class, HUNGER_TALISMAN_SIMPLE);
        BasePages.TALISMANS.addItem(HUNGER_TALISMAN_SIMPLE);
    }

    public static final ItemStack HUNGER_TALISMAN_ADVANCED = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.HUNGER_TALISMAN_ADVANCED)
            .set(DataComponentTypes.ITEM_MODEL, Objects.requireNonNull(Material.GOLDEN_APPLE.getDefaultData(DataComponentTypes.ITEM_MODEL)))
            .build();
    static {
        RebarItem.register(HungerTalisman.class, HUNGER_TALISMAN_ADVANCED);
        BasePages.TALISMANS.addItem(HUNGER_TALISMAN_ADVANCED);
    }

    public static final ItemStack HUNGER_TALISMAN_ULTIMATE = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.HUNGER_TALISMAN_ULTIMATE)
            .set(DataComponentTypes.ITEM_MODEL, Objects.requireNonNull(Material.GOLDEN_APPLE.getDefaultData(DataComponentTypes.ITEM_MODEL)))
            .build();
    static {
        RebarItem.register(HungerTalisman.class, HUNGER_TALISMAN_ULTIMATE);
        BasePages.TALISMANS.addItem(HUNGER_TALISMAN_ULTIMATE);
    }

    public static final ItemStack FARMING_TALISMAN_SIMPLE = ItemStackBuilder.rebar(Material.BOWL, BaseKeys.FARMING_TALISMAN_SIMPLE)
            .build();
    static {
        RebarItem.register(FarmingTalisman.class, FARMING_TALISMAN_SIMPLE);
        BasePages.TALISMANS.addItem(FARMING_TALISMAN_SIMPLE);
    }

    public static final ItemStack FARMING_TALISMAN_ADVANCED = ItemStackBuilder.rebar(Material.BOWL, BaseKeys.FARMING_TALISMAN_ADVANCED)
            .build();
    static {
        RebarItem.register(FarmingTalisman.class, FARMING_TALISMAN_ADVANCED);
        BasePages.TALISMANS.addItem(FARMING_TALISMAN_ADVANCED);
    }

    public static final ItemStack FARMING_TALISMAN_ULTIMATE = ItemStackBuilder.rebar(Material.BOWL, BaseKeys.FARMING_TALISMAN_ULTIMATE)
            .build();
    static {
        RebarItem.register(FarmingTalisman.class, FARMING_TALISMAN_ULTIMATE);
        BasePages.TALISMANS.addItem(FARMING_TALISMAN_ULTIMATE);
    }

    public static final ItemStack BARTERING_TALISMAN_SIMPLE = ItemStackBuilder.rebar(Material.GOLD_INGOT, BaseKeys.BARTERING_TALISMAN_SIMPLE)
            .build();
    static {
        RebarItem.register(BarteringTalisman.class, BARTERING_TALISMAN_SIMPLE);
        BasePages.TALISMANS.addItem(BARTERING_TALISMAN_SIMPLE);
    }

    public static final ItemStack BARTERING_TALISMAN_ADVANCED = ItemStackBuilder.rebar(Material.GOLD_INGOT, BaseKeys.BARTERING_TALISMAN_ADVANCED)
            .build();
    static {
        RebarItem.register(BarteringTalisman.class, BARTERING_TALISMAN_ADVANCED);
        BasePages.TALISMANS.addItem(BARTERING_TALISMAN_ADVANCED);
    }

    public static final ItemStack BARTERING_TALISMAN_ULTIMATE = ItemStackBuilder.rebar(Material.GOLD_INGOT, BaseKeys.BARTERING_TALISMAN_ULTIMATE)
            .build();
    static {
        RebarItem.register(BarteringTalisman.class, BARTERING_TALISMAN_ULTIMATE);
        BasePages.TALISMANS.addItem(BARTERING_TALISMAN_ULTIMATE);
    }

    public static final ItemStack WATER_BREATHING_TALISMAN_SIMPLE = ItemStackBuilder.rebar(Material.NAUTILUS_SHELL, BaseKeys.WATER_BREATHING_TALISMAN_SIMPLE)
            .build();
    static {
        RebarItem.register(WaterBreathingTalisman.class, WATER_BREATHING_TALISMAN_SIMPLE);
        BasePages.TALISMANS.addItem(WATER_BREATHING_TALISMAN_SIMPLE);
    }

    public static final ItemStack WATER_BREATHING_TALISMAN_ADVANCED = ItemStackBuilder.rebar(Material.NAUTILUS_SHELL, BaseKeys.WATER_BREATHING_TALISMAN_ADVANCED)
            .build();
    static {
        RebarItem.register(WaterBreathingTalisman.class, WATER_BREATHING_TALISMAN_ADVANCED);
        BasePages.TALISMANS.addItem(WATER_BREATHING_TALISMAN_ADVANCED);
    }

    public static final ItemStack WATER_BREATHING_TALISMAN_ULTIMATE = ItemStackBuilder.rebar(Material.NAUTILUS_SHELL, BaseKeys.WATER_BREATHING_TALISMAN_ULTIMATE)
            .build();
    static {
        RebarItem.register(WaterBreathingTalisman.class, WATER_BREATHING_TALISMAN_ULTIMATE);
        BasePages.TALISMANS.addItem(WATER_BREATHING_TALISMAN_ULTIMATE);
    }

    public static final ItemStack LUCK_TALISMAN_SIMPLE = ItemStackBuilder.rebar(Material.RABBIT_FOOT, BaseKeys.LUCK_TALISMAN_SIMPLE)
            .build();
    static {
        RebarItem.register(LuckTalisman.class, LUCK_TALISMAN_SIMPLE);
        BasePages.TALISMANS.addItem(LUCK_TALISMAN_SIMPLE);
    }

    public static final ItemStack LUCK_TALISMAN_ADVANCED = ItemStackBuilder.rebar(Material.RABBIT_FOOT, BaseKeys.LUCK_TALISMAN_ADVANCED)
            .build();
    static {
        RebarItem.register(LuckTalisman.class, LUCK_TALISMAN_ADVANCED);
        BasePages.TALISMANS.addItem(LUCK_TALISMAN_ADVANCED);
    }

    public static final ItemStack LUCK_TALISMAN_ULTIMATE = ItemStackBuilder.rebar(Material.RABBIT_FOOT, BaseKeys.LUCK_TALISMAN_ULTIMATE)
            .build();
    static {
        RebarItem.register(LuckTalisman.class, LUCK_TALISMAN_ULTIMATE);
        BasePages.TALISMANS.addItem(LUCK_TALISMAN_ULTIMATE);
    }

    public static final ItemStack BREEDING_TALISMAN_SIMPLE = ItemStackBuilder.rebar(Material.APPLE, BaseKeys.BREEDING_TALISMAN_SIMPLE)
            .build();
    static {
        RebarItem.register(BreedingTalisman.class, BREEDING_TALISMAN_SIMPLE);
        BasePages.TALISMANS.addItem(BREEDING_TALISMAN_SIMPLE);
    }

    public static final ItemStack BREEDING_TALISMAN_ADVANCED = ItemStackBuilder.rebar(Material.APPLE, BaseKeys.BREEDING_TALISMAN_ADVANCED)
            .build();
    static {
        RebarItem.register(BreedingTalisman.class, BREEDING_TALISMAN_ADVANCED);
        BasePages.TALISMANS.addItem(BREEDING_TALISMAN_ADVANCED);
    }

    public static final ItemStack BREEDING_TALISMAN_ULTIMATE = ItemStackBuilder.rebar(Material.APPLE, BaseKeys.BREEDING_TALISMAN_ULTIMATE)
            .build();
    static {
        RebarItem.register(BreedingTalisman.class, BREEDING_TALISMAN_ULTIMATE);
        BasePages.TALISMANS.addItem(BREEDING_TALISMAN_ULTIMATE);
    }

    public static final ItemStack ENCHANTING_TALISMAN_SIMPLE = ItemStackBuilder.rebar(Material.ENCHANTED_BOOK, BaseKeys.ENCHANTING_TALISMAN_SIMPLE)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false)
            .build();
    static {
        RebarItem.register(EnchantingTalisman.class, ENCHANTING_TALISMAN_SIMPLE);
        BasePages.TALISMANS.addItem(ENCHANTING_TALISMAN_SIMPLE);
    }

    public static final ItemStack ENCHANTING_TALISMAN_ADVANCED = ItemStackBuilder.rebar(Material.ENCHANTED_BOOK, BaseKeys.ENCHANTING_TALISMAN_ADVANCED)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false)
            .build();
    static {
        RebarItem.register(EnchantingTalisman.class, ENCHANTING_TALISMAN_ADVANCED);
        BasePages.TALISMANS.addItem(ENCHANTING_TALISMAN_ADVANCED);
    }

    public static final ItemStack ENCHANTING_TALISMAN_ULTIMATE = ItemStackBuilder.rebar(Material.ENCHANTED_BOOK, BaseKeys.ENCHANTING_TALISMAN_ULTIMATE)
            .set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, false)
            .build();
    static {
        RebarItem.register(EnchantingTalisman.class, ENCHANTING_TALISMAN_ULTIMATE);
        BasePages.TALISMANS.addItem(ENCHANTING_TALISMAN_ULTIMATE);
    }

    public static final ItemStack HUNTING_TALISMAN_SIMPLE = ItemStackBuilder.rebar(Material.SKELETON_SKULL, BaseKeys.HUNTING_TALISMAN_SIMPLE)
            .build();
    static {
        RebarItem.register(HuntingTalisman.class, HUNTING_TALISMAN_SIMPLE);
        BasePages.TALISMANS.addItem(HUNTING_TALISMAN_SIMPLE);
    }

    public static final ItemStack HUNTING_TALISMAN_ADVANCED = ItemStackBuilder.rebar(Material.SKELETON_SKULL, BaseKeys.HUNTING_TALISMAN_ADVANCED)
            .build();
    static {
        RebarItem.register(HuntingTalisman.class, HUNTING_TALISMAN_ADVANCED);
        BasePages.TALISMANS.addItem(HUNTING_TALISMAN_ADVANCED);
    }

    public static final ItemStack HUNTING_TALISMAN_ULTIMATE = ItemStackBuilder.rebar(Material.SKELETON_SKULL, BaseKeys.HUNTING_TALISMAN_ULTIMATE)
            .build();
    static {
        RebarItem.register(HuntingTalisman.class, HUNTING_TALISMAN_ULTIMATE);
        BasePages.TALISMANS.addItem(HUNTING_TALISMAN_ULTIMATE);
    }

    //</editor-fold>

    //<editor-fold desc="Armour" defaultstate=collapsed>

    public static final ItemStack BRONZE_HELMET = ItemStackBuilder.pylonHelmet(Material.CLAY_BALL, BaseKeys.BRONZE_HELMET, true)
            .set(DataComponentTypes.ITEM_MODEL, Material.GOLDEN_HELMET.getKey())
            .set(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.HEAD)
                    .assetId(Key.key("gold"))
                    .build())
            .build();
    static {
        RebarItem.register(BronzeArmor.class, BRONZE_HELMET);
        BasePages.ARMOUR.addItem(BRONZE_HELMET);
    }

    public static final ItemStack BRONZE_CHESTPLATE = ItemStackBuilder.pylonChestplate(Material.CLAY_BALL, BaseKeys.BRONZE_CHESTPLATE, true)
            .set(DataComponentTypes.ITEM_MODEL, Material.GOLDEN_CHESTPLATE.getKey())
            .set(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.CHEST)
                    .assetId(Key.key("gold"))
                    .build())
            .build();
    static {
        RebarItem.register(BronzeArmor.class, BRONZE_CHESTPLATE);
        BasePages.ARMOUR.addItem(BRONZE_CHESTPLATE);
    }

    public static final ItemStack BRONZE_LEGGINGS = ItemStackBuilder.pylonLeggings(Material.CLAY_BALL, BaseKeys.BRONZE_LEGGINGS, true)
            .set(DataComponentTypes.ITEM_MODEL, Material.GOLDEN_LEGGINGS.getKey())
            .set(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.LEGS)
                    .assetId(Key.key("gold"))
                    .build())
            .build();
    static {
        RebarItem.register(BronzeArmor.class, BRONZE_LEGGINGS);
        BasePages.ARMOUR.addItem(BRONZE_LEGGINGS);
    }

    public static final ItemStack BRONZE_BOOTS = ItemStackBuilder.pylonBoots(Material.GOLDEN_BOOTS, BaseKeys.BRONZE_BOOTS, true)
            .set(DataComponentTypes.ITEM_MODEL, Material.GOLDEN_BOOTS.getKey())
            .set(DataComponentTypes.EQUIPPABLE, Equippable.equippable(EquipmentSlot.FEET)
                    .assetId(Key.key("gold"))
                    .build())
            .build();
    static {
        RebarItem.register(BronzeArmor.class, BRONZE_BOOTS);
        BasePages.ARMOUR.addItem(BRONZE_BOOTS);
    }

    //</editor-fold>

    //<editor-fold desc="Food" defaultstate=collapsed>

    public static final ItemStack FLOUR = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.FLOUR)
            .set(DataComponentTypes.ITEM_MODEL, Material.SUGAR.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, FLOUR);
        BasePages.MISCELLANEOUS.addItem(FLOUR);
    }

    public static final ItemStack DOUGH = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.DOUGH)
            .set(DataComponentTypes.ITEM_MODEL, Material.YELLOW_DYE.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, DOUGH);
        BasePages.MISCELLANEOUS.addItem(DOUGH);
    }

    public static final ItemStack MONSTER_JERKY = ItemStackBuilder.rebar(Material.ROTTEN_FLESH, BaseKeys.MONSTER_JERKY)
            .set(DataComponentTypes.CONSUMABLE, Consumable.consumable().build())
            .set(DataComponentTypes.FOOD, FoodProperties.food()
                    .canAlwaysEat(false)
                    .nutrition(Settings.get(BaseKeys.MONSTER_JERKY).getOrThrow("nutrition", ConfigAdapter.INT))
                    .saturation(Settings.get(BaseKeys.MONSTER_JERKY).getOrThrow("saturation", ConfigAdapter.DOUBLE).floatValue())
                    .build()
            )
            .build();
    static {
        RebarItem.register(RebarItem.class, MONSTER_JERKY);
        BasePages.FOOD.addItem(MONSTER_JERKY);
    }

    //</editor-fold>

    //<editor-fold desc="Building" defaultstate=collapsed>

    public static final ItemStack IGNEOUS_COMPOSITE = ItemStackBuilder.rebar(Material.OBSIDIAN, BaseKeys.IGNEOUS_COMPOSITE)
            .build();
    static {
        RebarItem.register(RebarItem.class, IGNEOUS_COMPOSITE, BaseKeys.IGNEOUS_COMPOSITE);
        BasePages.BUILDING.addItem(IGNEOUS_COMPOSITE);
    }

    public static final ItemStack PEDESTAL = ItemStackBuilder.rebar(Material.STONE_BRICK_WALL, BaseKeys.PEDESTAL)
            .build();
    static {
        RebarItem.register(RebarItem.class, PEDESTAL, BaseKeys.PEDESTAL);
        BasePages.BUILDING.addItem(PEDESTAL);
    }

    public static final ItemStack ELEVATOR_1 = ItemStackBuilder.rebar(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_1)
            .build();
    static {
        RebarItem.register(Elevator.Item.class, ELEVATOR_1, BaseKeys.ELEVATOR_1);
        BasePages.BUILDING.addItem(ELEVATOR_1);
    }

    public static final ItemStack ELEVATOR_2 = ItemStackBuilder.rebar(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_2)
            .build();
    static {
        RebarItem.register(Elevator.Item.class, ELEVATOR_2, BaseKeys.ELEVATOR_2);
        BasePages.BUILDING.addItem(ELEVATOR_2);
    }

    public static final ItemStack ELEVATOR_3 = ItemStackBuilder.rebar(Material.SMOOTH_QUARTZ_SLAB, BaseKeys.ELEVATOR_3)
            .build();
    static {
        RebarItem.register(Elevator.Item.class, ELEVATOR_3, BaseKeys.ELEVATOR_3);
        BasePages.BUILDING.addItem(ELEVATOR_3);
    }

    public static final ItemStack EXPLOSIVE_TARGET = ItemStackBuilder.rebar(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET)
            .build();
    static {
        RebarItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET, BaseKeys.EXPLOSIVE_TARGET);
        BasePages.BUILDING.addItem(EXPLOSIVE_TARGET);
    }

    public static final ItemStack EXPLOSIVE_TARGET_FIERY = ItemStackBuilder.rebar(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET_FIERY)
            .build();
    static {
        RebarItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET_FIERY, BaseKeys.EXPLOSIVE_TARGET_FIERY);
        BasePages.BUILDING.addItem(EXPLOSIVE_TARGET_FIERY);
    }

    public static final ItemStack EXPLOSIVE_TARGET_SUPER = ItemStackBuilder.rebar(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET_SUPER)
            .build();
    static {
        RebarItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET_SUPER, BaseKeys.EXPLOSIVE_TARGET_SUPER);
        BasePages.BUILDING.addItem(EXPLOSIVE_TARGET_SUPER);
    }

    public static final ItemStack EXPLOSIVE_TARGET_SUPER_FIERY = ItemStackBuilder.rebar(Material.TARGET, BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY)
            .build();
    static {
        RebarItem.register(ExplosiveTarget.Item.class, EXPLOSIVE_TARGET_SUPER_FIERY, BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY);
        BasePages.BUILDING.addItem(EXPLOSIVE_TARGET_SUPER_FIERY);
    }

    public static final ItemStack IMMOBILIZER = ItemStackBuilder.rebar(Material.PISTON, BaseKeys.IMMOBILIZER)
            .build();
    static {
        RebarItem.register(Immobilizer.Item.class, IMMOBILIZER, BaseKeys.IMMOBILIZER);
        BasePages.BUILDING.addItem(IMMOBILIZER);
    }

    //</editor-fold>

    //<editor-fold desc="Machines - Simple Machines" defaultstate=collapsed>

    public static final ItemStack GRINDSTONE = ItemStackBuilder.rebar(Material.SMOOTH_STONE_SLAB, BaseKeys.GRINDSTONE)
            .build();
    static {
        RebarItem.register(RebarItem.class, GRINDSTONE, BaseKeys.GRINDSTONE);
        BasePages.SIMPLE_MACHINES.addItem(GRINDSTONE);
    }

    public static final ItemStack GRINDSTONE_HANDLE = ItemStackBuilder.rebar(Material.OAK_FENCE, BaseKeys.GRINDSTONE_HANDLE)
            .build();
    static {
        RebarItem.register(RebarItem.class, GRINDSTONE_HANDLE, BaseKeys.GRINDSTONE_HANDLE);
        BasePages.SIMPLE_MACHINES.addItem(GRINDSTONE_HANDLE);
    }

    public static final ItemStack MIXING_POT = ItemStackBuilder.rebar(Material.CAULDRON, BaseKeys.MIXING_POT)
            .build();
    static {
        RebarItem.register(MixingPot.MixingPotItem.class, MIXING_POT, BaseKeys.MIXING_POT);
        BasePages.SIMPLE_MACHINES.addItem(MIXING_POT);
    }

    public static final ItemStack MANUAL_CORE_DRILL_LEVER = ItemStackBuilder.rebar(Material.LEVER, BaseKeys.MANUAL_CORE_DRILL_LEVER)
            .build();
    static {
        RebarItem.register(RebarItem.class, MANUAL_CORE_DRILL_LEVER, BaseKeys.MANUAL_CORE_DRILL_LEVER);
        BasePages.SIMPLE_MACHINES.addItem(MANUAL_CORE_DRILL_LEVER);
    }

    public static final ItemStack MANUAL_CORE_DRILL = ItemStackBuilder.rebar(Material.CHISELED_STONE_BRICKS, BaseKeys.MANUAL_CORE_DRILL)
            .build();
    static {
        RebarItem.register(CoreDrill.Item.class, MANUAL_CORE_DRILL, BaseKeys.MANUAL_CORE_DRILL);
        BasePages.SIMPLE_MACHINES.addItem(MANUAL_CORE_DRILL);
    }

    public static final ItemStack IMPROVED_MANUAL_CORE_DRILL = ItemStackBuilder.rebar(Material.WAXED_OXIDIZED_COPPER, BaseKeys.IMPROVED_MANUAL_CORE_DRILL)
            .build();
    static {
        RebarItem.register(ImprovedManualCoreDrill.Item.class, IMPROVED_MANUAL_CORE_DRILL, BaseKeys.IMPROVED_MANUAL_CORE_DRILL);
        BasePages.SIMPLE_MACHINES.addItem(IMPROVED_MANUAL_CORE_DRILL);
    }

    public static final ItemStack PRESS = ItemStackBuilder.rebar(Material.COMPOSTER, BaseKeys.PRESS)
            .build();
    static {
        RebarItem.register(Press.PressItem.class, PRESS, BaseKeys.PRESS);
        BasePages.SIMPLE_MACHINES.addItem(PRESS);
    }

    public static final ItemStack SPRINKLER = ItemStackBuilder.rebar(Material.FLOWER_POT, BaseKeys.SPRINKLER)
            .build();
    static {
        RebarItem.register(Sprinkler.Item.class, SPRINKLER, BaseKeys.SPRINKLER);
        BasePages.FLUID_MACHINES.addItem(SPRINKLER);
    }

    public static final ItemStack CRUCIBLE = ItemStackBuilder.rebar(Material.CAULDRON, BaseKeys.CRUCIBLE)
            .build();
    static {
        RebarItem.register(RebarItem.class, CRUCIBLE, BaseKeys.CRUCIBLE);
        BasePages.SIMPLE_MACHINES.addItem(CRUCIBLE);
    }

    public static final ItemStack FLUID_STRAINER = ItemStackBuilder.rebar(Material.COPPER_GRATE, BaseKeys.FLUID_STRAINER)
            .build();
    static {
        RebarItem.register(FluidStrainer.Item.class, FLUID_STRAINER, BaseKeys.FLUID_STRAINER);
        BasePages.FLUID_MACHINES.addItem(FLUID_STRAINER);
    }

    public static final ItemStack VACUUM_HOPPER_1 = ItemStackBuilder.rebar(Material.HOPPER, BaseKeys.VACUUM_HOPPER_1)
            .build();
    static {
        RebarItem.register(VacuumHopper.Item.class, VACUUM_HOPPER_1, BaseKeys.VACUUM_HOPPER_1);
        BasePages.SIMPLE_MACHINES.addItem(VACUUM_HOPPER_1);
    }

    public static final ItemStack VACUUM_HOPPER_2 = ItemStackBuilder.rebar(Material.HOPPER, BaseKeys.VACUUM_HOPPER_2)
            .build();
    static {
        RebarItem.register(VacuumHopper.Item.class, VACUUM_HOPPER_2, BaseKeys.VACUUM_HOPPER_2);
        BasePages.SIMPLE_MACHINES.addItem(VACUUM_HOPPER_2);
    }

    public static final ItemStack VACUUM_HOPPER_3 = ItemStackBuilder.rebar(Material.HOPPER, BaseKeys.VACUUM_HOPPER_3)
            .build();
    static {
        RebarItem.register(VacuumHopper.Item.class, VACUUM_HOPPER_3, BaseKeys.VACUUM_HOPPER_3);
        BasePages.SIMPLE_MACHINES.addItem(VACUUM_HOPPER_3);
    }

    public static final ItemStack VACUUM_HOPPER_4 = ItemStackBuilder.rebar(Material.HOPPER, BaseKeys.VACUUM_HOPPER_4)
            .build();
    static {
        RebarItem.register(VacuumHopper.Item.class, VACUUM_HOPPER_4, BaseKeys.VACUUM_HOPPER_4);
        BasePages.SIMPLE_MACHINES.addItem(VACUUM_HOPPER_4);
    }

    public static final ItemStack SHIMMER_PEDESTAL = ItemStackBuilder.rebar(Material.MOSSY_STONE_BRICK_WALL, BaseKeys.SHIMMER_PEDESTAL)
            .build();
    static {
        RebarItem.register(RebarItem.class, SHIMMER_PEDESTAL, BaseKeys.SHIMMER_PEDESTAL);
        BasePages.SIMPLE_MACHINES.addItem(SHIMMER_PEDESTAL);
    }

    public static final ItemStack SHIMMER_ALTAR = ItemStackBuilder.rebar(Material.SMOOTH_STONE_SLAB, BaseKeys.SHIMMER_ALTAR)
            .build();
    static {
        RebarItem.register(RebarItem.class, SHIMMER_ALTAR, BaseKeys.SHIMMER_ALTAR);
        BasePages.SIMPLE_MACHINES.addItem(SHIMMER_ALTAR);
    }

    //</editor-fold>

    //<editor-fold desc="Machines - Smelting" defaultstate=collapsed>
    public static final ItemStack PIT_KILN = ItemStackBuilder.rebar(Material.DECORATED_POT, BaseKeys.PIT_KILN)
            .build();
    static {
        RebarItem.register(PitKiln.Item.class, PIT_KILN, BaseKeys.PIT_KILN);
        BasePages.SMELTING.addItem(PIT_KILN);
    }

    public static final ItemStack BLOOMERY = ItemStackBuilder.rebar(Material.MAGMA_BLOCK, BaseKeys.BLOOMERY)
            .build();
    static {
        RebarItem.register(RebarItem.class, BLOOMERY, BaseKeys.BLOOMERY);
        BasePages.SMELTING.addItem(BLOOMERY);
    }

    public static final ItemStack BRONZE_ANVIL = ItemStackBuilder.rebar(Material.ANVIL, BaseKeys.BRONZE_ANVIL)
            .build();
    static {
        RebarItem.register(RebarItem.class, BRONZE_ANVIL, BaseKeys.BRONZE_ANVIL);
        BasePages.SMELTING.addItem(BRONZE_ANVIL);
    }

    public static final ItemStack SMELTERY_CONTROLLER = ItemStackBuilder.rebar(Material.BLAST_FURNACE, BaseKeys.SMELTERY_CONTROLLER)
            .build();
    static {
        RebarItem.register(RebarItem.class, SMELTERY_CONTROLLER, BaseKeys.SMELTERY_CONTROLLER);
        BasePages.SMELTING.addItem(SMELTERY_CONTROLLER);
    }

    public static final ItemStack SMELTERY_INPUT_HATCH = ItemStackBuilder.rebar(Material.LIGHT_BLUE_TERRACOTTA, BaseKeys.SMELTERY_INPUT_HATCH)
            .build();
    static {
        RebarItem.register(RebarItem.class, SMELTERY_INPUT_HATCH, BaseKeys.SMELTERY_INPUT_HATCH);
        BasePages.SMELTING.addItem(SMELTERY_INPUT_HATCH);
    }

    public static final ItemStack SMELTERY_OUTPUT_HATCH = ItemStackBuilder.rebar(Material.ORANGE_TERRACOTTA, BaseKeys.SMELTERY_OUTPUT_HATCH)
            .build();
    static {
        RebarItem.register(RebarItem.class, SMELTERY_OUTPUT_HATCH, BaseKeys.SMELTERY_OUTPUT_HATCH);
        BasePages.SMELTING.addItem(SMELTERY_OUTPUT_HATCH);
    }

    public static final ItemStack SMELTERY_HOPPER = ItemStackBuilder.rebar(Material.HOPPER, BaseKeys.SMELTERY_HOPPER)
            .build();
    static {
        RebarItem.register(RebarItem.class, SMELTERY_HOPPER, BaseKeys.SMELTERY_HOPPER);
        BasePages.SMELTING.addItem(SMELTERY_HOPPER);
    }

    public static final ItemStack SMELTERY_CASTER = ItemStackBuilder.rebar(Material.BRICKS, BaseKeys.SMELTERY_CASTER)
            .build();
    static {
        RebarItem.register(RebarItem.class, SMELTERY_CASTER, BaseKeys.SMELTERY_CASTER);
        BasePages.SMELTING.addItem(SMELTERY_CASTER);
    }

    public static final ItemStack SMELTERY_BURNER = ItemStackBuilder.rebar(Material.FURNACE, BaseKeys.SMELTERY_BURNER)
            .build();
    static {
        RebarItem.register(RebarItem.class, SMELTERY_BURNER, BaseKeys.SMELTERY_BURNER);
        BasePages.SMELTING.addItem(SMELTERY_BURNER);
    }

    public static final ItemStack DIESEL_SMELTERY_HEATER = ItemStackBuilder.rebar(Material.FURNACE, BaseKeys.DIESEL_SMELTERY_HEATER)
            .build();
    static {
        RebarItem.register(DieselSmelteryHeater.Item.class, DIESEL_SMELTERY_HEATER, BaseKeys.DIESEL_SMELTERY_HEATER);
        BasePages.SMELTING.addItem(DIESEL_SMELTERY_HEATER);
    }


    //</editor-fold>

    //<editor-fold desc="Machines - Fluid Pipes and Tanks" defaultstate=collapsed>

    public static final ItemStack FLUID_PIPE_WOOD = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_WOOD)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_WOOD).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        RebarItem.register(FluidPipe.class, FLUID_PIPE_WOOD);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_WOOD);
    }

    public static final ItemStack FLUID_PIPE_COPPER = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_COPPER)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_COPPER).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        RebarItem.register(FluidPipe.class, FLUID_PIPE_COPPER);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_COPPER);
    }

    public static final ItemStack FLUID_PIPE_TIN = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_TIN)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_TIN).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        RebarItem.register(FluidPipe.class, FLUID_PIPE_TIN);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_TIN);
    }

    public static final ItemStack FLUID_PIPE_IRON = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_IRON)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_IRON).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        RebarItem.register(FluidPipe.class, FLUID_PIPE_IRON);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_IRON);
    }

    public static final ItemStack FLUID_PIPE_BRONZE = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_BRONZE)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_BRONZE).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        RebarItem.register(FluidPipe.class, FLUID_PIPE_BRONZE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_BRONZE);
    }

    public static final ItemStack FLUID_PIPE_IGNEOUS_COMPOSITE = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_IGNEOUS_COMPOSITE)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_IGNEOUS_COMPOSITE).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        RebarItem.register(FluidPipe.class, FLUID_PIPE_IGNEOUS_COMPOSITE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_IGNEOUS_COMPOSITE);
    }

    public static final ItemStack FLUID_PIPE_STEEL = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_STEEL)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_STEEL).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        RebarItem.register(FluidPipe.class, FLUID_PIPE_STEEL);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_PIPE_STEEL);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_WOOD
            = ItemStackBuilder.rebar(Material.BROWN_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_WOOD)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, RebarSerializers.DOUBLE, 0.0))
            .build();
    static {
        RebarItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_WOOD,
                BaseKeys.PORTABLE_FLUID_TANK_WOOD
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_WOOD);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_COPPER
            = ItemStackBuilder.rebar(Material.ORANGE_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_COPPER)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, RebarSerializers.DOUBLE, 0.0))
            .build();
    static {
        RebarItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_COPPER,
                BaseKeys.PORTABLE_FLUID_TANK_COPPER
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_COPPER);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_TIN
            = ItemStackBuilder.rebar(Material.GREEN_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_TIN)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, RebarSerializers.DOUBLE, 0.0))
            .build();
    static {
        RebarItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_TIN,
                BaseKeys.PORTABLE_FLUID_TANK_TIN
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_TIN);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_IRON
            = ItemStackBuilder.rebar(Material.LIGHT_GRAY_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_IRON)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, RebarSerializers.DOUBLE, 0.0))
            .build();
    static {
        RebarItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_IRON,
                BaseKeys.PORTABLE_FLUID_TANK_IRON
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_IRON);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_BRONZE
            = ItemStackBuilder.rebar(Material.ORANGE_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_BRONZE)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, RebarSerializers.DOUBLE, 0.0))
            .build();
    static {
        RebarItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_BRONZE,
                BaseKeys.PORTABLE_FLUID_TANK_BRONZE
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_BRONZE);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE
            = ItemStackBuilder.rebar(Material.BLACK_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, RebarSerializers.DOUBLE, 0.0))
            .build();
    static {
        RebarItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE,
                BaseKeys.PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE);
    }

    public static final ItemStack PORTABLE_FLUID_TANK_STEEL
            = ItemStackBuilder.rebar(Material.GRAY_STAINED_GLASS, BaseKeys.PORTABLE_FLUID_TANK_STEEL)
            .editPdc(pdc -> pdc.set(PortableFluidTank.Item.FLUID_AMOUNT_KEY, RebarSerializers.DOUBLE, 0.0))
            .build();
    static {
        RebarItem.register(
                PortableFluidTank.Item.class,
                PORTABLE_FLUID_TANK_STEEL,
                BaseKeys.PORTABLE_FLUID_TANK_STEEL
        );
        BasePages.FLUID_PIPES_AND_TANKS.addItem(PORTABLE_FLUID_TANK_STEEL);
    }

    public static final ItemStack FLUID_TANK
            = ItemStackBuilder.rebar(Material.GRAY_TERRACOTTA, BaseKeys.FLUID_TANK)
            .build();
    static {
        RebarItem.register(FluidTank.Item.class, FLUID_TANK, BaseKeys.FLUID_TANK);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK);
    }

    public static final ItemStack FLUID_TANK_CASING_WOOD
            = ItemStackBuilder.rebar(Material.BROWN_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_WOOD)
            .build();
    static {
        RebarItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_WOOD, BaseKeys.FLUID_TANK_CASING_WOOD);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_WOOD);
    }

    public static final ItemStack FLUID_TANK_CASING_COPPER
            = ItemStackBuilder.rebar(Material.ORANGE_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_COPPER)
            .build();
    static {
        RebarItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_COPPER, BaseKeys.FLUID_TANK_CASING_COPPER);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_COPPER);
    }

    public static final ItemStack FLUID_TANK_CASING_TIN
            = ItemStackBuilder.rebar(Material.GREEN_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_TIN)
            .build();
    static {
        RebarItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_TIN, BaseKeys.FLUID_TANK_CASING_TIN);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_TIN);
    }

    public static final ItemStack FLUID_TANK_CASING_IRON
            = ItemStackBuilder.rebar(Material.LIGHT_GRAY_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_IRON)
            .build();
    static {
        RebarItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_IRON, BaseKeys.FLUID_TANK_CASING_IRON);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_IRON);
    }

    public static final ItemStack FLUID_TANK_CASING_BRONZE
            = ItemStackBuilder.rebar(Material.ORANGE_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_BRONZE)
            .build();
    static {
        RebarItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_BRONZE, BaseKeys.FLUID_TANK_CASING_BRONZE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_BRONZE);
    }

    public static final ItemStack FLUID_TANK_CASING_IGNEOUS_COMPOSITE
            = ItemStackBuilder.rebar(Material.BLACK_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_IGNEOUS_COMPOSITE)
            .build();
    static {
        RebarItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_IGNEOUS_COMPOSITE, BaseKeys.FLUID_TANK_CASING_IGNEOUS_COMPOSITE);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_IGNEOUS_COMPOSITE);
    }

    public static final ItemStack FLUID_TANK_CASING_STEEL
            = ItemStackBuilder.rebar(Material.GRAY_STAINED_GLASS, BaseKeys.FLUID_TANK_CASING_STEEL)
            .build();
    static {
        RebarItem.register(FluidTankCasing.Item.class, FLUID_TANK_CASING_STEEL, BaseKeys.FLUID_TANK_CASING_STEEL);
        BasePages.FLUID_PIPES_AND_TANKS.addItem(FLUID_TANK_CASING_STEEL);
    }

    //</editor-fold>

    //<editor-fold desc="Machines - Fluid Machines" defaultstate=collapsed>

    public static final ItemStack WATER_PUMP = ItemStackBuilder.rebar(Material.BLUE_TERRACOTTA, BaseKeys.WATER_PUMP)
            .build();
    static {
        RebarItem.register(WaterPump.Item.class, WATER_PUMP, BaseKeys.WATER_PUMP);
        BasePages.FLUID_MACHINES.addItem(WATER_PUMP);
    }

    public static final ItemStack FLUID_VALVE = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.FLUID_VALVE)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        RebarItem.register(FluidValve.Item.class, FLUID_VALVE, BaseKeys.FLUID_VALVE);
        BasePages.FLUID_MACHINES.addItem(FLUID_VALVE);
    }

    public static final ItemStack FLUID_FILTER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.FLUID_FILTER)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        RebarItem.register(FluidFilter.Item.class, FLUID_FILTER, BaseKeys.FLUID_FILTER);
        BasePages.FLUID_MACHINES.addItem(FLUID_FILTER);
    }

    public static final ItemStack FLUID_LIMITER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.FLUID_LIMITER)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        RebarItem.register(FluidLimiter.Item.class, FLUID_LIMITER, BaseKeys.FLUID_LIMITER);
        BasePages.FLUID_MACHINES.addItem(FLUID_LIMITER);
    }

    public static final ItemStack FLUID_METER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.FLUID_METER)
            .set(DataComponentTypes.ITEM_MODEL, Material.LIGHT_BLUE_STAINED_GLASS.getKey())
            .build();
    static {
        RebarItem.register(FluidMeter.Item.class, FLUID_METER, BaseKeys.FLUID_METER);
        BasePages.FLUID_MACHINES.addItem(FLUID_METER);
    }

    public static final ItemStack FLUID_ACCUMULATOR = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.FLUID_ACCUMULATOR)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        RebarItem.register(FluidAccumulator.Item.class, FLUID_ACCUMULATOR, BaseKeys.FLUID_ACCUMULATOR);
        BasePages.FLUID_MACHINES.addItem(FLUID_ACCUMULATOR);
    }

    public static final ItemStack WATER_PLACER = ItemStackBuilder.rebar(Material.DISPENSER, BaseKeys.WATER_PLACER)
            .build();
    static {
        RebarItem.register(FluidPlacer.Item.class, WATER_PLACER, BaseKeys.WATER_PLACER);
        BasePages.FLUID_MACHINES.addItem(WATER_PLACER);
    }

    public static final ItemStack LAVA_PLACER = ItemStackBuilder.rebar(Material.DISPENSER, BaseKeys.LAVA_PLACER)
            .build();
    static {
        RebarItem.register(FluidPlacer.Item.class, LAVA_PLACER, BaseKeys.LAVA_PLACER);
        BasePages.FLUID_MACHINES.addItem(LAVA_PLACER);
    }

    public static final ItemStack WATER_DRAINER = ItemStackBuilder.rebar(Material.DISPENSER, BaseKeys.WATER_DRAINER)
            .build();
    static {
        RebarItem.register(FluidDrainer.Item.class, WATER_DRAINER, BaseKeys.WATER_DRAINER);
        BasePages.FLUID_MACHINES.addItem(WATER_DRAINER);
    }

    public static final ItemStack LAVA_DRAINER = ItemStackBuilder.rebar(Material.DISPENSER, BaseKeys.LAVA_DRAINER)
            .build();
    static {
        RebarItem.register(FluidDrainer.Item.class, LAVA_DRAINER, BaseKeys.LAVA_DRAINER);
        BasePages.FLUID_MACHINES.addItem(LAVA_DRAINER);
    }

    public static final ItemStack FLUID_VOIDER_1 = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_1)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        RebarItem.register(FluidVoider.Item.class, FLUID_VOIDER_1, BaseKeys.FLUID_VOIDER_1);
        BasePages.FLUID_MACHINES.addItem(FLUID_VOIDER_1);
    }

    public static final ItemStack FLUID_VOIDER_2 = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_2)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        RebarItem.register(FluidVoider.Item.class, FLUID_VOIDER_2, BaseKeys.FLUID_VOIDER_2);
        BasePages.FLUID_MACHINES.addItem(FLUID_VOIDER_2);
    }

    public static final ItemStack FLUID_VOIDER_3 = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.FLUID_VOIDER_3)
            .set(DataComponentTypes.ITEM_MODEL, Material.BLACK_TERRACOTTA.getKey())
            .build();
    static {
        RebarItem.register(FluidVoider.Item.class, FLUID_VOIDER_3, BaseKeys.FLUID_VOIDER_3);
        BasePages.FLUID_MACHINES.addItem(FLUID_VOIDER_3);
    }

    //</editor-fold>

    //<editor-fold desc="Machines - Hydraulic Machines" defaultstate=collapsed>

    public static final ItemStack HYDRAULIC_GRINDSTONE_TURNER = ItemStackBuilder.rebar(Material.SMOOTH_STONE, BaseKeys.HYDRAULIC_GRINDSTONE_TURNER)
            .build();
    static {
        RebarItem.register(HydraulicGrindstoneTurner.Item.class, HYDRAULIC_GRINDSTONE_TURNER, BaseKeys.HYDRAULIC_GRINDSTONE_TURNER);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_GRINDSTONE_TURNER);
    }

    public static final ItemStack HYDRAULIC_MIXING_ATTACHMENT = ItemStackBuilder.rebar(Material.CHISELED_STONE_BRICKS, BaseKeys.HYDRAULIC_MIXING_ATTACHMENT)
            .build();
    static {
        RebarItem.register(HydraulicMixingAttachment.Item.class, HYDRAULIC_MIXING_ATTACHMENT, BaseKeys.HYDRAULIC_MIXING_ATTACHMENT);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_MIXING_ATTACHMENT);
    }

    public static final ItemStack HYDRAULIC_PRESS_PISTON = ItemStackBuilder.rebar(Material.BROWN_TERRACOTTA, BaseKeys.HYDRAULIC_PRESS_PISTON)
            .build();
    static {
        RebarItem.register(HydraulicPressPiston.Item.class, HYDRAULIC_PRESS_PISTON, BaseKeys.HYDRAULIC_PRESS_PISTON);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_PRESS_PISTON);
    }

    public static final ItemStack HYDRAULIC_HAMMER_HEAD = ItemStackBuilder.rebar(Material.STONE_BRICKS, BaseKeys.HYDRAULIC_HAMMER_HEAD)
            .build();
    static {
        RebarItem.register(HydraulicHammerHead.Item.class, HYDRAULIC_HAMMER_HEAD, BaseKeys.HYDRAULIC_HAMMER_HEAD);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_HAMMER_HEAD);
    }

    public static final ItemStack HYDRAULIC_PIPE_BENDER = ItemStackBuilder.rebar(Material.WAXED_CHISELED_COPPER, BaseKeys.HYDRAULIC_PIPE_BENDER)
            .build();
    static {
        RebarItem.register(HydraulicPipeBender.Item.class, HYDRAULIC_PIPE_BENDER, BaseKeys.HYDRAULIC_PIPE_BENDER);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_PIPE_BENDER);
    }

    public static final ItemStack HYDRAULIC_TABLE_SAW = ItemStackBuilder.rebar(Material.WAXED_CUT_COPPER, BaseKeys.HYDRAULIC_TABLE_SAW)
            .build();
    static {
        RebarItem.register(HydraulicTableSaw.Item.class, HYDRAULIC_TABLE_SAW, BaseKeys.HYDRAULIC_TABLE_SAW);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_TABLE_SAW);
    }

    public static final ItemStack HYDRAULIC_FARMER = ItemStackBuilder.rebar(Material.WAXED_EXPOSED_COPPER_BULB, BaseKeys.HYDRAULIC_FARMER)
            .build();
    static {
        RebarItem.register(HydraulicFarmer.Item.class, HYDRAULIC_FARMER, BaseKeys.HYDRAULIC_FARMER);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_FARMER);
    }

    public static final ItemStack HYDRAULIC_MINER = ItemStackBuilder.rebar(Material.WAXED_EXPOSED_CHISELED_COPPER, BaseKeys.HYDRAULIC_MINER)
            .build();
    static {
        RebarItem.register(HydraulicMiner.Item.class, HYDRAULIC_MINER, BaseKeys.HYDRAULIC_MINER);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_MINER);
    }

    public static final ItemStack HYDRAULIC_BREAKER = ItemStackBuilder.rebar(Material.WAXED_EXPOSED_CUT_COPPER, BaseKeys.HYDRAULIC_BREAKER)
            .build();
    static {
        RebarItem.register(HydraulicBreaker.Item.class, HYDRAULIC_BREAKER, BaseKeys.HYDRAULIC_BREAKER);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_BREAKER);
    }

    public static final ItemStack HYDRAULIC_REFUELING_STATION = ItemStackBuilder.rebar(Material.WAXED_CUT_COPPER_SLAB, BaseKeys.HYDRAULIC_REFUELING_STATION)
            .build();
    static {
        RebarItem.register(RebarItem.class, HYDRAULIC_REFUELING_STATION, BaseKeys.HYDRAULIC_REFUELING_STATION);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_REFUELING_STATION);
    }

    public static final ItemStack HYDRAULIC_CORE_DRILL = ItemStackBuilder.rebar(Material.WAXED_COPPER_BULB, BaseKeys.HYDRAULIC_CORE_DRILL)
            .build();
    static {
        RebarItem.register(HydraulicCoreDrill.Item.class, HYDRAULIC_CORE_DRILL, BaseKeys.HYDRAULIC_CORE_DRILL);
        BasePages.HYDRAULIC_MACHINES.addItem(HYDRAULIC_CORE_DRILL);
    }

    //</editor-fold>

    //<editor-fold desc="Machines - Hydraulic Purification" defaultstate="collapsed">

    public static final ItemStack COAL_FIRED_PURIFICATION_TOWER = ItemStackBuilder.rebar(Material.BLAST_FURNACE, BaseKeys.COAL_FIRED_PURIFICATION_TOWER)
            .build();
    static {
        RebarItem.register(CoalFiredPurificationTower.Item.class, COAL_FIRED_PURIFICATION_TOWER, BaseKeys.COAL_FIRED_PURIFICATION_TOWER);
        BasePages.HYDRAULIC_PURIFICATION.addItem(COAL_FIRED_PURIFICATION_TOWER);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_1 = ItemStackBuilder.rebar(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_1)
            .build();
    static {
        RebarItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_1, BaseKeys.SOLAR_PURIFICATION_TOWER_1);
        BasePages.HYDRAULIC_PURIFICATION.addItem(SOLAR_PURIFICATION_TOWER_1);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_2 = ItemStackBuilder.rebar(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_2)
            .build();
    static {
        RebarItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_2, BaseKeys.SOLAR_PURIFICATION_TOWER_2);
        BasePages.HYDRAULIC_PURIFICATION.addItem(SOLAR_PURIFICATION_TOWER_2);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_3 = ItemStackBuilder.rebar(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_3)
            .build();
    static {
        RebarItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_3, BaseKeys.SOLAR_PURIFICATION_TOWER_3);
        BasePages.HYDRAULIC_PURIFICATION.addItem(SOLAR_PURIFICATION_TOWER_3);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_4 = ItemStackBuilder.rebar(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_4)
            .build();
    static {
        RebarItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_4, BaseKeys.SOLAR_PURIFICATION_TOWER_4);
        BasePages.HYDRAULIC_PURIFICATION.addItem(SOLAR_PURIFICATION_TOWER_4);
    }

    public static final ItemStack SOLAR_PURIFICATION_TOWER_5 = ItemStackBuilder.rebar(Material.WAXED_COPPER_BLOCK, BaseKeys.SOLAR_PURIFICATION_TOWER_5)
            .build();
    static {
        RebarItem.register(SolarPurificationTower.Item.class, SOLAR_PURIFICATION_TOWER_5, BaseKeys.SOLAR_PURIFICATION_TOWER_5);
        BasePages.HYDRAULIC_PURIFICATION.addItem(SOLAR_PURIFICATION_TOWER_5);
    }

    public static final ItemStack SOLAR_LENS = ItemStackBuilder.rebar(Material.GLASS_PANE, BaseKeys.SOLAR_LENS)
            .build();
    static {
        RebarItem.register(RebarItem.class, SOLAR_LENS, BaseKeys.SOLAR_LENS);
        BasePages.HYDRAULIC_PURIFICATION.addItem(SOLAR_LENS);
    }

    public static final ItemStack PURIFICATION_TOWER_GLASS = ItemStackBuilder.rebar(Material.LIGHT_GRAY_STAINED_GLASS, BaseKeys.PURIFICATION_TOWER_GLASS)
            .build();
    static {
        RebarItem.register(RebarItem.class, PURIFICATION_TOWER_GLASS, BaseKeys.PURIFICATION_TOWER_GLASS);
        BasePages.HYDRAULIC_PURIFICATION.addItem(PURIFICATION_TOWER_GLASS);
    }

    public static final ItemStack PURIFICATION_TOWER_CAP = ItemStackBuilder.rebar(Material.QUARTZ_SLAB, BaseKeys.PURIFICATION_TOWER_CAP)
            .build();
    static {
        RebarItem.register(RebarItem.class, PURIFICATION_TOWER_CAP, BaseKeys.PURIFICATION_TOWER_CAP);
        BasePages.HYDRAULIC_PURIFICATION.addItem(PURIFICATION_TOWER_CAP);
    }

    //</editor-fold>

    //<editor-fold desc="Machines - Cargo" defaultstate="collapsed">

    public static final ItemStack CARGO_DUCT = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_DUCT)
            .set(DataComponentTypes.ITEM_MODEL, Material.GRAY_CONCRETE.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, CARGO_DUCT, BaseKeys.CARGO_DUCT);
        BasePages.CARGO.addItem(CARGO_DUCT);
    }

    public static final ItemStack CARGO_EXTRACTOR = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_EXTRACTOR)
            .set(DataComponentTypes.ITEM_MODEL, Material.RED_TERRACOTTA.getKey())
            .build();
    static {
        RebarItem.register(CargoExtractor.Item.class, CARGO_EXTRACTOR, BaseKeys.CARGO_EXTRACTOR);
        BasePages.CARGO.addItem(CARGO_EXTRACTOR);
    }

    public static final ItemStack CARGO_INSERTER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_INSERTER)
            .set(DataComponentTypes.ITEM_MODEL, Material.LIME_TERRACOTTA.getKey())
            .build();
    static {
        RebarItem.register(CargoInserter.Item.class, CARGO_INSERTER, BaseKeys.CARGO_INSERTER);
        BasePages.CARGO.addItem(CARGO_INSERTER);
    }

    public static final ItemStack CARGO_VALVE = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_VALVE)
            .set(DataComponentTypes.ITEM_MODEL, Material.WHITE_CONCRETE.getKey())
            .build();
    static {
        RebarItem.register(CargoValve.Item.class, CARGO_VALVE, BaseKeys.CARGO_VALVE);
        BasePages.CARGO.addItem(CARGO_VALVE);
    }

    public static final ItemStack CARGO_BUFFER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_BUFFER)
            .set(DataComponentTypes.ITEM_MODEL, Material.BARREL.getKey())
            .build();
    static {
        RebarItem.register(CargoBuffer.Item.class, CARGO_BUFFER, BaseKeys.CARGO_BUFFER);
        BasePages.CARGO.addItem(CARGO_BUFFER);
    }

    public static final ItemStack CARGO_MONITOR = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_MONITOR)
            .set(DataComponentTypes.ITEM_MODEL, Material.PINK_STAINED_GLASS.getKey())
            .build();
    static {
        RebarItem.register(CargoMonitor.Item.class, CARGO_MONITOR, BaseKeys.CARGO_MONITOR);
        BasePages.CARGO.addItem(CARGO_MONITOR);
    }

    public static final ItemStack CARGO_METER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_METER)
            .set(DataComponentTypes.ITEM_MODEL, Material.LIGHT_BLUE_STAINED_GLASS.getKey())
            .build();
    static {
        RebarItem.register(CargoMeter.Item.class, CARGO_METER, BaseKeys.CARGO_METER);
        BasePages.CARGO.addItem(CARGO_METER);
    }

    public static final ItemStack CARGO_SPLITTER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_SPLITTER)
            .set(DataComponentTypes.ITEM_MODEL, Material.STRIPPED_CRIMSON_STEM.getKey())
            .build();
    static {
        RebarItem.register(CargoSplitter.Item.class, CARGO_SPLITTER, BaseKeys.CARGO_SPLITTER);
        BasePages.CARGO.addItem(CARGO_SPLITTER);
    }

    public static final ItemStack CARGO_MERGER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_MERGER)
            .set(DataComponentTypes.ITEM_MODEL, Material.STRIPPED_WARPED_STEM.getKey())
            .build();
    static {
        RebarItem.register(CargoSplitter.Item.class, CARGO_MERGER, BaseKeys.CARGO_MERGER);
        BasePages.CARGO.addItem(CARGO_MERGER);
    }

    public static final ItemStack CARGO_FILTER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_FILTER)
            .set(DataComponentTypes.ITEM_MODEL, Material.COMPARATOR.getKey())
            .build();
    static {
        RebarItem.register(CargoValve.Item.class, CARGO_FILTER, BaseKeys.CARGO_FILTER);
        BasePages.CARGO.addItem(CARGO_FILTER);
    }

    public static final ItemStack CARGO_OVERFLOW_GATE = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_OVERFLOW_GATE)
            .set(DataComponentTypes.ITEM_MODEL, Material.CRIMSON_STEM.getKey())
            .build();
    static {
        RebarItem.register(CargoOverflowGate.Item.class, CARGO_OVERFLOW_GATE, BaseKeys.CARGO_OVERFLOW_GATE);
        BasePages.CARGO.addItem(CARGO_OVERFLOW_GATE);
    }

    public static final ItemStack CARGO_GATE = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_GATE)
            .set(DataComponentTypes.ITEM_MODEL, Material.REPEATER.getKey())
            .build();
    static {
        RebarItem.register(CargoGate.Item.class, CARGO_GATE, BaseKeys.CARGO_GATE);
        BasePages.CARGO.addItem(CARGO_GATE);
    }

    public static final ItemStack CARGO_ACCUMULATOR = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_ACCUMULATOR)
            .set(DataComponentTypes.ITEM_MODEL, Material.REDSTONE_LAMP.getKey())
            .build();
    static {
        RebarItem.register(CargoAccumulator.Item.class, CARGO_ACCUMULATOR, BaseKeys.CARGO_ACCUMULATOR);
        BasePages.CARGO.addItem(CARGO_ACCUMULATOR);
    }

    public static final ItemStack CARGO_FLUID_ACCUMULATOR = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CARGO_FLUID_ACCUMULATOR)
            .set(DataComponentTypes.ITEM_MODEL, Material.NOTE_BLOCK.getKey())
            .build();
    static {
        RebarItem.register(CargoFluidAccumulator.Item.class, CARGO_FLUID_ACCUMULATOR, BaseKeys.CARGO_FLUID_ACCUMULATOR);
        BasePages.CARGO.addItem(CARGO_FLUID_ACCUMULATOR);
    }
    //</editor-fold>

    //<editor-fold desc="Machines - Diesel Machines" defaultstate="collapsed">

    public static final ItemStack DIESEL_GRINDSTONE = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.DIESEL_GRINDSTONE)
            .set(DataComponentTypes.ITEM_MODEL, Material.SMOOTH_STONE_SLAB.getKey())
            .build();
    static {
        RebarItem.register(DieselGrindstone.Item.class, DIESEL_GRINDSTONE, BaseKeys.DIESEL_GRINDSTONE);
        BasePages.DIESEL_MACHINES.addItem(DIESEL_GRINDSTONE);
    }

    public static final ItemStack DIESEL_MIXING_ATTACHMENT = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.DIESEL_MIXING_ATTACHMENT)
            .set(DataComponentTypes.ITEM_MODEL, Material.LIGHT_GRAY_CONCRETE.getKey())
            .build();
    static {
        RebarItem.register(DieselMixingAttachment.Item.class, DIESEL_MIXING_ATTACHMENT, BaseKeys.DIESEL_MIXING_ATTACHMENT);
        BasePages.DIESEL_MACHINES.addItem(DIESEL_MIXING_ATTACHMENT);
    }

    public static final ItemStack DIESEL_PRESS = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.DIESEL_PRESS)
            .set(DataComponentTypes.ITEM_MODEL, Material.COMPOSTER.getKey())
            .build();
    static {
        RebarItem.register(DieselPress.Item.class, DIESEL_PRESS, BaseKeys.DIESEL_PRESS);
        BasePages.DIESEL_MACHINES.addItem(DIESEL_PRESS);
    }

    public static final ItemStack DIESEL_HAMMER_HEAD = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.DIESEL_HAMMER_HEAD)
            .set(DataComponentTypes.ITEM_MODEL, Material.GRAY_CONCRETE.getKey())
            .build();
    static {
        RebarItem.register(DieselHammerHead.Item.class, DIESEL_HAMMER_HEAD, BaseKeys.DIESEL_HAMMER_HEAD);
        BasePages.DIESEL_MACHINES.addItem(DIESEL_HAMMER_HEAD);
    }

    public static final ItemStack DIESEL_PIPE_BENDER = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.DIESEL_PIPE_BENDER)
            .build();
    static {
        RebarItem.register(DieselPipeBender.Item.class, DIESEL_PIPE_BENDER, BaseKeys.DIESEL_PIPE_BENDER);
        BasePages.DIESEL_MACHINES.addItem(DIESEL_PIPE_BENDER);
    }

    public static final ItemStack DIESEL_TABLE_SAW = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.DIESEL_TABLE_SAW)
            .set(DataComponentTypes.ITEM_MODEL, Material.IRON_BARS.getKey())
            .build();
    static {
        RebarItem.register(DieselTableSaw.Item.class, DIESEL_TABLE_SAW, BaseKeys.DIESEL_TABLE_SAW);
        BasePages.DIESEL_MACHINES.addItem(DIESEL_TABLE_SAW);
    }

    public static final ItemStack DIESEL_MINER = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.DIESEL_MINER)
            .set(DataComponentTypes.ITEM_MODEL, Material.YELLOW_TERRACOTTA.getKey())
            .build();
    static {
        RebarItem.register(DieselMiner.Item.class, DIESEL_MINER, BaseKeys.DIESEL_MINER);
        BasePages.DIESEL_MACHINES.addItem(DIESEL_MINER);
    }

    public static final ItemStack DIESEL_BREAKER = ItemStackBuilder.rebar(Material.DROPPER, BaseKeys.DIESEL_BREAKER)
            .build();
    static {
        RebarItem.register(DieselBreaker.Item.class, DIESEL_BREAKER, BaseKeys.DIESEL_BREAKER);
        BasePages.DIESEL_MACHINES.addItem(DIESEL_BREAKER);
    }

    public static final ItemStack DIESEL_FURNACE = ItemStackBuilder.rebar(Material.FURNACE, BaseKeys.DIESEL_FURNACE)
            .build();
    static {
        RebarItem.register(DieselFurnace.Item.class, DIESEL_FURNACE, BaseKeys.DIESEL_FURNACE);
        BasePages.DIESEL_MACHINES.addItem(DIESEL_FURNACE);
    }

    public static final ItemStack DIESEL_BRICK_MOLDER = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.DIESEL_BRICK_MOLDER)
            .set(DataComponentTypes.ITEM_MODEL, Material.OAK_PLANKS.getKey())
            .build();
    static {
        RebarItem.register(DieselBrickMolder.Item.class, DIESEL_BRICK_MOLDER, BaseKeys.DIESEL_BRICK_MOLDER);
        BasePages.DIESEL_MACHINES.addItem(DIESEL_BRICK_MOLDER);
    }

    //</editor-fold>

    //<editor-fold desc="Machines - Diesel Production" defaultstate="collapsed">

    public static final ItemStack FERMENTER = ItemStackBuilder.rebar(Material.PINK_TERRACOTTA, BaseKeys.FERMENTER)
            .build();
    static {
        RebarItem.register(Fermenter.Item.class, FERMENTER, BaseKeys.FERMENTER);
        BasePages.DIESEL_PRODUCTION.addItem(FERMENTER);
    }

    public static final ItemStack FERMENTER_CORE = ItemStackBuilder.rebar(Material.GRAY_STAINED_GLASS, BaseKeys.FERMENTER_CORE)
            .build();
    static {
        RebarItem.register(RebarItem.class, FERMENTER_CORE, BaseKeys.FERMENTER_CORE);
        BasePages.DIESEL_PRODUCTION.addItem(FERMENTER_CORE);
    }

    public static final ItemStack FERMENTER_CASING = ItemStackBuilder.rebar(Material.GRAY_STAINED_GLASS_PANE, BaseKeys.FERMENTER_CASING)
            .build();
    static {
        RebarItem.register(RebarItem.class, FERMENTER_CASING, BaseKeys.FERMENTER_CASING);
        BasePages.DIESEL_PRODUCTION.addItem(FERMENTER_CASING);
    }

    public static final ItemStack BIOREFINERY = ItemStackBuilder.rebar(Material.PURPLE_TERRACOTTA, BaseKeys.BIOREFINERY)
            .build();
    static {
        RebarItem.register(Biorefinery.Item.class, BIOREFINERY, BaseKeys.BIOREFINERY);
        BasePages.DIESEL_PRODUCTION.addItem(BIOREFINERY);
    }

    public static final ItemStack BIOREFINERY_FOUNDATION = ItemStackBuilder.rebar(Material.LIGHT_GRAY_CONCRETE, BaseKeys.BIOREFINERY_FOUNDATION)
            .build();
    static {
        RebarItem.register(RebarItem.class, BIOREFINERY_FOUNDATION, BaseKeys.BIOREFINERY_FOUNDATION);
        BasePages.DIESEL_PRODUCTION.addItem(BIOREFINERY_FOUNDATION);
    }

    public static final ItemStack BIOREFINERY_PLATING = ItemStackBuilder.rebar(Material.GRAY_STAINED_GLASS_PANE, BaseKeys.BIOREFINERY_PLATING)
            .build();
    static {
        RebarItem.register(RebarItem.class, BIOREFINERY_PLATING, BaseKeys.BIOREFINERY_PLATING);
        BasePages.DIESEL_PRODUCTION.addItem(BIOREFINERY_PLATING);
    }

    public static final ItemStack BIOREFINERY_TOWER_RING = ItemStackBuilder.rebar(Material.IRON_BLOCK, BaseKeys.BIOREFINERY_TOWER_RING)
            .build();
    static {
        RebarItem.register(RebarItem.class, BIOREFINERY_TOWER_RING, BaseKeys.BIOREFINERY_TOWER_RING);
        BasePages.DIESEL_PRODUCTION.addItem(BIOREFINERY_TOWER_RING);
    }

    public static final ItemStack BIOREFINERY_SMOKESTACK_RING = ItemStackBuilder.rebar(Material.POLISHED_DEEPSLATE_WALL, BaseKeys.BIOREFINERY_SMOKESTACK_RING)
            .build();
    static {
        RebarItem.register(RebarItem.class, BIOREFINERY_SMOKESTACK_RING, BaseKeys.BIOREFINERY_SMOKESTACK_RING);
        BasePages.DIESEL_PRODUCTION.addItem(BIOREFINERY_SMOKESTACK_RING);
    }

    public static final ItemStack BIOREFINERY_SMOKESTACK_CAP = ItemStackBuilder.rebar(Material.FLOWER_POT, BaseKeys.BIOREFINERY_SMOKESTACK_CAP)
            .build();
    static {
        RebarItem.register(RebarItem.class, BIOREFINERY_SMOKESTACK_CAP, BaseKeys.BIOREFINERY_SMOKESTACK_CAP);
        BasePages.DIESEL_PRODUCTION.addItem(BIOREFINERY_SMOKESTACK_CAP);
    }

    //</editor-fold>

    //<editor-fold desc="Creative Items" defaultstate=collapsed>

    public static final ItemStack CREATIVE_FLUID_VOIDER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CREATIVE_FLUID_VOIDER)
            .set(DataComponentTypes.ITEM_MODEL, Material.PINK_CONCRETE.getKey())
            .build();
    static {
        RebarItem.register(FluidVoider.Item.class, CREATIVE_FLUID_VOIDER, BaseKeys.CREATIVE_FLUID_VOIDER);
        RebarGuide.hideItemUnlessAdmin(BaseKeys.CREATIVE_FLUID_VOIDER);
        BasePages.CREATIVE_ITEMS.addItem(CREATIVE_FLUID_VOIDER);
    }

    public static final ItemStack CREATIVE_FLUID_SOURCE = ItemStackBuilder.rebar(Material.PINK_CONCRETE, BaseKeys.CREATIVE_FLUID_SOURCE)
            .build();
    static {
        RebarItem.register(RebarItem.class, CREATIVE_FLUID_SOURCE, BaseKeys.CREATIVE_FLUID_SOURCE);
        RebarGuide.hideItemUnlessAdmin(BaseKeys.CREATIVE_FLUID_SOURCE);
        BasePages.CREATIVE_ITEMS.addItem(CREATIVE_FLUID_SOURCE);
    }

    public static final ItemStack FLUID_PIPE_CREATIVE = ItemStackBuilder.rebar(Material.CLAY_BALL, BaseKeys.FLUID_PIPE_CREATIVE)
            .set(
                    DataComponentTypes.ITEM_MODEL,
                    Settings.get(BaseKeys.FLUID_PIPE_CREATIVE).getOrThrow("material", ConfigAdapter.MATERIAL).key()
            )
            .build();
    static {
        RebarItem.register(FluidPipe.class, FLUID_PIPE_CREATIVE, BaseKeys.FLUID_PIPE_CREATIVE);
        RebarGuide.hideItemUnlessAdmin(BaseKeys.CREATIVE_FLUID_SOURCE);
        BasePages.CREATIVE_ITEMS.addItem(FLUID_PIPE_CREATIVE);
    }

    public static final ItemStack CREATIVE_ITEM_SOURCE = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CREATIVE_ITEM_SOURCE)
            .set(DataComponentTypes.ITEM_MODEL, Material.PINK_TERRACOTTA.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, CREATIVE_ITEM_SOURCE, BaseKeys.CREATIVE_ITEM_SOURCE);
        RebarGuide.hideItem(BaseKeys.CREATIVE_ITEM_SOURCE);
        BasePages.CREATIVE_ITEMS.addItem(CREATIVE_ITEM_SOURCE);
    }

    public static final ItemStack CREATIVE_ITEM_VOIDER = ItemStackBuilder.rebar(Material.STRUCTURE_VOID, BaseKeys.CREATIVE_ITEM_VOIDER)
            .set(DataComponentTypes.ITEM_MODEL, Material.PINK_TERRACOTTA.getKey())
            .build();
    static {
        RebarItem.register(RebarItem.class, CREATIVE_ITEM_VOIDER, BaseKeys.CREATIVE_ITEM_VOIDER);
        RebarGuide.hideItem(BaseKeys.CREATIVE_ITEM_VOIDER);
        BasePages.CREATIVE_ITEMS.addItem(CREATIVE_ITEM_VOIDER);
    }

    //</editor-fold>

    public static final ItemStack CLEANSING_POTION = ItemStackBuilder.rebar(Material.SPLASH_POTION, BaseKeys.CLEANSING_POTION)
            .set(DataComponentTypes.POTION_CONTENTS, PotionContents.potionContents()
                    .customColor(Color.FUCHSIA)
                    .build())
            .build();
    static {
        RebarItem.register(CleansingPotion.class, CLEANSING_POTION);
        BasePages.TOOLS.addItem(CLEANSING_POTION);

        // This recipe isn't configured because we currently have no way to set the healing potion data on it
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

    static {
        BasePages.initialise();
    }

    // Calling this method forces all the static blocks to run, which initializes our items
    public static void initialize() {
    }
}
