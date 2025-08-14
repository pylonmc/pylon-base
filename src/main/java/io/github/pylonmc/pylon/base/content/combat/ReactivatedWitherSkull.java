package io.github.pylonmc.pylon.base.content.combat;

import io.github.pylonmc.pylon.core.i18n.PylonArgument;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReactivatedWitherSkull extends PylonItem implements PylonInteractor {
    private final boolean chargedSkulls = getSettings().getOrThrow("charged-skulls", Boolean.class);
    private final double skullSpeed = getSettings().getOrThrow("skull-speed", Double.class);
    private final double playerHeight = getSettings().getOrThrow("player-height", Double.class);
    private static final TranslatableComponent trueCharged = Component.translatable("pylon.pylonbase.item.reactivated_wither_skull.charged.true");
    private static final TranslatableComponent falseCharged = Component.translatable("pylon.pylonbase.item.reactivated_wither_skull.charged.false");

    public ReactivatedWitherSkull(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        getStack().damage(1, event.getPlayer());
        Location skullPos = event.getPlayer().getLocation().clone().add(0, playerHeight, 0);
        WitherSkull witherSkull = (WitherSkull) skullPos.getWorld().spawnEntity(skullPos, EntityType.WITHER_SKULL);
        witherSkull.setCharged(chargedSkulls);
        witherSkull.setVelocity(event.getPlayer().getEyeLocation().getDirection().multiply(skullSpeed));
    }

    @Override
    public @NotNull List<@NotNull PylonArgument> getPlaceholders() {
        return List.of(
                PylonArgument.of("chargedskulls", chargedSkulls ? trueCharged : falseCharged),
                PylonArgument.of("skullspeed", UnitFormat.BLOCKS_PER_SECOND.format(skullSpeed))
        );
    }
}
