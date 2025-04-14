package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.watering.Sprinkler;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public class PylonBase extends JavaPlugin implements PylonAddon {

    @Getter
    private static PylonBase instance;

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
