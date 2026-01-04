package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.MachineUpdateReason;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.item.impl.controlitem.ControlItem;
import xyz.xenondevs.invui.window.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class VacuumHopper extends PylonBlock implements PylonTickingBlock, PylonGuiBlock, PylonHopper, PylonNoVanillaContainerBlock, PylonBreakHandler {
    public static class Item extends PylonItem {
        public final int radius = getSettings().getOrThrow("radius-blocks", ConfigAdapter.INT);
        public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("radius", UnitFormat.BLOCKS.format(radius)),
                    PylonArgument.of("tick_interval", UnitFormat.SECONDS.format(tickInterval / 20.0))
            );
        }
    }

    public static final NamespacedKey WHITELIST_KEY = baseKey("whitelist");
    // if whitelist is true behaves like a whitelist, otherwise like a blacklist
    public boolean whitelist;

    public final int radius = getSettings().getOrThrow("radius-blocks", ConfigAdapter.INT);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    @SuppressWarnings("unused")
    public VacuumHopper(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        this.whitelist = false;
        setTickInterval(tickInterval);
    }

    @SuppressWarnings("unused")
    public VacuumHopper(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        this.whitelist = pdc.get(WHITELIST_KEY, PylonSerializers.BOOLEAN);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(WHITELIST_KEY, PylonSerializers.BOOLEAN, whitelist);
    }

    private void preUpdate(@NotNull ItemPreUpdateEvent event) {
        if (event.getUpdateReason() == UpdateReason.SUPPRESSED) return;
        event.setCancelled(true);

        ItemStack newItem = event.getNewItem();
        int slot = event.getSlot();
        if (newItem == null) {
            event.getInventory().setItem(UpdateReason.SUPPRESSED, slot, null);
        } else {
            event.getInventory().setItem(UpdateReason.SUPPRESSED, slot, newItem.asOne());
        }
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Inventory> createInventoryMapping() {
        var itemsToCheck = new VirtualInventory(9);
        itemsToCheck.setPreUpdateHandler(this::preUpdate);
        return Map.of(
            "inventory", new VirtualInventory(5),
            "items_to_check", itemsToCheck
        );
    }

    public @NotNull Inventory getItemsToCheck() {
        return getInventoryOrThrow("items_to_check");
    }

    public @NotNull Inventory getHopperInventory() {
        return getInventoryOrThrow("inventory");
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        for (ItemStack item : getHopperInventory().getItems()) {
            if (item != null) {
                drops.add(item);
            }
        }
    }

    @Override
    public void onHopperPickUpItem(@NotNull InventoryPickupItemEvent event) {
        org.bukkit.entity.@NotNull Item item = event.getItem();
        ItemStack stack = item.getItemStack();
        Outcome outcome = this.addItem(stack);
        event.setCancelled(true);
        if (outcome == Outcome.FULLY_ADDED) {
            new ParticleBuilder(Particle.WITCH)
                    .location(item.getLocation())
                    .spawn();
            item.remove();
        } else if (outcome == Outcome.PARTIAL_ADDED) {
            new ParticleBuilder(Particle.WITCH)
                    .location(item.getLocation())
                    .spawn();
        }
    }


    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # x x x x x # #",
                        "# # # # $ # # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('x', getHopperInventory())
                .addIngredient('$', new SimpleItem(lang -> {
                    ItemStack item = new ItemStack(Material.REDSTONE);
                    item.setData(DataComponentTypes.ITEM_NAME, translation("settings"));
                    return item;
                }, click -> {
                    Window.single()
                            .setGui(createSettingsGui())
                            .setTitle(new AdventureComponentWrapper(translation("settings")))
                            .setViewer(click.getPlayer())
                            .build()
                            .open();
                }))
                .build();
    }

    private @NotNull Gui.Builder.Normal createSettingsGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # w # # # #",
                        "x x x x x x x x x",
                        "# # # # $ # # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('x', getItemsToCheck())
                .addIngredient('$', new SimpleItem(lang -> {
                    ItemStack item = new ItemStack(Material.CHEST);
                    item.setData(DataComponentTypes.ITEM_NAME, translation("inventory"));
                    return item;
                }, click -> Window.single()
                        .setGui(createGui())
                        .setTitle(new AdventureComponentWrapper(getNameTranslationKey()))
                        .setViewer(click.getPlayer())
                        .build()
                        .open())
                )
                .addIngredient('w', new ControlItem<>() {
                    @Override
                    public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
                        whitelist = !whitelist;
                        event.setCurrentItem(getItem());
                    }

                    @Override
                    public ItemProvider getItemProvider(Gui gui) {
                        return (ignored) -> getItem();
                    }

                    private ItemStack getItem() {
                        ItemStack item = new ItemStack(whitelist ? Material.WHITE_WOOL : Material.BLACK_WOOL);
                        item.setData(
                                DataComponentTypes.ITEM_NAME,
                                whitelist ? translation("whitelist")
                                        : translation("blacklist")
                        );
                        return item;
                    }
                });
    }

    @Override
    public void onItemMoveTo(@NotNull InventoryMoveItemEvent event) {
        ItemStack old = event.getItem();
        Outcome outcome = this.addItem(old);
        if (outcome == Outcome.FULLY_ADDED) {
            event.setItem(ItemStack.empty());
        }
    }

    @Override
    public void tick() {
        Block block = getBlock();
        Hopper hopper = (Hopper) block.getBlockData();
        if (!hopper.isEnabled()) {
            return; // don't vacuum if powered
        }

        // loops item entities to add
        for (Entity entity : block.getLocation().toCenterLocation().getNearbyEntities(radius + 0.5, radius + 0.5, radius + 0.5)) {
            if (!(entity instanceof org.bukkit.entity.Item item)) {
                continue;
            }

            Outcome outcome = addItem(item.getItemStack());
            if (outcome == Outcome.FULLY_ADDED) {
                new ParticleBuilder(Particle.WITCH)
                        .location(item.getLocation())
                        .spawn();
                item.remove();
                break;
            } else if (outcome == Outcome.PARTIAL_ADDED) {
                new ParticleBuilder(Particle.WITCH)
                        .location(item.getLocation())
                        .spawn();
            }
        }

        BlockFace facing = hopper.getFacing();
        Block other = block.getRelative(facing);
        BlockState data = other.getState();

        var inventory = getHopperInventory();
        if (data instanceof BlockInventoryHolder holder) {
            for (int i = 0; i < 5; i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null) continue;

                ItemStack singleItem = item.asOne();
                HashMap<Integer, ItemStack> excess = holder.getInventory().addItem(singleItem);
                if (excess.isEmpty()) {
                    inventory.setItem(new MachineUpdateReason() , i, item.subtract());
                }
            }
        }
    }

    public enum Outcome {
        INVALID,
        FULLY_ADDED,
        FAILED_FULL,
        PARTIAL_ADDED
    }

    public @NotNull Outcome addItem(@NotNull ItemStack item) {
        if (!isValid(item)) return Outcome.INVALID;

        int surplus = getHopperInventory().addItem(new MachineUpdateReason(), item);
        if (surplus == 0) {
            return Outcome.FULLY_ADDED;
        }

        if (surplus == item.getAmount()) {
            return Outcome.FAILED_FULL;
        }

        item.setAmount(surplus);
        return Outcome.PARTIAL_ADDED;
    }

    public boolean isValid(@NotNull ItemStack item) {
        ItemStack[] checkList = this.getItemsToCheck().getItems();
        for (ItemStack checkStack : checkList) {
            if (item.isSimilar(checkStack)) return whitelist;
        }

        return !whitelist;
    }

    private TranslatableComponent translation(String key) {
        return Component.translatable("pylon.pylonbase.gui.vacuum_hopper." + key);
    }
}
