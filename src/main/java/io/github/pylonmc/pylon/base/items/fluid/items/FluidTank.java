package io.github.pylonmc.pylon.base.items.fluid.items;

import io.github.pylonmc.pylon.base.PylonEntities;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
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

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class FluidTank {

    public static class FluidTankItem extends PylonItem<FluidTankItem.Schema> {

        public static class Schema extends PylonItemSchema implements BlockPlacer {

            private final FluidTankBlock.Schema block;

            public Schema(
                    @NotNull NamespacedKey key,
                    @NotNull Function<NamespacedKey, ItemStack> templateSupplier,
                    @NotNull FluidTankBlock.Schema block
            ) {
                super(key, FluidTankItem.class, templateSupplier);
                this.block = block;
            }

            @Override
            public @NotNull PylonBlockSchema getBlockSchema() {
                return block;
            }
        }

        public FluidTankItem(@NotNull Schema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }

        @Override
        public @NotNull Map<String, Component> getPlaceholders() {
            return Map.of(
                    "capacity", Component.text(getSchema().block.capacity),
                    "min_temperature", Component.text(getSchema().block.minTemp),
                    "max_temperature", Component.text(getSchema().block.maxTemp)
            );
        }
    }

    public static class FluidTankBlock extends PylonBlock<FluidTankBlock.Schema> implements PylonEntityHolderBlock, PylonFluidBlock {

        public static class Schema extends PylonBlockSchema {

            @Getter private final long capacity = getSettings().get("capacity", Integer.class);
            @Getter private final long minTemp = getSettings().get("temperature.min", Integer.class);
            @Getter private final long maxTemp = getSettings().get("temperature.max", Integer.class);

            public Schema(@NotNull NamespacedKey key, @NotNull Material material) {
                super(key, material, FluidTankBlock.class);
            }
        }

        private static final NamespacedKey FLUID_AMOUNT_KEY = pylonKey("fluid_amount");
        private static final NamespacedKey FLUID_TYPE_KEY = pylonKey("fluid_type");

        private final Map<String, UUID> entities;
        private long fluidAmount;
        private @Nullable PylonFluid fluidType;

        @SuppressWarnings("unused")
        public FluidTankBlock(@NotNull Schema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
            super(schema, block);

            ItemDisplay fluidDisplay = new ItemDisplayBuilder().build(block.getLocation().toCenterLocation());
            FluidTankEntity fluidTankEntity = new FluidTankEntity(PylonEntities.FLUID_TANK_DISPLAY, fluidDisplay);
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
        public FluidTankBlock(@NotNull Schema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
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
                        .filter(fluid -> fluid.hasTag(FluidTemperature.class))
                        .filter(fluid -> fluid.getTag(FluidTemperature.class).getValue() > getSchema().minTemp
                                && fluid.getTag(FluidTemperature.class).getValue() < getSchema().maxTemp)
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

        @Override
        public @NotNull WailaConfig getWaila(@NotNull Player player) {
            return new WailaConfig(
                    getName(),
                    // TODO add fluid name once fluids have names
                    Map.of(
                            "amount", Component.text(fluidAmount),
                            "capacity", Component.text(getSchema().capacity)
                    )
            );
        }
    }

    public static class FluidTankEntity extends PylonEntity<PylonEntitySchema, ItemDisplay> {

        public FluidTankEntity(@NotNull PylonEntitySchema schema, @NotNull ItemDisplay entity) {
            super(schema, entity);
        }
    }
}
