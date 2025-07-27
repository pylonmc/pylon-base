package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.tools.WateringCan;
import io.github.pylonmc.pylon.base.content.tools.WateringSettings;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.event.PrePylonBlockPlaceEvent;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class Sprinkler extends PylonBlock
        implements PylonFluidBufferBlock, PylonTickingBlock, PylonEntityHolderBlock {

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

    private static final Config settings = Settings.get(BaseKeys.SPRINKLER);
    public static final WateringSettings SETTINGS = WateringSettings.fromConfig(settings);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", Integer.class);
    public static final double WATER_PER_SECOND = settings.getOrThrow("water-per-second", Integer.class);

    @SuppressWarnings("unused")
    public Sprinkler(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.UP, -0.15F));
        createFluidBuffer(BaseFluids.WATER, WATER_PER_SECOND * 5, true, false);
    }

    @SuppressWarnings("unused")
    public Sprinkler(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return TICK_INTERVAL;
    }

    @Override
    public void tick(double deltaSeconds) {
        if (fluidAmount(BaseFluids.WATER) > WATER_PER_SECOND * deltaSeconds) {
            WateringCan.water(getBlock(), SETTINGS);
            removeFluid(BaseFluids.WATER, WATER_PER_SECOND * deltaSeconds);
        }
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
