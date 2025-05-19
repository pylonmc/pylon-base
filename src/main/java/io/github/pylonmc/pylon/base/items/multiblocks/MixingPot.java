package io.github.pylonmc.pylon.base.items.multiblocks;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.base.items.fluid.connection.FluidConnectionInteraction;
import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.item.builder.Quantity;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


@NullMarked
public final class MixingPot {

    private MixingPot() {
        throw new AssertionError("Container class");
    }

    public static class MixingPotBlock extends PylonBlock<PylonBlockSchema>
            implements PylonMultiblock, PylonInteractableBlock, PylonFluidBlock, PylonEntityHolderBlock {

        private static final NamespacedKey FLUID_KEY = KeyUtils.pylonKey("fluid");
        private static final NamespacedKey FLUID_AMOUNT_KEY = KeyUtils.pylonKey("fluid_amount");

        @Getter
        @Setter
        @Nullable
        private PylonFluid fluidType;

        @Getter
        @Setter
        private long fluidAmount;

        private final Map<String, UUID> entities;

        @SuppressWarnings("unused")
        public MixingPotBlock(PylonBlockSchema schema, Block block, BlockCreateContext context) {
            super(schema, block);

            Player player = null;
            if (context instanceof BlockCreateContext.PlayerPlace ctx) {
                player = ctx.getPlayer();
            }

            var inputNorth = new FluidConnectionPoint(getBlock(), "input_north", FluidConnectionPoint.Type.INPUT);
            var inputSouth = new FluidConnectionPoint(getBlock(), "input_south", FluidConnectionPoint.Type.INPUT);
            var outputEast = new FluidConnectionPoint(getBlock(), "output_east", FluidConnectionPoint.Type.OUTPUT);
            var outputWest = new FluidConnectionPoint(getBlock(), "output_west", FluidConnectionPoint.Type.OUTPUT);

            entities = Map.of(
                    "input_north", FluidConnectionInteraction.make(player, inputNorth, BlockFace.NORTH, 0.5F).getUuid(),
                    "input_south", FluidConnectionInteraction.make(player, inputSouth, BlockFace.SOUTH, 0.5F).getUuid(),
                    "output_east", FluidConnectionInteraction.make(player, outputEast, BlockFace.EAST, 0.5F).getUuid(),
                    "output_west", FluidConnectionInteraction.make(player, outputWest, BlockFace.WEST, 0.5F).getUuid()
            );
            fluidType = null;
            fluidAmount = 0;
        }

        @SuppressWarnings("unused")
        public MixingPotBlock(PylonBlockSchema schema, Block block, PersistentDataContainer pdc) {
            super(schema, block);

            entities = loadHeldEntities(pdc);
            fluidType = pdc.get(FLUID_KEY, PylonSerializers.PYLON_FLUID);
            fluidAmount = pdc.getOrDefault(FLUID_AMOUNT_KEY, PylonSerializers.LONG, 0L);
        }

        @Override
        public void write(PersistentDataContainer pdc) {
            saveHeldEntities(pdc);
            PdcUtils.setNullable(pdc, FLUID_KEY, PylonSerializers.PYLON_FLUID, fluidType);
            pdc.set(FLUID_AMOUNT_KEY, PylonSerializers.LONG, fluidAmount);
        }

        @Override
        public Set<ChunkPosition> getChunksOccupied() {
            return Set.of(new ChunkPosition(getBlock()));
        }

        @Override
        public boolean checkFormed() {
            return getFire().getType() == Material.FIRE;
        }

        @Override
        public boolean isPartOfMultiblock(Block otherBlock) {
            return new BlockPosition(otherBlock).equals(new BlockPosition(getFire()));
        }

        @Override
        public Map<PylonFluid, Long> getSuppliedFluids(String connectionPoint) {
            return fluidType == null
                    ? Map.of()
                    : Map.of(fluidType, fluidAmount);
        }

        @Override
        public Map<PylonFluid, Long> getRequestedFluids(String connectionPoint) {
            if (fluidType == null) {
                return PylonRegistry.FLUIDS.getValues()
                        .stream()
                        .collect(Collectors.toMap(Function.identity(), key -> 1000L));
            }
            if (fluidAmount >= 1000) {
                return Map.of();
            }
            return Map.of(fluidType, 1000L - fluidAmount);
        }

        @Override
        public void addFluid(String connectionPoint, PylonFluid fluid, long amount) {
            if (fluidType == null) {
                fluidType = fluid;
            }
            fluidAmount += amount;
            updateCauldron();
        }

        @Override
        public void removeFluid(String connectionPoint, PylonFluid fluid, long amount) {
            fluidAmount -= amount;
            if (fluidAmount <= 0) {
                fluidType = null;
                fluidAmount = 0;
            }
            updateCauldron();
        }

        private void updateCauldron() {
            int level = (int) fluidAmount / 333;
            if (level > 0 && getBlock().getType() == Material.CAULDRON) {
                getBlock().setType(Material.WATER_CAULDRON);
            } else if (level == 0) {
                getBlock().setType(Material.CAULDRON);
            }
            if (getBlock().getBlockData() instanceof Levelled levelled) {
                levelled.setLevel(level);
                getBlock().setBlockData(levelled);
            }
        }

        @Override
        public Map<String, UUID> getHeldEntities() {
            return entities;
        }

        @Override
        public WailaConfig getWaila(@NotNull Player player) {
            Component text = Component.text("").append(getName());
            if (fluidType != null) {
                text = text.append(Component.text(" | "))
                        .append(fluidType.getName())
                        .append(Component.text(": "))
                        .append(Component.text(fluidAmount))
                        .append(Quantity.FLUID);
            }
            return new WailaConfig(text);
        }

        @Override
        public void onInteract(PlayerInteractEvent event) {
            // Only allow inserting water - events trying to insert lava will be cancelled
            if (event.getItem() != null && Set.of(Material.BUCKET, Material.WATER_BUCKET, Material.GLASS_BOTTLE).contains(event.getMaterial())) {
                switch (event.getMaterial()) {
                    case BUCKET -> {
                        if (PylonFluids.WATER.equals(fluidType) && fluidAmount == 1000) {
                            fluidType = null;
                            fluidAmount = 0;
                            return;
                        }
                    }
                    case WATER_BUCKET -> {
                        if (fluidType == null) {
                            fluidType = PylonFluids.WATER;
                            fluidAmount = 1000;
                            return;
                        }
                    }
                    case GLASS_BOTTLE -> {
                        if (PylonFluids.WATER.equals(fluidType) && fluidAmount >= 250) {
                            fluidAmount -= 250;
                            if (fluidAmount == 0) {
                                fluidType = null;
                            }
                            return;
                        }
                    }
                    case POTION -> {
                        PotionMeta meta = (PotionMeta) event.getItem().getItemMeta();
                        if (!meta.hasCustomEffects()
                                && PotionType.WATER.equals(meta.getBasePotionType())
                                && (fluidType == null || fluidType == PylonFluids.WATER)
                                && fluidAmount <= 250 * 3
                        ) {
                            fluidType = PylonFluids.WATER;
                            fluidAmount += 250;
                            return;
                        }
                    }
                }
            }

            if (event.getPlayer().isSneaking() || event.getHand() != EquipmentSlot.HAND || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }

            event.setCancelled(true);

            if (!isFormedAndFullyLoaded()) {
                return;
            }

            if (!(getBlock().getBlockData() instanceof Levelled)) {
                return;
            }

            if (fluidType == null) {
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
                if (recipe.matches(stacks, isEnrichedFire, fluidType, (int) fluidAmount)) {
                    doRecipe(recipe, items);
                    break;
                }
            }
        }

        private void doRecipe(Recipe recipe, List<Item> items) {
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
     * @param fluidAmount the number of millibuckets of fluid to be used in the recipe
     */
    public record Recipe(
            NamespacedKey key,
            Map<RecipeChoice, Integer> input,
            ItemStack output,
            boolean requiresEnrichedFire,
            PylonFluid fluid,
            int fluidAmount
    ) implements Keyed {

        @Override
        public NamespacedKey getKey() {
            return key;
        }

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(
                new NamespacedKey(PylonBase.getInstance(), "mixing_pot")
        );

        static {
            PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
        }

        public boolean matches(
                List<ItemStack> input,
                boolean isEnrichedFire,
                PylonFluid fluid,
                int fluidAmount
        ) {
            if (requiresEnrichedFire && !isEnrichedFire) {
                return false;
            }

            if (fluidAmount < this.fluidAmount || fluid != this.fluid) {
                return false;
            }

            for (Map.Entry<RecipeChoice, Integer> choice : this.input.entrySet()) {
                boolean anyMatches = false;
                for (ItemStack stack : input) {
                    if (choice.getKey().test(stack) && stack.getAmount() >= choice.getValue()) {
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
        void takeIngredients(List<Item> items, Block block) {
            Levelled levelled = (Levelled) block.getBlockData();
            int newLevel = levelled.getLevel() - fluidAmount;
            if (newLevel < levelled.getMinimumLevel()) {
                block.setType(Material.CAULDRON);
            } else {
                levelled.setLevel(newLevel);
                block.setBlockData(levelled);
            }

            for (Map.Entry<RecipeChoice, Integer> choice : input.entrySet()) {
                for (Item item : items) {
                    ItemStack stack = item.getItemStack();
                    if (choice.getKey().test(stack) && stack.getAmount() >= choice.getValue()) {
                        item.setItemStack(stack.subtract(choice.getValue()));
                        break;
                    }
                }
            }
        }
    }
}
