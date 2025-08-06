package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.Waila;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.UnclickableInventory;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;
import xyz.xenondevs.invui.gui.Gui;

import java.time.Duration;
import java.util.*;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class PitKiln extends PylonBlock implements
        PylonSimpleMultiblock, PylonInteractableBlock, PylonTickingBlock {

    private static final int CAPACITY = Settings.get(BaseKeys.PIT_KILN).getOrThrow("capacity", Integer.class);
    private static final double PROCESSING_TIME_SECONDS =
            Settings.get(BaseKeys.PIT_KILN).getOrThrow("processing-time-seconds", Double.class);

    public static final class Item extends PylonItem {

        private static final int CAPACITY = Settings.get(BaseKeys.PIT_KILN).getOrThrow("capacity", Integer.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("capacity", CAPACITY),
                    PylonArgument.of("smelting_time", UnitFormat.formatDuration(Duration.ofSeconds((long) PROCESSING_TIME_SECONDS)))
            );
        }
    }

    private static final NamespacedKey CONTENTS_KEY = baseKey("contents");
    private static final PersistentDataType<?, Map<ItemStack, Integer>> CONTENTS_TYPE =
            PylonSerializers.MAP.mapTypeFrom(PylonSerializers.ITEM_STACK, PylonSerializers.INTEGER);
    private static final NamespacedKey PROCESSING_KEY = baseKey("processing");
    private static final NamespacedKey PROCESSING_TIME_KEY = baseKey("processing_time");

    private final Map<ItemStack, Integer> contents;
    private final Set<ItemStack> processing;
    private double processingTime;

    @SuppressWarnings("unused")
    public PitKiln(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        contents = new HashMap<>();
        processing = new HashSet<>();
        processingTime = Double.NaN;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
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
        pdc.set(PROCESSING_TIME_KEY, PylonSerializers.DOUBLE, processingTime);
    }

    @Override
    public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
        drops.clear(); // Don't drop the block itself
        for (Map.Entry<ItemStack, Integer> entry : contents.entrySet()) {
            ItemStack item = entry.getKey();
            item.setAmount(entry.getValue());
            drops.add(item);
        }
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
        player.swingHand(EquipmentSlot.HAND);

        ItemStack item = event.getItem();
        if (item == null || item.getType().isAir()) return;

        int currentAmount = 0;
        for (int amount : contents.values()) {
            currentAmount += amount;
        }
        int taken = Math.min(CAPACITY - currentAmount, item.getAmount());
        if (taken <= 0) return;

        contents.merge(item.asOne(), taken, Integer::sum);
        item.subtract(taken);
    }

    @Override
    public void tick(double deltaSeconds) {
        if (isFormedAndFullyLoaded()) {
            for (Vector3i relative : getComponents().keySet()) {
                BlockPosition block = new BlockPosition(getBlock()).addScalar(relative.x(), relative.y(), relative.z());
                Waila.addWailaOverride(block, this::getComponentWaila);
            }
            if (!isProcessing()) {
                tryStartProcessing();
            }
            if (isProcessing()) {
                processingTime -= deltaSeconds;
                if (processingTime <= 0) {
                    processingTime = Double.NaN;
                    int multiplier = 2;
                    for (Vector3i top : TOP_POSITIONS) {
                        Block topBlock = getBlock().getRelative(top.x(), top.y(), top.z());
                        if (topBlock.getType() != Material.PODZOL) {
                            multiplier = 1;
                        }
                        topBlock.setType(Material.COARSE_DIRT);
                    }
                    for (ItemStack outputItem : processing) {
                        contents.merge(outputItem.asOne(), outputItem.getAmount() * multiplier, Integer::sum);
                    }
                    processing.clear();
                    for (Vector3i coal : COAL_POSITIONS) {
                        Block coalBlock = getBlock().getRelative(coal.x(), coal.y(), coal.z());
                        coalBlock.setType(Material.AIR);
                    }
                }
            }
        } else {
            if (isProcessing()) {
                processingTime = Double.NaN;
                processing.clear();
            }
            removeWailas();
        }
    }

    private WailaConfig getComponentWaila(@NotNull Player player) {
        if (isProcessing()) {
            return new WailaConfig(Component.translatable(
                    "pylon.pylonbase.waila.pit_kiln",
                    PylonArgument.of(
                            "time",
                            UnitFormat.formatDuration(Duration.ofSeconds((long) processingTime))
                    )
            ));
        } else {
            return new WailaConfig(Component.translatable("pylon.pylonbase.item.pit_kiln.name"));
        }
    }

    private void removeWailas() {
        for (Vector3i relative : getComponents().keySet()) {
            BlockPosition block = new BlockPosition(getBlock()).addScalar(relative.x(), relative.y(), relative.z());
            Waila.removeWailaOverride(block);
        }
    }

    // <editor-fold desc="Recipe" defaultstate="collapsed">
    private void tryStartProcessing() {
        if (!Double.isNaN(processingTime) || contents.isEmpty()) return;
        recipeLoop:
        for (Recipe recipe : Recipe.RECIPE_TYPE) {
            int ratio = Integer.MAX_VALUE;
            for (ItemStack inputItem : recipe.input()) {
                if (!contents.containsKey(inputItem)) {
                    continue recipeLoop;
                }
                int existing = contents.get(inputItem.asOne());
                int required = inputItem.getAmount();
                if (existing < required) {
                    continue recipeLoop;
                }
                ratio = Math.min(ratio, existing / required);
            }
            if (ratio <= 0) continue;

            for (ItemStack inputItem : recipe.input()) {
                contents.merge(inputItem.asOne(), -inputItem.getAmount() * ratio, Integer::sum);
            }
            Set<ItemStack> outputItems = new HashSet<>(recipe.output().size());
            for (ItemStack outputItem : recipe.output()) {
                ItemStack outputCopy = outputItem.asOne();
                outputCopy.setAmount(outputItem.getAmount() * ratio);
                outputItems.add(outputCopy);
            }
            processing.addAll(outputItems);
            double multiplier = switch (getBlock().getRelative(FIRE_POSITION.x(), FIRE_POSITION.y(), FIRE_POSITION.z()).getType()) {
                case CAMPFIRE -> 0.5;
                case SOUL_FIRE -> 2;
                default -> 1;
            };
            processingTime = PROCESSING_TIME_SECONDS * multiplier;
            break;
        }
    }

    static {
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("copper_smelting"),
                List.of(BaseItems.CRUSHED_RAW_COPPER),
                List.of(new ItemStack(Material.COPPER_INGOT))
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("redstone_smelting"),
                List.of(new ItemStack(Material.REDSTONE)),
                List.of(BaseItems.SULFUR)
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("gold_smelting"),
                List.of(BaseItems.CRUSHED_RAW_GOLD),
                List.of(new ItemStack(Material.GOLD_INGOT))
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("tin_smelting"),
                List.of(BaseItems.CRUSHED_RAW_TIN),
                List.of(BaseItems.TIN_INGOT)
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("zinc_smelting"),
                List.of(BaseItems.CRUSHED_RAW_ZINC),
                List.of(BaseItems.ZINC_INGOT)
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("lead_smelting"),
                List.of(BaseItems.CRUSHED_RAW_LEAD),
                List.of(BaseItems.LEAD_INGOT)
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("coal_to_carbon"),
                List.of(BaseItems.COAL_DUST.asQuantity(2)),
                List.of(BaseItems.CARBON_DUST)
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("bronze"),
                List.of(new ItemStack(Material.COPPER_INGOT, 2), BaseItems.TIN_INGOT),
                List.of(BaseItems.BRONZE_INGOT)
        ));
        Recipe.RECIPE_TYPE.addRecipe(new Recipe(
                baseKey("brass"),
                List.of(new ItemStack(Material.COPPER_INGOT, 2), BaseItems.ZINC_INGOT),
                List.of(BaseItems.BRASS_INGOT)
        ));
    }

    public record Recipe(
            @NotNull NamespacedKey key,
            @NotNull List<ItemStack> input,
            @NotNull List<ItemStack> output
    ) implements PylonRecipe {

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(
                baseKey("pit_kiln_recipe")
        );

        static {
            RECIPE_TYPE.register();
        }

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }

        @Override
        public @NotNull List<RecipeChoice> getInputItems() {
            List<RecipeChoice> choices = new ArrayList<>();
            for (ItemStack item : input) {
                choices.add(new RecipeChoice.ExactChoice(item));
            }
            return choices;
        }

        @Override
        public @NotNull List<ItemStack> getOutputItems() {
            return output;
        }

        @Override
        public @NotNull Gui display() {
            UnclickableInventory inputs = new UnclickableInventory(9);
            for (ItemStack item : input) {
                inputs.addItem(item);
            }

            UnclickableInventory outputs = new UnclickableInventory(9);
            for (ItemStack item : output) {
                outputs.addItem(item);
            }

            return Gui.normal()
                    .setStructure(
                            "# # # # # # # # #",
                            "# , , # # # . . #",
                            "# , , # p # . . #",
                            "# , , # # # . . #",
                            "# # # # # # # # #"
                    )
                    .addIngredient('#', GuiItems.backgroundBlack())
                    .addIngredient(',', inputs)
                    .addIngredient('.', outputs)
                    .addIngredient('p', BaseItems.PIT_KILN)
                    .build();
        }
    }

    public boolean isProcessing() {
        return !Double.isNaN(processingTime);
    }
    // </editor-fold>

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
