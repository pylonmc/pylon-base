package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.machines.simple.CoreDrill;
import io.github.pylonmc.pylon.base.content.machines.simple.Grindstone;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.Map;

public class HydraulicCoreDrill extends CoreDrill implements PylonTickingBlock {

    public static class Item extends CoreDrill.Item {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            Map<String, ComponentLike> placeholders = new HashMap<>(super.getPlaceholders());
            placeholders.put("hydraulic-fluid-consumption", UnitFormat.MILLIBUCKETS_PER_SECOND.format(HYDRAULIC_FLUID_INPUT_MB_PER_SECOND));
            placeholders.put("dirty-hydraulic-fluid-output", UnitFormat.MILLIBUCKETS_PER_SECOND.format(DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND));
            return placeholders;
        }
    }

    public static final Config settings = Settings.get(BaseKeys.HYDRAULIC_CORE_DRILL);
    public static final int HYDRAULIC_FLUID_INPUT_MB_PER_SECOND = settings.getOrThrow("hydraulic-fluid-input-mb-per-second", Integer.class);
    public static final int DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND = settings.getOrThrow("dirty-hydraulic-fluid-output-mb-per-second", Integer.class);
    public final double fluidConsumptionPerCycle = HYDRAULIC_FLUID_INPUT_MB_PER_SECOND * getCycleDuration() / 20.0;
    public final double fluidOutputPerCycle = DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND * getCycleDuration() / 20.0;

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
        components.put(new Vector3i(1, -2, 3), new PylonMultiblockComponent(BaseKeys.HYDRAULIC_CORE_DRILL_INPUT_HATCH));
        components.put(new Vector3i(1, -1, 3), new PylonMultiblockComponent(BaseKeys.FLUID_TANK_CASING_COPPER));
        components.put(new Vector3i(-1, -2, 3), new PylonMultiblockComponent(BaseKeys.HYDRAULIC_CORE_DRILL_OUTPUT_HATCH));
        components.put(new Vector3i(-1, -1, 3), new PylonMultiblockComponent(BaseKeys.FLUID_TANK_CASING_COPPER));

        components.put(new Vector3i(0, -2, 4), new VanillaMultiblockComponent(Material.CHEST));

        return components;
    }

    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded() || isCycling()) {
            return;
        }

        HydraulicCoreDrillHatch inputHatch = BlockStorage.getAs(
                HydraulicCoreDrillInputHatch.class,
                getBlock().getLocation().add(
                        Vector.fromJOML(PylonUtils.rotateVectorToFace(new Vector3i(1, -2, 3), getFacing()))
                )
        );

        HydraulicCoreDrillHatch outputHatch = BlockStorage.getAs(
                HydraulicCoreDrillOutputHatch.class,
                getBlock().getLocation().add(
                        Vector.fromJOML(PylonUtils.rotateVectorToFace(new Vector3i(-1, -2, 3), getFacing()))
                )
        );

        Preconditions.checkState(inputHatch != null && outputHatch != null);

        if (inputHatch.fluidAmount(BaseFluids.HYDRAULIC_FLUID) < fluidConsumptionPerCycle
                || outputHatch.fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < fluidOutputPerCycle
        ) {
            return;
        }

        inputHatch.removeFluid(BaseFluids.HYDRAULIC_FLUID, fluidConsumptionPerCycle);
        outputHatch.addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, fluidConsumptionPerCycle);
        cycle();
    }

    @Override
    protected void finishCycle() {
        cycling = false;

        if (!(getBlock().getRelative(0, -2, 4).getState() instanceof Chest chest)) {
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
}
