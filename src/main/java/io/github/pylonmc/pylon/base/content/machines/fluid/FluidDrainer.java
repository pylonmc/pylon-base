package io.github.pylonmc.pylon.base.content.machines.fluid;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class FluidDrainer extends PylonBlock
        implements PylonFluidBufferBlock, PylonEntityHolderBlock, PylonTickingBlock, PylonInteractableBlock {

    public static class Item extends PylonItem {

        public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
        public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("drain_interval", UnitFormat.SECONDS.format(tickInterval / 20.0).decimalPlaces(1)),
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(buffer))
            );
        }
    }

    public final Material material = getSettings().getOrThrow("material", ConfigAdapter.MATERIAL);
    public final PylonFluid fluid = getSettings().getOrThrow("fluid", ConfigAdapter.PYLON_FLUID);
    public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final Block drainBlock;

    @SuppressWarnings("unused")
    public FluidDrainer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        setTickInterval(tickInterval);
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));
        createFluidBuffer(fluid, buffer, false, true);
        Preconditions.checkState(getBlock().getBlockData() instanceof Directional);
        Directional directional = (Directional) getBlock().getBlockData();
        drainBlock = getBlock().getRelative(directional.getFacing());
    }

    @SuppressWarnings("unused")
    public FluidDrainer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        Preconditions.checkState(getBlock().getBlockData() instanceof Directional);
        Directional directional = (Directional) getBlock().getBlockData();
        drainBlock = getBlock().getRelative(directional.getFacing());
    }

    @Override
    public void tick(double deltaSeconds) {
        if (fluidSpaceRemaining(fluid) >= 1000.0
                && drainBlock.getType() == material
                && drainBlock.getBlockData() instanceof Levelled levelled
                && levelled.getLevel() == 0 // 0 = source block (for some reason)
                && new BlockBreakBlockEvent(drainBlock, getBlock(), List.of()).callEvent()
        ) {
            drainBlock.setType(Material.AIR);
            addFluid(fluid, 1000.0);
        }
    }

    // Prevent opening dispenser
    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            event.setCancelled(true);
        }
    }
}
