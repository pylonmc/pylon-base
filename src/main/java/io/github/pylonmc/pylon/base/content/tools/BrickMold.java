package io.github.pylonmc.pylon.base.content.tools;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.context.BlockBreakContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonBlockInteractor;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import org.bukkit.Particle;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class BrickMold extends PylonItem implements PylonBlockInteractor {

    public final int cooldownTicks = getSettings().getOrThrow("cooldown-ticks", ConfigAdapter.INT);

    @SuppressWarnings("unused")
    public BrickMold(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToClickBlock(@NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() == null || !event.getAction().isRightClick()) {
            return;
        }

        if (!(BlockStorage.get(event.getClickedBlock()) instanceof Moldable moldable)) {
            return;
        }

        moldable.doMoldingClick();
        event.getPlayer().setCooldown(getStack(), cooldownTicks);
        new ParticleBuilder(Particle.BLOCK)
                .count(20)
                .offset(0.2, 0.2, 0.2)
                .location(event.getClickedBlock().getLocation().toCenterLocation())
                .data(event.getClickedBlock().getBlockData())
                .spawn();

        if (moldable.isMoldingFinished()) {
            BlockStorage.breakBlock(event.getClickedBlock(), new BlockBreakContext.PluginBreak(event.getClickedBlock(), false));
            event.getClickedBlock().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), moldable.moldingResult());
        }
    }

    @Override
    public boolean respectCooldown() {
        return true;
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("cooldown", UnitFormat.SECONDS.format(cooldownTicks / 20.0))
        );
    }
}
