package io.github.pylonmc.pylon.base.items.fluid.items;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidDrainer extends PylonBlock implements PylonEntityHolderBlock, PylonFluidBlock, PylonTickingBlock {

    public static class Item extends PylonItem {

        public final int tickInterval = getSettings().getOrThrow("tick-interval", Integer.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "drain_interval", UnitFormat.SECONDS.format(tickInterval / 20.0)
                            .decimalPlaces(1)
            );
        }
    }

    public static final NamespacedKey WATER_DRAINER_KEY = pylonKey("water_drainer");
    public static final NamespacedKey LAVA_DRAINER_KEY = pylonKey("lava_drainer");

    public static final NamespacedKey BUFFER_KEY = pylonKey("buffer");

    public final int tickInterval = getSettings().getOrThrow("tick-interval", Integer.class);

    protected final Map<String, UUID> entities;
    private double buffer;

    @SuppressWarnings("unused")
    public FluidDrainer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid drainer can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        FluidConnectionPoint output = new FluidConnectionPoint(getBlock(), "output", FluidConnectionPoint.Type.OUTPUT);

        entities = new HashMap<>(Map.of(
                "output", FluidConnectionInteraction.make(player, output, BlockFace.SOUTH, 0.5F).getUuid()
        ));
        buffer = 0.0;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidDrainer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        entities = loadHeldEntities(pdc);
        buffer = pdc.get(BUFFER_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        saveHeldEntities(pdc);
        pdc.set(BUFFER_KEY, PylonSerializers.DOUBLE, buffer);
    }

    @Override
    public @NotNull Map<String, UUID> getHeldEntities() {
        return entities;
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return Map.of(
                getFluid(), buffer
        );
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        buffer -= amount;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (Math.abs(buffer) > 1.0e-3) {
            return;
        }

        Preconditions.checkState(getBlock().getBlockData() instanceof Directional);
        Directional directional = (Directional) getBlock().getBlockData();
        Block placeBlock = getBlock().getRelative(directional.getFacing());

        if (placeBlock.getType() == getDrainMaterial()) {
            placeBlock.setType(Material.AIR);
            buffer = 1000.0;
        }
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return tickInterval;
    }

    private @NotNull PylonFluid getFluid() {
        return Map.of(
                WATER_DRAINER_KEY, PylonFluids.WATER,
                LAVA_DRAINER_KEY, PylonFluids.LAVA
        ).get(getKey());
    }

    private @NotNull Material getDrainMaterial() {
        return Map.of(
                WATER_DRAINER_KEY, Material.WATER,
                LAVA_DRAINER_KEY, Material.LAVA
        ).get(getKey());
    }
}
