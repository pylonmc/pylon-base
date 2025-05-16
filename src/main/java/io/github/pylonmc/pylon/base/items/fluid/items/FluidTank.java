package io.github.pylonmc.pylon.base.items.fluid.items;

import io.github.pylonmc.pylon.base.PylonEntities;
import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import lombok.Getter;
import org.bukkit.Material;
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


public class FluidTank extends PylonBlock<FluidTank.Schema> implements PylonEntityHolderBlock, PylonFluidBlock {

    public static class Schema extends PylonBlockSchema {

        @Getter private final long capacity;
        @Getter private final long flowRatePerSecond;

        public Schema(@NotNull NamespacedKey key, @NotNull Material material, long capacity, long flowRatePerSecond) {
            super(key, material, FluidTank.class);
            this.capacity = capacity;
            this.flowRatePerSecond = flowRatePerSecond;
        }
    }

    private static final NamespacedKey FLUID_AMOUNT_KEY = KeyUtils.pylonKey("fluid_amount");
    private static final NamespacedKey FLUID_TYPE_KEY = KeyUtils.pylonKey("fluid_type");

    private final Map<String, UUID> entities;
    private long fluidAmount;
    private @Nullable PylonFluid fluidType;

    @SuppressWarnings("unused")
    public FluidTank(@NotNull Schema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
        super(schema, block);

        FluidConnectionPoint input = new FluidConnectionPoint(getBlock(), "input", FluidConnectionPoint.Type.INPUT);
        FluidConnectionPoint output = new FluidConnectionPoint(getBlock(), "output", FluidConnectionPoint.Type.OUTPUT);

        ItemDisplay fluidDisplay = new ItemDisplayBuilder()
                .material(Material.ORANGE_CONCRETE) // TODO remove
                .transformation(new TransformBuilder() // TODO remove
                        .translate(0.0, -0.5 + 0.5 / 2, 0.0) // TODO remove
                        .scale(0.9, 0.5, 0.9) // TODO remove
                        .buildForItemDisplay()) // TODO remove
                .build(block.getLocation().toCenterLocation());
        FluidTankEntity fluidTankEntity = new FluidTankEntity(PylonEntities.FLUID_TANK_DISPLAY, fluidDisplay);
        EntityStorage.add(fluidTankEntity);

        Player player = null;
        if (context instanceof BlockCreateContext.PlayerPlace ctx) {
            player = ctx.getPlayer();
        }

        entities = Map.of(
                "fluid", fluidTankEntity.getUuid(),
                "input", FluidConnectionInteraction.make(player, input, BlockFace.UP, 0.5F).getUuid(),
                "output", FluidConnectionInteraction.make(player, output, BlockFace.NORTH, 0.5F).getUuid()
        );
        fluidAmount = getSchema().capacity / 2;
        fluidType = PylonFluids.LAVA;
    }

    @SuppressWarnings("unused")
    public FluidTank(@NotNull Schema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(schema, block);

        entities = loadHeldEntities(pdc);
        fluidAmount = pdc.get(FLUID_AMOUNT_KEY, PylonSerializers.LONG);
        fluidType = pdc.get(FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        saveHeldEntities(pdc);
        pdc.set(FLUID_AMOUNT_KEY, PylonSerializers.LONG, fluidAmount);
        PdcUtils.setNullable(pdc, FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID, fluidType);
    }

    @Override
    public @NotNull Map<String, UUID> getHeldEntities() {
        return entities;
    }

    @Override
    public @NotNull Map<PylonFluid, Long> getSuppliedFluids(@NotNull String connectionPoint) {
        return fluidType == null
                ? Map.of()
                : Map.of(fluidType, fluidAmount);
    }

    @Override
    public @NotNull Map<PylonFluid, Long> getRequestedFluids(@NotNull String connectionPoint) {
        // If no fluid contained, allow any fluid to be added
        if (fluidType == null) {
            return PylonRegistry.FLUIDS.getValues()
                    .stream()
                    .collect(Collectors.toMap(Function.identity(), key -> getSchema().flowRatePerSecond / 20));
        }

        // If tank full, don't request anything
        if (fluidAmount >= getSchema().capacity) {
            return Map.of();
        }

        // Otherwise, only allow more of the stored fluid to be added
        return Map.of(fluidType, Math.min(getSchema().capacity - fluidAmount, getSchema().flowRatePerSecond / 20));
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, long amount) {
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
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, long amount) {
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
        float scale = 0.9F * (float) fluidAmount / getSchema().capacity;
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

    public static class FluidTankEntity extends PylonEntity<PylonEntitySchema, ItemDisplay> {

        public FluidTankEntity(@NotNull PylonEntitySchema schema, @NotNull ItemDisplay entity) {
            super(schema, entity);
        }
    }
}