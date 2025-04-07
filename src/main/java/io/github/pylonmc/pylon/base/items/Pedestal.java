package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.base.PylonEntities;
import io.github.pylonmc.pylon.core.block.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.EntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PlayerInteractBlock;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static java.lang.Math.PI;


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

        private TransformBuilder transformBuilder;
        private UUID uuid;

        public PedestalBlock(PylonBlockSchema schema, Block block, BlockCreateContext context) {
            super(schema, block);

            transformBuilder = new TransformBuilder()
                            .translate(0, 0.7, 0)
                            .scale(0.4);

            ItemDisplay display = new ItemDisplayBuilder()
                    .transformation(transformBuilder.buildForItemDisplay())
                    .build(block.getLocation().toCenterLocation());

            PedestalEntity pylonEntity = new PedestalEntity(PylonEntities.PEDESTAL_ITEM, display);
            uuid = pylonEntity.getEntity().getUniqueId();
            EntityStorage.add(pylonEntity);
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
            if (event.getHand() == EquipmentSlot.HAND && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (event.getPlayer().isSneaking()) {
                    transformBuilder.rotate(0, PI / 4, 0);
                    getEntity().getEntity().setTransformationMatrix(transformBuilder.buildForItemDisplay());
                } else {
                    getEntity().getEntity().setItemStack(event.getItem());
                    event.setCancelled(true);
                }
            }
        }
    }

    public static class PedestalEntity extends PylonEntity<PylonEntitySchema, ItemDisplay> {

        public PedestalEntity(@NotNull PylonEntitySchema schema, @NotNull ItemDisplay entity) {
            super(schema, entity);
        }
    }
}
