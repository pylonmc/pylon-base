package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.LineBuilder;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.List;
import java.util.Optional;


@UtilityClass
public class DisplayProjectile {
    public void spawn(
            Player player,
            Material material,
            Location source,
            Vector direction,
            float thickness,
            float length,
            float speedBlockPerSecond,
            double damage,
            int tickInterval,
            int lifetimeTicks
    ) {
        Vector locationStep = direction.clone().multiply(speedBlockPerSecond * tickInterval / 20.0);
        BlockDisplay projectile = new BlockDisplayBuilder()
                .transformation(new LineBuilder()
                        .from(new Vector3d(0, 0, 0))
                        .to(direction.clone().multiply(length).toVector3d())
                        .thickness(thickness)
                        .build()
                )
                .material(material)
                .build(source);
        projectile.setPersistent(false);

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(PylonBase.getInstance(), () -> {
                tick(player, projectile, locationStep, tickInterval, thickness, damage);
        }, tickInterval, tickInterval);

        Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), projectile::remove, lifetimeTicks);
        Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), task::cancel, lifetimeTicks);
    }

    private void tick(
            Player player,
            @NotNull BlockDisplay projectile,
            Vector locationStep,
            int tickInterval,
            float thickness,
            double damage
    ) {
        if (projectile.isDead()) {
            return;
        }

        projectile.setTeleportDuration(tickInterval);
        projectile.teleport(projectile.getLocation().add(locationStep));

        List<Entity> nearbyEntities = projectile.getNearbyEntities(thickness * 1.5, thickness * 1.5, thickness * 1.5);
        if (nearbyEntities.isEmpty()) {
            return;
        }

        Optional<Damageable> hitEntity = nearbyEntities.stream()
                .filter(Damageable.class::isInstance)
                .map(Damageable.class::cast)
                .findFirst();
        hitEntity.ifPresent(entity -> {
            EntityDamageEvent event = new EntityDamageEvent(
                    entity,
                    EntityDamageEvent.DamageCause.ENTITY_ATTACK,
                    DamageSource.builder(DamageType.ARROW)
                            .withCausingEntity(player)
                            .withDirectEntity(entity)
                            .build(),
                    damage
            );
            if (event.callEvent()) {
                entity.damage(damage);
                entity.setVelocity(locationStep.clone().normalize().multiply(0.2));
            }
            projectile.remove();
        });
    }
}
