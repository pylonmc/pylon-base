package io.github.pylonmc.pylon.base.content.tools;

import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.core.item.PylonItem;
import io.github.pylonmc.pylon.core.item.base.PylonConsumable;
import io.github.pylonmc.pylon.core.particles.ConfettiParticle;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ConfettiPopper extends PylonItem implements PylonConsumable {
    private static final Random RANDOM = new Random();

    public final double length = getSettings().getOrThrow("length", ConfigAdapter.DOUBLE);
    public final double size = getSettings().getOrThrow("size", ConfigAdapter.DOUBLE);
    public final int amount = getSettings().getOrThrow("amount", ConfigAdapter.INT);
    public final int lifetime = getSettings().getOrThrow("lifetime-ticks", ConfigAdapter.INT);

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
        for (int i = 0; i < amount; i++) {
            double distance = RANDOM.nextDouble(1, length);

            double spreadX = (RANDOM.nextDouble() - 0.5) * 2 * size;
            double spreadY = (RANDOM.nextDouble() - 0.5) * 2 * size;
            double spreadZ = (RANDOM.nextDouble() - 0.5) * 2 * size;

            Vector offset = direction.clone().multiply(distance).add(new Vector(spreadX, spreadY, spreadZ));
            Vector spawnPos = eyeLocation.toVector().add(offset);

            int index = RANDOM.nextInt(ConfettiParticle.CONCRETES.size());
            Material mat = ConfettiParticle.CONCRETES.get(index);

            new ConfettiParticle(spawnPos.toLocation(player.getWorld()), direction, lifetime, mat);
        }
    }
}
