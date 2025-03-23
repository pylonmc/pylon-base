package io.github.pylonmc.pylon.base;

import org.bukkit.plugin.java.JavaPlugin;

public class PylonBase extends JavaPlugin {

    private static PylonBase INSTANCE;

    public static PylonBase getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        PylonItems.register();
        PylonBlocks.register();
    }

    @Override
    public void onDisable() {
        INSTANCE = null;
    }
}
