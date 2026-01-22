package io.github.pylonmc.pylon.base.content.machines.cargo;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonCargoBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticGroupType;
import io.github.pylonmc.pylon.core.util.MachineUpdateReason;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.AbstractItem;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class CargoFluidAccumulator extends PylonBlock implements
        PylonDirectionalBlock,
        PylonGuiBlock,
        PylonCargoBlock,
        PylonFluidTank {

    public static final NamespacedKey ITEM_THRESHOLD_KEY = baseKey("item_threshold");
    public static final NamespacedKey FLUID_THRESHOLD_KEY = baseKey("fluid_threshold");
    public static final NamespacedKey ALLOW_FLUID_INPUTS_KEY = baseKey("allow_fluid_inputs");

    private final VirtualInventory inputInventory = new VirtualInventory(5);
    private final VirtualInventory outputInventory = new VirtualInventory(5);

    public final int fluidBuffer = getSettings().getOrThrow("fluid-buffer", ConfigAdapter.INT);
    public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

    public int itemThreshold;
    public int fluidThreshold;
    public boolean allowFluidInputs;

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder sideStack = ItemStackBuilder.of(Material.NOTE_BLOCK)
            .addCustomModelDataString(getKey() + ":side");
    public final ItemStackBuilder inputStack = ItemStackBuilder.of(Material.LIME_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":input");
    public final ItemStackBuilder outputStack = ItemStackBuilder.of(Material.RED_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":output");
    public final ItemStackBuilder itemThresholdButtonStack = ItemStackBuilder.gui(Material.WHITE_CONCRETE, getKey() + "item_threshold_button")
            .lore(Component.translatable("pylon.pylonbase.gui.item_threshold_button.lore"));
    public final ItemStackBuilder fluidThresholdButtonStack = ItemStackBuilder.gui(Material.WHITE_CONCRETE, getKey() + "fluid_threshold_button")
            .lore(Component.translatable("pylon.pylonbase.gui.fluid_threshold_button.lore"));

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonGuiBlock.super.onBreak(drops, context);
        PylonFluidTank.super.onBreak(drops, context);
    }

    public static class Item extends PylonItem {

        public final int fluidBuffer = getSettings().getOrThrow("fluid-buffer", ConfigAdapter.INT);
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
                    ),
                    PylonArgument.of(
                            "fluid-buffer",
                            UnitFormat.MILLIBUCKETS_PER_SECOND.format(fluidBuffer)
                    )
            );
        }
    }

    @SuppressWarnings("unused")
    public CargoFluidAccumulator(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());

        addCargoLogisticGroup(getFacing(), "input");
        addCargoLogisticGroup(getFacing().getOppositeFace(), "output");
        setCargoTransferRate(transferRate);

        createFluidPoint(FluidPointType.INPUT, BlockFace.EAST, context, false, 0.35F);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.WEST, context, false, 0.35F);
        setCapacity(fluidBuffer);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .scale(0.7)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("vertical", new ItemDisplayBuilder()
                .itemStack(sideStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .scale(0.55, 0.75, 0.55)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("input", new ItemDisplayBuilder()
                .itemStack(inputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, 0.2)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("output", new ItemDisplayBuilder()
                .itemStack(outputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, -0.2)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );

        itemThreshold = 1;
        fluidThreshold = 10;
        allowFluidInputs = true;
    }

    @SuppressWarnings("unused")
    public CargoFluidAccumulator(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        itemThreshold = pdc.get(ITEM_THRESHOLD_KEY, PylonSerializers.INTEGER);
        fluidThreshold = pdc.get(FLUID_THRESHOLD_KEY, PylonSerializers.INTEGER);
        allowFluidInputs = pdc.get(ALLOW_FLUID_INPUTS_KEY, PylonSerializers.BOOLEAN);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(ITEM_THRESHOLD_KEY, PylonSerializers.INTEGER, itemThreshold);
        pdc.set(FLUID_THRESHOLD_KEY, PylonSerializers.INTEGER, fluidThreshold);
        pdc.set(ALLOW_FLUID_INPUTS_KEY, PylonSerializers.BOOLEAN, allowFluidInputs);
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid) {
        return Math.max(
                0,
                Math.min(
                        fluidThreshold - getFluidAmount(),
                        PylonFluidTank.super.fluidAmountRequested(fluid)
                )
        );
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids() {
        return allowFluidInputs ? Map.of() : PylonFluidTank.super.getSuppliedFluids();
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {
        PylonFluidTank.super.onFluidRemoved(fluid, amount);
        doTransfer();
    }

    @Override
    public void onFluidAdded(@NotNull PylonFluid fluid, double amount) {
        PylonFluidTank.super.onFluidAdded(fluid, amount);
        doTransfer();
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return allowFluidInputs;
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.builder()
                .setStructure(
                        "# I i i i i i I #",
                        "# # # # # # # # #",
                        "# O o o o o o O #",
                        "# # # # # # # # #",
                        "# # # t # T # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('i', inputInventory)
                .addIngredient('I', GuiItems.input())
                .addIngredient('o', outputInventory)
                .addIngredient('O', GuiItems.output())
                .addIngredient('t', new ItemThresholdButton())
                .addIngredient('T', new FluidThresholdButton())
                .build();
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("input", LogisticGroupType.INPUT, inputInventory);
        createLogisticGroup("output", LogisticGroupType.OUTPUT, outputInventory);
        inputInventory.addPostUpdateHandler(event -> {
            if (!(event.getUpdateReason() instanceof MachineUpdateReason)) {
                doTransfer();
            }
        });
        outputInventory.addPostUpdateHandler(event -> {
            if (!(event.getUpdateReason() instanceof MachineUpdateReason)) {
                doTransfer();
            }
        });
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("item-threshold", UnitFormat.ITEMS.format(itemThreshold)),
                PylonArgument.of("fluid-threshold", UnitFormat.MILLIBUCKETS.format(fluidThreshold)),
                PylonArgument.of("bars", BaseUtils.createFluidAmountBar(
                        getFluidAmount(),
                        getFluidCapacity(),
                        20,
                        TextColor.color(200, 255, 255)
                ))
        ));
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Inventory> createInventoryMapping() {
        return Map.of("input", inputInventory, "output", outputInventory);
    }

    private void doTransfer() {
        int inputTotal = 0;
        for (ItemStack stack : inputInventory.getItems()) {
            if (stack != null) {
                inputTotal += stack.getAmount();
            }
        }

        int outputTotal = 0;
        for (ItemStack stack : outputInventory.getItems()) {
            if (stack != null) {
                outputTotal += stack.getAmount();
            }
        }

        if (inputTotal >= itemThreshold) {
            getLogisticGroupOrThrow("input").setFilter(stack -> false);
        }

        if (outputTotal == 0 && getFluidAmount() < 1.0e-3) {
            getLogisticGroupOrThrow("input").setFilter(null);
            allowFluidInputs = true;
        }

        if (inputTotal >= itemThreshold && getFluidAmount() >= (fluidThreshold - 1.0e-6)) {
            List<ItemStack> stacks = Arrays.stream(inputInventory.getItems()).toList();
            Preconditions.checkState(outputInventory.canHold(stacks));
            for (ItemStack stack : stacks) {
                outputInventory.addItem(new MachineUpdateReason(), stack);
            }
            for (int slot = 0; slot < inputInventory.getItems().length; slot++) {
                inputInventory.setItem(new MachineUpdateReason(), slot, null);
            }
            allowFluidInputs = false;
        }
    }

    public class ItemThresholdButton extends AbstractItem {

        @Override
        public @NonNull ItemProvider getItemProvider(@NotNull Player viewer) {
            return itemThresholdButtonStack
                .name((Component.translatable("pylon.pylonbase.gui.item_threshold_button.name").arguments(
                        PylonArgument.of("threshold", itemThreshold)
                )));
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull Click click) {
            if (clickType.isLeftClick()) {
                itemThreshold += 1;
            } else {
                itemThreshold = Math.max(1, itemThreshold - 1);
            }
            notifyWindows();
            doTransfer();
        }
    }

    public class FluidThresholdButton extends AbstractItem {

        @Override
        public @NonNull ItemProvider getItemProvider(@NotNull Player viewer) {
            return fluidThresholdButtonStack
                .name((Component.translatable("pylon.pylonbase.gui.fluid_threshold_button.name").arguments(
                        PylonArgument.of("threshold", fluidThreshold)
                )));
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull Click click) {
            int amount = clickType.isShiftClick() ? 100 : 10;
            if (clickType.isLeftClick()) {
                fluidThreshold = Math.min(fluidBuffer, fluidThreshold + amount);
            } else {
                fluidThreshold = Math.min(fluidBuffer, Math.max(10, fluidThreshold - amount));
            }
            notifyWindows();
            doTransfer();
        }
    }
}
