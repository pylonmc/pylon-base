package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.components.FluidInputHatch;
import io.github.pylonmc.pylon.base.content.components.FluidOutputHatch;
import io.github.pylonmc.pylon.base.content.machines.simple.CoreDrill;
import io.github.pylonmc.rebar.block.BlockStorage;
import io.github.pylonmc.rebar.block.base.PylonTickingBlock;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.i18n.PylonArgument;
import io.github.pylonmc.rebar.util.PylonUtils;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HydraulicCoreDrill extends CoreDrill implements PylonTickingBlock {

    public final int hydraulicFluidUsage = getSettings().getOrThrow("hydraulic-fluid-usage", ConfigAdapter.INT);
    public final double hydraulicFluidPerCycle = hydraulicFluidUsage * getCycleDuration() / 20.0;

    public static class Item extends CoreDrill.Item {

        public final int hydraulicFluidUsage = getSettings().getOrThrow("hydraulic-fluid-usage", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            List<PylonArgument> placeholders = new ArrayList<>(super.getPlaceholders());
            placeholders.add(PylonArgument.of("hydraulic-fluid-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(hydraulicFluidUsage)));
            return placeholders;
        }
    }

    @SuppressWarnings("unused")
    public HydraulicCoreDrill(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(getCycleDuration());
    }

    @SuppressWarnings("unused")
    public HydraulicCoreDrill(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Map<Vector3i, MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();

        components.put(new Vector3i(0, 0, -1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(0, -1, -1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(0, -2, -1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));

        components.put(new Vector3i(1, 0, 0), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(1, -1, 0), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(1, -2, 0), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));

        components.put(new Vector3i(-1, 0, 0), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(-1, -1, 0), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(-1, -2, 0), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));

        components.put(new Vector3i(0, 0, 1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(0, -1, 1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(0, -2, 1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));

        components.put(new Vector3i(-1, -2, -1), new VanillaMultiblockComponent(Material.IRON_BARS));
        components.put(new Vector3i(-1, -2, 1), new VanillaMultiblockComponent(Material.IRON_BARS));
        components.put(new Vector3i(1, -2, -1), new VanillaMultiblockComponent(Material.IRON_BARS));
        components.put(new Vector3i(1, -2, 1), new VanillaMultiblockComponent(Material.IRON_BARS));

        components.put(new Vector3i(-1, -2, 2), new VanillaMultiblockComponent(Material.IRON_BARS));
        components.put(new Vector3i(1, -2, 2), new VanillaMultiblockComponent(Material.IRON_BARS));

        components.put(new Vector3i(0, -2, 3), new VanillaMultiblockComponent(Material.CAULDRON));
        components.put(new Vector3i(1, -2, 3), new PylonMultiblockComponent(BaseKeys.FLUID_INPUT_HATCH));
        components.put(new Vector3i(-1, -2, 3), new PylonMultiblockComponent(BaseKeys.FLUID_OUTPUT_HATCH));

        components.put(new Vector3i(0, -2, 4), new VanillaMultiblockComponent(Material.CHEST));

        return components;
    }

    @Override
    public void tick() {
        if (!isFormedAndFullyLoaded() || isProcessing()) {
            return;
        }

        FluidInputHatch inputHatch = getInputHatch();
        FluidOutputHatch outputHatch = getOutputHatch();
        Preconditions.checkState(inputHatch != null && outputHatch != null);

        if (inputHatch.fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidPerCycle
                || outputHatch.fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < hydraulicFluidPerCycle
        ) {
            return;
        }

        inputHatch.removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidPerCycle);
        outputHatch.addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidPerCycle);
        cycle();
    }

    @Override
    public void onProcessFinished() {
        Vector3i chestOffset = PylonUtils.rotateVectorToFace(new Vector3i(0, -2, 4), getFacing());
        if (!(getBlock().getRelative(chestOffset.x, chestOffset.y, chestOffset.z).getState() instanceof Chest chest)) {
            return;
        }

        if (chest.getInventory().addItem(output).isEmpty()) {
            return;
        }

        getBlock().getWorld().dropItemNaturally(
                getBlock().getRelative(BlockFace.DOWN, 2).getLocation().toCenterLocation(),
                output
        );
    }

    @Override
    public void onMultiblockFormed() {
        super.onMultiblockFormed();
        FluidInputHatch inputHatch = getInputHatch();
        FluidOutputHatch outputHatch = getOutputHatch();
        Preconditions.checkState(inputHatch != null && outputHatch != null);
        inputHatch.setFluidType(BaseFluids.HYDRAULIC_FLUID);
        outputHatch.setFluidType(BaseFluids.DIRTY_HYDRAULIC_FLUID);
    }

    @Override
    public void onMultiblockUnformed(boolean partUnloaded) {
        super.onMultiblockUnformed(partUnloaded);
        FluidInputHatch inputHatch = getInputHatch();
        if (inputHatch != null) {
            inputHatch.setFluidType(null);
        }
        FluidOutputHatch outputHatch = getOutputHatch();
        if (outputHatch != null) {
            outputHatch.setFluidType(null);
        }
    }

    public @Nullable FluidInputHatch getInputHatch() {
        Vector relativeLocation = Vector.fromJOML(PylonUtils.rotateVectorToFace(new Vector3i(1, -2, 3), getFacing()));
        Location inputHatchLocation = getBlock().getLocation().add(relativeLocation);
        return BlockStorage.getAs(FluidInputHatch.class, inputHatchLocation);
    }

    public @Nullable FluidOutputHatch getOutputHatch() {
        Vector relativeLocation = Vector.fromJOML(PylonUtils.rotateVectorToFace(new Vector3i(-1, -2, 3), getFacing()));
        Location inputHatchLocation = getBlock().getLocation().add(relativeLocation);
        return BlockStorage.getAs(FluidOutputHatch.class, inputHatchLocation);
    }
}
