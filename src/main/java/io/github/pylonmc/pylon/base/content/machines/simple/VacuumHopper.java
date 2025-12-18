package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
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
import xyz.xenondevs.invui.inventory.event.UpdateReason;
import xyz.xenondevs.invui.window.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class VacuumHopper extends PylonBlock implements PylonTickingBlock, PylonInteractBlock, PylonBreakHandler {

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

    public final int radius = getSettings().getOrThrow("radius-blocks", ConfigAdapter.INT);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    @SuppressWarnings("unused")
    public VacuumHopper(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        this.inventory = new VirtualInventory(5);
        setTickInterval(tickInterval);
    }

    @SuppressWarnings("unused")
    public VacuumHopper(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        this.inventory = VirtualInventory.deserialize(pdc.get(INVENTORY_KEY, PylonSerializers.BYTE_ARRAY));
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(INVENTORY_KEY, PylonSerializers.BYTE_ARRAY, inventory.serialize());
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
                .setGui(createGui())
                .setTitle(new AdventureComponentWrapper(getNameTranslationKey()))
                .setViewer(event.getPlayer())
                .build()
                .open();
    }

    private @NotNull Gui.Builder<?, ?> createGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # x x x x x # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('x', this.inventory);
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
}
