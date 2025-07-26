package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class MysticalFoodEnhancerHandle extends PylonBlock implements PylonInteractableBlock {

    public MysticalFoodEnhancerHandle(Block block, BlockCreateContext context) {
        super(block, context);
    }

    public MysticalFoodEnhancerHandle(Block block, PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        //noinspection ConstantConditions no npe on getClickedBlock since event.getClickedBlock != null is checked by the function that calls onInteract
        MysticalFoodEnhancer processor = BlockStorage.getAs(MysticalFoodEnhancer.class, event.getClickedBlock().getRelative(BlockFace.DOWN));
        if (processor == null) return;
        processor.cook();
    }
}