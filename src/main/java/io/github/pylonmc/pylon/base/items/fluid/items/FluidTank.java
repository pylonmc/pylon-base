package io.github.pylonmc.pylon.base.items.fluid.items;

import io.github.pylonmc.pylon.base.PylonEntities;
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
import org.jspecify.annotations.NullMarked;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@NullMarked
public class FluidTank extends PylonBlock<FluidTank.Schema> implements PylonEntityHolderBlock, PylonFluidBlock {

    public static class Schema extends PylonBlockSchema {

        @Getter private final long capacity;

        public Schema(NamespacedKey key, Material material, long capacity) {
            super(key, material, FluidTank.class);
            this.capacity = capacity;
        }
    }

    private static final NamespacedKey FLUID_AMOUNT_KEY = KeyUtils.pylonKey("fluid_amount");
    private static final NamespacedKey FLUID_TYPE_KEY = KeyUtils.pylonKey("fluid_type");

    private long fluidAmount;
    private @Nullable PylonFluid fluidType;

    @SuppressWarnings("unused")
    public FluidTank(Schema schema, Block block, BlockCreateContext context) {
        super(schema, block);
    }

    @SuppressWarnings("unused")
    public FluidTank(Schema schema, Block block, PersistentDataContainer pdc) {
        super(schema, block);

        fluidAmount = pdc.get(FLUID_AMOUNT_KEY, PylonSerializers.LONG);
        fluidType = pdc.get(FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID);
    }

    @Override
    public void write(PersistentDataContainer pdc) {
        pdc.set(FLUID_AMOUNT_KEY, PylonSerializers.LONG, fluidAmount);
        PdcUtils.setNullable(pdc, FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID, fluidType);
    }

    @Override
    public Map<String, UUID> createEntities(BlockCreateContext context) {
        ItemDisplay fluidDisplay = new ItemDisplayBuilder().build(getBlock().getLocation().toCenterLocation());
        FluidTankEntity fluidTankEntity = new FluidTankEntity(PylonEntities.FLUID_TANK_DISPLAY, fluidDisplay);
        EntityStorage.add(fluidTankEntity);

        Player player = null;
        if (context instanceof BlockCreateContext.PlayerPlace ctx) {
            player = ctx.getPlayer();
        }

        FluidConnectionPoint input = new FluidConnectionPoint(getBlock(), "input", FluidConnectionPoint.Type.INPUT);
        FluidConnectionPoint output = new FluidConnectionPoint(getBlock(), "output", FluidConnectionPoint.Type.OUTPUT);

        return Map.of(
                "fluid", fluidTankEntity.getUuid(),
                "input", FluidConnectionInteraction.make(player, input, BlockFace.UP, 0.5F).getUuid(),
                "output", FluidConnectionInteraction.make(player, output, BlockFace.DOWN, 0.5F).getUuid()
        );
    }

    @Override
    public Map<PylonFluid, Long> getSuppliedFluids(String connectionPoint) {
        return fluidType == null
                ? Map.of()
                : Map.of(fluidType, fluidAmount);
    }

    @Override
    public Map<PylonFluid, Long> getRequestedFluids(String connectionPoint) {
        // If no fluid contained, allow any fluid to be added
        if (fluidType == null) {
            return PylonRegistry.FLUIDS.getValues()
                    .stream()
                    .collect(Collectors.toMap(Function.identity(), key -> getSchema().capacity));
        }

        // If tank full, don't request anything
        if (fluidAmount >= getSchema().capacity) {
            return Map.of();
        }

        // Otherwise, only allow more of the stored fluid to be added
        return Map.of(fluidType, getSchema().capacity - fluidAmount);
    }

    @Override
    public void addFluid(String connectionPoint, PylonFluid fluid, long amount) {
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
    public void removeFluid(String connectionPoint, PylonFluid fluid, long amount) {
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
        float scale = 0.9F * fluidAmount / getSchema().capacity;
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

        public FluidTankEntity(PylonEntitySchema schema, ItemDisplay entity) {
            super(schema, entity);
        }
    }
}