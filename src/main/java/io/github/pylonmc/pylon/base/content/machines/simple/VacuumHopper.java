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
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.ItemPreUpdateEvent;
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class VacuumHopper extends PylonBlock implements PylonTickingBlock, PylonNoVanillaContainerBlock, PylonInteractBlock, PylonBreakHandler {

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


    public static final UpdateReason VACUUM = new UpdateReason() {};
    public static final NamespacedKey INVENTORY_KEY = baseKey("vacuum_hopper_inventory");
    public final VirtualInventory inventory; // todo: when assembly table additions is merged replace set & get with proper serializer

    public static final NamespacedKey WHITELIST_KEY = baseKey("whitelist");
    public static final NamespacedKey ITEMS_TO_CHECK_KEY = baseKey("vacuum_hopper_whitelist");

    // if whitelist is true behaves like a whitelist, otherwise like a blacklist
    public boolean whitelist;
    public final VirtualInventory itemsToCheck;

    public final int radius = getSettings().getOrThrow("radius-blocks", ConfigAdapter.INT);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    @SuppressWarnings("unused")
    public VacuumHopper(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        this.inventory = new VirtualInventory(5);
        this.whitelist = true;
        this.itemsToCheck = new VirtualInventory(9);
        this.itemsToCheck.setPreUpdateHandler(this::preUpdate);
        setTickInterval(tickInterval);
    }

    @SuppressWarnings("unused")
    public VacuumHopper(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        this.inventory = VirtualInventory.deserialize(pdc.get(INVENTORY_KEY, PylonSerializers.BYTE_ARRAY));
        this.whitelist = pdc.get(WHITELIST_KEY, PylonSerializers.BOOLEAN);
        this.itemsToCheck = VirtualInventory.deserialize(pdc.get(ITEMS_TO_CHECK_KEY, PylonSerializers.BYTE_ARRAY));
        this.itemsToCheck.setPreUpdateHandler(this::preUpdate);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(INVENTORY_KEY, PylonSerializers.BYTE_ARRAY, inventory.serialize());
        pdc.set(WHITELIST_KEY, PylonSerializers.BOOLEAN, whitelist);
        pdc.set(ITEMS_TO_CHECK_KEY, PylonSerializers.BYTE_ARRAY, itemsToCheck.serialize());
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
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        for (ItemStack item : inventory.getItems()) {
            if (item != null) {
                drops.add(item);
            }
        }
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()
                || event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND
                || event.useInteractedBlock() == Event.Result.DENY) {
            return;
        }

        event.setCancelled(true);

        Window.single()
                .setGui(createInventoryGui())
                .setTitle(new AdventureComponentWrapper(getNameTranslationKey()))
                .setViewer(event.getPlayer())
                .build()
                .open();
    }

    private @NotNull Gui.Builder.Normal createInventoryGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # x x x x x # #",
                        "# # # # $ # # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('x', this.inventory)
                .addIngredient('$', new SimpleItem(lang -> {
                    ItemStack item = new ItemStack(Material.REDSTONE);
                    item.setData(DataComponentTypes.ITEM_NAME, Component.text("Settings").color(NamedTextColor.RED));
                    return item;
                }, click -> {
                    Window.single()
                            .setGui(createSettingsGui())
                            .setTitle("Settings")
                            .setViewer(click.getPlayer())
                            .build()
                            .open();
                }));
    }

    private @NotNull Gui.Builder.Normal createSettingsGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "x x x x x x x x x",
                        "# # # # $ # # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('x', itemsToCheck)
                .addIngredient('$', new SimpleItem(lang -> {
                    ItemStack item = new ItemStack(Material.CHEST);
                    item.setData(DataComponentTypes.ITEM_NAME, Component.text("Inventory").color(NamedTextColor.LIGHT_PURPLE));
                    return item;
                }, click -> {
                    Window.single()
                            .setGui(createInventoryGui())
                            .setTitle(new AdventureComponentWrapper(getNameTranslationKey()))
                            .setViewer(click.getPlayer())
                            .build()
                            .open();
                }));
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!((org.bukkit.block.data.type.Hopper) getBlock().getBlockData()).isEnabled()) {
            return; // don't vacuum if powered
        }

        for (Entity entity : getBlock().getLocation().toCenterLocation().getNearbyEntities(radius + 0.5, radius + 0.5, radius + 0.5)) {
            if (!(entity instanceof org.bukkit.entity.Item item)) {
                continue;
            }

            ItemStack stack = item.getItemStack();
            if (!isValid(stack)) continue;

            int surplus = inventory.addItem(VACUUM, stack);
            if (surplus == 0) {
                new ParticleBuilder(Particle.WITCH)
                        .location(item.getLocation())
                        .spawn();
                item.remove();
                break;
            }

            if (surplus == stack.getAmount()) {
                continue;
            }

            stack.setAmount(surplus);
            new ParticleBuilder(Particle.WITCH)
                    .location(item.getLocation())
                    .spawn();
        }
    }

    public boolean isValid(@NotNull ItemStack item) {
        ItemStack[] checkList = this.itemsToCheck.getItems();
        for (ItemStack checkStack : checkList) {
            if (item.isSimilar(checkStack)) return whitelist;
        }

        return !whitelist;
    }
}
