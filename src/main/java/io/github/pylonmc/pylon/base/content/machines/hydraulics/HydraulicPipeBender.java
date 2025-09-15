package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.base.recipes.PipeBendingRecipe;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
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
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;

public class HydraulicPipeBender extends PylonBlock
        implements PylonEntityHolderBlock, PylonFluidBufferBlock, PylonInteractableBlock, PylonTickingBlock {

    private static final Config settings = Settings.get(BaseKeys.HYDRAULIC_PIPE_BENDER);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", ConfigAdapter.INT);
    public static final int HYDRAULIC_FLUID_INPUT_MB_PER_SECOND = settings.getOrThrow("hydraulic-fluid-input-mb-per-second", ConfigAdapter.INT);
    public static final int DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND = settings.getOrThrow("dirty-hydraulic-fluid-output-mb-per-second", ConfigAdapter.INT);
    public static final double HYDRAULIC_FLUID_BUFFER = settings.getOrThrow("hydraulic-fluid-buffer", ConfigAdapter.INT);
    public static final double DIRTY_HYDRAULIC_FLUID_BUFFER = settings.getOrThrow("dirty-hydraulic-fluid-buffer", ConfigAdapter.INT);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("hydraulic_fluid_input", UnitFormat.MILLIBUCKETS_PER_SECOND.format(HYDRAULIC_FLUID_INPUT_MB_PER_SECOND)),
                    PylonArgument.of("dirty_hydraulic_fluid_output", UnitFormat.MILLIBUCKETS_PER_SECOND.format(DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND))
            );
        }
    }

    private int recipeTicksRemaining;
    private @Nullable PipeBendingRecipe recipe;

    @SuppressWarnings("unused")
    public HydraulicPipeBender(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(TICK_INTERVAL);
        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));
        addEntity("item", new SimpleItemDisplay(new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .lookAlong(new Vector3d(0.0, 1.0, 0.0))
                        .scale(0.4))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        ));
        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_BUFFER, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, DIRTY_HYDRAULIC_FLUID_BUFFER, false, true);
        recipe = null;
    }

    @SuppressWarnings("unused")
    public HydraulicPipeBender(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        recipe = null;
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

        recipe = null;

        ItemDisplay itemDisplay = getItemDisplay().getEntity();
        ItemStack oldStack = itemDisplay.getItemStack();
        ItemStack newStack = event.getItem();

        // drop old item
        if (!oldStack.getType().isAir()) {
            getBlock().getWorld().dropItem(
                    getBlock().getLocation().toCenterLocation().add(0, 0.75, 0),
                    oldStack
            );
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
    public void tick(double deltaSeconds) {
        ItemStack stack = getItemDisplay().getEntity().getItemStack();

        if (recipe != null) {
            spawnParticles();

            if (recipeTicksRemaining > 0) {
                recipeTicksRemaining -= TICK_INTERVAL;
                return;
            }

            getItemDisplay().getEntity().setItemStack(stack.subtract(recipe.input().getAmount()));
            getBlock().getWorld().dropItemNaturally(
                    getBlock().getLocation().toCenterLocation().add(0, 0.75, 0),
                    recipe.result()
            );
            recipe = null;
            return;
        }

        for (PipeBendingRecipe recipe : PipeBendingRecipe.RECIPE_TYPE) {
            double hydraulicFluidInput = HYDRAULIC_FLUID_INPUT_MB_PER_SECOND * recipe.timeTicks() / 20.0;
            double dirtyHydraulicFluidOutput = DIRTY_HYDRAULIC_FLUID_OUTPUT_MB_PER_SECOND * recipe.timeTicks() / 20.0;
            if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < hydraulicFluidInput
                    || fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < dirtyHydraulicFluidOutput
                    || !recipe.input().matches(stack)
            ) {
                continue;
            }

            this.recipe = recipe;
            recipeTicksRemaining = recipe.timeTicks();
            spawnParticles();

            break;
        }
    }

    public SimpleItemDisplay getItemDisplay() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "item");
    }

    public void spawnParticles() {
        new ParticleBuilder(Particle.BLOCK)
                .count(5)
                .location(getBlock().getLocation().toCenterLocation().add(0, 0.75, 0))
                .data(recipe.particleData())
                .spawn();
    }

    @Override
    public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
        drops.add(getItemDisplay().getEntity().getItemStack());
    }
}
