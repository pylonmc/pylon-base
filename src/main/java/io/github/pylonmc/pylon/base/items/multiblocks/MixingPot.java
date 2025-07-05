package io.github.pylonmc.pylon.base.items.multiblocks;

import com.destroystokyo.paper.ParticleBuilder;
import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.base.fluid.pipe.PylonFluidIoBlock;
import io.github.pylonmc.pylon.base.fluid.pipe.SimpleFluidConnectionPoint;
import io.github.pylonmc.pylon.base.util.Either;
import io.github.pylonmc.pylon.base.util.KeyUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.fluid.FluidConnectionPoint;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
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
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

public final class MixingPot extends PylonBlock implements PylonMultiblock, PylonInteractableBlock, PylonFluidIoBlock {

    public static final NamespacedKey KEY = pylonKey("mixing_pot");

    private static final NamespacedKey FLUID_KEY = KeyUtils.pylonKey("fluid");
    private static final NamespacedKey FLUID_AMOUNT_KEY = KeyUtils.pylonKey("fluid_amount");

    @Getter
    @Setter
    private @Nullable PylonFluid fluidType;

    @Getter
    @Setter
    private double fluidAmount;

    @SuppressWarnings("unused")
    public MixingPot(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        fluidType = null;
        fluidAmount = 0;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public MixingPot(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);

        fluidType = pdc.get(FLUID_KEY, PylonSerializers.PYLON_FLUID);
        fluidAmount = pdc.get(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PdcUtils.setNullable(pdc, FLUID_KEY, PylonSerializers.PYLON_FLUID, fluidType);
        pdc.set(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, fluidAmount);
    }

    @Override
    public @NotNull List<SimpleFluidConnectionPoint> createFluidConnectionPoints(@NotNull BlockCreateContext context) {
        return List.of(
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.INPUT, BlockFace.NORTH),
                new SimpleFluidConnectionPoint(FluidConnectionPoint.Type.OUTPUT, BlockFace.SOUTH)
        );
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
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        return fluidType == null
                ? Map.of()
                : Map.of(fluidType, fluidAmount);
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getRequestedFluids(@NotNull String connectionPoint, double deltaSeconds) {
        if (fluidType == null) {
            return PylonRegistry.FLUIDS.getValues()
                    .stream()
                    .collect(Collectors.toMap(Function.identity(), key -> 1000D));
        }
        if (fluidAmount >= 1000) {
            return Map.of();
        }
        return Map.of(fluidType, 1000L - fluidAmount);
    }

    @Override
    public void addFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        if (fluidType == null) {
            fluidType = fluid;
        }
        fluidAmount += amount;
        updateCauldron();
    }

    @Override
    public void removeFluid(@NotNull String connectionPoint, @NotNull PylonFluid fluid, double amount) {
        fluidAmount -= amount;
        if (fluidAmount <= 1.0e-6) {
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
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        Component text = Component.text("").append(getName());
        if (fluidType != null) {
            text = text.append(Component.text(" | "))
                    .append(fluidType.getName())
                    .append(Component.text(": "))
                    .append(UnitFormat.MILLIBUCKETS.format(fluidAmount).decimalPlaces(1));
        }
        return new WailaConfig(text);
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

        if (!isFormedAndFullyLoaded() || fluidType == null) {
            return;
        }

        tryDoRecipe(event.getPlayer());
    }

    public boolean tryDoRecipe(@Nullable Player player) {
        List<Item> items = getBlock()
                .getLocation()
                .toCenterLocation()
                .getNearbyEntities(0.5, 0.8, 0.5) // 0.8 to allow items on top to be used
                .stream()
                .filter(Item.class::isInstance)
                .map(Item.class::cast)
                .toList();

        List<ItemStack> stacks = items.stream()
                .map(Item::getItemStack)
                .toList();

        PylonBlock ignitedBlock = BlockStorage.get(getIgnitedBlock());
        boolean isEnrichedFire = ignitedBlock != null
                && ignitedBlock.getSchema().getKey().equals(EnrichedNetherrack.KEY);

        for (Recipe recipe : Recipe.RECIPE_TYPE.getRecipes()) {
            if (recipe.matches(stacks, isEnrichedFire, fluidType, fluidAmount)) {
                if (!new PrePylonCraftEvent<>(Recipe.RECIPE_TYPE, recipe, this, player).callEvent()) {
                    continue;
                }

                doRecipe(recipe, items);
                return true;
            }
        }

        return false;
    }

    private void doRecipe(@NotNull Recipe recipe, @NotNull List<Item> items) {
        for (Map.Entry<RecipeChoice, Integer> choice : recipe.input.entrySet()) {
            for (Item item1 : items) {
                ItemStack stack = item1.getItemStack();
                if (choice.getKey().test(stack) && stack.getAmount() >= choice.getValue()) {
                    item1.setItemStack(stack.subtract(choice.getValue()));
                    break;
                }
            }
        }
        switch (recipe.output()) {
            case Either.Left(ItemStack item) -> {
                removeFluid("", recipe.fluid, recipe.fluidAmount);
                getBlock().getWorld().dropItemNaturally(getBlock().getLocation().toCenterLocation(), item);
            }
            case Either.Right(PylonFluid fluid) -> fluidType = fluid;
        }

        new PylonCraftEvent<>(Recipe.RECIPE_TYPE, recipe, this).callEvent();

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

    /**
     * Maximum 7 input items
     */
    public record Recipe(
            @NotNull NamespacedKey key,
            @NotNull Map<RecipeChoice, Integer> input,
            @NotNull Either<ItemStack, PylonFluid> output,
            boolean requiresEnrichedFire,
            @NotNull PylonFluid fluid,
            double fluidAmount
    ) implements PylonRecipe {

        public Recipe(
                @NotNull NamespacedKey key,
                @NotNull Map<RecipeChoice, Integer> input,
                @NotNull ItemStack output,
                boolean requiresEnrichedFire,
                @NotNull PylonFluid fluid,
                double fluidAmount
        ) {
            this(key, input, new Either.Left<>(output), requiresEnrichedFire, fluid, fluidAmount);
        }

        public Recipe(
                @NotNull NamespacedKey key,
                @NotNull Map<RecipeChoice, Integer> input,
                @NotNull PylonFluid output,
                boolean requiresEnrichedFire,
                @NotNull PylonFluid fluid,
                double fluidAmount
        ) {
            this(key, input, new Either.Right<>(output), requiresEnrichedFire, fluid, fluidAmount);
        }

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

        public boolean matches(
                List<ItemStack> input,
                boolean isEnrichedFire,
                PylonFluid fluid,
                double fluidAmount
        ) {
            if (requiresEnrichedFire && !isEnrichedFire) {
                return false;
            }

            // stupid floating point
            if (fluidAmount < this.fluidAmount - 1.0e-5 || !fluid.equals(this.fluid)) {
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

        @Override
        public @NotNull List<@NotNull RecipeChoice> getInputItems() {
            return input.keySet().stream().toList();
        }

        @Override
        public @NotNull List<@NotNull PylonFluid> getInputFluids() {
            return List.of(fluid);
        }

        @Override
        public @NotNull List<@NotNull ItemStack> getOutputItems() {
            if (output instanceof Either.Left<ItemStack, PylonFluid>(ItemStack left)) {
                return List.of(left);
            }
            return List.of();
        }

        @Override
        public @NotNull List<@NotNull PylonFluid> getOutputFluids() {
            if (output instanceof Either.Right<ItemStack, PylonFluid>(PylonFluid right)) {
                return List.of(right);
            }
            return List.of();
        }

        @Override
        public @NotNull Gui display() {
            Preconditions.checkState(input.size() <= 7);
            Gui.Builder.Normal builder = Gui.normal()
                    .setStructure(
                            "# # # # # # # # #",
                            "# . . . # f # # #",
                            "# . . . # m # o #",
                            "# . . . # i # # #",
                            "# # # # # # # # #"
                    )
                    .addIngredient('#', GuiItems.backgroundBlack())
                    .addIngredient('f', new FluidButton(fluid.getKey(), fluidAmount))
                    .addIngredient('m', ItemButton.fromStack(PylonItems.MIXING_POT))
                    .addIngredient('i', requiresEnrichedFire
                            ? ItemButton.fromStack(PylonItems.ENRICHED_NETHERRACK)
                            : GuiItems.background()
                    );

            if (output instanceof Either.Left<ItemStack, PylonFluid>(ItemStack left)) {
                builder.addIngredient('o', ItemButton.fromStack(left));
            }
            if (output instanceof Either.Right<ItemStack, PylonFluid>(PylonFluid right)) {
                builder.addIngredient('o', new FluidButton(right.getKey(), fluidAmount));
            }

            Gui gui = builder.build();

            int i = 0;
            for (Map.Entry<RecipeChoice, Integer> entry : input.entrySet()) {
                ItemStack stack = entry.getKey().getItemStack().clone();
                stack.setAmount(entry.getValue());
                gui.setItem(10 + ((i / 3) * 9) + i % 3, ItemButton.fromStack(stack));
                i++;
            }

            return gui;
        }
    }
}
