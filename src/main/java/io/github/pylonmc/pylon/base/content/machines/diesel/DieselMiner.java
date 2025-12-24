package io.github.pylonmc.pylon.base.content.machines.diesel;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
import io.github.pylonmc.pylon.core.util.MachineUpdateReason;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class DieselMiner extends PylonBlock implements
        PylonGuiBlock,
        PylonFluidBufferBlock,
        PylonDirectionalBlock,
        PylonTickingBlock,
        PylonMultiblock,
        PylonLogisticBlock,
        PylonProcessor {

    public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);
    public final double dieselBuffer = getSettings().getOrThrow("diesel-buffer", ConfigAdapter.DOUBLE);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final int ticksPerBlock = getSettings().getOrThrow("ticks-per-block", ConfigAdapter.INT);
    public final List<Material> blocks = getSettings().getOrThrow("blocks", ConfigAdapter.LIST.from(ConfigAdapter.MATERIAL));
    public final Material topMaterial = getSettings().getOrThrow("top-material", ConfigAdapter.MATERIAL);

    private final VirtualInventory outputInventory = new VirtualInventory(1);

    public static class Item extends PylonItem {

        public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);
        public final double dieselBuffer = getSettings().getOrThrow("diesel-buffer", ConfigAdapter.DOUBLE);
        public final int ticksPerBlock = getSettings().getOrThrow("ticks-per-block", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("diesel-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(dieselPerSecond)),
                    PylonArgument.of("diesel-buffer", UnitFormat.MILLIBUCKETS.format(dieselBuffer)),
                    PylonArgument.of("time-per-block", UnitFormat.SECONDS.format(ticksPerBlock / 20.0))
            );
        }
    }

    public ItemStackBuilder drillStack = ItemStackBuilder.of(Material.YELLOW_CONCRETE)
            .addCustomModelDataString(getKey() + ":drill");
    public ItemStackBuilder sideStack1 = ItemStackBuilder.of(Material.BRICKS)
            .addCustomModelDataString(getKey() + ":side1");
    public ItemStackBuilder sideStack2 = ItemStackBuilder.of(Material.BRICKS)
            .addCustomModelDataString(getKey() + ":side2");
    public ItemStackBuilder topStack = ItemStackBuilder.of(topMaterial)
            .addCustomModelDataString(getKey() + ":top");
    public ItemStackBuilder chimneyStack = ItemStackBuilder.of(Material.CYAN_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":chimney");

    @SuppressWarnings("unused")
    public DieselMiner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(tickInterval);
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false, 0.55F);
        setFacing(context.getFacing().getOppositeFace());
        addEntity("chimney", new ItemDisplayBuilder()
                .itemStack(chimneyStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0.4, 0.0, -0.4)
                        .scale(0.15))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("side1", new ItemDisplayBuilder()
                .itemStack(sideStack1)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, -0.5, -0.1)
                        .scale(0.8, 0.8, 0.9))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("side2", new ItemDisplayBuilder()
                .itemStack(sideStack2)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, -0.5, 0)
                        .scale(1.1, 0.8, 0.8))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("top", new ItemDisplayBuilder()
                .itemStack(topStack)
                .transformation(new TransformBuilder()
                        .scale(0.55, 0.2, 0.55))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("drill", new ItemDisplayBuilder()
                .itemStack(drillStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, -0.5, 0.5)
                        .scale(0.6, 0.6, 0.2)
                        .rotate(0, 0, Math.PI / 4))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        createFluidBuffer(BaseFluids.BIODIESEL, dieselBuffer, true, false);
    }

    @SuppressWarnings("unused")
    public DieselMiner(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("output", LogisticSlotType.OUTPUT, outputInventory);
        outputInventory.setPreUpdateHandler(PylonUtils.DISALLOW_PLAYERS_FROM_ADDING_ITEMS_HANDLER);
        outputInventory.setPostUpdateHandler(event -> tryStartDrilling());
        tryStartDrilling();
    }

    @Override
    public void tick() {
        if (!isProcessing() || fluidAmount(BaseFluids.BIODIESEL) < dieselPerSecond * tickInterval / 20) {
            return;
        }

        removeFluid(BaseFluids.BIODIESEL, dieselPerSecond * tickInterval / 20);
        progressProcess(tickInterval);
        Block drilling = getBlock().getRelative(getFacing());
        Vector smokePosition = Vector.fromJOML(PylonUtils.rotateVectorToFace(
                new Vector3d(0.4, 0.7, -0.4),
                getFacing().getOppositeFace()
        ));
        new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
                .location(getBlock().getLocation().toCenterLocation().add(smokePosition))
                .offset(0, 1, 0)
                .count(0)
                .extra(0.05)
                .spawn();
        new ParticleBuilder(Particle.BLOCK)
                .count(5)
                .location(getBlock().getLocation().toCenterLocation().add(0, 0.6, 0))
                .data(drilling.getBlockData())
                .spawn();
    }

    public void tryStartDrilling() {
        if (isProcessing()) {
            return;
        }

        Block toDrill = getBlock().getRelative(getFacing());
        if (BlockStorage.isPylonBlock(toDrill)
                || !blocks.contains(toDrill.getType())
                || !outputInventory.canHold(new ItemStack(toDrill.getType()))
        ) {
            return;
        }

        startProcess(ticksPerBlock);
    }

    @Override
    public void onProcessFinished() {
        Block toDrill = getBlock().getRelative(getFacing());
        if (BlockStorage.isPylonBlock(toDrill)
                || !blocks.contains(toDrill.getType())
                || !new BlockBreakBlockEvent(toDrill, getBlock(), new ArrayList<>()).callEvent()
        ) {
            return;
        }

        ItemStack output = new ItemStack(toDrill.getType());
        toDrill.setType(Material.AIR);
        outputInventory.addItem(new MachineUpdateReason(), output);
    }

    @Override
    public @NotNull Set<ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock().getRelative(getFacing()).getChunk()));
    }

    @Override
    public boolean checkFormed() {
        return true;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return getBlock().getRelative(getFacing()).equals(otherBlock);
    }

    @Override
    public void onMultiblockRefreshed() {
        Block toDrill = getBlock().getRelative(getFacing());
        if (isProcessing() && (BlockStorage.isPylonBlock(toDrill) || !blocks.contains(toDrill.getType()))) {
            stopProcess();
            return;
        }
        tryStartDrilling();
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure("# # # # o # # # #")
                .addIngredient('#', GuiItems.background())
                .addIngredient('o', outputInventory)
                .build();
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("bar", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.BIODIESEL),
                        fluidCapacity(BaseFluids.BIODIESEL),
                        20,
                        TextColor.fromHexString("#eaa627")
                ))
        ));
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonGuiBlock.super.onBreak(drops, context);
        PylonFluidBufferBlock.super.onBreak(drops, context);
    }
}
