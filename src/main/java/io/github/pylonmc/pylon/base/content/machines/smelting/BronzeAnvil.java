package io.github.pylonmc.pylon.base.content.machines.smelting;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.content.resources.Bloom;
import io.github.pylonmc.pylon.base.content.tools.Hammer;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class BronzeAnvil extends PylonBlock implements PylonEntityHolderBlock, PylonTickingBlock, PylonInteractBlock {

    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final float coolChance = getSettings().getOrThrow("cool-chance", ConfigAdapter.FLOAT);

    private static final Matrix4f BASE_TRANSFORM = new TransformBuilder()
            .scale(0.3)
            .translate(0, (1 - .5 + 1d / 16) * 3, 0)
            .rotate(Math.PI / 2, 0, 0)
            .buildForItemDisplay();

    @SuppressWarnings("unused")
    public BronzeAnvil(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        BlockFace orientation = ((Directional) block.getBlockData()).getFacing();
        addEntity("item", new ItemDisplayBuilder()
                .transformation(new Matrix4f(BASE_TRANSFORM)
                        .rotateLocalY(getItemRotation()))
                .build(getBlock().getLocation().toCenterLocation())
        );
        setTickInterval(tickInterval);
    }

    @SuppressWarnings("unused")
    public BronzeAnvil(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonEntityHolderBlock.super.onBreak(drops, context);
        ItemStack stack = getItemDisplay().getItemStack();
        if (!stack.isEmpty()) {
            drops.add(stack);
        }
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction().isRightClick()) {
            onRightClick(event);
        } else if (event.getAction().isLeftClick()) {
            onLeftClick(event);
        }
    }

    private void onRightClick(@NotNull PlayerInteractEvent event) {
        //noinspection DuplicatedCode
        ItemStack placedItem = event.getItem();

        ItemDisplay itemDisplay = getItemDisplay();
        ItemStack oldStack = itemDisplay.getItemStack();
        if (oldStack.getType().isAir()) {
            if (placedItem != null) {
                itemDisplay.setItemStack(placedItem.asOne());
                placedItem.subtract();
                if (PylonItem.fromStack(itemDisplay.getItemStack()) instanceof Bloom bloom) {
                    transformForWorking(bloom.getWorking());
                }
            }
        } else {
            Player player = event.getPlayer();
            for (ItemStack stack : player.getInventory().addItem(oldStack).values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), stack);
            }
            itemDisplay.setItemStack(null);
        }
        event.setCancelled(true);
        event.getPlayer().swingHand(EquipmentSlot.HAND);
    }

    private void onLeftClick(@NotNull PlayerInteractEvent event) {
        ItemDisplay itemDisplay = getItemDisplay();
        if (!(PylonItem.fromStack(itemDisplay.getItemStack()) instanceof Bloom bloom)) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        Player player = event.getPlayer();

        int temperature = bloom.getTemperature();
        int workingChange = ThreadLocalRandom.current().nextInt(-1, 2);
        if (temperature == 0) {
            workingChange = 0;
        } else if (PylonUtils.isPylonSimilar(item, BaseItems.TONGS)) {
            workingChange -= temperature;
        } else if (PylonItem.fromStack(item) instanceof Hammer hammer) {
            if (!player.hasCooldown(item)) {
                workingChange += temperature;
                player.setCooldown(item, hammer.cooldownTicks);
            }
        } else {
            return;
        }

        event.setCancelled(true);
        player.swingHand(EquipmentSlot.HAND);
        if (temperature == 0) return;

        int working = bloom.getWorking();
        int newWorking = working + workingChange;
        Location centerLoc = getBlock().getRelative(BlockFace.UP).getLocation().toCenterLocation();
        ParticleBuilder builder;
        if (Math.abs(newWorking) > Bloom.MAX_WORKING) {
            new ParticleBuilder(Particle.ITEM)
                    .location(centerLoc)
                    .receivers(32, true)
                    .offset(0.03, 0.01, 0.03)
                    .data(bloom.getStack())
                    .count(8)
                    .spawn();
            itemDisplay.setItemStack(null);
            return;
        } else if (Math.abs(newWorking) < Math.abs(working)) {
            builder = new ParticleBuilder(Particle.SMALL_FLAME);
        } else {
            builder = new ParticleBuilder(Particle.CRIT);
        }
        builder.location(centerLoc)
                .receivers(32, true)
                .offset(0.3, 0.1, 0.3)
                .extra(0.03)
                .count(temperature)
                .spawn();
        bloom.setWorking(newWorking);
        itemDisplay.setItemStack(bloom.getStack());
        transformForWorking(newWorking);
    }

    @Override
    public void tick(double deltaSeconds) {
        if (ThreadLocalRandom.current().nextFloat() > coolChance) return;
        ItemDisplay itemDisplay = getItemDisplay();
        if (!(PylonItem.fromStack(itemDisplay.getItemStack()) instanceof Bloom bloom)) return;
        int newTemperature = Math.max(0, bloom.getTemperature() - 1);
        bloom.setTemperature(newTemperature);
        if (bloom.getWorking() == 0 && newTemperature == 0) {
            itemDisplay.setItemStack(null);
            Location centerLoc = getBlock().getLocation().toCenterLocation();
            centerLoc.getWorld().dropItemNaturally(centerLoc, BaseItems.WROUGHT_IRON.clone());
            itemDisplay.setItemStack(null);
            return;
        }
        itemDisplay.setItemStack(bloom.getStack());
    }

    public @NotNull ItemDisplay getItemDisplay() {
        return getHeldEntityOrThrow(ItemDisplay.class, "item");
    }

    private void transformForWorking(int working) {
        Matrix4f transform = new Matrix4f(BASE_TRANSFORM)
                .rotateLocalY(getItemRotation())
                .scaleLocal(
                        Math.max(0, working * 0.5f) + 1,
                        1,
                        Math.max(0, -working * 0.5f) + 1
                );
        getItemDisplay().setTransformationMatrix(transform);
    }

    private float getItemRotation() {
        BlockFace orientation = ((Directional) getBlock().getBlockData()).getFacing();
        return (float) switch (orientation) {
            case NORTH -> 3 * Math.PI / 2;
            case EAST -> Math.PI;
            case SOUTH -> Math.PI / 2;
            case WEST -> 0;
            default -> 0;
        };
    }
}
