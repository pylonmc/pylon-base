package io.github.pylonmc.pylon.base.content.combat;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.config.Settings;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonArrow;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;


public class RecoilArrow extends PylonItem implements PylonArrow {

    public static final double EFFICIENCY = Settings.get(BaseKeys.RECOIL_ARROW).getOrThrow("efficiency", ConfigAdapter.DOUBLE);

    public RecoilArrow(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onArrowShotFromBow(@NotNull EntityShootBowEvent event) {
        event.getEntity().setVelocity(event.getEntity().getVelocity().add(event.getProjectile().getVelocity().multiply(-EFFICIENCY)));
    }
}