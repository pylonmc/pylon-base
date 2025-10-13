package io.github.pylonmc.pylon.base.content.talismans;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.tools.base.Talisman;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.InventoryTickSpeed;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;

public class HungerTalisman extends Talisman {
    private static final NamespacedKey HUNGER_TALISMAN_KEY = new NamespacedKey(PylonBase.getInstance(), "hunger_talisman");
    public final int hungerIncrease = getSettings().getOrThrow("hunger-increase", ConfigAdapter.INT);
    public final float saturationIncrease = getSettings().getOrThrow("saturation-increase", ConfigAdapter.FLOAT);
    public final int increasePeriod = getSettings().getOrThrow("period-ticks", ConfigAdapter.INT);
    public final int level = getSettings().getOrThrow("level", ConfigAdapter.INT);
    private static final WeakHashMap<UUID, BukkitTask> hungerTasks = new WeakHashMap<>();

    public HungerTalisman(@NotNull ItemStack stack) { super(stack); }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(PylonArgument.of("period", UnitFormat.SECONDS.format(increasePeriod / 20f)),
                PylonArgument.of("saturation_increase", Component.text(saturationIncrease)),
                PylonArgument.of("hunger_increase", Component.text(hungerIncrease)));
    }

    @Override
    protected void removeEffect_(@NotNull Player player) {
        hungerTasks.get(player.getUniqueId()).cancel();
    }

    @Override
    protected void applyEffect_(@NotNull Player player) {
        hungerTasks.put(player.getUniqueId(), Bukkit.getScheduler().runTaskTimer(PylonBase.getInstance(), () -> {
            player.setSaturation(player.getSaturation() + saturationIncrease);
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

    public static final class JoinListener implements Listener {
        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event){
            if(event.getPlayer().getPersistentDataContainer().has(HUNGER_TALISMAN_KEY)){
                int talismanLevel = event.getPlayer().getPersistentDataContainer().get(HUNGER_TALISMAN_KEY, PersistentDataType.INTEGER);
                for(ItemStack stack : event.getPlayer().getInventory()){
                    PylonItem item = PylonItem.fromStack(stack);
                    if(item == null){
                        continue;
                    }
                    if(!(item instanceof Talisman talisman)){
                        continue;
                    }
                    if(talisman.getLevel() != talismanLevel){
                        continue;
                    }
                    talisman.applyEffect(event.getPlayer());
                }
            }
        }
    }

}
