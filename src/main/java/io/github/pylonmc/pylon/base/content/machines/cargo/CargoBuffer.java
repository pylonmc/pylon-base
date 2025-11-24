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
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.logistics.VirtualInventoryLogisticSlot;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.List;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class CargoBuffer extends PylonBlock
        implements PylonDirectionalBlock, PylonGuiBlock, PylonCargoBlock, PylonEntityHolderBlock {

    private static final NamespacedKey FACE_KEY = baseKey("face");

    private final BlockFace facing;
    private final VirtualInventory inventory = new VirtualInventory(1);

    public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

    public final ItemStack mainStack = ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":main")
            .build();
    public final ItemStack side1Stack = ItemStackBuilder.of(Material.BARREL)
            .addCustomModelDataString(getKey() + ":side1")
            .build();
    public final ItemStack side2Stack = ItemStackBuilder.of(Material.BARREL)
            .addCustomModelDataString(getKey() + ":side2")
            .build();
    public final ItemStack inputStack = ItemStackBuilder.of(Material.LIME_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":input")
            .build();
    public final ItemStack outputStack = ItemStackBuilder.of(Material.RED_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":output")
            .build();

    public static class Item extends PylonItem {

        public final int transferRate = getSettings().getOrThrow("transfer-rate", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of(
                            "items-per-second",
                            UnitFormat.ITEMS_PER_SECOND.format(PylonCargoBlock.cargoItemsTransferredPerSecond(transferRate))
                    )
            );
        }
    }

    @SuppressWarnings("unused")
    public CargoBuffer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        if (!(context instanceof BlockCreateContext.PlayerPlace playerPlaceContext)) {
            throw new IllegalArgumentException("Cargo buffers can only be placed by player");
        }

        facing = PylonUtils.rotateToPlayerFacing(playerPlaceContext.getPlayer(), BlockFace.NORTH, true);

        addCargoLogisticGroup(facing, "input");
        addCargoLogisticGroup(facing.getOppositeFace(), "output");
        setCargoTransferRate(transferRate);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .scale(0.65)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("side1", new ItemDisplayBuilder()
                .itemStack(side1Stack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .rotate(Math.PI / 2, Math.PI / 2, 0)
                        .scale(0.5, 0.5, 0.7)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("side2", new ItemDisplayBuilder()
                .itemStack(side2Stack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .rotate(Math.PI / 2, Math.PI / 2, 0)
                        .scale(0.7, 0.5, 0.5)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("input", new ItemDisplayBuilder()
                .itemStack(inputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .translate(0, 0, 0.15)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("output", new ItemDisplayBuilder()
                .itemStack(outputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .translate(0, 0, -0.15)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public CargoBuffer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        facing = pdc.get(FACE_KEY, PylonSerializers.BLOCK_FACE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(FACE_KEY, PylonSerializers.BLOCK_FACE, facing);
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure("# # # # x # # # #")
                .addIngredient('#', GuiItems.background())
                .addIngredient('x', inventory)
                .build();
    }

    @Override
    public @Nullable BlockFace getFacing() {
        return facing;
    }

    @Override
    public void setupLogisticGroups() {
        createLogisticGroup("input", LogisticSlotType.INPUT, new VirtualInventoryLogisticSlot(inventory, 0));
        createLogisticGroup("output", LogisticSlotType.OUTPUT, new VirtualInventoryLogisticSlot(inventory, 0));
    }
}
