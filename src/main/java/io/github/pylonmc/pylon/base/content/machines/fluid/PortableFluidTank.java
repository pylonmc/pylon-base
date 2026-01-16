package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class PortableFluidTank extends PylonBlock implements PylonFluidTank, PylonInteractBlock {

    public static class Item extends PylonItem {
        public static final NamespacedKey FLUID_AMOUNT_KEY = baseKey("fluid_amount");
        public static final NamespacedKey FLUID_TYPE_KEY = baseKey("fluid_type");

        @Getter
        private final double capacity = getSettings().getOrThrow("capacity", ConfigAdapter.DOUBLE);

        @Getter
        private final List<FluidTemperature> allowedTemperatures = getSettings().getOrThrow(
                "allowed-temperatures",
                ConfigAdapter.LIST.from(ConfigAdapter.FLUID_TEMPERATURE)
        );

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        public @Nullable PylonFluid getFluid() {
            return getStack().getPersistentDataContainer().get(FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID);
        }

        public double getAmount() {
            return getStack().getPersistentDataContainer().getOrDefault(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, 0.0);
        }

        public void setFluid(@Nullable PylonFluid fluid) {
            getStack().editPersistentDataContainer(pdc -> PylonUtils.setNullable(pdc, FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID, fluid));
        }

        public void setAmount(double amount) {
            getStack().editPersistentDataContainer(pdc -> pdc.set(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, amount));
        }

        @Override
        public @Nullable PylonBlock place(@NotNull BlockCreateContext context) {
            PortableFluidTank tank = (PortableFluidTank) getSchema().place(context);
            if (tank != null) {
                tank.setFluidType(getFluid());
                tank.setFluid(getAmount());
            }
            return tank;
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("fluid", getFluid() == null
                            ? Component.translatable("pylon.pylonbase.fluid.none")
                            : getFluid().getName()
                    ),
                    PylonArgument.of("amount", Math.round(getAmount())),
                    PylonArgument.of("capacity", UnitFormat.MILLIBUCKETS.format(capacity)),
                    PylonArgument.of("temperatures", Component.join(
                            JoinConfiguration.separator(Component.text(", ")),
                            allowedTemperatures.stream()
                                    .map(FluidTemperature::getValueText)
                                    .collect(Collectors.toList())
                    ))
            );
        }
    }

    public final double capacity = getSettings().getOrThrow("capacity", ConfigAdapter.DOUBLE);

    public final List<FluidTemperature> allowedTemperatures = getSettings().getOrThrow(
            "allowed-temperatures",
            ConfigAdapter.LIST.from(ConfigAdapter.FLUID_TEMPERATURE)
    );

    private int lastDisplayUpdate = -1;

    @SuppressWarnings("unused")
    public PortableFluidTank(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        addEntity("fluid", new ItemDisplayBuilder()
                .build(getBlock().getLocation().toCenterLocation())
        );
        createFluidPoint(FluidPointType.INPUT, BlockFace.UP);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.DOWN);
        setCapacity(capacity);
    }

    @SuppressWarnings("unused")
    public PortableFluidTank(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return fluid.hasTag(FluidTemperature.class)
                && allowedTemperatures.contains(fluid.getTag(FluidTemperature.class));
    }

    @Override
    public void setFluidType(@Nullable PylonFluid fluid) {
        PylonFluidTank.super.setFluidType(fluid);
        getFluidDisplay().setItemStack(fluid == null ? null : fluid.getItem());
    }

    @Override
    public boolean setFluid(double amount) {
        double oldAmount = getFluidAmount();
        boolean result = PylonFluidTank.super.setFluid(amount);
        amount = getFluidAmount();
        if (lastDisplayUpdate == -1 || (result && oldAmount != amount)) {
            float scale = (float) (0.9F * amount / capacity);
            ItemDisplay fluidDisplay = getFluidDisplay();
            fluidDisplay.setInterpolationDelay(Math.min(-3 + (fluidDisplay.getTicksLived() - lastDisplayUpdate), 0));
            fluidDisplay.setInterpolationDuration(4);
            fluidDisplay.setTransformationMatrix(new TransformBuilder()
                    .translate(0.0, -0.45 + scale / 2, 0.0)
                    .scale(0.9, scale, 0.9)
                    .buildForItemDisplay()
            );
            lastDisplayUpdate = fluidDisplay.getTicksLived();
        }
        return result;
    }

    public @NotNull ItemDisplay getFluidDisplay() {
        return getHeldEntityOrThrow(ItemDisplay.class, "fluid");
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
    public @Nullable ItemStack getDropItem(@NotNull BlockBreakContext context) {
        return getPickItem();
    }

    @Override
    public @Nullable ItemStack getPickItem() {
        // TODO implement clone for PylonItem and just clone it
        ItemStack stack = PylonRegistry.ITEMS.getOrThrow(getKey()).getItemStack();

        Item item = new Item(stack);
        item.setFluid(getFluidType());
        item.setAmount(getFluidAmount());

        return stack;
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        BaseUtils.handleFluidTankRightClick(this, event);
    }
}
