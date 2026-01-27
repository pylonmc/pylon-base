package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.rebar.block.PylonBlock;
import io.github.pylonmc.rebar.block.base.*;
import io.github.pylonmc.rebar.block.context.BlockBreakContext;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.datatypes.PylonSerializers;
import io.github.pylonmc.rebar.i18n.PylonArgument;
import io.github.pylonmc.rebar.item.PylonItem;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import io.github.pylonmc.rebar.util.MachineUpdateReason;
import io.github.pylonmc.rebar.util.gui.GuiItems;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.ReferencingInventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.controlitem.ControlItem;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class VacuumHopper extends PylonBlock implements
        PylonTickingBlock,
        PylonGuiBlock,
        PylonVirtualInventoryBlock,
        PylonLogisticBlock {

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

    @Getter
    private final @NotNull Inventory hopperInventory;

    @Getter
    private final @NotNull VirtualInventory filterInventory = new VirtualInventory(9);

    public final int radius = getSettings().getOrThrow("radius-blocks", ConfigAdapter.INT);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    public final ItemStackBuilder filterGuiStack = ItemStackBuilder.gui(Material.PINK_STAINED_GLASS_PANE, getKey() + "filter")
            .name(Component.translatable("pylon.pylonbase.gui.filter"));

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

    {
        var hopper = (org.bukkit.block.Hopper) getBlock().getState(false);
        this.hopperInventory = ReferencingInventory.fromStorageContents(hopper.getInventory());
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(WHITELIST_KEY, PylonSerializers.BOOLEAN, whitelist);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull VirtualInventory> getVirtualInventories() {
        return Map.of("filter", filterInventory);
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        for (ItemStack item : hopperInventory.getItems()) {
            if (item != null) {
                drops.add(item);
            }
        }
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # x x x x x # #",
                        "# # # # # # # # #",
                        "w # f y y y y y f",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('x', hopperInventory)
                .addIngredient('f', filterGuiStack)
                .addIngredient('y', filterInventory)
                .addIngredient('w', new WhitelistItem())
                .build();
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

            ItemStack stack = item.getItemStack();
            if (!isFiltered(stack)) {
                continue;
            }

            int surplus = hopperInventory.addItem(new MachineUpdateReason(), stack);
            if (surplus == stack.getAmount()) {
                // Could not add any items in the stack
                continue;
            }

            if (surplus == 0) {
                // Stack fully added
                new ParticleBuilder(Particle.WITCH)
                        .location(item.getLocation())
                        .spawn();
                item.remove();
                break;
            }

            // Stack partially added
            stack.setAmount(surplus);
            new ParticleBuilder(Particle.WITCH)
                    .location(item.getLocation())
                    .spawn();
        }
    }

    public boolean isFiltered(@NotNull ItemStack item) {
        for (ItemStack filterStack : filterInventory.getItems()) {
            if (item.isSimilar(filterStack)) {
                return whitelist;
            }
        }
        return !whitelist;
    }

    public final class WhitelistItem extends ControlItem<Gui> {

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            whitelist = !whitelist;
            notifyWindows();
        }

        @Override
        public @NotNull ItemProvider getItemProvider(Gui gui) {
            return ItemStackBuilder.of(whitelist ? Material.WHITE_CONCRETE : Material.BLACK_CONCRETE)
                    .name(Component.translatable("pylon.pylonbase.gui.vacuum_hopper." + (whitelist ? "whitelist" : "blacklist")));
        }
    }
}
