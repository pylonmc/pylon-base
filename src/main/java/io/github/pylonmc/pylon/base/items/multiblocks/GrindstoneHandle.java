package io.github.pylonmc.pylon.base.items.multiblocks;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class GrindstoneHandle extends PylonBlock implements PylonInteractableBlock {

    public static final NamespacedKey KEY = pylonKey("grindstone_handle");

    @SuppressWarnings("unused")
    public GrindstoneHandle(PylonBlockSchema schema, Block block, BlockCreateContext context) {
        super(schema, block);
    }

    @SuppressWarnings("unused")
    public GrindstoneHandle(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
        super(schema, block);
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking() || event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        if (BlockStorage.get(getBlock().getRelative(BlockFace.DOWN)) instanceof Grindstone grindstone) {
            grindstone.tryStartRecipe();
        }
    }
}
