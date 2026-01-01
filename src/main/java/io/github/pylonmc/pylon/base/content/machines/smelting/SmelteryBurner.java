package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonProcessor;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.ProgressItem;
import kotlin.Pair;
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
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class SmelteryBurner extends SmelteryComponent implements PylonGuiBlock, PylonTickingBlock, PylonProcessor {

    public static final NamespacedKey FUELS_KEY = baseKey("smeltery_burner_fuels");
    public static final PylonRegistry<Fuel> FUELS = new PylonRegistry<>(FUELS_KEY);

    static {
        PylonRegistry.addRegistry(FUELS);
    }

    private static final NamespacedKey FUEL_KEY = baseKey("fuel");
    private static final PersistentDataType<?, Fuel> FUEL_TYPE = PylonSerializers.KEYED.keyedTypeFrom(Fuel.class, FUELS::getOrThrow);

    private @Nullable Fuel fuel;

    private final ItemStackBuilder notBurningProgressItem = ItemStackBuilder.of(Material.BLAZE_POWDER)
            .name(Component.translatable("pylon.pylonbase.gui.smeltery_burner.not_burning"));
    private final ItemStackBuilder burningProgressItem = ItemStackBuilder.of(Material.BLAZE_POWDER)
            .name(Component.translatable("pylon.pylonbase.gui.smeltery_burner.burning"));

    private final VirtualInventory inventory = new VirtualInventory(3);
    private final ProgressItem progressItem = new ProgressItem(notBurningProgressItem);

    @SuppressWarnings("unused")
    public SmelteryBurner(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setTickInterval(SmelteryController.TICK_INTERVAL);

        fuel = null;
    }

    @SuppressWarnings("unused")
    public SmelteryBurner(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        fuel = pdc.get(FUEL_KEY, FUEL_TYPE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, FUEL_KEY, FUEL_TYPE, fuel);
    }

    @Override
    public void postInitialise() {
        setProcessProgressItem(progressItem);
    }

    @Override
    public @NotNull Map<String, Pair<String, Integer>> getBlockTextureProperties() {
        var properties = super.getBlockTextureProperties();
        properties.put("lit", new Pair<>(fuel != null ? "true" : "false", 2));
        return properties;
    }

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

    @Override
    public void tick() {
        SmelteryController controller = getController();
        if (controller == null || !controller.isRunning()) {
            return;
        }

        progressProcess(getTickInterval());

        if (fuel != null) {
            controller.heatAsymptotically(getTickInterval() / 20.0, fuel.temperature);
            return;
        }

        itemLoop:
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) {
                continue;
            }

            for (Fuel fuel : FUELS) {
                if (!PylonUtils.isPylonSimilar(item, fuel.material)) {
                    continue;
                }

                this.fuel = fuel;
                progressItem.setItemStackBuilder(burningProgressItem);
                inventory.setItem(null, i, item.subtract());
                startProcess(Math.round(fuel.burnTimeSeconds * 20));
                refreshBlockTextureItem();
                break itemLoop;
            }
        }
    }

    @Override
    public void onProcessFinished() {
        progressItem.setItemStackBuilder(notBurningProgressItem);
        refreshBlockTextureItem();
        fuel = null;
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Inventory> createInventoryMapping() {
        return Map.of("fuels", inventory);
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
    }
}
