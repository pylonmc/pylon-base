package io.github.pylonmc.pylon.base.util;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.persistence.blockstorage.BlockStorage;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNullByDefault;

// TODO move to pylon-core
@NotNullByDefault
public sealed interface BlockType {

    boolean matches(Block block);

    void place(Block block);

    record Vanilla(Material material) implements BlockType {
        @Override
        public boolean matches(Block block) {
            return block.getType() == material && BlockStorage.get(block) == null;
        }

        @Override
        public void place(Block block) {
            block.setType(material);
        }
    }

    record Pylon(NamespacedKey key) implements BlockType {
        @Override
        public boolean matches(Block block) {
            PylonBlock<?> pylonBlock = BlockStorage.get(block);
            return pylonBlock != null && pylonBlock.getSchema().getKey().equals(key);
        }

        @Override
        public void place(Block block) {
            BlockStorage.set(block, PylonRegistry.BLOCKS.getOrThrow(key));
        }
    }
}
