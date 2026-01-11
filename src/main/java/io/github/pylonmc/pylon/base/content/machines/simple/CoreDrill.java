package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.PylonBase;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonDirectionalBlock;
import io.github.pylonmc.pylon.core.block.base.PylonProcessor;
import io.github.pylonmc.pylon.core.block.base.PylonSimpleMultiblock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.entity.display.ItemDisplayBuilder;
import io.github.pylonmc.pylon.core.entity.display.transform.TransformBuilder;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import io.github.pylonmc.pylon.core.waila.WailaDisplay;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.time.Duration;
import java.util.List;

public abstract class CoreDrill extends PylonBlock
        implements PylonSimpleMultiblock, PylonDirectionalBlock, PylonProcessor {

    public static class Item extends PylonItem {

        private final int rotationDuration = getSettings().getOrThrow("rotation-duration-ticks", ConfigAdapter.INT);
        private final int rotationsPerCycle = getSettings().getOrThrow("rotations-per-cycle", ConfigAdapter.INT);
        private final ItemStack output = getSettings().getOrThrow("output", ConfigAdapter.ITEM_STACK);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("cycle-time", UnitFormat.SECONDS.format(rotationsPerCycle * rotationDuration / 20)),
                    PylonArgument.of("cycle-output", output.effectiveName())
            );
        }
    }

    @Getter protected final int rotationDuration = getSettings().getOrThrow("rotation-duration-ticks", ConfigAdapter.INT);
    @Getter protected final int rotationsPerCycle = getSettings().getOrThrow("rotations-per-cycle", ConfigAdapter.INT);
    protected final ItemStack output = getSettings().getOrThrow("output", ConfigAdapter.ITEM_STACK);
    protected final Material drillMaterial = getSettings().getOrThrow("drill-material", ConfigAdapter.MATERIAL);
    protected final ItemStackBuilder drillStack = ItemStackBuilder.of(drillMaterial)
            .addCustomModelDataString(getKey() + ":drill");

    @SuppressWarnings("unused")
    protected CoreDrill(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);
        setFacing(context.getFacing());
        setMultiblockDirection(getFacing());
        addEntity("drill", new ItemDisplayBuilder()
                .itemStack(drillStack)
                .transformation(new TransformBuilder()
                        .scale(0.3, 2.1, 0.3)
                )
                .build(getBlock().getLocation().toCenterLocation().subtract(0, 1.5, 0))
        );
    }

    @SuppressWarnings("unused")
    protected CoreDrill(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    protected void postLoad() {
        if (isProcessing()) {
            finishProcess();
        }
    }

    public @Nullable ItemDisplay getDrillDisplay() {
        return getHeldEntity(ItemDisplay.class, "drill");
    }

    public static @NotNull Matrix4f getDrillDisplayMatrix(double rotation) {
        return new TransformBuilder()
                .scale(0.3, 2.1, 0.3)
                .rotate(0, rotation, 0)
                .buildForItemDisplay();
    }

    public void cycle() {
        if (isProcessing() || !isFormedAndFullyLoaded()) {
            return;
        }

        // Schedule animations - this is easier to do here rather than trying to do it in tick()
        for (int i = 0; i < rotationsPerCycle; i++) {
            for (int j = 0; j < 4; j++) {
                double rotation = (j / 4.0) * 2.0 * Math.PI;
                Bukkit.getScheduler().runTaskLater(PylonBase.getInstance(), () -> {
                    BaseUtils.animate(getDrillDisplay(), rotationDuration / 4, getDrillDisplayMatrix(rotation));
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
                    progressProcess(rotationDuration / 4);
                }, (long) ((i + j/4.0) * rotationDuration));
            }
        }

        startProcess(getCycleDuration());
    }

    @Override
    public void onProcessFinished() {
        getBlock().getWorld().dropItemNaturally(
                getBlock().getRelative(BlockFace.DOWN, 2).getLocation().toCenterLocation(),
                output,
                (item) -> item.setVelocity(getFacing().getDirection().multiply(0.3))
        );
    }

    public int getCycleDuration() {
        return rotationsPerCycle * rotationDuration;
    }

    @Override
    public @Nullable WailaDisplay getWaila(@NotNull Player player) {
        String wailaFormat = "pylon." + getKey().getNamespace() + ".item." + getKey().getKey() + ".waila_format";
        Integer timeLeft = getProcessTicksRemaining();
        return new WailaDisplay(getDefaultWailaTranslationKey().arguments(
            PylonArgument.of("duration_if_any",
                timeLeft == null
                    ? Component.empty()
                    : Component.translatable(wailaFormat).arguments(
                        PylonArgument.of("duration", UnitFormat.formatDuration(Duration.ofSeconds(timeLeft / 20), true, true))
                    )
            )
        ));
    }
}
