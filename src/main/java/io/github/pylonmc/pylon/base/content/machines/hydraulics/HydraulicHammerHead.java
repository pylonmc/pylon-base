package io.github.pylonmc.pylon.base.content.machines.hydraulics;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseFluids;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.content.tools.Hammer;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.*;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Config;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.content.fluid.FluidPointInteraction;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.fluid.FluidPointType;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Map;

import static io.github.pylonmc.pylon.base.util.BaseUtils.baseKey;


public class HydraulicHammerHead extends PylonBlock
        implements PylonTickingBlock, PylonInteractableBlock, PylonFluidBufferBlock, PylonEntityHolderBlock {

    public static final NamespacedKey HAMMER_KEY = baseKey("hammer");

    private static final Config settings = Settings.get(BaseKeys.HYDRAULIC_HAMMER_HEAD);
    public static final int GO_DOWN_TIME_TICKS = settings.getOrThrow("go-down-time-ticks", Integer.class);
    public static final double HYDRAULIC_FLUID_MB_PER_CRAFT = settings.getOrThrow("hydraulic-fluid-mb-per-craft", Integer.class);
    public static final double DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT = settings.getOrThrow("dirty-hydraulic-fluid-mb-per-craft", Integer.class);
    public static final int TICK_INTERVAL = settings.getOrThrow("tick-interval", Integer.class);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(HYDRAULIC_FLUID_MB_PER_CRAFT)),
                    PylonArgument.of("dirty_hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT))
            );
        }
    }

    @Getter @Nullable private Hammer hammer;
    @Getter double cooldown;

    @SuppressWarnings("unused")
    public HydraulicHammerHead(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        hammer = null;

        setTickInterval(TICK_INTERVAL);

        addEntity("input", FluidPointInteraction.make(context, FluidPointType.INPUT, BlockFace.NORTH));
        addEntity("output", FluidPointInteraction.make(context, FluidPointType.OUTPUT, BlockFace.SOUTH));
        addEntity("hammer_head", new SimpleItemDisplay(new ItemDisplayBuilder()
                .material(Material.GRAY_CONCRETE)
                .transformation(getHeadTransformation(0.7))
                .build(getBlock().getLocation().toCenterLocation().add(0, -1, 0))
        ));
        addEntity("hammer_tip", new SimpleItemDisplay(new ItemDisplayBuilder()
                .material(Material.AIR)
                .transformation(getTipTransformation(-0.3))
                .build(getBlock().getLocation().toCenterLocation().add(0, -1, 0))
        ));

        createFluidBuffer(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_MB_PER_CRAFT * 2, true, false);
        createFluidBuffer(BaseFluids.DIRTY_HYDRAULIC_FLUID, DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT * 2, false, true);
    }

    @SuppressWarnings("unused")
    public HydraulicHammerHead(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        hammer = (Hammer) PylonItem.fromStack(pdc.get(HAMMER_KEY, PylonSerializers.ITEM_STACK));
    }

    @Override
    public void write(@NotNull PersistentDataContainer pdc) {
        super.write(pdc);
        PdcUtils.setNullable(pdc, HAMMER_KEY, PylonSerializers.ITEM_STACK, hammer == null ? null : hammer.getStack());
    }

    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (!event.getAction().isRightClick() || event.getHand() != EquipmentSlot.HAND || event.getPlayer().isSneaking()) {
            return;
        }

        event.setCancelled(true);

        if (hammer != null) {
            event.getPlayer().give(hammer.getStack());
            hammer = null;
        } else {
            ItemStack stack = event.getPlayer().getInventory().getItem(EquipmentSlot.HAND);
            if (PylonItem.fromStack(stack.clone()) instanceof Hammer hammer) {
                this.hammer = hammer;
                stack.subtract();
            }
        }

        getHammerTip().getEntity().setItemStack(new ItemStack(hammer == null ? Material.AIR : hammer.baseBlock));
    }

    @Override
    public void tick(double deltaSeconds) {
        cooldown = Math.max(0, cooldown - deltaSeconds);

        if (cooldown > 1.0e-5 || hammer == null) {
            return;
        }

        Block baseBlock = getBlock().getRelative(BlockFace.DOWN, 3);

        if (BlockStorage.isPylonBlock(baseBlock) || baseBlock.getType() != hammer.baseBlock) {
            return;
        }

        if (hammer.getStack().getAmount() == 0) {
            this.hammer = null;
            getHammerTip().getEntity().setItemStack(new ItemStack(hammer == null ? Material.AIR : hammer.baseBlock));
        }

        if (fluidAmount(BaseFluids.HYDRAULIC_FLUID) < HYDRAULIC_FLUID_MB_PER_CRAFT
                || fluidSpaceRemaining(BaseFluids.DIRTY_HYDRAULIC_FLUID) < DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT
                || !hammer.tryDoRecipe(baseBlock, null)
        ) {
            return;
        }

        removeFluid(BaseFluids.HYDRAULIC_FLUID, HYDRAULIC_FLUID_MB_PER_CRAFT);
        addFluid(BaseFluids.DIRTY_HYDRAULIC_FLUID, DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT);

        getHammerHead().setTransform(GO_DOWN_TIME_TICKS, getHeadTransformation(-0.7));
        getHammerTip().setTransform(GO_DOWN_TIME_TICKS, getTipTransformation(-1.7));
        Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
            getHammerHead().setTransform(hammer.cooldownTicks - GO_DOWN_TIME_TICKS, getHeadTransformation(0.7));
            getHammerTip().setTransform(hammer.cooldownTicks - GO_DOWN_TIME_TICKS, getTipTransformation(0.3));
            new ParticleBuilder(Particle.BLOCK)
                    .data(baseBlock.getBlockData())
                    .count(20)
                    .location(baseBlock.getLocation().toCenterLocation().add(0, 0.6, 0))
                    .spawn();
        }, GO_DOWN_TIME_TICKS);

        cooldown = hammer.cooldownTicks / 20.0;
    }

    @Override
    public void postBreak() {
        PylonEntityHolderBlock.super.postBreak();
        if (hammer != null) {
            getBlock().getLocation().getWorld().dropItemNaturally(getBlock().getLocation(), hammer.getStack());
        }
    }

    public @NotNull SimpleItemDisplay getHammerHead() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "hammer_head");
    }

    public @NotNull SimpleItemDisplay getHammerTip() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "hammer_tip");
    }

    public static @NotNull Matrix4f getHeadTransformation(double translationY) {
        return new TransformBuilder()
                .translate(0, translationY, 0)
                .scale(0.3, 2.0, 0.3)
                .buildForItemDisplay();
    }

    public static @NotNull Matrix4f getTipTransformation(double translationY) {
        return new TransformBuilder()
                .translate(0, translationY, 0)
                .scale(0.6, 0.1, 0.6)
                .buildForItemDisplay();
    }
}
