package io.github.pylonmc.pylon.base.items.weapons;

import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.Arrow;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static io.github.pylonmc.pylon.base.util.KeyUtils.pylonKey;


public class RecoilArrow extends PylonItem implements Arrow {

    public static final NamespacedKey KEY = pylonKey("recoil_arrow");

    public static final double EFFICIENCY = getSettings(KEY).getOrThrow("efficiency", Double.class);

    public static final ItemStack STACK = ItemStackBuilder.pylonItem(Material.ARROW, KEY)
            .build();

    public RecoilArrow(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onArrowShotFromBow(@NotNull EntityShootBowEvent event) {
        event.getEntity().setVelocity(event.getEntity().getVelocity().add(event.getProjectile().getVelocity().multiply(-EFFICIENCY)));
    }

    @Override
    public void onArrowReady(@NotNull PlayerReadyArrowEvent event) {
        // Intentionally blank
    }
}