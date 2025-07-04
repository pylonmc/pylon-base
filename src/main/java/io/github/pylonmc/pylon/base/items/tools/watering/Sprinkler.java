package io.github.pylonmc.pylon.base.items.tools.watering;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.event.PrePylonBlockPlaceEvent;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class Sprinkler extends PylonBlock implements PylonFluidIoBlock, PylonTickingBlock, PylonFluidBlock {

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "range", UnitFormat.BLOCKS.format(SETTINGS.horizontalRange()),
                    "water_consumption", UnitFormat.MILLIBUCKETS_PER_SECOND.format(WATER_PER_SECOND)
            );
        }
    }

    public static final NamespacedKey KEY = pylonKey("sprinkler");

    public static final NamespacedKey WATER_BUFFER_KEY = pylonKey("water_buffer");

    public static final WateringSettings SETTINGS = WateringSettings.fromConfig(Settings.get(KEY));

    public static final int TICK_INTERVAL = Settings.get(KEY).getOrThrow("tick-interval", Integer.class);
    public static final double WATER_PER_SECOND = Settings.get(KEY).getOrThrow("water-per-second", Integer.class);

    private double waterBuffer;

    @SuppressWarnings("unused")
    public Sprinkler(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid valve can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        FluidConnectionPoint input = new FluidConnectionPoint(getBlock(), "input", FluidConnectionPoint.Type.INPUT);

        waterBuffer = 0.0;
    }

    @SuppressWarnings("unused")
    public Sprinkler(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        waterBuffer = pdc.get(WATER_BUFFER_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(WATER_BUFFER_KEY, PylonSerializers.DOUBLE, waterBuffer);
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, BlockFace.UP, -0.15F));
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return TICK_INTERVAL;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (waterBuffer > WATER_PER_SECOND * deltaSeconds) {
            WateringCan.water(getBlock(), SETTINGS);
            waterBuffer -= WATER_PER_SECOND * deltaSeconds;
        }
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        // should make sure we always have enough water for next tick, multiply by 2 to be safe
        return Map.of(PylonFluids.WATER, Math.max(0.0, 2 * TICK_INTERVAL * WATER_PER_SECOND * deltaSeconds - waterBuffer));
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        waterBuffer += amount;
    }

    public static class SprinklerPlaceListener implements Listener {
        @EventHandler
        private static void handle(@NotNull PrePylonBlockPlaceEvent event) {
            if (!(event.getPylonBlock() instanceof Sprinkler)) {
                return;
            }

            int horizontalRadiusToCheck = 2 * SETTINGS.horizontalRange();
            int verticalRadiusToCheck = 2 * SETTINGS.verticalRange();
            for (int x = -horizontalRadiusToCheck; x < horizontalRadiusToCheck; x++) {
                for (int z = -horizontalRadiusToCheck; z < horizontalRadiusToCheck; z++) {
                    for (int y = -verticalRadiusToCheck; y < verticalRadiusToCheck; y++) {
                        if (!(BlockStorage.get(event.getBlock().getRelative(x, y, z)) instanceof Sprinkler)) {
                            continue;
                        }

                        event.setCancelled(true);
                        if (event.getContext() instanceof BlockCreateContext.PlayerPlace context) {
                            context.getPlayer().sendMessage(Component.translatable(
                                    "pylon.pylonbase.message.sprinkler_too_close",
                                    PylonArgument.of("radius", horizontalRadiusToCheck)
                            ));
                        }
                        break;
                    }
                }
            }
        }
    }
}
