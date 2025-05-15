package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.base.util.gui.DefaultItems;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonInventoryBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.gui.structure.Markers;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.Map;

// Totally not a test item for inventory saving
public class BottomlessBarrel {

    private BottomlessBarrel() {
        throw new AssertionError("Container class");
    }

    public static class BottomlessBarrelBlock extends PylonInventoryBlock<BottomlessBarrelBlock.Schema> {

        public BottomlessBarrelBlock(@NotNull Schema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
            super(schema, block);
        }

        public BottomlessBarrelBlock(@NotNull Schema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
            super(schema, block, pdc);
        }

        @Override
        protected @NotNull Gui createGui() {
            return ScrollGui.inventories()
                    .setStructure(
                            "x x x x x x x x ^",
                            "x x x x x x x x #",
                            "x x x x x x x x #",
                            "x x x x x x x x #",
                            "x x x x x x x x #",
                            "x x x x x x x x v"
                    )
                    .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
                    .addIngredient('^', DefaultItems.scrollUp())
                    .addIngredient('v', DefaultItems.scrollDown())
                    .addIngredient('#', DefaultItems.background())
                    .addContent(new VirtualInventory(getSchema().size))
                    .build();
        }

        public static final class Schema extends PylonBlockSchema {

            @Getter
            private final int size = getSettings().getOrThrow("size", Integer.class);

            public Schema(@NotNull NamespacedKey key, @NotNull Material material, @NotNull Class<? extends @NotNull PylonBlock<?>> blockClass) {
                super(key, material, blockClass);
            }
        }
    }

    public static final class BottomlessBarrelItem extends PylonItem<PylonItemSchema> implements BlockPlacer {

        public BottomlessBarrelItem(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return PylonBlocks.BOTTOMLESS_BARREL;
        }

        @Override
        public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
            return Map.of("size", Component.text(PylonBlocks.BOTTOMLESS_BARREL.size));
        }
    }

}
