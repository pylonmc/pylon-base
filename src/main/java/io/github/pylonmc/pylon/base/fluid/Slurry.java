package io.github.pylonmc.pylon.base.fluid;

import io.github.pylonmc.pylon.base.PylonFluids;
import io.github.pylonmc.pylon.base.items.multiblocks.MixingPot;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.fluid.PylonFluidTag;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.registry.RegistryHandler;
import io.github.pylonmc.pylon.core.util.ItemUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Slurry extends PylonFluid implements RegistryHandler {

    @Getter
    private final ItemStack slurryMaterial;
    
    public Slurry(@NotNull NamespacedKey key, @NotNull Component name, @NotNull ItemStack slurryMaterial, @NotNull List<PylonFluidTag> tags) {
        super(key, name, Material.LIGHT_GRAY_CONCRETE, tags);
        this.slurryMaterial = slurryMaterial;
    }

    public Slurry(@NotNull NamespacedKey key, @NotNull ItemStack slurryMaterial, @NotNull PylonFluidTag @NotNull ... tags) {
        super(key, Material.LIGHT_GRAY_CONCRETE, tags);
        this.slurryMaterial = slurryMaterial;
    }

    @Override
    public void onRegister(@NotNull PylonRegistry<?> registry) {
        MixingPot.Recipe.RECIPE_TYPE.addRecipe(new MixingPot.Recipe(
                getKey(),
                Map.of(ItemUtils.asRecipeChoice(slurryMaterial), 1),
                this,
                false,
                PylonFluids.SLURRY,
                1000
        ));
    }
}
