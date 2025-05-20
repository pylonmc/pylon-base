package io.github.pylonmc.pylon.base.items.multiblocks;

import io.github.pylonmc.pylon.base.PylonEntities;
import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import lombok.Setter;
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
import java.util.Map;
import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;
import static java.lang.Math.PI;


public final class Pedestal {

    private Pedestal() {
        throw new AssertionError("Container class");
    }

    public static class PedestalBlock extends PylonBlock<PylonBlockSchema>
            implements PylonEntityHolderBlock, PylonInteractableBlock {

        private static final NamespacedKey ROTATION_KEY = pylonKey("rotation");
        private static final NamespacedKey LOCKED_KEY = pylonKey("locked");
        private double rotation;
        @Setter
        private boolean locked;
        private final Map<String, UUID> entities;

        @SuppressWarnings("unused")
        public PedestalBlock(PylonBlockSchema schema, Block block, BlockCreateContext context) {
            super(schema, block);

            rotation = 0;
            locked = false;

            ItemDisplay display = new ItemDisplayBuilder()
                    .transformation(transformBuilder().buildForItemDisplay())
                    .build(block.getLocation().toCenterLocation());
            PedestalEntity pylonEntity = new PedestalEntity(PylonEntities.PEDESTAL_ITEM, display);
            EntityStorage.add(pylonEntity);

            entities = Map.of("item", pylonEntity.getUuid());
        }

        @SuppressWarnings({"unused", "DataFlowIssue"})
        public PedestalBlock(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);
            entities = loadHeldEntities(pdc);
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
            saveHeldEntities(pdc);
            pdc.set(ROTATION_KEY, PylonSerializers.DOUBLE, rotation);
            pdc.set(LOCKED_KEY, PylonSerializers.BOOLEAN, locked);
        }

        @Override
        public @NotNull Map<String, UUID> getHeldEntities() {
            return entities;
        }

        @Override
        public void onInteract(@NotNull PlayerInteractEvent event) {
            if (locked || event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            event.setCancelled(true);

            ItemDisplay display = getHeldEntity(PedestalEntity.class, "item").getEntity();

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
                display.getWorld().dropItem(display.getLocation().toCenterLocation().add(0, 0.7, 0), oldStack);
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
            PylonEntityHolderBlock.super.onBreak(drops, context);
            drops.add(getItemDisplay().getItemStack());
        }

        public @NotNull ItemDisplay getItemDisplay() {
            return getHeldEntity(PedestalEntity.class, "item").getEntity();
        }
    }

    public static class PedestalEntity extends PylonEntity<PylonEntitySchema, ItemDisplay> {

        public PedestalEntity(@NotNull PylonEntitySchema schema, @NotNull ItemDisplay entity) {
            super(schema, entity);
        }
    }
}
