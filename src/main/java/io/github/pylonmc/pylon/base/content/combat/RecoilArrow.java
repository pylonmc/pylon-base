package io.github.pylonmc.pylon.base.content.combat;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.Arrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class RecoilArrow extends PylonItem implements Arrow {

    public final double efficiency = getSettings().getOrThrow("efficiency", Double.class);

    public RecoilArrow(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onArrowShotFromBow(@NotNull EntityShootBowEvent event) {
        event.getEntity().setVelocity(event.getEntity().getVelocity().add(event.getProjectile().getVelocity().multiply(-efficiency)));
    }
}