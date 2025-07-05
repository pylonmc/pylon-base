package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.core.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import org.bukkit.Material;

import static io.github.pylonmc.pylon.base.util.BaseUtils.pylonKey;

public class BasePages {

    public static final SimpleStaticGuidePage SCIENCE = new SimpleStaticGuidePage(pylonKey("science"), Material.BOOK);
    public static final SimpleStaticGuidePage RESOURCES = new SimpleStaticGuidePage(pylonKey("resources"), Material.GUNPOWDER);
    public static final SimpleStaticGuidePage COMPONENTS = new SimpleStaticGuidePage(pylonKey("components"), Material.PAPER);
    public static final SimpleStaticGuidePage TOOLS = new SimpleStaticGuidePage(pylonKey("tools"), Material.IRON_PICKAXE);
    public static final SimpleStaticGuidePage COMBAT = new SimpleStaticGuidePage(pylonKey("combat"), Material.IRON_SWORD);
    public static final SimpleStaticGuidePage ARMOUR = new SimpleStaticGuidePage(pylonKey("armour"), Material.IRON_CHESTPLATE);
    public static final SimpleStaticGuidePage FOOD = new SimpleStaticGuidePage(pylonKey("food"), Material.APPLE);

    public static final SimpleStaticGuidePage SIMPLE_MACHINES = new SimpleStaticGuidePage(pylonKey("simple_machines"), Material.SMOOTH_STONE_SLAB);
    public static final SimpleStaticGuidePage FLUID_MACHINES = new SimpleStaticGuidePage(pylonKey("fluid_machines"), Material.ORANGE_TERRACOTTA);
    public static final SimpleStaticGuidePage HYDRAULICS = new SimpleStaticGuidePage(pylonKey("hydraulics"), Material.BLUE_CONCRETE_POWDER);
    public static final SimpleStaticGuidePage SMELTING = new SimpleStaticGuidePage(pylonKey("smelting"), Material.DEEPSLATE_TILES);
    private static final SimpleStaticGuidePage MACHINES = new SimpleStaticGuidePage(pylonKey("machines"), Material.BLAST_FURNACE);

    public static final SimpleStaticGuidePage BUILDING = new SimpleStaticGuidePage(pylonKey("building"), Material.STONE_BRICK_WALL);

    static {
        PylonGuide.getRootPage().addPage(SCIENCE);
        PylonGuide.getRootPage().addPage(RESOURCES);
        PylonGuide.getRootPage().addPage(COMPONENTS);
        PylonGuide.getRootPage().addPage(TOOLS);
        PylonGuide.getRootPage().addPage(COMBAT);
        PylonGuide.getRootPage().addPage(ARMOUR);
        PylonGuide.getRootPage().addPage(FOOD);

        MACHINES.addPage(SIMPLE_MACHINES);
        MACHINES.addPage(FLUID_MACHINES);
        MACHINES.addPage(HYDRAULICS);
        MACHINES.addPage(SMELTING);
        PylonGuide.getRootPage().addPage(MACHINES);

        PylonGuide.getRootPage().addPage(BUILDING);
    }
}
