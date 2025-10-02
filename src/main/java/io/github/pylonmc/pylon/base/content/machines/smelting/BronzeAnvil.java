package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.List;

public final class BronzeAnvil extends PylonBlock implements PylonEntityHolderBlock, PylonTickingBlock, PylonInteractBlock {

    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    private static final Matrix4f BASE_TRANSFORM = new TransformBuilder()
            .scale(0.3)
            .translate(0, (1 - .5 + 1d / 16) * 3, 0)
            .rotate(Math.PI / 2, 0, 0)
            .buildForItemDisplay()
            .rotateLocalY((float) (Math.PI / 2));

    @SuppressWarnings("unused")
    public BronzeAnvil(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        BlockFace orientation = ((Directional) block.getBlockData()).getFacing();
        addEntity("item", new SimpleItemDisplay(new ItemDisplayBuilder()
                .transformation(new Matrix4f(BASE_TRANSFORM)
                        .rotateLocalY(getItemRotation()))
                .build(getBlock().getLocation().toCenterLocation())
        ));
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
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) return;
        event.setCancelled(true);
        //noinspection DuplicatedCode
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

    }

    public @NotNull ItemDisplay getItemDisplay() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "item").getEntity();
    }

    private float getItemRotation() {
        BlockFace orientation = ((Directional) getBlock().getBlockData()).getFacing();
        return (float) switch (orientation) {
            case NORTH -> 0;
            case EAST -> 3 * Math.PI / 2;
            case SOUTH -> Math.PI;
            case WEST -> Math.PI / 2;
            default -> 0;
        };
    }
}
