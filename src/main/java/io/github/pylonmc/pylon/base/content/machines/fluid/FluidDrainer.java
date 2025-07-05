package io.github.pylonmc.pylon.base.content.machines.fluid;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.fluid.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.pylonKey;


public class FluidDrainer extends PylonBlock implements PylonFluidIoBlock, PylonTickingBlock, PylonInteractableBlock {

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

    private double buffer;

    @SuppressWarnings("unused")
    public FluidDrainer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid drainer can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        FluidConnectionPoint output = new FluidConnectionPoint(getBlock(), "output", FluidConnectionPoint.Type.OUTPUT);

        buffer = 0.0;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidDrainer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        buffer = pdc.get(BUFFER_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(BUFFER_KEY, PylonSerializers.DOUBLE, buffer);
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.OUTPUT, BlockFace.SOUTH));
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

    // Prevent opening dispenser
    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) {
            event.setCancelled(true);
        }
    }

    private @NotNull PylonFluid getFluid() {
        return Map.of(
                WATER_DRAINER_KEY, BaseFluids.WATER,
                LAVA_DRAINER_KEY, BaseFluids.LAVA
        ).get(getKey());
    }

    private @NotNull Material getDrainMaterial() {
        return Map.of(
                WATER_DRAINER_KEY, Material.WATER,
                LAVA_DRAINER_KEY, Material.LAVA
        ).get(getKey());
    }
}
