package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.core.item.research.Research;
import lombok.experimental.UtilityClass;
import org.bukkit.Material;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


@UtilityClass
public class BaseResearches {

    public void initialize() {
        new Research(
                baseKey("newtons_second_law"),
                Material.STONE_PICKAXE,
                5L,
                BaseKeys.HAMMER_STONE,
                BaseKeys.HAMMER_IRON,
                BaseKeys.HAMMER_DIAMOND
        ).register();

        new Research(
                baseKey("plant_growth"),
                Material.BUCKET,
                5L,
                BaseKeys.WATERING_CAN
        ).register();

        new Research(
                baseKey("food_preservation"),
                Material.ROTTEN_FLESH,
                2L,
                BaseKeys.WATERING_CAN
        ).register();

        new Research(
                baseKey("primitive_alloying"),
                Material.IRON_INGOT,
                8L,
                BaseKeys.RAW_FERRODURALUM,
                BaseKeys.FERRODURALUM_INGOT
        ).register();

        new Research(
                baseKey("metal_ductility"),
                Material.PAPER,
                5L,
                BaseKeys.COPPER_SHEET,
                BaseKeys.GOLD_SHEET,
                BaseKeys.IRON_SHEET,
                BaseKeys.FERRODURALUM_SHEET
        ).register();

        new Research(
                baseKey("primitive_alloy_tools"),
                Material.GOLDEN_AXE,
                10L,
                BaseKeys.FERRODURALUM_SWORD,
                BaseKeys.FERRODURALUM_AXE,
                BaseKeys.FERRODURALUM_PICKAXE,
                BaseKeys.FERRODURALUM_SHOVEL,
                BaseKeys.FERRODURALUM_HOE
        ).register();

        new Research(
                baseKey("primitive_alloy_armor"),
                Material.GOLDEN_HELMET,
                10L,
                BaseKeys.FERRODURALUM_HELMET,
                BaseKeys.FERRODURALUM_CHESTPLATE,
                BaseKeys.FERRODURALUM_LEGGINGS,
                BaseKeys.FERRODURALUM_BOOTS
        ).register();

        new Research(
                baseKey("compression"),
                Material.OBSIDIAN,
                5L,
                BaseKeys.COMPRESSED_WOOD,
                BaseKeys.COMPRESSED_OBSIDIAN
        ).register();

        new Research(
                baseKey("portability"),
                Material.CRAFTING_TABLE,
                7L,
                BaseKeys.PORTABILITY_CATALYST,
                BaseKeys.PORTABLE_CRAFTING_TABLE,
                BaseKeys.PORTABLE_DUSTBIN,
                BaseKeys.PORTABLE_ENDER_CHEST
        ).register();

        new Research(
                baseKey("first_aid"),
                Material.COBWEB,
                5L,
                BaseKeys.BANDAGE,
                BaseKeys.SPLINT
        ).register();

        new Research(
                baseKey("medicine"),
                Material.BREWER_POTTERY_SHERD,
                10L,
                BaseKeys.DISINFECTANT,
                BaseKeys.MEDKIT
        ).register();

        new Research(
                baseKey("plant_growth_automated"),
                Material.FLOWER_POT,
                10L,
                BaseKeys.SPRINKLER
        ).register();

        new Research(
                baseKey("newtons_third_law"),
                Material.ARROW,
                15L,
                BaseKeys.RECOIL_ARROW
        ).register();

        new Research(
                baseKey("gravity"),
                Material.WOODEN_AXE,
                10L,
                BaseKeys.LUMBER_AXE
        ).register();

        new Research(
                baseKey("grinding"),
                Material.SMOOTH_STONE_SLAB,
                5L,
                BaseKeys.GRINDSTONE,
                BaseKeys.GRINDSTONE_HANDLE
        ).register();

        new Research(
                baseKey("baking"),
                Material.YELLOW_DYE,
                2L,
                BaseKeys.FLOUR,
                BaseKeys.DOUGH
        ).register();

        new Research(
                baseKey("better_health"),
                Material.AMETHYST_SHARD,
                10L,
                BaseKeys.HEALTH_TALISMAN_SIMPLE,
                BaseKeys.HEALTH_TALISMAN_ADVANCED,
                BaseKeys.HEALTH_TALISMAN_ULTIMATE
        ).register();

        new Research(
                baseKey("homogeneity"),
                Material.CAULDRON,
                6L,
                BaseKeys.MIXING_POT
        ).register();

        new Research(
                baseKey("glitter"),
                Material.SUGAR,
                5L,
                BaseKeys.SHIMMER_DUST_1,
                BaseKeys.SHIMMER_DUST_2,
                BaseKeys.SHIMMER_DUST_3,
                BaseKeys.SHIMMER_SKULL
        ).register();

        new Research(
                baseKey("french_revolution"),
                Material.DIAMOND_SWORD,
                10L,
                BaseKeys.BEHEADING_SWORD
        ).register();

        new Research(
                baseKey("showing_off"),
                Material.STONE_BRICK_WALL,
                2L,
                BaseKeys.PEDESTAL
        ).register();

        new Research(
                baseKey("magic"),
                Material.LIGHT_BLUE_DYE,
                6L,
                BaseKeys.MAGIC_PEDESTAL,
                BaseKeys.MAGIC_ALTAR,
                BaseKeys.COVALENT_BINDER
        ).register();
    }
}
