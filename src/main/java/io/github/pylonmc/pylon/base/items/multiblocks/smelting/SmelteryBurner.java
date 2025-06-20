package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.registry.PylonRegistryKey;
import io.github.pylonmc.pylon.core.util.ItemUtils;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.ProgressItem;
import io.github.pylonmc.pylon.core.util.gui.unit.MetricPrefix;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public final class SmelteryBurner extends SmelteryComponent implements PylonGuiBlock, PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("smeltery_burner");

    public static final double BURN_EFFICIENCY = Settings.get(KEY).getOrThrow("burn-efficiency", Double.class);
    public static final double DIMINISHING_RETURN = Settings.get(KEY).getOrThrow("diminishing-returns", Double.class);

    public static final PylonRegistryKey<Fuel> FUELS_KEY = new PylonRegistryKey<>(pylonKey("smeltery_burner_fuels"));
    public static final PylonRegistry<Fuel> FUELS = new PylonRegistry<>(FUELS_KEY);

    static {
        PylonRegistry.addRegistry(FUELS);
    }

    private static final NamespacedKey FUEL_KEY = pylonKey("fuel");
    private static final PersistentDataType<?, Fuel> FUEL_TYPE = PylonSerializers.KEYED.keyedTypeFrom(Fuel.class, FUELS::getOrThrow);
    private static final NamespacedKey SECONDS_ELAPSED_KEY = pylonKey("seconds_elapsed");

    private @Nullable Fuel fuel;
    private double secondsElapsed = 0;

    @SuppressWarnings("unused")
    public SmelteryBurner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        fuel = null;
        secondsElapsed = 0;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public SmelteryBurner(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        fuel = pdc.get(FUEL_KEY, FUEL_TYPE);
        secondsElapsed = pdc.get(SECONDS_ELAPSED_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PdcUtils.setNullable(pdc, FUEL_KEY, FUEL_TYPE, fuel);
        pdc.set(SECONDS_ELAPSED_KEY, PylonSerializers.DOUBLE, secondsElapsed);
    }

    private final VirtualInventory inventory = new VirtualInventory(3);

    private final BurnerProgressItem progressItem = new BurnerProgressItem();

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # f # # # #",
                        "# # # x x x # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('f', progressItem)
                .addIngredient('x', inventory)
                .addIngredient('#', GuiItems.background())
                .build();
    }

    private class BurnerProgressItem extends ProgressItem {
        public BurnerProgressItem() {
            super(Material.BLAZE_POWDER, true);
        }

        @Override
        protected void completeItem(@NotNull ItemStackBuilder builder) {
            Component name;
            List<Component> lore = new ArrayList<>();
            double powerOutput = getCurrentPowerOutput();
            if (fuel != null) {
                name = Component.translatable("pylon.pylonbase.gui.smeltery_burner.burning");
                double energyLeft = fuel.totalJoules * BURN_EFFICIENCY - (powerOutput * secondsElapsed);
                lore.add(Component.translatable(
                        "pylon.pylonbase.gui.smeltery_burner.energy_left",
                        PylonArgument.of(
                                "energy",
                                UnitFormat.JOULES.format(energyLeft)
                                        .significantFigures(3)
                                        .ignorePrefixes(MetricPrefix.Unused.GENERAL)
                                        .autoSelectPrefix()
                        )
                ));
                lore.add(Component.translatable(
                        "pylon.pylonbase.gui.smeltery_burner.power_output",
                        PylonArgument.of(
                                "power",
                                UnitFormat.WATTS.format(powerOutput)
                                        .significantFigures(3)
                                        .ignorePrefixes(MetricPrefix.Unused.GENERAL)
                                        .autoSelectPrefix()
                        )
                ));
            } else {
                name = Component.translatable("pylon.pylonbase.gui.smeltery_burner.not_burning");
            }
            builder.name(name).lore(lore);
        }

        @Override
        public @Nullable Duration getTotalTime() {
            if (fuel == null) return null;
            return Duration.ofSeconds(fuel.burnTimeSeconds);
        }
    }

    private double getCurrentPowerOutput() {
        if (fuel != null) {
            return fuel.totalJoules / fuel.burnTimeSeconds * BURN_EFFICIENCY;
        }
        return 0;
    }

    @Override
    public void tick(double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller != null) {
            if (fuel != null) {
                controller.heat(getCurrentPowerOutput() * deltaSeconds, DIMINISHING_RETURN);
            } else {
                itemLoop:
                for (ItemStack item : inventory.getUnsafeItems()) {
                    if (item == null) continue;
                    for (Fuel fuel : FUELS) {
                        if (ItemUtils.isPylonSimilar(item, fuel.material)) {
                            this.fuel = fuel;
                            item.subtract();
                            secondsElapsed = 0;
                            break itemLoop;
                        }
                    }
                }
            }
        }
        if (fuel != null) {
            secondsElapsed += deltaSeconds;
            progressItem.setProgress(secondsElapsed / fuel.burnTimeSeconds);
            if (secondsElapsed >= fuel.burnTimeSeconds) {
                secondsElapsed = 0;
                fuel = null;
                progressItem.notifyWindows();
            }
        }
    }

    public record Fuel(
            @NotNull NamespacedKey key,
            @NotNull ItemStack material,
            double totalJoules,
            long burnTimeSeconds
    ) implements Keyed {
        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }
    }

    static {
        FUELS.register(new Fuel(
                pylonKey("coal"),
                new ItemStack(Material.COAL),
                333_300_000,
                30
        ));
        FUELS.register(new Fuel(
                pylonKey("coal_dust"),
                PylonItems.COAL_DUST,
                333_300_000,
                30
        ));
        FUELS.register(new Fuel(
                pylonKey("charcoal"),
                new ItemStack(Material.CHARCOAL),
                350_000_000,
                30
        ));
        FUELS.register(new Fuel(
                pylonKey("coke"),
                PylonItems.COKE_DUST,
                500_000_000,
                30
        ));
    }
}
