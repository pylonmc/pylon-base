package io.github.pylonmc.pylon.base.content.machines.smelting;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.resources.IronBloom;
import io.github.pylonmc.pylon.base.content.tools.Hammer;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFallingBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class BronzeAnvil extends PylonBlock implements PylonFallingBlock, PylonBreakHandler, PylonEntityHolderBlock, PylonTickingBlock, PylonInteractBlock {

    public static final int TICK_INTERVAL = Settings.get(BaseKeys.BRONZE_ANVIL).getOrThrow("tick-interval", ConfigAdapter.INT);
    public static final float COOL_CHANCE = Settings.get(BaseKeys.BRONZE_ANVIL).getOrThrow("cool-chance", ConfigAdapter.FLOAT);
    public static final int TOLERANCE = Settings.get(BaseKeys.BRONZE_ANVIL).getOrThrow("tolerance", ConfigAdapter.INT);
    public static final Sound HAMMER_SOUND = Settings.get(BaseKeys.BRONZE_ANVIL).getOrThrow("sound.hammer", ConfigAdapter.SOUND);
    public static final Sound TONGS_SOUND = Settings.get(BaseKeys.BRONZE_ANVIL).getOrThrow("sound.tongs", ConfigAdapter.SOUND);

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
                        .rotateLocalY(getItemRotation(getBlockFace())))
                .build(getBlock().getLocation().toCenterLocation())
        );
        setTickInterval(TICK_INTERVAL);
    }

    @SuppressWarnings("unused")
    public BronzeAnvil(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
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
        ItemStack placedItem = event.getItem();

        ItemDisplay itemDisplay = getItemDisplay();
        ItemStack oldStack = itemDisplay.getItemStack();
        if (oldStack.getType().isAir()) {
            if (placedItem != null) {
                itemDisplay.setItemStack(placedItem.asOne());
                placedItem.subtract();
                if (PylonItem.fromStack(itemDisplay.getItemStack()) instanceof IronBloom bloom) {
                    transformForWorking(bloom.getWorking(), false);
                    bloom.setDisplayGlowOn(itemDisplay);
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
        if (!(PylonItem.fromStack(itemDisplay.getItemStack()) instanceof IronBloom bloom)) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        Player player = event.getPlayer();

        int temperature = bloom.getTemperature();
        int workingChange = ThreadLocalRandom.current().nextInt(-1, 2);
        if (temperature == 0) {
            player.swingHand(EquipmentSlot.HAND);
            return;
        } else if (PylonUtils.isPylonSimilar(item, BaseItems.TONGS)) {
            workingChange -= temperature;
            getBlock().getWorld().playSound(TONGS_SOUND, player);
        } else if (PylonItem.fromStack(item) instanceof Hammer hammer) {
            if (!player.hasCooldown(item)) {
                workingChange += temperature;
                player.setCooldown(item, hammer.cooldownTicks);
                getBlock().getWorld().playSound(HAMMER_SOUND, player);
            }
        } else {
            return;
        }

        event.setCancelled(true);
        int working = bloom.getWorking();
        int newWorking = working + workingChange;
        Location centerLoc = getBlock().getRelative(BlockFace.UP).getLocation().toCenterLocation();
        if (Math.abs(newWorking) > IronBloom.MAX_WORKING) {
            new ParticleBuilder(Particle.ITEM)
                    .location(centerLoc)
                    .receivers(32, true)
                    .offset(0.03, 0.01, 0.03)
                    .data(bloom.getStack())
                    .count(8)
                    .spawn();
            itemDisplay.setItemStack(null);
            return;
        } else {
            new ParticleBuilder(Particle.LAVA).location(centerLoc)
                    .receivers(32, true)
                    .offset(0.1, 0.1, 0.1)
                    .extra(0.03)
                    .count(temperature)
                    .spawn();
        }
        bloom.setWorking(newWorking);
        itemDisplay.setItemStack(bloom.getStack());
        transformForWorking(newWorking, PylonUtils.isPylonSimilar(item, BaseItems.TONGS));
    }

    @Override
    public void tick(double deltaSeconds) {
        if (ThreadLocalRandom.current().nextFloat() > COOL_CHANCE) return;
        ItemDisplay itemDisplay = getItemDisplay();
        if (!(PylonItem.fromStack(itemDisplay.getItemStack()) instanceof IronBloom bloom)) return;
        int newTemperature = Math.max(0, bloom.getTemperature() - 1);
        bloom.setTemperature(newTemperature);
        bloom.setDisplayGlowOn(itemDisplay);
        if (bloom.getWorking() >= -TOLERANCE && bloom.getWorking() <= TOLERANCE && newTemperature == 0) {
            itemDisplay.setItemStack(null);
            Location centerLoc = getBlock().getLocation().toCenterLocation();
            centerLoc.getWorld().dropItemNaturally(centerLoc, BaseItems.WROUGHT_IRON.clone());
            return;
        }
        itemDisplay.setItemStack(bloom.getStack());
    }

    public static final NamespacedKey DIRECTION_FALLING = baseKey("direction_falling");
    public static final NamespacedKey STORED_ITEM = baseKey("stored_item");
    @Override
    public void onFallStart(@NotNull EntityChangeBlockEvent event, @NotNull PylonFallingBlockEntity spawnedEntity) {
        var pdc = spawnedEntity.getEntity().getPersistentDataContainer();
        PylonUtils.setNullable(pdc, STORED_ITEM, PylonSerializers.ITEM_STACK, getItemDisplay().getItemStack());
        pdc.set(DIRECTION_FALLING, PylonSerializers.BLOCK_FACE, getBlockFace());
    }

    @Override
    public void onFallStop(@NotNull EntityChangeBlockEvent event, @NotNull PylonFallingBlockEntity entity) {
        var pdc = entity.getEntity().getPersistentDataContainer();

        BlockFace face = pdc.get(DIRECTION_FALLING, PylonSerializers.BLOCK_FACE);
        ItemStack stack = pdc.get(DIRECTION_FALLING, PylonSerializers.ITEM_STACK);
        addEntity("item", new ItemDisplayBuilder()
                .itemStack(stack)
                .transformation(new Matrix4f(BASE_TRANSFORM)
                        .rotateLocalY(getItemRotation(face)))
                .build(getBlock().getLocation().toCenterLocation())
        );
    }

    public @NotNull ItemDisplay getItemDisplay() {
        return getHeldEntityOrThrow(ItemDisplay.class, "item");
    }

    private void transformForWorking(int working, boolean interpolate) {
        Matrix4f transform = new Matrix4f(BASE_TRANSFORM)
                .rotateLocalY(getItemRotation(getBlockFace()))
                .scaleLocal(
                        Math.max(0, working * 0.5f) + 1,
                        1,
                        Math.max(0, -working * 0.5f) + 1
                );
        if (interpolate) {
            BaseUtils.animate(getItemDisplay(), 5, transform);
        } else {
            getItemDisplay().setTransformationMatrix(transform);
        }
    }

    private BlockFace getBlockFace() {
        return ((Directional) getBlock().getBlockData()).getFacing();
    }

    private static float getItemRotation(BlockFace face) {
        return (float) switch (face) {
            case NORTH -> 3 * Math.PI / 2;
            case EAST -> Math.PI;
            case SOUTH -> Math.PI / 2;
            case WEST -> 0;
            default -> 0;
        };
    }
}
