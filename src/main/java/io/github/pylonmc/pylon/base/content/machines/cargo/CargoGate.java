package io.github.pylonmc.pylon.base.content.machines.cargo;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonCargoBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonVirtualInventoryBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticGroupType;
import io.github.pylonmc.pylon.core.logistics.slot.VirtualInventoryLogisticSlot;
import io.github.pylonmc.pylon.core.util.MachineUpdateReason;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class CargoGate extends PylonBlock implements
        PylonDirectionalBlock,
        PylonGuiBlock,
        PylonVirtualInventoryBlock,
        PylonCargoBlock {

    public static final NamespacedKey THRESHOLD_KEY = baseKey("threshold");
    public static final NamespacedKey ITEMS_REMAINING_KEY = baseKey("items_remaining");

    public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

    public int threshold = 1;
    public int itemsRemaining = 1;

    private final VirtualInventory outputInventory = new VirtualInventory(1);
    private final VirtualInventory leftInventory = new VirtualInventory(1);
    private final VirtualInventory rightInventory = new VirtualInventory(1);

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder outputStack = ItemStackBuilder.of(Material.RED_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":output");
    public final ItemStackBuilder inputLeftStack = ItemStackBuilder.of(Material.YELLOW_CONCRETE)
            .addCustomModelDataString(getKey() + ":input_left");
    public final ItemStackBuilder inputRightStack = ItemStackBuilder.of(Material.LIGHT_BLUE_CONCRETE)
            .addCustomModelDataString(getKey() + ":input_right");

    public final ItemStackBuilder leftStack = ItemStackBuilder.gui(Material.YELLOW_STAINED_GLASS_PANE, getKey() + "left")
            .name(Component.translatable("pylon.pylonbase.gui.left"));
    public final ItemStackBuilder rightStack = ItemStackBuilder.gui(Material.LIGHT_BLUE_STAINED_GLASS_PANE, getKey() + "right")
            .name(Component.translatable("pylon.pylonbase.gui.right"));
    public final ItemStackBuilder thresholdButtonStack = ItemStackBuilder.gui(Material.WHITE_CONCRETE, getKey() + "threshold_button")
            .lore(Component.translatable("pylon.pylonbase.gui.threshold_button.lore"));

    public class ThresholdButton extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            return thresholdButtonStack
                .name((Component.translatable("pylon.pylonbase.gui.threshold_button.name").arguments(
                        PylonArgument.of("threshold", threshold)
                )));
        }

        @Override
        public void handleClick(
                @NotNull ClickType clickType,
                @NotNull Player player,
                @NotNull InventoryClickEvent event
        ) {
            if (clickType.isLeftClick()) {
                threshold += 1;
            } else {
                threshold = Math.max(1, threshold - 1);
            }
            itemsRemaining = threshold;
            notifyWindows();
        }
    }

    public static class Item extends PylonItem {

        public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of(
                            "transfer-rate",
                            UnitFormat.ITEMS_PER_SECOND.format(PylonCargoBlock.cargoItemsTransferredPerSecond(transferRate))
                    )
            );
        }
    }

    @SuppressWarnings("unused")
    public CargoGate(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());

        addCargoLogisticGroup(getFacing().getOppositeFace(), "output");
        addCargoLogisticGroup(PylonUtils.rotateFaceToReference(getFacing(), BlockFace.EAST), "left");
        addCargoLogisticGroup(PylonUtils.rotateFaceToReference(getFacing(), BlockFace.WEST), "right");
        setCargoTransferRate(transferRate);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .scale(0.7)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("repeater", new BlockDisplayBuilder()
                .blockData(Material.REPEATER.createBlockData("[powered=false]"))
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0.6, 0)
                        .scale(0.5)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("input", new ItemDisplayBuilder()
                .itemStack(outputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, -0.2)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("output_left", new ItemDisplayBuilder()
                .itemStack(inputLeftStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(-0.2, 0, 0)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("output_right", new ItemDisplayBuilder()
                .itemStack(inputRightStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0.2, 0, 0)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public CargoGate(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        threshold = pdc.get(THRESHOLD_KEY, PylonSerializers.INTEGER);
        itemsRemaining = pdc.get(ITEMS_REMAINING_KEY, PylonSerializers.INTEGER);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(THRESHOLD_KEY, PylonSerializers.INTEGER, threshold);
        pdc.set(ITEMS_REMAINING_KEY, PylonSerializers.INTEGER, itemsRemaining);
    }

    @Override
    public @NotNull Gui getGui() {
        return Gui.normal()
                .setStructure(
                        "# L # # O # # R #",
                        "# l # # o # # r #",
                        "# L # # O # # R #",
                        "# # # # # # # # #",
                        "# # # # t # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('L', leftStack)
                .addIngredient('l', leftInventory)
                .addIngredient('O', GuiItems.output())
                .addIngredient('o', outputInventory)
                .addIngredient('R', rightStack)
                .addIngredient('r', rightInventory)
                .addIngredient('t', new ThresholdButton())
                .build();
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("output", LogisticGroupType.OUTPUT, new VirtualInventoryLogisticSlot(outputInventory, 0));
        createLogisticGroup("left", LogisticGroupType.INPUT, new VirtualInventoryLogisticSlot(leftInventory, 0));
        createLogisticGroup("right", LogisticGroupType.INPUT, new VirtualInventoryLogisticSlot(rightInventory, 0));
        outputInventory.setPostUpdateHandler(event -> {
            if (!(event.getUpdateReason() instanceof MachineUpdateReason)) {
                doSplit();
            }
        });
        leftInventory.setPostUpdateHandler(event -> {
            if (!(event.getUpdateReason() instanceof MachineUpdateReason)) {
                doSplit();
            }
        });
        rightInventory.setPostUpdateHandler(event -> {
            if (!(event.getUpdateReason() instanceof MachineUpdateReason)) {
                doSplit();
            }
        });
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("threshold", threshold),
                PylonArgument.of("side", itemsRemaining == 0
                                ? Component.translatable("pylon.pylonbase.waila.cargo_splitter.left")
                                : Component.translatable("pylon.pylonbase.waila.cargo_splitter.right")
                )
        ));
    }

    @Override
    public @NotNull Map<String, VirtualInventory> getVirtualInventories() {
        return Map.of(
                "output", outputInventory,
                "left", leftInventory,
                "right", rightInventory
        );
    }

    private void doSplit() {
        getHeldEntityOrThrow(BlockDisplay.class, "repeater")
                .setBlock(Material.REPEATER.createBlockData("[powered=" + (itemsRemaining == 0 ? "true" : "false") + "]"));

        VirtualInventory inputInventory = itemsRemaining == 0 ? rightInventory : leftInventory;
        ItemStack input = inputInventory.getItem(0);
        if (input == null) {
            return;
        }

        ItemStack output = outputInventory.getItem(0);
        if (output == null || (output.isSimilar(input) && output.getAmount() < output.getMaxStackSize())) {
            if (output == null) {
                outputInventory.setItem(new MachineUpdateReason(), 0, input.asOne());
            } else {
                outputInventory.setItem(new MachineUpdateReason(), 0, output.add());
            }
            inputInventory.setItem(new MachineUpdateReason(), 0, input.subtract());
            itemsRemaining = itemsRemaining == 0
                    ? threshold
                    : itemsRemaining - 1;
            doSplit();
        }
    }
}
