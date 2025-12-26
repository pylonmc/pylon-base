package io.github.pylonmc.pylon.base.content.machines.simple;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonBreakHandler;
import io.github.pylonmc.pylon.core.block.base.PylonTickingBlock;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class VacuumHopper extends PylonBlock implements PylonTickingBlock, PylonBreakHandler {

    public static class Item extends PylonItem {
        public final int radius = getSettings().getOrThrow("radius-blocks", ConfigAdapter.INT);
        public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(
                    PylonArgument.of("radius", UnitFormat.BLOCKS.format(radius)),
                    PylonArgument.of("tick_interval", UnitFormat.SECONDS.format(tickInterval / 20.0))
            );
        }
    }

    public final int radius = getSettings().getOrThrow("radius-blocks", ConfigAdapter.INT);
    public final int tickInterval = getSettings().getOrThrow("tick-interval", ConfigAdapter.INT);

    @SuppressWarnings("unused")
    public VacuumHopper(@NotNull Block block, @NotNull BlockCreateContext context) {
        super(block, context);

        setTickInterval(tickInterval);
    }

    @SuppressWarnings("unused")
    public VacuumHopper(@NotNull Block block, @NotNull PersistentDataContainer pdc) {
        super(block, pdc);
    }

    @Override
    public void onBreak(@NotNull List<@NotNull ItemStack> drops, @NotNull BlockBreakContext context) {
        Hopper hopper = (Hopper) getBlock().getState();

        for (ItemStack item : hopper.getInventory()) {
            if (item != null) {
                drops.add(item);
            }
        }
    }

    @Override
    public void tick() {
        Hopper hopper = (Hopper) getBlock().getState();
        if (!((org.bukkit.block.data.type.Hopper) getBlock().getBlockData()).isEnabled()) {
            return; // don't vacuum if powered
        }

        for (Entity entity : getBlock().getLocation().toCenterLocation().getNearbyEntities(radius + 0.5, radius + 0.5, radius + 0.5)) {
            if (!(entity instanceof org.bukkit.entity.Item item)) {
                continue;
            }

            ItemStack stack = item.getItemStack();
            HashMap<Integer, ItemStack> rest = hopper.getInventory().addItem(stack);
            if (rest.isEmpty()) {
                new ParticleBuilder(Particle.WITCH)
                        .location(item.getLocation())
                        .spawn();
                item.remove();
                break;
            }

            int fail = rest.values().stream().findFirst().get().getAmount();
            if (fail == stack.getAmount()) {
                continue;
            }

            stack.setAmount(fail);
            new ParticleBuilder(Particle.WITCH)
                    .location(item.getLocation())
                    .spawn();
        }
    }
}
