package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonBlocks;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.block.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.Multiblock;
import io.github.pylonmc.pylon.core.block.base.PlayerInteractBlock;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.BlockPlacer;
import io.github.pylonmc.pylon.core.persistence.blockstorage.BlockStorage;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import kotlin.Pair;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;


public final class MixingPot {

    private MixingPot() {
        throw new AssertionError("Container class");
    }

    public static class MixingPotItem extends PylonItem<PylonItemSchema> implements BlockPlacer {

        public MixingPotItem(@NotNull PylonItemSchema schema, @NotNull ItemStack stack) {
            super(schema, stack);
        }

        @Override
        public @NotNull PylonBlockSchema getBlockSchema() {
            return PylonBlocks.MIXING_POT;
        }
    }

    public static class MixingPotBlock extends PylonBlock<PylonBlockSchema> implements Multiblock, PlayerInteractBlock {

        @SuppressWarnings("unused")
        public MixingPotBlock(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull BlockCreateContext context) {
            super(schema, block);
        }

        @SuppressWarnings("unused")
        public MixingPotBlock(@NotNull PylonBlockSchema schema, @NotNull Block block, @NotNull PersistentDataContainer pdc) {
            super(schema, block);
        }

        @Override
        public @NotNull Set<ChunkPosition> getChunksOccupied() {
            return Set.of(new ChunkPosition(getBlock()));
        }

        @Override
        public boolean checkFormed() {
            return getFire().getType() == Material.FIRE;
        }

        @Override
        public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
            return new BlockPosition(otherBlock).equals(new BlockPosition(getFire()));
        }

        @Override
        public void onInteract(@NotNull PlayerInteractEvent event) {
            // Only allow inserting water - events trying to insert lava will be cancelled
            if (event.getItem() != null && Set.of(Material.BUCKET, Material.WATER_BUCKET, Material.GLASS_BOTTLE).contains(event.getMaterial())) {
                return;
            }

            if (event.getPlayer().isSneaking() || event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            event.setCancelled(true);

            if (!isFormedAndFullyLoaded()) {
                return;
            }

            if (!(getBlock().getBlockData() instanceof Levelled levelled)) {
                return;
            }

            List<Item> items = getBlock()
                    .getLocation()
                    .toCenterLocation()
                    .getNearbyEntities(0.5, 0.5, 0.5)
                    .stream()
                    .filter(Item.class::isInstance)
                    .map(Item.class::cast)
                    .toList();

            List<ItemStack> stacks = items.stream()
                    .map(Item::getItemStack)
                    .toList();


            PylonBlock<?> ignitedBlock = BlockStorage.get(getIgnitedBlock());
            boolean isEnrichedFire = ignitedBlock != null
                    && ignitedBlock.getSchema().getKey().equals(PylonItems.ENRICHED_NETHERRACK.getKey());

            for (Recipe recipe : Recipe.RECIPE_TYPE.getRecipes()) {
                if (recipe.matches(stacks, isEnrichedFire, levelled.getLevel())) {
                    doRecipe(recipe, items);
                    break;
                }
            }
        }

        private void doRecipe(@NotNull Recipe recipe, @NotNull List<Item> items) {
            recipe.takeIngredients(items, getBlock());
            getBlock().getWorld().dropItemNaturally(getBlock().getLocation().toCenterLocation(), recipe.output);

            new ParticleBuilder(Particle.SPLASH)
                    .count(20)
                    .location(getBlock().getLocation().toCenterLocation().add(0, 0.5, 0))
                    .offset(0.3, 0, 0.3)
                    .spawn();

            new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
                    .count(30)
                    .location(getBlock().getLocation().toCenterLocation())
                    .extra(0.05)
                    .spawn();
        }

        public Block getFire() {
            return getBlock().getRelative(BlockFace.DOWN);
        }

        public Block getIgnitedBlock() {
            return getFire().getRelative(BlockFace.DOWN);
        }
    }

    /**
     * Liquid amount can be 0 (empty) to 3 (full cauldron / 1 bucket of water)
     */
    public record Recipe(
            @NotNull NamespacedKey key,
            @NotNull List<Pair<RecipeChoice, Integer>> input,
            @NotNull ItemStack output,
            boolean requiresEnrichedFire,
            int waterAmount
    ) implements Keyed {

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(
                new NamespacedKey(PylonBase.getInstance(), "mixing_pot")
        );

        static {
            PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
        }

        public boolean matches(@NotNull List<ItemStack> input, boolean isEnrichedFire, int waterAmount) {
            if (requiresEnrichedFire && !isEnrichedFire) {
                return false;
            }

            if (waterAmount < this.waterAmount) {
                return false;
            }

            for (Pair<RecipeChoice, Integer> choice : this.input) {
                boolean anyMatches = false;
                for (ItemStack stack : input) {
                    if (choice.getFirst().test(stack) && stack.getAmount() >= choice.getSecond()) {
                        anyMatches = true;
                        break;
                    }
                }
                if (!anyMatches) {
                    return false;
                }
            }

            return true;
        }

        /**
         * Assumes that recipe has been already checked to make sure it matches, and the block is a Levelled
         */
        void takeIngredients(@NotNull List<Item> items, @NotNull Block block) {
            Levelled levelled = (Levelled) block.getBlockData();
            int newLevel = levelled.getLevel() - waterAmount;
            if (newLevel < levelled.getMinimumLevel()) {
                block.setType(Material.CAULDRON);
            } else {
                levelled.setLevel(newLevel);
                block.setBlockData(levelled);
            }

            for (Pair<RecipeChoice, Integer> choice : input) {
                for (Item item : items) {
                    ItemStack stack = item.getItemStack();
                    if (choice.getFirst().test(stack) && stack.getAmount() >= choice.getSecond()) {
                        item.setItemStack(stack.subtract(choice.getSecond()));
                        break;
                    }
                }
            }
        }
    }
}
