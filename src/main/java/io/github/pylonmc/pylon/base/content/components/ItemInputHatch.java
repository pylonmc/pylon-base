package io.github.pylonmc.pylon.base.content.components;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonCargoBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonLogisticBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

public class ItemInputHatch extends PylonBlock implements PylonGuiBlock, PylonLogisticBlock {

    public final VirtualInventory inventory = new VirtualInventory(1);

    public ItemInputHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public ItemInputHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("input", LogisticSlotType.INPUT, inventory);
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure("# # # # x # # # #")
                .addIngredient('#', GuiItems.background())
                .addIngredient('x', inventory)
                .build();
    }
}
