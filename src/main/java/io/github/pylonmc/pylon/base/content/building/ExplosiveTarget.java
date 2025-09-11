package io.github.pylonmc.pylon.base.content.building;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTargetBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.papermc.paper.event.block.TargetHitEvent;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


public class ExplosiveTarget extends PylonBlock implements PylonTargetBlock {

    public static class Item extends PylonItem {
        private final Double explosivePower = getSettings().getOrThrow("explosive-power", ConfigAdapter.DOUBLE);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull List<PylonArgument> getPlaceholders() {
            return List.of(PylonArgument.of("explosive-power", explosivePower));
        }
    }

    public final double explosivePower = getSettings().getOrThrow("explosive-power", ConfigAdapter.DOUBLE);
    public final boolean createsFire = getSettings().getOrThrow("creates-fire", ConfigAdapter.BOOLEAN);

    @SuppressWarnings("unused")
    public ExplosiveTarget(Block block, BlockCreateContext context) {
        super(block);
    }

    @SuppressWarnings("unused")
    public ExplosiveTarget(Block block, PersistentDataContainer pdc) {
        super(block);
    }

    @Override
    public void onHit(@NotNull TargetHitEvent event) {
        event.setCancelled(true);
        if (!Objects.requireNonNull(event.getHitBlock()).getWorld().createExplosion(event.getHitBlock().getLocation(),
                (float) explosivePower,
                createsFire)) {
            return;
        }
        BlockStorage.breakBlock(getBlock());
    }
}
