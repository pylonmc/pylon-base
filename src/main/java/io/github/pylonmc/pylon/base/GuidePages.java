package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.core.guide.PylonGuide;
import io.github.pylonmc.pylon.core.guide.pages.base.SimpleStaticGuidePage;
import org.bukkit.Material;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class GuidePages {

    public static final SimpleStaticGuidePage RESOURCES = new SimpleStaticGuidePage(pylonKey("resources"), Material.GUNPOWDER);

    static {
        PylonGuide.rootPage.addPage(RESOURCES);
    }
}
