package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.List;
import java.util.Map;

public class Bloomery extends PylonBlock implements PylonSimpleMultiblock, PylonInteractBlock {

    @SuppressWarnings("unused")
    public Bloomery(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        addEntity("item", new SimpleItemDisplay(new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .scale(0.3)
                        .translate(0, (1 - .5 + 1d / 16) * 3, 0)
                        .rotate(Math.PI / 2, 0, 0))
                .build(getBlock().getLocation().toCenterLocation())
        ));
    }

    @SuppressWarnings("unused")
    public Bloomery(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @Nullable ItemStack getDropItem(@NotNull BlockBreakContext context) {
        return null;
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        event.setCancelled(true);
        if (!isFormedAndFullyLoaded()) return;
        ItemStack placedItem = event.getItem();
        if (placedItem == null) return;

        ItemDisplay itemDisplay = getItemDisplay();
        ItemStack oldStack = itemDisplay.getItemStack();
        if (oldStack.getType().isAir()) {
            itemDisplay.setItemStack(placedItem.asOne());
            placedItem.subtract();
        } else {
            event.getPlayer().getInventory().addItem(oldStack);
            itemDisplay.setItemStack(null);
        }
    }

    public ItemDisplay getItemDisplay() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "item").getEntity();
    }

    @Override
    public @NotNull Map<@NotNull Vector3i, @NotNull MultiblockComponent> getComponents() {
        return Map.of(
                new Vector3i(0, 2, 0), new PylonMultiblockComponent(BaseKeys.REFRACTORY_BRICKS),
                new Vector3i(1, 1, 0), new PylonMultiblockComponent(BaseKeys.REFRACTORY_BRICKS),
                new Vector3i(-1, 1, 0), new PylonMultiblockComponent(BaseKeys.REFRACTORY_BRICKS),
                new Vector3i(0, 1, 1), new PylonMultiblockComponent(BaseKeys.REFRACTORY_BRICKS)
        );
    }

    public static class CreationListener implements Listener {
        @EventHandler
        private void onSetFire(@NotNull BlockPlaceEvent event) {
            Block fire = event.getBlockPlaced();
            if (fire.getType() != Material.FIRE) return;
            Block against = event.getBlockAgainst();
            if (against.getType() != Material.COAL_BLOCK) return;
            List<Item> items = against.getWorld().getNearbyEntities(BoundingBox.of(fire)).stream()
                    .filter(e -> e instanceof Item)
                    .map(e -> (Item) e)
                    .toList();
            if (items.isEmpty()) return;
            Item gypsum = null;
            for (Item item : items) {
                if (PylonUtils.isPylonSimilar(item.getItemStack(), BaseItems.GYPSUM_DUST)) {
                    gypsum = item;
                    break;
                }
            }
            if (gypsum == null) return;
            gypsum.remove();
            BlockStorage.placeBlock(against, BaseKeys.BLOOMERY);
        }
    }
}
