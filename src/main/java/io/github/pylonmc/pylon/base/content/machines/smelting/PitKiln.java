package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.recipes.PitKilnRecipe;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.Waila;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.time.Duration;
import java.util.*;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class PitKiln extends PylonBlock implements
        PylonSimpleMultiblock, PylonInteractBlock, PylonTickingBlock {

    public static final int CAPACITY = Settings.get(BaseKeys.PIT_KILN).getOrThrow("capacity", ConfigAdapter.INT);
    public static final int PROCESSING_TIME_SECONDS =
            Settings.get(BaseKeys.PIT_KILN).getOrThrow("processing-time-seconds", ConfigAdapter.INT);

    private static final double MULTIPLIER_CAMPFIRE = Settings.get(BaseKeys.PIT_KILN).getOrThrow("speed-multipliers.campfire", ConfigAdapter.DOUBLE);
    private static final double MULTIPLIER_SOUL_CAMPFIRE = Settings.get(BaseKeys.PIT_KILN).getOrThrow("speed-multipliers.soul-campfire", ConfigAdapter.DOUBLE);
    private static final double MULTIPLIER_FIRE = Settings.get(BaseKeys.PIT_KILN).getOrThrow("speed-multipliers.fire", ConfigAdapter.DOUBLE);
    private static final double MULTIPLIER_SOUL_FIRE = Settings.get(BaseKeys.PIT_KILN).getOrThrow("speed-multipliers.soul-fire", ConfigAdapter.DOUBLE);

    private static final double MULTIPLIER_DIRT = Settings.get(BaseKeys.PIT_KILN).getOrThrow("item-multipliers.coarse-dirt", ConfigAdapter.DOUBLE);
    private static final double MULTIPLIER_PODZOL = Settings.get(BaseKeys.PIT_KILN).getOrThrow("item-multipliers.podzol", ConfigAdapter.DOUBLE);

    public static final class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("capacity", CAPACITY),
                    PylonArgument.of("smelting_time", UnitFormat.formatDuration(Duration.ofSeconds(PROCESSING_TIME_SECONDS))),
                    PylonArgument.of("campfire", MULTIPLIER_CAMPFIRE),
                    PylonArgument.of("soul_campfire", MULTIPLIER_SOUL_CAMPFIRE),
                    PylonArgument.of("fire", MULTIPLIER_FIRE),
                    PylonArgument.of("soul_fire", MULTIPLIER_SOUL_FIRE),
                    PylonArgument.of("coarse_dirt", MULTIPLIER_DIRT),
                    PylonArgument.of("podzol", MULTIPLIER_PODZOL)
            );
        }
    }

    private static final NamespacedKey CONTENTS_KEY = baseKey("contents");
    private static final PersistentDataType<?, Set<ItemStack>> CONTENTS_TYPE =
            PylonSerializers.SET.setTypeFrom(PylonSerializers.ITEM_STACK);
    private static final NamespacedKey PROCESSING_KEY = baseKey("processing");
    private static final NamespacedKey PROCESSING_TIME_KEY = baseKey("processing_time");

    private final Set<ItemStack> contents;
    private final Set<ItemStack> processing;
    private @Nullable Double processingTime;

    @SuppressWarnings("unused")
    public PitKiln(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        contents = new HashSet<>();
        processing = new HashSet<>();
        processingTime = null;
    }

    @SuppressWarnings("unused")
    public PitKiln(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        contents = pdc.get(CONTENTS_KEY, CONTENTS_TYPE);
        processing = pdc.get(PROCESSING_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.ITEM_STACK));
        processingTime = pdc.get(PROCESSING_TIME_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(CONTENTS_KEY, CONTENTS_TYPE, contents);
        pdc.set(PROCESSING_KEY, PylonSerializers.SET.setTypeFrom(PylonSerializers.ITEM_STACK), processing);
        PylonUtils.setNullable(pdc, PROCESSING_TIME_KEY, PylonSerializers.DOUBLE, processingTime);
    }

    @Override
    public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
        drops.addAll(contents);
    }

    @Override
    public void postBreak() {
        PylonSimpleMultiblock.super.postBreak();
        removeWailas();
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        event.setCancelled(true);
        Player player = event.getPlayer();

        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;
        //noinspection DataFlowIssue
        player.swingHand(event.getHand());

        addItem(item);
    }

    private void addItem(ItemStack item) {
        int currentAmount = 0;
        for (ItemStack contentItem : contents) {
            currentAmount += contentItem.getAmount();
        }
        if (currentAmount >= CAPACITY) return;

        for (ItemStack contentItem : contents) {
            if (contentItem.isSimilar(item)) {
                contentItem.add();
                item.subtract();
                return;
            }
        }

        contents.add(item.asOne());
        item.subtract();
    }

    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) {
            if (processingTime != null) {
                processingTime = null;
                processing.clear();
            }
            return;
        }
        if (processingTime == null) {
            tryStartProcessing();
        }

        if (processingTime == null) return;
        processingTime -= deltaSeconds;
        if (processingTime > 0) return;

        processingTime = null;
        double multiplier = 0;
        for (Vector3i top : TOP_POSITIONS) {
            Block topBlock = getBlock().getRelative(top.x(), top.y(), top.z());
            multiplier = switch (topBlock.getType()) {
                case PODZOL -> MULTIPLIER_PODZOL;
                case COARSE_DIRT -> MULTIPLIER_DIRT;
                default -> throw new AssertionError();
            };
            topBlock.setType(Material.COARSE_DIRT);
        }
        outputLoop:
        for (ItemStack outputItem : processing) {
            int addAmount = (int) Math.floor(outputItem.getAmount() * multiplier);
            for (ItemStack contentItem : contents) {
                if (contentItem.isSimilar(outputItem)) {
                    contentItem.add(addAmount);
                    continue outputLoop;
                }
            }
            contents.add(outputItem.asQuantity(addAmount));
        }
        processing.clear();
        for (Vector3i coal : COAL_POSITIONS) {
            Block coalBlock = getBlock().getRelative(coal.x(), coal.y(), coal.z());
            coalBlock.setType(Material.AIR);
        }
    }

    private WailaConfig getComponentWaila(@NotNull Player player) {
        if (processingTime != null) {
            return new WailaConfig(Component.translatable(
                    "pylon.pylonbase.waila.pit_kiln",
                    PylonArgument.of(
                            "time",
                            UnitFormat.formatDuration(Duration.ofSeconds(processingTime.longValue()))
                    )
            ));
        }
        return new WailaConfig(Component.translatable("pylon.pylonbase.item.pit_kiln.name"));
    }

    @Override
    public boolean checkFormed() {
        if (!PylonSimpleMultiblock.super.checkFormed()) {
            removeWailas();
            return false;
        }
        for (Vector3i relative : getComponents().keySet()) {
            BlockPosition block = new BlockPosition(getBlock()).addScalar(relative.x(), relative.y(), relative.z());
            Waila.addWailaOverride(block, this::getComponentWaila);
        }
        return true;
    }

    private void removeWailas() {
        for (Vector3i relative : getComponents().keySet()) {
            BlockPosition block = new BlockPosition(getBlock()).addScalar(relative.x(), relative.y(), relative.z());
            Waila.removeWailaOverride(block);
        }
    }

    private void tryStartProcessing() {
        if (processingTime != null || contents.isEmpty()) return;
        recipeLoop:
        for (PitKilnRecipe recipe : PitKilnRecipe.RECIPE_TYPE) {
            int ratio = Integer.MAX_VALUE;
            for (RecipeInput.Item input : recipe.input()) {
                int existing = 0;
                for (ItemStack contentItem : contents) {
                    if (input.contains(contentItem)) {
                        existing = contentItem.getAmount();
                        break;
                    }
                }
                int required = input.getAmount();
                if (existing < required) {
                    continue recipeLoop;
                }
                ratio = Math.min(ratio, existing / required);
            }
            if (ratio <= 0) continue;

            for (RecipeInput.Item input : recipe.input()) {
                int removeAmount = input.getAmount() * ratio;
                for (ItemStack contentItem : contents) {
                    if (input.contains(contentItem)) {
                        contentItem.subtract(removeAmount);
                        break;
                    }
                }
            }
            Set<ItemStack> outputItems = new HashSet<>(recipe.output().size());
            for (ItemStack outputItem : recipe.output()) {
                ItemStack outputCopy = outputItem.asOne();
                outputCopy.setAmount(outputItem.getAmount() * ratio);
                outputItems.add(outputCopy);
            }
            processing.addAll(outputItems);
            double multiplier = switch (getBlock().getRelative(FIRE_POSITION.x(), FIRE_POSITION.y(), FIRE_POSITION.z()).getType()) {
                case CAMPFIRE -> MULTIPLIER_CAMPFIRE;
                case SOUL_CAMPFIRE -> MULTIPLIER_SOUL_CAMPFIRE;
                case FIRE -> MULTIPLIER_FIRE;
                case SOUL_FIRE -> MULTIPLIER_SOUL_FIRE;
                default -> throw new AssertionError();
            };
            processingTime = PROCESSING_TIME_SECONDS / multiplier;
            break;
        }
    }

    // <editor-fold desc="Multiblock" defaultstate="collapsed">
    private static final List<Vector3i> COAL_POSITIONS = List.of(
            new Vector3i(-1, 0, -1),
            new Vector3i(0, 0, -1),
            new Vector3i(1, 0, -1),
            new Vector3i(-1, 0, 0),
            new Vector3i(1, 0, 0),
            new Vector3i(-1, 0, 1),
            new Vector3i(0, 0, 1),
            new Vector3i(1, 0, 1)
    );

    private static final List<Vector3i> TOP_POSITIONS = List.of(
            new Vector3i(-1, 1, -1),
            new Vector3i(0, 1, -1),
            new Vector3i(1, 1, -1),
            new Vector3i(-1, 1, 0),
            new Vector3i(0, 1, 0),
            new Vector3i(1, 1, 0),
            new Vector3i(-1, 1, 1),
            new Vector3i(0, 1, 1),
            new Vector3i(1, 1, 1)
    );

    private static final Vector3i FIRE_POSITION = new Vector3i(0, -1, 0);

    @Override
    public @NotNull Map<Vector3i, MultiblockComponent> getComponents() {
        Map<Vector3i, MultiblockComponent> components = new HashMap<>();
        for (Vector3i coalPosition : COAL_POSITIONS) {
            components.put(coalPosition, new VanillaMultiblockComponent(Material.COAL_BLOCK));
        }
        for (Vector3i podzolPosition : TOP_POSITIONS) {
            components.put(podzolPosition, new VanillaMultiblockComponent(Material.COARSE_DIRT, Material.PODZOL));
        }
        components.put(new Vector3i(-1, 0, -2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(0, 0, -2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(1, 0, -2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-1, 0, 2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(0, 0, 2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(1, 0, 2), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-2, 0, -1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-2, 0, 0), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-2, 0, 1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(2, 0, -1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(2, 0, 0), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(2, 0, 1), new VanillaMultiblockComponent(Material.COARSE_DIRT));

        components.put(new Vector3i(-1, -1, -1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(0, -1, -1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(1, -1, -1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-1, -1, 0), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(1, -1, 0), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(-1, -1, 1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(0, -1, 1), new VanillaMultiblockComponent(Material.COARSE_DIRT));
        components.put(new Vector3i(1, -1, 1), new VanillaMultiblockComponent(Material.COARSE_DIRT));

        components.put(FIRE_POSITION, new VanillaMultiblockComponent(
                Material.CAMPFIRE, Material.SOUL_CAMPFIRE, Material.FIRE, Material.SOUL_FIRE
        ));
        return components;
    }
    // </editor-fold>
}
