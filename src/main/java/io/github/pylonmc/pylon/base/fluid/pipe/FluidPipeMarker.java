package io.github.pylonmc.pylon.base.fluid.pipe;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class FluidPipeMarker extends PylonBlock implements PylonBreakHandler {

    private static final NamespacedKey PIPE_DISPLAY_KEY = baseKey("pipe_display");
    private static final NamespacedKey FROM_KEY = baseKey("from");
    private static final NamespacedKey TO_KEY = baseKey("to");

    // Should always be set immediately after the marker has been placed
    @Setter private UUID pipeDisplay;
    @Setter private UUID from;
    @Setter private UUID to;

    @SuppressWarnings("unused")
    public FluidPipeMarker(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidPipeMarker(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        pipeDisplay = pdc.get(PIPE_DISPLAY_KEY, PylonSerializers.UUID);
        from = pdc.get(FROM_KEY, PylonSerializers.UUID);
        to = pdc.get(TO_KEY, PylonSerializers.UUID);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(PIPE_DISPLAY_KEY, PylonSerializers.UUID, pipeDisplay);
        pdc.set(FROM_KEY, PylonSerializers.UUID, from);
        pdc.set(TO_KEY, PylonSerializers.UUID, to);
    }

    public @Nullable FluidPipeDisplay getPipeDisplay() {
        return EntityStorage.getAs(FluidPipeDisplay.class, pipeDisplay);
    }

    public @Nullable FluidConnectionInteraction getFrom() {
        return EntityStorage.getAs(FluidConnectionInteraction.class, from);
    }

    public @NotNull UUID getFromId() {
        return from;
    }

    public @Nullable FluidConnectionInteraction getTo() {
        return EntityStorage.getAs(FluidConnectionInteraction.class, to);
    }

    public @NotNull UUID getToId() {
        return to;
    }

    @Override
    public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
        // if this is triggered by a fluid connector being broken, the pipe display will already have been deleted
        // not the ideal solution, but can't think of anything better
        if (!(context instanceof BlockBreakContext.PluginBreak)) {
            FluidPipeDisplay pipeDisplay = getPipeDisplay();
            Preconditions.checkState(pipeDisplay != null);

            Player player = null;
            if (context instanceof BlockBreakContext.PlayerBreak breakContext) {
                player = breakContext.getEvent().getPlayer();
            }

            pipeDisplay.delete(true, player);
        }
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        FluidPipeDisplay pipeDisplay = getPipeDisplay();
        Preconditions.checkState(pipeDisplay != null);
        return new WailaConfig(getName(), Map.of("pipe", pipeDisplay.getPipe().getStack().effectiveName()));
    }
}
