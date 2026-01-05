package io.github.pylonmc.pylon.base.content.machines.cargo;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.cargo.CargoDuct;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.event.PylonCargoConnectEvent;
import io.github.pylonmc.pylon.core.event.PylonCargoDisconnectEvent;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticGroup;
import io.github.pylonmc.pylon.core.logistics.LogisticSlot;
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
import io.github.pylonmc.pylon.core.util.MachineUpdateReason;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

import java.util.*;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class CargoExtractor extends PylonBlock implements
        PylonDirectionalBlock,
        PylonCargoBlock,
        PylonGuiBlock,
        PylonTickingBlock {

    public static final NamespacedKey TARGET_LOGISTIC_GROUP_KEY = baseKey("target_logistic_group");
    public static final NamespacedKey ITEMS_TO_FILTER_KEY = baseKey("items_to_filter");
    public static final NamespacedKey IS_WHITELIST_KEY = baseKey("is_whitelist");
    public static final List<Material> groupMaterials = List.of(
            Material.LIGHT_BLUE_CONCRETE,
            Material.CYAN_CONCRETE,
            Material.BLUE_CONCRETE,
            Material.PURPLE_CONCRETE,
            Material.MAGENTA_CONCRETE,
            Material.PINK_CONCRETE
    );

    public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder outputStack = ItemStackBuilder.of(Material.RED_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":output");
    public final ItemStackBuilder ductStack = ItemStackBuilder.of(Material.GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":duct");

    public final ItemStackBuilder filterGuiStack = ItemStackBuilder.gui(Material.PINK_STAINED_GLASS_PANE, getKey() + "filter")
            .name(Component.translatable("pylon.pylonbase.gui.filter"));

    private final VirtualInventory outputInventory = new VirtualInventory(1);
    private final VirtualInventory filterInventory = new VirtualInventory(5);

    public @Nullable String targetLogisticGroup = null;
    public Set<ItemStack> itemsToFilter = new HashSet<>();
    public boolean isWhitelist = false;

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

    public class WhitelistToggleItem extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            return ItemStackBuilder.gui(isWhitelist ? Material.WHITE_CONCRETE : Material.BLACK_CONCRETE, "blacklist-whitelist-toggle")
                    .name(Component.translatable("pylon.pylonbase.gui.whitelist-blacklist-toggle."
                            + (isWhitelist ? "whitelist.name" : "blacklist.name")
                    ))
                    .lore(Component.translatable("pylon.pylonbase.gui.whitelist-blacklist-toggle."
                            + (isWhitelist ? "whitelist.lore" : "blacklist.lore")
                    ));
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            isWhitelist = !isWhitelist;
            notifyWindows();
        }
    }

    public class InventoryCycleItem extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            PylonLogisticBlock logisticBlock = getTargetLogisticBlock();
            if (targetLogisticGroup == null || logisticBlock == null) {
                return ItemStackBuilder.of(Material.BARRIER)
                        .name("pylon.pylonbase.gui.no-target-logistic-group");
            }

            List<String> availableGroups = getAvailableLogisticGroups();
            availableGroups.sort(String::compareTo);

            int index = availableGroups.indexOf(targetLogisticGroup);
            Preconditions.checkState(index != -1);
            Material material = groupMaterials.get(index % groupMaterials.size());

            PylonBlock pylonBlock = BlockStorage.get(logisticBlock.getBlock());
            Preconditions.checkState(pylonBlock != null);

            ItemStackBuilder builder = ItemStackBuilder.gui(material, "logistic-group:" + index)
                    .name(Component.translatable("pylon.pylonbase.gui.logistic-group-cycle-item.name")
                            .arguments(PylonArgument.of(
                                    "inventory",
                                    Component.translatable("pylon." + pylonBlock.getKey().getNamespace() + ".inventory." + targetLogisticGroup)
                            ))
                    );
            if (availableGroups.size() > 1) {
                builder.lore(Component.translatable("pylon.pylonbase.gui.logistic-group-cycle-item.lore"));
            }
            return builder;
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            PylonLogisticBlock logisticBlock = getTargetLogisticBlock();
            if (targetLogisticGroup == null || logisticBlock == null) {
                return;
            }

            List<String> availableGroups = getAvailableLogisticGroups();
            availableGroups.sort(String::compareTo);

            int index = availableGroups.indexOf(targetLogisticGroup);
            targetLogisticGroup = availableGroups.get((index + 1) % availableGroups.size());
            notifyWindows();
        }
    }

    @SuppressWarnings("unused")
    public CargoExtractor(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());
        setTickInterval(transferRate * 20);

        addCargoLogisticGroup(getFacing(), "output");
        for (BlockFace face : PylonUtils.perpendicularImmediateFaces(getFacing())) {
            addCargoLogisticGroup(face, "output");
        }
        setCargoTransferRate(transferRate);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, -0.4)
                        .scale(0.65, 0.65, 0.2)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("output", new ItemDisplayBuilder()
                .itemStack(outputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, -0.3)
                        .scale(0.4, 0.4, 0.05)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("duct", new ItemDisplayBuilder()
                .itemStack(ductStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, -0.0625)
                        .scale(0.35, 0.35, 0.475)
                )
                .build(block.getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public CargoExtractor(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        targetLogisticGroup = pdc.get(TARGET_LOGISTIC_GROUP_KEY, PylonSerializers.STRING);
        itemsToFilter = pdc.get(ITEMS_TO_FILTER_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.ITEM_STACK));
        isWhitelist = pdc.get(IS_WHITELIST_KEY, PylonSerializers.BOOLEAN);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, TARGET_LOGISTIC_GROUP_KEY, PylonSerializers.STRING, targetLogisticGroup);
        pdc.set(ITEMS_TO_FILTER_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.ITEM_STACK), itemsToFilter);
        pdc.set(IS_WHITELIST_KEY, PylonSerializers.BOOLEAN, isWhitelist);
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("output", LogisticSlotType.OUTPUT, outputInventory);

        filterInventory.setPostUpdateHandler(event -> {
            itemsToFilter.clear();
            for (ItemStack stack : filterInventory.getItems()) {
                if (stack != null) {
                    itemsToFilter.add(stack.asOne());
                }
            }
        });
    }

    @Override
    public void onDuctConnected(@NotNull PylonCargoConnectEvent event) {
        // Remove all faces that aren't to the connected block - this will make sure only
        // one duct is connected at a time
        for (BlockFace face : getCargoLogisticGroups().keySet()) {
            if (!getBlock().getRelative(face).equals(event.getBlock1().getBlock()) && !getBlock().getRelative(face).equals(event.getBlock2().getBlock())) {
                removeCargoLogisticGroup(face);
            }
        }
    }

    @Override
    public void onDuctDisconnected(@NotNull PylonCargoDisconnectEvent event) {
        // Allow connecting to all faces now that there are zero connections
        List<BlockFace> faces = PylonUtils.perpendicularImmediateFaces(getFacing());
        faces.add(getFacing().getOppositeFace());
        for (BlockFace face : faces) {
            addCargoLogisticGroup(face, "output");
        }
        for (BlockFace face : faces) {
            if (BlockStorage.get(getBlock().getRelative(face)) instanceof CargoDuct duct) {
                duct.updateConnectedFaces();
            }
        }
    }

    public @Nullable PylonLogisticBlock getTargetLogisticBlock() {
        Block block = getBlock().getRelative(getFacing().getOppositeFace());
        PylonLogisticBlock pylonBlock = BlockStorage.getAs(PylonLogisticBlock.class, block);
        return pylonBlock instanceof PylonCargoBlock ? null : pylonBlock;
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # O # # # #",
                        "# b # # o # # i #",
                        "# # # # O # # # #",
                        "# # # # # # # # #",
                        "# F f f f f f F #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('o', outputInventory)
                .addIngredient('O', GuiItems.output())
                .addIngredient('f', filterInventory)
                .addIngredient('F', filterGuiStack)
                .addIngredient('b', new WhitelistToggleItem())
                .addIngredient('i', new InventoryCycleItem())
                .build();
    }

    @Override
    public void tick() {
        PylonLogisticBlock target = getTargetLogisticBlock();
        if (target == null) {
            return;
        }

        if (targetLogisticGroup == null || target.getLogisticGroup(targetLogisticGroup) == null) {
            List<String> availableLogisticGroups = getAvailableLogisticGroups();
            if (availableLogisticGroups.isEmpty()) {
                return;
            }
            targetLogisticGroup = availableLogisticGroups.getFirst();
        }

        LogisticGroup group = target.getLogisticGroup(targetLogisticGroup);
        Preconditions.checkState(group != null);
        ItemStack output = outputInventory.getItem(0);
        if (output != null && output.getAmount() == output.getMaxStackSize()) {
            return;
        }

        for (LogisticSlot slot : group.getSlots()) {
            ItemStack slotStack = slot.getItemStack();
            if (slotStack == null || slot.getAmount() == 0 || (output != null && !output.isSimilar(slotStack))) {
                continue;
            }

            if ((isWhitelist && !itemsToFilter.contains(slotStack.asOne())) || (!isWhitelist && itemsToFilter.contains(slotStack.asOne()))) {
                continue;
            }

            if (output == null) {
                outputInventory.setItem(new MachineUpdateReason(), 0, slotStack.asOne());
            } else {
                outputInventory.setItem(new MachineUpdateReason(), 0, output.add());
            }
            slot.set(slotStack, slot.getAmount() - 1);
            return;
        }
    }

    private @NotNull List<String> getAvailableLogisticGroups() {
        PylonLogisticBlock target = getTargetLogisticBlock();
        if (target == null) {
            return List.of();
        }

        List<String> availableLogisticGroups = new ArrayList<>();
        for (Map.Entry<String, LogisticGroup> entry : target.getLogisticGroups().entrySet()) {
            if (entry.getValue().getSlotType() == LogisticSlotType.BOTH
                    || entry.getValue().getSlotType() == LogisticSlotType.OUTPUT
            ) {
                availableLogisticGroups.add(entry.getKey());
            }
        }
        return availableLogisticGroups;
    }
}
