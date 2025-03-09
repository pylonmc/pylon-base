package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.NoArrowBows;
import org.bukkit.plugin.java.JavaPlugin;

public class PylonBase extends JavaPlugin {

    private static PylonBase INSTANCE;

    private static NoArrowBows noArrowBowsMechanics = new NoArrowBows();

    public static NoArrowBows getNoArrowBowsMechanics() { return noArrowBowsMechanics; }

    public static PylonBase getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        PylonItems.register();
        getServer().getPluginManager().registerEvents(noArrowBowsMechanics, this);
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
    }
}
