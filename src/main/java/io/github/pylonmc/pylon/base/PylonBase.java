package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.HealthTalisman;
import io.github.pylonmc.pylon.base.items.Immobilizer;
import io.github.pylonmc.pylon.base.items.fluid.connection.connecting.ConnectingService;
import io.github.pylonmc.pylon.base.items.tools.watering.Sprinkler;
import io.github.pylonmc.pylon.base.listeners.WitherProofObsidianListener;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Set;


public class PylonBase extends JavaPlugin implements PylonAddon {

    @Getter
    private static PylonBase instance;

    @Override
    public void onEnable() {
        instance = this;

        registerWithPylon();

        saveDefaultConfig();

        PylonItems.initialize();
        PylonBlocks.initialize();
        PylonEntities.initialize();
        PylonFluids.initialize();

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new Sprinkler.SprinklerPlaceListener(), this);
        pm.registerEvents(new WitherProofObsidianListener(), this);
        pm.registerEvents(new ConnectingService(), this);
        pm.registerEvents(new Immobilizer.FreezeListener(), this);
        new HealthTalisman.HealthTalismanTicker().runTaskTimer(this, 0, 40);

        // 26154 is pluginID for PylonBase
        final Metrics metrics = new Metrics(this, 26154);
    }

    @Override
    public void onDisable() {
        ConnectingService.cleanup();
    }

    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return instance;
    }

    @Override
    public @NotNull String getDisplayName() {
        return "Base";
    }

    @Override
    public @NotNull Set<@NotNull Locale> getLanguages() {
        return Set.of(Locale.ENGLISH);
    }
}
