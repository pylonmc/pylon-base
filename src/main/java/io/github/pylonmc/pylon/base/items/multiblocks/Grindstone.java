package io.github.pylonmc.pylon.base.items.multiblocks;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.PylonItems;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3i;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;
import static io.github.pylonmc.pylon.core.util.ItemUtils.isPylonSimilar;


public class Grindstone extends PylonBlock implements PylonSimpleMultiblock, PylonInteractableBlock, PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("grindstone");

    public static final int TICK_RATE = Settings.get(KEY).getOrThrow("tick-rate", Integer.class);
    public static final int CYCLE_TIME_TICKS = Settings.get(KEY).getOrThrow("cycle-time-ticks", Integer.class);

    private static final NamespacedKey RECIPE_KEY = pylonKey("recipe");
    private static final NamespacedKey CYCLES_REMAINING_KEY = pylonKey("cycles_remaining");
    private static final NamespacedKey CYCLE_TICKS_REMAINING_KEY = pylonKey("cycle_ticks_remaining");

    @Getter private @Nullable NamespacedKey recipe;
    private @Nullable Integer cyclesRemaining;
    private @Nullable Integer cycleTicksRemaining;

    @SuppressWarnings("unused")
    public Grindstone(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        recipe = null;
        cyclesRemaining = null;
        cycleTicksRemaining = null;
    }

    @SuppressWarnings("unused")
    public Grindstone(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        recipe = pdc.get(RECIPE_KEY, PylonSerializers.NAMESPACED_KEY);
        cyclesRemaining = pdc.get(CYCLES_REMAINING_KEY, PylonSerializers.INTEGER);
        cycleTicksRemaining = pdc.get(CYCLE_TICKS_REMAINING_KEY, PylonSerializers.INTEGER);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PdcUtils.setNullable(pdc, RECIPE_KEY, PylonSerializers.NAMESPACED_KEY, recipe);
        PdcUtils.setNullable(pdc, CYCLES_REMAINING_KEY, PylonSerializers.INTEGER, cyclesRemaining);
        PdcUtils.setNullable(pdc, CYCLE_TICKS_REMAINING_KEY, PylonSerializers.INTEGER, cycleTicksRemaining);
    }

    @Override
    public @NotNull Map<String, PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        ItemDisplay itemDisplay = new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .scale(0.3)
                        .translate(0, 0.15, 0)
                        .rotate(Math.PI / 2, 0, 0))
                .build(getBlock().getLocation().toCenterLocation());

        ItemDisplay stoneDisplay = new ItemDisplayBuilder()
                .material(Material.SMOOTH_STONE_SLAB)
                .transformation(new TransformBuilder()
                        .translate(0, 0.8, 0))
                .build(getBlock().getLocation().toCenterLocation());

        return Map.of(
                "item", new GrindstoneItemEntity(itemDisplay),
                "block", new GrindstoneBlockEntity(stoneDisplay)
        );
    }

    @Override
    public @NotNull Map<Vector3i, MultiblockComponent> getComponents() {
        return Map.of(new Vector3i(0, 1, 0), new PylonMultiblockComponent(GrindstoneHandle.KEY));
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

    public @Nullable Recipe getNextRecipe() {
        if (recipe != null || cyclesRemaining != null || cycleTicksRemaining != null) {
            return null;
        }

        ItemStack input = getItemDisplay().getItemStack();
        if (input.getType().isAir()) {
            return null;
        }

        return Recipe.RECIPE_TYPE.getRecipes()
                .stream()
                .filter(recipe -> isPylonSimilar(recipe.input, input) && input.getAmount() >= recipe.input.getAmount())
                .findFirst()
                .orElse(null);
    }

    public boolean tryStartRecipe(@NotNull Recipe nextRecipe, @Nullable Player player) {
        if (!new PrePylonCraftEvent<>(Recipe.RECIPE_TYPE, nextRecipe, this, player).callEvent()) {
            return false;
        }

        ItemStack input = getItemDisplay().getItemStack();
        if (input.getType().isAir()) {
            return false;
        }

        getItemDisplay().setItemStack(input.subtract(nextRecipe.input.getAmount()));
        this.recipe = nextRecipe.key;
        cyclesRemaining = nextRecipe.cycles;
        cycleTicksRemaining = CYCLE_TIME_TICKS;
        return true;
    }

    public void finishRecipe() {
        assert recipe != null;
        Recipe recipe = Recipe.RECIPE_TYPE.getRecipe(this.recipe);
        assert recipe != null;
        getBlock().getWorld().dropItemNaturally(getBlock().getLocation().toCenterLocation().add(0, 0.25, 0), recipe.output);

        new PylonCraftEvent<>(Recipe.RECIPE_TYPE, recipe, this).callEvent();

        // lift stone up
        getStoneDisplay().setTransformationMatrix(new TransformBuilder()
                .translate(0, 0.8, 0)
                .buildForItemDisplay());
        getStoneDisplay().setInterpolationDelay(0);
        getStoneDisplay().setInterpolationDuration(TICK_RATE);

        this.recipe = null;
    }

    public ItemDisplay getItemDisplay() {
        return getHeldEntity(GrindstoneItemEntity.class, "item").getEntity();
    }

    public ItemDisplay getStoneDisplay() {
        return getHeldEntity(GrindstoneBlockEntity.class, "block").getEntity();
    }

    public static class GrindstoneItemEntity extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("grindstone_item");

        public GrindstoneItemEntity(@NotNull ItemDisplay entity) {
            super(KEY, entity);
        }
    }

    public static class GrindstoneBlockEntity extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("grindstone_block");

        public GrindstoneBlockEntity(@NotNull ItemDisplay entity) {
            super(KEY, entity);
        }
    }

    public record Recipe(
            NamespacedKey key,
            ItemStack input,
            ItemStack output,
            int cycles,
            BlockData particleBlockData
    ) implements PylonRecipe {

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

        public int timeTicks() {
            // add 2*tick rate to account for stone going up and down
            return cycles * CYCLE_TIME_TICKS + 2 * TICK_RATE;
        }

        @Override
        public @NotNull List<@NotNull RecipeChoice> getInputItems() {
            return List.of(new RecipeChoice.ExactChoice(input));
        }

        @Override
        public @NotNull List<@NotNull ItemStack> getOutputItems() {
            return List.of(output);
        }

        @Override
        public @NotNull Gui display() {
            return Gui.normal()
                    .setStructure(
                            "# # # # # # # # #",
                            "# # # # # # # # #",
                            "# g # # i c o # #",
                            "# # # # # # # # #",
                            "# # # # # # # # #"
                    )
                    .addIngredient('#', GuiItems.backgroundBlack())
                    .addIngredient('g', ItemButton.fromStack(PylonItems.GRINDSTONE))
                    .addIngredient('i', ItemButton.fromStack(input))
                    .addIngredient('c', GuiItems.progressCyclingItem(cycles * CYCLE_TIME_TICKS,
                            ItemStackBuilder.of(Material.CLOCK)
                                    .name(net.kyori.adventure.text.Component.translatable(
                                            "pylon.pylonbase.guide.recipe.grindstone",
                                            PylonArgument.of("time", UnitFormat.SECONDS.format(cycles * CYCLE_TIME_TICKS / 20))
                                    ))
                    ))
                    .addIngredient('o', ItemButton.fromStack(output))
                    .build();
        }
    }
}
