package io.github.pylonmc.pylon.base.content.building;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.List;


// Totally not a test item for inventory saving
public class DimensionalBarrel extends PylonBlock implements PylonGuiBlock {

    public static final int SIZE = Settings.get(BaseKeys.DIMENSIONAL_BARREL).getOrThrow("size", Integer.class);

    public DimensionalBarrel(Block block, BlockCreateContext context) {
        super(block);
    }

    public DimensionalBarrel(Block block, PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public @NotNull Gui createGui() {
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

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(PylonArgument.of("size", SIZE));
        }
    }
}
