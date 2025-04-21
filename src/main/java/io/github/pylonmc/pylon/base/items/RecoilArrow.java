package io.github.pylonmc.pylon.base.items;

import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.PylonItemSchema;
import io.github.pylonmc.pylon.core.item.base.Arrow;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class RecoilArrow extends PylonItemSchema {

    public final double efficiency;

    public RecoilArrow(
            NamespacedKey key,
            Function<NamespacedKey, ItemStack> templateSupplier,
            double efficiency
    ) {
        super(key, RecoilArrowItem.class, templateSupplier);
        this.efficiency = efficiency;
    }

    public static class RecoilArrowItem extends PylonItem<RecoilArrow> implements Arrow {

        private final RecoilArrow schema;
        public RecoilArrowItem(RecoilArrow schema, ItemStack stack) {
            super(schema, stack);
            this.schema = schema;
        }

        @Override
        public void onArrowShotFromBow(@NotNull EntityShootBowEvent event) {
            event.getEntity().setVelocity(event.getEntity().getVelocity().add(event.getProjectile().getVelocity().multiply(-1 * schema.efficiency)));
        }

        @Override
        public void onArrowReady(@NotNull PlayerReadyArrowEvent event) {
            // Intentionally blank
        }
    }
}