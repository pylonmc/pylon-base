package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
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


public class PortableFluidTank extends PylonBlock implements FluidTankWithDisplayEntity, PylonInteractBlock {

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

    @SuppressWarnings("unused")
    public PortableFluidTank(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        createFluidDisplay();
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
        if (!event.getAction().isRightClick()) {
            return;
        }

        ItemStack item = event.getItem();
        EquipmentSlot hand = event.getHand();
        if (item == null || hand == null || PylonItem.isPylonItem(item)) {
            return;
        }

        ItemStack newItemStack = null;

        // Inserting water
        if (item.getType() == Material.WATER_BUCKET) {
            event.setUseItemInHand(Event.Result.DENY);

            if (BaseFluids.WATER.equals(getFluidType()) && capacity - getFluidAmount() >= 1000.0) {
                setFluid(getFluidAmount() + 1000.0);
                newItemStack = new ItemStack(Material.BUCKET);
            }

            if (getFluidType() == null) {
                setFluidType(BaseFluids.WATER);
                setFluid(1000.0);
                newItemStack = new ItemStack(Material.BUCKET);
            }
        }

        // Inserting lava
        if (item.getType() == Material.LAVA_BUCKET) {
            event.setUseItemInHand(Event.Result.DENY);

            if (BaseFluids.LAVA.equals(getFluidType()) && capacity - getFluidAmount() >= 1000.0) {
                setFluid(getFluidAmount() + 1000.0);
                newItemStack = new ItemStack(Material.BUCKET);
            }

            if (getFluidType() == null) {
                setFluidType(BaseFluids.LAVA);
                setFluid(1000.0);
                newItemStack = new ItemStack(Material.BUCKET);
            }
        }

        if (item.getType() == Material.BUCKET) {
            event.setUseItemInHand(Event.Result.DENY);

            // Taking water
            if (BaseFluids.WATER.equals(getFluidType()) && getFluidAmount() >= 1000.0) {
                setFluid(getFluidAmount() - 1000.0);
                newItemStack = new ItemStack(Material.WATER_BUCKET);
            }

            // Taking lava
            if (BaseFluids.LAVA.equals(getFluidType()) && getFluidAmount() >= 1000.0) {
                setFluid(getFluidAmount() - 1000.0);
                newItemStack = new ItemStack(Material.LAVA_BUCKET);
            }
        }


        if (newItemStack != null) {
            event.getPlayer().swingHand(hand);
            if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                // This is a hack. When I change the item from within a PlayerInteractEvent, a new event
                // is fired for the new item stack. No idea why. Nor did the guy from the paper team.
                ItemStack finalNewItemStack = newItemStack;
                Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
                    item.subtract();
                    event.getPlayer().give(finalNewItemStack);
                }, 0);
            }
        }
    }
}
