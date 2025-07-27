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
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class HydraulicCoreDrillInputHatch extends HydraulicCoreDrillHatch {

    private final double capacity = Settings.get(BaseKeys.FLUID_TANK_CASING_COPPER).getOrThrow("capacity", Double.class);

    @SuppressWarnings("unused")
    public HydraulicCoreDrillInputHatch(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        createFluidBuffer(
                BaseFluids.HYDRAULIC_FLUID,
                capacity,
                true,
                false
        );
        addEntity("fluid", new SimpleItemDisplay(new ItemDisplayBuilder()
                .material(BaseFluids.HYDRAULIC_FLUID.getMaterial())
                .transformation(new TransformBuilder().scale(0))
                .build(getBlock().getLocation().toCenterLocation().add(0, 1, 0))
        ));
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
    }

    @SuppressWarnings("unused")
    public HydraulicCoreDrillInputHatch(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public boolean checkFormed() {
        boolean formed = super.checkFormed();
        if (!formed) {
            setFluidCapacity(BaseFluids.HYDRAULIC_FLUID, 0);
            setFluid(BaseFluids.HYDRAULIC_FLUID, 0);
        } else {
            setFluidCapacity(BaseFluids.HYDRAULIC_FLUID, capacity);
        }
        return formed;
    }
}
