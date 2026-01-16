package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.PylonConfig;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.TextDisplayBuilder;
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
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class FluidMeter extends PylonBlock implements
        PylonFluidTank,
        PylonDirectionalBlock,
        PylonTickingBlock,
        PylonGuiBlock {

    public static final NamespacedKey MEASUREMENTS_KEY = BaseUtils.baseKey("measurements");
    public static final NamespacedKey NUMBER_OF_MEASUREMENTS_KEY = BaseUtils.baseKey("number_of_measurements");

    public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);
    public final int minNumberOfMeasurements = getSettings().getOrThrow("min-number-of-measurements", ConfigAdapter.INT);
    public final int maxNumberOfMeasurements = getSettings().getOrThrow("max-number-of-measurements", ConfigAdapter.INT);

    private double fluidAddedLastUpdate;
    private final List<Double> measurements;
    private int numberOfMeasurements;

    public static class Item extends PylonItem {

        public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);
        public final int minNumberOfMeasurements = getSettings().getOrThrow("min-number-of-measurements", ConfigAdapter.INT);
        public final int maxNumberOfMeasurements = getSettings().getOrThrow("max-number-of-measurements", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(buffer)),
                    PylonArgument.of(
                            "min-measurement-time",
                            UnitFormat.formatDuration(getDuration(minNumberOfMeasurements), true, true)
                    ),
                    PylonArgument.of(
                            "max-measurement-time",
                            UnitFormat.formatDuration(getDuration(maxNumberOfMeasurements), true, true)
                    )
            );
        }
    }

    public final ItemStackBuilder mainStack = ItemStackBuilder.of(Material.WHITE_CONCRETE)
            .addCustomModelDataString(getKey() + ":main");
    public final ItemStackBuilder topStack = ItemStackBuilder.of(Material.LIGHT_GRAY_CONCRETE)
            .addCustomModelDataString(getKey() + ":top");
    public final ItemStackBuilder projectorStack = ItemStackBuilder.of(Material.LIGHT_BLUE_STAINED_GLASS)
            .addCustomModelDataString(getKey() + ":projector");

    @SuppressWarnings("unused")
    public FluidMeter(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());
        setCapacity(buffer);
        setTickInterval(PylonConfig.FLUID_TICK_INTERVAL);

        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false, 0.25F);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false, 0.25F);

        addEntity("main", new ItemDisplayBuilder()
                .itemStack(mainStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .scale(0.25, 0.25, 0.5)
                )
                .build(block.getLocation().toCenterLocation())
        );
        addEntity("top", new ItemDisplayBuilder()
                .itemStack(topStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .scale(0.2, 0.3, 0.45)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("projector", new ItemDisplayBuilder()
                .itemStack(projectorStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0, 0.125, 0)
                        .rotate(0, Math.PI / 4, 0)
                        .scale(0.12, 0.12, 0.12)
                )
                .build(block.getLocation().toCenterLocation())
        );

        addEntity("flow_rate", new TextDisplayBuilder()
                .transformation(new TransformBuilder()
                        .translate(new Vector3d(0.0, 0.35, 0.0))
                        .scale(0.3, 0.3, 0.3)
                )
                .billboard(Display.Billboard.VERTICAL)
                .backgroundColor(Color.fromARGB(0, 0, 0, 0))
                .text(UnitFormat.MILLIBUCKETS_PER_SECOND.format(0).asComponent())
                .build(block.getLocation().toCenterLocation())
        );
        addEntity("fluid", new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .translate(new Vector3d(0.0, 0.3, 0.0))
                        .scale(0.07, 0.07, 0.07)
                )
                .itemStack(new ItemStack(Material.BARRIER))
                .billboard(Display.Billboard.VERTICAL)
                .build(block.getLocation().toCenterLocation())
        );

        setDisableBlockTextureEntity(true);

        measurements = new ArrayList<>();
        numberOfMeasurements = minNumberOfMeasurements;
    }

    @SuppressWarnings("unused")
    public FluidMeter(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        setDisableBlockTextureEntity(true);

        measurements = new ArrayList<>(pdc.get(MEASUREMENTS_KEY, PylonSerializers.LIST.listTypeFrom(PylonSerializers.DOUBLE)));
        numberOfMeasurements = pdc.get(NUMBER_OF_MEASUREMENTS_KEY, PylonSerializers.INTEGER);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(MEASUREMENTS_KEY, PylonSerializers.LIST.listTypeFrom(PylonSerializers.DOUBLE), measurements);
        pdc.set(NUMBER_OF_MEASUREMENTS_KEY, PylonSerializers.INTEGER, numberOfMeasurements);
    }

    @Override
    public @NotNull Gui getGui() {
        return Gui.normal()
                .setStructure("# # # # m # # # #")
                .addIngredient('#', GuiItems.background())
                .addIngredient('m', new MeasurementDurationItem())
                .build();
    }

    @Override
    public void onFluidAdded(@NotNull PylonFluid fluid, double amount) {
        PylonFluidTank.super.onFluidAdded(fluid, amount);
        fluidAddedLastUpdate = amount;
        getHeldEntityOrThrow(ItemDisplay.class, "fluid").setItemStack(fluid.getItem());
    }

    @Override
    public void tick() {
        measurements.add(fluidAddedLastUpdate);
        fluidAddedLastUpdate = 0.0;
        if (measurements.size() > numberOfMeasurements) {
            for (int i = 0; i < measurements.size() - numberOfMeasurements; i++) {
                measurements.removeFirst();
            }
        }
        double total = measurements.stream()
                .mapToDouble(x -> x)
                .sum();
        double average = (total / measurements.size()) * 20.0 / getTickInterval();
        Component component = UnitFormat.MILLIBUCKETS_PER_SECOND.format(average).decimalPlaces(0).asComponent();
        getHeldEntityOrThrow(TextDisplay.class, "flow_rate").text(component);
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonFluidTank.super.onBreak(drops, context);
        PylonGuiBlock.super.onBreak(drops, context);
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return true;
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
                ),
                PylonArgument.of("duration", UnitFormat.formatDuration(getDuration(numberOfMeasurements), true, true))
        ));
    }

    public static Duration getDuration(int numberOfMeasurements) {
        return Duration.ofMillis((long) numberOfMeasurements * PylonConfig.FLUID_TICK_INTERVAL * 50);
    }

    public class MeasurementDurationItem extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            return ItemStackBuilder.of(Material.WHITE_CONCRETE)
                    .name(Component.translatable("pylon.pylonbase.gui.fluid_meter.name").arguments(
                            PylonArgument.of("measurement-duration", UnitFormat.formatDuration(getDuration(numberOfMeasurements), true, true))
                    ))
                    .lore(Component.translatable("pylon.pylonbase.gui.fluid_meter.lore"));
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            int newValue;
            if (clickType.isLeftClick()) {
                newValue = numberOfMeasurements + (clickType.isShiftClick() ? 10 : 1);
            } else if (clickType.isRightClick()) {
                newValue = numberOfMeasurements + (clickType.isShiftClick() ? -10 : -1);
            } else {
                newValue = numberOfMeasurements;
            }
            numberOfMeasurements = Math.clamp(newValue, minNumberOfMeasurements, maxNumberOfMeasurements);
            notifyWindows();
        }
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Inventory> createInventoryMapping() {
        return Map.of();
    }
}
