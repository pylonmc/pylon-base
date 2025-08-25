package io.github.pylonmc.pylon.base.content.magic;

import io.github.pylonmc.pylon.base.BaseConfig;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.magic.base.Rune;
import io.github.pylonmc.pylon.base.recipes.FireproofRuneRecipe;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
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

import java.util.Objects;

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

        ItemStack mappedResult = FireproofRuneRecipe.RECIPE_TYPE.getRecipes().stream().map(recipe -> {
            if (recipe.input().isSimilar(target)) {
                return recipe.result();
            }

            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);

        ItemStack handle;
        if (mappedResult != null) {
            handle = mappedResult.asQuantity(consume);
        } else {
            handle = target.asQuantity(consume);
        }

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
        BaseUtils.spawnParticle(Particle.EXPLOSION, explodeLoc, 1);
        BaseUtils.spawnParticle(Particle.FLAME, explodeLoc, 20);
        BaseUtils.spawnParticle(Particle.SMOKE, explodeLoc, 1);
        world.playSound(explodeLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);

        target.setAmount(0);
        rune.setAmount(0);
        player.sendMessage(SUCCESS);
    }
}
