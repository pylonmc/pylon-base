package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.context.BlockItemContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
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
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class PortableFluidTank extends PylonBlock implements PylonFluidBlock, PylonEntityHolderBlock, PylonInteractableBlock {

    public static class Item extends PylonItem {

        @Getter
        private final double capacity = getSettings().getOrThrow("capacity", Double.class);

        @Getter
        @SuppressWarnings("unchecked")
        private final List<FluidTemperature> allowedFluids = ((List<String>) getSettings().getOrThrow("allow-fluids", List.class)).stream()
                .map(s -> FluidTemperature.valueOf(s.toUpperCase(Locale.ROOT)))
                .toList();

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
            getStack().editPersistentDataContainer(pdc -> PdcUtils.setNullable(pdc, FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID, fluid));
        }

        public void setAmount(double amount) {
            getStack().editPersistentDataContainer(pdc -> pdc.set(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, amount));
        }

        @Override
        public @Nullable PylonBlock place(@NotNull BlockCreateContext context) {
            PortableFluidTank tank = (PortableFluidTank) getSchema().place(context);
            if (tank != null) {
                tank.setFluid(getFluid());
                tank.setAmount(getAmount());
            }
            return tank;
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "fluid", Component.translatable("pylon.pylonbase.fluid." + (getFluid() == null ? "none" : getFluid().getKey().getKey())),
                    "amount", Component.text(Math.round(getAmount())),
                    "capacity", UnitFormat.MILLIBUCKETS.format(capacity),
                    "fluids", Component.join(
                            JoinConfiguration.separator(Component.text(", ")),
                            allowedFluids.stream()
                                    .map(FluidTemperature::getValueText)
                                    .collect(Collectors.toList())
                    )
            );
        }
    }

    public static final NamespacedKey FLUID_AMOUNT_KEY = baseKey("fluid_amount");
    private static final NamespacedKey FLUID_TYPE_KEY = baseKey("fluid_type");

    public final double capacity = getSettings().getOrThrow("capacity", Double.class);
    @SuppressWarnings("unchecked")
    public final List<FluidTemperature> allowedFluids = ((List<String>) getSettings().getOrThrow("allow-fluids", List.class)).stream()
            .map(s -> FluidTemperature.valueOf(s.toUpperCase(Locale.ROOT)))
            .collect(Collectors.toList());

    private double fluidAmount;
    private @Nullable PylonFluid fluidType;

    @SuppressWarnings("unused")
    public PortableFluidTank(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public PortableFluidTank(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        fluidAmount = pdc.get(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
        fluidType = pdc.get(FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, fluidAmount);
        PdcUtils.setNullable(pdc, FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID, fluidType);
    }

    @Override
    public @NotNull Map<String, PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        return Map.of(
                "fluid", new SimpleItemDisplay(new ItemDisplayBuilder()
                        .build(getBlock().getLocation().toCenterLocation())),
                "input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.UP),
                "output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.DOWN)
        );
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(double deltaSeconds) {
        return fluidType == null
                ? Map.of()
                : Map.of(fluidType, fluidAmount);
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getRequestedFluids(double deltaSeconds) {
        // If no fluid contained, allow any fluid to be added
        if (fluidType == null) {
            return PylonRegistry.FLUIDS.getValues()
                    .stream()
                    .filter(fluid -> allowedFluids.contains(fluid.getTag(FluidTemperature.class)))
                    .collect(Collectors.toMap(Function.identity(), key -> capacity));
        }

        // If tank full, don't request anything
        if (fluidAmount >= capacity) {
            return Map.of();
        }

        // Otherwise, only allow more of the stored fluid to be added
        return Map.of(fluidType, capacity - fluidAmount);
    }

    @Override
    public void addFluid(@NotNull PylonFluid fluid, double amount) {
        if (!fluid.equals(fluidType)) {
            setFluid(fluid);
        }
        setAmount(fluidAmount + amount);
    }

    @Override
    public void removeFluid(@NotNull PylonFluid fluid, double amount) {
        setAmount(fluidAmount - amount);
    }

    public void setFluid(@Nullable PylonFluid fluid) {
        this.fluidType = fluid;
        getFluidDisplay().getEntity().setItemStack(fluid == null ? null : new ItemStack(fluid.getMaterial()));
    }

    public void setAmount(double amount) {
        this.fluidAmount = amount;

        if (fluidAmount < 1.0e-6) {
            setFluid(null);
            fluidAmount = 0.0;
        }
        
        updateFluidDisplayHeight();
    }

    public void updateFluidDisplayHeight() {
        float scale = (float) (0.9F * fluidAmount / capacity);
        getFluidDisplay().getEntity().setTransformationMatrix(new TransformBuilder()
                    .translate(0.0, -0.45 + scale / 2, 0.0)
                    .scale(0.9, scale, 0.9)
                    .buildForItemDisplay()
        );
    }

    public @NotNull SimpleItemDisplay getFluidDisplay() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "fluid");
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        Component info;
        if (fluidType == null) {
            info = Component.translatable("pylon.pylonbase.waila.fluid_tank.empty");
        } else {
            info = Component.translatable(
                    "pylon.pylonbase.waila.fluid_tank.filled",
                    PylonArgument.of("amount", Math.round(fluidAmount)),
                    PylonArgument.of("capacity", UnitFormat.MILLIBUCKETS.format(capacity)
                            .decimalPlaces(0)
                            .unitStyle(Style.empty())
                    ),
                    PylonArgument.of("fluid", fluidType.getName())
            );
        }
        return new WailaConfig(getName(), Map.of("info", info));
    }

    @Override
    public @Nullable ItemStack getItem(@NotNull BlockItemContext context) {
        // TODO implement clone for PylonItem and just clone it
        ItemStack stack = PylonRegistry.ITEMS.getOrThrow(getKey()).getItemStack();

        Item item = new Item(stack);
        item.setFluid(fluidType);
        item.setAmount(fluidAmount);

        return stack;
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }

        ItemStack item = event.getItem();
        EquipmentSlot hand = event.getHand();
        if (item == null || hand != EquipmentSlot.HAND || PylonItem.isPylonItem(item)) {
            return;
        }

        ItemStack newItemStack = null;

        // Inserting water
        if (item.getType() == Material.WATER_BUCKET) {
            event.setUseItemInHand(Event.Result.DENY);

            if (BaseFluids.WATER.equals(fluidType) && capacity - fluidAmount >= 1000.0) {
                setAmount(fluidAmount + 1000.0);
                newItemStack = new ItemStack(Material.BUCKET);
            }

            if (fluidType == null) {
                setFluid(BaseFluids.WATER);
                setAmount(1000.0);
                newItemStack = new ItemStack(Material.BUCKET);
            }
        }

        // Inserting lava
        if (item.getType() == Material.LAVA_BUCKET) {
            event.setUseItemInHand(Event.Result.DENY);

            if (BaseFluids.LAVA.equals(fluidType) && capacity - fluidAmount >= 1000.0) {
                setAmount(fluidAmount + 1000.0);
                newItemStack = new ItemStack(Material.BUCKET);
            }

            if (fluidType == null) {
                setFluid(BaseFluids.LAVA);
                setAmount(1000.0);
                newItemStack = new ItemStack(Material.BUCKET);
            }
        }

        if (item.getType() == Material.BUCKET) {
            event.setUseItemInHand(Event.Result.DENY);

            // Taking water
            if (BaseFluids.WATER.equals(fluidType) && fluidAmount >= 1000.0) {
                setAmount(fluidAmount - 1000.0);
                newItemStack = new ItemStack(Material.WATER_BUCKET);
            }

            // Taking lava
            if (BaseFluids.LAVA.equals(fluidType) && fluidAmount >= 1000.0) {
                setAmount(fluidAmount - 1000.0);
                newItemStack = new ItemStack(Material.LAVA_BUCKET);
            }
        }

        ItemStack finalNewItemStack = newItemStack;
        if (finalNewItemStack != null) {
            // This is a hack. When I change the item from within a PlayerInteractEvent, a new event
            // is fired for the new item stack. No idea why. Nor did the guy from the paper team.
            Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
                item.subtract();
                event.getPlayer().give(finalNewItemStack);
            }, 0);
        }
    }
}
