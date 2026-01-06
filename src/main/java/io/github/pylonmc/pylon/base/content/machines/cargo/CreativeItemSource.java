package io.github.pylonmc.pylon.base.content.machines.cargo;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonCargoBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticGroupType;
import io.github.pylonmc.pylon.core.logistics.slot.LogisticSlot;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;


public class CreativeItemSource extends PylonBlock
        implements PylonDirectionalBlock, PylonGuiBlock, PylonCargoBlock, PylonEntityHolderBlock {

    private final VirtualInventory inventory = new VirtualInventory(1);

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.PURPLE_CONCRETE)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder side1Stack = ItemStackBuilder.of(Material.BEDROCK)
            .addCustomModelDataString(getKey() + ":side1");
    public final ItemStackBuilder side2Stack = ItemStackBuilder.of(Material.BEDROCK)
            .addCustomModelDataString(getKey() + ":side2");
    public final ItemStackBuilder outputStack = ItemStackBuilder.of(Material.RED_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":output");

    @SuppressWarnings("unused")
    public CreativeItemSource(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());

        addCargoLogisticGroup(getFacing().getOppositeFace(), "output");
        setCargoTransferRate(Integer.MAX_VALUE);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .scale(0.6)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("side1", new ItemDisplayBuilder()
                .itemStack(side1Stack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .rotate(Math.PI / 2, Math.PI / 2, 0)
                        .scale(0.45, 0.45, 0.65)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("side2", new ItemDisplayBuilder()
                .itemStack(side2Stack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .rotate(Math.PI / 2, Math.PI / 2, 0)
                        .scale(0.65, 0.45, 0.45)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("output", new ItemDisplayBuilder()
                .itemStack(outputStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0, -0.125)
                        .scale(0.4, 0.4, 0.4)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("item", new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .rotate(Math.PI / 2, 0, 0)
                        .scale(0.3)
                        .translate(0, 0.5 / 0.3, 0))
                .build(block.getLocation().toCenterLocation())
        );
    }

    @SuppressWarnings("unused")
    public CreativeItemSource(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    private class InfiniteLogisticSlot implements LogisticSlot {

        @Override
        public @Nullable ItemStack getItemStack() {
            return inventory.getItem(0);
        }

        @Override
        public long getAmount() {
            ItemStack stack = inventory.getItem(0);
            return stack == null ? 0 : stack.getMaxStackSize();
        }

        @Override
        public long getMaxAmount(@NotNull ItemStack stack) {
            return stack.getMaxStackSize();
        }

        @Override
        public void set(@Nullable ItemStack stack, long amount) {
            // ignore hehe
        }
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
    public void postInitialise() {
        createLogisticGroup("output", LogisticGroupType.OUTPUT, new InfiniteLogisticSlot());
        inventory.setPostUpdateHandler(event -> getHeldEntityOrThrow(ItemDisplay.class, "item").setItemStack(inventory.getItem(0)));
    }
}
