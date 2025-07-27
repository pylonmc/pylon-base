package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.EnumPersistentDataType;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.Style;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class FluidTank extends PylonBlock
        implements PylonMultiblock, PylonFluidTank, PylonEntityHolderBlock {

    public static class Item extends PylonItem {

        private final int maxHeight = getSettings().getOrThrow("max-height", Integer.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "max-height", UnitFormat.BLOCKS.format(maxHeight)
            );
        }
    }

    private static final NamespacedKey HEIGHT_KEY = baseKey("height");
    private static final NamespacedKey ALLOWED_TEMPERATURES_KEY = baseKey("allowed_temperatures");
    private static final PersistentDataType<List<String>, List<FluidTemperature>> ALLOWED_TEMPERATURES_TYPE
            = PylonSerializers.LIST.listTypeFrom(new EnumPersistentDataType<>(FluidTemperature.class));

    private final int maxHeight = getSettings().getOrThrow("max-height", Integer.class);

    private int height;
    private List<FluidTemperature> allowedTemperatures;

    @SuppressWarnings("unused")
    public FluidTank(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        height = 0;
        allowedTemperatures = List.of();
        addEntity("fluid", new SimpleItemDisplay(new ItemDisplayBuilder()
                .build(getBlock().getLocation().toCenterLocation().add(0, 1, 0))
        ));
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidTank(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        height = pdc.get(HEIGHT_KEY, PylonSerializers.INTEGER);
        allowedTemperatures = pdc.get(ALLOWED_TEMPERATURES_KEY, ALLOWED_TEMPERATURES_TYPE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(HEIGHT_KEY, PylonSerializers.INTEGER, height);
        pdc.set(ALLOWED_TEMPERATURES_KEY, ALLOWED_TEMPERATURES_TYPE, allowedTemperatures);
    }

    @Override
    public @NotNull Set<ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock()));
    }

    @Override
    public boolean checkFormed() {
        height = 0;
        FluidTankCasing casingType = null;
        for (int i = 0; i < maxHeight; i++) {
            FluidTankCasing casing = BlockStorage.getAs(
                    FluidTankCasing.class,
                    getBlock().getLocation().add(0, i + 1, 0)
            );
            if (casingType == null) {
                casingType = casing;
            }
            if (casing == null || casing.getKey() != casingType.getKey()) {
                break;
            }
            height++;
        }

        if (casingType != null) {
            allowedTemperatures = casingType.getAllowedTemperatures();
            setCapacity(height * casingType.getCapacity());
        } else {
            allowedTemperatures = List.of();
            setCapacity(0);
        }
        setFluid(Math.min(fluidCapacity(), fluidAmount()));

        return casingType != null;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        Vector offset = otherBlock.getLocation().toVector()
                .subtract(getBlock().getLocation().toVector());
        return offset.getBlockX() == 0
                && offset.getBlockY() > 0
                && offset.getBlockY() <= maxHeight
                && offset.getBlockZ() == 0;
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return fluid.hasTag(FluidTemperature.class)
                && allowedTemperatures.contains(fluid.getTag(FluidTemperature.class));
    }

    @Override
    public void setFluidType(@Nullable PylonFluid fluid) {
        PylonFluidTank.super.setFluidType(fluid);
        getFluidDisplay().getEntity().setItemStack(fluid == null ? null : new ItemStack(fluid.getMaterial()));
    }

    @Override
    public boolean setFluid(double amount) {
        boolean result = PylonFluidTank.super.setFluid(amount);
        float scale = (float) (height * fluidAmount() / fluidCapacity() - 0.1F);
        getFluidDisplay().getEntity().setTransformationMatrix(new TransformBuilder()
                .translate(0.0, -0.45 + scale / 2, 0.0)
                .scale(0.9, scale, 0.9)
                .buildForItemDisplay()
        );
        return result;
    }

    public @NotNull SimpleItemDisplay getFluidDisplay() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "fluid");
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        Component info;
        if (fluidType() == null) {
            info = Component.translatable("pylon.pylonbase.waila.fluid_tank.empty");
        } else {
            info = Component.translatable(
                    "pylon.pylonbase.waila.fluid_tank.filled",
                    PylonArgument.of("amount", Math.round(fluidAmount())),
                    PylonArgument.of("capacity", UnitFormat.MILLIBUCKETS.format(fluidCapacity())
                            .decimalPlaces(0)
                            .unitStyle(Style.empty())
                    ),
                    PylonArgument.of("fluid", fluidType().getName())
            );
        }
        return new WailaConfig(getName(), Map.of("info", info));
    }
}
