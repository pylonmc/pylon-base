package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.Ticking;
import io.github.pylonmc.pylon.core.event.PylonBlockPlaceEvent;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;


public final class Sprinkler {

    public static final int HORIZONTAL_RANGE = 4;
    public static final int VERTICAL_RANGE = 4;

    private Sprinkler() {
        throw new AssertionError("Container class");
    }

    public static class SprinklerItem extends PylonItem<SprinklerItem.Schema> implements BlockPlacer {

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return PylonBlocks.SPRINKLER;
        }

        public static class Schema extends PylonItemSchema {

            public Schema(
                    @NotNull NamespacedKey key,
                    @NotNull Class<? extends PylonItem<? extends PylonItemSchema>> itemClass,
                    @NotNull ItemStack template
            ) {
                super(key, itemClass, template);
            }
        }

        public SprinklerItem(@NotNull Schema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }
    }

    public static class SprinklerBlock extends PylonBlock<SprinklerBlock.Schema> implements Ticking {

        public static class Schema extends PylonBlockSchema {

            public Schema(
                    @NotNull NamespacedKey key,
                    @NotNull Material material,
                    @NotNull Class<? extends PylonBlock<?>> blockClass
            ) {
                super(key, material, blockClass);
            }
        }

        public SprinklerBlock(Schema schema, Block block, BlockCreateContext context) {
            super(schema, block);
        }

        public SprinklerBlock(Schema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);
        }

        @Override
        public int getCustomTickRate(int globalTickRate) {
            return 5;
        }

        @Override
        public void tick(double deltaSeconds) {
            if (getBlock().getRelative(BlockFace.DOWN).getType() != Material.WATER) {
                return;
            }

            WateringCan.water(getBlock(), HORIZONTAL_RANGE, VERTICAL_RANGE);

            new ParticleBuilder(Particle.SPLASH)
                    .count(5)
                    .location(getBlock().getLocation().add(0.5, 0.5, 0.5))
                    .spawn();
        }
    }

    public static class SprinklerPlaceListener implements Listener {
        @EventHandler
        private static void handle(@NotNull PylonBlockPlaceEvent event) {
            if (!(event.getPylonBlock() instanceof SprinklerBlock)) {
                return;
            }

            int horizontalRadiusToCheck = 2 * HORIZONTAL_RANGE;
            int verticalRadiusToCheck = 2 * VERTICAL_RANGE;
            for (int x = -horizontalRadiusToCheck; x < horizontalRadiusToCheck; x++) {
                for (int z = -horizontalRadiusToCheck; z < horizontalRadiusToCheck; z++) {
                    for (int y = -verticalRadiusToCheck; y < verticalRadiusToCheck; y++) {
                        if (!(BlockStorage.get(event.getBlock().getRelative(x, y, z)) instanceof SprinklerBlock)) {
                            continue;
                        }

                        event.setCancelled(true);
                        if (event.getContext() instanceof BlockCreateContext.PlayerPlace context) {
                            context.getPlayer().sendMessage(ChatColor.RED
                                    + "You cannot place sprinklers within "
                                    + horizontalRadiusToCheck
                                    + " blocks of each other");
                        }
                        break;
                    }
                }
            }
        }
    }
}
