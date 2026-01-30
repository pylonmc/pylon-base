package io.github.pylonmc.pylon.content.machines.cargo;

import io.github.pylonmc.rebar.block.RebarBlock;
import io.github.pylonmc.rebar.block.base.RebarCargoBlock;
import io.github.pylonmc.rebar.block.base.RebarDirectionalBlock;
import io.github.pylonmc.rebar.block.base.RebarGuiBlock;
import io.github.pylonmc.rebar.block.base.RebarVirtualInventoryBlock;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.datatypes.RebarSerializers;
import io.github.pylonmc.rebar.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.rebar.entity.display.transform.TransformBuilder;
import io.github.pylonmc.rebar.i18n.RebarArgument;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import io.github.pylonmc.rebar.logistics.LogisticGroupType;
import io.github.pylonmc.rebar.logistics.slot.VirtualInventoryLogisticSlot;
import io.github.pylonmc.rebar.util.gui.GuiItems;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import io.github.pylonmc.rebar.waila.WailaDisplay;
import kotlin.Pair;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.util.PylonUtils.pylonKey;


public class CargoValve extends RebarBlock implements
        RebarDirectionalBlock,
        RebarGuiBlock,
        RebarVirtualInventoryBlock,
        RebarCargoBlock {

    public static final NamespacedKey ENABLED_KEY = pylonKey("enabled");

    public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

    private final VirtualInventory inventory = new VirtualInventory(1);

    public boolean enabled;

    public final ItemStackBuilder inputStack = ItemStackBuilder.of(Material.LIME_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":input");
    public final ItemStackBuilder outputStack = ItemStackBuilder.of(Material.RED_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":output");
    public final ItemStackBuilder stackOff = ItemStackBuilder.of(Material.CYAN_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":stack_off");
    public final ItemStackBuilder stackOn = ItemStackBuilder.of(Material.WHITE_CONCRETE)
            .addCustomModelDataString(getKey() + ":stack_on");

    public static class Item extends RebarItem {

        public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<RebarArgument> getPlaceholders() {
            return List.of(
                    RebarArgument.of(
                            "transfer-rate",
                            UnitFormat.ITEMS_PER_SECOND.format(RebarCargoBlock.cargoItemsTransferredPerSecond(transferRate))
                    )
            );
        }
    }

    @SuppressWarnings("unused")
    public CargoValve(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());

        addCargoLogisticGroup(getFacing(), "input");
        addCargoLogisticGroup(getFacing().getOppositeFace(), "output");

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(stackOff)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .scale(0.5, 0.5, 0.65)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("output", new ItemDisplayBuilder()
                .itemStack(outputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, -0.15)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("input", new ItemDisplayBuilder()
                .itemStack(inputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, 0.15)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );

        enabled = false;
        setCargoTransferRate(0);
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public CargoValve(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        enabled = pdc.get(ENABLED_KEY, RebarSerializers.BOOLEAN);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(ENABLED_KEY, RebarSerializers.BOOLEAN, enabled);
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!event.getPlayer().isSneaking()) {
            RebarGuiBlock.super.onInteract(event);
            return;
        }

        event.setUseItemInHand(Event.Result.DENY);

        enabled = !enabled;


        if (enabled) {
            setCargoTransferRate(transferRate);
        } else {
            setCargoTransferRate(0);
        }

        getHeldEntityOrThrow(ItemDisplay.class, "main")
                .setItemStack((enabled ? stackOn : stackOff).build());
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.builder()
                .setStructure("# # # # x # # # #")
                .addIngredient('#', GuiItems.background())
                .addIngredient('x', inventory)
                .build();
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("input", LogisticGroupType.INPUT, new VirtualInventoryLogisticSlot(inventory, 0));
        createLogisticGroup("output", LogisticGroupType.OUTPUT, new VirtualInventoryLogisticSlot(inventory, 0));
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                RebarArgument.of("status", Component.translatable(
                        "pylon.message.valve." + (enabled ? "enabled" : "disabled")
                ))
        ));
    }

    @Override
    public @NotNull Map<String, VirtualInventory> getVirtualInventories() {
        return Map.of("inventory", inventory);
    }

    @Override
    public @NotNull Map<String, Pair<String, Integer>> getBlockTextureProperties() {
        var properties = super.getBlockTextureProperties();
        properties.put("enabled", new Pair<>(enabled ? "true" : "false", 2));
        return properties;
    }
}
