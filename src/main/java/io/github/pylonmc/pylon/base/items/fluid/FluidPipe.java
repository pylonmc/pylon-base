package io.github.pylonmc.pylon.base.items.fluid;

import io.github.pylonmc.pylon.base.misc.PipeConnectorService;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.EntityInteractor;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;


public class FluidPipe extends PylonItem<FluidPipe.Schema> implements EntityInteractor {

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
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!(EntityStorage.get(event.getRightClicked()) instanceof FluidConnectionInteraction interaction)) {
            return;
        }

        if (PipeConnectorService.isConnecting(event.getPlayer())) {
            PipeConnectorService.finishConnection(event.getPlayer(), interaction.getPoint());
            FluidManager.setFluidPerTick(interaction.getPoint().getSegment(), getSchema().fluidPerTick);
            FluidManager.setFluidPredicate(interaction.getPoint().getSegment(), getSchema().getPredicate());
        } else {
            PipeConnectorService.startConnection(event.getPlayer(), interaction.getPoint(), interaction.getFace());
        }
    }
}
