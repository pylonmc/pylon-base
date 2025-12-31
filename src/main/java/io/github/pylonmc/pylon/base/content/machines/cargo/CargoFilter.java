package io.github.pylonmc.pylon.base.content.machines.cargo;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonCargoBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.List;


public class CargoFilter extends PylonBlock
        implements PylonDirectionalBlock, PylonGuiBlock, PylonCargoBlock, PylonEntityHolderBlock {

    public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

    private final VirtualInventory inputInventory = new VirtualInventory(1);
    private final VirtualInventory filteredInventory = new VirtualInventory(1);
    private final VirtualInventory unfilteredInventory = new VirtualInventory(1);
    private final VirtualInventory filterInventory = new VirtualInventory(5);

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder verticalStack = ItemStackBuilder.of(Material.PINK_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":vertical");
    public final ItemStackBuilder inputStack = ItemStackBuilder.of(Material.LIME_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":input");
    public final ItemStackBuilder filteredStack = ItemStackBuilder.of(Material.ORANGE_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":filtered");
    public final ItemStackBuilder unfilteredStack = ItemStackBuilder.of(Material.RED_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":unfiltered");

    public final ItemStackBuilder filteredGuiStack = ItemStackBuilder.gui(Material.ORANGE_STAINED_GLASS_PANE, getKey() + "filtered")
            .name(Component.translatable("pylon.pylonbase.gui.filtered"));
    public final ItemStackBuilder unfilteredGuiStack = ItemStackBuilder.gui(Material.RED_STAINED_GLASS_PANE, getKey() + "unfiltered")
            .name(Component.translatable("pylon.pylonbase.gui.unfiltered"));
    public final ItemStackBuilder filterGuiStack = ItemStackBuilder.gui(Material.PINK_STAINED_GLASS_PANE, getKey() + "filter")
            .name(Component.translatable("pylon.pylonbase.gui.filter"));

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
    public CargoFilter(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());

        addCargoLogisticGroup(getFacing(), "input");
        addCargoLogisticGroup(PylonUtils.rotateFaceToReference(getFacing(), BlockFace.EAST), "filtered");
        addCargoLogisticGroup(PylonUtils.rotateFaceToReference(getFacing(), BlockFace.WEST), "unfiltered");
        setCargoTransferRate(transferRate);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .scale(0.65)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("vertical1", new ItemDisplayBuilder()
                .itemStack(verticalStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(-0.15, 0, 0)
                        .scale(0.15, 0.7, 0.5)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("vertical2", new ItemDisplayBuilder()
                .itemStack(verticalStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0.15, 0, 0)
                        .scale(0.15, 0.7, 0.5)
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

        addEntity("output_filtered", new ItemDisplayBuilder()
                .itemStack(filteredStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(-0.15, 0, 0)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("output_unfiltered", new ItemDisplayBuilder()
                .itemStack(unfilteredStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0.15, 0, 0)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public CargoFilter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# L # # I # # R #",
                        "# l # # i # # r #",
                        "# L # # I # # R #",
                        "# # # # # # # # #",
                        "# F f f f f f F #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('L', filteredGuiStack)
                .addIngredient('l', filteredInventory)
                .addIngredient('I', GuiItems.input())
                .addIngredient('i', inputInventory)
                .addIngredient('R', unfilteredGuiStack)
                .addIngredient('r', unfilteredInventory)
                .addIngredient('f', filterInventory)
                .addIngredient('F', filterGuiStack)
                .build();
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("input", LogisticGroupType.INPUT, new VirtualInventoryLogisticSlot(inputInventory, 0));
        createLogisticGroup("filtered", LogisticGroupType.OUTPUT, new VirtualInventoryLogisticSlot(filteredInventory, 0));
        createLogisticGroup("unfiltered", LogisticGroupType.OUTPUT, new VirtualInventoryLogisticSlot(unfilteredInventory, 0));
        inputInventory.setPostUpdateHandler(event -> {
            if (!(event.getUpdateReason() instanceof MachineUpdateReason)) {
                doSplit();
            }
        });
        filteredInventory.setPostUpdateHandler(event -> {
            if (!(event.getUpdateReason() instanceof MachineUpdateReason)) {
                doSplit();
            }
        });
        unfilteredInventory.setPostUpdateHandler(event -> {
            if (!(event.getUpdateReason() instanceof MachineUpdateReason)) {
                doSplit();
            }
        });
        filterInventory.setPostUpdateHandler(event -> {
            if (!(event.getUpdateReason() instanceof MachineUpdateReason)) {
                doSplit();
            }
        });
    }

    private void doSplit() {
        ItemStack input = inputInventory.getItem(0);
        if (input == null) {
            return;
        }

        boolean matchesFilter = false;
        for (ItemStack filterStack : filterInventory.getItems()) {
            if (input.isSimilar(filterStack)) {
                matchesFilter = true;
                break;
            }
        }

        if (matchesFilter) {
            ItemStack filteredStack = filteredInventory.getItem(0);
            if (filteredStack == null
                    || (filteredStack.isSimilar(input) && filteredStack.getAmount() < filteredStack.getMaxStackSize())
            ) {
                if (filteredStack == null) {
                    filteredInventory.setItem(new MachineUpdateReason(), 0, input);
                    inputInventory.setItem(new MachineUpdateReason(), 0, null);
                } else {
                    int newAmount = Math.min(filteredStack.getMaxStackSize(), filteredStack.getAmount() + input.getAmount());
                    int toSubtract = newAmount - filteredStack.getAmount();
                    filteredInventory.setItem(new MachineUpdateReason(), 0, filteredStack.asQuantity(newAmount));
                    inputInventory.setItem(new MachineUpdateReason(), 0, input.subtract(toSubtract));
                }
            }
        } else {
            ItemStack unfilteredStack = unfilteredInventory.getItem(0);
            if (unfilteredStack == null
                    || (unfilteredStack.isSimilar(input) && unfilteredStack.getAmount() < unfilteredStack.getMaxStackSize())
            ) {
                if (unfilteredStack == null) {
                    unfilteredInventory.setItem(new MachineUpdateReason(), 0, input);
                    inputInventory.setItem(new MachineUpdateReason(), 0, null);
                } else {
                    int newAmount = Math.min(unfilteredStack.getMaxStackSize(), unfilteredStack.getAmount() + input.getAmount());
                    int toSubtract = newAmount - unfilteredStack.getAmount();
                    unfilteredInventory.setItem(new MachineUpdateReason(), 0, unfilteredStack.asQuantity(newAmount));
                    inputInventory.setItem(new MachineUpdateReason(), 0, input.subtract(toSubtract));
                }
            }
        }
    }
}
