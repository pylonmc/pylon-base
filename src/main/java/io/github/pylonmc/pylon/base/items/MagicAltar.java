package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.block.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.SimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.Ticking;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.Map;


public final class MagicAltar {
    private MagicAltar() {
        throw new AssertionError("Container class");
    }

    public static void drawLine(
            @NotNull Location start,
            @NotNull Location end,
            double spacing,
            @NotNull Particle particle,
            @Nullable Particle.DustOptions dustOptions
    ) {
        double currentPoint = 0;
        Vector startToEnd = end.clone().subtract(start).toVector();
        Vector step = startToEnd.clone().normalize().multiply(spacing);
        double length = startToEnd.length();
        Location current = start.clone();

        while (currentPoint < length) {
            if (dustOptions != null) {
                start.getWorld().spawnParticle(particle, current.getX(), current.getY(), current.getZ(), 1, dustOptions);
            } else {
                start.getWorld().spawnParticle(particle, current.getX(), current.getY(), current.getZ(), 1);
            }
            currentPoint += spacing;
            current.add(step);
        }
    }

    public static class MagicAltarItem extends PylonItem<MagicAltarItem.Schema> implements BlockPlacer {

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return PylonBlocks.MAGIC_ALTAR;
        }

        public static class Schema extends PylonItemSchema {

            public Schema(@NotNull NamespacedKey key, @NotNull ItemStack template) {
                super(key, MagicAltarItem.class, template);
            }
        }

        public MagicAltarItem(@NotNull Schema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }
    }

    public static class MagicAltarBlock extends PylonBlock<MagicAltarBlock.Schema> implements SimpleMultiblock, Ticking {

        private static final Component MAGIC_PEDESTAL_COMPONENT = new SimpleMultiblock.PylonComponent(PylonItems.MAGIC_PEDESTAL.getKey());

        @Override
        public @NotNull Map<Vector3i, Component> getComponents() {
            return Map.of(
                    new Vector3i(3, 0, 0), MAGIC_PEDESTAL_COMPONENT,
                    new Vector3i(2, 0, 2), MAGIC_PEDESTAL_COMPONENT,
                    new Vector3i(0, 0, 3), MAGIC_PEDESTAL_COMPONENT,
                    new Vector3i(-2, 0, 2), MAGIC_PEDESTAL_COMPONENT,
                    new Vector3i(-3, 0, 0), MAGIC_PEDESTAL_COMPONENT,
                    new Vector3i(-2, 0, -2), MAGIC_PEDESTAL_COMPONENT,
                    new Vector3i(0, 0, -3), MAGIC_PEDESTAL_COMPONENT,
                    new Vector3i(2, 0, -2), MAGIC_PEDESTAL_COMPONENT
            );
        }

        public static class Schema extends PylonBlockSchema {

            public Schema(@NotNull NamespacedKey key, @NotNull Material material) {
                super(key, material, MagicAltarBlock.class);
            }
        }

        public MagicAltarBlock(Schema schema, Block block, BlockCreateContext context) {
            super(schema, block);
        }

        public MagicAltarBlock(Schema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);
        }

        @Override
        public int getCustomTickRate(int globalTickRate) {
            return 5;
        }

        @Override
        public void tick(double deltaSeconds) {

            new ParticleBuilder(Particle.FLAME)
                    .count(5)
                    .location(getBlock().getLocation().add(0.5, 1.5, 0.5))
                    .spawn();

            if (!isFormedAndFullyLoaded()) {
                return;
            }

            new ParticleBuilder(Particle.SPLASH)
                    .count(5)
                    .location(getBlock().getLocation().add(0.5, 1.5, 0.5))
                    .spawn();
        }
    }
}
