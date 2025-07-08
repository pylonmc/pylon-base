package io.github.pylonmc.pylon.base.content.machines.fluid;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeConnector;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeDisplay;
import io.github.pylonmc.pylon.base.fluid.pipe.FluidPipeMarker;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.fluid.pipe.connection.connecting.*;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.item.base.PylonItemEntityInteractor;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@SuppressWarnings("UnstableApiUsage")
public class FluidPipe extends PylonItem implements PylonItemEntityInteractor, PylonInteractor {

    public final Material material = getMaterial(getKey());
    public final double fluidPerSecond = getSettings().getOrThrow("fluid-per-second", Double.class);

    @SuppressWarnings("unchecked")
    public final List<FluidTemperature> allowedFluids = ((List<String>) getSettings().getOrThrow("allow-fluids", List.class)).stream()
            .map(s -> FluidTemperature.valueOf(s.toUpperCase(Locale.ROOT)))
            .collect(Collectors.toList());

    public FluidPipe(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
        return Map.of(
                "fluid_per_second", UnitFormat.MILLIBUCKETS_PER_SECOND.format(fluidPerSecond),
                "fluids", Component.join(
                        JoinConfiguration.separator(Component.text(", ")),
                        allowedFluids.stream()
                                .map(FluidTemperature::getValueText)
                                .collect(Collectors.toList())
                )
        );
    }

    @Override
    public void onUsedToRightClickEntity(@NotNull PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (EntityStorage.get(event.getRightClicked()) instanceof FluidConnectionInteraction interaction) {
            if (!ConnectingService.isConnecting(event.getPlayer())) {
                ConnectingService.startConnection(event.getPlayer(), new ConnectingPointInteraction(interaction), this);
                return;
            }
        }

        if (ConnectingService.isConnecting(event.getPlayer())) {
            UUID segment = ConnectingService.placeConnection(event.getPlayer());
            if (segment != null) {
                FluidManager.setFluidPerSecond(segment, fluidPerSecond);
                FluidManager.setFluidPredicate(segment, getPredicate());
            }
        }
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Action action = event.getAction();
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (block != null && action == Action.RIGHT_CLICK_BLOCK && !ConnectingService.isConnecting(player)) {
            if (!tryStartConnection(player, block)) {
                tryStartConnection(player, block.getRelative(event.getBlockFace()));
            }
        }

        if ((action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) && ConnectingService.isConnecting(player)) {
            UUID segment = ConnectingService.placeConnection(player);
            if (segment != null) {
                FluidManager.setFluidPerSecond(segment, fluidPerSecond);
                FluidManager.setFluidPredicate(segment, getPredicate());
            }
        }
    }

    public static Material getMaterial(@NotNull NamespacedKey key) {
        return Map.of(
                BaseKeys.FLUID_PIPE_WOOD, Material.BROWN_TERRACOTTA,
                BaseKeys.FLUID_PIPE_COPPER, Material.ORANGE_TERRACOTTA,
                BaseKeys.FLUID_PIPE_OBSIDIAN, Material.BLACK_TERRACOTTA
        ).get(key);
    }

    public Predicate<PylonFluid> getPredicate() {
        return fluid -> fluid.hasTag(FluidTemperature.class) && allowedFluids.contains(fluid.getTag(FluidTemperature.class));
    }

    private boolean tryStartConnection(@NotNull Player player, @NotNull Block block) {
        if (BlockStorage.get(block) instanceof FluidPipeConnector connector) {
            if (!connector.getPipe().equals(this)) {
                player.sendActionBar(Component.translatable("pylon.pylonbase.message.pipe.not_of_same_type"));
                return true;
            }
            ConnectingPointPipeConnector connectingPoint = new ConnectingPointPipeConnector(connector);
            ConnectingService.startConnection(player, connectingPoint, this);
            return true;
        }

        if (BlockStorage.get(block) instanceof FluidPipeMarker marker) {
            FluidPipeDisplay pipeDisplay = marker.getPipeDisplay();
            Preconditions.checkState(pipeDisplay != null);
            if (!pipeDisplay.getPipe().equals(this)) {
                player.sendActionBar(Component.translatable("pylon.pylonbase.message.pipe.not_of_same_type"));
                return true;
            }
            ConnectingPointPipeMarker connectingPoint = new ConnectingPointPipeMarker(marker);
            ConnectingService.startConnection(player, connectingPoint, this);
            return true;
        }

        if (block.getType().isAir()) {
            ConnectingPointNewBlock connectingPoint = new ConnectingPointNewBlock(new BlockPosition(block));
            ConnectingService.startConnection(player, connectingPoint, this);
            return true;
        }

        return false;
    }
}
