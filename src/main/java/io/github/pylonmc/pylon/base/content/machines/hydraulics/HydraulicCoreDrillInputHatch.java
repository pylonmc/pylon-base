package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.content.machines.fluid.FluidTankCasing;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HydraulicCoreDrillInputHatch extends HydraulicCoreDrillHatch {

    @SuppressWarnings("unused")
    public HydraulicCoreDrillInputHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        createFluidBuffer(
                BaseFluids.HYDRAULIC_FLUID,
                0,
                true,
                false
        );
        addEntity("fluid", new ItemDisplayBuilder()
                .itemStack(BaseFluids.HYDRAULIC_FLUID.getItem())
                .transformation(new TransformBuilder().scale(0))
                .build(getBlock().getLocation().toCenterLocation().add(0, 1, 0))
        );
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, true);
    }

    @SuppressWarnings("unused")
    public HydraulicCoreDrillInputHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onMultiblockFormed() {
        FluidTankCasing casing = BlockStorage.getAs(FluidTankCasing.class, getBlock().getRelative(BlockFace.UP));
        setFluidCapacity(BaseFluids.HYDRAULIC_FLUID, casing.capacity);
    }

    @Override
    public void onMultiblockUnformed(boolean partUnloaded) {
        if (!partUnloaded) {
            setFluidCapacity(BaseFluids.HYDRAULIC_FLUID, 0);
            setFluid(BaseFluids.HYDRAULIC_FLUID, 0);
        }
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("bars", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.HYDRAULIC_FLUID),
                        fluidCapacity(BaseFluids.HYDRAULIC_FLUID),
                        20,
                        TextColor.fromHexString("#212d99")
                ))
        ));
    }
}
