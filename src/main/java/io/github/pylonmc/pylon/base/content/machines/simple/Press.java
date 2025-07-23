package io.github.pylonmc.pylon.base.content.machines.simple;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.base.recipes.PressRecipe;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidBufferBlock;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.core.util.ItemUtils.isPylonSimilar;


public class Press extends PylonBlock
        implements PylonInteractableBlock, PylonFluidBufferBlock, PylonEntityHolderBlock {

    private static final Config settings = Settings.get(BaseKeys.PRESS);
    public static final int TIME_PER_ITEM_TICKS = settings.getOrThrow("time-per-item-ticks", Integer.class);
    public static final int RETURN_TO_START_TIME_TICKS = settings.getOrThrow("return-to-start-time-ticks", Integer.class);
    public static final int CAPACITY_MB = settings.getOrThrow("capacity-mb", Integer.class);

    public static class PressItem extends PylonItem {

        public PressItem(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "time_per_item", UnitFormat.SECONDS.format(TIME_PER_ITEM_TICKS / 20.0)
            );
        }
    }

    // Not worth the effort to persist across unloads
    @Getter private @Nullable PressRecipe currentRecipe;

    @SuppressWarnings("unused")
    public Press(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        addEntity("press_cover", new SimpleItemDisplay(new ItemDisplayBuilder()
                .material(Material.SPRUCE_PLANKS)
                .transformation(getCoverTransform(0.4))
                .build(getBlock().getLocation().toCenterLocation())
        ));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.NORTH));

        createFluidBuffer(BaseFluids.PLANT_OIL, CAPACITY_MB, false, true);
    }

    @SuppressWarnings("unused")
    public Press(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public @NotNull WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(
                getName(),
                Map.of(
                        "plant_oil_amount", UnitFormat.MILLIBUCKETS.format(Math.round(fluidAmount(BaseFluids.PLANT_OIL))),
                        "plant_oil_capacity", UnitFormat.MILLIBUCKETS.format(CAPACITY_MB),
                        "plant_oil", BaseFluids.PLANT_OIL.getName()
                )
        );
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick() || event.getHand() != EquipmentSlot.HAND || event.getPlayer().isSneaking()) {
            return;
        }

        event.setCancelled(true);

        if (currentRecipe != null) {
            return;
        }

        tryStartRecipe(event.getPlayer());
    }

    public boolean tryStartRecipe(@Nullable Player player) {
        if (currentRecipe != null) {
            return false;
        }

        List<Item> items = getBlock()
                .getLocation()
                .toCenterLocation()
                .getNearbyEntities(0.5, 0.8, 0.5)
                .stream()
                .filter(Item.class::isInstance)
                .map(Item.class::cast)
                .toList();

        List<ItemStack> stacks = items.stream()
                .map(Item::getItemStack)
                .toList();

        for (PressRecipe recipe : PressRecipe.RECIPE_TYPE.getRecipes()) {
            for (ItemStack stack : stacks) {
                if (isPylonSimilar(recipe.input(), stack)) {
                    double availableSpace = CAPACITY_MB - fluidAmount(BaseFluids.PLANT_OIL);
                    if (recipe.oilAmount() > availableSpace
                            || !new PrePylonCraftEvent<>(PressRecipe.RECIPE_TYPE, recipe, this, player).callEvent()
                    ) {
                        continue;
                    }

                    stack.subtract();
                    startRecipe(recipe);
                    return true;
                }
            }
        }

        return false;
    }

    public void startRecipe(PressRecipe recipe) {
        this.currentRecipe = recipe;
        getCover().setTransform(TIME_PER_ITEM_TICKS - RETURN_TO_START_TIME_TICKS, getCoverTransform(0.0));

        Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
            getCover().setTransform(RETURN_TO_START_TIME_TICKS, getCoverTransform(0.0));

            Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
                addFluid(BaseFluids.PLANT_OIL, recipe.oilAmount());
                new PylonCraftEvent<>(PressRecipe.RECIPE_TYPE, recipe, this).callEvent();
                this.currentRecipe = null;
            }, RETURN_TO_START_TIME_TICKS);
        }, TIME_PER_ITEM_TICKS - RETURN_TO_START_TIME_TICKS);
    }

    public @NotNull SimpleItemDisplay getCover() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "press_cover");
    }

    public static @NotNull Matrix4f getCoverTransform(double translation) {
        return new TransformBuilder()
                .translate(0, translation, 0)
                .scale(0.9, 0.1, 0.9)
                .buildForItemDisplay();
    }


}
