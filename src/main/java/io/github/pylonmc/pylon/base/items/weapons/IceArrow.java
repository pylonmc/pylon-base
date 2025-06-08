package io.github.pylonmc.pylon.base.items.weapons;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.Arrow;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public class IceArrow extends PylonItem implements Arrow {
    public static final NamespacedKey KEY = pylonKey("ice_arrow");
    public static final ItemStack STACK = ItemStackBuilder.pylonItem(Material.ARROW, KEY).amount(8).build();
    private final int slowDuration = getSettings().getOrThrow("slow-duration-ticks", Integer.class);
    private final int slowAmplifier = getSettings().getOrThrow("slow-amplifier", Integer.class);
    private final double freezeDmg = getSettings().getOrThrow("freeze-damage", Double.class);

    public IceArrow(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onArrowShotFromBow(@NotNull EntityShootBowEvent event) {
        ((org.bukkit.entity.Arrow)event.getProjectile()).addCustomEffect(new PotionEffect(PotionEffectType.SLOWNESS, slowDuration, slowAmplifier, false, false, false), true);
    }

    @Override
    public void onArrowDamage(@NotNull EntityDamageByEntityEvent event) {
        if(event.getEntity() instanceof LivingEntity) {
            Bukkit.getScheduler().runTask(PylonBase.getInstance(), new DOTRunnable((LivingEntity)event.getEntity(), freezeDmg));
        }
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
        return Map.of("slow-duration-ticks", Component.text(slowDuration),
                "slow-amplifier", Component.text(slowAmplifier));
    }

    public static class DOTRunnable implements Runnable {
        private final LivingEntity applyTo;
        private final double damage;

        public DOTRunnable(LivingEntity applyTo, double damage){
            this.applyTo = applyTo;
            this.damage = damage;
        }

        @Override
        public void run() {
            // This is broken :c
            applyTo.damage(damage, DamageSource.builder(DamageType.FREEZE).build());
        }
    }
}
