package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.recipes.MixingPotRecipe;
import io.github.pylonmc.pylon.base.recipes.StrainingRecipe;
import io.github.pylonmc.rebar.fluid.PylonFluid;
import io.github.pylonmc.rebar.fluid.PylonFluidTag;
import io.github.pylonmc.rebar.recipe.FluidOrItem;
import io.github.pylonmc.rebar.recipe.RecipeInput;
import io.github.pylonmc.rebar.registry.PylonRegistry;
import io.github.pylonmc.rebar.registry.RegistryHandler;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Slurry extends PylonFluid implements RegistryHandler {

    @Getter
    private final ItemStack slurryStack;
    
    public Slurry(@NotNull NamespacedKey key, @NotNull Component name, @NotNull ItemStack slurryStack, @NotNull List<PylonFluidTag> tags) {
        super(key, name, Material.LIGHT_GRAY_CONCRETE, tags);
        this.slurryStack = slurryStack;
    }

    public Slurry(@NotNull NamespacedKey key, @NotNull ItemStack slurryStack, @NotNull PylonFluidTag @NotNull ... tags) {
        super(key, Material.LIGHT_GRAY_CONCRETE, tags);
        this.slurryStack = slurryStack;
    }

    @Override
    public void onRegister(@NotNull PylonRegistry<?> registry) {
        MixingPotRecipe.RECIPE_TYPE.addRecipe(new MixingPotRecipe(
                getKey(),
                List.of(RecipeInput.of(slurryStack)),
                RecipeInput.of(BaseFluids.SLURRY, 1000),
                FluidOrItem.of(this, 1000),
                false
        ));
        StrainingRecipe.RECIPE_TYPE.addRecipe(new StrainingRecipe(
                getKey(),
                RecipeInput.of(this, 1000),
                BaseFluids.SLURRY,
                slurryStack
        ));
    }
}
