package io.github.pylonmc.pylon.base.content.building;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonPiston;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class Immobilizer extends PylonBlock implements PylonPiston {
    public static class Item extends PylonItem {
        private final double radius = getSettings().getOrThrow("radius", Double.class);
        private final int duration = getSettings().getOrThrow("duration", Integer.class);
        private final int cooldown = getSettings().getOrThrow("cooldown", Integer.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("duration", duration),
                    PylonArgument.of("radius", radius),
                    PylonArgument.of("cooldown", cooldown)
            );
        }
    }

    public static Set<Player> frozenPlayers = new HashSet<>();
    private final NamespacedKey cooldownKey = baseKey("immobilizer_cooldown");
    private final double radius = getSettings().getOrThrow("radius", Double.class);
    private final int duration = getSettings().getOrThrow("duration", Integer.class);
    private final int cooldown = getSettings().getOrThrow("cooldown", Integer.class);
    private final int particleCount = getSettings().getOrThrow("particle.count", Integer.class);
    private final double particleRadius = getSettings().getOrThrow("particle.radius", Double.class);
    private final int particlePeriod = getSettings().getOrThrow("particle.period", Integer.class);

    public Immobilizer(Block block, BlockCreateContext context) {
        super(block);
    }

    public Immobilizer(Block block, PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public void onExtend(@NotNull BlockPistonExtendEvent event) {
        event.setCancelled(true);
        for (Player player : event.getBlock().getLocation().getNearbyPlayers(radius)) {
            if (player.getPersistentDataContainer().has(cooldownKey) &&
                    Bukkit.getCurrentTick() - player.getPersistentDataContainer().get(cooldownKey, PersistentDataType.INTEGER) < cooldown) {
                continue;
            }
            frozenPlayers.add(player);
            for (int i = 0; i < duration / particlePeriod; i++) {
                Bukkit.getScheduler().runTaskLaterAsynchronously(PylonBase.getInstance(), new PlayerVFX(player, this), (long) i * particlePeriod);
            }
            player.getPersistentDataContainer().set(cooldownKey, PersistentDataType.INTEGER, Bukkit.getCurrentTick());
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(PylonBase.getInstance(), () -> frozenPlayers.clear(), duration);
    }

    public static class PlayerVFX implements Runnable {
        private final Player player;
        private final Immobilizer block;

        public PlayerVFX(Player player, Immobilizer block) {
            this.player = player;
            this.block = block;
        }

        @Override
        public void run() {
            player.spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), block.particleCount,
                    block.particleRadius, block.particleRadius, block.particleRadius);
        }
    }

    public static class FreezeListener implements Listener {

        @EventHandler
        void onPlayerMove(PlayerMoveEvent event) {
            // There is some rubber-banding with this approach, but Player.setWalk/FlySpeed does not account for jumping
            if (event.hasExplicitlyChangedPosition() && frozenPlayers.contains(event.getPlayer())) {
                event.setCancelled(true);
            }
        }
    }

}
