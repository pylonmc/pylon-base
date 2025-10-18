package io.github.pylonmc.pylon.base.content.machines.cargo;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.cargo.CargoPointType;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;


public class CargoExtractor extends PylonBlock implements PylonMultiblock, PylonDirectionalBlock {

    private BlockFace facing;

    public CargoExtractor(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        if (!(context instanceof BlockCreateContext.PlayerPlace playerPlaceContext)) {
            throw new IllegalArgumentException("Cargo extractor can only be placed by player");
        }

        facing = playerPlaceContext.getPlayer().getFacing();

//        createCargoPoint(facing.getOppositeFace(), CargoPointType.OUTPUT);
    }

    public CargoExtractor(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    public Block getTarget() {
        return getBlock().getRelative(facing);
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock()), new ChunkPosition(getTarget()));
    }

    @Override
    public boolean checkFormed() {
        return getTarget().getState() instanceof BlockInventoryHolder;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return new BlockPosition(otherBlock)
                .equals(new BlockPosition(getTarget()));
    }

    @Override
    public @Nullable BlockFace getFacing() {
        return facing;
    }

//    @Override
//    public @Nullable Inventory getCargoInventory(BlockFace face) {
//        return isFormedAndFullyLoaded()
//                ? ((InventoryHolder) (getTarget().getState())).getInventory()
//                : null;
//    }
//
//    @Override
//    public void setCargoInventory(BlockFace face) {
//        throw new IllegalStateException("Should never be called");
//    }
}
