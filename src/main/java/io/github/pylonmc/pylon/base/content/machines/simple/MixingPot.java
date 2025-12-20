package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.content.components.EnrichedSoulSoil;
import io.github.pylonmc.pylon.base.recipes.MixingPotRecipe;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public final class MixingPot extends PylonBlock implements
        PylonDirectionalBlock,
        PylonInteractBlock,
        PylonFluidTank,
        PylonCauldron {

    private static final Set<Material> BUCKETS = Set.of(Material.BUCKET, Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.GLASS_BOTTLE);

    public static class MixingPotItem extends PylonItem {

        public MixingPotItem(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("capacity", UnitFormat.MILLIBUCKETS.format(1000))
            );
        }
    }

    @SuppressWarnings("unused")
    public MixingPot(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        setFacing(context.getFacing());
        setCapacity(1000.0);
        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);
    }

    @SuppressWarnings("unused")
    public MixingPot(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return true;
    }

    @Override
    public void onLevelChange(@NotNull CauldronLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onFluidAdded(@NotNull PylonFluid fluid, double amount) {
        PylonFluidTank.super.onFluidAdded(fluid, amount);
        updateCauldron();
    }

    @Override
    public void onFluidRemoved(@NotNull PylonFluid fluid, double amount) {
        PylonFluidTank.super.onFluidRemoved(fluid, amount);
        updateCauldron();
    }

    private void updateCauldron() {
        int level = (int) getFluidAmount() / 333;
        if (level > 0 && getBlock().getType() == Material.CAULDRON) {
            getBlock().setType(Material.WATER_CAULDRON);
        } else if (level == 0) {
            getBlock().setType(Material.CAULDRON);
        }
        if (getBlock().getBlockData() instanceof Levelled levelled) {
            levelled.setLevel(level);
            getBlock().setBlockData(levelled);
        }
    }

    @Override
    public @NotNull WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("bar", BaseUtils.createFluidAmountBar(
                        getFluidAmount(),
                        getFluidCapacity(),
                        20,
                        TextColor.color(200, 255, 255)
                )),
                getFluidType() == null
                    ? PylonArgument.of("fluid", Component.translatable("pylon.pylonbase.fluid.none"))
                    : PylonArgument.of("fluid", getFluidType().getName())
        ));
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) {
            return;
        }

        event.setCancelled(true);

        if (event.getItem() != null && BUCKETS.contains(event.getMaterial()) || getFluidType() == null) {
            return;
        }

        tryDoRecipe();
    }

    public boolean tryDoRecipe() {
        if (getFluidType() == null || (getFire().getType() != Material.FIRE && getFire().getType() != Material.SOUL_FIRE)) {
            return false;
        }

        List<Item> items = getBlock()
                .getLocation()
                .toCenterLocation()
                .getNearbyEntities(0.5, 0.8, 0.5) // 0.8 to allow items on top to be used
                .stream()
                .filter(Item.class::isInstance)
                .map(Item.class::cast)
                .toList();

        List<ItemStack> stacks = items.stream()
                .map(Item::getItemStack)
                .toList();

        boolean isEnriched = BlockStorage.getAs(EnrichedSoulSoil.class, getIgnitedBlock()) != null;

        for (MixingPotRecipe recipe : MixingPotRecipe.RECIPE_TYPE.getRecipes()) {
            if (!recipe.matches(stacks, isEnriched, getFluidType(), getFluidAmount())) {
                continue;
            }

            doRecipe(recipe, items);
            return true;
        }

        return false;
    }

    private void doRecipe(@NotNull MixingPotRecipe recipe, @NotNull List<Item> items) {
        for (RecipeInput.Item choice : recipe.inputItems()) {
            for (Item item : items) {
                ItemStack stack = item.getItemStack();
                if (choice.matches(stack)) {
                    item.setItemStack(stack.subtract(choice.getAmount()));
                    break;
                }
            }
        }
        switch (recipe.output()) {
            case FluidOrItem.Item item -> {
                removeFluid(recipe.inputFluid().amountMillibuckets());
                getBlock().getWorld().dropItemNaturally(getBlock().getLocation().toCenterLocation(), item.item());
            }
            case FluidOrItem.Fluid fluid -> {
                setFluidType(fluid.fluid());
                setFluid(fluid.amountMillibuckets());
            }
            default -> {}
        }

        new ParticleBuilder(Particle.SPLASH)
                .count(20)
                .location(getBlock().getLocation().toCenterLocation().add(0, 0.5, 0))
                .offset(0.3, 0, 0.3)
                .spawn();

        new ParticleBuilder(Particle.CAMPFIRE_COSY_SMOKE)
                .count(30)
                .location(getBlock().getLocation().toCenterLocation())
                .extra(0.05)
                .spawn();
    }

    public @NotNull Block getFire() {
        return getBlock().getRelative(BlockFace.DOWN);
    }

    public @NotNull Block getIgnitedBlock() {
        return getFire().getRelative(BlockFace.DOWN);
    }
}
