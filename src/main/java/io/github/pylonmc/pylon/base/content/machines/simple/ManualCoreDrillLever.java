package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Switch;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

public class ManualCoreDrillLever extends PylonBlock implements PylonInteractBlock {

    private BukkitTask leverResetTask;

    @SuppressWarnings("unused")
    public ManualCoreDrillLever(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
    }

    @SuppressWarnings("unused")
    public ManualCoreDrillLever(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick() || event.getPlayer().isSneaking()) {
            return;
        }

        if (!(getBlock().getBlockData() instanceof Switch blockData)) {
            throw new IllegalStateException("Block data is not switch");
        }

        if (blockData.isPowered()) {
            event.setCancelled(true);
        }

        ManualCoreDrill drill = BlockStorage.getAs(
                ManualCoreDrill.class,
                getBlock().getRelative(blockData.getFacing().getOppositeFace())
        );
        if (drill == null || drill.isCycling()) {
            return;
        }

        drill.cycle();

        if (leverResetTask != null) {
            leverResetTask.cancel();
        }

        leverResetTask = Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
            if (getBlock().getBlockData() instanceof Switch switchData) {
                switchData.setPowered(false);
                getBlock().setBlockData(switchData);
            }
        }, (long) drill.getRotationDuration() * drill.getRotationsPerCycle());
    }
}
