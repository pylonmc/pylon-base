package io.github.pylonmc.pylon.base;

import io.github.pylonmc.rebar.content.guide.PylonGuide;
import io.github.pylonmc.rebar.guide.pages.base.SimpleStaticGuidePage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class BasePages {

    public static final SimpleStaticGuidePage SCIENCE = new SimpleStaticGuidePage(baseKey("science"));

    public static final SimpleStaticGuidePage METALS = new SimpleStaticGuidePage(baseKey("resources_metals"));
    public static final SimpleStaticGuidePage CORE_CHUNKS = new SimpleStaticGuidePage(baseKey("resources_core_chunks"));
    public static final SimpleStaticGuidePage MAGIC = new SimpleStaticGuidePage(baseKey("resources_magic"));
    public static final SimpleStaticGuidePage MISCELLANEOUS = new SimpleStaticGuidePage(baseKey("resources_miscellaneous"));
    public static final SimpleStaticGuidePage RESOURCES = new SimpleStaticGuidePage(baseKey("resources"));

    public static final SimpleStaticGuidePage COMPONENTS = new SimpleStaticGuidePage(baseKey("components"));
    public static final SimpleStaticGuidePage TOOLS = new SimpleStaticGuidePage(baseKey("tools"));
    public static final SimpleStaticGuidePage COMBAT = new SimpleStaticGuidePage(baseKey("combat"));
    public static final SimpleStaticGuidePage TALISMANS = new SimpleStaticGuidePage(baseKey("talismans"));
    public static final SimpleStaticGuidePage ARMOUR = new SimpleStaticGuidePage(baseKey("armour"));
    public static final SimpleStaticGuidePage FOOD = new SimpleStaticGuidePage(baseKey("food"));

    public static final SimpleStaticGuidePage SIMPLE_MACHINES = new SimpleStaticGuidePage(baseKey("machines_simple_machines"));
    public static final SimpleStaticGuidePage SMELTING = new SimpleStaticGuidePage(baseKey("machines_smelting"));
    public static final SimpleStaticGuidePage FLUID_PIPES_AND_TANKS = new SimpleStaticGuidePage(baseKey("machines_fluid_pipes_and_tanks"));
    public static final SimpleStaticGuidePage FLUID_MACHINES = new SimpleStaticGuidePage(baseKey("machines_fluid_machines"));
    public static final SimpleStaticGuidePage HYDRAULIC_MACHINES = new SimpleStaticGuidePage(baseKey("machines_hydraulic_machines"));
    public static final SimpleStaticGuidePage HYDRAULIC_PURIFICATION = new SimpleStaticGuidePage(baseKey("machines_hydraulic_purification"));
    public static final SimpleStaticGuidePage CARGO = new SimpleStaticGuidePage(baseKey("machines_cargo"));
    public static final SimpleStaticGuidePage DIESEL_MACHINES = new SimpleStaticGuidePage(baseKey("machines_diesel_machines"));
    public static final SimpleStaticGuidePage DIESEL_PRODUCTION = new SimpleStaticGuidePage(baseKey("machines_diesel_production"));
    private static final SimpleStaticGuidePage MACHINES = new SimpleStaticGuidePage(baseKey("machines"));

    public static final SimpleStaticGuidePage BUILDING = new SimpleStaticGuidePage(baseKey("building"));

    public static final SimpleStaticGuidePage CREATIVE_ITEMS = new SimpleStaticGuidePage(baseKey("creative_items")) {
        @Override
        public boolean shouldDisplay(@NotNull Player player) {
            return player.hasPermission("pylon.guide.view_admin_pages");
        }
    };

    public static void initialise() {
        PylonGuide.getRootPage().addPage(BaseItems.LOUPE, SCIENCE);

        RESOURCES.addPage(BaseItems.BRONZE_INGOT, METALS);
        RESOURCES.addPage(BaseItems.SHALLOW_CORE_CHUNK, CORE_CHUNKS);
        RESOURCES.addPage(BaseItems.COVALENT_BINDER, MAGIC);
        RESOURCES.addPage(BaseItems.UNFIRED_REFRACTORY_BRICK, MISCELLANEOUS);
        PylonGuide.getRootPage().addPage(BaseItems.COPPER_DUST, RESOURCES);

        PylonGuide.getRootPage().addPage(BaseItems.BRONZE_DRILL_BIT, COMPONENTS);
        PylonGuide.getRootPage().addPage(BaseItems.BRONZE_PICKAXE, TOOLS);
        PylonGuide.getRootPage().addPage(BaseItems.BEHEADING_SWORD, COMBAT);
        PylonGuide.getRootPage().addPage(BaseItems.HEALTH_TALISMAN_SIMPLE, TALISMANS);
        PylonGuide.getRootPage().addPage(BaseItems.BRONZE_CHESTPLATE, ARMOUR);
        PylonGuide.getRootPage().addPage(Material.APPLE, FOOD);

        MACHINES.addPage(BaseItems.PRESS, SIMPLE_MACHINES);
        MACHINES.addPage(BaseItems.SMELTERY_CONTROLLER, SMELTING);
        MACHINES.addPage(BaseItems.FLUID_PIPE_COPPER, FLUID_PIPES_AND_TANKS);
        MACHINES.addPage(BaseItems.FLUID_METER, FLUID_MACHINES);
        MACHINES.addPage(BaseItems.HYDRAULIC_PIPE_BENDER, HYDRAULIC_MACHINES);
        MACHINES.addPage(BaseItems.SOLAR_PURIFICATION_TOWER_3, HYDRAULIC_PURIFICATION);
        MACHINES.addPage(BaseItems.CARGO_BUFFER, CARGO);
        MACHINES.addPage(BaseItems.DIESEL_PIPE_BENDER, DIESEL_MACHINES);
        MACHINES.addPage(BaseItems.BIOREFINERY, DIESEL_PRODUCTION);
        PylonGuide.getRootPage().addPage(BaseItems.MIXING_POT, MACHINES);

        PylonGuide.getRootPage().addPage(BaseItems.ELEVATOR_1, BUILDING);
        PylonGuide.getRootPage().addPage(BaseItems.FLUID_PIPE_CREATIVE, CREATIVE_ITEMS);
    }
}
