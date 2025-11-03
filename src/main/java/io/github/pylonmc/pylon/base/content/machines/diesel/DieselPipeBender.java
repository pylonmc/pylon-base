package io.github.pylonmc.pylon.base.content.machines.diesel;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.recipes.PipeBendingRecipe;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.ProgressItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;

import java.util.List;


public class DieselPipeBender extends PylonBlock implements PylonGuiBlock, PylonFluidBufferBlock, PylonTickingBlock {

    public final double dieselBuffer = getSettings().getOrThrow("diesel-buffer", ConfigAdapter.DOUBLE);
    public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final double speed = getSettings().getOrThrow("speed", ConfigAdapter.DOUBLE);

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonGuiBlock.super.onBreak(drops, context);
        PylonFluidBufferBlock.super.onBreak(drops, context);
    }

    public static class Item extends PylonItem {

        public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);
        public final double speed = getSettings().getOrThrow("speed", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("diesel-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(dieselPerSecond)),
                    PylonArgument.of("speed", UnitFormat.PERCENT.format(speed * 100))
            );
        }
    }

    private int recipeTicksRemaining;
    private @Nullable PipeBendingRecipe recipe;
    private final VirtualInventory inputInventory = new VirtualInventory(1);
    private final VirtualInventory outputInventory = new VirtualInventory(1);
    private final ProgressItem progressItem = new ProgressItem(ItemStackBuilder.of(GuiItems.background()));

    @SuppressWarnings("unused")
    public DieselPipeBender(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(tickInterval);
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH);
        addEntity("chimney", new ItemDisplayBuilder()
                .itemStack(ItemStackBuilder.of(Material.CYAN_TERRACOTTA)
                        .addCustomModelDataString(getKey() + ":chimney")
                        .build()
                )
                .transformation(new TransformBuilder()
                        .translate(0.3, 0.0, 0.3)
                        .scale(0.15))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("side-1", new ItemDisplayBuilder()
                .itemStack(ItemStackBuilder.of(Material.BRICKS)
                        .addCustomModelDataString(getKey() + ":side")
                        .build()
                )
                .transformation(new TransformBuilder()
                        .translate(0, -0.5, 0)
                        .scale(1.1, 0.8, 0.8))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("side-2", new ItemDisplayBuilder()
                .itemStack(ItemStackBuilder.of(Material.BRICKS)
                        .addCustomModelDataString(getKey() + ":side")
                        .build()
                )
                .transformation(new TransformBuilder()
                        .translate(0, -0.5, 0)
                        .scale(0.9, 0.8, 1.1))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("item", new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .lookAlong(new Vector3d(0.0, 1.0, 0.0))
                        .scale(0.4))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        createFluidBuffer(BaseFluids.BIODIESEL, dieselBuffer, true, false);
        recipe = null;
        attachInventoryHandlers();
    }

    @SuppressWarnings("unused")
    public DieselPipeBender(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        recipe = null;
        attachInventoryHandlers();
    }

    @Override
    public void tick(double deltaSeconds) {
        ItemStack stack = inputInventory.getItem(0);

        if (recipe != null) {
            spawnParticles();
            progressItem.setRemainingTimeTicks(recipeTicksRemaining);

            // tick recipe
            if (recipeTicksRemaining > 0) {
                recipeTicksRemaining -= tickInterval;
                return;
            }

            // finish recipe
            outputInventory.addItem(null, recipe.result().clone());
            progressItem.setItemStackBuilder(ItemStackBuilder.of(GuiItems.background()));
            progressItem.setTotalTime(null);
            recipe = null;
            return;
        }

        if (stack == null) {
            return;
        }

        for (PipeBendingRecipe recipe : PipeBendingRecipe.RECIPE_TYPE) {
            // check we have enough diesel to finish the craft
            double dieselAmount = dieselPerSecond * recipe.timeTicks() / 20.0;
            if (fluidAmount(BaseFluids.BIODIESEL) < dieselAmount || !recipe.input().matches(stack)) {
                continue;
            }

            // check output has no item or the same item as the result
            ItemStack outputItem = outputInventory.getItem(0);
            if (outputItem != null && !PylonUtils.isPylonSimilar(outputItem, recipe.result())) {
                return;
            }

            // if output item same as result, check there's space for the output
            if (outputItem != null && outputItem.getAmount() + recipe.result().getAmount() > outputItem.getMaxStackSize()) {
                return;
            }

            // start recipe
            this.recipe = recipe;
            recipeTicksRemaining = (int) Math.round(recipe.timeTicks() * speed);
            stack.subtract(recipe.input().getAmount());
            inputInventory.setItem(null, 0, stack);

            progressItem.setItemStackBuilder(
                    ItemStackBuilder.of(recipe.result().clone()).clearLore()
            );
            progressItem.setTotalTimeTicks(recipeTicksRemaining);
            progressItem.setRemainingTimeTicks(recipeTicksRemaining);

            spawnParticles();
            removeFluid(BaseFluids.BIODIESEL, dieselAmount);

            break;
        }
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # I # # # O # #",
                        "# # i # p # o # #",
                        "# # I # # # O # #"
                )
                .addIngredient('#', GuiItems.background())
                .addIngredient('I', GuiItems.input())
                .addIngredient('i', inputInventory)
                .addIngredient('p', progressItem)
                .addIngredient('O', GuiItems.output())
                .addIngredient('o', outputInventory)
                .build();
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("diesel-bar", BaseUtils.createFluidAmountBar(
                        fluidAmount(BaseFluids.BIODIESEL),
                        fluidCapacity(BaseFluids.BIODIESEL),
                        20,
                        TextColor.fromHexString("#eaa627")
                ))
        ));
    }

    private void attachInventoryHandlers() {
        inputInventory.setPostUpdateHandler(event -> {
            getHeldEntityOrThrow(ItemDisplay.class, "item").setItemStack(event.getNewItem());
        });
        outputInventory.setPreUpdateHandler(event -> {
            if (!event.isRemove() && event.getUpdateReason() instanceof PlayerUpdateReason) {
                event.setCancelled(true);
            }
        });
    }

    public void spawnParticles() {
        new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
                .location(getBlock().getLocation().toCenterLocation().add(0.3, 0.7, 0.3))
                .offset(0, 1, 0)
                .count(0)
                .extra(0.05)
                .spawn();
    }
}
