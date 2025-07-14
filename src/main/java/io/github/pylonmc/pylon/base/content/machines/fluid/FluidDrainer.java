package io.github.pylonmc.pylon.base.content.machines.fluid;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiBufferFluidBlock;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class FluidDrainer extends PylonBlock
        implements PylonMultiBufferFluidBlock, PylonEntityHolderBlock, PylonTickingBlock, PylonInteractableBlock {

    public static class Item extends PylonItem {

        public final int tickInterval = getSettings().getOrThrow("tick-interval", Integer.class);
        public final double buffer = getSettings().getOrThrow("buffer", Double.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "drain_interval", UnitFormat.SECONDS.format(tickInterval / 20.0)
                            .decimalPlaces(1),
                    "buffer", UnitFormat.MILLIBUCKETS.format(buffer)
            );
        }
    }

    public final Material material = getSettings().getMaterialOrThrow("material");
    public final PylonFluid fluid = getSettings().getFluidOrThrow("fluid");
    public final double buffer = getSettings().getOrThrow("buffer", Double.class);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", Integer.class);

    @SuppressWarnings("unused")
    public FluidDrainer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        createFluidBuffer(fluid, buffer);
    }

    @SuppressWarnings("unused")
    public FluidDrainer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        return Map.of(
                "output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH)
        );
    }

    @Override
    public void tick(double deltaSeconds) {
        if (fluidSpaceRemaining(fluid) < 1000.0) {
            return;
        }

        Preconditions.checkState(getBlock().getBlockData() instanceof Directional);
        Directional directional = (Directional) getBlock().getBlockData();
        Block placeBlock = getBlock().getRelative(directional.getFacing());

        if (placeBlock.getType() == material) {
            placeBlock.setType(Material.AIR);
            addFluid(fluid, 1000.0);
        }
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return tickInterval;
    }

    // Prevent opening dispenser
    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            event.setCancelled(true);
        }
    }
}
