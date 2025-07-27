package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.building.Pedestal;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.base.recipes.MagicAltarRecipe;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.*;
import java.util.function.Consumer;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class MagicAltar extends PylonBlock implements PylonSimpleMultiblock, PylonTickingBlock, PylonInteractableBlock {

    public static final int PEDESTAL_COUNT = 8;

    private static final NamespacedKey PROCESSING_RECIPE = baseKey("processing_recipe");
    private static final NamespacedKey REMAINING_TIME_SECONDS = baseKey("remaining_time_seconds");
    private static final Random random = new Random();

    private @Nullable NamespacedKey processingRecipe;
    private double remainingTimeSeconds;

    private static final MultiblockComponent MAGIC_PEDESTAL_COMPONENT = new PylonSimpleMultiblock.PylonMultiblockComponent(BaseKeys.MAGIC_PEDESTAL);

    private final int tickInterval = getSettings().getOrThrow("tick-interval", Integer.class);

    @SuppressWarnings("unused")
    public MagicAltar(Block block, BlockCreateContext context) {
        super(block);

        setTickInterval(tickInterval);

        addEntity("item", new SimpleItemDisplay(new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .translate(0, 0.5, 0)
                        .scale(0.5)
                        .buildForItemDisplay()
                )
                .build(getBlock().getLocation().toCenterLocation())
        ));
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public MagicAltar(Block block, PersistentDataContainer pdc) {
        super(block);

        processingRecipe = pdc.get(PROCESSING_RECIPE, PylonSerializers.NAMESPACED_KEY);
        remainingTimeSeconds = pdc.get(REMAINING_TIME_SECONDS, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PdcUtils.setNullable(pdc, PROCESSING_RECIPE, PylonSerializers.NAMESPACED_KEY, processingRecipe);
        pdc.set(REMAINING_TIME_SECONDS, PylonSerializers.DOUBLE, remainingTimeSeconds);
    }

    @Override
    public @NotNull Map<Vector3i, MultiblockComponent> getComponents() {
        // use linked to retain order of pedestals - important for recipes
        Map<Vector3i, MultiblockComponent> map = new LinkedHashMap<>();
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

        ItemDisplay itemDisplay = getItemDisplay().getEntity();

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

            for (MagicAltarRecipe recipe : MagicAltarRecipe.RECIPE_TYPE.getRecipes()) {
                if (recipe.isValidRecipe(ingredients, catalyst)) {
                    if (!new PrePylonCraftEvent<>(MagicAltarRecipe.RECIPE_TYPE, recipe, this, event.getPlayer()).callEvent()) {
                        continue;
                    }

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

    public @Nullable MagicAltarRecipe getCurrentRecipe() {
        if (processingRecipe == null) {
            return null;
        }
        return MagicAltarRecipe.RECIPE_TYPE.getRecipe(processingRecipe);
    }

    public SimpleItemDisplay getItemDisplay() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "item");
    }

    public void startRecipe(MagicAltarRecipe recipe) {
        for (Pedestal pedestal : getPedestals()) {
            pedestal.setLocked(true);
        }
        processingRecipe = recipe.key();
        remainingTimeSeconds = recipe.timeSeconds();
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
        ItemDisplay itemDisplay = getItemDisplay().getEntity();

        for (Pedestal pedestal : getPedestals()) {
            pedestal.getItemDisplay().setItemStack(null);
            pedestal.setLocked(false);
        }

        new PylonCraftEvent<>(MagicAltarRecipe.RECIPE_TYPE, getCurrentRecipe(), this).callEvent();

        itemDisplay.setItemStack(getCurrentRecipe().result());
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
}
