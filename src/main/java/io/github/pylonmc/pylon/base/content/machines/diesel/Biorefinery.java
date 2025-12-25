package io.github.pylonmc.pylon.base.content.machines.diesel;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonPiston;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class Biorefinery extends PylonBlock implements PylonPiston {

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }


    }

    public Biorefinery(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public Biorefinery(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onExtend(@NotNull BlockPistonExtendEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onRetract(@NotNull BlockPistonRetractEvent event) {
        event.setCancelled(true);
    }
}
