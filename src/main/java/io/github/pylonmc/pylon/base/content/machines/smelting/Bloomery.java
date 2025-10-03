package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.resources.Bloom;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.particles.PylonParticleBuilder;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public final class Bloomery extends PylonBlock implements PylonSimpleMultiblock, PylonInteractBlock, PylonTickingBlock {

    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final float heatChance = getSettings().getOrThrow("heat-chance", ConfigAdapter.FLOAT);

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
        setTickInterval(tickInterval);
    }

    @SuppressWarnings("unused")
    public Bloomery(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull ItemStack getDropItem(@NotNull BlockBreakContext context) {
        return getItemDisplay().getItemStack();
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) return;
        event.setCancelled(true);
        if (!isFormedAndFullyLoaded()) return;
        ItemStack placedItem = event.getItem();

        ItemDisplay itemDisplay = getItemDisplay();
        ItemStack oldStack = itemDisplay.getItemStack();
        if (oldStack.getType().isAir()) {
            if (placedItem != null) {
                itemDisplay.setItemStack(placedItem.asOne());
                placedItem.subtract();
            }
        } else {
            Player player = event.getPlayer();
            for (ItemStack stack : player.getInventory().addItem(oldStack).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), stack);
            }
            itemDisplay.setItemStack(null);
        }
    }

    @Override
    public void tick(double deltaSeconds) {
        ItemDisplay itemDisplay = getItemDisplay();
        ItemStack stack = itemDisplay.getItemStack();
        if (stack.getType().isAir()) return;

        if (PylonUtils.isPylonSimilar(stack, BaseItems.SPONGE_IRON)) {
            Bloom bloom = new Bloom(BaseItems.BLOOM.clone());
            bloom.setTemperature(0);
            bloom.setWorking(ThreadLocalRandom.current().nextInt(Bloom.MIN_WORKING, Bloom.MAX_WORKING + 1));
            itemDisplay.setItemStack(bloom.getStack());
            return;
        }

        if (!(PylonItem.fromStack(stack) instanceof Bloom bloom)) return;
        if (ThreadLocalRandom.current().nextFloat() > heatChance) return;

        int temperature = bloom.getTemperature();
        if (isFormedAndFullyLoaded()) {
            temperature = Math.min(Bloom.MAX_TEMPERATURE, temperature + 1);
        } else {
            temperature = Math.max(0, temperature - 1);
        }
        bloom.setTemperature(temperature);
        itemDisplay.setItemStack(bloom.getStack());

        Runnable particleSpawner = () -> {
            Location pos = getBlock().getLocation().add(
                    ThreadLocalRandom.current().nextDouble(1),
                    1.2,
                    ThreadLocalRandom.current().nextDouble(1)
            );
            new PylonParticleBuilder.Type.SmallFlame()
                    .velocity(0, 0, 0)
                    .location(pos)
                    .receivers(32, true)
                    .spawn();
        };
        for (int i = 0; i < temperature; i++) {
            Bukkit.getScheduler().runTaskLater(
                    PylonBase.getInstance(),
                    particleSpawner,
                    ThreadLocalRandom.current().nextInt(tickInterval)
            );
        }
    }

    public @NotNull ItemDisplay getItemDisplay() {
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
