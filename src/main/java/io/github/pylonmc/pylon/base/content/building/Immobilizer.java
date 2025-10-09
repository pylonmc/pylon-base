package io.github.pylonmc.pylon.base.content.building;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonPiston;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
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
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class Immobilizer extends PylonBlock implements PylonPiston, PylonBreakHandler {
    public static class Item extends PylonItem {
        private final double radius = getSettings().getOrThrow("radius", ConfigAdapter.DOUBLE);
        private final int duration = getSettings().getOrThrow("duration", ConfigAdapter.INT);
        private final int cooldown = getSettings().getOrThrow("cooldown", ConfigAdapter.INT);

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

    public static HashMap<BlockPosition, Set<Player>> frozenPlayers = new HashMap<>();
    private final NamespacedKey cooldownKey = baseKey("immobilizer_cooldown_millis");
    private final double radius = getSettings().getOrThrow("radius", ConfigAdapter.DOUBLE);
    private final int duration = getSettings().getOrThrow("duration", ConfigAdapter.INT);
    private final long cooldownMillis = getSettings().getOrThrow("cooldown", ConfigAdapter.INT) * 50L;
    private final int particleCount = getSettings().getOrThrow("particle.count", ConfigAdapter.INT);
    private final double particleRadius = getSettings().getOrThrow("particle.radius", ConfigAdapter.DOUBLE);
    private final int particlePeriod = getSettings().getOrThrow("particle.period", ConfigAdapter.INT);

    public Immobilizer(Block block, BlockCreateContext context) {
        super(block);
    }

    public Immobilizer(Block block, PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public void onExtend(@NotNull BlockPistonExtendEvent event) {
        event.setCancelled(true);

        BukkitScheduler scheduler = Bukkit.getScheduler();
        BlockPosition position = new BlockPosition(event.getBlock());

        for (Player player : event.getBlock().getLocation().getNearbyPlayers(radius)) {
            PersistentDataContainer playerPdc = player.getPersistentDataContainer();

            long now = System.currentTimeMillis();
            if (playerPdc.has(cooldownKey, PersistentDataType.LONG)) {
                long lastUsed = playerPdc.get(cooldownKey, PersistentDataType.LONG);
                if (now - lastUsed < cooldownMillis) {
                    continue;
                }
            }

            Set<Player> players = frozenPlayers.getOrDefault(position, new HashSet<>());
            players.add(player);
            frozenPlayers.put(position, players);

            int times = duration / particlePeriod;
            new PlayerVFX(player, this, times).runTaskTimer(PylonBase.getInstance(),0, particlePeriod);

            playerPdc.set(cooldownKey, PersistentDataType.LONG, now);
        }

        scheduler.runTaskLater(PylonBase.getInstance(), () -> frozenPlayers.clear(), duration);
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        BlockPosition position = new BlockPosition(context.getBlock());
        frozenPlayers.remove(position);
    }

    public static class PlayerVFX extends BukkitRunnable {
        private final Player player;
        private final Immobilizer block;
        private final BlockPosition pos;
        private final int duration;
        private int tick = 0;

        public PlayerVFX(Player player, Immobilizer block, int duration) {
            this.player = player;
            this.block = block;
            this.pos = new BlockPosition(block.getBlock());
            this.duration = duration;
        }

        @Override
        public void run() {
            // completed
            if (++tick >= duration) {
                cancel();
                return;
            }

            // block is gone
            if (frozenPlayers.get(pos) == null) {
                cancel();
                return;
            }

            player.spawnParticle(Particle.ELECTRIC_SPARK, player.getLocation(), block.particleCount,
                    block.particleRadius, block.particleRadius, block.particleRadius);
        }
    }

    public static class FreezeListener implements Listener {

        @EventHandler
        void onPlayerMove(PlayerMoveEvent event) {
            // There is some rubber-banding with this approach, but Player.setWalk/FlySpeed does not account for jumping
            if (!event.hasExplicitlyChangedPosition()) {
                return;
            }

            for (Set<Player> playerSet : frozenPlayers.values()) {
                if (playerSet.contains(event.getPlayer())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

}
