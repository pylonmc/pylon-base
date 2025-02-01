package io.github.pylonmc;

import org.bukkit.plugin.java.JavaPlugin;

public class PylonBase extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Hello, world!");
    }
}
