package io.github.pylonmc.pylon.content.assembling;

import io.github.pylonmc.rebar.block.BlockStorage;
import io.github.pylonmc.rebar.block.RebarBlock;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.item.RebarItem;
import io.github.pylonmc.rebar.item.base.RebarBlockInteractor;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AssemblyTool extends RebarItem implements RebarBlockInteractor {

    public final String toolType = getSettings().get("tool-type", ConfigAdapter.STRING);

    public AssemblyTool(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToClickBlock(@NotNull PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        RebarBlock rebarBlock = BlockStorage.get(block);
        if (rebarBlock instanceof AssemblyTable assemblyTable) {
            assemblyTable.useTool(toolType, event.getPlayer());
        }
    }
}
