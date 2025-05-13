package io.github.pylonmc.pylon.base.items.fluid;

import io.github.pylonmc.pylon.base.misc.PipeConnectorService;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockInteractor;
import io.github.pylonmc.pylon.core.item.base.EntityInteractor;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;


public class FluidPipe extends PylonItem<FluidPipe.Schema> implements EntityInteractor, Interactor {

    public static class Schema extends PylonItemSchema {

        @Getter private final long fluidPerTick = getSettings().get("fluid-per-second", Integer.class) / 20;
        private final int minTemperature = getSettings().get("temperature.min", Integer.class);
        private final int maxTemperature = getSettings().get("temperature.max", Integer.class);

        public Schema(@NotNull NamespacedKey key, @NotNull Function<NamespacedKey, ItemStack> templateSupplier) {
            super(key, FluidPipe.class, templateSupplier);
        }

        public Predicate<PylonFluid> getPredicate() {
            return fluid -> {
                FluidTemperature temperatureTag = fluid.getTag(FluidTemperature.class);
                if (temperatureTag == null) {
                    return false;
                }
                int temperature = temperatureTag.getValue();
                return temperature > minTemperature && temperature < maxTemperature;
            };
        }
    }

    public FluidPipe(@NotNull Schema schema, @NotNull ItemStack stack) {
        super(schema, stack);
    }

    @Override
    public void onUsedToRightClickEntity(@NotNull PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || PipeConnectorService.isConnecting(event.getPlayer())) {
            return;
        }

        if (EntityStorage.get(event.getRightClicked()) instanceof FluidConnectionInteraction interaction) {
            if (!PipeConnectorService.isConnecting(event.getPlayer())) {
                PipeConnectorService.startConnection(event.getPlayer(), interaction);
                return;
            }
        }

        if (PipeConnectorService.isConnecting(event.getPlayer())) {
            UUID segment = PipeConnectorService.finishConnection(event.getPlayer());
            if (segment != null) {
                FluidManager.setFluidPerTick(segment, getSchema().fluidPerTick);
                FluidManager.setFluidPredicate(segment, getSchema().getPredicate());
            }
        }
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getClickedBlock() != null && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!PipeConnectorService.isConnecting(event.getPlayer())) {
                if (BlockStorage.get(event.getClickedBlock()) instanceof FluidConnector connector) {
                    PipeConnectorService.startConnection(event.getPlayer(), connector.getFluidConnectionInteraction());
                    return;
                }
            }
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (PipeConnectorService.isConnecting(event.getPlayer())) {
                UUID segment = PipeConnectorService.finishConnection(event.getPlayer());
                if (segment != null) {
                    FluidManager.setFluidPerTick(segment, getSchema().fluidPerTick);
                    FluidManager.setFluidPredicate(segment, getSchema().getPredicate());
                }
            }
        }
    }
}
