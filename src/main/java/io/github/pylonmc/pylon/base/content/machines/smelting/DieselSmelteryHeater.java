package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class DieselSmelteryHeater extends SmelteryComponent implements
        PylonFluidBufferBlock,
        PylonDirectionalBlock,
        PylonTickingBlock {

    public final double dieselBuffer = getSettings().getOrThrow("diesel-buffer", ConfigAdapter.DOUBLE);
    public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final double temperature = getSettings().getOrThrow("temperature", ConfigAdapter.DOUBLE);

    public static class Item extends PylonItem {

        public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);
        public final double dieselBuffer = getSettings().getOrThrow("diesel-buffer", ConfigAdapter.DOUBLE);
        public final double temperature = getSettings().getOrThrow("temperature", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("diesel-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(dieselPerSecond)),
                    PylonArgument.of("diesel-buffer", UnitFormat.MILLIBUCKETS.format(dieselBuffer)),
                    PylonArgument.of("temperature", UnitFormat.CELSIUS.format(temperature))
            );
        }
    }

    @SuppressWarnings("unused")
    public DieselSmelteryHeater(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(tickInterval);
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        setFacing(context.getFacing());
        createFluidBuffer(BaseFluids.BIODIESEL, dieselBuffer, true, false);
    }

    @SuppressWarnings("unused")
    public DieselSmelteryHeater(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void tick() {
        SmelteryController controller = getController();
        if (controller == null
                || !controller.isRunning()
                || fluidAmount(BaseFluids.BIODIESEL) < dieselPerSecond * tickInterval / 20
        ) {
            return;
        }

        removeFluid(BaseFluids.BIODIESEL, dieselPerSecond * tickInterval / 20);
        controller.heatAsymptotically(tickInterval / 20.0, temperature);
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("bar", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.BIODIESEL),
                        fluidCapacity(BaseFluids.BIODIESEL),
                        20,
                        TextColor.fromHexString("#eaa627")
                ))
        ));
    }
}
