package io.github.pylonmc.pylon.base.content.machines.fluid.gui;


import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
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
                .addIngredient('x', GuiItems.background())
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
            return ItemStackBuilder.of(fluid == null ? new ItemStack(Material.BARRIER) : fluid.getItem())
                    .name(Component.translatable(
                            "pylon.pylonbase.message.fluid_selector.current_fluid",
                            PylonArgument.of(
                                    "fluid",
                                    Component.translatable((fluid == null
                                            ? "pylon.pylonbase.message.fluid_none"
                                            : "pylon.pylonbase.fluid." + fluid.getKey().getKey()
                                    ))
                            ))
                    );
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
            if (itemFluid == null) {
                return ItemStackBuilder.of(Material.BARRIER)
                        .name(Component.translatable("pylon.pylonbase.message.fluid_selector.clear"));
            }
            return ItemStackBuilder.of(itemFluid.getItem())
                    .name(Component.translatable("pylon.pylonbase.fluid." + itemFluid.getKey().getKey()));
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            setFluid.accept(itemFluid);
            currentItem.notifyWindows();
        }
    }
}
