package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.block.base.PylonInventoryBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


// Totally not a test item for inventory saving
public class DimensionalBarrel extends PylonInventoryBlock {

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "size", Component.text(SIZE)
            );
        }
    }

    public static final NamespacedKey KEY = pylonKey("dimensional_barrel");

    public static final int SIZE = Settings.get(KEY).getOrThrow("size", Integer.class);

    public DimensionalBarrel(Block block, BlockCreateContext context) {
        super(block);
    }

    public DimensionalBarrel(Block block, PersistentDataContainer pdc) {
        super(block, pdc);
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
