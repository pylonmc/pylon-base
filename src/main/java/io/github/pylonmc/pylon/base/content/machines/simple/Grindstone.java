package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
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
import org.bukkit.block.data.Ageable;
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
import org.joml.Matrix4f;
import org.joml.Vector3i;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;
import static io.github.pylonmc.pylon.core.util.ItemUtils.isPylonSimilar;


public class Grindstone extends PylonBlock implements PylonSimpleMultiblock, PylonInteractableBlock, PylonTickingBlock {

    private static final NamespacedKey RECIPE_KEY = baseKey("recipe");
    private static final NamespacedKey CYCLES_REMAINING_KEY = baseKey("cycles_remaining");
    private static final NamespacedKey CYCLE_TICKS_REMAINING_KEY = baseKey("cycle_ticks_remaining");

    public static final int TICK_RATE = Settings.get(BaseKeys.GRINDSTONE).getOrThrow("tick-rate", Integer.class);
    public static final int CYCLE_TIME_TICKS = Settings.get(BaseKeys.GRINDSTONE).getOrThrow("cycle-time-ticks", Integer.class);

    @Getter private @Nullable NamespacedKey recipe;
    private @Nullable Integer cyclesRemaining;
    private @Nullable Integer cycleTicksRemaining;

    @SuppressWarnings("unused")
    public Grindstone(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        addEntity("item", new SimpleItemDisplay(new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .scale(0.3)
                        .translate(0, 0.15, 0)
                        .rotate(Math.PI / 2, 0, 0))
                .build(getBlock().getLocation().toCenterLocation())
        ));
        addEntity("block", new SimpleItemDisplay(new ItemDisplayBuilder()
                .material(Material.SMOOTH_STONE_SLAB)
                .transformation(new TransformBuilder()
                        .translate(0, 0.8, 0))
                .build(getBlock().getLocation().toCenterLocation())
        ));

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
    public @NotNull Map<Vector3i, MultiblockComponent> getComponents() {
        return Map.of(new Vector3i(0, 1, 0), new PylonMultiblockComponent(BaseKeys.GRINDSTONE_HANDLE));
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

        ItemDisplay itemDisplay = getItemDisplay().getEntity();
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
        drops.add(getItemDisplay().getEntity().getItemStack());
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
                getStoneDisplay().setTransform(TICK_RATE, getStoneDisplayMatrix(0.5, 0.0));

                cyclesRemaining = null;
                cycleTicksRemaining = null;

                // run after interpolation to starting rotation is finished
                Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), this::finishRecipe, TICK_RATE);

                return;
            }
        }

        if (cycleTicksRemaining == CYCLE_TIME_TICKS) {
            // put top stone down
            getStoneDisplay().setTransform(TICK_RATE, getStoneDisplayMatrix(0.5, 0.0));
        } else {
            // rotate stone
            getStoneDisplay().setTransform(TICK_RATE, getStoneDisplayMatrix(
                    0.5,
                    2 * Math.PI * ((double) cycleTicksRemaining / CYCLE_TIME_TICKS))
            );

            Recipe recipe = Recipe.RECIPE_TYPE.getRecipe(this.recipe);
            assert recipe != null;
            new ParticleBuilder(Particle.BLOCK)
                    .data(recipe.particleBlockData)
                    .count(10)
                    .location(getBlock().getLocation().toCenterLocation())
                    .spawn();
        }

        cycleTicksRemaining -= TICK_RATE;
    }

    public @Nullable Recipe getNextRecipe() {
        if (recipe != null || cyclesRemaining != null || cycleTicksRemaining != null) {
            return null;
        }

        ItemStack input = getItemDisplay().getEntity().getItemStack();
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

        ItemStack input = getItemDisplay().getEntity().getItemStack();
        if (input.getType().isAir()) {
            return false;
        }

        getItemDisplay().getEntity().setItemStack(input.subtract(nextRecipe.input.getAmount()));
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

        getStoneDisplay().setTransform(TICK_RATE, getStoneDisplayMatrix(0.8, 0.0));

        this.recipe = null;
    }

    public SimpleItemDisplay getItemDisplay() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "item");
    }

    public SimpleItemDisplay getStoneDisplay() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "block");
    }

    public static @NotNull Matrix4f getStoneDisplayMatrix(double translation, double rotation) {
        return new TransformBuilder()
                .translate(0, translation, 0)
                .rotate(0, rotation, 0)
                .buildForItemDisplay();
    }

    static {
        Grindstone.Recipe.RECIPE_TYPE.addRecipe(new Grindstone.Recipe(
                baseKey("string_from_bamboo"),
                new ItemStack(Material.BAMBOO, 4),
                new ItemStack(Material.STRING),
                3,
                Material.BAMBOO.createBlockData(data -> {
                    Ageable ageable = (Ageable) data;
                    ageable.setAge(ageable.getMaximumAge());
                })
        ));
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
                    .addIngredient('g', ItemButton.fromStack(BaseItems.GRINDSTONE))
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
