package io.github.pylonmc.pylon.base.items.fluid.items;

import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidTank extends PylonBlock implements PylonEntityHolderBlock, PylonFluidBlock {

    public static final NamespacedKey FLUID_TANK_WOOD_KEY =  pylonKey("fluid_tank_wood");
    public static final NamespacedKey FLUID_TANK_COPPER_KEY =  pylonKey("fluid_tank_copper");

    private static final NamespacedKey FLUID_AMOUNT_KEY = pylonKey("fluid_amount");
    private static final NamespacedKey FLUID_TYPE_KEY = pylonKey("fluid_type");

    public final double capacity = getSettings().getOrThrow("capacity", Double.class);
    public final long minTemp = getSettings().getOrThrow("temperature.min", Integer.class);
    public final long maxTemp = getSettings().getOrThrow("temperature.max", Integer.class);

    private final Map<String, UUID> entities;
    private double fluidAmount;
    private @Nullable PylonFluid fluidType;

    @SuppressWarnings("unused")
    public FluidTank(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        ItemDisplay fluidDisplay = new ItemDisplayBuilder().build(block.getLocation().toCenterLocation());
        FluidTankEntity fluidTankEntity = new FluidTankEntity(fluidDisplay);
        EntityStorage.add(fluidTankEntity);

        Player player = null;
        if (context instanceof BlockCreateContext.PlayerPlace ctx) {
            player = ctx.getPlayer();
        }

        FluidConnectionPoint input = new FluidConnectionPoint(getBlock(), "input", FluidConnectionPoint.Type.INPUT);
        FluidConnectionPoint output = new FluidConnectionPoint(getBlock(), "output", FluidConnectionPoint.Type.OUTPUT);

        entities = Map.of(
                "fluid", fluidTankEntity.getUuid(),
                "input", FluidConnectionInteraction.make(player, input, BlockFace.UP, 0.5F).getUuid(),
                "output", FluidConnectionInteraction.make(player, output, BlockFace.DOWN, 0.5F).getUuid()
        );
    }

    @SuppressWarnings("unused")
    public FluidTank(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        entities = loadHeldEntities(pdc);
        fluidAmount = pdc.get(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
        fluidType = pdc.get(FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        saveHeldEntities(pdc);
        pdc.set(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, fluidAmount);
        PdcUtils.setNullable(pdc, FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID, fluidType);
    }

    @Override
    public @NotNull Map<String, UUID> getHeldEntities() {
        return entities;
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return fluidType == null
                ? Map.of()
                : Map.of(fluidType, fluidAmount);
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        // If no fluid contained, allow any fluid to be added
        if (fluidType == null) {
            return PylonRegistry.FLUIDS.getValues()
                    .stream()
                    .filter(fluid -> {
                        FluidTemperature temperature = fluid.getTag(FluidTemperature.class);
                        return temperature != null && temperature.getValue() > minTemp && temperature.getValue() < maxTemp;
                    })
                    .collect(Collectors.toMap(Function.identity(), key -> capacity));
        }

        // If tank full, don't request anything
        if (fluidAmount >= capacity) {
            return Map.of();
        }

        // Otherwise, only allow more of the stored fluid to be added
        return Map.of(fluidType, capacity - fluidAmount);
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        if (!fluid.equals(fluidType)) {
            fluidType = fluid;
            ItemDisplay display = getFluidDisplay();
            if (display != null) {
                display.setItemStack(new ItemStack(fluid.getMaterial()));
            }
        }
        fluidAmount += amount;
        updateFluidDisplay();
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        fluidAmount -= amount;
        if (fluidAmount == 0) {
            fluidType = null;
            ItemDisplay display = getFluidDisplay();
            if (display != null) {
                display.setItemStack(null);
            }
        }
        updateFluidDisplay();
    }

    public void updateFluidDisplay() {
        float scale = (float) (0.9F * fluidAmount / capacity);
        ItemDisplay display = getFluidDisplay();
        if (display != null) {
            display.setTransformationMatrix(new TransformBuilder()
                    .translate(0.0, -0.45 + scale / 2, 0.0)
                    .scale(0.9, scale, 0.9)
                    .buildForItemDisplay());
        }
    }

    private @Nullable ItemDisplay getFluidDisplay() {
        FluidTankEntity fluidTankEntity = getHeldEntity(FluidTankEntity.class, "fluid");
        if (fluidTankEntity == null) {
            return null;
        }
        return fluidTankEntity.getEntity();
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(
                getName(),
                // TODO add fluid name once fluids have names
                Map.of(
                        "amount", Component.text(Math.round(fluidAmount)),
                        "capacity", Component.text(Math.round(capacity))
                )
        );
    }


    public static class FluidTankEntity extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("fluid_tank_entity");

        public FluidTankEntity(@NotNull ItemDisplay entity) {
            super(KEY, entity);
        }
    }
}
