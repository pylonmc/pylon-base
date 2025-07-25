package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.entities.SimpleItemDisplay;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformUtil;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import lombok.Getter;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import java.util.Map;

public abstract class CoreDrill extends PylonBlock implements PylonSimpleMultiblock {

    public static class Item extends PylonItem {

        private final int rotationDuration = getSettings().getOrThrow("rotation-duration-ticks", Integer.class);
        private final int rotationsPerCycle = getSettings().getOrThrow("rotations-per-cycle", Integer.class);
        private final ItemStack output = getSettings().getItemOrThrow("output");

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<String, ComponentLike> getPlaceholders() {
            return Map.of(
                    "cycle-time", UnitFormat.SECONDS.format(rotationsPerCycle * rotationDuration / 20),
                    "cycle-output", output.effectiveName()
            );
        }
    }

    @Getter protected final int rotationDuration = getSettings().getOrThrow("rotation-duration-ticks", Integer.class);
    @Getter protected final int rotationsPerCycle = getSettings().getOrThrow("rotations-per-cycle", Integer.class);
    protected final ItemStack output = getSettings().getItemOrThrow("output");
    protected final Material drillMaterial = getSettings().getMaterialOrThrow("drill-material");
    @Getter protected boolean cycling;

    @SuppressWarnings("unused")
    public CoreDrill(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        if (context instanceof BlockCreateContext.PlayerPlace playerPlace) {
            setFacing(TransformUtil.yawToFace(playerPlace.getPlayer().getYaw()));
        } else {
            setFacing(BlockFace.NORTH);
        }
        addEntity("drill", new SimpleItemDisplay(new ItemDisplayBuilder()
                .material(drillMaterial)
                .transformation(new TransformBuilder()
                        .scale(0.3, 2.1, 0.3))
                .build(getBlock().getLocation().toCenterLocation().subtract(0, 1.5, 0))
        ));
        cycling = false;
    }

    @SuppressWarnings("unused")
    public CoreDrill(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
        cycling = false;
    }

    public SimpleItemDisplay getDrillDisplay() {
        return getHeldEntityOrThrow(SimpleItemDisplay.class, "drill");
    }

    public static @NotNull Matrix4f getDrillDisplayMatrix(double rotation) {
        return new TransformBuilder()
                .scale(0.3, 2.1, 0.3)
                .rotate(0, rotation, 0)
                .buildForItemDisplay();
    }

    public void cycle() {
        if (cycling || !isFormedAndFullyLoaded()) {
            return;
        }

        cycling = true;

        for (int i = 0; i < rotationsPerCycle; i++) {
            for (int j = 0; j < 4; j++) {
                double rotation = (j / 4.0) * 2.0 * Math.PI;
                Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
                    getDrillDisplay().setTransform(
                            rotationDuration / 4, getDrillDisplayMatrix(rotation)
                    );
                    new ParticleBuilder(Particle.BLOCK)
                            .count(5)
                            .data(getBlock().getRelative(BlockFace.DOWN, 3).getBlockData())
                            .location(getBlock()
                                    .getRelative(BlockFace.DOWN, 2)
                                    .getLocation()
                                    .toCenterLocation()
                                    .subtract(0, 0.3, 0)
                            )
                            .spawn();
                }, (long) ((i + j/4.0) * rotationDuration));
            }
        }

        Bukkit.getScheduler().runTaskLater(
                PylonBase.getInstance(),
                this::finishCycle,
                getCycleDuration()
        );
    }

    protected void finishCycle() {
        cycling = false;
        getBlock().getWorld().dropItemNaturally(
                getBlock().getRelative(BlockFace.DOWN, 2).getLocation().toCenterLocation(),
                output
        );
    }

    public int getCycleDuration() {
        return rotationsPerCycle * rotationDuration;
    }
}
