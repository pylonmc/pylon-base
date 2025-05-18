package io.github.pylonmc.pylon.base.items.multiblocks;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.base.PylonEntities;
import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.EntityStorage;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.PylonEntitySchema;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public final class Grindstone {

    private static final int TICK_RATE = 2;
    private static final int CYCLE_TIME_TICKS = 20;

    private Grindstone() {
        throw new AssertionError("Container class");
    }

    public static class GrindstoneBlock extends PylonBlock<PylonBlockSchema>
            implements PylonSimpleMultiblock, PylonInteractableBlock, PylonTickingBlock {

        private static final NamespacedKey RECIPE_KEY = KeyUtils.pylonKey("recipe");
        private static final NamespacedKey CYCLES_REMAINING_KEY = KeyUtils.pylonKey("cycles_remaining");
        private static final NamespacedKey CYCLE_TICKS_REMAINING_KEY = KeyUtils.pylonKey("cycle_ticks_remaining");

        private final Map<String, UUID> entities;
        private @Nullable NamespacedKey recipe;
        private @Nullable Integer cyclesRemaining;
        private @Nullable Integer cycleTicksRemaining;

        @SuppressWarnings("unused")
        public GrindstoneBlock(PylonBlockSchema schema, Block block, BlockCreateContext context) {
            super(schema, block);

            ItemDisplay itemDisplay = new ItemDisplayBuilder()
                    .transformation(new TransformBuilder()
                            .scale(0.3)
                            .translate(0, 0.15, 0)
                            .rotate(Math.PI / 2, 0, 0))
                    .build(block.getLocation().toCenterLocation());
            EntityStorage.add(new GrindstoneItemEntity(PylonEntities.GRINDSTONE_ITEM, itemDisplay));

            ItemDisplay stoneDisplay = new ItemDisplayBuilder()
                    .material(Material.SMOOTH_STONE_SLAB)
                    .transformation(new TransformBuilder()
                            .translate(0, 0.8, 0))
                    .build(block.getLocation().toCenterLocation());
            EntityStorage.add(new GrindstoneBlockEntity(PylonEntities.GRINDSTONE_BLOCK, stoneDisplay));

            recipe = null;
            cyclesRemaining = null;
            cycleTicksRemaining = null;

            entities = new HashMap<>();
            entities.put("item", itemDisplay.getUniqueId());
            entities.put("block", stoneDisplay.getUniqueId());

            spawnMultiblockGhosts();
        }

        @SuppressWarnings("unused")
        public GrindstoneBlock(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);
            entities = loadHeldEntities(pdc);
            recipe = pdc.get(RECIPE_KEY, PylonSerializers.NAMESPACED_KEY);
            cyclesRemaining = pdc.get(CYCLES_REMAINING_KEY, PylonSerializers.INTEGER);
            cycleTicksRemaining = pdc.get(CYCLE_TICKS_REMAINING_KEY, PylonSerializers.INTEGER);
        }

        @Override
        public void write(@NotNull PersistentDataContainer pdc) {
            saveHeldEntities(pdc);

            PdcUtils.setNullable(pdc, RECIPE_KEY, PylonSerializers.NAMESPACED_KEY, recipe);
            PdcUtils.setNullable(pdc, CYCLES_REMAINING_KEY, PylonSerializers.INTEGER, cyclesRemaining);
            PdcUtils.setNullable(pdc, CYCLE_TICKS_REMAINING_KEY, PylonSerializers.INTEGER, cycleTicksRemaining);
        }

        @Override
        public @NotNull Map<String, UUID> getHeldEntities() {
            return entities;
        }

        @Override
        public @NotNull Map<Vector3i, Component> getComponents() {
            return Map.of(new Vector3i(0, 1, 0), new PylonComponent(PylonBlocks.GRINDSTONE_HANDLE.getKey()));
        }

        @Override
        public void onInteract(@NotNull PlayerInteractEvent event) {
            if (event.getPlayer().isSneaking() || event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            event.setCancelled(true);

            if (!isFormedAndFullyLoaded()) {
                return;
            }

            if (recipe != null) {
                return;
            }

            ItemDisplay itemDisplay = getItemDisplay();
            ItemStack oldStack = itemDisplay.getItemStack();
            ItemStack newStack = event.getItem();

            // drop old item
            if (!oldStack.getType().isAir()) {
                getBlock().getWorld().dropItem(getBlock().getLocation().toCenterLocation().add(0, 0.25, 0), oldStack);
                itemDisplay.setItemStack(null);
                return;
            }

            // insert new item
            if (newStack != null) {
                ItemStack stackToInsert = newStack.clone();
                itemDisplay.setItemStack(stackToInsert);
                newStack.setAmount(0);
            }
        }

        @Override
        public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
            PylonSimpleMultiblock.super.onBreak(drops, context);
            drops.add(getItemDisplay().getItemStack());
        }

        @Override
        public int getCustomTickRate(int globalTickRate) {
            return TICK_RATE;
        }

        @Override
        public void tick(double deltaSeconds) {
            if (!isFormedAndFullyLoaded() || cyclesRemaining == null || recipe == null || cycleTicksRemaining == null) {
                return;
            }

            // check if cycle finished
            if (cycleTicksRemaining <= 0) {
                cycleTicksRemaining = CYCLE_TIME_TICKS;
                cyclesRemaining -= 1;

                // check if recipe finished
                if (cyclesRemaining <= 0) {

                    // rotate stone back to starting rotation
                    getStoneDisplay().setTransformationMatrix(new TransformBuilder()
                            .translate(0, 0.5, 0)
                            .buildForItemDisplay());
                    getStoneDisplay().setInterpolationDelay(0);
                    getStoneDisplay().setInterpolationDuration(TICK_RATE);

                    cyclesRemaining = null;
                    cycleTicksRemaining = null;

                    // run after interpolation to starting rotation is finished
                    Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), this::finishRecipe, TICK_RATE);

                    return;
                }
            }

            if (cycleTicksRemaining == CYCLE_TIME_TICKS) {
                // put top stone down
                getStoneDisplay().setTransformationMatrix(new TransformBuilder()
                        .translate(0, 0.5, 0)
                        .buildForItemDisplay());
            } else {
                // rotate stone
                getStoneDisplay().setTransformationMatrix(new TransformBuilder()
                        .translate(0, 0.5, 0)
                        .rotate(0, 2 * Math.PI * ((double) cycleTicksRemaining / CYCLE_TIME_TICKS), 0)
                        .buildForItemDisplay());

                Recipe recipe = Recipe.RECIPE_TYPE.getRecipe(this.recipe);
                assert recipe != null;
                new ParticleBuilder(Particle.BLOCK)
                        .data(recipe.particleBlockData)
                        .count(10)
                        .location(getBlock().getLocation().toCenterLocation())
                        .spawn();
            }
            getStoneDisplay().setInterpolationDelay(0);
            getStoneDisplay().setInterpolationDuration(TICK_RATE);

            cycleTicksRemaining -= TICK_RATE;
        }

        public void tryStartRecipe() {
            if (recipe != null || cyclesRemaining != null || cycleTicksRemaining != null) {
                return;
            }

            ItemStack input = getItemDisplay().getItemStack();
            if (input.getType().isAir()) {
                return;
            }

            for (Recipe recipe : Recipe.RECIPE_TYPE.getRecipes()) {
                if (recipe.input.test(input) && input.getAmount() >= recipe.inputAmount) {
                    getItemDisplay().setItemStack(input.subtract(recipe.inputAmount));
                    this.recipe = recipe.key;
                    cyclesRemaining = recipe.cycles;
                    cycleTicksRemaining = CYCLE_TIME_TICKS;
                    break;
                }
            }
        }

        public void finishRecipe() {
            assert recipe != null;
            Recipe recipe = Recipe.RECIPE_TYPE.getRecipe(this.recipe);
            assert recipe != null;
            getBlock().getWorld().dropItemNaturally(getBlock().getLocation().toCenterLocation().add(0, 0.25, 0), recipe.output);

            // lift stone up
            getStoneDisplay().setTransformationMatrix(new TransformBuilder()
                    .translate(0, 0.8, 0)
                    .buildForItemDisplay());
            getStoneDisplay().setInterpolationDelay(0);
            getStoneDisplay().setInterpolationDuration(TICK_RATE);

            this.recipe = null;
        }

        public @NotNull ItemDisplay getItemDisplay() {
            return getHeldEntity(GrindstoneItemEntity.class, "item").getEntity();
        }

        public @NotNull ItemDisplay getStoneDisplay() {
            return getHeldEntity(GrindstoneBlockEntity.class, "block").getEntity();
        }
    }

    public static class GrindstoneItemEntity extends PylonEntity<PylonEntitySchema, ItemDisplay> {

        public GrindstoneItemEntity(@NotNull PylonEntitySchema schema, @NotNull ItemDisplay entity) {
            super(schema, entity);
        }
    }

    public static class GrindstoneBlockEntity extends PylonEntity<PylonEntitySchema, ItemDisplay> {

        public GrindstoneBlockEntity(@NotNull PylonEntitySchema schema, @NotNull ItemDisplay entity) {
            super(schema, entity);
        }
    }

    public record Recipe(
            @NotNull NamespacedKey key,
            @NotNull RecipeChoice input,
            int inputAmount,
            @NotNull ItemStack output,
            int cycles,
            @NotNull BlockData particleBlockData
    ) implements Keyed {

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(
                new NamespacedKey(PylonBase.getInstance(), "grindstone")
        );

        static {
            PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
        }
    }
}
