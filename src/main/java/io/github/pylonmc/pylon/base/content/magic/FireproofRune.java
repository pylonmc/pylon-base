package io.github.pylonmc.pylon.base.content.magic;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.content.magic.base.Rune;
import io.github.pylonmc.pylon.core.item.PylonItem;
import net.kyori.adventure.text.Component;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.tag.DamageTypeTags;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author balugaq
 */
public class FireproofRune extends Rune {
    public static final Component SUCCESS = Component.translatable("pylon.pylonbase.message.fireproof_result.success");
    public static final Component SUCCESS_TRICKY = Component.translatable("pylon.pylonbase.message.fireproof_result.success_tricky");

    public static final Map<Material, Material> TRICKY_FIREPROOF_MAP = Map.of(
            Material.DIAMOND_AXE, Material.NETHERITE_AXE,
            Material.DIAMOND_HOE, Material.NETHERITE_HOE,
            Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE,
            Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL,
            Material.DIAMOND_SWORD, Material.NETHERITE_SWORD,
            Material.DIAMOND_HELMET, Material.NETHERITE_HELMET,
            Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE,
            Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS,
            Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS
    );

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
        handle.editMeta(meta -> meta.setDamageResistant(DamageTypeTags.IS_FIRE));
        boolean tricked = trickyAdvance(target, handle);

        // (N)Either left runes or targets
        int leftRunes = rune.getAmount() - consume;
        int leftTargets = target.getAmount() - consume;

        Player player = event.getPlayer();
        Location explodeLoc = player.getTargetBlockExact((int) Math.ceil(CHECK_RANGE), FluidCollisionMode.NEVER).getLocation();
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
        player.sendMessage(tricked ? SUCCESS_TRICKY : SUCCESS);
    }

    public boolean trickyAdvance(@NotNull ItemStack target, @NotNull ItemStack handle) {
        PylonItem instance = PylonItem.fromStack(target);
        if (instance != null && !(instance instanceof Trickable)) {
            // only handle vanilla items and trickable items
            return false;
        }

        for (Map.Entry<Material, Material> entry : TRICKY_FIREPROOF_MAP.entrySet()) {
            Material from = entry.getKey();
            Material to = entry.getValue();
            if (target.getType() == from) {
                // Interesting trick >_<
                handle.setType(to);
                return true;
            }
        }

        return false;
    }

    public void spawnParticle(Particle particle, Location location, int count) {
        new ParticleBuilder(particle)
                .location(location)
                .offset(0, 0, 0)
                .count(count)
                .spawn();
    }

    /**
     * A marker interface for items that can be tricked by the fireproof rune.
     *
     * @author balugaq
     */
    public interface Trickable {
    }
}
