package io.github.pylonmc.pylon.base.items.fluid;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


public final class FluidTank {

    private FluidTank() {
        throw new AssertionError("Container class");
    }

    public static class FluidTankBlock extends PylonBlock<FluidTankBlock.Schema> implements PylonEntityHolderBlock, PylonFluidBlock {

        public static class Schema extends PylonBlockSchema {

            @Getter
            private final int capacity;

            public Schema(@NotNull NamespacedKey key, @NotNull Material material, int capacity) {
                super(key, material, FluidTankBlock.class);
                this.capacity = capacity;
            }
        }

        private static final NamespacedKey FLUID_AMOUNT_KEY = KeyUtils.pylonKey("fluid_amount");
        private static final NamespacedKey FLUID_TYPE_KEY = KeyUtils.pylonKey("fluid_type");

        private final Map<String, UUID> entities;
        private int fluidAmount;
        private @Nullable PylonFluid fluidType;

        @SuppressWarnings("unused")
        protected FluidTankBlock(@NotNull Schema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
            super(schema, block);

            entities = Map.of();
            fluidAmount = 0;
            fluidType = null;
        }

        @SuppressWarnings("unused")
        protected FluidTankBlock(@NotNull Schema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
            super(schema, block);

            entities = loadHeldEntities(pdc);
            fluidAmount = pdc.get(FLUID_AMOUNT_KEY, PylonSerializers.INTEGER);
            fluidType = PylonRegistry.FLUIDS.get(pdc.get(FLUID_TYPE_KEY, PylonSerializers.NAMESPACED_KEY));
        }

        @Override
        public void write(@NotNull PersistentDataContainer pdc) {
            saveHeldEntities(pdc);
            pdc.set(FLUID_AMOUNT_KEY, PylonSerializers.INTEGER, fluidAmount);
        }

        @Override
        public @NotNull Map<String, UUID> getHeldEntities() {
            return entities;
        }

        @Override
        public @NotNull Map<PylonFluid, Integer> getAvailableFluids(@NotNull String connectionPoint) {
            return fluidType == null
                    ? Map.of()
                    : Map.of(fluidType, fluidAmount);
        }

        @Override
        public @NotNull Map<PylonFluid, Integer> getFluidCapacities(@NotNull String connectionPoint) {
            // If no fluid contained, allow any fluid to be added, otherwise, only allow more of the stored fluid to be added
            return fluidType == null
                    ? PylonRegistry.FLUIDS.getValues()
                        .stream()
                        .collect(Collectors.toMap(Function.identity(), key -> getSchema().capacity))
                    : Map.of(fluidType, getSchema().capacity - fluidAmount);
        }

        @Override
        public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, int amount) {
            if (!fluid.equals(fluidType)) {
                Preconditions.checkState(fluidAmount == 0, "Attempt to assign a new fluid when the tank already contains a fluid");
                fluidType = fluid;
            }
            fluidAmount += amount;
        }

        @Override
        public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, int amount) {
            fluidAmount -= amount;
            if (fluidAmount == 0) {
                fluidType = null;
            }
        }
    }
}
