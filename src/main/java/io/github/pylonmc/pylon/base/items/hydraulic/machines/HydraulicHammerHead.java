package io.github.pylonmc.pylon.base.items.hydraulic.machines;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.items.tools.Hammer;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.base.PylonInteractableBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.datatypes.PylonSerializers;
import io.github.pylonmc.pylon.core.entity.PylonEntity;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.PdcUtils;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.Map;
import java.util.Objects;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class HydraulicHammerHead extends SimpleHydraulicMachine implements PylonTickingBlock, PylonInteractableBlock {

    public static final NamespacedKey KEY = pylonKey("hydraulic_hammer_head");
    public static final NamespacedKey HAMMER_KEY = pylonKey("hammer");

    public static final int GO_DOWN_TIME_TICKS = Settings.get(KEY).getOrThrow("go-down-time-ticks", Integer.class);
    public static final double HYDRAULIC_FLUID_MB_PER_CRAFT = Settings.get(KEY).getOrThrow("hydraulic-fluid-mb-per-craft", Integer.class);
    public static final double DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT = Settings.get(KEY).getOrThrow("dirty-hydraulic-fluid-mb-per-craft", Integer.class);
    public static final double HYDRAULIC_FLUID_BUFFER = HYDRAULIC_FLUID_MB_PER_CRAFT * 2;
    public static final double DIRTY_HYDRAULIC_FLUID_BUFFER = DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT * 2;

    public static final int TICK_INTERVAL = Settings.get(KEY).getOrThrow("tick-interval", Integer.class);

    public static class Item extends PylonItem {

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(HYDRAULIC_FLUID_MB_PER_CRAFT),
                    "dirty_hydraulic_fluid_per_craft", UnitFormat.MILLIBUCKETS.format(DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT)
            );
        }
    }

    @Getter @Nullable private Hammer hammer;
    @Getter double cooldown;

    @SuppressWarnings("unused")
    public HydraulicHammerHead(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        hammer = null;
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

    @Override
    double getHydraulicFluidBuffer() {
        return HYDRAULIC_FLUID_BUFFER;
    }

    @Override
    double getDirtyHydraulicFluidBuffer() {
        return DIRTY_HYDRAULIC_FLUID_BUFFER;
    }

    @Override
    public @NotNull Map<String, PylonEntity<?>> createEntities(@NotNull BlockCreateContext context) {
        Map<String, PylonEntity<?>> entities = super.createEntities(context);
        entities.put("hammer_head", new HammerHeadEntity(getBlock()));
        entities.put("hammer_tip", new HammerTipEntity(getBlock()));
        return entities;
    }

    @Override
    public int getCustomTickRate(int globalTickRate) {
        return TICK_INTERVAL;
    }

    @Override
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

        getPistonTip().setHammer(hammer);
    }

    @Override
    public void tick(double deltaSeconds) {
        cooldown = Math.max(0, cooldown - deltaSeconds);

        if (hammer == null || cooldown > 1.0e-5) {
            return;
        }

        Block baseBlock = getBlock()
                .getRelative(BlockFace.DOWN)
                .getRelative(BlockFace.DOWN)
                .getRelative(BlockFace.DOWN);

        if (BlockStorage.isPylonBlock(baseBlock) || baseBlock.getType() != hammer.baseBlock) {
            return;
        }

        if (canStartCraft(HYDRAULIC_FLUID_MB_PER_CRAFT, DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT) && hammer.tryDoRecipe(baseBlock, null)) {
            startCraft(HYDRAULIC_FLUID_MB_PER_CRAFT, DIRTY_HYDRAULIC_FLUID_MB_PER_CRAFT);
            getPistonShaft().goDown();
            getPistonTip().goDown();
            Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
                getPistonShaft().goUp(hammer);
                getPistonTip().goUp(hammer);
                new ParticleBuilder(Particle.BLOCK)
                        .data(baseBlock.getBlockData())
                        .count(20)
                        .location(baseBlock.getLocation().toCenterLocation().add(0, 0.6, 0))
                        .spawn();
            }, GO_DOWN_TIME_TICKS);
            cooldown = hammer.cooldownTicks / 20.0;
        }

        if (hammer.getStack().getAmount() == 0) {
            this.hammer = null;
            getPistonTip().setHammer(hammer);
        }
    }

    @Override
    public void postBreak() {
        super.postBreak();
        if (hammer != null) {
            getBlock().getLocation().getWorld().dropItemNaturally(getBlock().getLocation(), hammer.getStack());
        }
    }

    public @NotNull HydraulicHammerHead.HammerHeadEntity getPistonShaft() {
        return Objects.requireNonNull(getHeldEntity(HammerHeadEntity.class, "hammer_head"));
    }

    public @NotNull HydraulicHammerHead.HammerTipEntity getPistonTip() {
        return Objects.requireNonNull(getHeldEntity(HammerTipEntity.class, "hammer_tip"));
    }

    public static class HammerHeadEntity extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("hammer_head");

        @SuppressWarnings("unused")
        public HammerHeadEntity(@NotNull ItemDisplay entity) {
            super(entity);
        }

        public HammerHeadEntity(@NotNull  Block block) {
            super(
                    KEY,
                    new ItemDisplayBuilder()
                            .material(Material.GRAY_CONCRETE)
                            .transformation(getTransformation(0.7))
                            .build(block.getLocation().toCenterLocation().add(0, -1, 0))
            );
        }

        public void goDown() {
            getEntity().setTransformationMatrix(getTransformation(-0.7));
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(GO_DOWN_TIME_TICKS);
        }

        public void goUp(@NotNull Hammer hammer) {
            getEntity().setTransformationMatrix(getTransformation(0.7));
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(hammer.cooldownTicks - GO_DOWN_TIME_TICKS);
        }

        private static @NotNull Matrix4f getTransformation(double yTranslation) {
            return new TransformBuilder()
                    .translate(0, yTranslation, 0)
                    .scale(0.3, 2.0, 0.3)
                    .buildForItemDisplay();

        }
    }

    public static class HammerTipEntity extends PylonEntity<ItemDisplay> {

        public static final NamespacedKey KEY = pylonKey("hammer_tip");

        @SuppressWarnings("unused")
        public HammerTipEntity(@NotNull ItemDisplay entity) {
            super(entity);
        }

        public HammerTipEntity(@NotNull Block block) {
            super(
                    KEY,
                    new ItemDisplayBuilder()
                            .material(Material.AIR)
                            .transformation(getTransformation(-0.3))
                            .build(block.getLocation().toCenterLocation().add(0, -1, 0))
            );
        }

        public void goDown() {
            getEntity().setTransformationMatrix(getTransformation(-1.7));
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(GO_DOWN_TIME_TICKS);
        }

        public void goUp(@NotNull Hammer hammer) {
            getEntity().setTransformationMatrix(getTransformation(-0.3));
            getEntity().setInterpolationDelay(0);
            getEntity().setInterpolationDuration(hammer.cooldownTicks - GO_DOWN_TIME_TICKS);
        }

        public void setHammer(@Nullable Hammer hammer) {
            if (hammer == null) {
                getEntity().setItemStack(new ItemStack(Material.AIR));
            } else {
                getEntity().setItemStack(new ItemStack(hammer.baseBlock));
            }
        }

        private static @NotNull Matrix4f getTransformation(double yTranslation) {
            return new TransformBuilder()
                    .translate(0, yTranslation, 0)
                    .scale(0.6, 0.1, 0.6)
                    .buildForItemDisplay();
        }
    }
}
