package io.github.pylonmc.pylon.base;

import io.github.pylonmc.pylon.base.command.PylonBaseCommand;
import io.github.pylonmc.pylon.base.content.magic.base.Rune;
import io.github.pylonmc.pylon.base.content.tools.HealthTalisman;
import io.github.pylonmc.pylon.base.content.building.Immobilizer;
import io.github.pylonmc.pylon.base.content.building.IgneousCompositeListener;
import io.github.pylonmc.pylon.base.content.machines.fluid.Sprinkler;
import io.github.pylonmc.pylon.core.addon.PylonAddon;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class PylonBase extends JavaPlugin implements PylonAddon {

    private static final int BSTATS_ID = 27323;
    private static Metrics metrics;

    @Getter
    private static PylonBase instance;

    @Override
    public void onEnable() {
        instance = this;

        metrics = new Metrics(this, BSTATS_ID);

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
        BaseRecipes.initialize();

        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new Sprinkler.SprinklerPlaceListener(), this);
        pm.registerEvents(new IgneousCompositeListener(), this);
        pm.registerEvents(new Immobilizer.FreezeListener(), this);
        pm.registerEvents(new Rune.RuneListener(), this);
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

    // TODO remove and replace caller with runTaskLater for consistency
    public static void runSyncTimer(@NotNull Consumer<BukkitTask> task, long delay, long period) {
        Bukkit.getScheduler().runTaskTimer(instance, task, delay, period);
    }

    @NotNull
    public static BukkitTask runSyncLater(@NotNull Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(instance, runnable, delay);
    }
}
