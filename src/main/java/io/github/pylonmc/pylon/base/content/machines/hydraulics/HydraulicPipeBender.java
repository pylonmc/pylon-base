package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.recipes.PipeBendingRecipe;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonRecipeProcessor;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.List;

public class HydraulicPipeBender extends PylonBlock
        implements PylonFluidBufferBlock, PylonInteractBlock, PylonTickingBlock, PylonRecipeProcessor<PipeBendingRecipe> {

    private static final Config settings = Settings.get(BaseKeys.HYDRAULIC_PIPE_BENDER);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", ConfigAdapter.INT);
    public static final int HYDRAULIC_FLUID_USAGE = settings.getOrThrow("hydraulic-fluid-usage", ConfigAdapter.INT);
    public static final double HYDRAULIC_FLUID_BUFFER = settings.getOrThrow("hydraulic-fluid-buffer", ConfigAdapter.INT);

    public ItemStack cubeStack = ItemStackBuilder.of(Material.ORANGE_CONCRETE)
            .addCustomModelDataString(getKey() + ":cube")
            .build();

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("hydraulic-fluid-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(HYDRAULIC_FLUID_USAGE))
            );
        }
    }

    @SuppressWarnings("unused")
    public HydraulicPipeBender(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(TICK_INTERVAL);
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);
        BlockFace facing;
        if (context instanceof BlockCreateContext.PlayerPlace playerPlaceContext) {
            facing = PylonUtils.rotateToPlayerFacing(playerPlaceContext.getPlayer(), BlockFace.NORTH, false);
        } else {
            facing = BlockFace.NORTH;
        }
        addEntity("cube1", new ItemDisplayBuilder()
                .itemStack(cubeStack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .translate(0, 0, 0.2)
                        .scale(0.25))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("cube2", new ItemDisplayBuilder()
                .itemStack(cubeStack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .translate(0.2, 0, -0.2)
                        .scale(0.25))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("cube3", new ItemDisplayBuilder()
                .itemStack(cubeStack)
                .transformation(new TransformBuilder()
                        .lookAlong(facing)
                        .translate(-0.2, 0, -0.2)
                        .scale(0.25))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("item", new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .lookAlong(new Vector3d(0.0, 1.0, 0.0))
                        .scale(0.4))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, false, true);
        setRecipeType(PipeBendingRecipe.RECIPE_TYPE);
    }

    @SuppressWarnings("unused")
    public HydraulicPipeBender(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
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

        ItemDisplay itemDisplay = getItemDisplay();
        ItemStack oldStack = itemDisplay.getItemStack();
        ItemStack newStack = event.getItem();

        // drop old item
        if (!oldStack.getType().isAir()) {
            getBlock().getWorld().dropItem(
                    getBlock().getLocation().toCenterLocation().add(0, 0.75, 0),
                    oldStack
            );
            itemDisplay.setItemStack(null);
            stopRecipe();
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
    public void onRecipeFinished(@NotNull PipeBendingRecipe recipe) {
        getItemDisplay().setItemStack(getItemDisplay().getItemStack().subtract(recipe.input().getAmount()));
        getBlock().getWorld().dropItemNaturally(
                getBlock().getLocation().toCenterLocation().add(0, 0.75, 0),
                recipe.result()
        );
    }

    @Override
    public void tick() {
        progressRecipe(TICK_INTERVAL);

        if (isProcessingRecipe()) {
            spawnParticles();
            return;
        }

        ItemStack stack = getItemDisplay().getItemStack();
        for (PipeBendingRecipe recipe : PipeBendingRecipe.RECIPE_TYPE) {
            double hydraulicFluidInput = HYDRAULIC_FLUID_USAGE * recipe.timeTicks() / 20.0;
            double dirtyHydraulicFluidOutput = HYDRAULIC_FLUID_USAGE * recipe.timeTicks() / 20.0;
            if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidInput
                    || fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < dirtyHydraulicFluidOutput
                    || !recipe.input().matches(stack)
            ) {
                continue;
            }

            startRecipe(recipe, recipe.timeTicks());
            spawnParticles();
            break;
        }
    }

    public ItemDisplay getItemDisplay() {
        return getHeldEntityOrThrow(ItemDisplay.class, "item");
    }

    public void spawnParticles() {
        new ParticleBuilder(Particle.BLOCK)
                .count(5)
                .location(getBlock().getLocation().toCenterLocation().add(0, 0.75, 0))
                .data(getCurrentRecipe().particleData())
                .spawn();
    }

    @Override
    public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
        drops.add(getItemDisplay().getItemStack());
    }
}
