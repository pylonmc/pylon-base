package io.github.pylonmc.pylon.base.util.gui;

import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.ScrollGui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.controlitem.ScrollItem;

final class PylonScrollItem extends ScrollItem {

    private final Component name;
    private final int direction;

    PylonScrollItem(int direction, String key) {
        super(direction);
        this.direction = direction;
        this.name = Component.translatable("pylon.pylonbase.gui.scroll." + key);
    }

    @Override
    public @NotNull ItemProvider getItemProvider(@NotNull ScrollGui<?> gui) {
        return ItemStackBuilder.of(gui.canScroll(direction) ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE)
                .name(name);
    }
}
