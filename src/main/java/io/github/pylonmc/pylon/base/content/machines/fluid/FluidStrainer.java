package io.github.pylonmc.pylon.base.content.machines.fluid;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.recipes.StrainingRecipe;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonRecipeProcessor;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.MachineUpdateReason;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.Inventory;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class FluidStrainer extends PylonBlock implements
        PylonDirectionalBlock,
        PylonFluidBlock,
        PylonGuiBlock,
        PylonRecipeProcessor<StrainingRecipe> {

    private static final NamespacedKey FLUID_AMOUNT_KEY = baseKey("fluid_amount");
    private static final NamespacedKey FLUID_TYPE_KEY = baseKey("fluid_type");

    public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);
    public @Nullable PylonFluid fluidType;
    public double fluidAmount;
    private final VirtualInventory inventory = new VirtualInventory(9 * 3);

    public static class Item extends PylonItem {

        public final double buffer = getSettings().getOrThrow("buffer", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("buffer", UnitFormat.MILLIBUCKETS.format(buffer))
            );
        }
    }

    @SuppressWarnings("unused")
    public FluidStrainer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setFacing(context.getFacing());
        createFluidPoint(FluidPointType.INPUT, BlockFace.UP);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.DOWN);
        setRecipeType(StrainingRecipe.RECIPE_TYPE);

        fluidType = null;
        fluidAmount = 0.0;
    }

    @SuppressWarnings("unused")
    public FluidStrainer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        fluidType = null;
        fluidAmount = pdc.get(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, FLUID_TYPE_KEY, PylonSerializers.PYLON_FLUID, fluidType);
        pdc.set(FLUID_AMOUNT_KEY, PylonSerializers.DOUBLE, fluidAmount);
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid) {
        if (getCurrentRecipe() == null) {
            if (StrainingRecipe.getRecipeForFluid(fluid) == null) {
                return 0.0;
            } else {
                return 1000.0 - fluidAmount;
            }
        }
        if (getCurrentRecipe().input().contains(fluid)) {
            return getCurrentRecipe().input().amountMillibuckets() - fluidAmount;
        }
        return 0;
    }

    @Override
    public void onFluidAdded(@NotNull PylonFluid fluid, double amount) {
        if (!isProcessingRecipe()) {
            StrainingRecipe recipe = StrainingRecipe.getRecipeForFluid(fluid);
            Preconditions.checkState(recipe != null);
            startRecipe(recipe, (int) Math.round(recipe.input().amountMillibuckets()));
            fluidType = recipe.outputFluid();
        }
        if (isProcessingRecipe()) {
            progressRecipe((int) Math.round(amount));
        }
        fluidAmount += amount;
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids() {
        return fluidType != null
                ? Map.of(fluidType, fluidAmount)
                : Map.of();
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {
        fluidAmount -= amount;
        if (fluidAmount < 1.0e-6) {
            fluidType = null;
        }
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("info", getCurrentRecipe() == null
                        ? Component.empty()
                        : Component.translatable("pylon.pylonbase.waila.fluid_strainer.straining",
                                PylonArgument.of("item", getCurrentRecipe().outputItem().effectiveName()),
                                PylonArgument.of("bars", BaseUtils.createProgressBar(
                                        getCurrentRecipe().input().amountMillibuckets() - getRecipeTicksRemaining(),
                                        getCurrentRecipe().input().amountMillibuckets(),
                                        20,
                                        TextColor.color(100, 255, 100)
                                )),
                                PylonArgument.of("progress", 100.0 * getRecipeTicksRemaining() / getCurrentRecipe().input().amountMillibuckets())
                        )
                )
        ));
    }

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure("# # . . . . . # #")
                .addIngredient('.', inventory)
                .addIngredient('#', GuiItems.background())
                .build();
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull Inventory> createInventoryMapping() {
        return Map.of();
    }

    @Override
    public void onRecipeFinished(@NotNull StrainingRecipe recipe) {
        inventory.addItem(new MachineUpdateReason(), recipe.outputItem());
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonFluidBlock.super.onBreak(drops, context);
        PylonGuiBlock.super.onBreak(drops, context);
    }
}