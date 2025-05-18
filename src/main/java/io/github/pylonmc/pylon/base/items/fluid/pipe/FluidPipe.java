package io.github.pylonmc.pylon.base.items.fluid.pipe;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.items.fluid.connection.connecting.ConnectingPointNewBlock;
import io.github.pylonmc.pylon.base.items.fluid.connection.connecting.ConnectingPointPipeConnector;
import io.github.pylonmc.pylon.base.items.fluid.connection.connecting.ConnectingPointPipeMarker;
import io.github.pylonmc.pylon.base.items.fluid.connection.connecting.ConnectingPointInteraction;
import io.github.pylonmc.pylon.base.items.fluid.connection.connecting.ConnectingService;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.EntityInteractor;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;


public class FluidPipe extends PylonItem<FluidPipe.Schema> implements EntityInteractor, Interactor {

    public static class Schema extends PylonItemSchema {

        @SuppressWarnings("DataFlowIssue")
        @Getter private final long fluidPerTick = getSettings().get("fluid-per-second", Integer.class) / 20;
        @SuppressWarnings("DataFlowIssue")
        private final int minTemperature = getSettings().get("temperature.min", Integer.class);
        @SuppressWarnings("DataFlowIssue")
        private final int maxTemperature = getSettings().get("temperature.max", Integer.class);
        @Getter private final Material material;

        public Schema(@NotNull NamespacedKey key, @NotNull Function<NamespacedKey, ItemStack> templateSupplier, Material material) {
            super(key, FluidPipe.class, templateSupplier);
            this.material = material;
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
    public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
        return Map.of(
                "fluid_per_second", Component.text(getSchema().fluidPerTick * 20),
                "min_temperature", Component.text(getSchema().minTemperature),
                "max_temperature", Component.text(getSchema().maxTemperature)
        );
    }

    @Override
    public void onUsedToRightClickEntity(@NotNull PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (EntityStorage.get(event.getRightClicked()) instanceof FluidConnectionInteraction interaction) {
            if (!ConnectingService.isConnecting(event.getPlayer())) {
                ConnectingService.startConnection(event.getPlayer(), new ConnectingPointInteraction(interaction), getSchema());
                return;
            }
        }

        if (ConnectingService.isConnecting(event.getPlayer())) {
            UUID segment = ConnectingService.placeConnection(event.getPlayer());
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

        if (event.getClickedBlock() != null
                && event.getAction() == Action.RIGHT_CLICK_BLOCK
                && !ConnectingService.isConnecting(event.getPlayer())
        ) {
            if (BlockStorage.get(event.getClickedBlock()) instanceof FluidPipeConnector connector) {
                if (!connector.getPipe().equals(getSchema())) {
                    event.getPlayer().sendActionBar(Component.translatable("pylon.pylonbase.pipe.not_of_same_type"));
                    return;
                }
                ConnectingPointPipeConnector connectingPoint = new ConnectingPointPipeConnector(connector);
                ConnectingService.startConnection(event.getPlayer(), connectingPoint, getSchema());
                return;
            }

            if (BlockStorage.get(event.getClickedBlock()) instanceof FluidPipeMarker marker) {
                FluidPipeDisplay pipeDisplay = marker.getPipeDisplay();
                Preconditions.checkState(pipeDisplay != null);
                if (!pipeDisplay.getPipe().equals(getSchema())) {
                    event.getPlayer().sendActionBar(Component.translatable("pylon.pylonbase.pipe.not_of_same_type"));
                    return;
                }
                ConnectingPointPipeMarker connectingPoint = new ConnectingPointPipeMarker(marker);
                ConnectingService.startConnection(event.getPlayer(), connectingPoint, getSchema());
                return;
            }

            Block placementBlock = event.getClickedBlock().getRelative(event.getBlockFace());
            if (placementBlock.getType().isAir()) {
                ConnectingPointNewBlock connectingPoint = new ConnectingPointNewBlock(new BlockPosition(placementBlock));
                ConnectingService.startConnection(event.getPlayer(), connectingPoint, getSchema());
                return;
            }
        }

        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)
                && ConnectingService.isConnecting(event.getPlayer())
        ) {
            UUID segment = ConnectingService.placeConnection(event.getPlayer());
            if (segment != null) {
                FluidManager.setFluidPerTick(segment, getSchema().fluidPerTick);
                FluidManager.setFluidPredicate(segment, getSchema().getPredicate());
            }
        }
    }
}
