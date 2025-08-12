package io.github.pylonmc.pylon.base.content.magic;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.BaseConfig;
import io.github.pylonmc.pylon.base.content.magic.base.Rune;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import net.kyori.adventure.text.Component;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author balugaq
 */
@SuppressWarnings("UnstableApiUsage")
public class FireproofRune extends Rune {
    public static final Component SUCCESS = Component.translatable("pylon.pylonbase.message.fireproof_result.success");

    public FireproofRune(@NotNull ItemStack stack) {
        super(stack);
    }

    /**
     * Handles contacting between an item and a rune.
     *
     * @param event  The event
     * @param rune   The rune item, amount may be > 1
     * @param target The item to handle, amount may be > 1
     */
    @Override
    public void onContactItem(@NotNull PlayerDropItemEvent event, @NotNull ItemStack rune, @NotNull ItemStack target) {
        // As many runes as possible to consume
        int consume = Math.min(rune.getAmount(), target.getAmount());

        ItemStack handle = target.asQuantity(consume);
        handle.setData(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE));

        // (N)Either left runes or targets
        int leftRunes = rune.getAmount() - consume;
        int leftTargets = target.getAmount() - consume;

        Player player = event.getPlayer();
        Location explodeLoc = player.getTargetBlockExact((int) Math.ceil(BaseConfig.RUNE_CHECK_RANGE), FluidCollisionMode.NEVER).getLocation();
        World world = explodeLoc.getWorld();
        if (leftRunes > 0) {
            world.dropItemNaturally(explodeLoc, rune.asQuantity(leftRunes));
        }
        if (leftTargets > 0) {
            world.dropItemNaturally(explodeLoc, target.asQuantity(leftTargets));
        }
        world.dropItemNaturally(explodeLoc, handle);

        // simple particles
        spawnParticle(Particle.EXPLOSION, explodeLoc, 1);
        spawnParticle(Particle.FLAME, explodeLoc, 20);
        spawnParticle(Particle.SMOKE, explodeLoc, 1);
        world.playSound(explodeLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

        target.setAmount(0);
        rune.setAmount(0);
        player.sendMessage(SUCCESS);
    }

    public void spawnParticle(@NotNull Particle particle, @NotNull Location location, int count) {
        new ParticleBuilder(particle)
                .location(location)
                .offset(0, 0, 0)
                .count(count)
                .spawn();
    }
}
