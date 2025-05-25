package io.github.pylonmc.pylon.base.items.fluid;

import com.google.common.base.Preconditions;
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
import io.github.pylonmc.pylon.core.item.base.EntityInteractor;
import io.github.pylonmc.pylon.core.item.base.Interactor;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
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

import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidPipe extends PylonItem implements EntityInteractor, Interactor {

    public static final NamespacedKey PIPE_WOOD_KEY = pylonKey("fluid_pipe_wooden");
    public static final NamespacedKey PIPE_COPPER_KEY = pylonKey("fluid_pipe_copper");
    public static final NamespacedKey PIPE_OBSIDIAN_KEY = pylonKey("fluid_pipe_obsidian");

    public static final ItemStack PIPE_WOOD_STACK = ItemStackBuilder.pylonItem(Material.CLAY_BALL, PIPE_WOOD_KEY)
            .set(DataComponentTypes.ITEM_MODEL, getMaterial(PIPE_WOOD_KEY).getKey())
            .build();
    public static final ItemStack PIPE_COPPER_STACK = ItemStackBuilder.pylonItem(Material.CLAY_BALL, PIPE_COPPER_KEY)
            .set(DataComponentTypes.ITEM_MODEL, getMaterial(PIPE_COPPER_KEY).getKey())
            .build();
    public static final ItemStack PIPE_OBSIDIAN_STACK = ItemStackBuilder.pylonItem(Material.CLAY_BALL, PIPE_OBSIDIAN_KEY)
            .set(DataComponentTypes.ITEM_MODEL, getMaterial(PIPE_OBSIDIAN_KEY).getKey())
            .build();

    public final Material material = getMaterial(getKey());
    public final double fluidPerSecond = getSettings().getOrThrow("fluid-per-second", Double.class);
    public final int minTemperature = getSettings().getOrThrow("temperature.min", Integer.class);
    public final int maxTemperature = getSettings().getOrThrow("temperature.max", Integer.class);

    public FluidPipe(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Component> getPlaceholders() {
        return Map.of(
                "fluid_per_second", Component.text(fluidPerSecond),
                "min_temperature", Component.text(minTemperature),
                "max_temperature", Component.text(maxTemperature)
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

    private static Material getMaterial(@NotNull NamespacedKey key) {
        return Map.of(
                PIPE_WOOD_KEY, Material.BROWN_TERRACOTTA,
                PIPE_COPPER_KEY, Material.ORANGE_TERRACOTTA,
                PIPE_OBSIDIAN_KEY, Material.BLACK_TERRACOTTA
        ).get(key);
    }

    public Predicate<PylonFluid> getPredicate() {
        return fluid -> {
            FluidTemperature temperatureTag = fluid.getTag(FluidTemperature.class);
            int temperature = temperatureTag.getTemperature();
            return temperature > minTemperature && temperature < maxTemperature;
        };
    }

    private boolean tryStartConnection(@NotNull Player player, @NotNull Block block) {
        if (BlockStorage.get(block) instanceof FluidPipeConnector connector) {
            if (!connector.getPipe().equals(this)) {
                player.sendActionBar(Component.translatable("pylon.pylonbase.pipe.not_of_same_type"));
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
                player.sendActionBar(Component.translatable("pylon.pylonbase.pipe.not_of_same_type"));
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
