package io.github.pylonmc.pylon.base.content.tools;

import org.bukkit.inventory.ItemStack;


public interface Moldable {
    void doMoldingClick();
    boolean isMoldingFinished();
    ItemStack moldingResult();
}
