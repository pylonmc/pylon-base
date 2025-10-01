package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FluidTank extends PylonBlock
        implements PylonMultiblock, PylonFluidTank, PylonEntityHolderBlock {

    public static class Item extends PylonItem {

        private final int maxHeight = getSettings().getOrThrow("max-height", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("max-height", UnitFormat.BLOCKS.format(maxHeight))
            );
        }
    }

    private final int maxHeight = getSettings().getOrThrow("max-height", ConfigAdapter.INT);

    private final List<FluidTankCasing> casings = new ArrayList<>();
    private final List<FluidTemperature> allowedTemperatures = new ArrayList<>();

    private int lastDisplayUpdate = -1;

    @SuppressWarnings("unused")
    public FluidTank(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        addEntity("fluid", new ItemDisplayBuilder()
                .build(getBlock().getLocation().toCenterLocation().add(0, 1, 0))
        );
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidTank(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Set<ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock()));
    }

    @Override
    public boolean checkFormed() {
        casings.forEach(FluidTankCasing::reset);
        casings.clear();
        for (int i = 0; i < maxHeight; i++) {
            FluidTankCasing casing = BlockStorage.getAs(
                    FluidTankCasing.class,
                    getBlock().getRelative(0, i + 1, 0)
            );

            FluidTankCasing casingType = casings.isEmpty() ? casing : casings.getFirst();
            if (casing == null || casing.getKey() != casingType.getKey()) {
                break;
            }

            casing.setFluidTank(this);
            casings.add(casing);
        }
        return !casings.isEmpty();
    }

    @Override
    public void onMultiblockFormed() {
        onMultiblockRefreshed();
    }

    @Override
    public void onMultiblockRefreshed() {
        FluidTankCasing casingType = casings.getFirst();
        allowedTemperatures.clear();
        allowedTemperatures.addAll(casingType.getAllowedTemperatures());
        setCapacity(casings.size() * casingType.getCapacity());
        setFluid(Math.min(getFluidCapacity(), getFluidAmount()));

        int height = casings.size();
        for (int i = 0; i < casings.size(); i++) {
            FluidTankCasing casing = casings.get(i);
            if (i == 0) {
                casing.setShape(height == 1 ? "single" : "bottom");
            } else if (i == casings.size() - 1) {
                casing.setShape("top");
            } else {
                casing.setShape("middle");
            }
        }
    }

    @Override
    public void onMultiblockUnformed() {
        casings.forEach(casing -> casing.setFluidTank(null));
        casings.clear();
        allowedTemperatures.clear();
        setCapacity(0);
        setFluid(0);
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        Vector offset = otherBlock.getLocation().toVector().subtract(getBlock().getLocation().toVector());
        return offset.getBlockX() == 0
                && offset.getBlockY() > 0
                && offset.getBlockY() <= maxHeight
                && offset.getBlockZ() == 0;
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return fluid.hasTag(FluidTemperature.class) && allowedTemperatures.contains(fluid.getTag(FluidTemperature.class));
    }

    @Override
    public void setFluidType(@Nullable PylonFluid fluid) {
        PylonFluidTank.super.setFluidType(fluid);
        getFluidDisplay().setItemStack(fluid == null ? null : fluid.getItem());
    }

    @Override
    public boolean setFluid(double amount) {
        double oldAmount = getFluidAmount();
        boolean result = PylonFluidTank.super.setFluid(amount);
        amount = getFluidAmount();
        if (lastDisplayUpdate == -1 || (result && oldAmount != amount)) {
            float scale = (float) ((casings.size() - 0.1) * amount / getFluidCapacity());
            ItemDisplay fluidDisplay = getFluidDisplay();
            fluidDisplay.setInterpolationDelay(Math.min(-3 + (fluidDisplay.getTicksLived() - lastDisplayUpdate), 0));
            fluidDisplay.setInterpolationDuration(4);
            fluidDisplay.setTransformationMatrix(new TransformBuilder()
                    .translate(0.0, -0.45 + scale / 2, 0.0)
                    .scale(0.9, scale, 0.9)
                    .buildForItemDisplay()
            );
            lastDisplayUpdate = fluidDisplay.getTicksLived();
        }
        return result;
    }

    public @NotNull ItemDisplay getFluidDisplay() {
        return getHeldEntityOrThrow(ItemDisplay.class, "fluid");
    }

    @Override
    public @NotNull WailaDisplay getWaila(@NotNull Player player) {
        Component info;
        if (getFluidType() == null) {
            info = Component.translatable("pylon.pylonbase.waila.fluid_tank.empty");
        } else {
            info = Component.translatable(
                    "pylon.pylonbase.waila.fluid_tank.filled",
                    PylonArgument.of("amount", Math.round(getFluidAmount())),
                    PylonArgument.of("capacity", UnitFormat.MILLIBUCKETS.format(getFluidCapacity())
                            .decimalPlaces(0)
                            .unitStyle(Style.empty())
                    ),
                    PylonArgument.of("fluid", getFluidType().getName())
            );
        }
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(PylonArgument.of("info", info)));
    }
}
