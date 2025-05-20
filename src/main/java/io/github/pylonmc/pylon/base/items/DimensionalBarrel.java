package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonInventoryBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.PagedGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.Map;
import java.util.function.Function;

// Totally not a test item for inventory saving
@NullMarked
public class DimensionalBarrel {

    private DimensionalBarrel() {
        throw new AssertionError("Container class");
    }

    public static class DimensionalBarrelBlock extends PylonInventoryBlock<DimensionalBarrelBlock.Schema> {

        public DimensionalBarrelBlock(Schema schema, Block block, BlockCreateContext context) {
            super(schema, block);
        }

        public DimensionalBarrelBlock(Schema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block, pdc);
        }

        @Override
        protected Gui createGui() {
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
                    .addContent(new VirtualInventory(getSchema().size))
                    .build();
        }

        public static final class Schema extends PylonBlockSchema {

            @Getter
            private final int size = getSettings().getOrThrow("size", Integer.class);

            public Schema(NamespacedKey key, Material material, Class<? extends PylonBlock<?>> blockClass) {
                super(key, material, blockClass);
            }
        }
    }

    public static final class DimensionalBarrelItem extends PylonItem<DimensionalBarrelItem.Schema> implements BlockPlacer {

        public static final class Schema extends PylonItemSchema {

            private final DimensionalBarrelBlock.Schema blockSchema;

            public Schema(
                    NamespacedKey key,
                    Class<? extends PylonItem<? extends DimensionalBarrelItem.Schema>> itemClass,
                    DimensionalBarrelBlock.Schema blockSchema,
                    Function<NamespacedKey, ItemStack> templateSupplier
            ) {
                super(key, itemClass, templateSupplier);
                this.blockSchema = blockSchema;
            }
        }

        public DimensionalBarrelItem(DimensionalBarrelItem.Schema schema, ItemStack stack) {
            super(schema, stack);
        }

        @Override
        public DimensionalBarrelBlock.Schema getBlockSchema() {
            return getSchema().blockSchema;
        }

        @Override
        public Map<String, Component> getPlaceholders() {
            return Map.of("size", Component.text(getBlockSchema().size));
        }
    }
}
