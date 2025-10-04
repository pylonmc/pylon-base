package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.LineBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.joml.Vector3d;

import java.util.List;
import java.util.Optional;


public final class DisplayProjectile {
    private final Player player;
    private final float thickness;
    private final double damage;
    private final int tickInterval;
    private final Vector locationStep;
    private final BlockDisplay projectile;


    public DisplayProjectile(
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
        this.player = player;
        this.thickness = thickness;
        this.damage = damage;
        this.tickInterval = tickInterval;
        this.locationStep = direction.clone().multiply(speedBlockPerSecond * tickInterval / 20.0);
        this.projectile = new BlockDisplayBuilder()
            .transformation(new LineBuilder()
                .from(new Vector3d(0, 0, 0))
                .to(direction.clone().multiply(length).toVector3d())
                .thickness(thickness)
                .build()
            )
            .material(material)
            .build(source);
        this.projectile.setPersistent(false);

        new Task(tickInterval, lifetimeTicks).runTaskTimer(PylonBase.getInstance(), tickInterval, tickInterval);
    }

    private class Task extends BukkitRunnable {
        private final int tickInterval;
        private final int lifetimeTicks;
        private int livedTicks;

        public Task(int tickInterval, int lifetimeTicks) {
            this.tickInterval = tickInterval;
            this.lifetimeTicks = lifetimeTicks;
        }


        @Override
        public void run() {
            if (livedTicks >= lifetimeTicks) {
                projectile.remove();
                cancel();
                return;
            }
            livedTicks+=tickInterval;

            if (projectile.isDead()) {
                cancel();
                return;
            }

            projectile.setTeleportDuration(tickInterval);

            Location teleportTo = projectile.getLocation().add(locationStep);
            if (!teleportTo.getBlock().isPassable()) {
                teleportTo.getWorld().spawnParticle(
                    Particle.CRIT,
                    teleportTo,
                    15
                );
                projectile.remove();
                cancel();
                return;
            }

            projectile.teleport(teleportTo);

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
                Task.this.cancel();
            });
        }
    }
}
