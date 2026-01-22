package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class FluidAccumulator extends PylonBlock implements
        PylonDirectionalBlock,
        PylonFluidTank,
        PylonGuiBlock {

    public static final NamespacedKey IS_DISCHARGING_KEY = baseKey("is_discharging");

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.WHITE_CONCRETE)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder lampStack = ItemStackBuilder.of(Material.REDSTONE_LAMP)
            .addCustomModelDataString(getKey() + ":lamp");

    public final int minAmount = getSettings().getOrThrow("min-amount", ConfigAdapter.INT);
    public final int maxAmount = getSettings().getOrThrow("max-amount", ConfigAdapter.INT);

    private boolean isDischarging;

    public static class Item extends PylonItem {

        public final int minAmount = getSettings().getOrThrow("min-amount", ConfigAdapter.INT);
        public final int maxAmount = getSettings().getOrThrow("max-amount", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of(
                            "min-amount",
                            UnitFormat.MILLIBUCKETS.format(minAmount)
                    ),
                    PylonArgument.of(
                            "max-amount",
                            UnitFormat.MILLIBUCKETS.format(maxAmount)
                    )
            );
        }
    }

    @SuppressWarnings("unused")
    public FluidAccumulator(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        this.isDischarging = false;

        setCapacity(minAmount);
        setFacing(context.getFacing());

        addEntity("main", new ItemDisplayBuilder()
            .itemStack(mainStack)
            .transformation(new TransformBuilder()
                .lookAlong(getFacing())
                .scale(0.25, 0.25, 0.5)
            )
            .build(block.getLocation().toCenterLocation())
        );
        addEntity("lamp", new BlockDisplayBuilder()
                .blockData(Material.REDSTONE_LAMP.createBlockData())
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .scale(0.2, 0.3, 0.45)
                )
                .build(block.getLocation().toCenterLocation())
        );

        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false, 0.25F);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false, 0.25F);
        setDisableBlockTextureEntity(true);
    }

    @SuppressWarnings("unused")
    public FluidAccumulator(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        this.isDischarging = pdc.get(IS_DISCHARGING_KEY, PylonSerializers.BOOLEAN);

        setDisableBlockTextureEntity(true);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(IS_DISCHARGING_KEY, PylonSerializers.BOOLEAN, isDischarging);
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("bars", BaseUtils.createFluidAmountBar(
                        getFluidAmount(),
                        getFluidCapacity(),
                        20,
                        TextColor.color(200, 255, 255)
                )),
                PylonArgument.of("fluid", getFluidType() == null
                        ? Component.translatable("pylon.pylonbase.fluid.none")
                        : getFluidType().getName()
                )
        ));
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return true;
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure("# # # # m # # # #")
                .addIngredient('#', GuiItems.background())
                .addIngredient('m', new AmountItem())
                .build();
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid) {
        if (getBlock().isBlockIndirectlyPowered()) {
            return 0.0;
        }

        if (getFluidAmount() < 1.0e-6) {
            isDischarging = false;
            getHeldEntityOrThrow(BlockDisplay.class, "lamp")
                .setBlock(Material.REDSTONE_LAMP.createBlockData("[lit=false]"));
        }

        if (isDischarging) {
            return 0.0;
        }

        return PylonFluidTank.super.fluidAmountRequested(fluid);
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids() {
        if (getBlock().isBlockIndirectlyPowered()) {
            return PylonFluidTank.super.getSuppliedFluids();
        }

        if (getFluidSpaceRemaining() < 1.0e-6) {
            isDischarging = true;
            getHeldEntityOrThrow(BlockDisplay.class, "lamp")
                    .setBlock(Material.REDSTONE_LAMP.createBlockData("[lit=true]"));
        }

        if (isDischarging) {
            return PylonFluidTank.super.getSuppliedFluids();
        }

        return Map.of();
    }

    public class AmountItem extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            return ItemStackBuilder.of(Material.WHITE_CONCRETE)
                    .name(Component.translatable("pylon.pylonbase.gui.fluid_accumulator.name").arguments(
                            PylonArgument.of("amount", UnitFormat.MILLIBUCKETS.format(getFluidCapacity()))
                    ))
                    .lore(Component.translatable("pylon.pylonbase.gui.fluid_accumulator.lore"));
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            double delta = clickType.isShiftClick() ? 100 : 10;
            double newAmount;
            if (clickType.isLeftClick()) {
                newAmount = getFluidCapacity() + delta;
            } else if (clickType.isRightClick()) {
                newAmount = getFluidCapacity() - delta;
            } else {
                newAmount = getFluidCapacity();
            }
            newAmount = Math.clamp(newAmount, minAmount, maxAmount);
            setCapacity(newAmount);
            notifyWindows();
        }
    }
}
