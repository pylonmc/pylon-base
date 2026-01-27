package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.machines.simple.MixingPot;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.rebar.block.BlockStorage;
import io.github.pylonmc.rebar.block.PylonBlock;
import io.github.pylonmc.rebar.block.base.PylonDirectionalBlock;
import io.github.pylonmc.rebar.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.rebar.block.base.PylonProcessor;
import io.github.pylonmc.rebar.block.base.PylonTickingBlock;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.rebar.entity.display.transform.TransformBuilder;
import io.github.pylonmc.rebar.fluid.FluidPointType;
import io.github.pylonmc.rebar.i18n.PylonArgument;
import io.github.pylonmc.rebar.item.PylonItem;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import io.github.pylonmc.rebar.waila.WailaDisplay;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;


public class HydraulicMixingAttachment extends PylonBlock implements
        PylonTickingBlock,
        PylonFluidBufferBlock,
        PylonProcessor,
        PylonDirectionalBlock {

    public final int cooldownTicks = getSettings().getOrThrow("cooldown-ticks", ConfigAdapter.INT);
    public final int downAnimationTimeTicks = getSettings().getOrThrow("down-animation-time-ticks", ConfigAdapter.INT);
    public final int upAnimationTimeTicks = getSettings().getOrThrow("up-animation-time-ticks", ConfigAdapter.INT);
    public final double hydraulicFluidPerCraft = getSettings().getOrThrow("hydraulic-fluid-per-craft", ConfigAdapter.INT);
    public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.INT);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    public static class Item extends PylonItem {

        public final int cooldownTicks = getSettings().getOrThrow("cooldown-ticks", ConfigAdapter.INT);
        public final double hydraulicFluidPerCraft = getSettings().getOrThrow("hydraulic-fluid-per-craft", ConfigAdapter.INT);
        public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("cooldown", UnitFormat.SECONDS.format(cooldownTicks / 20.0)),
                    PylonArgument.of("hydraulic-fluid-per-craft", UnitFormat.MILLIBUCKETS.format(hydraulicFluidPerCraft)),
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(buffer))
            );
        }
    }

    @SuppressWarnings("unused")
    public HydraulicMixingAttachment(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setTickInterval(tickInterval);
        setFacing(context.getFacing());

        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);

        addEntity("mixing_attachment_shaft", new ItemDisplayBuilder()
                .itemStack(ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
                        .addCustomModelDataString(getKey() + ":mixing_attachment_shaft")
                )
                .transformation(getShaftTransformation(0.7))
                .build(getBlock().getLocation().toCenterLocation().add(0, -1, 0))
        );

        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, buffer, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, buffer, false, true);
    }

    @SuppressWarnings("unused")
    public HydraulicMixingAttachment(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }


    @Override
    public void tick() {
        if (isProcessing()) {
            progressProcess(getTickInterval());
            return;
        }

        MixingPot mixingPot = BlockStorage.getAs(MixingPot.class, getBlock().getRelative(BlockFace.DOWN, 2));
        if (mixingPot == null || fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidPerCraft
                || fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < hydraulicFluidPerCraft
                || !mixingPot.tryDoRecipe()) {
            return;
        }

        removeFluid(BaseFluids.HYDRAULIC_FLUID, hydraulicFluidPerCraft);
        addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, hydraulicFluidPerCraft);

        startProcess(cooldownTicks);

        BaseUtils.animate(getMixingAttachmentShaft(), downAnimationTimeTicks, getShaftTransformation(0.2));
        Bukkit.getScheduler().runTaskLater(
                PylonBase.getInstance(),
                () -> BaseUtils.animate(getMixingAttachmentShaft(), upAnimationTimeTicks, getShaftTransformation(0.7)),
                downAnimationTimeTicks
        );
    }

    private static @NotNull Matrix4f getShaftTransformation(double yTranslation) {
        return new TransformBuilder()
                .translate(0, yTranslation, 0)
                .scale(0.2, 1.5, 0.2)
                .buildForItemDisplay();
    }

    public @Nullable ItemDisplay getMixingAttachmentShaft() {
        return getHeldEntity(ItemDisplay.class, "mixing_attachment_shaft");
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("input-bar", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.HYDRAULIC_FLUID),
                        fluidCapacity(BaseFluids.HYDRAULIC_FLUID),
                        20,
                        TextColor.fromHexString("#212d99")
                )),
                PylonArgument.of("output-bar", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.DIRTY_HYDRAULIC_FLUID),
                        fluidCapacity(BaseFluids.DIRTY_HYDRAULIC_FLUID),
                        20,
                        TextColor.fromHexString("#48459b")
                ))
        ));
    }
}
