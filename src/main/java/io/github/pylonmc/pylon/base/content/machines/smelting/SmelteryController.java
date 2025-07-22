package io.github.pylonmc.pylon.base.content.machines.smelting;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.entities.SimpleTextDisplay;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.base.util.HslColor;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformUtil;
import io.github.pylonmc.pylon.core.event.PylonBlockUnloadEvent;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
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
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.Item;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

@Slf4j
public final class SmelteryController extends SmelteryComponent
        implements PylonGuiBlock, PylonMultiblock, PylonTickingBlock, PylonUnloadBlock, PylonEntityHolderBlock {

    public static final double FLUID_REACTION_PER_SECOND = Settings.get(BaseKeys.SMELTERY_CONTROLLER).getOrThrow("fluid-reaction-per-second", Double.class);

    private static final NamespacedKey TEMPERATURE_KEY = baseKey("temperature");
    private static final NamespacedKey RUNNING_KEY = baseKey("running");
    private static final NamespacedKey HEIGHT_KEY = baseKey("height");
    private static final NamespacedKey CAPACITY_KEY = baseKey("capacity");
    private static final NamespacedKey COMPONENTS_KEY = baseKey("components");
    private static final NamespacedKey FLUIDS_KEY = baseKey("fluids");

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
    public SmelteryController(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        Location location = center.getLocation().add(-1, 0, -1);
        int counter = 0;
        for (int x = 0; x < PIXELS_PER_SIDE; x++) {
            for (int z = 0; z < PIXELS_PER_SIDE; z++) {
                Location relative = location.clone().add((double) x / RESOLUTION, 0, (double) z / RESOLUTION);
                TextDisplay display = BaseUtils.spawnUnitSquareTextDisplay(relative, BaseUtils.METAL_GRAY);
                display.setTransformationMatrix(
                        TransformUtil.transformationToMatrix(display.getTransformation())
                                .translateLocal(0, -1, 0) // move the origin so it will be correct after rotation
                                .rotateLocalX((float) Math.toRadians(-90))
                                .scaleLocal(1f / RESOLUTION)
                );
                display.setBrightness(new Display.Brightness(15, 15));
                addEntity("pixel_" + counter++, new SimpleTextDisplay(display));
            }
        }

        temperature = ROOM_TEMPERATURE_CELSIUS;
        running = false;
        height = 0;
        capacity = 0;
        components.add(new BlockPosition(getBlock()));
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public SmelteryController(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        temperature = pdc.get(TEMPERATURE_KEY, PylonSerializers.DOUBLE);
        running = pdc.get(RUNNING_KEY, PylonSerializers.BOOLEAN);
        height = pdc.get(HEIGHT_KEY, PylonSerializers.INTEGER);
        capacity = pdc.get(CAPACITY_KEY, PylonSerializers.DOUBLE);
        components.addAll(pdc.get(COMPONENTS_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.BLOCK_POSITION)));
        fluids.putAll(pdc.get(FLUIDS_KEY, PylonSerializers.MAP.mapTypeFrom(PylonSerializers.PYLON_FLUID, PylonSerializers.DOUBLE)));
    }

    @Override
    public void onUnload(@NotNull PylonBlockUnloadEvent event) {
        applyHeat();
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(TEMPERATURE_KEY, PylonSerializers.DOUBLE, temperature);
        pdc.set(RUNNING_KEY, PylonSerializers.BOOLEAN, running);
        pdc.set(HEIGHT_KEY, PylonSerializers.INTEGER, height);
        pdc.set(CAPACITY_KEY, PylonSerializers.DOUBLE, capacity);
        pdc.set(COMPONENTS_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.BLOCK_POSITION), components);
        pdc.set(FLUIDS_KEY, PylonSerializers.MAP.mapTypeFrom(PylonSerializers.PYLON_FLUID, PylonSerializers.DOUBLE), fluids);
    }

    @Override
    public void postBreak() {
        PylonEntityHolderBlock.super.postBreak();
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
    public @NotNull Gui createGui() {
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
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
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
            if (fluids.isEmpty()) {
                lore.add(Component.translatable("pylon.pylonbase.gui.smeltery.contents.empty"));
            } else {
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
            }
            return ItemStackBuilder.of(Material.LAVA_BUCKET)
                    .name(Component.translatable("pylon.pylonbase.gui.smeltery.contents.name"))
                    .lore(lore);
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
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
    public @NotNull Set<ChunkPosition> getChunksOccupied() {
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
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        if (otherBlock.getY() < center.getY() - 1) return false;
        return otherBlock.getX() >= center.getX() - 2 && otherBlock.getX() <= center.getX() + 2
                && otherBlock.getZ() >= center.getZ() - 2 && otherBlock.getZ() <= center.getZ() + 2;
    }

    private static List<BlockPosition> addY(@NotNull List<BlockPosition> positions, int y) {
        if (y == 0) return positions;
        return positions.stream()
                .map(pos -> pos.addScalar(0, y, 0))
                .toList();
    }

    private boolean checkAllComponent(@NotNull List<BlockPosition> positions) {
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

    private static int countAir(@NotNull List<BlockPosition> positions) {
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
    public void addFluid(@NotNull PylonFluid fluid, double amount) {
        Preconditions.checkArgument(fluid.hasTag(FluidTemperature.class), "Fluid does not have a temperature tag");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");

        double amountToAdd = Math.min(amount, capacity - getTotalFluid());
        fluids.mergeDouble(fluid, amountToAdd, Double::sum);

        double originalHeatCapacity = getHeatCapacity();
        double addedHeatCapacity = amountToAdd / 1000.0 * DENSITY * SPECIFIC_HEAT;
        temperature = (originalHeatCapacity * temperature + addedHeatCapacity * ROOM_TEMPERATURE_CELSIUS) / (originalHeatCapacity + addedHeatCapacity);
    }

    public void removeFluid(@NotNull PylonFluid fluid, double amount) {
        Preconditions.checkArgument(fluid.hasTag(FluidTemperature.class), "Fluid does not have a temperature tag");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");

        fluids.mergeDouble(fluid, -amount, Double::sum);
        if (fluids.getDouble(fluid) <= 0.001) { // Consider anything less than a nanobucket as empty
            fluids.removeDouble(fluid);
        }
    }

    public double getFluidAmount(@NotNull PylonFluid fluid) {
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

    private static final Config settings = Settings.get(BaseKeys.SMELTERY_CONTROLLER);
    private static final double STEFAN_BOLTZMANN_CONSTANT = 5.67e-8; // W/m^2*K^4
    private static final double SPECIFIC_HEAT = settings.getOrThrow("specific-heat.fluid", Double.class);
    private static final double DENSITY = settings.getOrThrow("density.fluid", Double.class);
    private static final double SPECIFIC_HEAT_AIR = settings.getOrThrow("specific-heat.air", Double.class);
    private static final double DENSITY_AIR = settings.getOrThrow("density.air", Double.class);
    private static final double HEAT_LOSS_COEFFICIENT = settings.getOrThrow("heat-loss-coefficient", Double.class);
    private static final double EMISSIVITY = settings.getOrThrow("emissivity", Double.class);
    private static final double CELSIUS_TO_KELVIN = 273.15;
    private static final double ROOM_TEMPERATURE_CELSIUS = settings.getOrThrow("room-temperature", Double.class);
    private static final double ROOM_TEMPERATURE_KELVIN = ROOM_TEMPERATURE_CELSIUS + CELSIUS_TO_KELVIN;

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

    private double heatAccumulation = 0;
    private double heaterIndex = 0;

    public void heat(double joules, double diminishingReturnFactor) {
        Preconditions.checkArgument(!Double.isNaN(joules) && !Double.isInfinite(joules), "Joules cannot be NaN or infinite");
        Preconditions.checkArgument(diminishingReturnFactor > 0, "Multiple factor must be positive");
        Preconditions.checkArgument(diminishingReturnFactor <= 1, "Multiple factor must be less than or equal to 1");

        heatAccumulation += joules * Math.pow(diminishingReturnFactor, heaterIndex);
        heaterIndex++;
    }

    private void applyHeat() {
        temperature += heatAccumulation / (getHeatCapacity() + getAirHeatCapacity());
        heatAccumulation = 0;
        heaterIndex = 0;
    }
    // </editor-fold>

    // <editor-fold desc="Recipe" defaultstate="collapsed">
    public static final class Recipe implements PylonRecipe {

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(baseKey("smeltery")) {
            @Override
            public void addRecipe(@NotNull Recipe recipe) {
                super.addRecipe(recipe);
                Map<NamespacedKey, Recipe> recipes = getRegisteredRecipes();
                Map<NamespacedKey, Recipe> newMap = recipes.entrySet().stream()
                        .sorted(
                                Comparator.<Map.Entry<NamespacedKey, Recipe>>comparingDouble(entry -> -entry.getValue().getTemperature())
                                        .thenComparingInt(entry -> -entry.getValue().getFluidInputs().size())
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

        @Getter(onMethod_ = @Override)
        private final NamespacedKey key;

        @Getter
        private final Map<PylonFluid, Double> fluidInputs;

        @Getter
        private final Map<PylonFluid, Double> fluidOutputs;

        private final PylonFluid highestFluid;

        @Getter
        private final double temperature;

        public Recipe(
                @NotNull NamespacedKey key,
                @NotNull Map<PylonFluid, Double> inputFluids,
                @NotNull Map<PylonFluid, Double> outputFluids,
                double temperature
        ) {
            this.key = key;
            this.temperature = temperature;

            var highestFluidEntry = inputFluids.entrySet().stream()
                    .max(Comparator.comparingDouble(Map.Entry::getValue))
                    .orElseThrow(() -> new IllegalArgumentException("Input fluids cannot be empty"));
            this.highestFluid = highestFluidEntry.getKey();
            double highestFluidAmount = highestFluidEntry.getValue();

            this.fluidInputs = new HashMap<>();
            for (var entry : inputFluids.entrySet()) {
                Preconditions.checkArgument(entry.getValue() > 0, "Input fluid amount must be positive");
                this.fluidInputs.put(entry.getKey(), entry.getValue() / highestFluidAmount);
            }

            this.fluidOutputs = new HashMap<>();
            for (var entry : outputFluids.entrySet()) {
                Preconditions.checkArgument(entry.getValue() > 0, "Output fluid amount must be positive");
                this.fluidOutputs.put(entry.getKey(), entry.getValue() / highestFluidAmount);
            }
        }

        @Override
        public @NotNull List<@NotNull RecipeChoice> getInputItems() {
            return List.of();
        }

        @Override
        public @NotNull List<@NotNull PylonFluid> getInputFluids() {
            return fluidInputs.keySet().stream().toList();
        }

        @Override
        public @NotNull List<@NotNull ItemStack> getOutputItems() {
            return List.of();
        }

        @Override
        public @NotNull List<@NotNull PylonFluid> getOutputFluids() {
            return fluidOutputs.keySet().stream().toList();
        }

        @Override
        public @NotNull Gui display() {
            Preconditions.checkState(fluidInputs.size() < 6);
            Preconditions.checkState(fluidOutputs.size() < 6);
            Gui gui = Gui.normal()
                    .setStructure(
                            "# # # # # # # # #",
                            "# . . # # # . . #",
                            "# . . # s # . . #",
                            "# . . # t # . . #",
                            "# # # # # # # # #"
                    )
                    .addIngredient('#', GuiItems.backgroundBlack())
                    .addIngredient('s', ItemButton.fromStack(BaseItems.SMELTERY_CONTROLLER))
                    .addIngredient('t', ItemStackBuilder.of(Material.COAL)
                            .name(Component.translatable(
                                    "pylon.pylonbase.gui.smeltery.temperature",
                                    PylonArgument.of("temperature", UnitFormat.CELSIUS.format(temperature))
                            )))
                    .build();

            int i = 0;
            for (Map.Entry<PylonFluid, Double> entry : fluidInputs.entrySet()) {
                gui.setItem(10 + (i / 2) * 9 + (i % 2), new FluidButton(entry.getKey().getKey(), entry.getValue()));
                i++;
            }

            i = 0;
            for (Map.Entry<PylonFluid, Double> entry : fluidOutputs.entrySet()) {
                gui.setItem(15 + (i / 2) * 9 + (i % 2), new FluidButton(entry.getKey().getKey(), entry.getValue()));
                i++;
            }

            return gui;
        }
    }

    private void performRecipes(double deltaSeconds) {
        if (fluids.isEmpty()) return;
        recipeLoop:
        for (Recipe recipe : Recipe.RECIPE_TYPE) {
            if (recipe.temperature > temperature) continue;

            for (PylonFluid fluid : recipe.fluidInputs.keySet()) {
                if (getFluidAmount(fluid) == 0) continue recipeLoop;
            }

            double highestFluidAmount = getFluidAmount(recipe.highestFluid);
            double consumptionRatio = highestFluidAmount / (deltaSeconds * FLUID_REACTION_PER_SECOND);
            double currentTemperature = temperature;
            for (var entry : recipe.fluidInputs.entrySet()) {
                PylonFluid fluid = entry.getKey();
                double amount = entry.getValue() * consumptionRatio;
                removeFluid(fluid, amount);
            }
            for (var entry : recipe.fluidOutputs.entrySet()) {
                PylonFluid fluid = entry.getKey();
                double amount = entry.getValue() * consumptionRatio;
                addFluid(fluid, amount);
            }
            temperature = currentTemperature; // offset addFluid/removeFluid temperature change
        }
    }

    static {
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("redstone_decomposition"),
                Map.of(BaseFluids.REDSTONE_SLURRY, 1.0),
                Map.of(
                        BaseFluids.SULFUR, 0.25,
                        BaseFluids.MERCURY, 0.25,
                        BaseFluids.SLURRY, 0.5
                ),
                345
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("coal_to_carbon"),
                Map.of(BaseFluids.COAL_SLURRY, 1.0),
                Map.of(
                        BaseFluids.CARBON_SLURRY, 0.9,
                        BaseFluids.SLURRY, 0.1
                ),
                1000
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("copper_smelting"),
                Map.of(BaseFluids.RAW_COPPER_SLURRY, 1.0),
                Map.of(
                        BaseFluids.COPPER, 0.5,
                        BaseFluids.SLURRY, 0.5
                ),
                1085
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("copper_smelting_with_sulfur"),
                Map.of(
                        BaseFluids.RAW_COPPER_SLURRY, 1.0,
                        BaseFluids.SULFUR, 0.1
                ),
                Map.of(
                        BaseFluids.COPPER, 0.5,
                        BaseFluids.SLURRY, 0.3,
                        BaseFluids.RAW_LEAD_SLURRY, 0.1,
                        BaseFluids.RAW_ZINC_SLURRY, 0.1
                ),
                1085
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("gold_smelting"),
                Map.of(
                        BaseFluids.RAW_GOLD_SLURRY, 1.0,
                        BaseFluids.MERCURY, 1.0
                ),
                Map.of(
                        BaseFluids.GOLD, 0.5,
                        BaseFluids.SLURRY, 0.3,
                        BaseFluids.SILVER, 0.15,
                        BaseFluids.RAW_TIN_SLURRY, 0.15,
                        BaseFluids.MERCURY, 0.9
                ),
                1064
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("tin_smelting"),
                Map.of(
                        BaseFluids.RAW_TIN_SLURRY, 1.0,
                        BaseFluids.CARBON_SLURRY, 0.5
                ),
                Map.of(
                        BaseFluids.TIN, 1.0,
                        BaseFluids.SLURRY, 0.5
                ),
                250
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("lead_smelting"),
                Map.of(
                        BaseFluids.RAW_LEAD_SLURRY, 1.0,
                        BaseFluids.CARBON_SLURRY, 0.5,
                        BaseFluids.SULFUR, 0.1
                ),
                Map.of(
                        BaseFluids.LEAD, 1.0,
                        BaseFluids.SLURRY, 0.5,
                        BaseFluids.SILVER, 0.1
                ),
                350
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("zinc_smelting"),
                Map.of(
                        BaseFluids.RAW_ZINC_SLURRY, 1.0,
                        BaseFluids.CARBON_SLURRY, 0.5
                ),
                Map.of(
                        BaseFluids.ZINC, 1.0,
                        BaseFluids.SLURRY, 0.5
                ),
                700
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("iron_smelting"),
                Map.of(
                        BaseFluids.RAW_IRON_SLURRY, 1.0,
                        BaseFluids.CARBON_SLURRY, 0.5
                ),
                Map.of(
                        BaseFluids.IRON, 1.0,
                        BaseFluids.SLURRY, 0.5
                ),
                1540
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("iron_smelting_with_sulfur"),
                Map.of(
                        BaseFluids.RAW_IRON_SLURRY, 1.0,
                        BaseFluids.CARBON_SLURRY, 0.5,
                        BaseFluids.SULFUR, 0.1
                ),
                Map.of(
                        BaseFluids.IRON, 1.0,
                        BaseFluids.SLURRY, 0.4,
                        BaseFluids.COBALT, 0.1,
                        BaseFluids.NICKEL, 0.1
                ),
                1540
        ));

        // Alloys
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("bronze"),
                Map.of(
                        BaseFluids.COPPER, 1.0 - 0.12,
                        BaseFluids.TIN, 0.12
                ),
                Map.of(BaseFluids.BRONZE, 1.0),
                950
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("brass"),
                Map.of(
                        BaseFluids.COPPER, 1.0 - 0.3,
                        BaseFluids.ZINC, 0.3
                ),
                Map.of(BaseFluids.BRASS, 1.0),
                900
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("steel"),
                Map.of(
                        BaseFluids.IRON, 1.0 - 0.04,
                        BaseFluids.CARBON_SLURRY, 0.04
                ),
                Map.of(BaseFluids.STEEL, 1.0),
                1540
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("decarburization"), // yes this is a real word
                Map.of(
                        BaseFluids.STEEL, 1.0,
                        BaseFluids.WATER, 0.1
                ),
                Map.of(BaseFluids.IRON, 1.0),
                1540
        ));
    }
    // </editor-fold>

    // <editor-fold desc="Fluid display" defaultstate="collapsed">
    private final List<SimpleTextDisplay> pixels = new ArrayList<>();
    private static final int RESOLUTION = settings.getOrThrow("display.resolution", Integer.class);
    private static final int PIXELS_PER_SIDE = 3 * RESOLUTION;

    private final SimplexOctaveGenerator noise = new SimplexOctaveGenerator(
           getBlock().getWorld().getSeed(), 4
    );
    {
        noise.setScale(1 / 16.0);
    }
    private double cumulativeSeconds = 0;

    public @NotNull List<SimpleTextDisplay> getPixels() {
        if (pixels.isEmpty()) {
            for (int i = 0; i < PIXELS_PER_SIDE * PIXELS_PER_SIDE; i++) {
                pixels.add(getHeldEntityOrThrow(SimpleTextDisplay.class, "pixel_" + i));
            }
        }
        return pixels;
    }

    private double lastHeight = 0;
    private static final double LIGHTNESS_VARIATION = settings.getOrThrow("display.lightness-variation", Double.class);

    private void updateFluidDisplay() {
        HslColor color = HslColor.fromRgb(BaseUtils.colorFromTemperature(temperature));
        double fill = getTotalFluid() / capacity;
        if (Double.isNaN(fill) || Double.isInfinite(fill)) {
            fill = 0;
        }
        double finalHeight = center.getY() + height * fill - 0.01;

        List<SimpleTextDisplay> pixels = getPixels();
        for (int i = 0; i < pixels.size(); i++) {
            SimpleTextDisplay pixel = pixels.get(i);
            TextDisplay entity = pixel.getEntity();
            if (!entity.isValid()) continue;

            int x = i % PIXELS_PER_SIDE;
            int z = (i / PIXELS_PER_SIDE) % PIXELS_PER_SIDE;
            double value = noise.noise(
                    x,
                    z,
                    cumulativeSeconds * 3,
                    0.01,
                    0.01,
                    true
            );
            HslColor newColor = new HslColor(
                    color.hue(),
                    color.saturation(),
                    color.lightness() + value * LIGHTNESS_VARIATION
            );
            entity.setBackgroundColor(newColor.toRgb());

            if (lastHeight != finalHeight) {
                Location location = entity.getLocation();
                location.setY(finalHeight);
                entity.teleportAsync(location);
            }
        }
        lastHeight = finalHeight;
    }
    // </editor-fold>

    @Override
    public void tick(double deltaSeconds) {
        cumulativeSeconds += deltaSeconds;
        if (isFormedAndFullyLoaded()) {
            if (running) {
                applyHeat();
                performRecipes(deltaSeconds);
            }
            coolNaturally(deltaSeconds);
            if (temperature < 0) {
                PylonBase.getInstance().getLogger().warning("Smeltery temperature dropped below 0, something is probably wrong with the heat transfer calculations");
            }
        } else if (height <= 0) { // check height instead because of the brief moment when the multiblock is loaded but not checked yet
            running = false;
        }
        updateFluidDisplay();
        infoItem.notifyWindows();
        contentsItem.notifyWindows();
    }
}
