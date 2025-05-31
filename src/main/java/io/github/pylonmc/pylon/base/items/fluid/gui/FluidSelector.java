package io.github.pylonmc.pylon.base.items.fluid.gui;


import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public final class FluidSelector {

    private FluidSelector() {}

    public static Gui make(@NotNull Supplier<PylonFluid> getFluid, @NotNull Consumer<PylonFluid> setFluid) {
        AbstractItem currentItem = new CurrentItem(getFluid);
        Function<PylonFluid, AbstractItem> fluidItem = itemFluid -> new FluidItem(itemFluid, setFluid, currentItem);

        Gui gui = Gui.normal()
                .setStructure(
                        "x x b x x x c x x",
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        ". . . . . . . . ."
                )
                .addIngredient('x', new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE))
                .addIngredient('b', fluidItem.apply(null))
                .addIngredient('c', currentItem)
                .build();

        int i = 9;
        for (PylonFluid itemFluid : PylonRegistry.FLUIDS.getValues()) {
            gui.setItem(i, fluidItem.apply(itemFluid));
            i++;
        }

        return gui;
    }

    private static class CurrentItem extends AbstractItem {
        private final @NotNull Supplier<PylonFluid> getFluid;

        public CurrentItem(@NotNull Supplier<PylonFluid> getFluid) {
            super();
            this.getFluid = getFluid;
        }

        @Override
        public @NotNull ItemProvider getItemProvider() {
            PylonFluid fluid = getFluid.get();
            return new ItemBuilder(fluid == null ? Material.BARRIER : fluid.getMaterial())
                    // TODO use fluid name when that's in
                    .setDisplayName("Current fluid: " + (fluid == null ? "None" : fluid.getKey().toString()));
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {}
    }

    private static class FluidItem extends AbstractItem {
        private final PylonFluid itemFluid;
        private final @NotNull Consumer<PylonFluid> setFluid;
        private final AbstractItem currentItem;

        public FluidItem(PylonFluid itemFluid, @NotNull Consumer<PylonFluid> setFluid, AbstractItem currentItem) {
            super();
            this.itemFluid = itemFluid;
            this.setFluid = setFluid;
            this.currentItem = currentItem;
        }

        @Override
        public ItemProvider getItemProvider() {
            // TODO use fluid name when that's in
            if (itemFluid == null) {
                return new ItemBuilder(Material.BARRIER).setDisplayName("Clear");
            }
            return new ItemBuilder(itemFluid.getMaterial()).setDisplayName(itemFluid.getKey().toString());
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            setFluid.accept(itemFluid);
            currentItem.notifyWindows();
        }
    }
}
