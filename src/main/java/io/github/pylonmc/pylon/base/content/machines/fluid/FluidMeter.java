package io.github.pylonmc.pylon.base.content.machines.fluid;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.entities.SimpleTextDisplay;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.TextDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;


public class FluidMeter extends FluidFilter implements PylonTickingBlock {

    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    private double removedSinceLastUpdate;

    @SuppressWarnings("unused")
    public FluidMeter(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid valve can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        setTickInterval(tickInterval);

        addEntity("flow_rate_north", createTextDisplay(player, BlockFace.NORTH));
        addEntity("flow_rate_south", createTextDisplay(player, BlockFace.SOUTH));

        removedSinceLastUpdate = 0.0;
    }

    @SuppressWarnings("unused")
    public FluidMeter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        removedSinceLastUpdate = 0.0;
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {
        super.onFluidRemoved(fluid, amount);
        removedSinceLastUpdate += amount;
    }

    public void tick(double deltaSeconds) {
        Component component = UnitFormat.MILLIBUCKETS_PER_SECOND.format(Math.round(removedSinceLastUpdate / deltaSeconds)).asComponent();

        SimpleTextDisplay northDisplay = getHeldEntity(SimpleTextDisplay.class, "flow_rate_north");
        if (northDisplay != null) {
            northDisplay.getEntity().text(component);
        }

        SimpleTextDisplay southDisplay = getHeldEntity(SimpleTextDisplay.class, "flow_rate_south");
        if (southDisplay != null) {
            southDisplay.getEntity().text(component);
        }

        removedSinceLastUpdate = 0.0;
    }

    private @NotNull SimpleTextDisplay createTextDisplay(@NotNull Player player, @NotNull BlockFace face) {
        return new SimpleTextDisplay(new TextDisplayBuilder()
                .transformation(new TransformBuilder()
                        .lookAlong(PylonUtils.rotateToPlayerFacing(player, face, false).getDirection().toVector3d())
                        .translate(new Vector3d(0.0, 0.0, 0.126))
                        .scale(0.3, 0.3, 0.0001)
                )
                .backgroundColor(Color.fromARGB(0, 0, 0, 0))
                .text(UnitFormat.MILLIBUCKETS_PER_SECOND.format(0).asComponent())
                .build(getBlock().getLocation().toCenterLocation())
        );
    }

    @Override
    public @NotNull Component getGuiTitle() {
        return Component.translatable("pylon.pylonbase.item.fluid_meter.gui");
    }
}
