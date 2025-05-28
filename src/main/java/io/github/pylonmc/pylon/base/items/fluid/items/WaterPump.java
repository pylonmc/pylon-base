package io.github.pylonmc.pylon.base.items.fluid.items;

import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class WaterPump extends PylonBlock implements PylonEntityHolderBlock, PylonFluidBlock {

    public static final NamespacedKey KEY = pylonKey("water_pump");

    public static final double WATER_PER_SECOND = getSettings(KEY).getOrThrow("water-per-second", Double.class);

    private final Map<String, UUID> entities;

    @SuppressWarnings("unused")
    public WaterPump(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

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
    public WaterPump(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

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
        return Map.of(PylonFluids.WATER, WATER_PER_SECOND);
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        // nothing, water block is treated as infinite lol
    }
}
