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
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
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

        public ImmobilizerBlock(Schema schema, Block block, BlockCreateContext context) {
            super(schema, block);
        }

        public ImmobilizerBlock(Schema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);
        }

        @Override
        public void onExtend(@NotNull BlockPistonExtendEvent event) {
            event.setCancelled(true);
            for (Player player : event.getBlock().getLocation().getNearbyPlayers(getSchema().radius)) {
                frozenPlayers.add(player);
                Bukkit.getScheduler().runTaskLaterAsynchronously(PylonBase.getInstance(), new UnfreezePlayer(player), getSchema().duration);
            }
        }

        public static class Schema extends PylonBlockSchema {
            public final double radius = getSettings().getOrThrow("radius", Double.class);
            public final int duration = getSettings().getOrThrow("duration", Integer.class);
            public final double cooldown = getSettings().getOrThrow("cooldown", Double.class);

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
            return Map.of("radius", Component.text(getSchema().block.radius), "duration", Component.text(getSchema().block.duration));
        }
    }
}
