package io.github.pylonmc.pylon.base.items.weapons;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonArrow;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public class IceArrow extends PylonItem implements PylonArrow {
    public static final NamespacedKey KEY = pylonKey("ice_arrow");
    public static final ItemStack STACK = ItemStackBuilder.pylonItem(Material.ARROW, KEY).amount(8).build();
    private final int freezeDuration = getSettings().getOrThrow("freeze-duration", Integer.class);
    private final double freezeSpeed = getSettings().getOrThrow("freeze-speed", Double.class);

    public IceArrow(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onArrowDamage(@NotNull EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            // Has to be run every tick or effect will flicker in and out from game resetting it since the player isn't in a powdered snow block
            new DOTRunnable((LivingEntity)event.getEntity(), freezeDuration, Bukkit.getCurrentTick(), (float) freezeSpeed).runTaskTimer(PylonBase.getInstance(), 0, 1);
        }
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
        return Map.of("freeze-duration", Component.text(freezeDuration));
    }

    public static class DOTRunnable extends BukkitRunnable {
        private final LivingEntity applyTo;
        private final int freezeDuration;
        private final int startTick;
        private final float freezeSpeed;

        public DOTRunnable(LivingEntity applyTo, int freezeDuration, int startTick, float freezeSpeed){
            this.applyTo = applyTo;
            this.freezeDuration = freezeDuration;
            this.startTick = startTick;
            this.freezeSpeed = freezeSpeed;
        }

        @Override
        public void run() {
            if(Bukkit.getCurrentTick() - startTick > freezeDuration){
                applyTo.setFreezeTicks(0);
                this.cancel();
            }
            // This is the only thing I could find that works to apply this effect, it's not the greatest but too bad.
            applyTo.setFreezeTicks(Math.round((Bukkit.getCurrentTick() - startTick) * freezeSpeed) + applyTo.getMaxFreezeTicks());
        }
    }
}
