package io.github.pylonmc.pylon.base.content.magic;

import io.github.pylonmc.pylon.base.BaseConfig;
import io.github.pylonmc.pylon.base.BaseItems;
import io.github.pylonmc.pylon.base.BaseKeys;
import io.github.pylonmc.pylon.base.content.magic.base.Rune;
import io.github.pylonmc.pylon.base.util.BaseUtils;
import io.github.pylonmc.pylon.core.guide.button.ItemButton;
import io.github.pylonmc.pylon.core.recipe.FluidOrItem;
import io.github.pylonmc.pylon.core.recipe.PylonRecipe;
import io.github.pylonmc.pylon.core.recipe.RecipeType;
import io.github.pylonmc.pylon.core.util.gui.GuiItems;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DamageResistant;
import io.papermc.paper.registry.keys.tags.DamageTypeTagKeys;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.invui.gui.Gui;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author balugaq
 */
@SuppressWarnings("UnstableApiUsage")
public class FireproofRune extends Rune {
    public static final FireproofRuneRecipeType RECIPE_TYPE = new FireproofRuneRecipeType();

    public static class FireproofRuneRecipeType extends RecipeType<FireproofRuneRecipe> {

        private FireproofRuneRecipeType() {
            super(BaseKeys.FIREPROOF_RUNE);
        }
    }

    @Getter
    public static class FireproofRuneRecipe implements PylonRecipe {
        private final NamespacedKey key;
        private final ItemStack input;
        private final ItemStack result;
        private FireproofRuneRecipe(
                @NotNull NamespacedKey key,
                @NotNull ItemStack input,
                @NotNull ItemStack result
        ) {
            this.key = key;
            this.input = input;
            this.result = result;
        }

        public static FireproofRuneRecipe of(
                @NotNull NamespacedKey key,
                @NotNull ItemStack input,
                @NotNull ItemStack result
        ) {
            if (!input.getType().isItem()) {
                throw new IllegalArgumentException("Input must be an item");
            }
            if (!result.getType().isItem()) {
                throw new IllegalArgumentException("Result must be an item");
            }
            return new FireproofRuneRecipe(key, input, result);
        }


        @Override
        public @NotNull Gui display() {
            return Gui.normal()
                    .setStructure(
                            "# # # # # # # # #",
                            "# # # # b # # # #",
                            "# # # # # # # # #",
                            "# i # # # # # r #",
                            "# # # # # # # # #",
                            "# # # # # # # # #"
                    )
                    .addIngredient('#', GuiItems.backgroundBlack())
                    .addIngredient('b', ItemButton.fromStack(BaseItems.FIREPROOF_RUNE))
                    .addIngredient('i', ItemButton.fromStack(getInput()))
                    .addIngredient('r', ItemButton.fromStack(getResult()))
                    .build();
        }

        /**
         * Return the namespaced identifier for this object.
         *
         * @return this object's key
         */
        @Override
        public @NotNull NamespacedKey getKey() {
            return key;
        }

        @Override
        public @NotNull List<FluidOrItem> getInputs() {
            return List.of(FluidOrItem.of(input));
        }

        @Override
        public @NotNull List<FluidOrItem> getResults() {
            return List.of(FluidOrItem.of(result));
        }
    }

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

        ItemStack mappedResult = RECIPE_TYPE.getRecipes().stream().map(recipe -> {
            if (recipe.input.isSimilar(target)) {
                return recipe.result;
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
