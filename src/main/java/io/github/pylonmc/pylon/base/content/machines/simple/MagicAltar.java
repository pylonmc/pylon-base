package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.building.Pedestal;
import io.github.pylonmc.pylon.base.recipes.MagicAltarRecipe;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonRecipeProcessor;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
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
import org.joml.Vector3i;

import java.util.*;
import java.util.function.Consumer;

public class MagicAltar extends PylonBlock
        implements PylonSimpleMultiblock, PylonInteractBlock, PylonTickingBlock, PylonRecipeProcessor<MagicAltarRecipe> {

    public static final int PEDESTAL_COUNT = 8;

    private static final Random random = new Random();

    private static final MultiblockComponent MAGIC_PEDESTAL_COMPONENT = new PylonSimpleMultiblock.PylonMultiblockComponent(BaseKeys.MAGIC_PEDESTAL);

    private final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    @SuppressWarnings("unused")
    public MagicAltar(Block block, BlockCreateContext context) {
        super(block);

        setTickInterval(tickInterval);

        addEntity("item", new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .translate(0, 0.5, 0)
                        .scale(0.5)
                        .buildForItemDisplay()
                )
                .build(getBlock().getLocation().toCenterLocation())
        );

        setRecipeType(MagicAltarRecipe.RECIPE_TYPE);
    }

    @SuppressWarnings("unused")
    public MagicAltar(Block block, PersistentDataContainer pdc) {
        super(block);
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
        if (event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) {
            return;
        }

        event.setCancelled(true);

        // drop item if not processing and an item is already on the altar
        ItemDisplay itemDisplay = getItemDisplay();
        ItemStack displayItem = itemDisplay.getItemStack();
        if (!isProcessingRecipe() && !displayItem.getType().isAir()) {
            Location location = itemDisplay.getLocation().add(0, 0.5, 0);
            location.getWorld().dropItemNaturally(location, displayItem);
            itemDisplay.setItemStack(new ItemStack(Material.AIR));
            return;
        }

        ItemStack catalyst = event.getItem();
        if (!isFormedAndFullyLoaded() || catalyst == null || isProcessingRecipe()) {
            return;
        }

        // attempt to start recipe
        List<ItemStack> ingredients = new ArrayList<>();
        for (Pedestal pedestal : getPedestals()) {
            ingredients.add(pedestal.getItemDisplay().getItemStack());
        }

        for (MagicAltarRecipe recipe : MagicAltarRecipe.RECIPE_TYPE.getRecipes()) {
            if (!recipe.isValidRecipe(ingredients, catalyst)) {
                continue;
            }

            itemDisplay.setItemStack(catalyst.asQuantity(1));
            catalyst.subtract();
            for (Pedestal pedestal : getPedestals()) {
                pedestal.setLocked(true);
            }
            startRecipe(recipe, recipe.timeSeconds() * 20);
            break;
        }
    }

    @Override
    public void tick() {
        progressRecipe(tickInterval);

        if (!isProcessingRecipe()) {
            return;
        }

        if (!isFormedAndFullyLoaded()) {
            cancelRecipe();
            return;
        }

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
        Preconditions.checkState(usedPedestals.size() > 1);
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

    public List<Pedestal> getPedestals() {
        List<Pedestal> pedestals = new ArrayList<>();
        for (Vector3i vector : getComponents().keySet()) {
            Block block = getBlock().getRelative(vector.x, vector.y, vector.z);
            pedestals.add(BlockStorage.getAs(Pedestal.class, block));
        }
        return pedestals;
    }

    public ItemDisplay getItemDisplay() {
        return getHeldEntityOrThrow(ItemDisplay.class, "item");
    }

    @Override
    public void onRecipeFinished(@NotNull MagicAltarRecipe recipe) {
        for (Pedestal pedestal : getPedestals()) {
            pedestal.getItemDisplay().setItemStack(null);
            pedestal.setLocked(false);
        }

        getItemDisplay().setItemStack(recipe.result());

        new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
                .count(20)
                .extra(0.02)
                .location(getBlock().getLocation().toCenterLocation())
                .spawn();
    }

    public void cancelRecipe() {
        stopRecipe();

        for (Pedestal pedestal : getPedestals()) {
            if (pedestal != null) {
                pedestal.setLocked(false);
            }
        }

        new ParticleBuilder(Particle.WHITE_SMOKE)
                .count(20)
                .extra(0.05)
                .location(getBlock().getLocation().toCenterLocation())
                .spawn();
    }

    public static void drawLine(
            Location start,
            @NotNull Location end,
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
