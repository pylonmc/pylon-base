package io.github.pylonmc.pylon.base.content.machines.fluid;

import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBlock;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class FluidStrainer extends PylonBlock
        implements PylonFluidBlock, PylonEntityHolderBlock, PylonTickingBlock, PylonGuiBlock {

    public final double bufferSize = getSettings().getOrThrow("buffer-size", Double.class);

    private static final NamespacedKey CURRENT_RECIPE_KEY = baseKey("current_recipe");
    private static final NamespacedKey BUFFER_KEY = baseKey("buffer");
    private static final NamespacedKey PASSED_FLUID_KEY = baseKey("passed_fluid");

    private @Nullable Recipe currentRecipe;
    private double buffer;
    private double passedFluid;

    @SuppressWarnings("unused")
    public FluidStrainer(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        currentRecipe = null;
        buffer = 0;
        passedFluid = 0;
    }

    @SuppressWarnings({"unused", "DataFlowIssue"})
    public FluidStrainer(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);

        currentRecipe = pdc.get(CURRENT_RECIPE_KEY, Recipe.DATA_TYPE);
        buffer = pdc.get(BUFFER_KEY, PylonSerializers.DOUBLE);
        passedFluid = pdc.get(PASSED_FLUID_KEY, PylonSerializers.DOUBLE);
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PdcUtils.setNullable(pdc, CURRENT_RECIPE_KEY, Recipe.DATA_TYPE, currentRecipe);
        pdc.set(BUFFER_KEY, PylonSerializers.DOUBLE, buffer);
        pdc.set(PASSED_FLUID_KEY, PylonSerializers.DOUBLE, passedFluid);
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        return Map.of(
                "input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH),
                "output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH)
        );
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getSuppliedFluids(double deltaSeconds) {
        return currentRecipe == null ?
                Map.of() :
                Map.of(currentRecipe.outputFluid(), buffer);
    }

    @Override
    public @NotNull Map<PylonFluid, Double> getRequestedFluids(double deltaSeconds) {
        return Recipe.RECIPE_TYPE.getRecipes().stream()
                .map(Recipe::inputFluid)
                .collect(Collectors.toMap(Function.identity(), f -> bufferSize - buffer));
    }

    @Override
    public void addFluid(@NotNull PylonFluid fluid, double amount) {
        if (!fluid.equals(currentRecipe == null ? null : currentRecipe.inputFluid())) {
            passedFluid = 0;
            currentRecipe = null;
            for (Recipe recipe : Recipe.RECIPE_TYPE) {
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
    public void removeFluid(@NotNull PylonFluid fluid, double amount) {
        buffer -= amount;
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(
                getName(),
                Map.of("info", currentRecipe == null ?
                        Component.empty() :
                        Component.translatable("pylon.pylonbase.waila.fluid_strainer.straining",
                                PylonArgument.of("item", currentRecipe.outputItem().effectiveName()),
                                PylonArgument.of("progress", UnitFormat.PERCENT.format(passedFluid / 10)
                                        .decimalPlaces(0))
                        )
                )
        );
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
        if (currentRecipe != null && passedFluid >= currentRecipe.inputAmount()) {
            inventory.addItem(null, currentRecipe.outputItem().clone());
            passedFluid -= currentRecipe.inputAmount();
        }
        if (passedFluid < 1.0e-9) {
            currentRecipe = null;
            passedFluid = 0;
        }
    }

    public record Recipe(
            @NotNull NamespacedKey key,
            @NotNull PylonFluid inputFluid,
            double inputAmount,
            @NotNull PylonFluid outputFluid,
            @NotNull ItemStack outputItem
    ) implements PylonRecipe {

        public static final RecipeType<Recipe> RECIPE_TYPE = new RecipeType<>(
                baseKey("fluid_strainer")
        );

        static {
            RECIPE_TYPE.register();
        }

        private static final PersistentDataType<?, Recipe> DATA_TYPE =
                PylonSerializers.KEYED.keyedTypeFrom(Recipe.class, RECIPE_TYPE::getRecipeOrThrow);

        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }

        @Override
        public @NotNull List<@NotNull RecipeChoice> getInputItems() {
            return List.of();
        }

        @Override
        public @NotNull List<@NotNull PylonFluid> getInputFluids() {
            return List.of(inputFluid);
        }

        @Override
        public @NotNull List<@NotNull ItemStack> getOutputItems() {
            return List.of(outputItem);
        }

        @Override
        public @NotNull List<@NotNull PylonFluid> getOutputFluids() {
            return List.of(outputFluid);
        }

        @Override
        public @NotNull Gui display() {
            // TODO
            return null;
        }
    }
}
