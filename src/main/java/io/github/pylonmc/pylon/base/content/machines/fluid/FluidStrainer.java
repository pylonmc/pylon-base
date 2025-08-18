package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.base.recipes.StrainingRecipe;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class FluidStrainer extends PylonBlock
        implements PylonFluidBlock, PylonTickingBlock, PylonGuiBlock, PylonEntityHolderBlock {

    public final double bufferSize = getSettings().getOrThrow("buffer-size", ConfigAdapter.DOUBLE);

    private static final NamespacedKey CURRENT_RECIPE_KEY = baseKey("current_recipe");
    private static final NamespacedKey BUFFER_KEY = baseKey("buffer");
    private static final NamespacedKey PASSED_FLUID_KEY = baseKey("passed_fluid");

    private final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    private @Nullable StrainingRecipe currentRecipe;
    private double buffer;
    private double passedFluid;

    @SuppressWarnings("unused")
    public FluidStrainer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setTickInterval(tickInterval);

        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.UP));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.DOWN));

        currentRecipe = null;
        buffer = 0;
        passedFluid = 0;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidStrainer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        currentRecipe = pdc.get(CURRENT_RECIPE_KEY, StrainingRecipe.DATA_TYPE);
        buffer = pdc.get(BUFFER_KEY, PylonSerializers.DOUBLE);
        passedFluid = pdc.get(PASSED_FLUID_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PdcUtils.setNullable(pdc, CURRENT_RECIPE_KEY, StrainingRecipe.DATA_TYPE, currentRecipe);
        pdc.set(BUFFER_KEY, PylonSerializers.DOUBLE, buffer);
        pdc.set(PASSED_FLUID_KEY, PylonSerializers.DOUBLE, passedFluid);
    }

    @Override
    public @NotNull Map<@NotNull PylonFluid, @NotNull Double> getSuppliedFluids(double deltaSeconds) {
        return currentRecipe == null ?
                Map.of() :
                Map.of(currentRecipe.outputFluid(), buffer);
    }

    @Override
    public double fluidAmountRequested(@NotNull PylonFluid fluid, double deltaSeconds) {
        if (StrainingRecipe.RECIPE_TYPE.getRecipes().stream().anyMatch(recipe -> fluid.equals(recipe.inputFluid()))) {
            return bufferSize - buffer;
        } else {
            return 0.0;
        }
    }

    @Override
    public void onFluidAdded(@NotNull PylonFluid fluid, double amount) {
        if (!fluid.equals(currentRecipe == null ? null : currentRecipe.inputFluid())) {
            passedFluid = 0;
            currentRecipe = null;
            for (StrainingRecipe recipe : StrainingRecipe.RECIPE_TYPE) {
                if (recipe.inputFluid().equals(fluid)) {
                    currentRecipe = recipe;
                    break;
                }
            }
            if (currentRecipe == null) {
                throw new IllegalStateException("No recipe found for fluid: " + fluid);
            }
        }
        buffer += amount;
        passedFluid += amount;
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {
        buffer -= amount;
    }

    @Override
    public @Nullable WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(getDefaultTranslationKey().arguments(
                PylonArgument.of("info", currentRecipe == null ?
                        Component.empty() :
                        Component.translatable("pylon.pylonbase.waila.fluid_strainer.straining",
                                PylonArgument.of("item", currentRecipe.outputItem().effectiveName()),
                                PylonArgument.of("progress", UnitFormat.PERCENT.format(passedFluid / 10)
                                        .decimalPlaces(0))
                        )
                )
        ));
    }

    private final VirtualInventory inventory = new VirtualInventory(9 * 3);

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        ". . . . . . . . ."
                )
                .addIngredient('.', inventory)
                .build();
    }

    @Override
    public void tick(double deltaSeconds) {
        if (currentRecipe != null && passedFluid >= currentRecipe.fluidAmount()) {
            inventory.addItem(null, currentRecipe.outputItem().clone());
            passedFluid -= currentRecipe.fluidAmount();
        }
        if (passedFluid < 1e-9) {
            currentRecipe = null;
            passedFluid = 0;
        }
    }
}