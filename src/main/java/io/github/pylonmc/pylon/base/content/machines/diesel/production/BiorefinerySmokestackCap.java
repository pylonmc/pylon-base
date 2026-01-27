package io.github.pylonmc.pylon.base.content.machines.diesel.production;

import io.github.pylonmc.rebar.block.PylonBlock;
import io.github.pylonmc.rebar.block.base.PylonFlowerPot;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;


public class BiorefinerySmokestackCap extends PylonBlock implements PylonFlowerPot {

    public BiorefinerySmokestackCap(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    public BiorefinerySmokestackCap(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onFlowerPotManipulated(@NotNull PlayerFlowerPotManipulateEvent event) {
        event.setCancelled(true);
    }
}
