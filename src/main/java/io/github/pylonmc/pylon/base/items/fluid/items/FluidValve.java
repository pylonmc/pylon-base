package io.github.pylonmc.pylon.base.items.fluid.items;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.FluidManager;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidValve extends PylonBlock implements PylonEntityHolderBlock, PylonFluidBlock, PylonInteractableBlock {

    public static final NamespacedKey KEY = pylonKey("fluid_valve");

    public static final NamespacedKey ENABLED_KEY = pylonKey("enabled");

    private static final Material MAIN_MATERIAL = Material.WHITE_TERRACOTTA;
    private static final int BRIGHTNESS_OFF = 6;
    private static final int BRIGHTNESS_ON = 13;

    private final Map<String, UUID> entities;
    private boolean enabled;

    @SuppressWarnings("unused")
    public FluidValve(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid valve can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        FluidConnectionPoint eastPoint = new FluidConnectionPoint(getBlock(), "east", FluidConnectionPoint.Type.CONNECTOR);
        FluidConnectionPoint westPoint = new FluidConnectionPoint(getBlock(), "west", FluidConnectionPoint.Type.CONNECTOR);

        entities = Map.of(
                "main", FluidValveDisplay.make(block, player).getUuid(),
                "east", FluidConnectionInteraction.make(player, eastPoint, BlockFace.EAST, 0.25F).getUuid(),
                "west", FluidConnectionInteraction.make(player, westPoint, BlockFace.WEST, 0.25F).getUuid()
        );
        enabled = false;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidValve(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        entities = loadHeldEntities(pdc);
        enabled = pdc.get(ENABLED_KEY, PylonSerializers.BOOLEAN);

        if (enabled) {
            // connect east and west points when they load
            EntityStorage.whenEntityLoads(entities.get("east"), FluidConnectionInteraction.class, east -> {
                EntityStorage.whenEntityLoads(entities.get("west"), FluidConnectionInteraction.class, west -> {
                    FluidManager.connect(getEastPoint(), getWestPoint());
                });
            });
        }
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        saveHeldEntities(pdc);
        pdc.set(ENABLED_KEY, PylonSerializers.BOOLEAN, enabled);
    }

    @Override
    public @NotNull Map<String, UUID> getHeldEntities() {
        return entities;
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        event.setUseItemInHand(Event.Result.DENY);

        enabled = !enabled;

        getMainDisplay().setEnabled(enabled);

        if (enabled) {
            FluidManager.connect(getEastPoint(), getWestPoint());
        } else {
            FluidManager.disconnect(getEastPoint(), getWestPoint());
        }
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(getName(), Map.of(
                "status", Component.translatable("pylon.pylonbase.message.valve." + (enabled ? "enabled" : "disabled"))
        ));
    }

    private @NotNull FluidValveDisplay getMainDisplay() {
        return Objects.requireNonNull(getHeldEntity(FluidValveDisplay.class, "main"));
    }

    private @NotNull FluidConnectionPoint getEastPoint() {
        //noinspection DataFlowIssue
        return getHeldEntity(FluidConnectionInteraction.class, "east").getPoint();
    }

    private @NotNull FluidConnectionPoint getWestPoint() {
        //noinspection DataFlowIssue
        return getHeldEntity(FluidConnectionInteraction.class, "west").getPoint();
    }

    public static class FluidValveDisplay extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("fluid_valve_display");

        @SuppressWarnings("unused")
        public FluidValveDisplay(@NotNull ItemDisplay entity) {
            super(entity);
        }

        public FluidValveDisplay(@NotNull Block block, @NotNull Player player) {
            super(KEY, new ItemDisplayBuilder()
                    .material(MAIN_MATERIAL)
                    .brightness(BRIGHTNESS_OFF)
                    .transformation(new TransformBuilder()
                            .lookAlong(PylonUtils.rotateToPlayerFacing(player, BlockFace.EAST).getDirection().toVector3d())
                            .scale(0.25, 0.25, 0.5)
                    )
                    .build(block.getLocation().toCenterLocation())
            );
        }

        public static @NotNull FluidValveDisplay make(@NotNull Block block, @NotNull Player player) {
            FluidValveDisplay display = new FluidValveDisplay(block, player);
            EntityStorage.add(display);
            return display;
        }

        public void setEnabled(boolean enabled) {
            getEntity().setBrightness(new Display.Brightness(0, enabled ? BRIGHTNESS_ON : BRIGHTNESS_OFF));
        }
    }
}
