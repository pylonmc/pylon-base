package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonConsumable;
import io.github.pylonmc.pylon.core.item.base.PylonInteractor;
import io.github.pylonmc.pylon.core.particles.ConfettiParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ConfettiPopper extends PylonItem implements PylonConsumable {
    public static final double LENGTH = getSettings(BaseKeys.CONFETTI_POPPER).getOrThrow("length", ConfigAdapter.DOUBLE);
    public static final double SIZE = getSettings(BaseKeys.CONFETTI_POPPER).getOrThrow("size", ConfigAdapter.DOUBLE);
    public static final int AMOUNT = getSettings(BaseKeys.CONFETTI_POPPER).getOrThrow("amount", ConfigAdapter.INT);

    private static final Random RANDOM = new Random();

    public ConfettiPopper(@NotNull ItemStack stack) {
        super(stack);
    }

    @Override
    public void onConsumed(@NotNull PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Location eyeLocation = player.getEyeLocation();
        Vector direction = eyeLocation.getDirection().normalize();

        player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1.6f);
        player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 1f, 1.5f);
        for (int i = 0; i < AMOUNT; i++) {
            double distance = RANDOM.nextDouble(1, LENGTH);

            double spreadX = (RANDOM.nextDouble() - 0.5) * 2 * SIZE;
            double spreadY = (RANDOM.nextDouble() - 0.5) * 2 * SIZE;
            double spreadZ = (RANDOM.nextDouble() - 0.5) * 2 * SIZE;

            Vector offset = direction.clone().multiply(distance).add(new Vector(spreadX, spreadY, spreadZ));
            Vector spawnPos = eyeLocation.toVector().add(offset);

            int index = RANDOM.nextInt(ConfettiParticle.CONCRETES.size());
            Material mat = ConfettiParticle.CONCRETES.get(index);

            new ConfettiParticle(spawnPos.toLocation(player.getWorld()), mat, direction);
        }
    }
}
