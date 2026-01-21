package io.github.pylonmc.pylon.base.content.machines.diesel.production;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.components.FluidOutputHatch;
import io.github.pylonmc.pylon.base.content.components.ItemInputHatch;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.MachineUpdateReason;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.Waila;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Fermenter extends PylonBlock implements
        PylonSimpleMultiblock,
        PylonDirectionalBlock,
        PylonTickingBlock,
        PylonFluidBufferBlock {

    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final double ethanolPerSugarcane = getSettings().getOrThrow("ethanol-per-sugarcane", ConfigAdapter.DOUBLE);
    public final int sugarcaneCapacity = getSettings().getOrThrow("sugarcane-capacity", ConfigAdapter.INT);
    public final double maxEthanolOutputRate = getSettings().getOrThrow("max-ethanol-output-rate", ConfigAdapter.DOUBLE);

    public static class Item extends PylonItem {

        public final double ethanolPerSugarcane = getSettings().getOrThrow("ethanol-per-sugarcane", ConfigAdapter.DOUBLE);
        public final int sugarcaneCapacity = getSettings().getOrThrow("sugarcane-capacity", ConfigAdapter.INT);
        public final double maxEthanolOutputRate = getSettings().getOrThrow("max-ethanol-output-rate", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("ethanol-per-sugarcane", UnitFormat.MILLIBUCKETS.format(ethanolPerSugarcane)),
                    PylonArgument.of("sugarcane-capacity", UnitFormat.ITEMS.format(sugarcaneCapacity)),
                    PylonArgument.of("max-ethanol-output-rate", UnitFormat.MILLIBUCKETS_PER_SECOND.format(maxEthanolOutputRate))
            );
        }
    }

    @SuppressWarnings("unused")
    public Fermenter(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setFacing(context.getFacing());
        setMultiblockDirection(context.getFacing());
        setTickInterval(tickInterval);
        createFluidBuffer(BaseFluids.SUGARCANE, ethanolPerSugarcane * sugarcaneCapacity, false, false);
        addEntity("sugarcane", new ItemDisplayBuilder()
                .itemStack(BaseFluids.SUGARCANE.getItem())
                .transformation(new TransformBuilder()
                        .scale(0, 0, 0))
                .build(getBlock().getLocation().toCenterLocation().add(0, 0.5, 0))
        );
    }

    @SuppressWarnings("unused")
    public Fermenter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public @NotNull Map<@NotNull Vector3i, @NotNull MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();

        components.put(new Vector3i(-1, 0, 0), new VanillaMultiblockComponent(Material.IRON_BLOCK));
        components.put(new Vector3i(1, 0, 0), new VanillaMultiblockComponent(Material.IRON_BLOCK));
        components.put(new Vector3i(0, 0, -1), new PylonMultiblockComponent(BaseKeys.ITEM_INPUT_HATCH));
        components.put(new Vector3i(0, 0, 1), new PylonMultiblockComponent(BaseKeys.FLUID_OUTPUT_HATCH));
        components.put(new Vector3i(-1, 0, -1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(-1, 0, 1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(1, 0, -1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));
        components.put(new Vector3i(1, 0, 1), new VanillaMultiblockComponent(Material.POLISHED_DEEPSLATE_WALL));

        for (int x = -1; x <= 1; x++) {
            for (int y = 1 ; y <= 4; y++) {
                for (int z = -1; z <= 1; z++) {
                    Vector3i position = new Vector3i(x, y, z);
                    if (x == 0 && z == 0) {
                        components.put(position, new PylonMultiblockComponent(BaseKeys.FERMENTER_CORE));
                    } else {
                        components.put(position, new PylonMultiblockComponent(BaseKeys.FERMENTER_CASING));
                    }
                }
            }
        }

        components.remove(new Vector3i(0, 1, 1));

        return components;
    }

    @Override
    public boolean checkFormed() {
        boolean formed = PylonSimpleMultiblock.super.checkFormed();
        if (formed) {
            getOutputHatch().setFluidType(BaseFluids.ETHANOL);
            for (Vector3i position : getComponents().keySet()) {
                Vector relative = Vector.fromJOML(PylonUtils.rotateVectorToFace(position, getFacing()));
                Location location = getBlock().getLocation().add(relative);
                Waila.addWailaOverride(location.getBlock(), this::getWaila);
            }
            getHeldEntityOrThrow(ItemDisplay.class, "sugarcane").setItemStack(BaseFluids.SUGARCANE.getItem());
        }
        return formed;
    }

    @Override
    public void onMultiblockUnformed(boolean partUnloaded) {
        PylonSimpleMultiblock.super.onMultiblockUnformed(partUnloaded);
        FluidOutputHatch outputHatch = getOutputHatch();
        if (outputHatch != null) {
            outputHatch.setFluidType(null);
        }
        for (Vector3i position : getComponents().keySet()) {
            Vector relative = Vector.fromJOML(PylonUtils.rotateVectorToFace(position, getFacing()));
            Location location = getBlock().getLocation().add(relative);
            Waila.removeWailaOverride(location.getBlock());
        }
        getHeldEntityOrThrow(ItemDisplay.class, "sugarcane").setItemStack(null);
    }

    @Override
    public void tick() {
        if (!isFormedAndFullyLoaded()) {
            return;
        }

        ItemInputHatch inputHatch = getInputHatch();
        FluidOutputHatch outputHatch = getOutputHatch();
        Preconditions.checkState(inputHatch != null && outputHatch != null);

        ItemStack sugarcane = inputHatch.inventory.getItem(0);
        if (sugarcane != null
                && PylonItem.fromStack(sugarcane) == null
                && sugarcane.getType().equals(Material.SUGAR_CANE)
                && fluidSpaceRemaining(BaseFluids.SUGARCANE) > ethanolPerSugarcane
        ) {
            int max = (int) (fluidSpaceRemaining(BaseFluids.SUGARCANE) / ethanolPerSugarcane);
            int sugarcaneToConsume = Math.min(max, sugarcane.getAmount());
            addFluid(BaseFluids.SUGARCANE, sugarcaneToConsume * ethanolPerSugarcane);
            inputHatch.inventory.setItem(new MachineUpdateReason(), 0, sugarcane.subtract(sugarcaneToConsume));
        }

        double sugarcaneProportion = fluidAmount(BaseFluids.SUGARCANE) / fluidCapacity(BaseFluids.SUGARCANE);
        double outputSpaceRemaining = outputHatch.fluidSpaceRemaining(BaseFluids.ETHANOL);
        double ethanolToOutput = Math.min(outputSpaceRemaining, sugarcaneProportion * maxEthanolOutputRate * getTickInterval() / 20);
        if (ethanolToOutput > 1.0e-6) {
            removeFluid(BaseFluids.SUGARCANE, ethanolToOutput);
            outputHatch.addFluid(BaseFluids.ETHANOL, ethanolToOutput);
        }
    }

    @Override
    public boolean setFluid(@NotNull PylonFluid fluid, double amount) {
        boolean wasSet = PylonFluidBufferBlock.super.setFluid(fluid, amount);
        if (wasSet) {
            double sugarcaneProportion = fluidAmount(BaseFluids.SUGARCANE) / fluidCapacity(BaseFluids.SUGARCANE);
            getHeldEntityOrThrow(ItemDisplay.class, "sugarcane").setTransformationMatrix(
                    new TransformBuilder()
                            .scale(0.7, 4 * sugarcaneProportion, 0.7)
                            .translate(0, 0.5, 0)
                            .buildForItemDisplay()
            );
        }
        return wasSet;
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        double sugarcaneProportion = fluidAmount(BaseFluids.SUGARCANE) / fluidCapacity(BaseFluids.SUGARCANE);
        int sugarcaneAmount = sugarcaneProportion < 1.0e-3
                ? 0
                : Math.min(sugarcaneCapacity, (int) (sugarcaneProportion * sugarcaneCapacity) + 1);
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("sugarcane-bar", BaseUtils.createBar(
                        sugarcaneProportion,
                        20, TextColor.color(163, 237, 45)
                )),
                PylonArgument.of("sugarcane-amount", sugarcaneAmount)
        ));
    }

    public @Nullable ItemInputHatch getInputHatch() {
        Vector relative = Vector.fromJOML(PylonUtils.rotateVectorToFace(new Vector3i(0, 0, -1), getFacing()));
        Location location = getBlock().getLocation().add(relative);
        return BlockStorage.getAs(ItemInputHatch.class, location);
    }

    public @Nullable FluidOutputHatch getOutputHatch() {
        Vector relative = Vector.fromJOML(PylonUtils.rotateVectorToFace(new Vector3i(0, 0, 1), getFacing()));
        Location location = getBlock().getLocation().add(relative);
        return BlockStorage.getAs(FluidOutputHatch.class, location);
    }
}