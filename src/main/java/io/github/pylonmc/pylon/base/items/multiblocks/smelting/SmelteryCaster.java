package io.github.pylonmc.pylon.base.items.multiblocks.smelting;

import io.github.pylonmc.pylon.base.fluid.CastableFluid;
import io.github.pylonmc.pylon.core.block.base.PylonGuiBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import kotlin.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.TranslationArgument;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.inventory.VirtualInventory;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.impl.AbstractItem;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;

@NullMarked
public final class SmelteryCaster extends SmelteryComponent implements PylonGuiBlock, PylonTickingBlock {

    public static final NamespacedKey KEY = pylonKey("smeltery_caster");

    private @Nullable PylonFluid canCast = null;

    public SmelteryCaster(Block block, BlockCreateContext context) {
        super(block, context);
    }

    public SmelteryCaster(Block block, PersistentDataContainer pdc) {
        super(block, pdc);
    }

    private final VirtualInventory inventory = new VirtualInventory(1);
    private final CastItem castItem = new CastItem();

    @Override
    public Gui createGui() {
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
            if (controller == null || canCast == null) {
                return ItemStackBuilder.of(Material.BARRIER)
                        .name(casterKey("cannot_cast"));
            } else {
                CastableFluid fluid = canCast.getTag(CastableFluid.class);
                ItemStack result = fluid.castResult();
                Component name = result.effectiveName();
                double temperature = fluid.castTemperature();
                if (controller.getTemperature() < temperature) {
                    return ItemStackBuilder.of(Material.BARRIER)
                            .name(casterKey("cannot_cast"))
                            .lore(casterKey(
                                    "too_cold",
                                    PylonArgument.of("item", name),
                                    PylonArgument.of("temperature", temperature)
                            ));
                } else if (controller.getFluidAmount(canCast) < 111) {
                    return ItemStackBuilder.of(Material.BARRIER)
                            .name(casterKey("cannot_cast"))
                            .lore(casterKey(
                                    "not_enough",
                                    PylonArgument.of("fluid", canCast.getName()),
                                    PylonArgument.of("item", name),
                                    PylonArgument.of("amount", UnitFormat.MILLIBUCKETS.format(controller.getFluidAmount(canCast)))
                            ));
                } else {
                    return ItemStackBuilder.of(result.getType())
                            .name(casterKey("cast"))
                            .lore(casterKey(
                                    "click_to_cast",
                                    PylonArgument.of("item", name)
                            ));
                }
            }
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            SmelteryController controller = getController();
            if (controller == null || canCast == null || controller.getFluidAmount(canCast) < 111) return;

            CastableFluid fluid = canCast.getTag(CastableFluid.class);
            if (controller.getTemperature() < fluid.castTemperature()) return;

            ItemStack result = fluid.castResult();
            inventory.addItem(null, result);
            controller.removeFluid(canCast, 111);
        }

        private static TranslatableComponent casterKey(String subkey, TranslationArgument... args) {
            return Component.translatable("pylon.pylonbase.gui.smeltery_caster." + subkey, args);
        }
    }

    @Override
    public void tick(double deltaSeconds) {
        SmelteryController controller = getController();
        if (controller == null) {
            canCast = null;
        } else {
            Pair<PylonFluid, Double> bottomFluid = controller.getBottomFluid();
            if (bottomFluid == null) {
                canCast = null;
            } else {
                PylonFluid fluid = bottomFluid.getFirst();
                if (fluid.hasTag(CastableFluid.class)) {
                    canCast = fluid;
                } else {
                    canCast = null;
                }
            }
        }
        castItem.notifyWindows();
    }
}
