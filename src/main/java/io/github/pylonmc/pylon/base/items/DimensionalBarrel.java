package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonInventoryBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


// Totally not a test item for inventory saving
public class DimensionalBarrel extends PylonInventoryBlock {

    public static final NamespacedKey KEY = pylonKey("dimensional_barrel");

    public static final int SIZE = getSettings(KEY).getOrThrow("size", Integer.class);

    public DimensionalBarrel(PylonBlockSchema schema, Block block, BlockCreateContext context) {
        super(schema, block);
    }

    public DimensionalBarrel(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
        super(schema, block, pdc);
    }

    @Override
    protected @NotNull Gui createGui() {
        return PagedGui.inventories()
                .setStructure(
                        "x x x x x x x x ^",
                        "x x x x x x x x #",
                        "x x x x x x x x #",
                        "x x x x x x x x #",
                        "x x x x x x x x #",
                        "x x x x x x x x v"
                )
                .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                .addIngredient('^', GuiItems.pagePrevious())
                .addIngredient('v', GuiItems.pageNext())
                .addIngredient('#', GuiItems.background())
                .addContent(new VirtualInventory(SIZE))
                .build();
    }
}
