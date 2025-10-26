package io.github.pylonmc.pylon.base.content.machines.diesel;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;


public abstract class DieselMachine extends PylonBlock {


    protected DieselMachine(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    protected DieselMachine(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }
}
