package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class PitKiln extends PylonBlock implements PylonSimpleMultiblock, PylonInteractableBlock, PylonBreakHandler {

    private static final int CAPACITY = Settings.get(BaseKeys.PIT_KILN).getOrThrow("capacity", Integer.class);

    public static final class Item extends PylonItem {
        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(PylonArgument.of("capacity", CAPACITY));
        }
    }

    private static final NamespacedKey CONTENTS_KEY = baseKey("contents");
    private static final PersistentDataType<?, Map<ItemStack, Integer>> CONTENTS_TYPE =
            PylonSerializers.MAP.mapTypeFrom(PylonSerializers.ITEM_STACK, PylonSerializers.INTEGER);

    private final Map<ItemStack, Integer> contents;

    @SuppressWarnings("unused")
    public PitKiln(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        contents = new HashMap<>();
    }

    @SuppressWarnings("unused")
    public PitKiln(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        contents = pdc.get(CONTENTS_KEY, CONTENTS_TYPE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(CONTENTS_KEY, CONTENTS_TYPE, contents);
    }

    @Override
    public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
        drops.clear(); // Don't drop the block itself
        for (Map.Entry<ItemStack, Integer> entry : contents.entrySet()) {
            ItemStack item = entry.getKey();
            item.setAmount(entry.getValue());
            drops.add(item);
        }
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        player.swingHand(EquipmentSlot.HAND);

        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        int currentAmount = 0;
        for (int amount : contents.values()) {
            currentAmount += amount;
        }
        int taken = Math.min(CAPACITY - currentAmount, item.getAmount());
        if (taken <= 0) return;

        contents.merge(item.asOne(), taken, Integer::sum);
        item.subtract(taken);
    }

    private static final List<Vector3i> COAL_POSITIONS = List.of(
            new Vector3i(-1, 0, -1),
            new Vector3i(0, 0, -1),
            new Vector3i(1, 0, -1),
            new Vector3i(-1, 0, 0),
            new Vector3i(1, 0, 0),
            new Vector3i(-1, 0, 1),
            new Vector3i(0, 0, 1),
            new Vector3i(1, 0, 1)
    );

    private static final List<Vector3i> PODZOL_POSITIONS = List.of(
            new Vector3i(-1, 1, -1),
            new Vector3i(0, 1, -1),
            new Vector3i(1, 1, -1),
            new Vector3i(-1, 1, 0),
            new Vector3i(0, 1, 0),
            new Vector3i(1, 1, 0),
            new Vector3i(-1, 1, 1),
            new Vector3i(0, 1, 1),
            new Vector3i(1, 1, 1)
    );

    @Override
    public @NotNull Map<Vector3i, MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();
        for (Vector3i coalPosition : COAL_POSITIONS) {
            components.put(coalPosition, new VanillaMultiblockComponent(Material.COAL_BLOCK));
        }
        for (Vector3i podzolPosition : PODZOL_POSITIONS) {
            components.put(podzolPosition, new VanillaMultiblockComponent(Material.PODZOL));
        }
        components.put(new Vector3i(-1, 0, -2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(0, 0, -2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(1, 0, -2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-1, 0, 2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(0, 0, 2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(1, 0, 2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-2, 0, -1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-2, 0, 0), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-2, 0, 1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(2, 0, -1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(2, 0, 0), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(2, 0, 1), new VanillaMultiblockComponent(Material.COARSE_DIRT));

        components.put(new Vector3i(-1, -1, -1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(0, -1, -1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(1, -1, -1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-1, -1, 0), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(1, -1, 0), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-1, -1, 1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(0, -1, 1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(1, -1, 1), new VanillaMultiblockComponent(Material.COARSE_DIRT));

        components.put(new Vector3i(0, -1, 0), new VanillaMultiblockComponent(Material.FIRE));
        return components;
    }
}
