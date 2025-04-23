package io.github.pylonmc.pylon.base.items;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;


public final class GrindstoneHandle {

    private GrindstoneHandle() {
        throw new AssertionError("Container class");
    }

    public static class GrindstoneHandleBlock extends PylonBlock<PylonBlockSchema> implements PylonInteractableBlock {

        public GrindstoneHandleBlock(PylonBlockSchema schema, Block block) {
            super(schema, block);
        }

        @Override
        public void onInteract(@NotNull PlayerInteractEvent event) {
            if (event.getPlayer().isSneaking() || event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            event.setCancelled(true);

            if (BlockStorage.get(getBlock().getRelative(BlockFace.DOWN)) instanceof Grindstone.GrindstoneBlock grindstone) {
                grindstone.tryStartRecipe();
            }
        }
    }
}
