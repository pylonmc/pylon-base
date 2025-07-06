package io.github.pylonmc.pylon.base.content.building;

import io.github.pylonmc.pylon.core.block.BlockStorage;
import io.github.pylonmc.pylon.core.block.PylonBlock;
import io.github.pylonmc.pylon.core.block.base.PylonTargetBlock;
import io.github.pylonmc.pylon.core.block.context.BlockCreateContext;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.papermc.paper.event.block.TargetHitEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;


public class ExplosiveTarget extends PylonBlock implements PylonTargetBlock {

    public static class Item extends PylonItem {
        private final Double explosivePower = getSettings().getOrThrow("explosive-power", Double.class);

        public Item(@NotNull ItemStack stack) {
            super(stack);
        }

        @Override
        public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
            return Map.of("explosive-power", Component.text(explosivePower));
        }
    }

    public final double explosivePower = getSettings().getOrThrow("explosive-power", Double.class);
    public final boolean createsFire = getSettings().getOrThrow("creates-fire", Boolean.class);

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
