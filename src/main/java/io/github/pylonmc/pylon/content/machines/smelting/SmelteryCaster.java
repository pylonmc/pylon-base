package io.github.pylonmc.pylon.content.machines.smelting;

import io.github.pylonmc.pylon.recipes.CastingRecipe;
import io.github.pylonmc.rebar.block.base.RebarGuiBlock;
import io.github.pylonmc.rebar.block.base.RebarLogisticBlock;
import io.github.pylonmc.rebar.block.base.RebarTickingBlock;
import io.github.pylonmc.rebar.block.base.RebarVirtualInventoryBlock;
import io.github.pylonmc.rebar.block.context.BlockCreateContext;
import io.github.pylonmc.rebar.fluid.RebarFluid;
import io.github.pylonmc.rebar.i18n.RebarArgument;
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder;
import io.github.pylonmc.rebar.logistics.LogisticGroupType;
import io.github.pylonmc.rebar.util.gui.GuiItems;
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat;
import kotlin.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import xyz.xenondevs.invui.Click;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.AbstractItem;
import xyz.xenondevs.invui.item.ItemProvider;

import java.util.Map;

public final class SmelteryCaster extends SmelteryComponent implements
        RebarGuiBlock,
        RebarVirtualInventoryBlock,
        RebarTickingBlock,
        RebarLogisticBlock {

    private @Nullable RebarFluid bottomFluid = null;

    @SuppressWarnings("unused")
    public SmelteryCaster(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setTickInterval(SmelteryController.TICK_INTERVAL);
    }

    @SuppressWarnings("unused")
    public SmelteryCaster(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void postInitialise() {
        createLogisticGroup("output", LogisticGroupType.OUTPUT, inventory);
    }

    private final VirtualInventory inventory = new VirtualInventory(1);
    private final CastItem castItem = new CastItem();

    @Override
    public @NotNull Gui createGui() {
        return Gui.builder()
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
        public @NonNull ItemProvider getItemProvider(@NotNull Player viewer) {
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
                                RebarArgument.of("item", name),
                                RebarArgument.of("temperature", temperature)
                        ));
            }
            double bottomAmount = controller.getFluidAmount(bottomFluid);
            if (bottomAmount < recipe.input().amountMillibuckets()) {
                return ItemStackBuilder.of(result.getType())
                        .name(casterKey("cannot_cast"))
                        .lore(casterKey(
                                "not_enough",
                                RebarArgument.of("fluid", bottomFluid.getName()),
                                RebarArgument.of("needed", UnitFormat.MILLIBUCKETS.format(recipe.input().amountMillibuckets())),
                                RebarArgument.of("amount", UnitFormat.MILLIBUCKETS.format(bottomAmount)
                                        .decimalPlaces(1))
                        ));
            }
            return ItemStackBuilder.of(result.getType())
                    .name(casterKey("cast"))
                    .lore(casterKey(
                            "click_to_cast",
                            RebarArgument.of("amount", UnitFormat.MILLIBUCKETS.format(bottomAmount)),
                            RebarArgument.of("needed", UnitFormat.MILLIBUCKETS.format(recipe.input().amountMillibuckets())),
                            RebarArgument.of("fluid", bottomFluid.getName())
                    ));
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull Click click) {
            SmelteryController controller = getController();
            if (controller == null || bottomFluid == null) return;

            CastingRecipe recipe = CastingRecipe.getCastRecipeFor(bottomFluid);
            if (recipe == null || controller.getTemperature() < recipe.temperature() || controller.getFluidAmount(bottomFluid) < recipe.input().amountMillibuckets())
                return;

            ItemStack result = recipe.result();
            if (!inventory.canHold(result)) {
                return;
            }

            inventory.addItem(null, result);

            controller.removeFluid(bottomFluid, recipe.input().amountMillibuckets());
        }

        private static TranslatableComponent casterKey(@NotNull String subkey, @NotNull RebarArgument @NotNull ... args) {
            return Component.translatable("pylon.gui.smeltery_caster." + subkey, args);
        }
    }

    @Override
    public @NotNull Map<String, VirtualInventory> getVirtualInventories() {
        return Map.of("output", inventory);
    }

    @Override
    public void tick() {
        SmelteryController controller = getController();
        if (controller == null) {
            bottomFluid = null;
        } else {
            Pair<RebarFluid, Double> bottomFluid = controller.getBottomFluid();
            if (bottomFluid == null) {
                this.bottomFluid = null;
            } else {
                this.bottomFluid = bottomFluid.getFirst();
            }
        }
        castItem.notifyWindows();
    }
}
