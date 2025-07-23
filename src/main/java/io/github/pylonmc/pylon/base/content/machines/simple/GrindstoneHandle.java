package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.base.recipes.GrindstoneRecipe;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;


public class GrindstoneHandle extends PylonBlock implements PylonInteractableBlock {

    @SuppressWarnings("unused")
    public GrindstoneHandle(Block block, BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings("unused")
    public GrindstoneHandle(Block block, PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) {
            return;
        }

        event.setCancelled(true);

        if (BlockStorage.get(getBlock().getRelative(BlockFace.DOWN)) instanceof Grindstone grindstone) {
            GrindstoneRecipe nextRecipe = grindstone.getNextRecipe();
            if (nextRecipe != null) {
                grindstone.tryStartRecipe(nextRecipe, event.getPlayer());
            }
        }
    }
}
