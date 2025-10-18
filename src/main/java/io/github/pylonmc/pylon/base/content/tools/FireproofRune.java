package io.github.pylonmc.pylon.base.content.tools;

import com.destroystokyo.paper.ParticleBuilder;
import io.github.pylonmc.pylon.base.content.tools.base.Rune;
import io.github.pylonmc.pylon.core.config.adapter.ConfigAdapter;
import io.github.pylonmc.pylon.base.recipes.FireproofRuneRecipe;
import io.github.pylonmc.pylon.core.item.builder.ItemStackBuilder;
import io.github.pylonmc.pylon.core.util.RandomizedSound;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * @author balugaq
 */
@SuppressWarnings("UnstableApiUsage")
public class FireproofRune extends Rune {
    public static final Component SUCCESS = Component.translatable("pylon.pylonbase.message.fireproof_result.success");
    public static final Component TOOLTIP = Component.translatable("pylon.pylonbase.message.fireproof_result.tooltip");

    private final RandomizedSound applySound = getSettings().getOrThrow("apply-sound", ConfigAdapter.RANDOMIZED_SOUND);

    public FireproofRune(@NotNull ItemStack stack) {
        super(stack);
    }

    /**
     * Fixes #156 - Fireproof rune can be applied multiple times
     * <p>
     * Checks if the rune is applicable to the target item.
     *
     * @param event  The event
     * @param rune   The rune item, amount may be > 1
     * @param target The item to handle, amount may be > 1
     * @return true if applicable, false otherwise
     */
    public boolean isApplicableToTarget(@NotNull PlayerDropItemEvent event, @NotNull ItemStack rune, @NotNull ItemStack target) {
        DamageResistant data = target.getData(DataComponentTypes.DAMAGE_RESISTANT);
        if (data == null) return true;
        return !data.types().equals(DamageTypeTagKeys.IS_FIRE);
    }

    /**
     * Handles contacting between an item and a rune.
     * When a Fireproof Rune contacts another item, it makes that item fireproof.
     *
     * @param event  The player drop item event
     * @param rune   The rune item, amount may be > 1
     * @param target The item to handle, amount may be > 1
     */
    @Override
    public void onContactItem(@NotNull PlayerDropItemEvent event, @NotNull ItemStack rune, @NotNull ItemStack target) {
        // As many runes as possible to consume
        int consume = Math.min(rune.getAmount(), target.getAmount());

        Player player = event.getPlayer();
        ItemStack mappedResult = FireproofRuneRecipe.RECIPE_TYPE.getRecipes().stream().map(recipe -> {
            if (recipe.input().isSimilar(target)) {
                return recipe.result();
            }

            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);

        ItemStack handle;
        if (mappedResult != null) {
            handle = mappedResult.asQuantity(consume); // Just clone it, don't modify the item
        } else {
            handle = ItemStackBuilder.of(target.asQuantity(consume)) // Already cloned in `asQuantity`
                    .set(DataComponentTypes.DAMAGE_RESISTANT, DamageResistant.damageResistant(DamageTypeTagKeys.IS_FIRE))
                    .lore(GlobalTranslator.render(TOOLTIP, player.locale()))
                    .build();
        }

        // (N)Either left runes or targets
        int leftRunes = rune.getAmount() - consume;
        int leftTargets = target.getAmount() - consume;

        Location explodeLoc = event.getItemDrop().getLocation();
        World world = explodeLoc.getWorld();
        if (leftRunes > 0) {
            world.dropItemNaturally(explodeLoc, rune.asQuantity(leftRunes)).setGlowing(true);
        }
        if (leftTargets > 0) {
            world.dropItemNaturally(explodeLoc, target.asQuantity(leftTargets)).setGlowing(true);
        }
        world.dropItemNaturally(explodeLoc, handle).setGlowing(true);

        // simple particles
        new ParticleBuilder(Particle.EXPLOSION).count(1).location(explodeLoc).spawn();
        new ParticleBuilder(Particle.FLAME).count(50).location(explodeLoc).spawn();
        new ParticleBuilder(Particle.SMOKE).count(40).location(explodeLoc).spawn();
        world.playSound(applySound.create(), explodeLoc.x(), explodeLoc.y(), explodeLoc.z());

        target.setAmount(0);
        rune.setAmount(0);
        player.sendMessage(SUCCESS);
    }
}
