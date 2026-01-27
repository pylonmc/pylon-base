package io.github.pylonmc.pylon.base.content.combat;

import io.github.pylonmc.rebar.config.adapter.ConfigAdapter;
import io.github.pylonmc.rebar.item.PylonItem;
import io.github.pylonmc.rebar.item.base.PylonArrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class RecoilArrow extends PylonItem implements PylonArrow {

    public final double efficiency = getSettings().getOrThrow("efficiency", ConfigAdapter.DOUBLE);

    public RecoilArrow(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onArrowShotFromBow(@NotNull EntityShootBowEvent event) {
        event.getEntity().setVelocity(event.getEntity().getVelocity().add(event.getProjectile().getVelocity().multiply(-efficiency)));
    }
}