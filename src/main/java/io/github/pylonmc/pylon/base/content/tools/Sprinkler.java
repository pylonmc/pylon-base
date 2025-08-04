package io.github.pylonmc.pylon.base.content.tools;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.event.PrePylonBlockPlaceEvent;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
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

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class Sprinkler extends PylonBlock implements PylonFluidBlock, PylonTickingBlock, PylonEntityHolderBlock {

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("range", UnitFormat.BLOCKS.format(SETTINGS.horizontalRange())),
                    PylonArgument.of("water_consumption", UnitFormat.MILLIBUCKETS_PER_SECOND.format(WATER_PER_SECOND))
            );
        }
    }

    public static final NamespacedKey WATER_BUFFER_KEY = baseKey("water_buffer");

    private static final Config settings = Settings.get(BaseKeys.SPRINKLER);
    public static final WateringSettings SETTINGS = WateringSettings.fromConfig(settings);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", Integer.class);
    public static final double WATER_PER_SECOND = settings.getOrThrow("water-per-second", Integer.class);

    private double waterBuffer;

    @SuppressWarnings("unused")
    public Sprinkler(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid valve can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

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
    public @NotNull Map<@NotNull String, @NotNull PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        return Map.of(
                "input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.UP, -0.15F)
        );
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
    public @NotNull Map<PylonFluid, Double> getRequestedFluids(double deltaSeconds) {
        // should make sure we always have enough water for next tick, multiply by 2 to be safe
        return Map.of(BaseFluids.WATER, Math.max(0.0, 2 * TICK_INTERVAL * WATER_PER_SECOND * deltaSeconds - waterBuffer));
    }

    @Override
    public void addFluid(@NotNull PylonFluid fluid, double amount) {
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
