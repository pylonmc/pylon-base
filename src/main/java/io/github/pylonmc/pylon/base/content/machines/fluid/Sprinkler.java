package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.tools.WateringCan;
import io.github.pylonmc.pylon.base.content.tools.WateringSettings;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.rebar.block.BlockStorage;
import io.github.pylonmc.rebar.block.PylonBlock;
import io.github.pylonmc.rebar.block.base.PylonFlowerPot;
import io.github.pylonmc.rebar.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.rebar.block.base.PylonTickingBlock;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.config.Config;
import io.github.pylonmc.rebar.config.PylonConfig;
import io.github.pylonmc.rebar.config.Settings;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.event.PrePylonBlockPlaceEvent;
import io.github.pylonmc.rebar.fluid.FluidPointType;
import io.github.pylonmc.rebar.i18n.PylonArgument;
import io.github.pylonmc.rebar.item.PylonItem;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import io.github.pylonmc.rebar.waila.WailaDisplay;
import io.papermc.paper.event.player.PlayerFlowerPotManipulateEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class Sprinkler extends PylonBlock
        implements PylonFluidBufferBlock, PylonTickingBlock, PylonFlowerPot {

    private static final Config settings = Settings.get(BaseKeys.SPRINKLER);
    public static final WateringSettings SETTINGS = WateringSettings.fromConfig(settings);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", ConfigAdapter.INT);
    public static final double WATER_PER_SECOND = settings.getOrThrow("water-per-second", ConfigAdapter.INT);
    public static final double BUFFER = settings.getOrThrow("buffer", ConfigAdapter.INT);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("range", UnitFormat.BLOCKS.format(SETTINGS.horizontalRange())),
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(BUFFER)),
                    PylonArgument.of("water_consumption", UnitFormat.MILLIBUCKETS_PER_SECOND.format(WATER_PER_SECOND))
            );
        }
    }

    @SuppressWarnings("unused")
    public Sprinkler(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        setTickInterval(TICK_INTERVAL);
        createFluidPoint(FluidPointType.INPUT, BlockFace.UP, -0.15F);
        createFluidBuffer(BaseFluids.WATER, BUFFER, true, false);
    }

    @SuppressWarnings("unused")
    public Sprinkler(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public void onFlowerPotManipulated(@NotNull PlayerFlowerPotManipulateEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void tick() {
        if (fluidAmount(BaseFluids.WATER) > WATER_PER_SECOND * PylonConfig.FLUID_TICK_INTERVAL / 20.0) {
            WateringCan.water(getBlock(), SETTINGS);
            removeFluid(BaseFluids.WATER, WATER_PER_SECOND * PylonConfig.FLUID_TICK_INTERVAL / 20.0);
        }
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("bars", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.WATER),
                        fluidCapacity(BaseFluids.WATER),
                        20,
                        NamedTextColor.BLUE
                ))
        ));
    }

    public static class SprinklerPlaceListener implements Listener {
        @EventHandler
        private static void handle(@NotNull PrePylonBlockPlaceEvent event) {
            if (event.getBlockSchema().getKey() != BaseKeys.SPRINKLER) {
                return;
            }

            int horizontalRadiusToCheck = 2 * SETTINGS.horizontalRange();
            int verticalRadiusToCheck = 2 * SETTINGS.verticalRange();
            for (int x = -horizontalRadiusToCheck; x <= horizontalRadiusToCheck; x++) {
                for (int z = -horizontalRadiusToCheck; z <= horizontalRadiusToCheck; z++) {
                    for (int y = -verticalRadiusToCheck; y <= verticalRadiusToCheck; y++) {
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
                        return;
                    }
                }
            }
        }
    }
}
