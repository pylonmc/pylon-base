package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.core.item.research.Research;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


@UtilityClass
public class BaseResearches {

    public void initialize() {
        new Research(
                baseKey("baking"),
                Material.YELLOW_DYE,
                1L,
                BaseKeys.FLOUR,
                BaseKeys.DOUGH
        ).register();

        new Research(
                baseKey("showing_off"),
                Material.STONE_BRICK_WALL,
                2L,
                BaseKeys.PEDESTAL
        ).register();

        new Research(
                baseKey("climbing_equipment"),
                Material.IRON_HOE,
                2L,
                BaseKeys.CLIMBING_PICK
        ).register();

        new Research(
                baseKey("food_preservation"),
                Material.ROTTEN_FLESH,
                3L,
                BaseKeys.MONSTER_JERKY
        ).register();

        new Research(
                baseKey("fletching"),
                Material.ARROW,
                4L,
                BaseKeys.RECOIL_ARROW,
                BaseKeys.ICE_ARROW
        ).register();

        new Research(
                baseKey("first_aid"),
                Material.COBWEB,
                5L,
                BaseKeys.BANDAGE,
                BaseKeys.SPLINT
        ).register();

        new Research(
                baseKey("molding"),
                Material.OAK_FENCE_GATE,
                5L,
                BaseKeys.BRICK_MOLD
        ).register();

        new Research(
                baseKey("shimmer_manipulation"),
                Material.SUGAR,
                7L,
                BaseKeys.SHIMMER_DUST_2,
                BaseKeys.COVALENT_BINDER,
                BaseKeys.ENRICHED_NETHERRACK
        ).register();

        new Research(
                baseKey("irrigation"),
                Material.BUCKET,
                8L,
                BaseKeys.WATERING_CAN,
                BaseKeys.SPRINKLER
        ).register();

        new Research(
                baseKey("explosive_targets"),
                Material.ARROW,
                8L,
                BaseKeys.EXPLOSIVE_TARGET,
                BaseKeys.EXPLOSIVE_TARGET_FIERY,
                BaseKeys.EXPLOSIVE_TARGET_SUPER,
                BaseKeys.EXPLOSIVE_TARGET_SUPER_FIERY
        ).register();

        new Research(
                baseKey("simple_components"),
                Material.IRON_TRAPDOOR,
                8L,
                BaseKeys.ROTOR,
                BaseKeys.BACKFLOW_VALVE,
                BaseKeys.ANALOGUE_DISPLAY,
                BaseKeys.FILTER_MESH,
                BaseKeys.NOZZLE,
                BaseKeys.ABYSSAL_CATALYST,
                BaseKeys.AXLE,
                BaseKeys.SAWBLADE,
                BaseKeys.WEIGHTED_SHAFT,
                BaseKeys.BRONZE_DRILL_BIT,
                BaseKeys.HYDRAULIC_MOTOR
        ).register();

        new Research(
                baseKey("vacuum_hoppers"),
                Material.HOPPER,
                8L,
                BaseKeys.VACUUM_HOPPER_1,
                BaseKeys.VACUUM_HOPPER_2
        ).register();

        new Research(
                baseKey("scientific_revolution_1"),
                Material.RED_BANNER,
                10L,
                BaseKeys.RESEARCH_PACK_1
        ).register();

        new Research(
                baseKey("medicine"),
                Material.BREWER_POTTERY_SHERD,
                10L,
                BaseKeys.DISINFECTANT,
                BaseKeys.MEDKIT
        ).register();

        new Research(
                baseKey("primitive_alloying"),
                Material.IRON_INGOT,
                10L,
                BaseKeys.PIT_KILN,
                BaseKeys.CARBON,
                BaseKeys.SULFUR,
                BaseKeys.BRONZE_INGOT,
                BaseKeys.BRONZE_DUST,
                BaseKeys.BRONZE_SHEET,
                BaseKeys.BRONZE_NUGGET,
                BaseKeys.BRONZE_BLOCK
        ).register();

        new Research(
                baseKey("bronze_equipment"),
                Material.BRICK,
                10L,
                BaseKeys.BRONZE_SWORD,
                BaseKeys.BRONZE_AXE,
                BaseKeys.BRONZE_PICKAXE,
                BaseKeys.BRONZE_SHOVEL,
                BaseKeys.BRONZE_HOE,
                BaseKeys.BRONZE_BOOTS,
                BaseKeys.BRONZE_LEGGINGS,
                BaseKeys.BRONZE_CHESTPLATE,
                BaseKeys.BRONZE_HELMET
        ).register();

        new Research(
                baseKey("elevation"),
                Material.QUARTZ_SLAB,
                10L,
                BaseKeys.ELEVATOR_1,
                BaseKeys.ELEVATOR_2,
                BaseKeys.ELEVATOR_3
        ).register();

        new Research(
                baseKey("fluid_storage"),
                Material.GRAY_TERRACOTTA,
                10L,
                BaseKeys.FLUID_TANK,
                BaseKeys.FLUID_TANK_CASING_WOOD,
                BaseKeys.FLUID_TANK_CASING_COPPER,
                BaseKeys.FLUID_TANK_CASING_TIN,
                BaseKeys.FLUID_TANK_CASING_IRON
        ).register();

        new Research(
                baseKey("pressing"),
                Material.COMPOSTER,
                12L,
                BaseKeys.PRESS
        ).register();

        new Research(
                baseKey("flow_control"),
                Material.WHITE_CONCRETE,
                12L,
                BaseKeys.FLUID_VALVE,
                BaseKeys.FLUID_FILTER,
                BaseKeys.FLUID_METER
        ).register();

        new Research(
                baseKey("pumps"),
                Material.LIGHT_BLUE_TERRACOTTA,
                12L,
                BaseKeys.WATER_PUMP,
                BaseKeys.WATER_PLACER,
                BaseKeys.WATER_DRAINER,
                BaseKeys.LAVA_PLACER,
                BaseKeys.LAVA_DRAINER
        ).register();

        new Research(
                baseKey("improved_drilling_techniques"),
                Material.IRON_PICKAXE,
                15L,
                BaseKeys.IMPROVED_MANUAL_CORE_DRILL,
                BaseKeys.SUBSURFACE_CORE_CHUNK
        ).register();

        new Research(
                baseKey("better_hammers"),
                Material.STONE_PICKAXE,
                15L,
                BaseKeys.HAMMER_IRON,
                BaseKeys.HAMMER_DIAMOND
        ).register();

        new Research(
                baseKey("vitality_1"),
                Material.AMETHYST_SHARD,
                15L,
                BaseKeys.HEALTH_TALISMAN_SIMPLE,
                BaseKeys.HEALTH_TALISMAN_ADVANCED
        ).register();

        new Research(
                baseKey("hydraulic_purification"),
                Material.WAXED_COPPER_BLOCK,
                15L,
                BaseKeys.SOLAR_PURIFICATION_TOWER_1,
                BaseKeys.SOLAR_PURIFICATION_TOWER_2,
                BaseKeys.SOLAR_PURIFICATION_TOWER_3,
                BaseKeys.COAL_FIRED_PURIFICATION_TOWER,
                BaseKeys.PURIFICATION_TOWER_GLASS,
                BaseKeys.PURIFICATION_TOWER_CAP,
                BaseKeys.SOLAR_LENS
        ).register();

        new Research(
                baseKey("fluid_handling_1"),
                Material.ORANGE_TERRACOTTA,
                15L,
                BaseKeys.FLUID_PIPE_BRONZE,
                BaseKeys.PORTABLE_FLUID_TANK_BRONZE,
                BaseKeys.FLUID_TANK_CASING_BRONZE
        ).register();

        new Research(
                baseKey("igneous_magic"),
                Material.OBSIDIAN,
                18L,
                BaseKeys.OBSIDIAN_CHIP,
                BaseKeys.IGNEOUS_COMPOSITE
        ).register();

        new Research(
                baseKey("forbidden_medicine"),
                Material.SPLASH_POTION,
                18L,
                BaseKeys.CLEANSING_POTION
        ).register();

        new Research(
                baseKey("advanced_vacuum_hoppers"),
                Material.HOPPER,
                8L,
                BaseKeys.VACUUM_HOPPER_3,
                BaseKeys.VACUUM_HOPPER_4
        ).register();

        new Research(
                baseKey("portable_workstations"),
                Material.CRAFTING_TABLE,
                20L,
                BaseKeys.PORTABILITY_CATALYST,
                BaseKeys.PORTABLE_CRAFTING_TABLE,
                BaseKeys.PORTABLE_DUSTBIN,
                BaseKeys.PORTABLE_ENDER_CHEST
        ).register();

        new Research(
                baseKey("fluid_voiding_1"),
                Material.BLACK_TERRACOTTA,
                20L,
                BaseKeys.FLUID_VOIDER_1
        ).register();

        new Research(
                baseKey("rituals"),
                Material.WITHER_SKELETON_SKULL,
                20L,
                BaseKeys.MAGIC_PEDESTAL,
                BaseKeys.MAGIC_ALTAR,
                BaseKeys.SHIMMER_DUST_3,
                BaseKeys.SHIMMER_SKULL
        ).register();

        new Research(
                baseKey("hydraulic_automation"),
                Material.WAXED_CHISELED_COPPER,
                22L,
                BaseKeys.HYDRAULIC_GRINDSTONE_TURNER,
                BaseKeys.HYDRAULIC_MIXING_ATTACHMENT,
                BaseKeys.HYDRAULIC_PRESS_PISTON,
                BaseKeys.HYDRAULIC_HAMMER_HEAD,
                BaseKeys.HYDRAULIC_PIPE_BENDER,
                BaseKeys.HYDRAULIC_TABLE_SAW
        ).register();

        new Research(
                baseKey("large_scale_hydraulic_purification"),
                Material.WAXED_COPPER_BLOCK,
                25L,
                BaseKeys.SOLAR_PURIFICATION_TOWER_4,
                BaseKeys.SOLAR_PURIFICATION_TOWER_5
        ).register();

        new Research(
                baseKey("fireproofing"),
                Material.FIRE_CHARGE,
                25L,
                BaseKeys.FIREPROOF_RUNE
        ).register();

        new Research(
                baseKey("french_revolution"),
                Material.DIAMOND_SWORD,
                25L,
                BaseKeys.BEHEADING_SWORD
        ).register();

        new Research(
                baseKey("deforestation"),
                Material.WOODEN_AXE,
                30L,
                BaseKeys.LUMBER_AXE
        ).register();

        new Research(
                baseKey("immobilization"),
                Material.PISTON,
                30L,
                BaseKeys.IMMOBILIZER
        ).register();

        new Research(
                baseKey("fluid_voiding_2"),
                Material.BLACK_TERRACOTTA,
                30L,
                BaseKeys.FLUID_VOIDER_2,
                BaseKeys.FLUID_VOIDER_3
        ).register();

        new Research(
                baseKey("fluid_handling_2"),
                Material.BLACK_TERRACOTTA,
                30L,
                BaseKeys.FLUID_PIPE_IGNEOUS_COMPOSITE,
                BaseKeys.PORTABLE_FLUID_TANK_IGNEOUS_COMPOSITE,
                BaseKeys.FLUID_TANK_CASING_IGNEOUS_COMPOSITE
        ).register();

        new Research(
                baseKey("straining"),
                Material.WHITE_CONCRETE,
                32L,
                BaseKeys.FLUID_STRAINER
        ).register();

        new Research(
                baseKey("vitality_2"),
                Material.BUDDING_AMETHYST,
                35L,
                BaseKeys.HEALTH_TALISMAN_ULTIMATE
        ).register();

        new Research(
                baseKey("high_temperature_materials"),
                Material.DEEPSLATE_TILES,
                35L,
                BaseKeys.REFRACTORY_MIX,
                BaseKeys.UNFIRED_REFRACTORY_BRICK,
                BaseKeys.REFRACTORY_BRICK,
                BaseKeys.REFRACTORY_BRICKS
        ).register();

        new Research(
                baseKey("hydraulic_drilling"),
                Material.WAXED_COPPER_BULB,
                40L,
                BaseKeys.HYDRAULIC_CORE_DRILL,
                BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH,
                BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH,
                BaseKeys.INTERMEDIATE_CORE_CHUNK
        ).register();

        new Research(
                baseKey("scientific_revolution_2"),
                Material.LIME_BANNER,
                40L,
                BaseKeys.RESEARCH_PACK_2
        ).register();

        new Research(
                baseKey("fluid_handling_3"),
                Material.GRAY_CONCRETE,
                45L,
                BaseKeys.FLUID_PIPE_STEEL,
                BaseKeys.PORTABLE_FLUID_TANK_STEEL,
                BaseKeys.FLUID_TANK_CASING_STEEL
        ).register();

        new Research(
                baseKey("high_temperature_smelting"),
                Material.BLAST_FURNACE,
                50L,
                BaseKeys.SMELTERY_CONTROLLER,
                BaseKeys.SMELTERY_BURNER,
                BaseKeys.SMELTERY_CASTER,
                BaseKeys.SMELTERY_HOPPER,
                BaseKeys.SMELTERY_INPUT_HATCH,
                BaseKeys.SMELTERY_OUTPUT_HATCH
        ).register();

        new Research(
                baseKey("steel"),
                Material.NETHERITE_INGOT,
                50L,
                BaseKeys.STEEL_DUST,
                BaseKeys.STEEL_INGOT,
                BaseKeys.STEEL_NUGGET,
                BaseKeys.STEEL_BLOCK,
                BaseKeys.STEEL_SHEET
        ).register();

        new Research(
                baseKey("magnetism"),
                Material.IRON_INGOT,
                50L,
                BaseKeys.NICKEL_DUST,
                BaseKeys.NICKEL_INGOT,
                BaseKeys.NICKEL_NUGGET,
                BaseKeys.NICKEL_BLOCK,
                BaseKeys.COBALT_DUST,
                BaseKeys.COBALT_BLOCK,
                BaseKeys.COBALT_INGOT,
                BaseKeys.COBALT_NUGGET
        ).register();
    }
}
