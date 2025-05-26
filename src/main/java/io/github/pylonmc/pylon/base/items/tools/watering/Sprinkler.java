package io.github.pylonmc.pylon.base.items.tools.watering;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.event.PrePylonBlockPlaceEvent;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Function;


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
                    @NotNull Function<NamespacedKey, ItemStack> templateSupplier
            ) {
                super(key, itemClass, templateSupplier);
            }
        }

        public SprinklerItem(@NotNull Schema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }

        @Override
        public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
            return Map.of("range", UnitFormat.BLOCKS.format(HORIZONTAL_RANGE));
        }
    }

    public static class SprinklerBlock extends PylonBlock<SprinklerBlock.Schema> implements PylonTickingBlock {

        public static class Schema extends PylonBlockSchema {

            // TODO make this configurable once block settings are a thing
            private final WateringSettings settings = new WateringSettings(
                    HORIZONTAL_RANGE,
                    VERTICAL_RANGE,
                    0.01,
                    0.007,
                    0.01,
                    0.01,
                    Sound.WEATHER_RAIN
            );

            public Schema(
                    @NotNull NamespacedKey key,
                    @NotNull Material material,
                    @NotNull Class<? extends PylonBlock<?>> blockClass
            ) {
                super(key, material, blockClass);
            }
        }

        @SuppressWarnings("unused")
        public SprinklerBlock(Schema schema, Block block, BlockCreateContext context) {
            super(schema, block);
        }

        @SuppressWarnings("unused")
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

            WateringCan.WateringCanItem.water(getBlock(), getSchema().settings);

            new ParticleBuilder(Particle.SPLASH)
                    .count(5)
                    .location(getBlock().getLocation().add(0.5, 0.5, 0.5))
                    .spawn();
        }
    }

    public static class SprinklerPlaceListener implements Listener {
        @EventHandler
        private static void handle(@NotNull PrePylonBlockPlaceEvent event) {
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
                            context.getPlayer().sendMessage(Component.translatable(
                                    "pylon.pylonbase.message.sprinkler_too_close",
                                    PylonArgument.of("radius", horizontalRadiusToCheck)
                            ));
                        }
                        break;
                    }
                }
            }
        }
    }
}
