package io.github.pylonmc.pylon.base.content.machines.diesel;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.recipes.TableSawRecipe;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonLogisticBlock;
import io.github.pylonmc.pylon.core.block.base.PylonRecipeProcessor;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.inventory.event.PlayerUpdateReason;

import java.util.List;


public class DieselTableSaw extends PylonBlock
        implements PylonGuiBlock, PylonFluidBufferBlock, PylonTickingBlock, PylonLogisticBlock, PylonRecipeProcessor<TableSawRecipe> {

    public final double dieselBuffer = getSettings().getOrThrow("diesel-buffer", ConfigAdapter.DOUBLE);
    public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final double speed = getSettings().getOrThrow("speed", ConfigAdapter.DOUBLE);

    public ItemStack sideStack = ItemStackBuilder.of(Material.BRICKS)
            .addCustomModelDataString(getKey() + ":side")
            .build();

    private final VirtualInventory inputInventory = new VirtualInventory(1);
    private final VirtualInventory outputInventory = new VirtualInventory(1);
    private final ProgressItem progressItem = new ProgressItem(ItemStackBuilder.of(GuiItems.background()));

    public static class Item extends PylonItem {

        public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);
        public final double speed = getSettings().getOrThrow("speed", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("diesel-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(dieselPerSecond)),
                    PylonArgument.of("speed", UnitFormat.PERCENT.format(speed * 100))
            );
        }
    }

    @SuppressWarnings("unused")
    public DieselTableSaw(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(tickInterval);
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false, 0.55F);
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
        addEntity("side1", new ItemDisplayBuilder()
                .itemStack(sideStack)
                .transformation(new TransformBuilder()
                        .translate(0, -0.5, 0)
                        .scale(1.1, 0.8, 0.8))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("side2", new ItemDisplayBuilder()
                .itemStack(sideStack)
                .transformation(new TransformBuilder()
                        .translate(0, -0.5, 0)
                        .scale(0.9, 0.8, 1.1))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("item", new ItemDisplayBuilder()
                .transformation(new TransformBuilder()
                        .scale(0.3))
                .build(block.getLocation().toCenterLocation().add(0, 0.65, 0))
        );
        addEntity("saw", new BlockDisplayBuilder()
                .blockData(Material.IRON_BARS.createBlockData("[east=true,west=true]"))
                .transformation(new TransformBuilder()
                        .scale(0.6, 0.4, 0.4))
                .build(block.getLocation().toCenterLocation().add(0, 0.7, 0))
        );
        createFluidBuffer(BaseFluids.BIODIESEL, dieselBuffer, true, false);
        setRecipeType(TableSawRecipe.RECIPE_TYPE);
    }

    @SuppressWarnings("unused")
    public DieselTableSaw(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("input", LogisticSlotType.INPUT, inputInventory);
        createLogisticGroup("output", LogisticSlotType.OUTPUT, outputInventory);
        setProgressItem(progressItem);
        inputInventory.setPostUpdateHandler(event -> {
            // Null check here because I've seen exceptions when the block is loading in but the entity hasn't been
            // loaded yet (no idea why this handler would fire at that point but oh well)
            ItemDisplay display = getHeldEntity(ItemDisplay.class, "item");
            if (display != null) {
                display.setItemStack(event.getNewItem());
            }
        });
        outputInventory.setPreUpdateHandler(event -> {
            if (!event.isRemove() && event.getUpdateReason() instanceof PlayerUpdateReason) {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonGuiBlock.super.onBreak(drops, context);
        PylonFluidBufferBlock.super.onBreak(drops, context);
    }

    @Override
    public void tick(double deltaSeconds) {
        progressRecipe(tickInterval);

        if (isProcessingRecipe()) {
            spawnParticles();
            return;
        }

        ItemStack stack = inputInventory.getItem(0);
        if (stack == null) {
            return;
        }

        for (TableSawRecipe recipe : TableSawRecipe.RECIPE_TYPE) {
            // check we have enough diesel to finish the craft and we have the correct input
            double dieselAmount = dieselPerSecond * recipe.timeTicks() / 20.0;
            if (fluidAmount(BaseFluids.BIODIESEL) < dieselAmount || !recipe.input().isSimilar(stack)) {
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
            startRecipe(recipe, recipe.timeTicks());
            stack.subtract(recipe.input().getAmount());
            inputInventory.setItem(null, 0, stack);
            progressItem.setItemStackBuilder(ItemStackBuilder.of(recipe.result().clone()).clearLore());
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

    @Override
    public void onRecipeFinished(@NotNull TableSawRecipe recipe) {
        outputInventory.addItem(null, recipe.result().clone());
        progressItem.setItemStackBuilder(ItemStackBuilder.of(GuiItems.background()));
    }

    public void spawnParticles() {
        new ParticleBuilder(Particle.BLOCK)
                .count(5)
                .location(getBlock().getLocation().toCenterLocation().add(0, 0.75, 0))
                .data(getCurrentRecipe().particleData())
                .spawn();
    }
}
