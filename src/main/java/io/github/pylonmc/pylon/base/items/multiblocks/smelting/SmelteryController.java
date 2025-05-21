package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.tags.FluidTemperature;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntRBTreeMap;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.persistence.PersistentDataContainer;
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
public class SmelteryController extends SmelteryComponent<PylonBlockSchema>
        implements PylonGuiBlock, PylonMultiblock, PylonTickingBlock {

    private static final NamespacedKey TEMPERATURE_KEY = pylonKey("temperature");
    private static final NamespacedKey RUNNING_KEY = pylonKey("running");
    private static final NamespacedKey HEIGHT_KEY = pylonKey("height");
    private static final NamespacedKey VOLUME_KEY = pylonKey("volume");
    private static final NamespacedKey COMPONENTS_KEY = pylonKey("components");
    private static final NamespacedKey FLUIDS_KEY = pylonKey("fluids");

    @Getter
    @Setter
    private double temperature;

    @Getter
    @Setter
    private boolean running;

    private final Object2IntMap<PylonFluid> fluids = new Object2IntRBTreeMap<>((a, b) -> {
        FluidTemperature temperatureA = a.getTag(FluidTemperature.class);
        FluidTemperature temperatureB = b.getTag(FluidTemperature.class);
        if (temperatureA == null || temperatureB == null) {
            throw new IllegalStateException("Fluid does not have a temperature tag");
        }
        return Integer.compare(temperatureA.getTemperature(), temperatureB.getTemperature());
    });

    @Getter
    private int volume;

    private int height;
    private final Set<BlockPosition> components = new HashSet<>();

    @SuppressWarnings("unused")
    public SmelteryController(PylonBlockSchema schema, Block block, BlockCreateContext context) {
        super(schema, block, context);

        temperature = 20;
        running = false;
        height = 0;
        volume = 0;
        components.add(new BlockPosition(getBlock()));

        fluids.defaultReturnValue(-1);
    }

    @SuppressWarnings("unused")
    public SmelteryController(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
        super(schema, block, pdc);

        temperature = pdc.getOrDefault(TEMPERATURE_KEY, PylonSerializers.DOUBLE, 20.0);
        running = pdc.getOrDefault(RUNNING_KEY, PylonSerializers.BOOLEAN, false);
        height = pdc.getOrDefault(HEIGHT_KEY, PylonSerializers.INTEGER, 0);
        volume = pdc.getOrDefault(VOLUME_KEY, PylonSerializers.INTEGER, 0);
        components.addAll(pdc.getOrDefault(COMPONENTS_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.BLOCK_POSITION), Set.of()));
        fluids.putAll(pdc.getOrDefault(FLUIDS_KEY, PylonSerializers.MAP.mapTypeFrom(PylonSerializers.PYLON_FLUID, PylonSerializers.INTEGER), Map.of()));

        fluids.defaultReturnValue(-1);
    }

    @Override
    public void write(PersistentDataContainer pdc) {
        pdc.set(TEMPERATURE_KEY, PylonSerializers.DOUBLE, temperature);
        pdc.set(RUNNING_KEY, PylonSerializers.BOOLEAN, running);
        pdc.set(HEIGHT_KEY, PylonSerializers.INTEGER, height);
        pdc.set(VOLUME_KEY, PylonSerializers.INTEGER, volume);
        pdc.set(COMPONENTS_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.BLOCK_POSITION), components);
        pdc.set(FLUIDS_KEY, PylonSerializers.MAP.mapTypeFrom(PylonSerializers.PYLON_FLUID, PylonSerializers.INTEGER), fluids);
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
                    lore.add(Component.translatable("pylon.pylonbase.gui.smeltery.status.running"));
                } else {
                    material = Material.YELLOW_STAINED_GLASS_PANE;
                    lore.add(Component.translatable("pylon.pylonbase.gui.smeltery.status.not_running"));
                }
                lore.add(Component.translatable(
                        "pylon.pylonbase.gui.smeltery.status.height",
                        PylonArgument.of("height", height)
                ));
                lore.add(Component.translatable(
                        "pylon.pylonbase.gui.smeltery.status.volume",
                        PylonArgument.of("capacity", volume)
                ));
            } else {
                material = Material.RED_STAINED_GLASS_PANE;
                lore.add(Component.translatable("pylon.pylonbase.gui.smeltery.status.incomplete"));
            }
            return ItemStackBuilder.of(material)
                    .name(Component.translatable("pylon.pylonbase.gui.smeltery.status.name"))
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

        @Override
        public ItemProvider getItemProvider() {
            return ItemStackBuilder.of(Material.LAVA_BUCKET);
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
        height = 0;
        volume = 0;

        for (BlockPosition pos : components) {
            if (BlockStorage.get(pos) instanceof SmelteryComponent<?> component) {
                component.setController(null);
            }
        }

        // Check floor
        if (!checkAllComponent(addY(multiblockWithAirPositions, -1))) return false;

        // Check sides up to world height
        for (int i = 0; i < getBlock().getWorld().getMaxHeight(); i++) {
            if (checkAllComponent(addY(multiblockPositions, i))) {
                height++;
                volume += countAir(addY(insidePositions, i)) * 1000;
            } else {
                break;
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
        Map<BlockPosition, SmelteryComponent<?>> components = new HashMap<>();
        for (BlockPosition pos : positions) {
            if (BlockStorage.get(pos) instanceof SmelteryComponent<?> component) {
                components.put(pos, component);
            } else {
                return false;
            }
        }
        for (Map.Entry<BlockPosition, SmelteryComponent<?>> entry : components.entrySet()) {
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
    public void addFluid(PylonFluid fluid, int amount) {
        Preconditions.checkArgument(fluid.hasTag(FluidTemperature.class), "Fluid does not have a temperature tag");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");

        int amountToAdd = Math.min(amount, volume - getTotalFluid());
        fluids.mergeInt(fluid, amountToAdd, Integer::sum);
    }

    public void removeFluid(PylonFluid fluid, int amount) {
        Preconditions.checkArgument(fluid.hasTag(FluidTemperature.class), "Fluid does not have a temperature tag");
        Preconditions.checkArgument(amount > 0, "Amount must be positive");

        fluids.mergeInt(fluid, -amount, Integer::sum);
        if (fluids.getInt(fluid) <= 0) {
            fluids.removeInt(fluid);
        }
    }

    public int getFluidAmount(PylonFluid fluid) {
        return fluids.getInt(fluid);
    }

    public int getTotalFluid() {
        int sum = 0;
        for (int value : fluids.values()) {
            sum += value;
        }
        return sum;
    }
    // </editor-fold>

    @Override
    public void tick(double deltaSeconds) {
        infoItem.notifyWindows();
        contentsItem.notifyWindows();
    }
}
