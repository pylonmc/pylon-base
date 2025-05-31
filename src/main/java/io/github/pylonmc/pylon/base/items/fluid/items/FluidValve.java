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

    private static final Material MAIN_MATERIAL = Material.LIGHT_GRAY_CONCRETE;
    private static final int BRIGHTNESS_OFF = 6;
    private static final int BRIGHTNESS_ON = 13;

    private final Map<String, UUID> entities;
    private boolean enabled;

    @SuppressWarnings("unused")
    public FluidValve(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        Preconditions.checkState(context instanceof BlockCreateContext.PlayerPlace, "Fluid valve can only be placed by a player");
        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        FluidConnectionPoint northPoint = new FluidConnectionPoint(getBlock(), "input", FluidConnectionPoint.Type.CONNECTOR);
        FluidConnectionPoint southPoint = new FluidConnectionPoint(getBlock(), "output", FluidConnectionPoint.Type.CONNECTOR);

        entities = Map.of(
                "main", FluidValveDisplay.make(block, player).getUuid(),
                "north", FluidConnectionInteraction.make(player, northPoint, BlockFace.NORTH, 0.25F).getUuid(),
                "south", FluidConnectionInteraction.make(player, southPoint, BlockFace.SOUTH, 0.25F).getUuid()
        );
        enabled = false;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidValve(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        entities = loadHeldEntities(pdc);
        enabled = pdc.get(ENABLED_KEY, PylonSerializers.BOOLEAN);

        if (enabled) {
            FluidManager.connect(getNorthPoint(), getSouthPoint());
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

        enabled = !enabled;

        getMainDisplay().setEnabled(enabled);

        if (enabled) {
            FluidManager.connect(getNorthPoint(), getSouthPoint());
        } else {
            FluidManager.disconnect(getNorthPoint(), getSouthPoint());
        }
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        // TODO translation
        return new WailaConfig(getKey(), Map.of("status", Component.text(enabled ? "enabled" : "disabled")));
    }

    private @NotNull FluidValveDisplay getMainDisplay() {
        return Objects.requireNonNull(getHeldEntity(FluidValveDisplay.class, "main"));
    }

    private @NotNull FluidConnectionPoint getNorthPoint() {
        //noinspection DataFlowIssue
        return getHeldEntity(FluidConnectionInteraction.class, "north").getPoint();
    }

    private @NotNull FluidConnectionPoint getSouthPoint() {
        //noinspection DataFlowIssue
        return getHeldEntity(FluidConnectionInteraction.class, "south").getPoint();
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
                            .lookAlong(PylonUtils.rotateToPlayerFacing(player, BlockFace.NORTH).getDirection().toVector3d())
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
