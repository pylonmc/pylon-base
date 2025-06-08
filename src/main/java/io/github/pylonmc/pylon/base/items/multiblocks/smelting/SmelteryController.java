package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleRBTreeMap;
import kotlin.Pair;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

@NullMarked
public final class SmelteryController extends SmelteryComponent
        implements PylonGuiBlock, PylonMultiblock, PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("smeltery_controller");

    // TODO setting
    private static final double FLUID_REACTION_PER_SECOND = 100;

    private static final NamespacedKey TEMPERATURE_KEY = pylonKey("temperature");
    private static final NamespacedKey RUNNING_KEY = pylonKey("running");
    private static final NamespacedKey HEIGHT_KEY = pylonKey("height");
    private static final NamespacedKey CAPACITY_KEY = pylonKey("capacity");
    private static final NamespacedKey COMPONENTS_KEY = pylonKey("components");
    private static final NamespacedKey FLUIDS_KEY = pylonKey("fluids");

    @Getter
    @Setter
    private double temperature;

    @Getter
    @Setter
    private boolean running;

    private final Object2DoubleMap<PylonFluid> fluids = new Object2DoubleRBTreeMap<>(
            Comparator.<PylonFluid, FluidTemperature>comparing(fluid -> fluid.getTag(FluidTemperature.class))
                    .reversed()
                    .thenComparing(fluid -> fluid.getKey().toString())
    );

    @Getter
    private double capacity;

    private int height;
    private final Set<BlockPosition> components = new HashSet<>();

    @SuppressWarnings("unused")
    public SmelteryController(Block block, BlockCreateContext context) {
        super(block, context);

        temperature = 20;
        running = false;
        height = 0;
        capacity = 0;
        components.add(new BlockPosition(getBlock()));

        fluids.defaultReturnValue(-1);
    }

    @SuppressWarnings("unused")
    public SmelteryController(Block block, PersistentDataContainer pdc) {
        super(block, pdc);

        temperature = pdc.getOrDefault(TEMPERATURE_KEY, PylonSerializers.DOUBLE, 20.0);
        running = pdc.getOrDefault(RUNNING_KEY, PylonSerializers.BOOLEAN, false);
        height = pdc.getOrDefault(HEIGHT_KEY, PylonSerializers.INTEGER, 0);
        capacity = pdc.getOrDefault(CAPACITY_KEY, PylonSerializers.DOUBLE, 0D);
        components.addAll(pdc.getOrDefault(COMPONENTS_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.BLOCK_POSITION), Set.of()));
        fluids.putAll(pdc.getOrDefault(FLUIDS_KEY, PylonSerializers.MAP.mapTypeFrom(PylonSerializers.PYLON_FLUID, PylonSerializers.DOUBLE), Map.of()));

        fluids.defaultReturnValue(-1);
    }

    @Override
    public void write(PersistentDataContainer pdc) {
        pdc.set(TEMPERATURE_KEY, PylonSerializers.DOUBLE, temperature);
        pdc.set(RUNNING_KEY, PylonSerializers.BOOLEAN, running);
        pdc.set(HEIGHT_KEY, PylonSerializers.INTEGER, height);
        pdc.set(CAPACITY_KEY, PylonSerializers.DOUBLE, capacity);
        pdc.set(COMPONENTS_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.BLOCK_POSITION), components);
        pdc.set(FLUIDS_KEY, PylonSerializers.MAP.mapTypeFrom(PylonSerializers.PYLON_FLUID, PylonSerializers.DOUBLE), fluids);
    }

    @Override
    public void postBreak() {
        for (BlockPosition pos : components) {
            if (BlockStorage.get(pos) instanceof SmelteryComponent component) {
                component.setController(null);
            }
        }
    }

    // <editor-fold desc="GUI" defaultstate="collapsed">
    private final Item infoItem = new InfoItem();
    private final Item contentsItem = new ContentsItem();

    @Override
    public Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # i # c # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('i', infoItem)
                .addIngredient('c', contentsItem)
                .addIngredient('#', GuiItems.background())
                .build();
    }

    private class InfoItem extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            Material material;
            List<Component> lore = new ArrayList<>();
            if (isFormedAndFullyLoaded()) {
                if (running) {
                    material = Material.GREEN_STAINED_GLASS_PANE;
                    lore.add(Component.translatable("pylon.pylonbase.gui.status.running"));
                } else {
                    material = Material.YELLOW_STAINED_GLASS_PANE;
                    lore.add(Component.translatable("pylon.pylonbase.gui.status.not_running"));
                }
                lore.add(Component.translatable("pylon.pylonbase.gui.status.toggle"));
                lore.add(Component.empty());
                lore.add(Component.translatable(
                        "pylon.pylonbase.gui.smeltery.height",
                        PylonArgument.of("height", UnitFormat.BLOCKS.format(height))
                ));
                lore.add(Component.translatable(
                        "pylon.pylonbase.gui.smeltery.capacity",
                        PylonArgument.of("capacity", UnitFormat.MILLIBUCKETS.format(capacity).decimalPlaces(0))
                ));
                lore.add(Component.translatable(
                        "pylon.pylonbase.gui.smeltery.temperature",
                        PylonArgument.of("temperature", UnitFormat.CELSIUS.format(temperature).decimalPlaces(1))
                ));
            } else {
                material = Material.RED_STAINED_GLASS_PANE;
                lore.add(Component.translatable("pylon.pylonbase.gui.status.incomplete"));
            }
            return ItemStackBuilder.of(material)
                    .name(Component.translatable("pylon.pylonbase.gui.status.name"))
                    .lore(lore);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            if (isFormedAndFullyLoaded()) {
                running = !running;
                notifyWindows();
            }
        }
    }

    private class ContentsItem extends AbstractItem {

        private static final Map<PylonFluid, TextColor> fluidColors = new HashMap<>();

        @Override
        public ItemProvider getItemProvider() {
            List<Component> lore = new ArrayList<>();
            for (Object2DoubleMap.Entry<PylonFluid> entry : fluids.object2DoubleEntrySet()) {
                PylonFluid fluid = entry.getKey();
                double amount = entry.getDoubleValue();
                lore.add(Component.text().build().append(Component.translatable(
                        "pylon.pylonbase.gui.smeltery.contents.fluid",
                        PylonArgument.of(
                                "amount",
                                UnitFormat.MILLIBUCKETS.format(amount)
                                        .decimalPlaces(1)
                                        .unitStyle(Style.empty())
                        ),
                        PylonArgument.of("fluid", fluid.getName())
                )));
            }
            return ItemStackBuilder.of(Material.LAVA_BUCKET)
                    .name(Component.translatable("pylon.pylonbase.gui.smeltery.contents.name"))
                    .lore(lore);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
        }
    }

    private final BlockPosition center = new BlockPosition(
            getBlock().getRelative(
                    ((Directional) getBlock().getBlockData()).getFacing().getOppositeFace(),
                    2
            )
    );
    // </editor-fold>

    // <editor-fold desc="Multiblock" defaultstate="collapsed">
    // @formatter:off
    private static final Vector3i[] MULTIBLOCK_DIRECTIONS = new Vector3i[] {
            new Vector3i(-2, 0, -2), new Vector3i(-2, 0, -1), new Vector3i(-2, 0, 0), new Vector3i(-2, 0, 1), new Vector3i(-2, 0, 2),
            new Vector3i(-1, 0, -2),                                                                                                      new Vector3i(-1, 0, 2),
            new Vector3i(0, 0, -2),                                                                                                       new Vector3i(0, 0, 2),
            new Vector3i(1, 0, -2),                                                                                                       new Vector3i(1, 0, 2),
            new Vector3i(2, 0, -2),  new Vector3i(2, 0, -1),  new Vector3i(2, 0, 0),  new Vector3i(2, 0, 1),  new Vector3i(2, 0, 2),
    };
    private final List<BlockPosition> multiblockPositions = Arrays.stream(MULTIBLOCK_DIRECTIONS)
            .map(center::plus)
            .toList();

    private static final Vector3i[] INSIDE_DIRECTIONS = new Vector3i[] {
            new Vector3i(-1, 0, -1), new Vector3i(-1, 0, 0), new Vector3i(-1, 0, 1),
            new Vector3i(0, 0, -1),  new Vector3i(0, 0, 0),  new Vector3i(0, 0, 1),
            new Vector3i(1, 0, -1),  new Vector3i(1, 0, 0),  new Vector3i(1, 0, 1),
    };
    private final List<BlockPosition> insidePositions = Arrays.stream(INSIDE_DIRECTIONS)
            .map(center::plus)
            .toList();

    private static final Vector3i[] MULTIBLOCK_WITH_AIR = ArrayUtils.addAll(MULTIBLOCK_DIRECTIONS, INSIDE_DIRECTIONS);
    private final List<BlockPosition> multiblockWithAirPositions = Arrays.stream(MULTIBLOCK_WITH_AIR)
            .map(center::plus)
            .toList();
    // @formatter:on

    @Override
    public Set<ChunkPosition> getChunksOccupied() {
        return multiblockPositions.stream()
                .map(BlockPosition::getChunk)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean checkFormed() {
        double previousCapacity = capacity;
        height = 0;
        capacity = 0;

        for (BlockPosition pos : components) {
            if (BlockStorage.get(pos) instanceof SmelteryComponent component) {
                component.setController(null);
            }
        }

        // Check floor
        if (!checkAllComponent(addY(multiblockWithAirPositions, -1))) return false;

        // Check sides up to world height
        for (int i = 0; i < getBlock().getWorld().getMaxHeight(); i++) {
            if (checkAllComponent(addY(multiblockPositions, i))) {
                height++;
                capacity += countAir(addY(insidePositions, i)) * 1000L;
            } else {
                break;
            }
        }

        if (capacity < previousCapacity) {
            double ratio = capacity / previousCapacity;
            for (PylonFluid fluid : fluids.keySet()) {
                double amount = fluids.getDouble(fluid);
                if (amount > 0) {
                    double newAmount = amount * ratio;
                    if (newAmount == 0) {
                        fluids.removeDouble(fluid);
                    } else {
                        fluids.put(fluid, newAmount);
                    }
                }
            }
        }

        return height > 0;
    }

    @Override
    public boolean isPartOfMultiblock(Block otherBlock) {
        if (otherBlock.getY() < center.getY() - 1) return false;
        return otherBlock.getX() >= center.getX() - 2 && otherBlock.getX() <= center.getX() + 2
                && otherBlock.getZ() >= center.getZ() - 2 && otherBlock.getZ() <= center.getZ() + 2;
    }

    private static List<BlockPosition> addY(List<BlockPosition> positions, int y) {
        if (y == 0) return positions;
        return positions.stream()
                .map(pos -> pos.addScalar(0, y, 0))
                .toList();
    }

    private boolean checkAllComponent(List<BlockPosition> positions) {
        Map<BlockPosition, SmelteryComponent> components = new HashMap<>();
        for (BlockPosition pos : positions) {
            if (BlockStorage.get(pos) instanceof SmelteryComponent component) {
                components.put(pos, component);
            } else {
                return false;
            }
        }
        for (Map.Entry<BlockPosition, SmelteryComponent> entry : components.entrySet()) {
            this.components.add(entry.getKey());
            entry.getValue().setController(this);
        }
        return true;
    }

    private static int countAir(List<BlockPosition> positions) {
        int count = 0;
        for (BlockPosition pos : positions) {
            if (pos.getBlock().isEmpty()) {
                count++;
            }
        }
        return count;
    }
    // </editor-fold>

    // <editor-fold desc="Fluid" defaultstate="collapsed">
    public void addFluid(PylonFluid fluid, double amount) {
        Preconditions.checkArgument(fluid.hasTag(FluidTemperature.class), "Fluid does not have a temperature tag");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");

        double amountToAdd = Math.min(amount, capacity - getTotalFluid());
        fluids.mergeDouble(fluid, amountToAdd, Double::sum);

        double originalHeatCapacity = getHeatCapacity();
        double addedHeatCapacity = amountToAdd / 1000.0 * DENSITY * SPECIFIC_HEAT;
        temperature = (originalHeatCapacity * temperature + addedHeatCapacity * 20) / (originalHeatCapacity + addedHeatCapacity);
    }

    public void removeFluid(PylonFluid fluid, double amount) {
        Preconditions.checkArgument(fluid.hasTag(FluidTemperature.class), "Fluid does not have a temperature tag");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");

        fluids.mergeDouble(fluid, -amount, Double::sum);
        if (fluids.getDouble(fluid) <= 0.001) { // Consider anything less than a nanobucket as empty
            fluids.removeDouble(fluid);
        }
    }

    public double getFluidAmount(PylonFluid fluid) {
        return fluids.getDouble(fluid);
    }

    public double getTotalFluid() {
        double sum = 0;
        for (double value : fluids.values()) {
            sum += value;
        }
        if (sum <= 0.001) return 0;
        return sum;
    }

    public @Nullable Pair<PylonFluid, Double> getBottomFluid() {
        Object2DoubleMap.Entry<PylonFluid> lastEntry = null;
        for (var entry : fluids.object2DoubleEntrySet()) {
            lastEntry = entry;
        }
        if (lastEntry == null) return null;
        return new Pair<>(lastEntry.getKey(), lastEntry.getDoubleValue());
    }
    // </editor-fold>

    // <editor-fold desc="Heating" defaultstate="collapsed">
    @SuppressWarnings("SameParameterValue")
    private static double newtonsLawOfCooling(double heatLossCoeff, double t, double t0, double dt) {
        return heatLossCoeff * (t - t0) * dt;
    }

    @SuppressWarnings("SameParameterValue")
    private static double stefanBoltzmannLaw(double emissivity, double t, double t0, double dt) {
        return emissivity * STEFAN_BOLTZMANN_CONSTANT * (Math.pow(t, 4) - Math.pow(t0, 4)) * dt;
    }

    private static final double STEFAN_BOLTZMANN_CONSTANT = 5.67e-8; // W/m^2*K^4
    private static final double SPECIFIC_HEAT = 215; // J/kg*K
    private static final double SPECIFIC_HEAT_AIR = 717; // J/kg*K
    private static final double DENSITY = 698; // kg/m^3
    private static final double DENSITY_AIR = 1.2; // kg/m^3 at sea level
    private static final double HEAT_LOSS_COEFFICIENT = 10; // W/C
    private static final double EMISSIVITY = 0.9;

    private static final double CELSIUS_TO_KELVIN = 273.15;
    private static final double ROOM_TEMPERATURE_KELVIN = 20 + CELSIUS_TO_KELVIN;

    private double getHeatCapacity() {
        return getTotalFluid() / 1000.0 * DENSITY * SPECIFIC_HEAT;
    }

    private double getAirHeatCapacity() {
        return (capacity - getTotalFluid()) / 1000.0 * DENSITY_AIR * SPECIFIC_HEAT_AIR; // Remaining capacity is air
    }

    private void coolNaturally(double dt) {
        // Surface area of the outside
        int surfaceArea = (height * 5) * 4 /* four sides */ + (5 * 5) * 2 /* top and bottom */;
        double kelvin = temperature + CELSIUS_TO_KELVIN;
        double conductiveHeatLoss = newtonsLawOfCooling(
                HEAT_LOSS_COEFFICIENT,
                kelvin,
                ROOM_TEMPERATURE_KELVIN,
                dt
        );
        double radiativeHeatLoss = stefanBoltzmannLaw(
                EMISSIVITY,
                kelvin,
                ROOM_TEMPERATURE_KELVIN,
                dt
        );
        temperature -= ((conductiveHeatLoss + radiativeHeatLoss) * surfaceArea) / (getHeatCapacity() + getAirHeatCapacity());
    }

    public void heat(double joules) {
        Preconditions.checkArgument(!Double.isNaN(joules) && !Double.isInfinite(joules), "Joules cannot be NaN or infinite");
        temperature += joules / (getHeatCapacity() + getAirHeatCapacity());
    }
    // </editor-fold>

    // <editor-fold desc="Recipe" defaultstate="collapsed">
    record Recipe(
            NamespacedKey key,
            Map<PylonFluid, Double> inputFluids,
            Map<PylonFluid, Double> outputFluids,
            double temperature
    ) implements Keyed {

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(pylonKey("smeltery")) {
            @Override
            public void addRecipe(Recipe recipe) {
                super.addRecipe(recipe);
                Map<NamespacedKey, Recipe> recipes = getRegisteredRecipes();
                Map<NamespacedKey, Recipe> newMap = recipes.entrySet().stream()
                        .sorted(
                                Comparator.<Map.Entry<NamespacedKey, Recipe>>comparingDouble(entry -> -entry.getValue().temperature())
                                        .thenComparingInt(entry -> -entry.getValue().inputFluids().size())
                        )
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (existing, replacement) -> existing,
                                () -> new LinkedHashMap<>(recipes.size())
                        ));
                recipes.clear();
                recipes.putAll(newMap);
            }
        };
        static {
            RECIPE_TYPE.register();
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }

    private void performRecipes(double deltaSeconds) {
        if (fluids.isEmpty()) return;
        recipeLoop:
        for (Recipe recipe : Recipe.RECIPE_TYPE) {
            if (recipe.temperature > temperature) continue; // recipe requires higher temperature
            for (var entry : recipe.inputFluids().entrySet()) {
                PylonFluid fluid = entry.getKey();
                double amount = entry.getValue() * deltaSeconds;
                if (fluids.getDouble(fluid) < amount) {
                    continue recipeLoop; // not enough fluid for this recipe
                }
            }

            // Perform recipe
            for (var entry : recipe.inputFluids().entrySet()) {
                PylonFluid fluid = entry.getKey();
                double amount = entry.getValue() * deltaSeconds * FLUID_REACTION_PER_SECOND;
                removeFluid(fluid, amount);
            }
            for (var entry : recipe.outputFluids().entrySet()) {
                PylonFluid fluid = entry.getKey();
                double amount = entry.getValue() * deltaSeconds * FLUID_REACTION_PER_SECOND;
                addFluid(fluid, amount);
            }
        }
    }

    static {
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                pylonKey("redstone_decomposition"),
                Map.of(PylonFluids.REDSTONE_SLURRY, 1D),
                Map.of(PylonFluids.SULFUR, 0.25, PylonFluids.MERCURY, 0.25, PylonFluids.SLURRY, 0.5),
                345
        ));
    }
    // </editor-fold>

    @Override
    public void tick(double deltaSeconds) {
        if (isFormedAndFullyLoaded()) {
            if (running) {
                performRecipes(deltaSeconds);
            }
            coolNaturally(deltaSeconds);
            if (temperature < 0) {
                PylonBase.getInstance().getLogger().warning("Smeltery temperature dropped below 0, something is probably wrong with the heat transfer calculations");
            }
        } else if (height <= 0) { // check height instead because of the brief moment when the multiblock is loaded but not checked yet
            running = false;
        }
        infoItem.notifyWindows();
        contentsItem.notifyWindows();
    }
}
