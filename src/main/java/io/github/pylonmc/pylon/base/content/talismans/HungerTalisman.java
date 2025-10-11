package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.tools.base.Talisman;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.base.InventoryTickSpeed;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

public class HungerTalisman extends Talisman {
    private static final NamespacedKey HUNGER_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "hunger_talisman");
    public final int hungerIncrease = getSettings().getOrThrow("hunger-increase", ConfigAdapter.INT);
    public final float saturationMultiplier = getSettings().getOrThrow("saturation-multiplier", ConfigAdapter.FLOAT);
    public final int increasePeriod = getSettings().getOrThrow("period-ticks", ConfigAdapter.INT);
    public final int level = getSettings().getOrThrow("level", ConfigAdapter.INT);
    private static final WeakHashMap<UUID, BukkitTask> hungerTasks = new WeakHashMap<>();

    public HungerTalisman(@NotNull ItemStack stack) { super(stack); }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("period", UnitFormat.SECONDS.format(increasePeriod / 20f)),
                PylonArgument.of("saturation_multiplier", Component.text(saturationMultiplier)),
                PylonArgument.of("hunger_increase", Component.text(hungerIncrease)));
    }

    @Override
    protected void removeEffect_(@NotNull Player player, @NotNull ItemStack stack) {
        hungerTasks.get(player.getUniqueId()).cancel();
    }

    @Override
    protected void applyEffect_(@NotNull Player player, @NotNull ItemStack stack) {
        hungerTasks.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(PylonBase.getInstance(), () -> {
            player.setSaturation(player.getSaturation() * saturationMultiplier);
            player.setFoodLevel(player.getFoodLevel() + hungerIncrease);
        }, 0, increasePeriod));
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public NamespacedKey getTalismanKey() {
        return HUNGER_TALISMAN_KEY;
    }

    @Override
    public @NotNull InventoryTickSpeed getTickSpeed() {
        return InventoryTickSpeed.SLOW;
    }
}
