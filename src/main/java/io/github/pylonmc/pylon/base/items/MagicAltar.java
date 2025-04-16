package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.base.PylonEntities;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.block.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.SimplePylonMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import io.github.pylonmc.pylon.core.persistence.blockstorage.BlockStorage;
import io.github.pylonmc.pylon.core.persistence.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import org.bukkit.Color;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;


public final class MagicAltar {

    private MagicAltar() {
        throw new AssertionError("Container class");
    }

    private static final int PEDESTAL_COUNT = 8;

    public static void drawLine(
            @NotNull Location start,
            @NotNull Location end,
            double spacing,
            @NotNull Consumer<Location> spawnParticle
            ) {
        double currentPoint = 0;
        Vector startToEnd = end.clone().subtract(start).toVector();
        Vector step = startToEnd.clone().normalize().multiply(spacing);
        double length = startToEnd.length();
        Location current = start.clone();

        while (currentPoint < length) {
            spawnParticle.accept(current);
            currentPoint += spacing;
            current.add(step);
        }
    }

    public static class MagicAltarItem extends PylonItem<MagicAltarItem.Schema> implements BlockPlacer {

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return PylonBlocks.MAGIC_ALTAR;
        }

        public static class Schema extends PylonItemSchema {

            public Schema(@NotNull NamespacedKey key, @NotNull Function<NamespacedKey, ItemStack> templateSupplier) {
                super(key, MagicAltarItem.class, templateSupplier);
            }
        }

        public MagicAltarItem(@NotNull Schema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }
    }

    public static class MagicAltarBlock extends PylonBlock<MagicAltarBlock.Schema>
            implements SimplePylonMultiblock, PylonTickingBlock, PylonInteractableBlock {

        private static final NamespacedKey PROCESSING_RECIPE = KeyUtils.pylonKey("processing_recipe");
        private static final NamespacedKey REMAINING_TIME_SECONDS = KeyUtils.pylonKey("remaining_time_seconds");
        private static final Random random = new Random();

        private @Nullable NamespacedKey processingRecipe;
        private double remainingTimeSeconds;
        private final Map<String, UUID> entities;

        private static final Component MAGIC_PEDESTAL_COMPONENT = new SimplePylonMultiblock.PylonComponent(PylonItems.MAGIC_PEDESTAL.getKey());

        public static class Schema extends PylonBlockSchema {

            public Schema(@NotNull NamespacedKey key, @NotNull Material material) {
                super(key, material, MagicAltarBlock.class);
            }
        }

        @SuppressWarnings("unused")
        public MagicAltarBlock(Schema schema, Block block, BlockCreateContext context) {
            super(schema, block);

            ItemDisplay display = new ItemDisplayBuilder()
                    .transformation(new TransformBuilder()
                            .translate(0, 0.3, 0)
                            .scale(0.6)
                            .buildForItemDisplay())
                    .build(block.getLocation().toCenterLocation());

            entities = new HashMap<>();
            entities.put("item", display.getUniqueId());

            spawnMultiblockGhosts();

            Pedestal.PedestalEntity pylonEntity = new Pedestal.PedestalEntity(PylonEntities.PEDESTAL_ITEM, display);
            EntityStorage.add(pylonEntity);
        }

        @SuppressWarnings({"unused", "DataFlowIssue"})
        public MagicAltarBlock(Schema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);

            entities = loadHeldEntities(pdc);
            processingRecipe = pdc.get(PROCESSING_RECIPE, PylonSerializers.NAMESPACED_KEY);
            remainingTimeSeconds = pdc.get(REMAINING_TIME_SECONDS, PylonSerializers.DOUBLE);
        }

        @Override
        public void write(@NotNull PersistentDataContainer pdc) {
            saveHeldEntities(pdc);
            if (processingRecipe == null) {
                pdc.remove(PROCESSING_RECIPE);
            } else {
                pdc.set(PROCESSING_RECIPE, PylonSerializers.NAMESPACED_KEY, processingRecipe);
            }
            pdc.set(REMAINING_TIME_SECONDS, PylonSerializers.DOUBLE, remainingTimeSeconds);
        }

        @Override
        public @NotNull Map<String, UUID> getHeldEntities() {
            return entities;
        }

        @Override
        public @NotNull Map<Vector3i, Component> getComponents() {
            // use linked to retain order of pedestals - important for recipes
            Map<Vector3i, Component> map = new LinkedHashMap<>();
            map.put(new Vector3i(3, 0, 0), MAGIC_PEDESTAL_COMPONENT);
            map.put(new Vector3i(2, 0, 2), MAGIC_PEDESTAL_COMPONENT);
            map.put(new Vector3i(0, 0, 3), MAGIC_PEDESTAL_COMPONENT);
            map.put(new Vector3i(-2, 0, 2), MAGIC_PEDESTAL_COMPONENT);
            map.put(new Vector3i(-3, 0, 0), MAGIC_PEDESTAL_COMPONENT);
            map.put(new Vector3i(-2, 0, -2), MAGIC_PEDESTAL_COMPONENT);
            map.put(new Vector3i(0, 0, -3), MAGIC_PEDESTAL_COMPONENT);
            map.put(new Vector3i(2, 0, -2), MAGIC_PEDESTAL_COMPONENT);
            return map;
        }

        @Override
        public void onInteract(@NotNull PlayerInteractEvent event) {
            if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            event.setCancelled(true);

            ItemDisplay itemDisplay = getHeldEntity(Pedestal.PedestalEntity.class, "item").getEntity();

            // drop item if not processing and an item is already on the altar
            ItemStack displayItem = itemDisplay.getItemStack();
            if (processingRecipe == null && !displayItem.getType().isAir()) {
                Location location = itemDisplay.getLocation().add(0, 0.5, 0);
                location.getWorld().dropItemNaturally(location, displayItem);
                itemDisplay.setItemStack(new ItemStack(Material.AIR));
                return;
            }

            if (!isFormedAndFullyLoaded()) {
                return;
            }

            // attempt to start recipe
            ItemStack catalyst = event.getItem();
            if (catalyst != null && processingRecipe == null) {
                List<ItemStack> ingredients = new ArrayList<>();
                for (Pedestal.PedestalBlock pedestal : getPedestals()) {
                    ingredients.add(pedestal.getItemDisplay().getItemStack());
                }

                for (Recipe recipe : Recipe.RECIPE_TYPE.getRecipes()) {
                    if (recipe.isValidRecipe(ingredients, catalyst)) {
                        ItemStack stackToInsert = catalyst.clone();
                        stackToInsert.setAmount(1);
                        itemDisplay.setItemStack(stackToInsert);
                        catalyst.subtract();
                        startRecipe(recipe);
                        break;
                    }
                }
            }
        }

        @Override
        public int getCustomTickRate(int globalTickRate) {
            return 5;
        }

        @Override
        public void tick(double deltaSeconds) {
            if (!isFormedAndFullyLoaded()) {
                if (getCurrentRecipe() != null) {
                    cancelRecipe();
                }
                return;
            }

            if (getCurrentRecipe() != null) {
                if (remainingTimeSeconds <= 0.0) {
                    finishRecipe();
                } else {
                    tickRecipe(deltaSeconds);
                }
            }
        }

        public List<Pedestal.PedestalBlock> getPedestals() {
            List<Pedestal.PedestalBlock> pedestals = new ArrayList<>();
            for (Vector3i vector : getComponents().keySet()) {
                Block block = getBlock().getRelative(vector.x, vector.y, vector.z);
                pedestals.add(BlockStorage.getAs(Pedestal.PedestalBlock.class, block));
            }
            return pedestals;
        }

        public @Nullable Recipe getCurrentRecipe() {
            if (processingRecipe == null) {
                return null;
            }
            return Recipe.RECIPE_TYPE.getRecipe(processingRecipe);
        }

        public void startRecipe(Recipe recipe) {
            assert processingRecipe == null;
            for (Pedestal.PedestalBlock pedestal : getPedestals()) {
                pedestal.setLocked(true);
            }
            processingRecipe = recipe.key;
            remainingTimeSeconds = recipe.timeSeconds;
        }

        public void tickRecipe(double deltaSeconds) {
            remainingTimeSeconds -= deltaSeconds;

            List<Pedestal.PedestalBlock> usedPedestals = getPedestals()
                    .stream()
                    .filter(pedestal -> !pedestal.getItemDisplay().getItemStack().getType().isAir())
                    .toList();

            // dust line animation
            for (Pedestal.PedestalBlock pedestal : usedPedestals) {
                drawLine(
                        pedestal.getBlock().getLocation().toCenterLocation().add(0.0, 0.7, 0.0),
                        getBlock().getLocation().toCenterLocation().subtract(0.0, 0.3, 0.0),
                        0.25,
                        location -> new ParticleBuilder(Particle.DUST)
                                .color(Color.RED)
                                .extra(0.8F)
                                .count(1)
                                .location(location)
                                .spawn()
                );
            }

            // end rod line animation
            assert usedPedestals.size() > 1;
            int from = random.nextInt(usedPedestals.size());
            int to = random.nextInt(usedPedestals.size());
            while (to == from) {
                to = random.nextInt(usedPedestals.size());
            }
            drawLine(
                    usedPedestals.get(from).getBlock().getLocation().toCenterLocation(),
                    usedPedestals.get(to).getBlock().getLocation().toCenterLocation(),
                    0.25,
                    location -> new ParticleBuilder(Particle.END_ROD)
                            .count(1)
                            .extra(0.0)
                            .location(location)
                            .spawn()
            );
        }

        public void finishRecipe() {
            ItemDisplay itemDisplay = getHeldEntity(Pedestal.PedestalEntity.class, "item").getEntity();

            for (Pedestal.PedestalBlock pedestal : getPedestals()) {
                pedestal.getItemDisplay().setItemStack(null);
                pedestal.setLocked(false);
            }

            itemDisplay.setItemStack(getCurrentRecipe().result);
            processingRecipe = null;

            new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
                    .count(20)
                    .extra(0.02)
                    .location(getBlock().getLocation().toCenterLocation())
                    .spawn();
        }

        public void cancelRecipe() {
            for (Pedestal.PedestalBlock pedestal : getPedestals()) {
                if (pedestal != null) {
                    pedestal.setLocked(false);
                }
            }

            processingRecipe = null;

            new ParticleBuilder(Particle.WHITE_SMOKE)
                    .count(20)
                    .extra(0.05)
                    .location(getBlock().getLocation().toCenterLocation())
                    .spawn();
        }
    }

    /**
     * Ingredients list must be of size 8. Set an ingredient to null to leave that pedestal empty.
     *
     * Ingredients and catalyst must have an amount of 1
     */
    public record Recipe(
            @NotNull NamespacedKey key,
            @NotNull List<RecipeChoice> ingredients,
            @NotNull RecipeChoice catalyst,
            @NotNull ItemStack result,
            double timeSeconds
    ) implements Keyed {

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(
                new NamespacedKey(PylonBase.getInstance(), "magic_altar")
        );

        static {
            PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
        }

        public boolean ingredientsMatch(@NotNull List<ItemStack> ingredients) {
            assert this.ingredients.size() == PEDESTAL_COUNT;
            assert ingredients.size() == PEDESTAL_COUNT;

            for (int i = 0; i < PEDESTAL_COUNT; i++) {

                boolean allIngredientsMatch = true;
                for (int j = 0; j < PEDESTAL_COUNT; j++) {
                    RecipeChoice recipeChoice = this.ingredients.get(j);
                    if (recipeChoice != null && !recipeChoice.test(ingredients.get(j))) {
                        allIngredientsMatch = false;
                        break;
                    }
                }

                if (allIngredientsMatch) {
                    return true;
                }

                ingredients.add(ingredients.removeFirst());
            }

            return false;
        }

        public boolean isValidRecipe(@NotNull List<ItemStack> ingredients, ItemStack catalyst) {
            return ingredientsMatch(ingredients) && this.catalyst.test(catalyst);
        }
    }
}
