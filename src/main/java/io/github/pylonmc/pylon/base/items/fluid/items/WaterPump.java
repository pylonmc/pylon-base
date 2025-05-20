package io.github.pylonmc.pylon.base.items.fluid.items;

import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.UUID;

@NullMarked
public class WaterPump extends PylonBlock<WaterPump.Schema> implements PylonEntityHolderBlock, PylonFluidBlock {

    public static class Schema extends PylonBlockSchema {

        // TODO settings
        @Getter private final long fluidPerTick = 500 / 20;

        public Schema(NamespacedKey key, Material material) {
            super(key, material, WaterPump.class);
        }
    }

    @SuppressWarnings("unused")
    public WaterPump(Schema schema, Block block, BlockCreateContext context) {
        super(schema, block);
    }

    @SuppressWarnings("unused")
    public WaterPump(Schema schema, Block block, PersistentDataContainer pdc) {
        super(schema, block);
    }

    @Override
    public Map<String, UUID> createEntities(BlockCreateContext context) {
        Player player = null;
        if (context instanceof BlockCreateContext.PlayerPlace ctx) {
            player = ctx.getPlayer();
        }

        FluidConnectionPoint output = new FluidConnectionPoint(getBlock(), "output", FluidConnectionPoint.Type.OUTPUT);

        return Map.of(
                "output", FluidConnectionInteraction.make(player, output, BlockFace.UP, 0.5F).getUuid()
        );
    }

    @Override
    public Map<PylonFluid, Long> getSuppliedFluids(String connectionPoint) {
        if (getBlock().getRelative(BlockFace.DOWN).getType() != Material.WATER) {
            return Map.of();
        }
        return Map.of(PylonFluids.WATER, getSchema().fluidPerTick);
    }

    @Override
    public void removeFluid(String connectionPoint, PylonFluid fluid, long amount) {
        // nothing, water block is treated as infinite lol
    }
}