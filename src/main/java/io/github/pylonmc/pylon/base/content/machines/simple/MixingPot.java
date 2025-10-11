package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.recipes.MixingPotRecipe;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonEntityHolderBlock;
import io.github.pylonmc.pylon.core.block.base.PylonFluidTank;
import io.github.pylonmc.pylon.core.block.base.PylonInteractBlock;
import io.github.pylonmc.pylon.core.block.base.PylonMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.block.waila.WailaConfig;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.RecipeInput;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.BlockPosition;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public final class MixingPot extends PylonBlock implements PylonMultiblock, PylonInteractBlock, PylonFluidTank {

    @SuppressWarnings("unused")
    public MixingPot(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);

        createFluidPoint(FluidPointType.INPUT, BlockFace.NORTH, context, false);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);

        setCapacity(1000.0);
    }

    @SuppressWarnings("unused")
    public MixingPot(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public @NotNull Set<ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock()));
    }

    @Override
    public boolean checkFormed() {
        return getFire().getType() == Material.FIRE;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return new BlockPosition(otherBlock).equals(new BlockPosition(getFire()));
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return true;
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
    public @Nullable WailaConfig getWaila(@NotNull Player player) {
        return new WailaConfig(getDefaultWailaTranslationKey().arguments(
                PylonArgument.of("info", getFluidType() == null ?
                        Component.translatable("pylon.pylonbase.waila.mixing_pot.empty") :
                        Component.translatable("pylon.pylonbase.waila.mixing_pot.filled",
                                PylonArgument.of("fluid", getFluidType().getName()),
                                PylonArgument.of("amount", UnitFormat.MILLIBUCKETS.format(getFluidAmount()).decimalPlaces(1))
                        ))
        ));
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        // Don't allow fluid to be manually inserted/removed
        if (event.getItem() != null
                && Set.of(Material.BUCKET, Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.GLASS_BOTTLE).contains(event.getMaterial())
        ) {
            event.setCancelled(true);
            return;
        }

        if (event.getPlayer().isSneaking()
                || event.getHand() != EquipmentSlot.HAND
                || event.getAction() != Action.RIGHT_CLICK_BLOCK
        ) {
            return;
        }

        event.setCancelled(true);

        if (!isFormedAndFullyLoaded() || getFluidType() == null) {
            return;
        }

        tryDoRecipe(event.getPlayer());
    }

    public boolean tryDoRecipe(@Nullable Player player) {
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

        PylonBlock ignitedBlock = BlockStorage.get(getIgnitedBlock());
        boolean isEnrichedFire = ignitedBlock != null
                && ignitedBlock.getSchema().getKey().equals(BaseKeys.ENRICHED_NETHERRACK);

        for (MixingPotRecipe recipe : MixingPotRecipe.RECIPE_TYPE.getRecipes()) {
            if (recipe.matches(stacks, isEnrichedFire, getFluidType(), getFluidAmount())) {
                if (!new PrePylonCraftEvent<>(MixingPotRecipe.RECIPE_TYPE, recipe, this, player).callEvent()) {
                    continue;
                }

                doRecipe(recipe, items);
                return true;
            }
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
            default -> {
            }
        }

        new PylonCraftEvent<>(MixingPotRecipe.RECIPE_TYPE, recipe, this).callEvent();

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
