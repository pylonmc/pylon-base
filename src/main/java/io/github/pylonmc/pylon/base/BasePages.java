package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class BasePages {

    public static final SimpleStaticGuidePage SCIENCE = new SimpleStaticGuidePage(baseKey("science"), Material.BOOK);
    public static final SimpleStaticGuidePage RESOURCES = new SimpleStaticGuidePage(baseKey("resources"), Material.GUNPOWDER);
    public static final SimpleStaticGuidePage COMPONENTS = new SimpleStaticGuidePage(baseKey("components"), Material.PAPER);
    public static final SimpleStaticGuidePage TOOLS = new SimpleStaticGuidePage(baseKey("tools"), Material.IRON_PICKAXE);
    public static final SimpleStaticGuidePage COMBAT = new SimpleStaticGuidePage(baseKey("combat"), Material.IRON_SWORD);
    public static final SimpleStaticGuidePage ARMOUR = new SimpleStaticGuidePage(baseKey("armour"), Material.IRON_CHESTPLATE);
    public static final SimpleStaticGuidePage FOOD = new SimpleStaticGuidePage(baseKey("food"), Material.APPLE);

    public static final SimpleStaticGuidePage SIMPLE_MACHINES = new SimpleStaticGuidePage(baseKey("simple_machines"), Material.SMOOTH_STONE_SLAB);
    public static final SimpleStaticGuidePage SMELTING = new SimpleStaticGuidePage(baseKey("smelting"), Material.DEEPSLATE_TILES);
    public static final SimpleStaticGuidePage FLUID_PIPES_AND_TANKS = new SimpleStaticGuidePage(baseKey("fluid_pipes_and_tanks"), Material.ORANGE_TERRACOTTA);
    public static final SimpleStaticGuidePage FLUID_MACHINES = new SimpleStaticGuidePage(baseKey("fluid_machines"), Material.LIGHT_BLUE_STAINED_GLASS);
    public static final SimpleStaticGuidePage HYDRAULICS = new SimpleStaticGuidePage(baseKey("hydraulics"), Material.BLUE_CONCRETE_POWDER);
    public static final SimpleStaticGuidePage CARGO = new SimpleStaticGuidePage(baseKey("cargo"), Material.HOPPER);
    public static final SimpleStaticGuidePage DIESEL_MACHINES = new SimpleStaticGuidePage(baseKey("diesel_machines"), Material.YELLOW_CONCRETE);
    public static final SimpleStaticGuidePage DIESEL_PRODUCTION = new SimpleStaticGuidePage(baseKey("diesel_production"), Material.YELLOW_CONCRETE_POWDER);
    private static final SimpleStaticGuidePage MACHINES = new SimpleStaticGuidePage(baseKey("machines"), Material.BLAST_FURNACE);

    public static final SimpleStaticGuidePage BUILDING = new SimpleStaticGuidePage(baseKey("building"), Material.STONE_BRICK_WALL);

    public static final SimpleStaticGuidePage CREATIVE_ITEMS = new SimpleStaticGuidePage(baseKey("creative_items"), Material.BEDROCK) {
        @Override
        public boolean shouldDisplay(@NotNull Player player) {
            return player.hasPermission("pylon.guide.view_admin_pages");
        }
    };

    static {
        PylonGuide.getRootPage().addPage(SCIENCE);
        PylonGuide.getRootPage().addPage(RESOURCES);
        PylonGuide.getRootPage().addPage(COMPONENTS);
        PylonGuide.getRootPage().addPage(TOOLS);
        PylonGuide.getRootPage().addPage(COMBAT);
        PylonGuide.getRootPage().addPage(ARMOUR);
        PylonGuide.getRootPage().addPage(FOOD);

        MACHINES.addPage(SIMPLE_MACHINES);
        MACHINES.addPage(SMELTING);
        MACHINES.addPage(FLUID_PIPES_AND_TANKS);
        MACHINES.addPage(FLUID_MACHINES);
        MACHINES.addPage(HYDRAULICS);
        MACHINES.addPage(CARGO);
        MACHINES.addPage(DIESEL_MACHINES);
        MACHINES.addPage(DIESEL_PRODUCTION);
        PylonGuide.getRootPage().addPage(MACHINES);

        PylonGuide.getRootPage().addPage(BUILDING);
        PylonGuide.getRootPage().addPage(CREATIVE_ITEMS);
    }
}
