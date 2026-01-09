package io.github.pylonmc.pylon.base.content.machines.diesel;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.recipes.TableSawRecipe;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.BlockDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.logistics.LogisticSlotType;
import io.github.pylonmc.pylon.core.util.MachineUpdateReason;
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
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.List;
import java.util.Map;


public class DieselTableSaw extends PylonBlock implements
        PylonGuiBlock,
        PylonFluidBufferBlock,
        PylonDirectionalBlock,
        PylonTickingBlock,
        PylonLogisticBlock,
        PylonRecipeProcessor<TableSawRecipe> {

    public final double dieselBuffer = getSettings().getOrThrow("diesel-buffer", ConfigAdapter.DOUBLE);
    public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);
    public final double speed = getSettings().getOrThrow("speed", ConfigAdapter.DOUBLE);

    public ItemStackBuilder sideStack1 = ItemStackBuilder.of(Material.BRICKS)
            .addCustomModelDataString(getKey() + ":side1");
    public ItemStackBuilder sideStack2 = ItemStackBuilder.of(Material.BRICKS)
            .addCustomModelDataString(getKey() + ":side2");
    public ItemStackBuilder chimneyStack = ItemStackBuilder.of(Material.CYAN_TERRACOTTA)
            .addCustomModelDataString(getKey() + ":chimney");

    private final VirtualInventory inputInventory = new VirtualInventory(1);
    private final VirtualInventory outputInventory = new VirtualInventory(1);

    public static class Item extends PylonItem {

        public final double dieselPerSecond = getSettings().getOrThrow("diesel-per-second", ConfigAdapter.DOUBLE);
        public final double dieselBuffer = getSettings().getOrThrow("diesel-buffer", ConfigAdapter.DOUBLE);
        public final double speed = getSettings().getOrThrow("speed", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("diesel-usage", UnitFormat.MILLIBUCKETS_PER_SECOND.format(dieselPerSecond)),
                    PylonArgument.of("diesel-buffer", UnitFormat.MILLIBUCKETS.format(dieselBuffer)),
                    PylonArgument.of("speed", UnitFormat.PERCENT.format(speed * 100))
            );
        }
    }

    @SuppressWarnings("unused")
    public DieselTableSaw(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(tickInterval);
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false, 0.55F);
        setFacing(context.getFacing());
        addEntity("chimney", new ItemDisplayBuilder()
                .itemStack(chimneyStack)
                .transformation(new TransformBuilder()
                        .lookAlong(getFacing())
                        .translate(0.4, 0.0, -0.4)
                        .scale(0.15))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("side1", new ItemDisplayBuilder()
                .itemStack(sideStack1)
                .transformation(new TransformBuilder()
                        .translate(0, -0.5, 0)
                        .scale(1.1, 0.8, 0.8))
                .build(block.getLocation().toCenterLocation().add(0, 0.5, 0))
        );
        addEntity("side2", new ItemDisplayBuilder()
                .itemStack(sideStack2)
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
                        .rotate(0, PylonUtils.faceToYaw(getFacing()), 0)
                        .scale(0.6, 0.4, 0.4))
                .build(block.getLocation().toCenterLocation().add(0, 0.7, 0))
        );
        createFluidBuffer(BaseFluids.BIODIESEL, dieselBuffer, true, false);
        setRecipeType(TableSawRecipe.RECIPE_TYPE);
        setRecipeProgressItem(new ProgressItem(GuiItems.background()));
    }

    @SuppressWarnings("unused")
    public DieselTableSaw(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("input", LogisticSlotType.INPUT, inputInventory);
        createLogisticGroup("output", LogisticSlotType.OUTPUT, outputInventory);
        outputInventory.setPreUpdateHandler(PylonUtils.DISALLOW_PLAYERS_FROM_ADDING_ITEMS_HANDLER);
        outputInventory.setPostUpdateHandler(event -> tryStartRecipe());
        inputInventory.setPostUpdateHandler(event -> {
            if (!(event.getUpdateReason() instanceof MachineUpdateReason)) {
                tryStartRecipe();
            }
        });
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonGuiBlock.super.onBreak(drops, context);
        PylonFluidBufferBlock.super.onBreak(drops, context);
    }

    @Override
    public void tick() {
        if (!isProcessingRecipe() || fluidAmount(BaseFluids.BIODIESEL) < dieselPerSecond * tickInterval / 20) {
            return;
        }

        removeFluid(BaseFluids.BIODIESEL, dieselPerSecond * tickInterval / 20);
        Vector smokePosition = Vector.fromJOML(PylonUtils.rotateVectorToFace(
                new Vector3d(0.4, 0.7, -0.4),
                getFacing().getOppositeFace()
        ));
        new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
                .location(getBlock().getLocation().toCenterLocation().add(smokePosition))
                .offset(0, 1, 0)
                .count(0)
                .extra(0.05)
                .spawn();
        new ParticleBuilder(Particle.BLOCK)
                .count(5)
                .location(getBlock().getLocation().toCenterLocation().add(0, 0.75, 0))
                .data(getCurrentRecipe().particleData())
                .spawn();
        progressRecipe(tickInterval);
    }

    public void tryStartRecipe() {
        if (isProcessingRecipe()) {
            return;
        }

        ItemStack stack = inputInventory.getItem(0);
        if (stack == null) {
            return;
        }

        for (TableSawRecipe recipe : TableSawRecipe.RECIPE_TYPE) {
            double dieselAmount = dieselPerSecond * recipe.timeTicks() / 20.0;
            if (fluidAmount(BaseFluids.BIODIESEL) < dieselAmount
                    || !recipe.input().isSimilar(stack)
                    || !outputInventory.canHold(recipe.result())
            ) {
                continue;
            }

            startRecipe(recipe, recipe.timeTicks());
            getRecipeProgressItem().setItemStackBuilder(ItemStackBuilder.of(stack.asOne()).clearLore());
            getHeldEntityOrThrow(ItemDisplay.class, "item").setItemStack(stack);
            inputInventory.setItem(new MachineUpdateReason(), 0, stack.subtract(recipe.input().getAmount()));
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
                .addIngredient('p', getRecipeProgressItem())
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
        getRecipeProgressItem().setItemStackBuilder(ItemStackBuilder.of(GuiItems.background()));
        getHeldEntityOrThrow(ItemDisplay.class, "item").setItemStack(null);
        outputInventory.addItem(null, recipe.result().clone());
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Inventory> createInventoryMapping() {
        return Map.of(
                "input", inputInventory,
                "output", outputInventory
        );
    }
}
