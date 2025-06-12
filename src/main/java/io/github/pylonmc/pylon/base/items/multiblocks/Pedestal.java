package io.github.pylonmc.pylon.base.items.multiblocks;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
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

public class Pedestal extends PylonBlock implements PylonEntityHolderBlock, PylonInteractableBlock {

    public static final NamespacedKey PEDESTAL_KEY = pylonKey("pedestal");
    public static final NamespacedKey MAGIC_PEDESTAL_KEY = pylonKey("magic_pedestal");

    private static final NamespacedKey ROTATION_KEY = pylonKey("rotation");
    private static final NamespacedKey LOCKED_KEY = pylonKey("locked");
    private double rotation;
    @Setter
    private boolean locked;

    @SuppressWarnings("unused")
    public Pedestal(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        rotation = 0;
        locked = false;
}

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public Pedestal(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        
        rotation = pdc.get(ROTATION_KEY, PylonSerializers.DOUBLE);
        locked = pdc.get(LOCKED_KEY, PylonSerializers.BOOLEAN);
    }
    
    @Override
    public Map<String, UUID> createEntities(@NotNull BlockCreateContext context) {
        ItemDisplay display = new ItemDisplayBuilder()
                .transformation(transformBuilder().buildForItemDisplay())
                .build(getBlock().getLocation().toCenterLocation());
        PedestalItemEntity pylonEntity = new PedestalItemEntity(display);
        EntityStorage.add(pylonEntity);
        return Map.of("item", pylonEntity.getUuid());
    }

    public TransformBuilder transformBuilder() {
        return new TransformBuilder()
                .translate(0, 0.7, 0)
                .scale(0.4)
                .rotate(0, rotation, 0);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(ROTATION_KEY, PylonSerializers.DOUBLE, rotation);
        pdc.set(LOCKED_KEY, PylonSerializers.BOOLEAN, locked);
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (locked || event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        ItemDisplay display = getHeldEntity(PedestalItemEntity.class, "item").getEntity();

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

    public ItemDisplay getItemDisplay() {
        return getHeldEntity(PedestalItemEntity.class, "item").getEntity();
    }

    public static class PedestalItemEntity extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("pedestal_item");

        public PedestalItemEntity(@NotNull ItemDisplay entity) {
            super(KEY, entity);
        }
    }
}
