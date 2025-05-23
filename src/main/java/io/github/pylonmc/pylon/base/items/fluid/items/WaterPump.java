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

import java.util.Map;
import java.util.UUID;


public class WaterPump extends PylonBlock<WaterPump.Schema> implements PylonEntityHolderBlock, PylonFluidBlock {

    public static class Schema extends PylonBlockSchema {

        // TODO settings
        @Getter private final double fluidPerSecond = 500.0;

        public Schema(@NotNull NamespacedKey key, @NotNull Material material) {
            super(key, material, WaterPump.class);
        }
    }

    private final Map<String, UUID> entities;

    @SuppressWarnings("unused")
    public WaterPump(@NotNull Schema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
        super(schema, block);

        Player player = null;
        if (context instanceof BlockCreateContext.PlayerPlace ctx) {
            player = ctx.getPlayer();
        }

        FluidConnectionPoint output = new FluidConnectionPoint(getBlock(), "output", FluidConnectionPoint.Type.OUTPUT);

        entities = Map.of(
                "output", FluidConnectionInteraction.make(player, output, BlockFace.UP, 0.5F).getUuid()
        );
    }

    @SuppressWarnings("unused")
    public WaterPump(@NotNull Schema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(schema, block);

        entities = loadHeldEntities(pdc);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        saveHeldEntities(pdc);
    }

    @Override
    public @NotNull Map<String, UUID> getHeldEntities() {
        return entities;
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        if (getBlock().getRelative(BlockFace.DOWN).getType() != Material.WATER) {
            return Map.of();
        }
        return Map.of(PylonFluids.WATER, getSchema().fluidPerSecond);
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        // nothing, water block is treated as infinite lol
    }
}