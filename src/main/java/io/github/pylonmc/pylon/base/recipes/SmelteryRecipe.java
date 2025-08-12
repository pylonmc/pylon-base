package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.*;
import java.util.stream.Collectors;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public class SmelteryRecipe implements PylonRecipe {

    public static final RecipeType<SmelteryRecipe> RECIPE_TYPE = new RecipeType<>(baseKey("smeltery")) {
        @Override
        public void addRecipe(@NotNull SmelteryRecipe recipe) {
            super.addRecipe(recipe);
            Map<NamespacedKey, SmelteryRecipe> recipes = getRegisteredRecipes();
            Map<NamespacedKey, SmelteryRecipe> newMap = recipes.entrySet().stream()
                    .sorted(
                            Comparator.<Map.Entry<NamespacedKey, SmelteryRecipe>>comparingDouble(entry -> -entry.getValue().getTemperature())
                                    .thenComparingInt(entry -> -entry.getValue().getFluidInputs().size())
                    )
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (existing, replacement) -> existing,
                            () -> new LinkedHashMap<>(recipes.size())
                    ));
            recipes.clear();
            recipes.putAll(newMap);
        }
    };

    @Getter(onMethod_ = @Override) private final NamespacedKey key;
    @Getter private final Map<PylonFluid, Double> fluidInputs;
    @Getter private final Map<PylonFluid, Double> fluidOutputs;
    @Getter private final PylonFluid highestFluid;
    @Getter private final double temperature;

    public SmelteryRecipe(
            @NotNull NamespacedKey key,
            @NotNull Map<PylonFluid, Double> inputFluids,
            @NotNull Map<PylonFluid, Double> outputFluids,
            double temperature
    ) {
        this.key = key;
        this.temperature = temperature;

        var highestFluidEntry = inputFluids.entrySet().stream()
                .max(Comparator.comparingDouble(Map.Entry::getValue))
                .orElseThrow(() -> new IllegalArgumentException("Input fluids cannot be empty"));
        this.highestFluid = highestFluidEntry.getKey();
        double highestFluidAmount = highestFluidEntry.getValue();

        this.fluidInputs = new HashMap<>();
        for (var entry : inputFluids.entrySet()) {
            Preconditions.checkArgument(entry.getValue() > 0, "Input fluid amount must be positive");
            this.fluidInputs.put(entry.getKey(), entry.getValue() / highestFluidAmount);
        }

        this.fluidOutputs = new HashMap<>();
        for (var entry : outputFluids.entrySet()) {
            Preconditions.checkArgument(entry.getValue() > 0, "Output fluid amount must be positive");
            this.fluidOutputs.put(entry.getKey(), entry.getValue() / highestFluidAmount);
        }
    }

    @Override
    public @NotNull List<FluidOrItem> getInputs() {
        return fluidInputs.entrySet()
                .stream()
                .map(pair -> (FluidOrItem) FluidOrItem.of(pair.getKey(), pair.getValue()))
                .toList();
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return fluidOutputs.entrySet()
                .stream()
                .map(pair -> (FluidOrItem) FluidOrItem.of(pair.getKey(), pair.getValue()))
                .toList();
    }

    @Override
    public @NotNull Gui display() {
        Preconditions.checkState(fluidInputs.size() < 6);
        Preconditions.checkState(fluidOutputs.size() < 6);
        Gui gui = Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# . . # # # . . #",
                        "# . . # s # . . #",
                        "# . . # t # . . #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('s', ItemButton.fromStack(BaseItems.SMELTERY_CONTROLLER))
                .addIngredient('t', ItemStackBuilder.of(Material.COAL)
                        .name(Component.translatable(
                                "pylon.pylonbase.gui.smeltery.temperature",
                                PylonArgument.of("temperature", UnitFormat.CELSIUS.format(temperature))
                        )))
                .build();

        int i = 0;
        for (Map.Entry<PylonFluid, Double> entry : fluidInputs.entrySet()) {
            gui.setItem(10 + (i / 2) * 9 + (i % 2), new FluidButton(entry.getKey().getKey(), entry.getValue()));
            i++;
        }

        i = 0;
        for (Map.Entry<PylonFluid, Double> entry : fluidOutputs.entrySet()) {
            gui.setItem(15 + (i / 2) * 9 + (i % 2), new FluidButton(entry.getKey().getKey(), entry.getValue()));
            i++;
        }

        return gui;
    }
}