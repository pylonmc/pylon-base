package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.base.recipes.GrindstoneRecipe;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3i;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static io.github.pylonmc.pylon.core.util.ItemUtils.isPylonSimilar;


public class Grindstone extends PylonBlock implements PylonSimpleMultiblock, PylonInteractableBlock {

    private static final Random random = new Random();

    public static final int CYCLE_DURATION_TICKS = Settings.get(BaseKeys.GRINDSTONE)
            .getOrThrow("cycle-duration-ticks", ConfigAdapter.INT);

    @Getter private boolean recipeInProgress;

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
        recipeInProgress = false;
    }

    @SuppressWarnings("unused")
    public Grindstone(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        recipeInProgress = false;
    }

    @Override
    public @NotNull Map<Vector3i, MultiblockComponent> getComponents() {
        return Map.of(new Vector3i(0, 1, 0), new PylonMultiblockComponent(BaseKeys.GRINDSTONE_HANDLE));
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) {
            return;
        }

        event.setCancelled(true);

        if (!isFormedAndFullyLoaded()) {
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

    public @Nullable GrindstoneRecipe getNextRecipe() {
        if (recipeInProgress) {
            return null;
        }

        ItemStack input = getItemDisplay().getEntity().getItemStack();
        if (input.getType().isAir()) {
            return null;
        }

        return GrindstoneRecipe.RECIPE_TYPE.getRecipes()
                .stream()
                .filter(recipe -> isPylonSimilar(recipe.input(), input) && input.getAmount() >= recipe.input().getAmount())
                .findFirst()
                .orElse(null);
    }

    public boolean tryStartRecipe(@NotNull GrindstoneRecipe nextRecipe, @Nullable Player player) {
        if (!new PrePylonCraftEvent<>(GrindstoneRecipe.RECIPE_TYPE, nextRecipe, this, player).callEvent()) {
            return false;
        }

        ItemStack input = getItemDisplay().getEntity().getItemStack();
        if (input.getType().isAir()) {
            return false;
        }

        getItemDisplay().getEntity().setItemStack(input.subtract(nextRecipe.input().getAmount()));

        recipeInProgress = true;

        for (int i = 0; i < nextRecipe.cycles(); i++) {
            for (int j = 0; j < 4; j++) {
                boolean isLast = i == nextRecipe.cycles() - 1 && j == 3;
                double translation = isLast ? 0.8 : 0.5;
                double rotation = (j / 4.0) * 2.0 * Math.PI;
                Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
                    getStoneDisplay().setTransform(
                            CYCLE_DURATION_TICKS / 4, getStoneDisplayMatrix(translation, rotation)
                    );
                    new ParticleBuilder(Particle.BLOCK)
                            .data(nextRecipe.particleBlockData())
                            .count(10)
                            .location(getBlock().getLocation().toCenterLocation())
                            .spawn();
                }, (long) ((i + j/4.0) * CYCLE_DURATION_TICKS));
            }
        }

        Bukkit.getScheduler().runTaskLater(
                PylonBase.getInstance(),
                () -> {
                    getBlock().getWorld().dropItemNaturally(
                            getBlock().getLocation().toCenterLocation().add(0, 0.25, 0),
                            nextRecipe.results().getRandom()
                    );

                    new PylonCraftEvent<>(GrindstoneRecipe.RECIPE_TYPE, nextRecipe, this).callEvent();

                    recipeInProgress = false;
                },
                nextRecipe.timeTicks()
        );

        return true;
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
}
