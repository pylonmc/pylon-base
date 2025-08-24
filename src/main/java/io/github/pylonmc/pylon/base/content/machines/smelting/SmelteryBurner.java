package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.registry.PylonRegistryKey;
import io.github.pylonmc.pylon.core.util.ItemUtils;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.ProgressItem;
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

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class SmelteryBurner extends SmelteryComponent implements PylonGuiBlock, PylonTickingBlock {

    public static final PylonRegistryKey<Fuel> FUELS_KEY = new PylonRegistryKey<>(baseKey("smeltery_burner_fuels"));
    public static final PylonRegistry<Fuel> FUELS = new PylonRegistry<>(FUELS_KEY);

    static {
        PylonRegistry.addRegistry(FUELS);
    }

    private static final NamespacedKey FUEL_KEY = baseKey("fuel");
    private static final PersistentDataType<?, Fuel> FUEL_TYPE = PylonSerializers.KEYED.keyedTypeFrom(Fuel.class, FUELS::getOrThrow);
    private static final NamespacedKey SECONDS_ELAPSED_KEY = baseKey("seconds_elapsed");

    private @Nullable Fuel fuel;
    private double secondsElapsed;

    @SuppressWarnings("unused")
    public SmelteryBurner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setTickInterval(SmelteryController.TICK_INTERVAL);

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
            builder.name(Component.translatable(
                    fuel != null
                            ? "pylon.pylonbase.gui.smeltery_burner.burning"
                            : "pylon.pylonbase.gui.smeltery_burner.not_burning"
            ));
        }

        @Override
        public @Nullable Duration getTotalTime() {
            if (fuel == null) return null;
            return Duration.ofSeconds(fuel.burnTimeSeconds);
        }
    }

    @Override
    public void tick(double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller != null && controller.isRunning()) {
            if (fuel != null) {
                controller.heatAsymptotically(deltaSeconds, fuel.temperature);
            } else {
                itemLoop:
                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack item = inventory.getItem(i);
                    if (item == null) continue;
                    for (Fuel fuel : FUELS) {
                        if (ItemUtils.isPylonSimilar(item, fuel.material)) {
                            this.fuel = fuel;
                            item.subtract();
                            inventory.setItem(null, i, item);
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

    // TODO display fuels
    public record Fuel(
            @NotNull NamespacedKey key,
            @NotNull ItemStack material,
            double temperature,
            long burnTimeSeconds
    ) implements Keyed {
        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }
    }

    static {
        FUELS.register(new Fuel(
                baseKey("coal"),
                new ItemStack(Material.COAL),
                1100,
                30
        ));
        FUELS.register(new Fuel(
                baseKey("coal_dust"),
                BaseItems.COAL_DUST,
                1100,
                30
        ));
        FUELS.register(new Fuel(
                baseKey("charcoal"),
                new ItemStack(Material.CHARCOAL),
                1100,
                30
        ));
        FUELS.register(new Fuel(
                baseKey("carbon"),
                BaseItems.CARBON_DUST,
                1600,
                60
        ));
    }
}
