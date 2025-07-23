package io.github.pylonmc.pylon.base.recipes;

import com.google.common.base.Preconditions;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.machines.simple.Press;
import io.github.pylonmc.pylon.core.config.ConfigSection;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.guide.button.FluidButton;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;

/**
 * @param input input item (assumed to be of item amount 1)
 */
public record PressRecipe(
        NamespacedKey key,
        ItemStack input,
        double oilAmount
) implements PylonRecipe {

    public static final RecipeType<PressRecipe> RECIPE_TYPE = new RecipeType<>(
            new NamespacedKey(PylonBase.getInstance(), "press")
    );

    static {
        PylonRegistry.RECIPE_TYPES.register(RECIPE_TYPE);
        ConfigSection config = Settings.get(BaseKeys.PRESS).getSectionOrThrow("oil-amount");
        for (String key : config.getKeys()) {
            Material material = Material.getMaterial(key.toUpperCase());
            Preconditions.checkState(material != null, "No such material " + key);
            int amount = config.get(key, Integer.class);
            RECIPE_TYPE.addRecipe(new PressRecipe(material.getKey(), new ItemStack(material), amount));
        }
    }

    @Override
    public @NotNull NamespacedKey getKey() {
        return key;
    }

    @Override
    public @NotNull List<FluidOrItem> getInputs() {
        return List.of(FluidOrItem.of(input));
    }

    @Override
    public @NotNull List<FluidOrItem> getResults() {
        return List.of(FluidOrItem.of(BaseFluids.PLANT_OIL, oilAmount));
    }

    @Override
    public @NotNull Gui display() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # # # # # #",
                        "# p # # i c o # #",
                        "# # # # # # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('#', GuiItems.backgroundBlack())
                .addIngredient('p', ItemButton.fromStack(BaseItems.PRESS))
                .addIngredient('i', ItemButton.fromStack(input))
                .addIngredient('c', GuiItems.progressCyclingItem(Press.TIME_PER_ITEM_TICKS,
                        ItemStackBuilder.of(Material.CLOCK)
                                .name(net.kyori.adventure.text.Component.translatable(
                                        "pylon.pylonbase.guide.recipe.press",
                                        PylonArgument.of("time", UnitFormat.SECONDS.format(Press.TIME_PER_ITEM_TICKS / 20.0))
                                ))
                ))
                .addIngredient('o', new FluidButton(BaseFluids.PLANT_OIL.getKey(), oilAmount))
                .build();
    }
}