package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.Sprinkler;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public class PylonBase extends JavaPlugin implements PylonAddon {

    private static PylonBase instance;

    public static PylonBase getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        registerWithPylon();
        PylonItems.register();
        PylonBlocks.register();
        Bukkit.getPluginManager().registerEvents(new Sprinkler.SprinklerPlaceListener(), this);
    }

    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return instance;
    }

    @Override
    public @NotNull String displayName() {
        return "Base";
    }
}
