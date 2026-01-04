package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.core.content.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class BasePages {

    public static final SimpleStaticGuidePage SCIENCE = new SimpleStaticGuidePage(baseKey("science"));
    public static final SimpleStaticGuidePage RESOURCES = new SimpleStaticGuidePage(baseKey("resources"));
    public static final SimpleStaticGuidePage COMPONENTS = new SimpleStaticGuidePage(baseKey("components"));
    public static final SimpleStaticGuidePage TOOLS = new SimpleStaticGuidePage(baseKey("tools"));
    public static final SimpleStaticGuidePage COMBAT = new SimpleStaticGuidePage(baseKey("combat"));
    public static final SimpleStaticGuidePage ARMOUR = new SimpleStaticGuidePage(baseKey("armour"));
    public static final SimpleStaticGuidePage FOOD = new SimpleStaticGuidePage(baseKey("food"));

    public static final SimpleStaticGuidePage SIMPLE_MACHINES = new SimpleStaticGuidePage(baseKey("simple_machines"));
    public static final SimpleStaticGuidePage SMELTING = new SimpleStaticGuidePage(baseKey("smelting"));
    public static final SimpleStaticGuidePage FLUID_PIPES_AND_TANKS = new SimpleStaticGuidePage(baseKey("fluid_pipes_and_tanks"));
    public static final SimpleStaticGuidePage FLUID_MACHINES = new SimpleStaticGuidePage(baseKey("fluid_machines"));
    public static final SimpleStaticGuidePage HYDRAULICS = new SimpleStaticGuidePage(baseKey("hydraulics"));
    public static final SimpleStaticGuidePage CARGO = new SimpleStaticGuidePage(baseKey("cargo"));
    public static final SimpleStaticGuidePage DIESEL_MACHINES = new SimpleStaticGuidePage(baseKey("diesel_machines"));
    public static final SimpleStaticGuidePage DIESEL_PRODUCTION = new SimpleStaticGuidePage(baseKey("diesel_production"));
    private static final SimpleStaticGuidePage MACHINES = new SimpleStaticGuidePage(baseKey("machines"));

    public static final SimpleStaticGuidePage BUILDING = new SimpleStaticGuidePage(baseKey("building"));

    public static final SimpleStaticGuidePage CREATIVE_ITEMS = new SimpleStaticGuidePage(baseKey("creative_items")) {
        @Override
        public boolean shouldDisplay(@NotNull Player player) {
            return player.hasPermission("pylon.guide.view_admin_pages");
        }
    };

    static {
        PylonGuide.getRootPage().addPage(Material.BOOK, SCIENCE);
        PylonGuide.getRootPage().addPage(Material.GUNPOWDER, RESOURCES);
        PylonGuide.getRootPage().addPage(Material.PAPER, COMPONENTS);
        PylonGuide.getRootPage().addPage(Material.IRON_PICKAXE, TOOLS);
        PylonGuide.getRootPage().addPage(Material.IRON_SWORD, COMBAT);
        PylonGuide.getRootPage().addPage(Material.IRON_CHESTPLATE, ARMOUR);
        PylonGuide.getRootPage().addPage(Material.APPLE, FOOD);

        MACHINES.addPage(Material.SMOOTH_STONE_SLAB, SIMPLE_MACHINES);
        MACHINES.addPage(Material.DEEPSLATE_TILES, SMELTING);
        MACHINES.addPage(Material.ORANGE_TERRACOTTA, FLUID_PIPES_AND_TANKS);
        MACHINES.addPage(Material.LIGHT_BLUE_STAINED_GLASS, FLUID_MACHINES);
        MACHINES.addPage(Material.BLUE_CONCRETE_POWDER, HYDRAULICS);
        MACHINES.addPage(Material.HOPPER, CARGO);
        MACHINES.addPage(Material.YELLOW_CONCRETE, DIESEL_MACHINES);
        MACHINES.addPage(Material.YELLOW_CONCRETE_POWDER, DIESEL_PRODUCTION);
        PylonGuide.getRootPage().addPage(Material.BLAST_FURNACE, MACHINES);

        PylonGuide.getRootPage().addPage(Material.STONE_BRICK_WALL, BUILDING);
        PylonGuide.getRootPage().addPage(Material.BEDROCK, CREATIVE_ITEMS);
    }
}
