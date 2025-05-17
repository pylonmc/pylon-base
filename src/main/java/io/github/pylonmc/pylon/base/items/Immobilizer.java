package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonPiston;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class Immobilizer {
    private Immobilizer() {
        throw new AssertionError("Container class");
    }

    public static class ImmobilizerBlock extends PylonBlock<ImmobilizerBlock.Schema> implements PylonPiston {
        public static Set<Player> frozenPlayers = new HashSet<>();
        private int cooldownTickNo;
        private final TranslatableComponent cooldownMsg = Component.translatable("pylon.pylonbase.message.immobilizer_on_cooldown");

        public ImmobilizerBlock(Schema schema, Block block, BlockCreateContext context) {
            super(schema, block);
        }

        public ImmobilizerBlock(Schema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);
        }

        @Override
        public void onExtend(@NotNull BlockPistonExtendEvent event) {
            event.setCancelled(true);
            if(Bukkit.getCurrentTick() < cooldownTickNo + getSchema().cooldown){
                for(Player player : event.getBlock().getLocation().getNearbyPlayers(getSchema().radius)){
                    player.sendMessage(cooldownMsg.color(NamedTextColor.RED));
                    new PlayerVFX(player, Particle.LARGE_SMOKE, getSchema()).run();
                    // particles are packets-based so they should be okay to run async
                    Bukkit.getScheduler().runTaskLaterAsynchronously(PylonBase.getInstance(), new PlayerVFX(player, Particle.LARGE_SMOKE, getSchema()), getSchema().particlePeriod / 2);
                }
                return;
            }
            for (Player player : event.getBlock().getLocation().getNearbyPlayers(getSchema().radius)) {
                frozenPlayers.add(player);
                for(int i = 0; i < getSchema().duration / getSchema().particlePeriod; i++){
                    Bukkit.getScheduler().runTaskLaterAsynchronously(PylonBase.getInstance(), new PlayerVFX(player, Particle.ELECTRIC_SPARK, getSchema()), (long) i * getSchema().particlePeriod);
                }
                Bukkit.getScheduler().runTaskLaterAsynchronously(PylonBase.getInstance(), new UnfreezePlayer(player), getSchema().duration);
            }
            cooldownTickNo = Bukkit.getCurrentTick();
        }

        public static class PlayerVFX implements Runnable {
            private final Player player;
            private final Particle particle;
            private final Schema schema;

            public PlayerVFX(Player player, Particle particle, Schema schema){
                this.player = player;
                this.particle = particle;
                this.schema = schema;
            }

            @Override
            public void run() {
                player.spawnParticle(particle, player.getLocation(), schema.particleCount,
                        schema.particleRadius, schema.particleRadius, schema.particleRadius);
            }
        }

        public static class Schema extends PylonBlockSchema {
            public final double radius = getSettings().getOrThrow("radius", Double.class);
            public final int duration = getSettings().getOrThrow("duration", Integer.class);
            public final int cooldown = getSettings().getOrThrow("cooldown", Integer.class);
            public final int particleCount = getSettings().getOrThrow("particle-count", Integer.class);
            public final double particleRadius = getSettings().getOrThrow("particle-radius", Double.class);
            public final int particlePeriod = getSettings().getOrThrow("particle-period", Integer.class);

            public Schema(@NotNull NamespacedKey key, @NotNull Material material, @NotNull Class<? extends @NotNull PylonBlock<?>> blockClass) {
                super(key, material, blockClass);
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

        public static class UnfreezePlayer implements Runnable {
            private final Player player;

            public UnfreezePlayer(Player player) {
                this.player = player;
            }

            @Override
            public void run() {
                // Removes if present so not a problem if onRetract is called
                frozenPlayers.remove(player);
            }
        }
    }

    public static class ImmobilizerItem extends PylonItem<ImmobilizerItem.Schema> implements BlockPlacer {
        public ImmobilizerItem(@NotNull Schema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return getSchema().block;
        }

        public static class Schema extends PylonItemSchema {
            public final ImmobilizerBlock.Schema block;

            public Schema(@NotNull NamespacedKey key, @NotNull Class<? extends @NotNull PylonItem<? extends @NotNull PylonItemSchema>> itemClass, @NotNull Function<@NotNull NamespacedKey, @NotNull ItemStack> templateSupplier, ImmobilizerBlock.Schema block) {
                super(key, itemClass, templateSupplier);
                this.block = block;
            }
        }

        @Override
        public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
            return Map.of("radius", Component.text(getSchema().block.radius),
                          "duration", Component.text(getSchema().block.duration / 20),
                          "cooldown", Component.text(getSchema().block.cooldown / 20));
        }
    }
}
