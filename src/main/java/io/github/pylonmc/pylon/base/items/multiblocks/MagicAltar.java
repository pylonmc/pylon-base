package io.github.pylonmc.pylon.base.items.multiblocks;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.*;
import java.util.function.Consumer;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public class MagicAltar extends PylonBlock implements PylonSimpleMultiblock, PylonTickingBlock, PylonInteractableBlock {

    public static final NamespacedKey KEY = pylonKey("magic_altar");

    private static final int PEDESTAL_COUNT = 8;

    private static final NamespacedKey PROCESSING_RECIPE = pylonKey("processing_recipe");
    private static final NamespacedKey REMAINING_TIME_SECONDS = pylonKey("remaining_time_seconds");
    private static final Random random = new Random();

    private @Nullable NamespacedKey processingRecipe;
    private double remainingTimeSeconds;

    private static final Component MAGIC_PEDESTAL_COMPONENT = new PylonSimpleMultiblock.PylonComponent(PylonItems.MAGIC_PEDESTAL_KEY);

    @SuppressWarnings("unused")
    public MagicAltar(Block block, BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public MagicAltar(Block block, PersistentDataContainer pdc) {
        super(block);

        processingRecipe = pdc.get(PROCESSING_RECIPE, PylonSerializers.NAMESPACED_KEY);
        remainingTimeSeconds = pdc.get(REMAINING_TIME_SECONDS, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(PersistentDataContainer pdc) {
        PdcUtils.setNullable(pdc, PROCESSING_RECIPE, PylonSerializers.NAMESPACED_KEY, processingRecipe);
    }

    @Override
    public Map<Vector3i, Component> getComponents() {
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
    public void onInteract(PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        ItemDisplay itemDisplay = getHeldEntity(Pedestal.PedestalItemEntity.class, "item").getEntity();

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
            for (Pedestal pedestal : getPedestals()) {
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

    public List<Pedestal> getPedestals() {
        List<Pedestal> pedestals = new ArrayList<>();
        for (Vector3i vector : getComponents().keySet()) {
            Block block = getBlock().getRelative(vector.x, vector.y, vector.z);
            pedestals.add(BlockStorage.getAs(Pedestal.class, block));
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
        for (Pedestal pedestal : getPedestals()) {
            pedestal.setLocked(true);
        }
        processingRecipe = recipe.key;
        remainingTimeSeconds = recipe.timeSeconds;
    }

    public void tickRecipe(double deltaSeconds) {
        remainingTimeSeconds -= deltaSeconds;

        List<Pedestal> usedPedestals = getPedestals()
                .stream()
                .filter(pedestal -> !pedestal.getItemDisplay().getItemStack().getType().isAir())
                .toList();

        // dust line animation
        for (Pedestal pedestal : usedPedestals) {
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
        ItemDisplay itemDisplay = getHeldEntity(Pedestal.PedestalItemEntity.class, "item").getEntity();

        for (Pedestal pedestal : getPedestals()) {
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
        for (Pedestal pedestal : getPedestals()) {
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

    public static void drawLine(
            Location start,
            Location end,
            double spacing,
            Consumer<Location> spawnParticle
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

    /**
     * Ingredients list must be of size 8. Set an ingredient to null to leave that pedestal empty.
     *
     * Ingredients and catalyst must have an amount of 1
     */
    public record Recipe(
            NamespacedKey key,
            List<RecipeChoice> ingredients,
            RecipeChoice catalyst,
            ItemStack result,
            double timeSeconds
    ) implements Keyed {

        @Override
        public NamespacedKey getKey() {
            return key;
        }

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(
                new NamespacedKey(PylonBase.getInstance(), "magic_altar")
        );

        static {
            PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
        }

        public boolean ingredientsMatch(List<ItemStack> ingredients) {
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

        public boolean isValidRecipe(List<ItemStack> ingredients, ItemStack catalyst) {
            return ingredientsMatch(ingredients) && this.catalyst.test(catalyst);
        }
    }
}
