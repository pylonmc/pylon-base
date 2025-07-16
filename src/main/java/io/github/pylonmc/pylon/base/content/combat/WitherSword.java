package io.github.pylonmc.pylon.base.content.combat;

import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.util.gui.unit.UnitFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class WitherSword extends PylonItem implements PylonInteractor {
    private final boolean chargedSkulls = getSettings().getOrThrow("charged-skulls", Boolean.class);
    private final double skullSpeed = getSettings().getOrThrow("skull-speed", Double.class);
    private static final TranslatableComponent trueCharged = Component.translatable("pylon.pylonbase.item.wither_sword.charged.true");
    private static final TranslatableComponent falseCharged = Component.translatable("pylon.pylonbase.item.wither_sword.charged.false");

    public WitherSword(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onUsedToRightClick(@NotNull PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Location playerPos = event.getPlayer().getLocation();
        WitherSkull witherSkull = (WitherSkull)playerPos.getWorld().spawnEntity(playerPos, EntityType.WITHER_SKULL);
        witherSkull.setCharged(chargedSkulls);
        witherSkull.setVelocity(event.getPlayer().getEyeLocation().getDirection().multiply(skullSpeed));
    }

    @Override
    public @NotNull Map<@NotNull String, @NotNull ComponentLike> getPlaceholders() {
        return Map.of("chargedskulls", chargedSkulls ? trueCharged : falseCharged,
                "skullspeed", UnitFormat.BLOCKS_PER_SECOND.format(skullSpeed));
    }
}
