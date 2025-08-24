package io.github.pylonmc.pylon.base.content.machines.smelting;

import io.github.pylonmc.pylon.base.recipes.CastingRecipe;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import kotlin.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

public final class SmelteryCaster extends SmelteryComponent implements PylonGuiBlock, PylonTickingBlock {

    private @Nullable PylonFluid bottomFluid = null;

    @SuppressWarnings("unused")
    public SmelteryCaster(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(SmelteryController.TICK_INTERVAL);
    }

    @SuppressWarnings("unused")
    public SmelteryCaster(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    private final VirtualInventory inventory = new VirtualInventory(1);
    private final CastItem castItem = new CastItem();

    @Override
    public @NotNull Gui createGui() {
        return Gui.normal()
                .setStructure(
                        "# # # # # # # # #",
                        "# # # # c # # # #",
                        "# # # # x # # # #",
                        "# # # # # # # # #"
                )
                .addIngredient('c', castItem)
                .addIngredient('x', inventory)
                .addIngredient('#', GuiItems.background())
                .build();
    }

    private class CastItem extends AbstractItem {

        @Override
        public ItemProvider getItemProvider() {
            SmelteryController controller = getController();
            CastingRecipe recipe = (bottomFluid == null) ? null : CastingRecipe.getCastRecipeFor(bottomFluid);
            if (controller == null || bottomFluid == null || recipe == null) {
                return ItemStackBuilder.of(Material.BARRIER)
                        .name(casterKey("cannot_cast"));
            }
            ItemStack result = recipe.result();
            Component name = result.effectiveName();
            double temperature = recipe.temperature();
            if (controller.getTemperature() < temperature) {
                return ItemStackBuilder.of(result.getType())
                        .name(casterKey("cannot_cast"))
                        .lore(casterKey(
                                "too_cold",
                                PylonArgument.of("item", name),
                                PylonArgument.of("temperature", temperature)
                        ));
            }
            double bottomAmount = controller.getFluidAmount(bottomFluid);
            if (bottomAmount < CastingRecipe.CAST_AMOUNT) {
                return ItemStackBuilder.of(result.getType())
                        .name(casterKey("cannot_cast"))
                        .lore(casterKey(
                                "not_enough",
                                PylonArgument.of("fluid", bottomFluid.getName()),
                                PylonArgument.of("needed", UnitFormat.MILLIBUCKETS.format(CastingRecipe.CAST_AMOUNT)),
                                PylonArgument.of("amount", UnitFormat.MILLIBUCKETS.format(bottomAmount)
                                        .decimalPlaces(1))
                        ));
            }
            return ItemStackBuilder.of(result.getType())
                    .name(casterKey("cast"))
                    .lore(casterKey(
                            "click_to_cast",
                            PylonArgument.of("amount", UnitFormat.MILLIBUCKETS.format(bottomAmount)),
                            PylonArgument.of("needed", UnitFormat.MILLIBUCKETS.format(CastingRecipe.CAST_AMOUNT)),
                            PylonArgument.of("fluid", bottomFluid.getName())
                    ));
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {
            SmelteryController controller = getController();
            if (controller == null || bottomFluid == null || controller.getFluidAmount(bottomFluid) < CastingRecipe.CAST_AMOUNT)
                return;

            CastingRecipe recipe = CastingRecipe.getCastRecipeFor(bottomFluid);
            if (recipe == null || controller.getTemperature() < recipe.temperature()) return;

            if (!new PrePylonCraftEvent<>(CastingRecipe.RECIPE_TYPE, recipe, controller, player).callEvent()) {
                return;
            }

            ItemStack result = recipe.result();
            inventory.addItem(null, result);
            controller.removeFluid(bottomFluid, CastingRecipe.CAST_AMOUNT);

            new PylonCraftEvent<>(CastingRecipe.RECIPE_TYPE, recipe, controller).callEvent();
        }

        private static TranslatableComponent casterKey(@NotNull String subkey, @NotNull PylonArgument @NotNull ... args) {
            return Component.translatable("pylon.pylonbase.gui.smeltery_caster." + subkey, args);
        }
    }

    @Override
    public void tick(double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller == null) {
            bottomFluid = null;
        } else {
            Pair<PylonFluid, Double> bottomFluid = controller.getBottomFluid();
            if (bottomFluid == null) {
                this.bottomFluid = null;
            } else {
                this.bottomFluid = bottomFluid.getFirst();
            }
        }
        castItem.notifyWindows();
    }
}
