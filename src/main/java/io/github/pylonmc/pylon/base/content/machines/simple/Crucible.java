package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.recipes.CrucibleRecipe;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.util.PylonUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class Crucible extends PylonBlock implements PylonMultiblock, PylonInteractBlock, PylonFluidTank, PylonCauldron, PylonBreakHandler, PylonTickingBlock {
    public final int capacity = getSettings().getOrThrow("capacity", ConfigAdapter.INT);
    public final int smeltTime = getSettings().getOrThrow("smelt-time", ConfigAdapter.INT);

    private ItemStack processingType = null;
    private int amount = 0;

    private static final NamespacedKey PROCESSING_KEY = baseKey("processing");
    private static final NamespacedKey AMOUNT_KEY = baseKey("amount");

    public static final Map<Material, Integer> VANILLA_BLOCK_HEAT_MAP = Settings.get(BaseKeys.CRUCIBLE).getOrThrow("vanilla-block-heat-map", ConfigAdapter.MAP.from(ConfigAdapter.MATERIAL, ConfigAdapter.INT));
    public static final Set<Material> ITEM_BLACKLIST = Set.of(Material.BUCKET, Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.GLASS_BOTTLE);

    @SuppressWarnings("unused")
    public Crucible(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.NORTH, context, false);
        setCapacity(1000.0);
    }

    @SuppressWarnings("unused")
    public Crucible(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        processingType = pdc.get(PROCESSING_KEY, PylonSerializers.ITEM_STACK);
        amount = pdc.get(AMOUNT_KEY, PylonSerializers.INTEGER);
    }

    //region Inventory handling
    public int spaceAvailable() {
        return capacity - amount;
    }

    @Override
    public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
        PylonFluidTank.super.onBreak(drops, context);
        if (processingType == null || amount == 0) return;

        int maxStack = processingType.getMaxStackSize();
        int cycles = amount / maxStack;
        int spare = amount % maxStack;
        for (int i = 0; i < cycles; i++) {
            drops.add(processingType.asQuantity(maxStack));
        }

        drops.add(processingType.asQuantity(spare));
    }
    //endregion

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        PylonUtils.setNullable(pdc, PROCESSING_KEY, PylonSerializers.ITEM_STACK, processingType);
        pdc.set(AMOUNT_KEY, PylonSerializers.INTEGER, amount);
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return true;
    }

    @Override
    public void onInteract(@NotNull PlayerInteractEvent event) {
        // Don't allow fluid to be manually inserted/removed
        if (event.getItem() != null && ITEM_BLACKLIST.contains(event.getMaterial())) {
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

        if (!isFormedAndFullyLoaded()) {
            return;
        }

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (!CrucibleRecipe.isValid(item)) {
            return;
        }

        int amount = Math.min(item.getAmount(), spaceAvailable());
        if (amount == 0) {
            return;
        }

        if (processingType == null) {
            this.processingType = item.asOne();
            this.amount = amount;
            item.subtract(amount);
        } else if (processingType.isSimilar(item)) {
            this.amount += amount;
            item.subtract(amount);
        }
    }

    public void clearInventory() {
        this.processingType = null;
        this.amount = 0;
    }

    public boolean tryDoRecipe() {
        if (processingType == null) return false;

        for (CrucibleRecipe recipe : CrucibleRecipe.RECIPE_TYPE.getRecipes()) {
            if (recipe.matches(processingType)) {
                if (!new PrePylonCraftEvent<>(CrucibleRecipe.RECIPE_TYPE, recipe, this, null).callEvent()) {
                    continue;
                }

                doRecipe(recipe);
                return true;
            }
        }

        return false;
    }

    private void doRecipe(@NotNull CrucibleRecipe recipe) {
        if (recipe.output().fluid().equals(getFluidType())) {
            if (getFluidAmount() == getFluidCapacity()) return; // no need to waste stuff
        }

        FluidOrItem.Fluid fluid = recipe.output();

        setFluidType(fluid.fluid());
        addFluid(fluid.amountMillibuckets());

        new PylonCraftEvent<>(CrucibleRecipe.RECIPE_TYPE, recipe, this).callEvent();

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

        this.amount--;
        if (this.amount == 0) {
            clearInventory();
        }
    }

    @Override
    public @NotNull Set<@NotNull ChunkPosition> getChunksOccupied() {
        return Set.of(new ChunkPosition(getBlock()));
    }

    @Override
    public boolean checkFormed() {
        return getHeatFactor() != null;
    }

    @Override
    public boolean isPartOfMultiblock(@NotNull Block otherBlock) {
        return otherBlock.equals(getBelow());
    }

    //region Cauldron logic

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

    //endregion

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
            PylonArgument.of("item_info", this.processingType == null ?
                Component.translatable("pylon.pylonbase.waila.crucible.item.empty") :
                Component.translatable("pylon.pylonbase.waila.crucible.item.stored",
                    PylonArgument.of("type", this.processingType.getData(DataComponentTypes.ITEM_NAME)),
                    PylonArgument.of("amount", this.amount)
                )),

            PylonArgument.of("liquid_info", getFluidType() == null ?
                Component.translatable("pylon.pylonbase.waila.crucible.liquid.empty") :
                Component.translatable("pylon.pylonbase.waila.crucible.liquid.filled",
                    PylonArgument.of("fluid", getFluidType().getName()),
                    PylonArgument.of("amount", UnitFormat.MILLIBUCKETS.format(getFluidAmount()).decimalPlaces(1))
                ))
        ));
    }

    //region Tick handling
    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) return;

        tryDoRecipe();
        updateCauldron();
    }

    @Override
    public int getTickInterval() {
        if (processingType == null) {
            return smeltTime; // minimize ticking when empty
        }

        Integer heatFactor = getHeatFactor();
        if (heatFactor == null) {
            return smeltTime; // probably won't even work be operating in such scenario
        }

        // stronger heat factor -> faster smelting
        return smeltTime / heatFactor;
    }

    //endregion

    //region Heat handling
    public interface HeatedBlock extends Keyed {
        int heatGenerated();
    }

    public @NotNull Block getBelow() {
        return getBlock().getRelative(BlockFace.DOWN);
    }

    public Integer getHeatFactor() {
        Block below = getBelow();
        var pylonBlock = BlockStorage.get(below);
        if (pylonBlock != null) {
            if (pylonBlock instanceof HeatedBlock heatedBlock) {
                return heatedBlock.heatGenerated();
            }

            return null;
        }

        return VANILLA_BLOCK_HEAT_MAP.get(below.getType());
    }
    //endregion
}
