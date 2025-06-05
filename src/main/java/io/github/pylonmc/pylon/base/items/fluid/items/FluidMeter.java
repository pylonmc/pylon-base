package io.github.pylonmc.pylon.base.items.fluid.items;

import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.TextDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidMeter extends FluidFilter implements PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("fluid_meter");

    public static final int INTERVAL_TICKS = Settings.get(KEY).getOrThrow("interval-ticks", Integer.class);

    private double removedSinceLastUpdate;

    @SuppressWarnings("unused")
    public FluidMeter(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        entities.put("flow_rate_north", FlowRateDisplay.make(block, player, BlockFace.NORTH).getUuid());
        entities.put("flow_rate_south", FlowRateDisplay.make(block, player, BlockFace.SOUTH).getUuid());

        removedSinceLastUpdate = 0.0;
    }

    @SuppressWarnings("unused")
    public FluidMeter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        removedSinceLastUpdate = 0.0;
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        super.removeFluid(connectionPoint, fluid, amount);

        removedSinceLastUpdate += amount;
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return INTERVAL_TICKS;
    }

    @Override
    public void tick(double deltaSeconds) {

        getHeldEntity(FlowRateDisplay.class, "flow_rate_north").setFlowRate(removedSinceLastUpdate / deltaSeconds);
        getHeldEntity(FlowRateDisplay.class, "flow_rate_south").setFlowRate(removedSinceLastUpdate / deltaSeconds);

        removedSinceLastUpdate = 0.0;
    }

    public static class FlowRateDisplay extends PylonEntity<TextDisplay> {

        public static final NamespacedKey KEY = pylonKey("fluid_meter_flow_rate_display");

        @SuppressWarnings("unused")
        public FlowRateDisplay(@NotNull TextDisplay entity) {
            super(entity);
        }

        public FlowRateDisplay(@NotNull Block block, @NotNull Player player, @NotNull BlockFace face) {
            super(KEY, new TextDisplayBuilder()
                    .transformation(new TransformBuilder()
                            .lookAlong(PylonUtils.rotateToPlayerFacing(player, face).getDirection().toVector3d())
                            .translate(new Vector3d(0.0, 0.0, 0.126))
                            .scale(0.3, 0.3, 0.0001)
                    )
                    .backgroundColor(Color.fromARGB(0, 0, 0, 0))
                    .text(UnitFormat.MILLIBUCKETS_PER_SECOND.format(0).asComponent())
                    .build(block.getLocation().toCenterLocation())
            );
        }

        public void setFlowRate(double flowRate) {
            getEntity().text(UnitFormat.MILLIBUCKETS_PER_SECOND.format(Math.round(flowRate)).asComponent());
        }

        public static @NotNull FlowRateDisplay make(@NotNull Block block, @NotNull Player player, @NotNull BlockFace face) {
            FlowRateDisplay display = new FlowRateDisplay(block, player, face);
            EntityStorage.add(display);
            return display;
        }
    }
}
