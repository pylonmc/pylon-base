package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonEntities;
import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.block.BlockBreakContext;
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
import io.github.pylonmc.pylon.core.persistence.datatypes.PylonSerializers;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static java.lang.Math.PI;


public final class Pedestal {

    private Pedestal() {
        throw new AssertionError("Container class");
    }

    public static class PedestalItem extends PylonItem<PedestalItem.Schema> implements BlockPlacer {

        public static class Schema extends PylonItemSchema {

            private final PylonBlockSchema blockSchema;

            public Schema(@NotNull NamespacedKey key, @NotNull ItemStack template, @NotNull PylonBlockSchema blockSchema) {
                super(key, PedestalItem.class, template);
                this.blockSchema = blockSchema;
            }

            public PylonBlockSchema getBlockSchema() {
                return blockSchema;
            }
        }

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return getSchema().getBlockSchema();
        }

        public PedestalItem(@NotNull PedestalItem.Schema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }
    }

    public static class PedestalBlock extends PylonBlock<PylonBlockSchema>
            implements EntityHolderBlock<PedestalEntity>, PlayerInteractBlock {

        private static final NamespacedKey ROTATION_KEY = KeyUtils.pylonKey("rotation");
        private static final NamespacedKey LOCKED_KEY = KeyUtils.pylonKey("locked");
        private double rotation;
        private boolean locked;
        private UUID uuid;

        public PedestalBlock(PylonBlockSchema schema, Block block, BlockCreateContext context) {
            super(schema, block);

            rotation = 0;
            locked = false;

            ItemDisplay display = new ItemDisplayBuilder()
                    .transformation(transformBuilder().buildForItemDisplay())
                    .build(block.getLocation().toCenterLocation());

            PedestalEntity pylonEntity = new PedestalEntity(PylonEntities.PEDESTAL_ITEM, display);
            uuid = pylonEntity.getEntity().getUniqueId();
            EntityStorage.add(pylonEntity);
        }

        public PedestalBlock(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);
            loadEntity(pdc);
            rotation = pdc.get(ROTATION_KEY, PylonSerializers.DOUBLE);
            locked = pdc.get(LOCKED_KEY, PylonSerializers.BOOLEAN);
        }

        public TransformBuilder transformBuilder() {
            return new TransformBuilder()
                    .translate(0, 0.7, 0)
                    .scale(0.4)
                    .rotate(0, rotation, 0);
        }

        @Override
        public void write(@NotNull PersistentDataContainer pdc) {
            saveEntity(pdc);
            pdc.set(ROTATION_KEY, PylonSerializers.DOUBLE, rotation);
            pdc.set(LOCKED_KEY, PylonSerializers.BOOLEAN, locked);
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
            if (locked || event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            event.setCancelled(true);

            ItemDisplay display = getEntity().getEntity();

            // rotate
            if (event.getPlayer().isSneaking()) {
                rotation += PI / 4;
                display.setTransformationMatrix(transformBuilder().buildForItemDisplay());
                return;
            }

            // drop old item
            ItemStack oldStack = display.getItemStack();
            ItemStack newStack = event.getItem();
            if (!oldStack.getType().isAir()) {
                display.getWorld().dropItemNaturally(display.getLocation().toCenterLocation().add(0, 0.7, 0), oldStack);
                display.setItemStack(null);
                return;
            }

            // insert new item
            if (newStack != null) {
                ItemStack stackToInsert = newStack.clone();
                stackToInsert.setAmount(1);
                display.setItemStack(stackToInsert);
                newStack.subtract();
            }
        }

        @Override
        public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
            EntityHolderBlock.super.onBreak(drops, context);
            drops.add(getEntity().getEntity().getItemStack());
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }
    }

    public static class PedestalEntity extends PylonEntity<PylonEntitySchema, ItemDisplay> {

        public PedestalEntity(@NotNull PylonEntitySchema schema, @NotNull ItemDisplay entity) {
            super(schema, entity);
        }
    }
}
