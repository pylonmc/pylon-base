package io.github.pylonmc.pylon.base.content.building.sponge;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.item.PylonItem;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class WetPowerfulWaterSponge extends PylonBlock {
    protected WetPowerfulWaterSponge(@NotNull Block block) {
        super(block);
    }

    public static class Item extends PylonItem {
        public Item(@NotNull ItemStack stack) {
            super(stack);
        }
    }
}
