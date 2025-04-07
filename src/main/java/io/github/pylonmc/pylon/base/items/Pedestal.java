package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.core.block.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.EntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PlayerInteractBlock;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


public final class Pedestal {

    private Pedestal() {
        throw new AssertionError("Container class");
    }

    public static class PedestalItem extends PylonItem<PylonItemSchema> implements BlockPlacer {

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return PylonBlocks.PEDESTAL;
        }

        public PedestalItem(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }
    }

    public static class PedestalBlock extends PylonBlock<PylonBlockSchema>
            implements EntityHolderBlock<PedestalEntity>, PlayerInteractBlock {

        private UUID uuid;

        public PedestalBlock(PylonBlockSchema schema, Block block, BlockCreateContext context) {
            super(schema, block);
        }

        public PedestalBlock(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);
            loadEntity(pdc);
        }

        @Override
        public void write(@NotNull PersistentDataContainer pdc) {
            saveEntity(pdc);
        }

        @Override
        public @NotNull UUID getEntityUuid() {
            return uuid;
        }

        @Override
        public void setEntityUuid(@NotNull UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public void onInteract(@NotNull PlayerInteractEvent event) {
            getEntity().getEntity().setItemStack(event.getItem());
        }
    }

    public static class PedestalEntity extends PylonEntity<PylonEntitySchema, ItemDisplay> {

        public PedestalEntity(@NotNull PylonEntitySchema schema, @NotNull Location location) {
            super(schema, new ItemDisplayBuilder().build(location));
        }

        public PedestalEntity(@NotNull PylonEntitySchema schema, @NotNull ItemDisplay entity) {
            super(schema, entity);
        }
    }
}
