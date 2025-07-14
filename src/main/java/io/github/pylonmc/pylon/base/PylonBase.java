package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.command.PylonBaseCommand;
import io.github.pylonmc.pylon.base.content.tools.HealthTalisman;
import io.github.pylonmc.pylon.base.content.building.Immobilizer;
import io.github.pylonmc.pylon.base.content.tools.Sprinkler;
import io.github.pylonmc.pylon.base.content.building.WitherProofObsidianListener;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class PylonBase extends JavaPlugin implements PylonAddon {

    @Getter
    private static PylonBase instance;

    @Override
    public void onEnable() {
        instance = this;

        registerWithPylon();

        saveDefaultConfig();

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(PylonBaseCommand.ROOT);
        });

        BaseItems.initialize();
        BaseBlocks.initialize();
        BaseEntities.initialize();
        BaseFluids.initialize();
        BaseResearches.initialize();

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new Sprinkler.SprinklerPlaceListener(), this);
        pm.registerEvents(new WitherProofObsidianListener(), this);
        pm.registerEvents(new Immobilizer.FreezeListener(), this);
        new HealthTalisman.HealthTalismanTicker().runTaskTimer(this, 0, 40);
    }

    @Override
    public @NotNull JavaPlugin getJavaPlugin() {
        return instance;
    }

    @Override
    public @NotNull Set<@NotNull Locale> getLanguages() {
        return Set.of(Locale.ENGLISH);
    }

    @Override
    public @NotNull Material getMaterial() {
        return Material.COPPER_INGOT;
    }
}
