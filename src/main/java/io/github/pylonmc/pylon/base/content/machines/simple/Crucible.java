package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.recipes.CrucibleRecipe;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.PylonBlockSchema;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.event.PrePylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonCraftEvent;
import io.github.pylonmc.pylon.core.event.PylonRegisterEvent;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.fluid.PylonFluid;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.registry.PylonRegistry;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.util.position.ChunkPosition;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import net.kyori.adventure.text.Component;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;

public final class Crucible extends PylonBlock implements PylonMultiblock, PylonInteractBlock, PylonFluidTank, PylonCauldron, PylonBreakHandler, PylonTickingBlock {
    public final int CAPACITY = getSettings().getOrThrow("capacity", ConfigAdapter.INT);
    public final int SMELT_TIME = getSettings().getOrThrow("smelt-time", ConfigAdapter.INT);

    private final Stack<ItemStack> contents = new Stack<>();
    private ItemStack processing = null;

    private static final NamespacedKey CONTENTS_KEY = baseKey("contents");
    private static final PersistentDataType<?, List<ItemStack>> CONTENTS_TYPE = PylonSerializers.LIST.listTypeFrom(PylonSerializers.ITEM_STACK);
    private static final NamespacedKey PROCESSING_KEY = baseKey("processing");

    @SuppressWarnings("unused")
    public Crucible(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block);
        createFluidPoint(FluidPointType.OUTPUT, BlockFace.SOUTH, context, false);
        setCapacity(1000.0);
    }

    @SuppressWarnings("unused")
    public Crucible(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block);
        contents.addAll(pdc.get(CONTENTS_KEY, CONTENTS_TYPE));
        processing = pdc.get(PROCESSING_KEY, PylonSerializers.ITEM_STACK);
    }

    //region Inventory handling
    public int spaceAvailable() {
        return CAPACITY - contents.stream().mapToInt(ItemStack::getAmount).sum() - ((processing == null) ? 0 : processing.getAmount());
    }

    @Override
    public void onBreak(@NotNull List<ItemStack> drops, @NotNull BlockBreakContext context) {
        drops.addAll(contents);
    }
    //endregion

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        pdc.set(CONTENTS_KEY, CONTENTS_TYPE, contents);
        pdc.set(PROCESSING_KEY, PylonSerializers.ITEM_STACK, processing);
    }

    @Override
    public boolean isAllowedFluid(@NotNull PylonFluid fluid) {
        return true;
    }

    public static final Set<Material> ITEM_BLACKLIST = Set.of(Material.BUCKET, Material.WATER_BUCKET, Material.LAVA_BUCKET, Material.GLASS_BOTTLE);
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

        if (!isValid(item)) {
            return;
        }

        int amount = Math.min(item.getAmount(), spaceAvailable());
        if (amount == 0) {
            return;
        }

        ItemStack toAdd = item.asQuantity(amount);
        contents.add(toAdd);
        item.subtract(amount);
    }

    public boolean isValid(ItemStack item) {
        for(var entry : CrucibleRecipe.RECIPE_TYPE.getRecipes()) {
            if (entry.matches(item)) {
                return true;
            }
        }

        return false;
    }

    public boolean tryDoRecipe() {
        if (processing == null) return false;

        for (CrucibleRecipe recipe : CrucibleRecipe.RECIPE_TYPE.getRecipes()) {
            if (recipe.matches(processing)) {
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

        processing.subtract();
        if (processing.getAmount() == 0) {
            processing = null;
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
            PylonArgument.of("info", getFluidType() == null ?
                Component.translatable("pylon.pylonbase.waila.crucible.empty") :
                Component.translatable("pylon.pylonbase.waila.crucible.filled",
                    PylonArgument.of("fluid", getFluidType().getName()),
                    PylonArgument.of("amount", UnitFormat.MILLIBUCKETS.format(getFluidAmount()).decimalPlaces(1))
                ))
        ));
    }

    //region Tick handling
    @Override
    public void tick(double deltaSeconds) {
        if (!isFormedAndFullyLoaded()) return;
        if (processing == null) {
            if (!contents.isEmpty()) {
                processing = contents.pop();
            }
        }

        tryDoRecipe();
        updateCauldron();
    }

    @Override
    public int getTickInterval() {
        if (processing == null) {
            return SMELT_TIME; // minimize ticking when empty
        }

        Integer heatFactor = getHeatFactor();
        if (heatFactor == null) {
            return SMELT_TIME; // probably won't even work be operating in such scenario
        }

        // stronger heat factor -> faster smelting
        return SMELT_TIME / heatFactor;
    }

    //endregion

    //region Heat handling
    public static final Map<Material, Integer> VANILLA_BLOCK_HEAT_MAP = Settings.get(BaseKeys.CRUCIBLE).getOrThrow("vanilla-block-heat-map", ConfigAdapter.MAP.from(ConfigAdapter.MATERIAL, ConfigAdapter.INT));
    public static final Set<NamespacedKey> HEATED_BLOCKS = new HashSet<>();

    public static class HeatRegistrar implements Listener {
        @EventHandler
        void registerHeatable(PylonRegisterEvent e) {
            if (!e.getRegistry().equals(PylonRegistry.BLOCKS)) return;

            PylonBlockSchema blockSchema = (PylonBlockSchema) e.getValue();
            if (!Crucible.HeatedBlock.class.isAssignableFrom(blockSchema.getBlockClass())) return;
            HEATED_BLOCKS.add(blockSchema.getKey());
        }
    }

    static {
        for (var material : VANILLA_BLOCK_HEAT_MAP.keySet()) {
            HEATED_BLOCKS.add(material.getKey());
        }
    }

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
