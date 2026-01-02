package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.content.machines.fluid.gui.IntRangeInventory;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.component.AdventureComponentWrapper;
import xyz.xenondevs.invui.window.Window;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class FluidAccumulator extends PylonBlock implements PylonDirectionalBlock, PylonFluidTank, PylonInteractBlock {

    public final ItemStack mainStack = ItemStackBuilder.of(Material.WHITE_CONCRETE)
        .addCustomModelDataString(getKey() + ":main")
        .build();
    public final ItemStack noFluidStack = ItemStackBuilder.of(Material.RED_CONCRETE)
        .addCustomModelDataString(getKey() + ":fluid:none")
        .build();

    public final int buffer = getSettings().getOrThrow("buffer", ConfigAdapter.INT);
    public final IntRangeInventory regulator;

    // when true, starts the output and stops the input, opposite when false
    private boolean outputReady;

    public static final NamespacedKey AMOUNT_KEY = baseKey("amount");
    public static final NamespacedKey OUTPUT_READY_KEY = baseKey("output_ready");

    @SuppressWarnings("unused")
    public FluidAccumulator(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        this.regulator = new IntRangeInventory(
            BaseItems.FLUID_ACCUMULATOR,
            () -> buffer,
            Component.translatable("pylon.pylonbase.item.fluid_accumulator.gui.add"),
            Component.translatable("pylon.pylonbase.item.fluid_accumulator.gui.dec"),
            Component.translatable("pylon.pylonbase.item.fluid_accumulator.gui.amount")
        );
        this.outputReady = false;
        setCapacity(buffer);

        Player player = ((BlockCreateContext.PlayerPlace) context).getPlayer();

        // fatty filter for now todo: add a proper unique display
        addEntity("main", new ItemDisplayBuilder()
            .itemStack(mainStack)
            .transformation(new TransformBuilder()
                .lookAlong(getFacing())
                .scale(0.5, 0.25, 0.5)
            )
            .build(block.getLocation().toCenterLocation())
        );
        addEntity("fluid", new ItemDisplayBuilder()
            .itemStack(noFluidStack)
            .transformation(new TransformBuilder()
                .lookAlong(getFacing())
                .scale(0.3, 0.3, 0.3)
            )
            .build(block.getLocation().toCenterLocation())
        );
        createFluidPoint(FluidPointType.INPUT, BlockFace.EAST, context, false, 0.3F);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.WEST, context, false, 0.3F);
        setDisableBlockTextureEntity(true);
    }

    @SuppressWarnings("unused")
    public FluidAccumulator(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        this.regulator = new IntRangeInventory(
            BaseItems.FLUID_ACCUMULATOR,
            () -> buffer,
            pdc.get(AMOUNT_KEY, PylonSerializers.INTEGER),
            Component.translatable("pylon.pylonbase.item.fluid_accumulator.gui.add"),
            Component.translatable("pylon.pylonbase.item.fluid_accumulator.gui.dec"),
            Component.translatable("pylon.pylonbase.item.fluid_accumulator.gui.amount")
        );
        this.outputReady = pdc.get(OUTPUT_READY_KEY, PylonSerializers.BOOLEAN);
        setCapacity(regulator.getAmount());
        setDisableBlockTextureEntity(true);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(AMOUNT_KEY, PylonSerializers.INTEGER, regulator.getAmount());
        pdc.set(OUTPUT_READY_KEY, PylonSerializers.BOOLEAN, outputReady);
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return true;
    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()
            || event.getPlayer().isSneaking()
            || event.getHand() != EquipmentSlot.HAND
            || event.useInteractedBlock() == Event.Result.DENY) {
            return;
        }

        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);

        Window.single()
            .setGui(regulator.makeGui())
            .setTitle(new AdventureComponentWrapper(getNameTranslationKey()))
            .setViewer(event.getPlayer())
            .addCloseHandler(() -> setCapacity(regulator.getAmount()))
            .build()
            .open();
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid) {
        if (getBlock().isBlockIndirectlyPowered()) return 0.0;

        if (getFluidAmount() == 0.0) {
            outputReady = false;
            return PylonFluidTank.super.fluidAmountRequested(fluid);
        }

        if (outputReady) return 0.0;

        return PylonFluidTank.super.fluidAmountRequested(fluid);
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids() {
        if (getBlock().isBlockIndirectlyPowered()) return PylonFluidTank.super.getSuppliedFluids();

        if (getFluidSpaceRemaining() == 0.0) {
            outputReady = true;
            return PylonFluidTank.super.getSuppliedFluids();
        }

        if (outputReady) return PylonFluidTank.super.getSuppliedFluids();

        return Map.of();
    }
}
