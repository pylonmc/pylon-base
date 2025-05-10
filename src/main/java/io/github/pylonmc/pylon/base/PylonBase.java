package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.items.HealthTalisman;
import io.github.pylonmc.pylon.base.misc.WaterCauldronRightClickRecipe;
import io.github.pylonmc.pylon.base.items.watering.Sprinkler;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import lombok.Getter;
import org.bukkit.Bukkit;
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
        PylonItems.initialize();
        PylonBlocks.initialize();
        PylonEntities.initialize();
        PylonFluids.initialize();
        Bukkit.getPluginManager().registerEvents(new Sprinkler.SprinklerPlaceListener(), this);
        Bukkit.getPluginManager().registerEvents(new WaterCauldronRightClickRecipe.CauldronListener(), this);
        new HealthTalisman.HealthTalismanTicker().runTaskTimer(this, 0, 40);
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
