package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class HydraulicCoreDrillOutputHatch extends HydraulicCoreDrillHatch {

    @SuppressWarnings("unused")
    public HydraulicCoreDrillOutputHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        createFluidBuffer(
                BaseFluids.DIRTY_HYDRAULIC_FLUID,
                Settings.get(BaseKeys.FLUID_TANK_CASING_COPPER).getOrThrow("capacity", Double.class),
                false,
                true
        );
        addEntity("fluid", new SimpleItemDisplay(new ItemDisplayBuilder()
                .material(BaseFluids.DIRTY_HYDRAULIC_FLUID.getMaterial())
                .transformation(new TransformBuilder().scale(0))
                .build(getBlock().getLocation().toCenterLocation().add(0, 1, 0))
        ));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.NORTH));
    }

    @SuppressWarnings("unused")
    public HydraulicCoreDrillOutputHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public boolean checkFormed() {
        boolean formed = super.checkFormed();
        if (!formed) {
            setFluidCapacity(BaseFluids.DIRTY_HYDRAULIC_FLUID, 0);
            setFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, 0);
        }
        return formed;
    }
}
